package com.monitorjbl.timbersaw.inputs;

import akka.actor.ActorSelection;
import akka.actor.UntypedActor;
import com.monitorjbl.timbersaw.config.Configuration;
import com.monitorjbl.timbersaw.domain.LogLine;
import com.monitorjbl.timbersaw.domain.SingleStep;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class StdinInput extends UntypedActor {

  @Override
  public void preStart() {
    Scanner sc = new Scanner(System.in);

    new Thread(() -> {
      while(!self().isTerminated() && sc.hasNextLine()) {
        Map<String, String> fields = new HashMap<>();
        fields.put("message", sc.nextLine());

        SingleStep next = Configuration.step(0);
        ActorSelection nextActor = context().actorSelection("../" + next.getName());
        nextActor.tell(new LogLine(next.getNumber(), fields), context().system().deadLetters());
      }
    }).start();
  }

  @Override
  public void onReceive(Object message) throws Throwable { }
}
