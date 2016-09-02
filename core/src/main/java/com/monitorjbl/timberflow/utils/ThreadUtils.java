package com.monitorjbl.timberflow.utils;

public class ThreadUtils {
  public static void sleep(long milli) {
    try {
      Thread.sleep(milli);
    } catch(InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
