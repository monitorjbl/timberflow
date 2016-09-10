package com.monitorjbl.timberflow;

import com.monitorjbl.timberflow.api.LogLine;
import com.monitorjbl.timberflow.domain.ConditionalStep;
import com.monitorjbl.timberflow.domain.SingleStep;
import com.monitorjbl.timberflow.domain.Step;

import java.util.ArrayList;
import java.util.List;

public class RuntimeConfiguration {
  private static List<Step> steps = new ArrayList<>();

  public static void applyConfig(List<Step> steps) {
    RuntimeConfiguration.steps = steps;
  }

  public static String printout() {
    StringBuilder sb = new StringBuilder();
    int conditional = -1;
    for(int i = 0; i < steps.size(); i++) {
      Step step = steps.get(i);
      if(step instanceof SingleStep) {
        if(i < conditional) {
          sb.append(String.format("  %d:   %s\n", i, ((SingleStep) step).getCls().getSimpleName()));
        } else {
          sb.append(String.format("  %d: %s\n", i, ((SingleStep) step).getCls().getSimpleName()));
        }
      } else if(step instanceof ConditionalStep) {
        sb.append(String.format("  %d: if %s\n", i, ((ConditionalStep) step).getComparison()));
        conditional = i + ((ConditionalStep) step).getJump();
      }
    }
    return sb.toString();
  }

  public static SingleStep step(Integer index) {
    if(index >= steps.size()) {
      return null;
    } else {
      return (SingleStep) steps.get(index);
    }
  }

  public static SingleStep step(LogLine logLine) {
    return step(logLine.getCurrentStep());
  }

  public static SingleStep nextStep(LogLine logLine) {
    Integer pc = logLine.getCurrentStep() + 1;
    if(pc >= steps.size()) {
      return null;
    }

    Step step = steps.get(pc);
    if(step instanceof SingleStep) {
      return (SingleStep) step;
    } else if(step instanceof ConditionalStep) {
      ConditionalStep conditionalStep = ((ConditionalStep) step);
      if(conditionalStep.compare(logLine)) {
        return step(pc + 1);
      } else {
        return step(pc + conditionalStep.getJump());
      }
    } else {
      throw new IllegalStateException("No idea how to handle " + step.getClass());
    }
  }
}
