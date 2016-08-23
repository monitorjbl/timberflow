package com.monitorjbl.timberflow.inputs.stdin;

import com.monitorjbl.timberflow.config.ConfigParser;
import com.monitorjbl.timberflow.dsl.DSLPlugin;

public class StdinConfigParser implements ConfigParser<StdinConfig> {
  @Override
  public StdinConfig generateConfig(DSLPlugin dslPlugin) {
    return new StdinConfig();
  }
}
