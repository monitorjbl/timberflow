package com.monitorjbl.timberflow.api;

import java.util.Map;

public interface LogLine {
  Integer getCurrentStep();

  Map<String, String> getFields();

  String getField(String field);
}
