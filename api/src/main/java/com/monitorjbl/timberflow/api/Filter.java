package com.monitorjbl.timberflow.api;

public interface Filter<T extends Config> {
  LogLine apply(LogLine logLine, T config);
}
