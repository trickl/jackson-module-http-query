package com.github.trickl.jackson.module.httpquery;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.trickl.jackson.module.httpquery.annotations.HttpQuery;
import java.io.IOException;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class JsonIgnoreTest {

  private static ObjectMapper objectMapper;

  @BeforeAll
  public static void setup() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new HttpQueryModule());
  }

  @HttpQuery
  @NoArgsConstructor
  @AllArgsConstructor
  @EqualsAndHashCode
  private static class MultiPropertyQuery {
    @JsonProperty("paramA")
    private String valueA;

    @JsonIgnore
    @JsonProperty("paramB")
    private String valueB;

    @JsonProperty("paramC")
    private int valueC;
  }

  @Test
  public void testIgnoreParamSerialization() throws JsonProcessingException {
    assertEquals(
        "?paramA=valueA&paramC=123",
        objectMapper.valueToTree(new MultiPropertyQuery("valueA", "valueB", 123)).textValue());
  }

  @Test
  public void testIgnoreParamDeserialization() throws IOException {
    assertEquals(
        new MultiPropertyQuery("valueA", null, 123),
        objectMapper.readValue("\"?paramA=valueA&paramC=123\"", MultiPropertyQuery.class));
  }
}
