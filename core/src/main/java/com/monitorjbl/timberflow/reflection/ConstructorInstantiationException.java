package com.monitorjbl.timberflow.reflection;

public class ConstructorInstantiationException extends RuntimeException {
  public ConstructorInstantiationException(String message) {
    super(message);
  }

  public ConstructorInstantiationException(String message, Throwable cause) {
    super(message, cause);
  }
}
