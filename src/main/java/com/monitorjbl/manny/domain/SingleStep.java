package com.monitorjbl.manny.domain;

public class SingleStep<T> {
  private Integer number;
  private String name;
  private T config;

  public SingleStep() { }

  public SingleStep(Integer number, String name, T config) {
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

  public T getConfig() {
    return config;
  }
}
