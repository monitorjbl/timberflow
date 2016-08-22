package com.monitorjbl.timbersaw.inputs.file;

import com.monitorjbl.timbersaw.inputs.Input;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;

import java.io.File;

public class FileInput extends Input {
  private final String path;
  private Tailer tailer;

  public FileInput(String path) {
    this.path = path;
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
    }, 1, true);
  }
}
