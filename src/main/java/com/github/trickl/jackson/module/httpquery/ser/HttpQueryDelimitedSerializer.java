package com.github.trickl.jackson.module.httpquery.ser;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.github.trickl.jackson.module.httpquery.ParamValueGenerator;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

public class HttpQueryDelimitedSerializer extends StdSerializer<Collection<?>> {

  private static final long serialVersionUID = 1L;

  private final JavaType javaType;
  private final String delimiter;
  private final boolean encodeDelimiter;
  private final boolean encodeValues;

  /**
   * Create a serializer for writing a list as a delimited query parameter.
   *
   * @param delimiter The delimiter
   */
  public HttpQueryDelimitedSerializer(
      JavaType javaType, String delimiter, boolean encodeDelimiter, boolean encodeValues) {
    super((Class<Collection<?>>) javaType.getRawClass());
    this.javaType = javaType;
    this.delimiter = delimiter;
    this.encodeDelimiter = encodeDelimiter;
    this.encodeValues = encodeValues;
  }

  @Override
  public final void serialize(
      Collection<?> collection, JsonGenerator gen, SerializerProvider provider) throws IOException {
    StringBuilder builder = new StringBuilder();
    String encodedDelimiter = encodeDelimiter ? encode(delimiter) : delimiter;
    for (Object value : collection) {
      if (builder.length() > 0) {
        builder.append(encodedDelimiter);
      }
      builder.append(serialize(value, provider));
    }
    gen.writeRaw(builder.toString());
  }

  private String serialize(Object collectionValue, SerializerProvider provider) throws IOException {
    StringWriter valueWriter = new StringWriter();
    JsonFactory jsonFactory = new JsonFactory();
    try (ParamValueGenerator valueGenerator =
        new ParamValueGenerator(jsonFactory.createGenerator(valueWriter))) {
      if (collectionValue == null) {
        provider.defaultSerializeNull(valueGenerator);
      } else {
        Class<?> cls = collectionValue.getClass();
        provider
            .findTypedValueSerializer(cls, true, null)
            .serialize(collectionValue, valueGenerator, provider);
      }
    } catch (IOException ex) {
      wrapAndThrow(provider, ex, javaType.getContentType().getTypeName(), javaType.getTypeName());
    }

    String value = valueWriter.getBuffer().toString();
    return encodeValues ? encode(value) : value;
  }

  private String encode(String value) throws UnsupportedEncodingException {
    return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
  }
}
