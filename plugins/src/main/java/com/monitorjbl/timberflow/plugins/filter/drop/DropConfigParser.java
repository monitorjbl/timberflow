package com.monitorjbl.timberflow.plugins.filter.drop;

import com.monitorjbl.timberflow.api.ConfigParser;
import com.monitorjbl.timberflow.api.PluginContent;

import java.util.Collections;

import static java.util.Arrays.asList;

public class DropConfigParser implements ConfigParser<DropConfig> {
  @Override
  public DropConfig generateConfig(PluginContent dslPlugin) {
    String fields = (String) dslPlugin.getSingleProperties().get("fields");
    if(fields != null) {
      return new DropConfig(asList(fields.split(",")));
    } else {
      return new DropConfig(Collections.emptyList());
    }
  }
}
