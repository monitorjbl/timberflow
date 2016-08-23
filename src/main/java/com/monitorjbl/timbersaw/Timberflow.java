package com.monitorjbl.timbersaw;

import akka.actor.ActorSystem;
import akka.actor.Props;
import com.google.common.io.Resources;
import com.monitorjbl.timbersaw.config.RuntimeConfiguration;
import com.monitorjbl.timbersaw.dsl.CompilationContext;
import com.monitorjbl.timbersaw.dsl.DSL;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Timberflow {
  private static final Logger log = LoggerFactory.getLogger(Timberflow.class);

  public static void main(String[] args) throws Exception {
    String test = Resources.toString(Resources.getResource("pipeline.conf"), Charset.defaultCharset());

    log.debug("Loading configuration");
    CompilationContext ctx = new CompilationContext();
    ctx.addEntry("stdin", StdinInput.class, new StdinConfigParser());
    ctx.addEntry("file", FileInput.class, new FileConfigParser());
    ctx.addEntry("grep", GrepFilter.class, new GrepConfigParser());
    ctx.addEntry("drop", DropFilter.class, new DropConfigParser());
    ctx.addEntry("stdout", StdoutOutput.class, new StdoutConfigParser());
    DSL dsl = new TimberflowCompiler(ctx).compile(test);
    RuntimeConfiguration.applyConfig(dsl.getSteps());
    log.debug("Loaded configuration");

    log.debug("Starting actors");
    ActorSystem system = ActorSystem.create("timberflow");
    AtomicInteger actorCount = new AtomicInteger(0);
    dsl.getInputs().getPlugins().forEach(input -> {
      List<Object> props = input.getConfig().getConstructorArgs();
      system.actorOf(Props.create(input.getCls(), props.toArray(new Object[props.size()])), input.getName() + "-" + actorCount.getAndIncrement());
    });
    system.actorOf(Props.create(GrepFilter.class), GrepFilter.class.getSimpleName());
    system.actorOf(Props.create(DropFilter.class), DropFilter.class.getSimpleName());
    system.actorOf(Props.create(StdoutOutput.class), StdoutOutput.class.getSimpleName());
    log.debug("Started actors");

    log.info("Started");
    while(true) {
      Thread.sleep(100);
    }
  }
}
