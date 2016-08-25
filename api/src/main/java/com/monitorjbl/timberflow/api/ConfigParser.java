package com.monitorjbl.timberflow.api;

public interface ConfigParser<T extends Config> {
  T generateConfig(PluginContent pluginContent);
}
