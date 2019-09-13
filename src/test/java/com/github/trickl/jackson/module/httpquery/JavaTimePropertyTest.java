package com.github.trickl.jackson.module.httpquery;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.trickl.jackson.module.httpquery.annotations.HttpQuery;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class JavaTimePropertyTest {

  private static ObjectMapper objectMapper;

  @BeforeAll
  public static void setup() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new HttpQueryModule());
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    objectMapper.configure(
        DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE, true);
  }

  @HttpQuery
  @NoArgsConstructor
  @AllArgsConstructor
  @EqualsAndHashCode
  private static class JsonFormatQuery {
    @JsonProperty("param")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC")
    private LocalDate value;
  }


  @HttpQuery
  @NoArgsConstructor
  @AllArgsConstructor
  @EqualsAndHashCode
  private static class JsonFormatEncodedQuery {
    @JsonProperty("param")
    @JsonFormat(shape = JsonFormat.Shape.STRING,
        pattern = "yyyy-MM-dd'T'HH:mm:ssX", timezone = "UTC")
    private Instant value;
  }

  @Test
  public void testStringParamSerialization() throws JsonProcessingException {
    assertEquals(
        "?param=2013-01-31",
        objectMapper.valueToTree(new JsonFormatQuery(LocalDate.parse("2013-01-31"))).textValue());
  }

  @Test
  public void testStringParamDeserialization() throws IOException {
    assertEquals(
        new JsonFormatQuery(LocalDate.parse("2013-01-31")),
        objectMapper.readValue("\"?param=2013-01-31\"", JsonFormatQuery.class));
  }

  @Test
  public void testFormatEncodedSerialization() throws JsonProcessingException {
    assertEquals(
        "?param=2013-01-31T12%3A00%3A15Z",
        objectMapper.valueToTree(new JsonFormatEncodedQuery(Instant.parse("2013-01-31T12:00:15Z"))).textValue());
  }

  @Test
  public void testFormatEncodedDeserialization() throws IOException {
    assertEquals(
        new JsonFormatEncodedQuery(Instant.parse("2013-01-31T12:00:15Z")),
        objectMapper.readValue("\"?param=2013-01-31T12%3A00%3A15Z\"", JsonFormatEncodedQuery.class));
  }
}
