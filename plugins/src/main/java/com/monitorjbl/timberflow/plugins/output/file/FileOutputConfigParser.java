package com.monitorjbl.timberflow.plugins.output.file;

import com.monitorjbl.timberflow.api.ConfigParser;
import com.monitorjbl.timberflow.api.PluginContent;
import com.monitorjbl.timberflow.api.PluginContent.KeyValue;

import java.io.File;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class FileOutputConfigParser implements ConfigParser<FileOutputConfig> {
  @Override
  public FileOutputConfig generateConfig(PluginContent dslPlugin) {
    Map<String, String> addFields = null;
    if(dslPlugin.getMultiProperties().containsKey("add_fields")) {
      addFields = dslPlugin.getMultiProperties().get("add_fields").stream().collect(toMap(KeyValue::getKey, KeyValue::getValue));
    }

    String path = (String) dslPlugin.getSingleProperties().get("path");
    if(path == null) {
      throw new IllegalStateException("path is required for file{} plugins");
    }

    String type = (String) dslPlugin.getSingleProperties().get("type");
    return new FileOutputConfig(path, type == null ? "json" : type, addFields);
  }
}
