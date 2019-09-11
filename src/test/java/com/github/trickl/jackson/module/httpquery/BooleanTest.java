package com.github.trickl.jackson.module.httpquery;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.trickl.jackson.module.httpquery.annotations.HttpQuery;
import com.github.trickl.jackson.module.httpquery.annotations.HttpQueryNoValue;
import java.io.IOException;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class BooleanTest {

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
  private static class PrimitivePropertyQuery {
    @JsonProperty("paramA")
    @HttpQueryNoValue
    private boolean valueA;

    @JsonProperty("paramB")
    private boolean valueB;
  }

  @HttpQuery
  @NoArgsConstructor
  @AllArgsConstructor
  @EqualsAndHashCode
  private static class BoxedPropertyQuery {
    @JsonProperty("paramA")
    @HttpQueryNoValue
    private Boolean valueA;

    @JsonProperty("paramB")
    private Boolean valueB;
  }

  @Test
  public void testFalsePrimitivesSerialization() throws JsonProcessingException {
    assertEquals(
        "?paramB=false", objectMapper.writeValueAsString(new PrimitivePropertyQuery(false, false)));
  }

  @Test
  public void testTruePrimitiveSerialization() throws JsonProcessingException {
    assertEquals(
        "?paramA&paramB=true",
        objectMapper.writeValueAsString(new PrimitivePropertyQuery(true, true)));
  }

  @Test
  public void testFalsePrimitivesDeserialization() throws IOException {
    assertEquals(
        new PrimitivePropertyQuery(false, false),
        objectMapper.readValue("\"?paramB=false\"", PrimitivePropertyQuery.class));
  }

  @Test
  public void testTruePrimitiveDeserialization() throws IOException {
    assertEquals(
        new PrimitivePropertyQuery(true, true),
        objectMapper.readValue("\"?paramA&paramB=true\"", PrimitivePropertyQuery.class));
  }

  @Test
  public void testFalseBoxedSerialization() throws JsonProcessingException {
    assertEquals(
        "?paramB=false", objectMapper.writeValueAsString(new BoxedPropertyQuery(false, false)));
  }

  @Test
  public void testTrueBoxedSerialization() throws JsonProcessingException {
    assertEquals(
        "?paramA&paramB=true", objectMapper.writeValueAsString(new BoxedPropertyQuery(true, true)));
  }

  @Test
  public void testNullBoxedSerialization() throws JsonProcessingException {
    assertEquals(
        "?paramB=null", objectMapper.writeValueAsString(new BoxedPropertyQuery(null, null)));
  }

  @Test
  public void testFalseBoxedDeserialization() throws IOException {
    assertEquals(
        new BoxedPropertyQuery(null, false),
        objectMapper.readValue("\"?paramB=false\"", BoxedPropertyQuery.class));
  }

  @Test
  public void testTrueBoxedDeserialization() throws IOException {
    assertEquals(
        new BoxedPropertyQuery(true, true),
        objectMapper.readValue("\"?paramA&paramB=true\"", BoxedPropertyQuery.class));
  }

  @Test
  public void testNullBoxedDeserialization() throws IOException {
    assertEquals(
        new BoxedPropertyQuery(null, null),
        objectMapper.readValue("\"?paramB=null\"", BoxedPropertyQuery.class));
  }
}
