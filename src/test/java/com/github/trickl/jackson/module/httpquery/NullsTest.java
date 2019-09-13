package com.github.trickl.jackson.module.httpquery;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.annotation.JsonInclude;
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

public class NullsTest {

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
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("paramA")
    private String valueA;

    @JsonProperty("paramB")
    private Integer valueB;
  }

  @Test
  public void testExcludedNullParamSerialization() throws JsonProcessingException {
    assertEquals("?paramB=123", objectMapper.valueToTree(new MultiPropertyQuery(null, 123)).textValue());
  }

  @Test
  public void testIncludedNullAsEmptyParamSerialization() throws JsonProcessingException {
    assertEquals(
        "?paramA=valueA&paramB=null",
        objectMapper.valueToTree(new MultiPropertyQuery("valueA", null)).textValue());
  }

  @Test
  public void testExcludedNullParamDeserialization() throws IOException {
    assertEquals(
        new MultiPropertyQuery(null, 123),
        objectMapper.readValue("\"?paramB=123\"", MultiPropertyQuery.class));
  }

  @Test
  public void testIncludeNullAsEmptyParamDeserialization() throws IOException {
    assertEquals(
        new MultiPropertyQuery("valueA", null),
        objectMapper.readValue("\"?paramA=valueA&paramB=null\"", MultiPropertyQuery.class));
  }
}
