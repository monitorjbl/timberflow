package com.monitorjbl.timberflow.plugins.input.kafka;

import com.monitorjbl.timberflow.api.ConfigParser;
import com.monitorjbl.timberflow.api.PluginContent;

public class KafkaInputConfigParser implements ConfigParser<KafkaInputConfig> {
  @Override
  public KafkaInputConfig generateConfig(PluginContent dslPlugin) {
    String bootstrapServers = (String) dslPlugin.getSingleProperties().get("bootstrap_servers");
    String groupId = (String) dslPlugin.getSingleProperties().get("group_id");
    String topic = (String) dslPlugin.getSingleProperties().get("topic");

    if(bootstrapServers == null) {
      throw new IllegalStateException("bootstrapServers is required for kafka{}");
    }
    if(topic == null) {
      throw new IllegalStateException("topic is required for kafka{}");
    }

    return new KafkaInputConfig(bootstrapServers, groupId, topic);
  }
}
