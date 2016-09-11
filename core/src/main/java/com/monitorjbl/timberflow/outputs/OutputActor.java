package com.monitorjbl.timberflow.outputs;

import akka.actor.ActorSelection;
import com.monitorjbl.timberflow.BaseActor;
import com.monitorjbl.timberflow.RuntimeConfiguration;
import com.monitorjbl.timberflow.api.LogLine;
import com.monitorjbl.timberflow.api.Output;
import com.monitorjbl.timberflow.domain.ActorConfig;
import com.monitorjbl.timberflow.domain.LogLineImpl;
import com.monitorjbl.timberflow.domain.SingleStep;

public class OutputActor extends BaseActor {

  private final Output output;

  public OutputActor(Class<? extends Output> outputClass, ActorConfig config) {
    super(config);
    this.output = instiatiatePlugin(outputClass, config);
  }

  @Override
  @SuppressWarnings("unchecked")
  public void onReceive(Object message) throws Throwable {
    if(message instanceof LogLine) {
      LogLine logLine = (LogLine) message;
      handleMessage(logLine.getFields());
      output.apply(logLine, RuntimeConfiguration.step(logLine).getConfig());

      SingleStep next = RuntimeConfiguration.nextStep(logLine);
      if(next != null) {
        ActorSelection nextActor = context().actorSelection("../" + next.getName());
        nextActor.tell(new LogLineImpl(next.getNumber(), logLine.getFields()), self());
      }
    } else {
      unhandled(message);
    }
  }

}
