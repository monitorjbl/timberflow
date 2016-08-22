package com.monitorjbl.timbersaw.dsl;

public class DSL {
  private DSLBlock inputs;
  private DSLBlock filters;
  private DSLBlock outputs;

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
}
