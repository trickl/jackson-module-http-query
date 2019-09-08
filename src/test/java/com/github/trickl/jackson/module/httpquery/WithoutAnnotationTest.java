package com.github.trickl.jackson.module.httpquery;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

public class WithoutAnnotationTest {

  private static ObjectMapper objectMapper;

  @BeforeAll
  public static void setup() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new HttpQueryModule());
  }

  @NoArgsConstructor
  @EqualsAndHashCode
  private static class SinglePropertyQuery {
    @JsonProperty("param")
    private String value;

    public SinglePropertyQuery(String value) {
      this.value = value;
    }
  }

  @Test
  public void testSerializesAsExpected() throws JsonProcessingException {
    assertEquals("{\"param\":\"value\"}", 
        objectMapper.writeValueAsString(new SinglePropertyQuery("value")));
  }

  @Test
  public void testDeserializesAsExpected() throws IOException {
    assertEquals(new SinglePropertyQuery("value"),
        objectMapper.readValue("{\"param\":\"value\"}", SinglePropertyQuery.class));
  }
}
