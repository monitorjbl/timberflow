package com.monitorjbl.timberflow.outputs;

import akka.actor.UntypedActor;
import com.monitorjbl.timberflow.config.Config;
import com.monitorjbl.timberflow.config.RuntimeConfiguration;
import com.monitorjbl.timberflow.domain.LogLine;

public abstract class Output<T extends Config> extends UntypedActor {

  @Override
  @SuppressWarnings("unchecked")
  public void onReceive(Object message) throws Throwable {
    if(message instanceof LogLine) {
      LogLine logLine = (LogLine) message;
      apply(logLine, (T) RuntimeConfiguration.step(logLine).getConfig());
    } else {
      unhandled(message);
    }
  }

  abstract protected void apply(LogLine logLine, T config);
}
