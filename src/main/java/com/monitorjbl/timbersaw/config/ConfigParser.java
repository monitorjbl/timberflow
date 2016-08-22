package com.monitorjbl.timbersaw.config;

import com.monitorjbl.timbersaw.dsl.DSLPlugin;

public interface ConfigParser<T extends Config> {
  T generateConfig(DSLPlugin dslPlugin);
}
