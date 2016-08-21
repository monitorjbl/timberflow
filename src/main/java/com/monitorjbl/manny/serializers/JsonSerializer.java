package com.monitorjbl.manny.serializers;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonSerializer {

  private static final ObjectMapper mapper = new ObjectMapper();

  static {
    mapper.setSerializationInclusion(Include.NON_NULL);
  }

  public static String serialize(Object obj) {
    try {
      return mapper.writeValueAsString(obj);
    } catch(JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
