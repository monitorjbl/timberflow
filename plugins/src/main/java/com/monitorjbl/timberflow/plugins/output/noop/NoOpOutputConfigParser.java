package com.monitorjbl.timberflow.plugins.output.noop;

import com.monitorjbl.timberflow.api.ConfigParser;
import com.monitorjbl.timberflow.api.PluginContent;

public class NoOpOutputConfigParser implements ConfigParser<NoOpOutputConfig> {
  @Override
  public NoOpOutputConfig generateConfig(PluginContent dslPlugin) {
    return new NoOpOutputConfig();
  }
}
