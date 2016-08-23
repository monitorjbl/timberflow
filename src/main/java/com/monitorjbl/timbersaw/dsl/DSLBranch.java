package com.monitorjbl.timbersaw.dsl;

import com.monitorjbl.timbersaw.domain.Comparison;

import java.util.ArrayList;
import java.util.List;

public class DSLBranch implements DSLBlockStatement {
  private Comparison comparison;
  private List<DSLPlugin> plugins = new ArrayList<>();

  public DSLBranch(Comparison comparison) {
    this.comparison = comparison;
  }

  public Comparison getComparison() {
    return comparison;
  }

  public List<DSLPlugin> getPlugins() {
    return plugins;
  }
}
