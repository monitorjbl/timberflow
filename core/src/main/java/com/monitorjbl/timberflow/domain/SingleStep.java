package com.monitorjbl.timberflow.domain;

import com.monitorjbl.timberflow.api.Config;

public class SingleStep implements Step {
  private Integer number;
  private Class cls;
  private String name;
  private Config config;

  public SingleStep() { }

  public SingleStep(Integer number, Class cls, String name, Config config) {
    this.number = number;
    this.cls = cls;
    this.name = name;
    this.config = config;
  }

  public Integer getNumber() {
    return number;
  }

  public Class getCls() {
    return cls;
  }

  public String getName() {
    return name;
  }

  public Config getConfig() {
    return config;
  }
}
