package com.monitorjbl.timberflow.plugins.filter.grep;

import com.monitorjbl.timberflow.api.Config;

import java.util.List;
import java.util.regex.Pattern;

public class GrepConfig implements Config {
  private final List<Match> matches;
  private final int instances;

  public GrepConfig(List<Match> matches, int instances) {
    this.matches = matches;
    this.instances = instances;
  }

  public List<Match> getMatches() {
    return matches;
  }

  @Override
  public int getInstances(){
    return instances;
  }

  public static class Match {
    private final String field;
    private final Pattern regex;
    private final List<String> fields;

    public Match(String field, Pattern regex, List<String> fields) {
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
