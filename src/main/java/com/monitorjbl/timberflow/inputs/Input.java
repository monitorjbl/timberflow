package com.monitorjbl.timberflow.inputs;

import akka.actor.ActorSelection;
import akka.actor.UntypedActor;
import com.monitorjbl.timberflow.config.RuntimeConfiguration;
import com.monitorjbl.timberflow.domain.LogLine;
import com.monitorjbl.timberflow.domain.SingleStep;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.TreeMap;

public abstract class Input extends UntypedActor {
  @Override
  public final void onReceive(Object message) throws Throwable { }

  protected void sendMessage(String message) {
    Map<String, String> fields = new TreeMap<>();
    fields.put("message", message);
    fields.put("@timestamp", ZonedDateTime.now(ZoneOffset.UTC).toString());

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
