package com.monitorjbl.timberflow.dsl;

import com.monitorjbl.timberflow.api.Config;
import com.monitorjbl.timberflow.api.ConfigParser;

import java.util.HashMap;
import java.util.Map;

public class CompilationContext {
  private Map<String, Class> plugins = new HashMap<>();
  private Map<String, ConfigParser> configGenerators = new HashMap<>();

  public void addEntry(String name, Class actor, ConfigParser configParser) {
    plugins.put(name, actor);
    configGenerators.put(name, configParser);
  }

  public Class getPluginClass(String name) {
    assertExists(name);
    return plugins.get(name);
  }

  public Config generatePluginConfig(String name, DSLPlugin plugin) {
    assertExists(name);
    ConfigParser generator = configGenerators.get(name);
    if(generator != null) {
      return generator.generateConfig(plugin);
    } else {
      return null;
    }
  }

  private void assertExists(String name) {
    if(!plugins.containsKey(name)) {
      throw new IllegalArgumentException("No definition for '" + name + "'");
    }
  }
}
