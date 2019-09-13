package com.github.trickl.jackson.module.httpquery;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.trickl.jackson.module.httpquery.annotations.HttpQuery;
import com.github.trickl.jackson.module.httpquery.annotations.HttpQueryDelimited;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class EmptyListsTest {

  private static ObjectMapper objectMapper;

  private static final List<String> EXAMPLE_LIST = Arrays.asList("first", "second", "third");

  @BeforeAll
  public static void setup() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new HttpQueryModule());
  }

  @HttpQuery
  @NoArgsConstructor
  @EqualsAndHashCode
  @ToString
  private static class RegularListQuery {
    @JsonProperty("paramA")
    private List<String> valuesA;

    @JsonProperty("paramB")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> valuesB;

    public RegularListQuery(List<String> valuesA, List<String> valuesB) {
      this.valuesA = valuesA;
      this.valuesB = valuesB;
    }
  }

  @HttpQuery
  @NoArgsConstructor
  @EqualsAndHashCode
  @ToString
  private static class DelimitedListQuery {
    @JsonProperty("paramA")
    @HttpQueryDelimited
    private List<String> valuesA;

    @JsonProperty("paramB")
    @HttpQueryDelimited
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> valuesB;

    public DelimitedListQuery(List<String> valuesA, List<String> valuesB) {
      this.valuesA = valuesA;
      this.valuesB = valuesB;
    }
  }

  @Test
  public void testRegularListNullIncludedSerialization() throws JsonProcessingException {
    assertEquals(
        "?paramA=null&paramB=first&paramB=second&paramB=third",
        objectMapper.valueToTree(new RegularListQuery(
            null, EXAMPLE_LIST)).textValue());
  }

  @Test
  public void testRegularListNullExcludedSerialization() throws JsonProcessingException {
    assertEquals(
        "?paramA=first&paramA=second&paramA=third",
        objectMapper.valueToTree(new RegularListQuery(EXAMPLE_LIST,
            null)).textValue());
  }

  @Test
  public void testDelimitedListNullIncludedSerialization() throws JsonProcessingException {
    assertEquals(
        "?paramA=null&paramB=first%2Csecond%2Cthird",
        objectMapper.valueToTree(new DelimitedListQuery(
            null, EXAMPLE_LIST)).textValue());
  }

  @Test
  public void testDelimitedListNullExcludedSerialization() throws JsonProcessingException {
    assertEquals(
        "?paramA=first%2Csecond%2Cthird",
        objectMapper.valueToTree(new DelimitedListQuery(EXAMPLE_LIST, 
            null)).textValue());
  }
  
  @Test
  public void testRegularListEmptyIncludedSerialization() throws JsonProcessingException {
    assertEquals(
        "?paramB=first&paramB=second&paramB=third",
        objectMapper.valueToTree(new RegularListQuery(
            Collections.emptyList(), EXAMPLE_LIST)).textValue());
  }

  @Test
  public void testRegularListEmptyExcludedSerialization() throws JsonProcessingException {
    assertEquals(
        "?paramA=first&paramA=second&paramA=third",
        objectMapper.valueToTree(new RegularListQuery(EXAMPLE_LIST,
            Collections.emptyList())).textValue());
  }

  @Test
  public void testDelimitedListEmptyIncludedSerialization() throws JsonProcessingException {
    assertEquals(
        "?paramB=first%2Csecond%2Cthird",
        objectMapper.valueToTree(new DelimitedListQuery(
           Collections.emptyList(), EXAMPLE_LIST)).textValue());
  }

  @Test
  public void testDelimitedListEmptyExcludedSerialization() throws JsonProcessingException {
    assertEquals(
        "?paramA=first%2Csecond%2Cthird",
        objectMapper.valueToTree(new DelimitedListQuery(EXAMPLE_LIST, 
           Collections.emptyList())).textValue());
  }
}
