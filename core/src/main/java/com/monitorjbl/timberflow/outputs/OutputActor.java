package com.monitorjbl.timberflow.outputs;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import com.monitorjbl.timberflow.api.LogLine;
import com.monitorjbl.timberflow.api.Output;
import com.monitorjbl.timberflow.config.RuntimeConfiguration;
import com.monitorjbl.timberflow.monitor.StatsMessage;
import com.monitorjbl.timberflow.reflection.ObjectCreator;

import static com.monitorjbl.timberflow.utils.ThreadUtils.sleep;

public class OutputActor extends UntypedActor {

  private final Output output;
  private final Thread throughputMonitor;
  private long messages = 0;

  public OutputActor(Class<? extends Output> outputClass, Object... constructorArgs) {
    this.output = ObjectCreator.newInstance(outputClass, constructorArgs);
    this.throughputMonitor = new Thread(() -> {
      long lastCheck = messages;
      long lastCheckTime = System.currentTimeMillis();
      ActorRef monitor = context().actorFor("/user/monitor");

      while(true) {
        sleep(1000);
        long throughput = ((messages - lastCheck) / (System.currentTimeMillis() - lastCheckTime));
        monitor.tell(new StatsMessage("input", outputClass.getSimpleName(), throughput), self());
        lastCheckTime = System.currentTimeMillis();
        lastCheck = messages;
      }
    });
    this.throughputMonitor.start();
  }

  @Override
  @SuppressWarnings("unchecked")
  public void onReceive(Object message) throws Throwable {
    if(message instanceof LogLine) {
      messages++;
      LogLine logLine = (LogLine) message;
      output.apply(logLine, RuntimeConfiguration.step(logLine).getConfig());
    } else {
      unhandled(message);
    }
  }

}
