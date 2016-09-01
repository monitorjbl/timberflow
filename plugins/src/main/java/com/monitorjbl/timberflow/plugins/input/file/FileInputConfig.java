package com.monitorjbl.timberflow.plugins.input.file;

import com.monitorjbl.timberflow.api.Config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class FileInputConfig implements Config {
  private String path;
  private boolean fromBeginning;
  private Map<String, String> addFields = new HashMap<>();

  public FileInputConfig(String path, boolean fromBeginning, Map<String, String> addFields) {
    this.path = path;
    this.fromBeginning = fromBeginning;
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

  @Override
  public List<Object> getConstructorArgs() {
    return asList(path, fromBeginning);
  }
}
