package com.monitorjbl.timberflow.filters.drop;

import com.monitorjbl.timberflow.config.ConfigParser;
import com.monitorjbl.timberflow.dsl.DSLPlugin;

import java.util.Collections;

import static java.util.Arrays.asList;

public class DropConfigParser implements ConfigParser<DropConfig> {
  @Override
  public DropConfig generateConfig(DSLPlugin dslPlugin) {
    String fields = (String) dslPlugin.getSingleProperties().get("fields");
    if(fields != null) {
      return new DropConfig(asList(fields.split(",")));
    } else {
      return new DropConfig(Collections.emptyList());
    }
  }
}
