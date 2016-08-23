package com.monitorjbl.timberflow.inputs.stdin;

import com.monitorjbl.timberflow.inputs.Input;
import com.monitorjbl.timberflow.plugin.Plugin;

import java.util.Scanner;

@Plugin(dslName = "stdin", configParser = StdinConfigParser.class)
public class StdinInput extends Input {
  @Override
  protected void start() {
    Scanner sc = new Scanner(System.in);
    while(sc.hasNextLine()) {
      sendMessage(sc.nextLine());
    }
  }
}
