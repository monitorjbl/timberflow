package com.monitorjbl.timberflow.outputs.stdout;

import com.monitorjbl.timberflow.config.ConfigParser;
import com.monitorjbl.timberflow.dsl.DSLPlugin;

public class StdoutConfigParser implements ConfigParser<StdoutConfig> {
  @Override
  public StdoutConfig generateConfig(DSLPlugin dslPlugin) {
    String type = (String) dslPlugin.getSingleProperties().get("type");
    return new StdoutConfig(type == null ? "json" : type);
  }
}
