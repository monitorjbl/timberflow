package com.monitorjbl.timbersaw;

import akka.actor.ActorSystem;
import akka.actor.Props;
import com.google.common.io.Resources;
import com.monitorjbl.timbersaw.config.RuntimeConfiguration;
import com.monitorjbl.timbersaw.dsl.CompilationContext;
import com.monitorjbl.timbersaw.dsl.DSL;
import com.monitorjbl.timbersaw.dsl.DSLPlugin;
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

public class Timberflow {
  private static final Logger log = LoggerFactory.getLogger(Timberflow.class);

  private static DSL compile(String source) {
    CompilationContext ctx = new CompilationContext();
    //TODO: gather this from classpath scanning
    ctx.addEntry("stdin", StdinInput.class, new StdinConfigParser());
    ctx.addEntry("file", FileInput.class, new FileConfigParser());
    ctx.addEntry("grep", GrepFilter.class, new GrepConfigParser());
    ctx.addEntry("drop", DropFilter.class, new DropConfigParser());
    ctx.addEntry("stdout", StdoutOutput.class, new StdoutConfigParser());
    return new TimberflowCompiler(ctx).compile(source);
  }

  private static void startActor(ActorSystem system, DSLPlugin plugin) {
    List<Object> props = plugin.getConfig().getConstructorArgs();
    system.actorOf(Props.create(plugin.getCls(), props.toArray(new Object[props.size()])), plugin.getName());
  }

  public static void main(String[] args) throws Exception {
    String test = Resources.toString(Resources.getResource("pipeline.conf"), Charset.defaultCharset());

    log.debug("Loading configuration");
    long start = System.currentTimeMillis();
    DSL dsl = compile(test);
    RuntimeConfiguration.applyConfig(dsl.getSteps());

    log.debug("Starting filter actors");
    ActorSystem system = ActorSystem.create("timberflow");
    dsl.getFilters().getPlugins().forEach(filter -> startActor(system, filter));

    log.debug("Starting output actors");
    dsl.getOutputs().getPlugins().forEach(output -> startActor(system, output));

    log.debug("Starting input actors");
    dsl.getInputs().getPlugins().forEach(input -> startActor(system, input));

    log.info("Started in {}ms", (System.currentTimeMillis() - start));
    while(true) {
      Thread.sleep(100);
    }
  }
}
