package com.monitorjbl.timberflow.domain;

import com.monitorjbl.timberflow.api.Config;

public class SingleStep implements Step {
  private Integer number;
  private String name;
  private Config config;

  public SingleStep() { }

  public SingleStep(Integer number, String name, Config config) {
    this.number = number;
    this.name = name;
    this.config = config;
  }

  public Integer getNumber() {
    return number;
  }

  public String getName() {
    return name;
  }

  public Config getConfig() {
    return config;
  }
}
