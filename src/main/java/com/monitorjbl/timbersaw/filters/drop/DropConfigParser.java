package com.monitorjbl.timbersaw.filters.drop;

import com.monitorjbl.timbersaw.config.ConfigParser;
import com.monitorjbl.timbersaw.dsl.DSLPlugin;

import static java.util.Arrays.asList;

public class DropConfigParser implements ConfigParser<DropConfig> {
  @Override
  public DropConfig generateConfig(DSLPlugin dslPlugin) {
    String fields = (String) dslPlugin.getSingleProperties().get("fields");
    return new DropConfig(asList(fields.split(",")));
  }
}
