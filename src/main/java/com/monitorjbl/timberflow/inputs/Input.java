package com.monitorjbl.timberflow.inputs;

public interface Input {
  default void stop() {}

  default void start(MessageSender sender) {}
}
