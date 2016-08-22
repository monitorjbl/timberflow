package com.monitorjbl.timbersaw.filters.drop;

import com.monitorjbl.timbersaw.config.Config;

import java.util.List;

public class DropConfig implements Config {
  private List<String> fields;

  public DropConfig(List<String> fields) {
    this.fields = fields;
  }

  public List<String> getFields() {
    return fields;
  }
}
