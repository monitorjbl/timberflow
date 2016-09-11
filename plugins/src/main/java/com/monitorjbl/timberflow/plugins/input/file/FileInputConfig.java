package com.monitorjbl.timberflow.plugins.input.file;

import com.monitorjbl.timberflow.api.Config;

public class FileInputConfig implements Config {
  private String path;
  private boolean fromBeginning;

  public FileInputConfig(String path, boolean fromBeginning) {
    this.path = path;
    this.fromBeginning = fromBeginning;
  }

  public boolean isFromBeginning() {
    return fromBeginning;
  }

  public String getPath() {
    return path;
  }
}
