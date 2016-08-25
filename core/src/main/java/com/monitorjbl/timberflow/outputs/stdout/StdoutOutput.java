package com.monitorjbl.timberflow.outputs.stdout;

import com.monitorjbl.timberflow.api.LogLine;
import com.monitorjbl.timberflow.api.Output;
import com.monitorjbl.timberflow.api.Plugin;
import com.monitorjbl.timberflow.serializers.JsonSerializer;

import java.util.Map;

@Plugin(dslName = "stdout", configParser = StdoutConfigParser.class)
public class StdoutOutput implements Output<StdoutConfig> {
  @Override
  public void apply(LogLine logLine, StdoutConfig config) {
    System.out.println(serialize(config.getType(), logLine.getFields()));
    System.out.flush();
  }

  private static String serialize(String type, Map<String, String> fields) {
    switch(type) {
      case "json":
        return JsonSerializer.serialize(fields);
      default:
        return "{}";
    }
  }
}
