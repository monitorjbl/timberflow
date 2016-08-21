package com.monitorjbl.timbersaw.outputs;

import akka.actor.UntypedActor;
import com.monitorjbl.timbersaw.config.Configuration;
import com.monitorjbl.timbersaw.domain.LogLine;
import com.monitorjbl.timbersaw.domain.SingleStep;
import com.monitorjbl.timbersaw.serializers.JsonSerializer;

import java.util.Map;

public class StdoutOutput extends UntypedActor {
  @Override
  public void onReceive(Object message) throws Throwable {
    if(message instanceof LogLine) {
      LogLine logLine = (LogLine) message;
      SingleStep<String> step = Configuration.step(logLine.getCurrentStep());

      System.out.println(serialize(step.getConfig(), logLine.getFields()));
      System.out.flush();
    } else {
      unhandled(message);
    }
  }

  private static String serialize(String type, Map<String, String> fields) {
    switch(type) {
      case "json":
        return JsonSerializer.serialize(fields);
      default:
        return "{}";
    }
  }
}
