package com.monitorjbl.timberflow.plugins.output.noop;

import com.monitorjbl.timberflow.api.Config;

import java.util.Collections;
import java.util.List;

public class NoOpOutputConfig implements Config {
  @Override
  public List<Object> getConstructorArgs() {
    return Collections.emptyList();
  }
}
