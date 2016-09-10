package com.monitorjbl.timberflow.inputs;

import akka.actor.ActorSelection;
import com.monitorjbl.timberflow.BaseActor;
import com.monitorjbl.timberflow.RuntimeConfiguration;
import com.monitorjbl.timberflow.api.Input;
import com.monitorjbl.timberflow.api.MessageSender;
import com.monitorjbl.timberflow.domain.ActorConfig;
import com.monitorjbl.timberflow.domain.LogLineImpl;
import com.monitorjbl.timberflow.domain.SingleStep;
import com.monitorjbl.timberflow.reflection.ObjectCreator;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.TreeMap;

public class InputActor extends BaseActor {

  private final Input input;
  private final String name;
  private final MessageSender messageSender;

  public InputActor(Class<? extends Input> outputClass, ActorConfig config, Object... constructorArgs) {
    super(config);
    this.input = ObjectCreator.newInstance(outputClass, constructorArgs);
    this.messageSender = new MessageSenderImpl();
    this.name = self().path().name();
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
      fields.put("@input_source", name);
      fields.put("@timestamp", ZonedDateTime.now(ZoneOffset.UTC).toString());
      handleMessage(fields);

      SingleStep next = RuntimeConfiguration.step(0);
      if(next != null) {
        ActorSelection nextActor = context().actorSelection("../" + next.getCls().getCanonicalName());
        nextActor.tell(new LogLineImpl(next.getNumber(), fields), self());
      }
    }
  }
}
