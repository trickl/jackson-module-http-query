package com.github.trickl.jackson.module.httpquery;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.trickl.jackson.module.httpquery.annotations.HttpQuery;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class JsonFormatPropertyTest {

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
  private static class JsonFormatQuery {
    @JsonProperty("param")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date value;
  }

  @Test
  public void testStringParamSerialization() throws JsonProcessingException {
    Calendar calendar = new GregorianCalendar(2013, 0, 31);
    assertEquals(
        "?param=2013-01-31",
        objectMapper.writeValueAsString(new JsonFormatQuery(calendar.getTime())));
  }

  @Test
  public void testStringParamDeserialization() throws IOException {
    Calendar calendar = new GregorianCalendar(2013, 0, 31);
    assertEquals(
        new JsonFormatQuery(calendar.getTime()),
        objectMapper.readValue("\"?param=2013-01-31\"", JsonFormatQuery.class));
  }
}
