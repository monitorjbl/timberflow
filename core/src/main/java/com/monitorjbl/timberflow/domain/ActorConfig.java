package com.monitorjbl.timberflow.domain;

import com.monitorjbl.timberflow.api.Config;

import java.util.Map;

public class ActorConfig {
  private Config config;
  private int instances;
  private Map<String, String> addedFields;

  public ActorConfig(Config config, int instances, Map<String, String> addedFields) {
    this.config = config;
    this.instances = instances;
    this.addedFields = addedFields;
  }

  public Config getConfig() {
    return config;
  }

  public int getInstances() {
    return instances;
  }

  public Map<String, String> getAddedFields() {
    return addedFields;
  }
}
