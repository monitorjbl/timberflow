package com.monitorjbl.timbersaw.dsl;

public class KeyValue {
  public String key;
  public String value;

  public KeyValue(String key, String value) {
    this.key = key;
    this.value = value;
  }

  public String getKey() {
    return key;
  }

  public String getValue() {
    return value;
  }
}
