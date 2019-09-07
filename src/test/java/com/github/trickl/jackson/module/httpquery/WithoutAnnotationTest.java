package com.github.trickl.jackson.module.httpquery;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class WithoutAnnotationTest {

  private static ObjectMapper objectMapper;

  @BeforeAll
  public static void setup() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new HttpQueryModule());
  }

  private class SinglePropertyQuery<T> {
    @JsonProperty("param")
    private T value;

    public SinglePropertyQuery(T value) {
      this.value = value;
    }
  } 

  @Test
  public void testSerializesAsExpected() throws JsonProcessingException {
    assertEquals("{\"param\":\"value\"}",
        objectMapper.writeValueAsString(
            new SinglePropertyQuery<>("value")));
  }
}
