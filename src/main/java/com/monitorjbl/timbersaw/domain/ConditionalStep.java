package com.monitorjbl.timbersaw.domain;

import static com.monitorjbl.timbersaw.domain.Comparison.CompareOperation.EQUALS;
import static com.monitorjbl.timbersaw.domain.Comparison.CompareOperation.NOT_EQUALS;

public class ConditionalStep implements Step {
  private Integer number;
  private Comparison comparison;
  private Integer jump;

  public ConditionalStep() { }

  public ConditionalStep(Integer number, Comparison comparison, Integer jump) {
    this.number = number;
    this.comparison = comparison;
    this.jump = jump;
  }

  public Integer getNumber() {
    return number;
  }

  public Comparison getComparison() {
    return comparison;
  }

  public Integer getJump() {
    return jump;
  }

  public boolean compare(LogLine logLine) {
    if(comparison.getCompareOperation().equals(EQUALS)) {
      return comparison.getRightHand().equals(logLine.getField(comparison.getLeftHand()));
    } else if(comparison.getCompareOperation().equals(NOT_EQUALS)) {
      return !comparison.getRightHand().equals(logLine.getField(comparison.getLeftHand()));
    } else {
      throw new IllegalArgumentException("Cannot handle comparison " + comparison.getCompareOperation());
    }
  }
}
