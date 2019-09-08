package com.github.trickl.jackson.module.httpquery.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.util.JsonGeneratorDelegate;
import java.io.IOException;

public class QuotelessStringGenerator extends JsonGeneratorDelegate {
  public QuotelessStringGenerator(JsonGenerator delegate) {
    super(delegate, true);
  }

  @Override
  public void writeString(String text) throws IOException {
    // No quotes
    super.writeRawValue(text);
  }

  @Override
  public void writeString(SerializableString text) throws IOException {
    // No quotes
    super.writeRawValue(text);
  }

  @Override
  public void writeString(char[] text, int offset, int len) throws IOException {
    // No quotes
    super.writeRawValue(text, offset, len);
  }
}
