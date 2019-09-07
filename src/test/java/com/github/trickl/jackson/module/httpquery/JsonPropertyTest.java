package com.github.trickl.jackson.module.httpquery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.github.trickl.jackson.module.httpquery.annotations.HttpQuery;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class JsonPropertyTest {

  private static ObjectMapper objectMapper;

  @BeforeAll
  public static void setup() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new HttpQueryModule());
  }

  private class ObjectProperty {    
    private String valueA;

    private String valueB;

    public ObjectProperty(String valueA, String valueB) {
      this.valueA = valueA;
      this.valueB = valueB;
    }
  }

  @HttpQuery
  private class JsonPropertyQuery {
    @JsonProperty("param")
    private ObjectProperty value;

    public JsonPropertyQuery(ObjectProperty value) {
      this.value = value;
    }
  }

  @Test
  public void testNestedParamSerialization() throws JsonProcessingException {
    // Not supported
    assertThrows(InvalidDefinitionException.class, () ->
        objectMapper.writeValueAsString(new ObjectProperty("valueA", "valueB")));
  }
}
