package com.monitorjbl.timbersaw.config;

import com.monitorjbl.timbersaw.domain.SingleStep;

import java.util.ArrayList;
import java.util.List;

public class RuntimeConfiguration {
  private static List<SingleStep> steps = new ArrayList<>();

  public static void applyConfig(List<SingleStep> steps) {
    RuntimeConfiguration.steps = steps;
  }

  public static <E> SingleStep<E> step(Integer step) {
    return steps.get(step);
  }

}
