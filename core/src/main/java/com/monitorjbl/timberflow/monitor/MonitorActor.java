package com.monitorjbl.timberflow.monitor;

import akka.actor.UntypedActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.monitorjbl.timberflow.utils.ThreadUtils.sleep;
import static java.util.stream.Collectors.joining;

public class MonitorActor extends UntypedActor {
  private static final Logger log = LoggerFactory.getLogger(MonitorActor.class);

  private final Map<String, Long> throughput = new HashMap<>();
  private final String border = "-------------------------------------------------";
  private Thread reporter;
  private boolean running = true;

  @Override
  public void preStart() {
    this.reporter = new Thread(() -> {
      sleep(1000);
      while(running) {
        String report = throughput.entrySet().stream()
            .map(e -> String.format("%-25s: %s/ms", e.getKey(), e.getValue()))
            .collect(joining("\n"));
        log.debug("Throughput Report\n{}\n{}\n{}", border, report, border);
        sleep(1000);
      }
    });
    this.reporter.start();
  }

  @Override
  public void onReceive(Object message) throws Throwable {
    if(message instanceof StatsMessage) {
      StatsMessage stats = (StatsMessage) message;
      throughput.put(stats.getPluginName(), stats.getMessagesPerMillisecond());
    } else {
      unhandled(message);
    }
  }
}
