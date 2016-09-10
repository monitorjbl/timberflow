package com.monitorjbl.timberflow;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import com.monitorjbl.timberflow.domain.ActorConfig;
import com.monitorjbl.timberflow.monitor.StatsMessage;

import java.util.Map;

import static com.monitorjbl.timberflow.utils.ThreadUtils.sleep;

public abstract class BaseActor extends UntypedActor {
  private final ActorConfig config;
  private final Thread throughputMonitor;
  private long messages = 0;

  protected BaseActor(ActorConfig config) {
    this.config = config;
    this.throughputMonitor = new Thread(() -> {
      long lastCheck = messages;
      long lastCheckTime = System.currentTimeMillis();
      ActorRef monitor = context().actorFor("/user/monitor");

      while(true) {
        sleep(1000);
        long throughput = ((messages - lastCheck) / (System.currentTimeMillis() - lastCheckTime));
        monitor.tell(new StatsMessage("input", self().path().name(), throughput), self());
        lastCheckTime = System.currentTimeMillis();
        lastCheck = messages;
      }
    });
    this.throughputMonitor.start();
  }

  protected void handleMessage(Map<String, String> fields) {
    messages++;
    config.getAddedFields().forEach(fields::put);
  }
}
