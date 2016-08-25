package com.monitorjbl.timberflow.filters.drop;

import com.monitorjbl.timberflow.api.Config;

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
