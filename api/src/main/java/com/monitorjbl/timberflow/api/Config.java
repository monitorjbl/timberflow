package com.monitorjbl.timberflow.api;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public interface Config {
  default List<Object> getConstructorArgs() {
    return Collections.emptyList();
  }

  default int getInstances() {
    return 1;
  }

  default void setInstances(int instances) {}

  default Map<String, String> getAddedFields() {
    return Collections.emptyMap();
  }

  default void setAddedFields(Map<String, String> addedFields) {}
}
