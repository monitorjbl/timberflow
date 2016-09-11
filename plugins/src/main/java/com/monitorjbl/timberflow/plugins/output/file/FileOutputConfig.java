package com.monitorjbl.timberflow.plugins.output.file;

import com.monitorjbl.timberflow.api.Config;

public class FileOutputConfig implements Config {
  private String path;
  private String type;

  public FileOutputConfig(String path, String type) {
    this.path = path;
    this.type = type;
  }

  public String getPath() {
    return path;
  }

  public String getType() {
    return type;
  }
}
