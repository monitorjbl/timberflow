package com.monitorjbl.timbersaw;

import akka.actor.ActorSystem;
import akka.actor.Props;
import com.monitorjbl.timbersaw.config.Configuration;
import com.monitorjbl.timbersaw.filters.DropFilter;
import com.monitorjbl.timbersaw.filters.GrepFilter;
import com.monitorjbl.timbersaw.inputs.FileInput;
import com.monitorjbl.timbersaw.inputs.StdinInput;
import com.monitorjbl.timbersaw.outputs.StdoutOutput;

public class Manny {

  public static void main(String[] args) throws InterruptedException {
    ActorSystem system = ActorSystem.create("MySystem");

    system.actorOf(Props.create(StdinInput.class), StdinInput.class.getSimpleName());
    system.actorOf(Props.create(FileInput.class, "/tmp/test1"), FileInput.class.getSimpleName()+"-1");
    system.actorOf(Props.create(FileInput.class, "/tmp/test2"), FileInput.class.getSimpleName()+"-2");

    system.actorOf(Props.create(GrepFilter.class), GrepFilter.class.getSimpleName());
    system.actorOf(Props.create(DropFilter.class), DropFilter.class.getSimpleName());

    system.actorOf(Props.create(StdoutOutput.class), StdoutOutput.class.getSimpleName());

    Configuration.applyConfig();

    while(true) {
      Thread.sleep(100);
    }
  }
}
