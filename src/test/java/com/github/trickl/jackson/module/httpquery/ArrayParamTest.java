package com.github.trickl.jackson.module.httpquery;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.trickl.jackson.module.httpquery.annotations.HttpQuery;
import com.github.trickl.jackson.module.httpquery.annotations.HttpQueryDelimited;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class ArrayParamTest {

  private static ObjectMapper objectMapper;

  private static final String[] EXAMPLE_ARRAY 
      = new String[] {"first", "second", "third"};

  @BeforeAll
  public static void setup() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new HttpQueryModule());
  }

  @HttpQuery
  @NoArgsConstructor
  @EqualsAndHashCode
  @ToString
  private static class RegularArrayQuery {    
    @JsonProperty("paramA")
    private String[] values;

    public RegularArrayQuery(String[] values) {
      this.values = values;
    }
  }

  @HttpQuery
  @NoArgsConstructor
  @EqualsAndHashCode
  @ToString
  private static class DelimitedArrayQuery {    
    @JsonProperty("paramA")
    @HttpQueryDelimited
    private String[] values;

    public DelimitedArrayQuery(String[] values) {
      this.values = values;
    }
  }

  @HttpQuery
  @NoArgsConstructor
  @EqualsAndHashCode
  @ToString
  private static class DelimitedArrayNoEncodeQuery {    
    @JsonProperty("paramA")
    @HttpQueryDelimited(encodeDelimiter = false)
    private String[] values;

    public DelimitedArrayNoEncodeQuery(String[] values) {
      this.values = values;
    }
  }

  @HttpQuery
  @NoArgsConstructor
  @EqualsAndHashCode
  @ToString
  private static class CustomDelimiterArrayQuery {    
    @JsonProperty("paramA")
    @HttpQueryDelimited(delimiter = ";")
    private String[] values;

    public CustomDelimiterArrayQuery(String[] values) {
      this.values = values;
    }
  }

  @HttpQuery
  @NoArgsConstructor
  @EqualsAndHashCode
  @ToString
  private static class CustomDelimiterNoEncodeArrayQuery {    
    @JsonProperty("paramA")
    @HttpQueryDelimited(delimiter = ";", encodeDelimiter = false)
    private String[] values;

    public CustomDelimiterNoEncodeArrayQuery(String[] values) {
      this.values = values;
    }
  }

  @Test
  public void testRegularArraySerialization() throws JsonProcessingException {
    assertEquals("?paramA=first&paramA=second&paramA=third",
        objectMapper.writeValueAsString(
            new RegularArrayQuery(EXAMPLE_ARRAY)));
  }

  @Test
  public void testRegularArrayDeserialization() throws IOException {
    assertEquals(new RegularArrayQuery(EXAMPLE_ARRAY),
        objectMapper.readValue("\"?paramA=first&paramA=second&paramA=third\"",
        RegularArrayQuery.class));
  }

  @Test
  public void testDelimitedArraySerialization() throws JsonProcessingException {
    assertEquals("?paramA=first%2Csecond%2Cthird",
        objectMapper.writeValueAsString(
            new DelimitedArrayQuery(EXAMPLE_ARRAY)));
  }

  @Test
  public void testDelimitedArrayDeserialization() throws IOException {
    assertEquals(new DelimitedArrayQuery(EXAMPLE_ARRAY),
        objectMapper.readValue("\"?paramA=first%2Csecond%2Cthird\"",
        DelimitedArrayQuery.class));
  }

  @Test
  public void testDelimitedArrayNoEncodeSerialization() throws JsonProcessingException {
    assertEquals("?paramA=first,second,third",
        objectMapper.writeValueAsString(
            new DelimitedArrayNoEncodeQuery(EXAMPLE_ARRAY)));
  }

  @Test
  public void testDelimitedArrayNoEncodeDeserialization() throws IOException {
    assertEquals(new DelimitedArrayNoEncodeQuery(EXAMPLE_ARRAY),
        objectMapper.readValue("\"?paramA=first,second,third\"",
        DelimitedArrayNoEncodeQuery.class));
  }

  @Test
  public void testCustomDelimiterArraySerialization() throws JsonProcessingException {
    assertEquals("?paramA=first%3Bsecond%3Bthird",
        objectMapper.writeValueAsString(
            new CustomDelimiterArrayQuery(EXAMPLE_ARRAY)));
  }

  @Test
  public void testCustomDelimiterArrayDeserialization() throws IOException {
    assertEquals(new CustomDelimiterArrayQuery(EXAMPLE_ARRAY),
        objectMapper.readValue("\"?paramA=first%3Bsecond%3Bthird\"",
        CustomDelimiterArrayQuery.class));
  }

  @Test
  public void testCustomDelimiterNoEncodeArraySerialization() throws JsonProcessingException {
    assertEquals("?paramA=first;second;third",
        objectMapper.writeValueAsString(
            new CustomDelimiterNoEncodeArrayQuery(EXAMPLE_ARRAY)));
  }

  @Test
  public void testCustomDelimiterNoEncodeArrayDeserialization() throws IOException {
    assertEquals(new CustomDelimiterNoEncodeArrayQuery(EXAMPLE_ARRAY),
        objectMapper.readValue("\"?paramA=first;second;third\"",
        CustomDelimiterNoEncodeArrayQuery.class));
  }
}
