package com.monitorjbl.timberflow.plugins.input.kafka;

import com.monitorjbl.timberflow.api.Config;

public class KafkaInputConfig implements Config {
  private String bootstrapServers;
  private String groupId;
  private String topic;

  public KafkaInputConfig(String bootstrapServers, String groupId, String topic) {
    this.bootstrapServers = bootstrapServers;
    this.groupId = groupId;
    this.topic = topic;
  }

  public String getBootstrapServers() {
    return bootstrapServers;
  }

  public String getGroupId() {
    return groupId;
  }

  public String getTopic() {
    return topic;
  }
}
