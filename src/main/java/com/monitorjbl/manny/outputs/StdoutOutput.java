package com.monitorjbl.manny.outputs;

import akka.actor.UntypedActor;
import com.monitorjbl.manny.config.Configuration;
import com.monitorjbl.manny.domain.LogLine;
import com.monitorjbl.manny.domain.SingleStep;
import com.monitorjbl.manny.serializers.JsonSerializer;

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
