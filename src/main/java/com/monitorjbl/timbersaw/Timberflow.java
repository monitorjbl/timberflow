package com.monitorjbl.timbersaw;

import akka.actor.ActorSystem;
import akka.actor.Props;
import com.monitorjbl.timbersaw.config.RuntimeConfiguration;
import com.monitorjbl.timbersaw.dsl.CompilationContext;
import com.monitorjbl.timbersaw.dsl.DSL;
import com.monitorjbl.timbersaw.dsl.DSLPlugin;
import com.monitorjbl.timbersaw.dsl.TimberflowCompiler;
import com.monitorjbl.timbersaw.plugin.Plugin;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Timberflow {
  private static final Logger log = LoggerFactory.getLogger(Timberflow.class);
  private static final String ANSI_RESET = "\u001B[0m";
  private static final String ANSI_GREEN = "\u001B[32m";
  private static final String ANSI_YELLOW = "\u001B[33m";
  private static final String ANSI_CYAN = "\u001B[36m";
  private static final String ANSI_WHITE = "\u001B[37m";

  @Option(name = "--config", required = true, usage = "Path to the config file")
  private File configFile;

  private DSL compile(String source) {
    CompilationContext ctx = new CompilationContext();
    Reflections reflections = new Reflections();
    reflections.getTypesAnnotatedWith(Plugin.class).forEach(t -> addPlugin(ctx, t));
    return new TimberflowCompiler(ctx).compile(source);
  }

  private void addPlugin(CompilationContext ctx, Class t) {
    try {
      Plugin plugin = (Plugin) t.getAnnotation(Plugin.class);
      ctx.addEntry(plugin.dslName(), t, plugin.configParser().newInstance());
    } catch(InstantiationException | IllegalAccessException e) {
      throw new IllegalArgumentException("Could not start plugin " + t, e);
    }
  }

  private void startActor(ActorSystem system, DSLPlugin plugin) {
    List<Object> props = plugin.getConfig().getConstructorArgs();
    system.actorOf(Props.create(plugin.getCls(), props.toArray(new Object[props.size()])), plugin.getName());
  }

  static String readFile(String path) {
    try {
      byte[] content = Files.readAllBytes(Paths.get(path));
      return new String(content, Charset.defaultCharset());
    } catch(IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private void start() {
    log.debug("Loading configuration");
    long start = System.currentTimeMillis();
    DSL dsl = compile(readFile(configFile.getAbsolutePath()));
    RuntimeConfiguration.applyConfig(dsl.getSteps());

    log.debug("Starting filter actors");
    ActorSystem system = ActorSystem.create("timberflow");
    dsl.getFilters().getPlugins().forEach(filter -> startActor(system, filter));

    log.debug("Starting output actors");
    dsl.getOutputs().getPlugins().forEach(output -> startActor(system, output));

    log.debug("Starting input actors");
    dsl.getInputs().getPlugins().forEach(input -> startActor(system, input));

    System.out.println(String.format("%sStarted up in%s %dms", ANSI_GREEN, ANSI_RESET, (System.currentTimeMillis() - start)));
  }

  public static void main(String[] args) throws Exception {
    Timberflow timberflow = new Timberflow();
    CmdLineParser parser = new CmdLineParser(timberflow);

    try {
      parser.parseArgument(args);
      timberflow.start();
    } catch(CmdLineException e) {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      parser.printUsage(bos);
      System.err.println(e.getMessage());
      System.err.println("usage: java -jar timberflow.jar [options]");
      System.err.println("\t" + new String(bos.toByteArray()).replaceAll("\n", "\t\n"));
      System.err.flush();
      System.exit(1);
    }

    while(true) {
      Thread.sleep(100);
    }
  }
}
