package com.monitorjbl.timbersaw.inputs;

import akka.actor.ActorSelection;
import akka.actor.UntypedActor;
import com.monitorjbl.timbersaw.config.RuntimeConfiguration;
import com.monitorjbl.timbersaw.domain.LogLine;
import com.monitorjbl.timbersaw.domain.SingleStep;

import java.util.HashMap;
import java.util.Map;

public abstract class Input extends UntypedActor {
  @Override
  public final void onReceive(Object message) throws Throwable { }

  protected void sendMessage(String message) {
    Map<String, String> fields = new HashMap<>();
    fields.put("message", message);

    SingleStep next = RuntimeConfiguration.step(0);
    ActorSelection nextActor = context().actorSelection("../" + next.getName());
    nextActor.tell(new LogLine(next.getNumber(), fields), self());
  }

  @Override
  public void preStart() {
    new Thread(this::start).start();
  }

  @Override
  public void postStop() {
    stop();
  }

  protected void stop() {}

  abstract protected void start();
}
