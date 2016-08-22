package com.monitorjbl.timbersaw.inputs.stdin;

import com.monitorjbl.timbersaw.inputs.Input;

import java.util.Scanner;

public class StdinInput extends Input {
  @Override
  protected void start() {
    Scanner sc = new Scanner(System.in);
    while(sc.hasNextLine()) {
      sendMessage(sc.nextLine());
    }
  }
}
