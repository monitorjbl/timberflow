package com.monitorjbl.timberflow.dsl;

import com.monitorjbl.timberflow.api.Config;
import com.monitorjbl.timberflow.api.ConfigParser;
import com.monitorjbl.timberflow.api.Filter;
import com.monitorjbl.timberflow.api.Input;
import com.monitorjbl.timberflow.api.Output;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toSet;

public class CompilationContext {
  private static Set<Class> SUPPORTED_BLOCKS = newHashSet(Input.class, Filter.class, Output.class);
  private Map<String, Map<String, Class>> plugins = new HashMap<>();
  private Map<String, Map<String, ConfigParser>> configGenerators = new HashMap<>();

  public void addEntry(String name, Class actor, ConfigParser configParser) {
    getSupportedBlockNames(actor).forEach(block -> {
      if(!plugins.containsKey(block)) {
        plugins.put(block, new HashMap<>());
      }
      plugins.get(block).put(name, actor);

      if(!configGenerators.containsKey(block)) {
        configGenerators.put(block, new HashMap<>());
      }
      configGenerators.get(block).put(name, configParser);
    });
  }

  public Class getPluginClass(String block, String name) {
    assertExists(block, name);
    return plugins.get(block).get(name);
  }

  public Config generatePluginConfig(String block, String name, DSLPlugin plugin) {
    assertExists(block, name);
    ConfigParser generator = configGenerators.get(block).get(name);
    if(generator != null) {
      return generator.generateConfig(plugin);
    } else {
      return null;
    }
  }

  private void assertExists(String block, String name) {
    if(!plugins.containsKey(block) || !plugins.get(block).containsKey(name)) {
      throw new PluginNotFoundException(name);
    }
  }

  private Set<String> getSupportedBlockNames(Class cls) {
    Class parent = cls;
    Set<String> blocks = new HashSet<>();
    while(parent != Object.class) {
      blocks.addAll(stream(parent.getInterfaces())
          .filter(c -> SUPPORTED_BLOCKS.contains(c))
          .map(c -> c.getSimpleName().toLowerCase())
          .collect(toSet()));
      parent = cls.getSuperclass();
    }

    if(blocks.size() > 0) {
      return blocks;
    } else {
      throw new IllegalArgumentException("Class " + cls.getCanonicalName() + " does not implement any supported interfaces " + "(expected one of " +
          Input.class.getCanonicalName() + "," + Filter.class.getCanonicalName() + ", or " + Output.class.getCanonicalName() + ")");
    }
  }

  public static class PluginNotFoundException extends RuntimeException {
    private final String name;

    public PluginNotFoundException(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }
  }
}
