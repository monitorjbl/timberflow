package com.monitorjbl.timberflow.domain;

import java.util.HashMap;
import java.util.Map;

public class LogLine {
  private Integer currentStep;
  private Map<String, String> fields = new HashMap<>();

  public LogLine() { }

  public LogLine(Integer currentStep, Map<String, String> fields) {
    this.currentStep = currentStep;
    this.fields = fields;
  }

  public Integer getCurrentStep() {
    return currentStep;
  }

  public Map<String, String> getFields() {
    return fields;
  }

  public String getField(String field) {
    return fields.get(field);
  }
}
