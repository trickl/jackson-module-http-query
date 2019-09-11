package com.github.trickl.jackson.module.httpquery;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.github.trickl.jackson.module.httpquery.annotations.HttpQuery;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class JsonPropertyTest {

  private static ObjectMapper objectMapper;

  @BeforeAll
  public static void setup() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new HttpQueryModule());
  }

  @AllArgsConstructor
  private static class ObjectProperty {
    private String valueA;

    private String valueB;
  }

  @HttpQuery
  @AllArgsConstructor
  private static class JsonPropertyQuery {
    @JsonProperty("param")
    private ObjectProperty value;
  }

  @Test
  public void testNestedParamSerialization() throws JsonProcessingException {
    // Not supported
    assertThrows(
        InvalidDefinitionException.class,
        () -> objectMapper.writeValueAsString(new ObjectProperty("valueA", "valueB")));
  }
}
