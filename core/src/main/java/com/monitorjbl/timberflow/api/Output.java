package com.monitorjbl.timberflow.api;

public interface Output<T extends Config> {
  void apply(LogLine logLine, T config);
}
