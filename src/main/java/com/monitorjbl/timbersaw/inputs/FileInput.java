package com.monitorjbl.timbersaw.inputs;

import akka.actor.ActorSelection;
import akka.actor.UntypedActor;
import com.monitorjbl.timbersaw.config.Configuration;
import com.monitorjbl.timbersaw.domain.LogLine;
import com.monitorjbl.timbersaw.domain.SingleStep;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FileInput extends UntypedActor {
  private final String path;
  private final Tailer tailer;

  public FileInput(String path) {
    this.path = path;
    this.tailer = Tailer.create(new File(path), new TailerListenerAdapter() {
      public void handle(String line) {
        Map<String, String> fields = new HashMap<>();
        fields.put("message", line);

        SingleStep next = Configuration.step(0);
        ActorSelection nextActor = context().actorSelection("../" + next.getName());
        nextActor.tell(new LogLine(next.getNumber(), fields), context().system().deadLetters());
      }
    }, 1, true);
  }

  @Override
  public void postStop() {
    tailer.stop();
  }

  @Override
  public void onReceive(Object message) throws Throwable { }
}
