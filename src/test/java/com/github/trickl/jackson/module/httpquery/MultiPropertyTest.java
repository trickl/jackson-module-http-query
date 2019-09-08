package com.github.trickl.jackson.module.httpquery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.trickl.jackson.module.httpquery.annotations.HttpQuery;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

public class MultiPropertyTest {

  private static ObjectMapper objectMapper;

  @BeforeAll
  public static void setup() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new HttpQueryModule());
  }

  @HttpQuery
  @NoArgsConstructor
  @EqualsAndHashCode
  private static class MultiPropertyQuery {
    @JsonProperty("paramA")
    private String valueA;

    @JsonProperty("paramB")
    private String valueB;

    @JsonProperty("paramC")
    private int valueC;

    public MultiPropertyQuery(String valueA, String valueB, int valueC) {
      this.valueA = valueA;
      this.valueB = valueB;
      this.valueC = valueC;
    }
  }

  @Test
  public void testMultiParamSerialization() throws JsonProcessingException {
    assertEquals("?paramA=valueA&paramB=valueB&paramC=123",
        objectMapper.writeValueAsString(new MultiPropertyQuery("valueA", "valueB", 123)));
  }


  @Test
  public void testMultiParamDeserializationRequiresString() throws IOException {
    assertThrows(JsonParseException.class, () -> 
        objectMapper.readValue("{ \"value\": \"value\" }",
            MultiPropertyQuery.class));
  }

  @Test
  public void testMultiParamDeserialization() throws IOException {
    assertEquals(new MultiPropertyQuery("valueA", "valueB", 123),
        objectMapper.readValue("\"?paramA=valueA&paramB=valueB&paramC=123\"",
            MultiPropertyQuery.class));
  }
}
