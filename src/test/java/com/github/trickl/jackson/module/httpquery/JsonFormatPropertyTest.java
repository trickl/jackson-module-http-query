package com.github.trickl.jackson.module.httpquery;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.trickl.jackson.module.httpquery.annotations.HttpQuery;
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
  private class JsonFormatQuery {
    @JsonProperty("param")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date value;

    public JsonFormatQuery(Date value) {
      this.value = value;
    }
  }

  @Test
  public void testStringParamSerialization() throws JsonProcessingException {
    Calendar calendar = new GregorianCalendar(2013,0,31);
    assertEquals("?param=2013-01-31",
        objectMapper.writeValueAsString(new JsonFormatQuery(
            calendar.getTime())));
  }
}
