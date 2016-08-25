package com.monitorjbl.timberflow.plugins.stdout;

import com.monitorjbl.timberflow.api.ConfigParser;
import com.monitorjbl.timberflow.api.PluginContent;

public class StdoutConfigParser implements ConfigParser<StdoutConfig> {
  @Override
  public StdoutConfig generateConfig(PluginContent dslPlugin) {
    String type = (String) dslPlugin.getSingleProperties().get("type");
    return new StdoutConfig(type == null ? "json" : type);
  }
}
