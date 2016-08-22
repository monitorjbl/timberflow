package com.monitorjbl.timbersaw.outputs.stdout;

import com.monitorjbl.timbersaw.config.Config;

public class StdoutConfig implements Config {
  private String type;

  public StdoutConfig(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }
}
