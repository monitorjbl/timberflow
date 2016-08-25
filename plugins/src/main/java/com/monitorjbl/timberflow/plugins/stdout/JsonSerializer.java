package com.monitorjbl.timberflow.plugins.stdout;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonSerializer {

  private static final ObjectMapper mapper = new ObjectMapper();

  static {
    mapper.setSerializationInclusion(Include.NON_NULL);
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
  }

  public static String serialize(Object obj) {
    try {
      return mapper.writeValueAsString(obj);
    } catch(JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
