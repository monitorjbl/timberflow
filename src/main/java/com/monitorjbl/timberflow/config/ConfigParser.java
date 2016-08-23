package com.monitorjbl.timberflow.config;

import com.monitorjbl.timberflow.dsl.DSLPlugin;

public interface ConfigParser<T extends Config> {
  T generateConfig(DSLPlugin dslPlugin);
}
