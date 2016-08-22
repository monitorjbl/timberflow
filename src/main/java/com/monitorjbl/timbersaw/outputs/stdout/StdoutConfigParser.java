package com.monitorjbl.timbersaw.outputs.stdout;

import com.monitorjbl.timbersaw.config.ConfigParser;
import com.monitorjbl.timbersaw.dsl.DSLPlugin;

public class StdoutConfigParser implements ConfigParser<StdoutConfig> {
  @Override
  public StdoutConfig generateConfig(DSLPlugin dslPlugin) {
    String type = (String) dslPlugin.getSingleProperties().get("type");
    return new StdoutConfig(type == null ? "json" : type);
  }
}
