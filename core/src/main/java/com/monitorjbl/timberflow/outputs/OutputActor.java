package com.monitorjbl.timberflow.outputs;

import com.monitorjbl.timberflow.BaseActor;
import com.monitorjbl.timberflow.api.LogLine;
import com.monitorjbl.timberflow.api.Output;
import com.monitorjbl.timberflow.config.RuntimeConfiguration;
import com.monitorjbl.timberflow.domain.ActorConfig;
import com.monitorjbl.timberflow.reflection.ObjectCreator;

public class OutputActor extends BaseActor {

  private final Output output;

  public OutputActor(Class<? extends Output> outputClass, ActorConfig config, Object... constructorArgs) {
    super(config);
    this.output = ObjectCreator.newInstance(outputClass, constructorArgs);
  }

  @Override
  @SuppressWarnings("unchecked")
  public void onReceive(Object message) throws Throwable {
    if(message instanceof LogLine) {
      LogLine logLine = (LogLine) message;
      handleMessage(logLine);
      output.apply(logLine, RuntimeConfiguration.step(logLine).getConfig());
    } else {
      unhandled(message);
    }
  }

}
