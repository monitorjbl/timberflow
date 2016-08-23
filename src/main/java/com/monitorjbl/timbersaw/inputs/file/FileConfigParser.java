package com.monitorjbl.timbersaw.inputs.file;

import com.monitorjbl.timbersaw.config.ConfigParser;
import com.monitorjbl.timbersaw.dsl.DSLPlugin;
import com.monitorjbl.timbersaw.dsl.KeyValue;

import java.io.File;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class FileConfigParser implements ConfigParser<FileConfig> {
  @Override
  public FileConfig generateConfig(DSLPlugin dslPlugin) {
    Map<String, String> addFields = null;
    if(dslPlugin.getMultiProperties().containsKey("add_fields")) {
      addFields = dslPlugin.getMultiProperties().get("add_fields").stream().collect(toMap(KeyValue::getKey, KeyValue::getValue));
    }

    String path = (String) dslPlugin.getSingleProperties().get("path");
    if(path == null) {
      throw new IllegalStateException("path is required for file{} plugins");
    }
    if(!new File(path).exists()) {
      throw new IllegalStateException("path must exist for file{} plugins ('" + path + "' not found)");
    }

    boolean fromBeginning = false;
    if(dslPlugin.getSingleProperties().containsKey("from_beginning")) {
      fromBeginning = (boolean) dslPlugin.getSingleProperties().get("from_beginning");
    }

    return new FileConfig(path, fromBeginning, addFields);
  }
}
