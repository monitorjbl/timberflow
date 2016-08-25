package com.monitorjbl.timberflow.inputs.stdin;

import com.monitorjbl.timberflow.inputs.Input;
import com.monitorjbl.timberflow.inputs.MessageSender;
import com.monitorjbl.timberflow.plugin.Plugin;

import java.util.Scanner;

@Plugin(dslName = "stdin", configParser = StdinConfigParser.class)
public class StdinInput implements Input {
  @Override
  public void start(MessageSender sender) {
    Scanner sc = new Scanner(System.in);
    while(sc.hasNextLine()) {
      sender.sendMessage(sc.nextLine());
    }
  }
}
