package com.monitorjbl.timbersaw.inputs.file;

import com.monitorjbl.timbersaw.inputs.Input;
import com.monitorjbl.timbersaw.plugin.Plugin;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;

import java.io.File;

@Plugin(dslName = "file", configParser = FileConfigParser.class)
public class FileInput extends Input {
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
  protected void start() {
    tailer = Tailer.create(new File(path), new TailerListenerAdapter() {
      public void handle(String line) {
        sendMessage(line);
      }
    }, 1, !fromBeginning);
  }
}
