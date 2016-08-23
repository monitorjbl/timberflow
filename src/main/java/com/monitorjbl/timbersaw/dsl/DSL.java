package com.monitorjbl.timbersaw.dsl;

import com.monitorjbl.timbersaw.domain.Step;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

public class DSL {
  private List<DSLPlugin> inputs = new ArrayList<>();
  private List<DSLBlockStatement> filters = new ArrayList<>();
  private List<DSLBlockStatement> outputs = new ArrayList<>();
  private List<Step> steps = new ArrayList<>();

  public List<DSLPlugin> getInputs() {
    return inputs;
  }

  void setInputs(List<DSLPlugin> inputs) {
    this.inputs = inputs;
  }

  List<DSLBlockStatement> getFilters() {
    return filters;
  }

  void setFilters(List<DSLBlockStatement> filters) {
    this.filters = filters;
  }

  List<DSLBlockStatement> getOutputs() {
    return outputs;
  }

  void setOutputs(List<DSLBlockStatement> outputs) {
    this.outputs = outputs;
  }

  public List<Step> getSteps() {
    return steps;
  }

  void setSteps(List<Step> steps) {
    this.steps = steps;
  }

  public List<DSLPlugin> inputPlugins() {
    return inputs;
  }

  public List<DSLPlugin> filterPlugins() {
    return filters.stream()
        .flatMap(s -> findPlugins(s).stream())
        .collect(toList());
  }

  public List<DSLPlugin> outputPlugins() {
    return outputs.stream()
        .flatMap(s -> findPlugins(s).stream())
        .collect(toList());
  }

  private List<DSLPlugin> findPlugins(DSLBlockStatement s) {
    if(s instanceof DSLPlugin) {
      return singletonList((DSLPlugin) s);
    } else if(s instanceof DSLBranch) {
      return ((DSLBranch) s).getPlugins();
    } else {
      throw new IllegalArgumentException("Cannot parse " + s.getClass());
    }
  }
}
