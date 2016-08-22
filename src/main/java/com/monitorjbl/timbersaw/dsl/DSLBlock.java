package com.monitorjbl.timbersaw.dsl;

import java.util.ArrayList;
import java.util.List;

public class DSLBlock {
  private final String name;
  private final List<DSLPlugin> plugins = new ArrayList<>();

  public DSLBlock(String name) {
    this.name = name;
  }

  public List<DSLPlugin> getPlugins() {
    return plugins;
  }
}
