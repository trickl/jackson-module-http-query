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

    @JsonProperty("paramC")
    @JsonSerialize(nullsUsing = NullSerializer.class)
    private String valueC;

    public MultiPropertyQuery(String valueA, String valueB, String valueC) {
      this.valueA = valueA;
      this.valueB = valueB;
      this.valueC = valueC;
    }
  }

  @Test
  public void testExcludedNullParamSerialization() throws JsonProcessingException {
    assertEquals("?paramB=valueB&paramC=valueC",
        objectMapper.writeValueAsString(
            new MultiPropertyQuery(null, "valueB", "valueC")));
  }

  @Test
  public void testIncludedNullAsEmptyParamSerialization() throws JsonProcessingException {
    assertEquals("?paramA=valueA&paramB&paramC=valueC",
        objectMapper.writeValueAsString(
            new MultiPropertyQuery("valueA", null, "valueC")));
  }

  @Test
  public void testIncludedNullAsNullParamSerialization() throws JsonProcessingException {
    assertEquals("?paramA=valueA&paramB=valueB&paramC=null",
        objectMapper.writeValueAsString(
            new MultiPropertyQuery("valueA", "valueB", null)));
  }
}
