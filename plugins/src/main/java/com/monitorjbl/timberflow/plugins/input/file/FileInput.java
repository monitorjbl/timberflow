package com.monitorjbl.timberflow.plugins.input.file;

import com.monitorjbl.timberflow.api.Input;
import com.monitorjbl.timberflow.api.MessageSender;
import com.monitorjbl.timberflow.api.Plugin;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;

import java.io.File;

@Plugin(dslName = "file", configParser = FileInputConfigParser.class)
public class FileInput implements Input {
  private final FileInputConfig config;
  private Tailer tailer;

  public FileInput(FileInputConfig config) {
    this.config = config;
  }

  @Override
  public void stop() {
    tailer.stop();
  }

  @Override
  public void start(MessageSender sender) {
    tailer = Tailer.create(new File(config.getPath()), new TailerListenerAdapter() {
      public void handle(String line) {
        sender.sendMessage(line);
      }
    }, 1, !config.isFromBeginning());
  }
}
