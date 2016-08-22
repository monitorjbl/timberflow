package com.monitorjbl.timbersaw.dsl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DSLPlugin {
  private final String name;
  private final Map<String, List<KeyValue>> multiProperties = new HashMap<>();
  private final Map<String, Object> singleProperties = new HashMap<>();

  public DSLPlugin(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public Map<String, List<KeyValue>> getMultiProperties() {
    return multiProperties;
  }

  public Map<String, Object> getSingleProperties() {
    return singleProperties;
  }
}
