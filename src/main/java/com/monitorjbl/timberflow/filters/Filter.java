package com.monitorjbl.timberflow.filters;

import akka.actor.ActorSelection;
import akka.actor.UntypedActor;
import com.monitorjbl.timberflow.config.Config;
import com.monitorjbl.timberflow.config.RuntimeConfiguration;
import com.monitorjbl.timberflow.domain.LogLine;
import com.monitorjbl.timberflow.domain.SingleStep;

public abstract class Filter<T extends Config> extends UntypedActor {

  @Override
  public void onReceive(Object message) throws Throwable {
    if(message instanceof LogLine) {
      LogLine logLine = (LogLine) message;
      SingleStep<T> current = RuntimeConfiguration.step(logLine);

      logLine = apply(logLine, current.getConfig());

      SingleStep<T> next = RuntimeConfiguration.nextStep(logLine);
      ActorSelection nextActor = context().actorSelection("../" + next.getName());
      nextActor.tell(new LogLine(next.getNumber(), logLine.getFields()), self());
    } else {
      unhandled(message);
    }
  }

  abstract protected LogLine apply(LogLine logLine, T config);
}
