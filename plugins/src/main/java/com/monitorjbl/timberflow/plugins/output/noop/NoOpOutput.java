package com.monitorjbl.timberflow.plugins.output.noop;

import com.monitorjbl.timberflow.api.LogLine;
import com.monitorjbl.timberflow.api.Output;
import com.monitorjbl.timberflow.api.Plugin;

@Plugin(dslName = "noop", configParser = NoOpOutputConfigParser.class)
public class NoOpOutput implements Output<NoOpOutputConfig> {
  @Override
  public void apply(LogLine logLine, NoOpOutputConfig config) { }
}
