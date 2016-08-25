package com.monitorjbl.timberflow.inputs;

import akka.actor.ActorSelection;
import akka.actor.UntypedActor;
import com.monitorjbl.timberflow.config.RuntimeConfiguration;
import com.monitorjbl.timberflow.domain.LogLine;
import com.monitorjbl.timberflow.domain.SingleStep;
import com.monitorjbl.timberflow.reflection.ObjectCreator;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.TreeMap;

public class InputActor extends UntypedActor {

  private final Input input;
  private final MessageSender messageSender;

  public InputActor(Class<? extends Input> outputClass, Object... constructorArgs) {
    this.input = ObjectCreator.newInstance(outputClass, constructorArgs);
    this.messageSender = new MessageSenderImpl();
  }

  @Override
  public final void onReceive(Object message) throws Throwable { }

  @Override
  public void preStart() {
    new Thread(() -> input.start(messageSender)).start();
  }

  @Override
  public void postStop() {
    input.stop();
  }

  private class MessageSenderImpl implements MessageSender {
    public void sendMessage(String message) {
      Map<String, String> fields = new TreeMap<>();
      fields.put("message", message);
      fields.put("@timestamp", ZonedDateTime.now(ZoneOffset.UTC).toString());

      SingleStep next = RuntimeConfiguration.step(0);
      ActorSelection nextActor = context().actorSelection("../" + next.getName());
      nextActor.tell(new LogLine(next.getNumber(), fields), self());
    }
  }
}
