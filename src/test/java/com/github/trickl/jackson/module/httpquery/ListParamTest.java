package com.github.trickl.jackson.module.httpquery;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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

public class ListParamTest {

  private static ObjectMapper objectMapper;

  private static final List<String> EXAMPLE_LIST 
      = Arrays.asList("first", "second", "third");

  @BeforeAll
  public static void setup() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new HttpQueryModule());
  }

  @HttpQuery
  private static class RegularListQuery {    
    @JsonProperty("paramA")
    private List<String> values;

    public RegularListQuery(List<String> values) {
      this.values = values;
    }
  }

  @HttpQuery
  private static class DelimitedListQuery {    
    @JsonProperty("paramA")
    @HttpQueryDelimited
    private List<String> values;

    public DelimitedListQuery(List<String> values) {
      this.values = values;
    }
  }

  @HttpQuery
  private static class DelimitedListNoEncodeQuery {    
    @JsonProperty("paramA")
    @HttpQueryDelimited(encodeDelimiter = false)
    private List<String> values;

    public DelimitedListNoEncodeQuery(List<String> values) {
      this.values = values;
    }
  }

  @HttpQuery
  private static class CustomDelimiterListQuery {    
    @JsonProperty("paramA")
    @HttpQueryDelimited(delimiter = ";")
    private List<String> values;

    public CustomDelimiterListQuery(List<String> values) {
      this.values = values;
    }
  }

  @HttpQuery
  @NoArgsConstructor
  @EqualsAndHashCode
  private static class CustomDelimiterNoEncodeListQuery {    
    @JsonProperty("paramA")
    @HttpQueryDelimited(delimiter = ";", encodeDelimiter = false)
    private List<String> values;

    public CustomDelimiterNoEncodeListQuery(List<String> values) {
      this.values = values;
    }
  }

  @Test
  public void testRegularListSerialization() throws JsonProcessingException {
    assertEquals("?paramA=first&paramA=second&paramA=third",
        objectMapper.writeValueAsString(
            new RegularListQuery(EXAMPLE_LIST)));
  }

  @Test
  public void testDelimitedListSerialization() throws JsonProcessingException {
    assertEquals("?paramA=first%2Csecond%2Cthird",
        objectMapper.writeValueAsString(
            new DelimitedListQuery(EXAMPLE_LIST)));
  }

  @Test
  public void testDelimitedListNoEncodeSerialization() throws JsonProcessingException {
    assertEquals("?paramA=first,second,third",
        objectMapper.writeValueAsString(
            new DelimitedListNoEncodeQuery(EXAMPLE_LIST)));
  }

  @Test
  public void testCustomDelimiterListSerialization() throws JsonProcessingException {
    assertEquals("?paramA=first%3Bsecond%3Bthird",
        objectMapper.writeValueAsString(
            new CustomDelimiterListQuery(EXAMPLE_LIST)));
  }

  @Test
  public void testCustomDelimiterNoEncodeListSerialization() throws JsonProcessingException {
    assertEquals("?paramA=first;second;third",
        objectMapper.writeValueAsString(
            new CustomDelimiterNoEncodeListQuery(EXAMPLE_LIST)));
  }

  @Test
  public void testCustomDelimiterNoEncodeListDeserialization() throws IOException {
    assertEquals(new CustomDelimiterNoEncodeListQuery(EXAMPLE_LIST),
        objectMapper.readValue("\"?paramA=first;second;third\"",
            CustomDelimiterNoEncodeListQuery.class));
  }
}
