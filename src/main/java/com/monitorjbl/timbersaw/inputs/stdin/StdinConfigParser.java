package com.monitorjbl.timbersaw.inputs.stdin;

import com.monitorjbl.timbersaw.config.ConfigParser;
import com.monitorjbl.timbersaw.dsl.DSLPlugin;

public class StdinConfigParser implements ConfigParser<StdinConfig> {
  @Override
  public StdinConfig generateConfig(DSLPlugin dslPlugin) {
    return new StdinConfig();
  }
}
