package com.monitorjbl.timbersaw.outputs;

import akka.actor.UntypedActor;
import com.monitorjbl.timbersaw.config.Config;
import com.monitorjbl.timbersaw.config.RuntimeConfiguration;
import com.monitorjbl.timbersaw.domain.LogLine;

public abstract class Output<T extends Config> extends UntypedActor {

  @Override
  @SuppressWarnings("unchecked")
  public void onReceive(Object message) throws Throwable {
    if(message instanceof LogLine) {
      LogLine logLine = (LogLine) message;
      apply(logLine, (T) RuntimeConfiguration.step(logLine.getCurrentStep()).getConfig());
    } else {
      unhandled(message);
    }
  }

  abstract protected void apply(LogLine logLine, T config);
}
