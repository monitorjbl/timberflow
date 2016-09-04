package com.monitorjbl.timberflow.plugins.input.file;

import com.monitorjbl.timberflow.api.ConfigParser;
import com.monitorjbl.timberflow.api.PluginContent;

import java.io.File;

public class FileInputConfigParser implements ConfigParser<FileInputConfig> {
  @Override
  public FileInputConfig generateConfig(PluginContent dslPlugin) {
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

    return new FileInputConfig(path, fromBeginning);
  }
}
