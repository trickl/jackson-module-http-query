package com.github.trickl.jackson.module.httpquery;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.trickl.jackson.module.httpquery.annotations.HttpQuery;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class SinglePropertyTest {

  private static ObjectMapper objectMapper;

  @BeforeAll
  public static void setup() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new HttpQueryModule());
  }

  @HttpQuery
  private class SinglePropertyQuery<T> {
    @JsonProperty("param")
    private T value;

    public SinglePropertyQuery(T value) {
      this.value = value;
    }
  }

  @Test
  public void testStringParamSerialization() throws JsonProcessingException {
    assertEquals("?param=value",
        objectMapper.writeValueAsString(new SinglePropertyQuery<>("value")));
  }

  @Test
  public void testIntegerParamSerialization() throws JsonProcessingException {
    assertEquals("?param=123",
        objectMapper.writeValueAsString(new SinglePropertyQuery<>(123)));
  }

  @Test
  public void testLongParamSerialization() throws JsonProcessingException {
    assertEquals("?param=123",
        objectMapper.writeValueAsString(new SinglePropertyQuery<>(123L)));
  }

  @Test
  public void testBooleanParamSerialization() throws JsonProcessingException {
    assertEquals("?param=true",
        objectMapper.writeValueAsString(new SinglePropertyQuery<>(true)));
  }
}
