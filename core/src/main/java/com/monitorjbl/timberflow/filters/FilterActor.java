package com.monitorjbl.timberflow.filters;

import akka.actor.ActorSelection;
import com.monitorjbl.timberflow.BaseActor;
import com.monitorjbl.timberflow.api.Filter;
import com.monitorjbl.timberflow.api.LogLine;
import com.monitorjbl.timberflow.RuntimeConfiguration;
import com.monitorjbl.timberflow.domain.ActorConfig;
import com.monitorjbl.timberflow.domain.LogLineImpl;
import com.monitorjbl.timberflow.domain.SingleStep;
import com.monitorjbl.timberflow.reflection.ObjectCreator;

public class FilterActor extends BaseActor {

  private final Filter filter;

  public FilterActor(Class<? extends Filter> outputClass, ActorConfig config, Object... constructorArgs) {
    super(config);
    this.filter = ObjectCreator.newInstance(outputClass, constructorArgs);
  }

  @Override
  @SuppressWarnings("unchecked")
  public void onReceive(Object message) throws Throwable {
    if(message instanceof LogLine) {
      LogLine logLine = (LogLine) message;
      SingleStep current = RuntimeConfiguration.step(logLine);

      logLine = filter.apply(logLine, current.getConfig());
      handleMessage(logLine.getFields());

      SingleStep next = RuntimeConfiguration.nextStep(logLine);
      if(next != null) {
        ActorSelection nextActor = context().actorSelection("../" + next.getName());
        nextActor.tell(new LogLineImpl(next.getNumber(), logLine.getFields()), self());
      }
    } else {
      unhandled(message);
    }
  }
}
