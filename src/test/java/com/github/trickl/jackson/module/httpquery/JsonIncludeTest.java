package com.github.trickl.jackson.module.httpquery;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import com.github.trickl.jackson.module.httpquery.annotations.HttpQuery;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class JsonIncludeTest {

  private static ObjectMapper objectMapper;

  @BeforeAll
  public static void setup() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new HttpQueryModule());
  }

  @HttpQuery
  private static class MultiPropertyQuery {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("paramA")
    private String valueA;

    @JsonProperty("paramB")
    private String valueB;

    public MultiPropertyQuery(String valueA, String valueB) {
      this.valueA = valueA;
      this.valueB = valueB;
    }
  }

  @Test
  public void testExcludedNullParamSerialization() throws JsonProcessingException {
    assertEquals("?paramB=valueB",
        objectMapper.writeValueAsString(
            new MultiPropertyQuery(null, "valueB")));
  }

  @Test
  public void testIncludedNullAsEmptyParamSerialization() throws JsonProcessingException {
    assertEquals("?paramA=valueA&paramB",
        objectMapper.writeValueAsString(
            new MultiPropertyQuery("valueA", null)));
  }
}
