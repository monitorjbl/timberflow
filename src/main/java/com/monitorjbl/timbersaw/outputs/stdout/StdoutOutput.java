package com.monitorjbl.timbersaw.outputs.stdout;

import com.monitorjbl.timbersaw.domain.LogLine;
import com.monitorjbl.timbersaw.outputs.Output;
import com.monitorjbl.timbersaw.serializers.JsonSerializer;

import java.util.Map;

public class StdoutOutput extends Output<StdoutConfig> {
  @Override
  protected void apply(LogLine logLine, StdoutConfig config) {
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
