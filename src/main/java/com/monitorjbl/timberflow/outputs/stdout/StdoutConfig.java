package com.monitorjbl.timberflow.outputs.stdout;

import com.monitorjbl.timberflow.config.Config;

public class StdoutConfig implements Config {
  private String type;

  public StdoutConfig(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }
}
