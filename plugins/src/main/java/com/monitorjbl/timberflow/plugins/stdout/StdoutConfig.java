package com.monitorjbl.timberflow.plugins.stdout;

import com.monitorjbl.timberflow.api.Config;

public class StdoutConfig implements Config {
  private String type;

  public StdoutConfig(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }
}
