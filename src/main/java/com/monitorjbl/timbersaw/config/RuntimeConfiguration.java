package com.monitorjbl.timbersaw.config;

import com.monitorjbl.timbersaw.domain.SingleStep;
import com.monitorjbl.timbersaw.dsl.DSLPlugin;
import com.monitorjbl.timbersaw.dsl.KeyValue;
import com.monitorjbl.timbersaw.filters.drop.DropFilter;
import com.monitorjbl.timbersaw.filters.grep.GrepConfigParser;
import com.monitorjbl.timbersaw.filters.grep.GrepFilter;
import com.monitorjbl.timbersaw.outputs.stdout.StdoutOutput;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class RuntimeConfiguration {
  private static List<SingleStep> steps = new ArrayList<>();
  private static GrepConfigParser grepGenerator = new GrepConfigParser();

  public static void applyConfig(List<SingleStep> steps){
    RuntimeConfiguration.steps = steps;
  }

  public static void applyConfig() {
    DSLPlugin plugin = new DSLPlugin("grep");
    plugin.getMultiProperties().put("matches", newArrayList(new KeyValue(
        "message",
        "%{DATA:timestamplocal}\\|%{NUMBER:duration}\\|%{WORD:requesttype}\\|%{IP:clientip}\\|%{DATA:username}\\|%{WORD:method}\\|%{PATH:resource}\\|%{DATA:protocol}\\|%{NUMBER:statuscode}\\|%{NUMBER:bytes}"
    )));
    steps = newArrayList(
        new SingleStep<>(0, GrepFilter.class.getSimpleName(), grepGenerator.generateConfig(plugin)),
        new SingleStep<>(1, DropFilter.class.getSimpleName(), newArrayList("message")),
        new SingleStep<>(2, StdoutOutput.class.getSimpleName(), "json"));
  }

  public static <E> SingleStep<E> step(Integer step) {
    return steps.get(step);
  }

}
