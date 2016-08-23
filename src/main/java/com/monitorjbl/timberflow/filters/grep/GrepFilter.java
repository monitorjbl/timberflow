package com.monitorjbl.timberflow.filters.grep;

import com.monitorjbl.timberflow.domain.LogLine;
import com.monitorjbl.timberflow.filters.Filter;
import com.monitorjbl.timberflow.plugin.Plugin;

import java.util.regex.Matcher;

@Plugin(dslName = "grep", configParser = GrepConfigParser.class)
public class GrepFilter extends Filter<GrepConfig> {

  @Override
  protected LogLine apply(LogLine logLine, GrepConfig config) {
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
