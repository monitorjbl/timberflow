package com.monitorjbl.timbersaw.filters.drop;

import com.monitorjbl.timbersaw.domain.LogLine;
import com.monitorjbl.timbersaw.filters.Filter;

import java.util.List;
import java.util.Map;

public class DropFilter extends Filter<DropConfig> {
  @Override
  protected LogLine apply(LogLine logLine, DropConfig config) {
    dropFields(logLine.getFields(), config.getFields());
    return logLine;
  }

  private void dropFields(Map<String, String> map, List<String> fields) {
    fields.forEach(map::remove);
  }
}
