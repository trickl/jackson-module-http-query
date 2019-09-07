package com.github.trickl.jackson.module.httpquery;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.trickl.jackson.module.httpquery.annotations.HttpQuery;
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
  private class MultiPropertyQuery {    
    @JsonProperty("paramA")
    private String valueA;

    @JsonIgnore
    @JsonProperty("paramB")
    private String valueB;

    @JsonProperty("paramC")
    private int valueC;

    public MultiPropertyQuery(String valueA, String valueB, int valueC) {
      this.valueA = valueA;
      this.valueB = valueB;
      this.valueC = valueC;
    }
  }

  @Test
  public void testIgnoreParamSerialization() throws JsonProcessingException {
    assertEquals("?paramA=valueA&paramC=123",
        objectMapper.writeValueAsString(
            new MultiPropertyQuery("valueA", "valueB", 123)));
  }
}
