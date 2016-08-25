package com.monitorjbl.timberflow.filters;

import com.monitorjbl.timberflow.config.Config;
import com.monitorjbl.timberflow.domain.LogLine;

public interface Filter<T extends Config> {
  LogLine apply(LogLine logLine, T config);
}
