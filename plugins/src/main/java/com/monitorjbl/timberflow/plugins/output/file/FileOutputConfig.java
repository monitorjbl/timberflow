package com.monitorjbl.timberflow.plugins.output.file;

import com.monitorjbl.timberflow.api.Config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class FileOutputConfig implements Config {
  private String path;
  private String type;
  private Map<String, String> addFields = new HashMap<>();

  public FileOutputConfig(String path, String type, Map<String, String> addFields) {
    this.path = path;
    this.type = type;
    this.addFields = addFields;
  }

  public String getPath() {
    return path;
  }

  public String getType() {
    return type;
  }

  public Map<String, String> getAddFields() {
    return addFields;
  }

  @Override
  public List<Object> getConstructorArgs() {
    return asList(path);
  }

}
