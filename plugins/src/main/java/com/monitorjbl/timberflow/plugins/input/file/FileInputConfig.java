package com.monitorjbl.timberflow.plugins.input.file;

import com.monitorjbl.timberflow.api.Config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class FileInputConfig implements Config {
  private String path;
  private boolean fromBeginning;
  private Map<String, String> addedFields = new HashMap<>();

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

  @Override
  public List<Object> getConstructorArgs() {
    return asList(path, fromBeginning);
  }
}
