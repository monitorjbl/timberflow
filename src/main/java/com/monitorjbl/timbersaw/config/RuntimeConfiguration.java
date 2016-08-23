package com.monitorjbl.timbersaw.config;

import com.monitorjbl.timbersaw.domain.ConditionalStep;
import com.monitorjbl.timbersaw.domain.LogLine;
import com.monitorjbl.timbersaw.domain.SingleStep;
import com.monitorjbl.timbersaw.domain.Step;

import java.util.ArrayList;
import java.util.List;

public class RuntimeConfiguration {
  private static List<Step> steps = new ArrayList<>();

  public static void applyConfig(List<Step> steps) {
    RuntimeConfiguration.steps = steps;
  }

  public static <E> SingleStep<E> step(Integer index) {
    return (SingleStep<E>) steps.get(index);
  }

  public static <E> SingleStep<E> step(LogLine logLine) {
    return (SingleStep<E>) steps.get(logLine.getCurrentStep());
  }

  public static SingleStep nextStep(LogLine logLine) {
    Integer pc = logLine.getCurrentStep() + 1;
    Step step = steps.get(pc);
    if(step instanceof SingleStep) {
      return (SingleStep) step;
    } else if(step instanceof ConditionalStep) {
      ConditionalStep conditionalStep = ((ConditionalStep) step);
      if(conditionalStep.compare(logLine)) {
        return (SingleStep) steps.get(pc + 1);
      } else {
        return (SingleStep) steps.get(pc + conditionalStep.getJump());
      }
    } else {
      throw new IllegalStateException("No idea how to handle " + step.getClass());
    }
  }
}
