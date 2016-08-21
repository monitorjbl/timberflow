package com.monitorjbl.manny.filters;

import akka.actor.ActorSelection;
import akka.actor.UntypedActor;
import com.monitorjbl.manny.config.Configuration;
import com.monitorjbl.manny.domain.LogLine;
import com.monitorjbl.manny.domain.SingleStep;

import java.util.List;
import java.util.Map;

public class DropFilter extends UntypedActor {
  @Override
  public void onReceive(Object message) throws Throwable {
    if(message instanceof LogLine) {
      LogLine logLine = (LogLine) message;
      SingleStep<List<String>> step = Configuration.step(logLine.getCurrentStep());
      dropFields(logLine.getFields(), step.getConfig());

      SingleStep next = Configuration.step(logLine.getCurrentStep() + 1);
      ActorSelection nextActor = context().actorSelection("../" + next.getName());
      nextActor.tell(new LogLine(next.getNumber(), logLine.getFields()), context().system().deadLetters());
    } else {
      unhandled(message);
    }
  }

  private void dropFields(Map<String, String> map, List<String> fields) {
    fields.forEach(map::remove);
  }
}
