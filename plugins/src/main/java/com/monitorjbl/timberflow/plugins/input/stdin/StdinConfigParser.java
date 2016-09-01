package com.monitorjbl.timberflow.plugins.input.stdin;

import com.monitorjbl.timberflow.api.ConfigParser;
import com.monitorjbl.timberflow.api.PluginContent;

public class StdinConfigParser implements ConfigParser<StdinConfig> {
  @Override
  public StdinConfig generateConfig(PluginContent dslPlugin) {
    return new StdinConfig();
  }
}
