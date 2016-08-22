package com.monitorjbl.timbersaw;

import akka.actor.ActorSystem;
import akka.actor.Props;
import com.google.common.io.Resources;
import com.monitorjbl.timbersaw.config.RuntimeConfiguration;
import com.monitorjbl.timbersaw.domain.SingleStep;
import com.monitorjbl.timbersaw.dsl.CompilationContext;
import com.monitorjbl.timbersaw.dsl.TimberflowCompiler;
import com.monitorjbl.timbersaw.filters.drop.DropConfigParser;
import com.monitorjbl.timbersaw.filters.drop.DropFilter;
import com.monitorjbl.timbersaw.filters.grep.GrepConfigParser;
import com.monitorjbl.timbersaw.filters.grep.GrepFilter;
import com.monitorjbl.timbersaw.inputs.file.FileConfigParser;
import com.monitorjbl.timbersaw.inputs.file.FileInput;
import com.monitorjbl.timbersaw.inputs.stdin.StdinConfigParser;
import com.monitorjbl.timbersaw.inputs.stdin.StdinInput;
import com.monitorjbl.timbersaw.outputs.stdout.StdoutConfigParser;
import com.monitorjbl.timbersaw.outputs.stdout.StdoutOutput;

import java.nio.charset.Charset;
import java.util.List;

public class Timberflow {

  public static void main(String[] args) throws Exception {
    String test = Resources.toString(Resources.getResource("pipeline.conf"), Charset.defaultCharset());

    CompilationContext ctx = new CompilationContext();
    ctx.addEntry("stdin", StdinInput.class, new StdinConfigParser());
    ctx.addEntry("file", FileInput.class, new FileConfigParser());
    ctx.addEntry("grep", GrepFilter.class, new GrepConfigParser());
    ctx.addEntry("drop", DropFilter.class, new DropConfigParser());
    ctx.addEntry("stdout", StdoutOutput.class, new StdoutConfigParser());
    List<SingleStep> steps = new TimberflowCompiler(ctx).compile(test);
    RuntimeConfiguration.applyConfig(steps);

    ActorSystem system = ActorSystem.create("timberflow");
    system.actorOf(Props.create(StdinInput.class), StdinInput.class.getSimpleName());
    system.actorOf(Props.create(FileInput.class, "/tmp/test1"), FileInput.class.getSimpleName()+"-1");
    system.actorOf(Props.create(FileInput.class, "/tmp/test2"), FileInput.class.getSimpleName()+"-2");
    system.actorOf(Props.create(GrepFilter.class), GrepFilter.class.getSimpleName());
    system.actorOf(Props.create(DropFilter.class), DropFilter.class.getSimpleName());
    system.actorOf(Props.create(StdoutOutput.class), StdoutOutput.class.getSimpleName());

    System.out.println("Started!");
    while(true) {
      Thread.sleep(100);
    }
  }
}
