package com.monitorjbl.timberflow.plugins.input.file;

import com.monitorjbl.timberflow.api.Input;
import com.monitorjbl.timberflow.api.MessageSender;
import com.monitorjbl.timberflow.api.Plugin;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;

import java.io.File;

@Plugin(dslName = "file", configParser = FileInputConfigParser.class)
public class FileInput implements Input {
  private final String path;
  private final boolean fromBeginning;
  private Tailer tailer;

  public FileInput(String path, boolean fromBeginning) {
    this.path = path;
    this.fromBeginning = fromBeginning;
  }

  @Override
  public void stop() {
    tailer.stop();
  }

  @Override
  public void start(MessageSender sender) {
    tailer = Tailer.create(new File(path), new TailerListenerAdapter() {
      public void handle(String line) {
        sender.sendMessage(line);
      }
    }, 1, !fromBeginning);
  }
}
