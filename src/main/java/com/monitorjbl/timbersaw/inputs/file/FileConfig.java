package com.monitorjbl.timbersaw.inputs.file;

import com.monitorjbl.timbersaw.config.Config;

import java.util.HashMap;
import java.util.Map;

public class FileConfig implements Config {
  private boolean fromBeginning;
  private String path;
  private Map<String, String> addFields = new HashMap<>();

  public FileConfig(boolean fromBeginning, String path, Map<String, String> addFields) {
    this.fromBeginning = fromBeginning;
    this.path = path;
    this.addFields = addFields;
  }

  public boolean isFromBeginning() {
    return fromBeginning;
  }

  public String getPath() {
    return path;
  }

  public Map<String, String> getAddFields() {
    return addFields;
  }
}
