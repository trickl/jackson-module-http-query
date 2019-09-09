package com.github.trickl.jackson.module.httpquery;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import com.github.trickl.jackson.module.httpquery.annotations.HttpQuery;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

public class JsonNullsTest {

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
    assertEquals("?paramB=123",
        objectMapper.writeValueAsString(
            new MultiPropertyQuery(null, 123)));
  }

  @Test
  public void testIncludedNullAsEmptyParamSerialization() throws JsonProcessingException {
    assertEquals("?paramA=valueA&paramB=null",
        objectMapper.writeValueAsString(
            new MultiPropertyQuery("valueA", null)));
  }

  @Test
  public void testExcludedNullParamDeserialization() throws IOException {
    assertEquals(new MultiPropertyQuery(null, 123),
        objectMapper.readValue("\"?paramB=123\"",
        MultiPropertyQuery.class));
  }

  @Test
  public void testIncludeNullAsEmptyParamDeserialization() throws IOException {
    assertEquals(new MultiPropertyQuery("valueA", null),
        objectMapper.readValue("\"?paramA=valueA&paramB=null\"",
        MultiPropertyQuery.class));
  }
}
