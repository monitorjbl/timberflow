package com.monitorjbl.timberflow.inputs;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.UntypedActor;
import com.monitorjbl.timberflow.api.Input;
import com.monitorjbl.timberflow.api.MessageSender;
import com.monitorjbl.timberflow.config.RuntimeConfiguration;
import com.monitorjbl.timberflow.domain.LogLineImpl;
import com.monitorjbl.timberflow.domain.SingleStep;
import com.monitorjbl.timberflow.monitor.StatsMessage;
import com.monitorjbl.timberflow.reflection.ObjectCreator;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.TreeMap;

import static com.monitorjbl.timberflow.utils.ThreadUtils.sleep;

public class InputActor extends UntypedActor {

  private final Input input;
  private final MessageSender messageSender;
  private final Thread throughputMonitor;
  private long messages = 0;

  public InputActor(Class<? extends Input> outputClass, Object... constructorArgs) {
    this.input = ObjectCreator.newInstance(outputClass, constructorArgs);
    this.messageSender = new MessageSenderImpl();
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
      messages++;
      Map<String, String> fields = new TreeMap<>();
      fields.put("message", message);
      fields.put("@timestamp", ZonedDateTime.now(ZoneOffset.UTC).toString());

      SingleStep next = RuntimeConfiguration.step(0);
      ActorSelection nextActor = context().actorSelection("../" + next.getCls().getCanonicalName());
      nextActor.tell(new LogLineImpl(next.getNumber(), fields), self());
    }
  }
}
