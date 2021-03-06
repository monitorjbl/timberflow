package com.monitorjbl.timberflow.dsl;

import com.monitorjbl.timberflow.api.PluginContent;
import com.monitorjbl.timberflow.domain.ActorConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DSLPlugin implements DSLBlockStatement, PluginContent {
  private final Class cls;
  private final Map<String, List<KeyValue>> multiProperties = new HashMap<>();
  private final Map<String, Object> singleProperties = new HashMap<>();
  private String name;
  private ActorConfig actorConfig;

  public DSLPlugin(String name, Class cls) {
    this.name = name;
    this.cls = cls;
  }

  public String getName() {
    return name;
  }

  void setName(String name) {
    this.name = name;
  }

  public Class getCls() {
    return cls;
  }

  public Map<String, List<KeyValue>> getMultiProperties() {
    return multiProperties;
  }

  public Map<String, Object> getSingleProperties() {
    return singleProperties;
  }

  public ActorConfig getActorConfig() {
    return actorConfig;
  }

  void setActorConfig(ActorConfig actorConfig) {
    this.actorConfig = actorConfig;
  }
}
