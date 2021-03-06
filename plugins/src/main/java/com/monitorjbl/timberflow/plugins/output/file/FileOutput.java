package com.monitorjbl.timberflow.plugins.output.file;

import com.monitorjbl.timberflow.api.LogLine;
import com.monitorjbl.timberflow.api.Output;
import com.monitorjbl.timberflow.api.Plugin;
import com.monitorjbl.timberflow.plugins.serializers.JsonSerializer;

import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;

@Plugin(dslName = "file", configParser = FileOutputConfigParser.class)
public class FileOutput implements Output<FileOutputConfig> {
  private final FileWriter output;

  public FileOutput(FileOutputConfig config) {
    try {
      this.output = new FileWriter(config.getPath());
    } catch(IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public void apply(LogLine logLine, FileOutputConfig config) {
    try {
      output.write(serialize(config.getType(), logLine.getFields()) + "\n");
      output.flush();
    } catch(IOException e) {
      e.printStackTrace();
    }
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
