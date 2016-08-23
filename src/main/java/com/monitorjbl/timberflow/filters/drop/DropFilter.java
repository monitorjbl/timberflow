package com.monitorjbl.timberflow.filters.drop;

import com.monitorjbl.timberflow.domain.LogLine;
import com.monitorjbl.timberflow.filters.Filter;
import com.monitorjbl.timberflow.plugin.Plugin;

import java.util.List;
import java.util.Map;

@Plugin(dslName = "drop", configParser = DropConfigParser.class)
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
