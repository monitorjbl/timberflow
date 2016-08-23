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
import com.monitorjbl.timbersaw.outputs.stdout.StdoutConfigParser;
import com.monitorjbl.timbersaw.outputs.stdout.StdoutOutput;
import com.monitorjbl.timbersaw.plugin.Plugin;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.List;

public class Timberflow {
  private static final Logger log = LoggerFactory.getLogger(Timberflow.class);

  private static DSL compile(String source) {
    CompilationContext ctx = new CompilationContext();
    Reflections reflections = new Reflections();
    reflections.getTypesAnnotatedWith(Plugin.class).forEach(t -> addPlugin(ctx, t));
    return new TimberflowCompiler(ctx).compile(source);
  }

  private static void addPlugin(CompilationContext ctx, Class t) {
    try {
      Plugin plugin = (Plugin) t.getAnnotation(Plugin.class);
      ctx.addEntry(plugin.dslName(), t, plugin.configParser().newInstance());
    } catch(InstantiationException | IllegalAccessException e) {
      throw new IllegalArgumentException("Could not start plugin " + t, e);
    }
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

    log.info("Started up in {}ms", (System.currentTimeMillis() - start));
    while(true) {
      Thread.sleep(100);
    }
  }
}
