package com.monitorjbl.timberflow.monitor;

public class StatsMessage {
  private String type;
  private String pluginName;
  private long messagesPerMillisecond;

  public StatsMessage(String type, String pluginName, long messagesPerMillisecond) {
    this.type = type;
    this.pluginName = pluginName;
    this.messagesPerMillisecond = messagesPerMillisecond;
  }

  public String getType() {
    return type;
  }

  public String getPluginName() {
    return pluginName;
  }

  public long getMessagesPerMillisecond() {
    return messagesPerMillisecond;
  }
}
