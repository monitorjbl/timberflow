package com.monitorjbl.timberflow.plugins.stdin;

import com.monitorjbl.timberflow.api.ConfigParser;
import com.monitorjbl.timberflow.api.PluginContent;

public class StdinConfigParser implements ConfigParser<StdinConfig> {
  @Override
  public StdinConfig generateConfig(PluginContent dslPlugin) {
    return new StdinConfig();
  }
}
