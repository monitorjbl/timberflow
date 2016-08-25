package com.monitorjbl.timberflow.outputs;

import akka.actor.UntypedActor;
import com.monitorjbl.timberflow.api.LogLine;
import com.monitorjbl.timberflow.api.Output;
import com.monitorjbl.timberflow.config.RuntimeConfiguration;
import com.monitorjbl.timberflow.reflection.ObjectCreator;

public class OutputActor extends UntypedActor {

  private final Output output;

  public OutputActor(Class<? extends Output> outputClass, Object... constructorArgs) {
    this.output = ObjectCreator.newInstance(outputClass, constructorArgs);
  }

  @Override
  @SuppressWarnings("unchecked")
  public void onReceive(Object message) throws Throwable {
    if(message instanceof LogLine) {
      LogLine logLine = (LogLine) message;
      output.apply(logLine, RuntimeConfiguration.step(logLine).getConfig());
    } else {
      unhandled(message);
    }
  }

}
