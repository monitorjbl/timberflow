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
  public Map<String, String> getAddedFields() {
    return addedFields;
  }

  @Override
  public void setAddedFields(Map<String, String> addedFields) {
    this.addedFields = addedFields;
  }

  @Override
  public List<Object> getConstructorArgs() {
    return asList(path, fromBeginning);
  }
}
