package com.monitorjbl.timbersaw.filters;

import akka.actor.ActorSelection;
import akka.actor.UntypedActor;
import com.monitorjbl.timbersaw.config.Config;
import com.monitorjbl.timbersaw.config.RuntimeConfiguration;
import com.monitorjbl.timbersaw.domain.LogLine;
import com.monitorjbl.timbersaw.domain.SingleStep;

public abstract class Filter<T extends Config> extends UntypedActor {

  @Override
  public void onReceive(Object message) throws Throwable {
    if(message instanceof LogLine) {
      LogLine logLine = (LogLine) message;
      SingleStep<T> current = RuntimeConfiguration.step(logLine.getCurrentStep());
      SingleStep<T> next = RuntimeConfiguration.step(logLine.getCurrentStep() + 1);

      logLine = apply(logLine, current.getConfig());
      ActorSelection nextActor = context().actorSelection("../" + next.getName());
      nextActor.tell(new LogLine(next.getNumber(), logLine.getFields()), self());
    } else {
      unhandled(message);
    }
  }

  abstract protected LogLine apply(LogLine logLine, T config);
}
