package com.monitorjbl.timberflow.plugins.filter.grep;

import com.monitorjbl.timberflow.api.Config;

import java.util.List;
import java.util.regex.Pattern;

public class GrepConfig implements Config {
  private final List<Extract> extracts;

  public GrepConfig(List<Extract> extracts) {
    this.extracts = extracts;
  }

  public List<Extract> getExtracts() {
    return extracts;
  }

  public static class Extract {
    private final String field;
    private final Pattern regex;
    private final List<String> fields;

    public Extract(String field, Pattern regex, List<String> fields) {
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
}
