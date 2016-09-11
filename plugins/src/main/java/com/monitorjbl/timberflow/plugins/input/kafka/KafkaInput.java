package com.monitorjbl.timberflow.plugins.input.kafka;

import com.monitorjbl.timberflow.api.Input;
import com.monitorjbl.timberflow.api.MessageSender;
import com.monitorjbl.timberflow.api.Plugin;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Collections;
import java.util.Properties;

import static java.lang.Thread.currentThread;

@Plugin(dslName = "kafka", configParser = KafkaInputConfigParser.class)
public class KafkaInput implements Input {

  private final KafkaInputConfig config;
  private KafkaConsumer<String, String> consumer;

  public KafkaInput(KafkaInputConfig config) {
    this.config = config;
  }

  @Override
  public void stop() {
    consumer.close();
  }

  @Override
  public void start(MessageSender sender) {
    Properties props = new Properties();
    props.put("bootstrap.servers", config.getBootstrapServers());
    props.put("group.id", config.getGroupId() == null ? config.getTopic() + "-" + System.nanoTime() : config.getGroupId());
    props.put("enable.auto.commit", "true");
    props.put("auto.commit.interval.ms", "1000");
    props.put("session.timeout.ms", "30000");
    props.put("key.deserializer", StringDeserializer.class);
    props.put("value.deserializer", StringDeserializer.class);

    //workaround for KAFKA-3218
    currentThread().setContextClassLoader(null);
    consumer = new KafkaConsumer<>(props);
    consumer.subscribe(Collections.singletonList(config.getTopic()));
    while(true) {
      ConsumerRecords<String, String> records = consumer.poll(100);
      for(ConsumerRecord<String, String> record : records) {
        sender.sendMessage(record.value());
      }
    }
  }
}
