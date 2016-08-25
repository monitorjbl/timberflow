package com.monitorjbl.timberflow.api;

import java.util.List;
import java.util.Map;

public interface PluginContent {
  String getName();

  Class getCls();

  Map<String, List<KeyValue>> getMultiProperties();

  Map<String, Object> getSingleProperties();

  Config getConfig();

  class KeyValue {
    private final String key;
    private final String value;

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
}
