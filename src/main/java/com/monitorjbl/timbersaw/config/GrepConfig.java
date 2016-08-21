package com.monitorjbl.timbersaw.config;

import java.util.List;
import java.util.regex.Pattern;

public class GrepConfig {
  private final String field;
  private final Pattern regex;
  private final List<String> fields;

  public GrepConfig(String field, Pattern regex, List<String> fields) {
    this.field = field;
    this.regex = regex;
    this.fields = fields;
  }

  public String getField() {
    return field;
  }

  public Pattern getRegex() {
    return regex;
  }

  public List<String> getFields() {
    return fields;
  }
}
