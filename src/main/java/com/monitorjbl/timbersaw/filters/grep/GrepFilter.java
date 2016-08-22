package com.monitorjbl.timbersaw.filters.grep;

import com.monitorjbl.timbersaw.domain.LogLine;
import com.monitorjbl.timbersaw.filters.Filter;

import java.util.regex.Matcher;

public class GrepFilter extends Filter<GrepConfig> {

  @Override
  protected LogLine apply(LogLine logLine, GrepConfig config) {
    config.getMatches().forEach(m -> {
      if(logLine.getFields().containsKey(m.getField())) {
        Matcher matcher = m.getRegex().matcher(logLine.getField(m.getField()));
        if(matcher.matches()) {
          m.getFields().forEach(field -> logLine.getFields().put(field, matcher.group(field)));
        }
      }
    });
    return logLine;
  }
}
