package com.monitorjbl.timberflow.api;

import java.util.Collections;
import java.util.List;

public interface Config {
  default List<Object> getConstructorArgs(){
    return Collections.emptyList();
  }
}
