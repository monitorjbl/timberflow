package com.monitorjbl.timberflow.api;

public interface Input {
  default void stop() {}

  default void start(MessageSender sender) {}
}
