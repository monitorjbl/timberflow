package com.monitorjbl.timberflow;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.RoundRobinGroup;
import com.monitorjbl.timberflow.api.Plugin;
import com.monitorjbl.timberflow.config.RuntimeConfiguration;
import com.monitorjbl.timberflow.dsl.CompilationContext;
import com.monitorjbl.timberflow.dsl.CompilationContext.PluginNotFoundException;
import com.monitorjbl.timberflow.dsl.DSL;
import com.monitorjbl.timberflow.dsl.DSLPlugin;
import com.monitorjbl.timberflow.dsl.TimberflowCompiler;
import com.monitorjbl.timberflow.filters.FilterActor;
import com.monitorjbl.timberflow.inputs.InputActor;
import com.monitorjbl.timberflow.monitor.MonitorActor;
import com.monitorjbl.timberflow.outputs.OutputActor;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.monitorjbl.timberflow.utils.ThreadUtils.sleep;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class Timberflow {
  private static final Logger log = LoggerFactory.getLogger(Timberflow.class);
  private static final String ANSI_RESET = "\u001B[0m";
  private static final String ANSI_GREEN = "\u001B[32m";

  @Option(name = "--config", usage = "Path to the config file", required = true)
  private File configFile = new File(classLocation() + "../conf/");
  @Option(name = "--plugins", usage = "Path to the plugins directory (defaults to ${TIMBERFLOW_HOME}/plugins)")
  private File pluginsDir = new File(classLocation() + "/../plugins");

  private Map<Class, Plugin> pluginInfo = new HashMap<>();
  private Map<Class, List<ActorRef>> actors = new HashMap<>();
  private Map<Class, ActorRef> routers = new HashMap<>();

  private void createRouters(ActorSystem system) {
    actors.entrySet().stream()
        .collect(toMap(
            e -> e.getKey(),
            e -> e.getValue().stream()
                .map(r -> r.path().toString())
                .collect(toList())))
        .forEach((key, value) -> routers.put(key, system.actorOf(new RoundRobinGroup(value).props(), key.getCanonicalName())));
  }

  private DSL compile(String source, List<PluginJar> pluginJars) {
    CompilationContext ctx = new CompilationContext();
    pluginJars.forEach(jar -> {
      Reflections reflections = new Reflections(jar.jar, jar.classLoader);
      reflections.getTypesAnnotatedWith(Plugin.class).forEach(t -> addPlugin(ctx, t));
    });
    return new TimberflowCompiler(ctx).compile(source);
  }

  private void addPlugin(CompilationContext ctx, Class t) {
    try {
      Plugin plugin = (Plugin) t.getAnnotation(Plugin.class);
      ctx.addEntry(plugin.dslName(), t, plugin.configParser().newInstance());
      pluginInfo.put(t, plugin);
    } catch(InstantiationException | IllegalAccessException e) {
      throw new IllegalArgumentException("Could not start plugin " + t, e);
    }
  }

  private void startActor(ActorSystem system, Class baseActor, DSLPlugin plugin) {
    List<Object> props = plugin.getConfig().getConstructorArgs();
    if(!actors.containsKey(plugin.getCls())) {
      actors.put(plugin.getCls(), new ArrayList<>());
    }
    for(int i = 0; i < plugin.getConfig().getInstances(); i++) {
      String suffix = i > 0 ? "-" + i : "";
      actors.get(plugin.getCls()).add(system.actorOf(
          Props.create(baseActor, plugin.getCls(), props.toArray(new Object[props.size()])).withMailbox("bounded-mailbox"),
          plugin.getName() + suffix));
    }
  }

  private List<PluginJar> loadPlugins() {
    log.debug("Scanning {} for plugins", pluginsDir.getAbsolutePath());
    File[] jars = pluginsDir.listFiles();
    return stream(jars == null ? new File[0] : jars)
        .filter(f -> f.getName().endsWith(".jar"))
        .map(jar -> new PluginJar(toUrl(jar), new URLClassLoader(new URL[]{toUrl(jar)}, this.getClass().getClassLoader())))
        .collect(toList());
  }

  private void start() {
    log.debug("Loading configuration");
    long start = System.currentTimeMillis();
    DSL dsl = compile(readFile(configFile.getAbsolutePath()), loadPlugins());
    RuntimeConfiguration.applyConfig(dsl.getSteps());

    log.debug("Starting monitor actor");
    ActorSystem system = ActorSystem.create("timberflow");
    system.actorOf(Props.create(MonitorActor.class), "monitor");

    log.debug("Starting filter actors");
    dsl.filterPlugins().forEach(filter -> startActor(system, FilterActor.class, filter));

    log.debug("Starting output actors");
    dsl.outputPlugins().forEach(output -> startActor(system, OutputActor.class, output));

    log.debug("Starting input actors");
    dsl.inputPlugins().forEach(input -> startActor(system, InputActor.class, input));

    log.debug("Starting routers");
    createRouters(system);

    System.out.println(String.format("%sStarted up in%s %dms", ANSI_GREEN, ANSI_RESET, (System.currentTimeMillis() - start)));
  }

  private static String classLocation() {
    Class<?> c = Timberflow.class;
    return c.getResource(c.getSimpleName() + ".class").getPath()
        .replace(c.getSimpleName() + ".class", "")
        .replaceAll("/[^/]+\\.jar.*", "")
        .replaceAll("^file:", "");
  }

  private static URL toUrl(File f) {
    try {
      return f.toURI().toURL();
    } catch(MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  private static String readFile(String path) {
    try {
      byte[] content = Files.readAllBytes(Paths.get(path));
      return new String(content, Charset.defaultCharset());
    } catch(IOException e) {
      throw new UncheckedIOException(e);
    }
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
    } catch(PluginNotFoundException e) {
      System.err.println("Plugin not found: " + e.getName());
      System.exit(1);
    }

    while(true) {
      sleep(100);
    }
  }

  private static class PluginJar {
    private final URL jar;
    private final ClassLoader classLoader;

    public PluginJar(URL jar, ClassLoader classLoader) {
      this.jar = jar;
      this.classLoader = classLoader;
    }
  }
}
