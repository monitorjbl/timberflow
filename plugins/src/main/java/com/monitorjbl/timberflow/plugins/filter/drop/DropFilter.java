package com.monitorjbl.timberflow.plugins.filter.drop;

import com.monitorjbl.timberflow.api.Filter;
import com.monitorjbl.timberflow.api.LogLine;
import com.monitorjbl.timberflow.api.Plugin;

import java.util.List;
import java.util.Map;

@Plugin(dslName = "drop", configParser = DropConfigParser.class)
public class DropFilter implements Filter<DropConfig> {
  @Override
  public LogLine apply(LogLine logLine, DropConfig config) {
    dropFields(logLine.getFields(), config.getFields());
    return logLine;
  }

  private void dropFields(Map<String, String> map, List<String> fields) {
    fields.forEach(map::remove);
  }
}
