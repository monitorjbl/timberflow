package com.monitorjbl.timbersaw.dsl;

import com.monitorjbl.timbersaw.domain.SingleStep;

import java.util.ArrayList;
import java.util.List;

public class DSL {
  private DSLBlock inputs;
  private DSLBlock filters;
  private DSLBlock outputs;
  private List<SingleStep> steps = new ArrayList<>();

  public DSLBlock getInputs() {
    return inputs;
  }

  public void setInputs(DSLBlock inputs) {
    this.inputs = inputs;
  }

  public DSLBlock getFilters() {
    return filters;
  }

  public void setFilters(DSLBlock filters) {
    this.filters = filters;
  }

  public DSLBlock getOutputs() {
    return outputs;
  }

  public void setOutputs(DSLBlock outputs) {
    this.outputs = outputs;
  }

  public List<SingleStep> getSteps() {
    return steps;
  }

  public void setSteps(List<SingleStep> steps) {
    this.steps = steps;
  }
}
