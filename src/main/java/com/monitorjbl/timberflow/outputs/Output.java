package com.monitorjbl.timberflow.outputs;

import com.monitorjbl.timberflow.config.Config;
import com.monitorjbl.timberflow.domain.LogLine;

public interface Output<T extends Config> {
  void apply(LogLine logLine, T config);
}
