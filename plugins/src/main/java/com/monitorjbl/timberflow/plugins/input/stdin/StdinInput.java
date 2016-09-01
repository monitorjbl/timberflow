package com.monitorjbl.timberflow.plugins.input.stdin;

import com.monitorjbl.timberflow.api.Input;
import com.monitorjbl.timberflow.api.MessageSender;
import com.monitorjbl.timberflow.api.Plugin;

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
