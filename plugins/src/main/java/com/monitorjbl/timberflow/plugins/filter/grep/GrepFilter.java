package com.monitorjbl.timberflow.plugins.filter.grep;

import com.monitorjbl.timberflow.api.Filter;
import com.monitorjbl.timberflow.api.LogLine;
import com.monitorjbl.timberflow.api.Plugin;

import java.util.regex.Matcher;

@Plugin(dslName = "grep", configParser = GrepConfigParser.class)
public class GrepFilter implements Filter<GrepConfig> {
  @Override
  public LogLine apply(LogLine logLine, GrepConfig config) {
    config.getMatches().forEach(m -> {
      if(logLine.getFields().containsKey(m.getField())) {
        Matcher matcher = m.getRegex().matcher(logLine.getField(m.getField()));
        if(matcher.matches()) {
          m.getFields().forEach(field -> logLine.getFields().put(field.replaceAll(GrepConfigParser.UNDERSCORE, "_"), matcher.group(field)));
        }
      }
    });
    return logLine;
  }
}
