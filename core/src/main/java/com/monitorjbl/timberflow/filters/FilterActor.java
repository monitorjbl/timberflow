package com.monitorjbl.timberflow.filters;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.UntypedActor;
import com.monitorjbl.timberflow.api.Filter;
import com.monitorjbl.timberflow.api.LogLine;
import com.monitorjbl.timberflow.config.RuntimeConfiguration;
import com.monitorjbl.timberflow.domain.LogLineImpl;
import com.monitorjbl.timberflow.domain.SingleStep;
import com.monitorjbl.timberflow.monitor.StatsMessage;
import com.monitorjbl.timberflow.reflection.ObjectCreator;

import static com.monitorjbl.timberflow.utils.ThreadUtils.sleep;

public class FilterActor extends UntypedActor {

  private final Filter filter;
  private final Thread throughputMonitor;
  private long messages = 0;
  private long throughput = 0;

  public FilterActor(Class<? extends Filter> outputClass, Object... constructorArgs) {
    this.filter = ObjectCreator.newInstance(outputClass, constructorArgs);
    this.throughputMonitor = new Thread(() -> {
      long lastCheck = messages;
      long lastCheckTime = System.currentTimeMillis();
      ActorRef monitor = context().actorFor("/user/monitor");

      while(true) {
        sleep(1000);
        throughput = ((messages - lastCheck) / (System.currentTimeMillis() - lastCheckTime));
        monitor.tell(new StatsMessage("filter", self().path().name(), throughput), self());
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
      SingleStep current = RuntimeConfiguration.step(logLine);

      logLine = filter.apply(logLine, current.getConfig());

      SingleStep next = RuntimeConfiguration.nextStep(logLine);
      ActorSelection nextActor = context().actorSelection("../" + next.getName());
      nextActor.tell(new LogLineImpl(next.getNumber(), logLine.getFields()), self());
    } else {
      unhandled(message);
    }
  }
}
