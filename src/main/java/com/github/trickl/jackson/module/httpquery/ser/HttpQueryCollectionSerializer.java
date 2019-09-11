package com.github.trickl.jackson.module.httpquery.ser;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

public class HttpQueryCollectionSerializer extends StdSerializer<Collection<?>> {

  private static final long serialVersionUID = 1L;

  private final BeanPropertyWriter prop;
  private final boolean encodeNames;
  private final boolean encodeValues;

  /** Create a serializer for converting an object to an Http query string. */
  public HttpQueryCollectionSerializer(
      BeanPropertyWriter prop, boolean encodeNames, boolean encodeValues) {
    super((Class<Collection<?>>) prop.getPropertyType());
    this.prop = prop;
    this.encodeNames = encodeNames;
    this.encodeValues = encodeValues;
  }

  @Override
  public final void serialize(
      Collection<?> collection, JsonGenerator gen, SerializerProvider provider) throws IOException {

    boolean propertyWritten = false;
    try {
      for (Object value : collection) {
        if (propertyWritten) {
          gen.writeRaw("&");
        }

        propertyWritten = serializeAsNameValue(value, prop, gen, provider);
      }
    } catch (Exception e) {
      wrapAndThrow(provider, e, "Collection", prop.getName());
    }
  }

  /** Write a property out as "name=value". */
  public boolean serializeAsNameValue(
      Object propValue, BeanPropertyWriter prop, JsonGenerator gen, SerializerProvider provider)
      throws Exception {

    JsonFactory jsonFactory = new JsonFactory();
    StringWriter valueWriter = new StringWriter();
    String propName = prop.getName();
    String name = encodeNames ? encode(propName) : propName;

    try (QuotelessStringGenerator valueGenerator =
        new QuotelessStringGenerator(jsonFactory.createGenerator(valueWriter))) {
      if (propValue == null) {
        if (prop.willSuppressNulls()) {
          return false;
        } else if (!prop.hasNullSerializer()) {
          // Just return the parameter name
          gen.writeRaw(name);
          return true;
        } else {
          provider.findNullValueSerializer(prop).serialize(propValue, valueGenerator, provider);
        }
      } else {
        Class<?> cls = propValue.getClass();
        provider
            .findTypedValueSerializer(cls, true, prop)
            .serialize(propValue, valueGenerator, provider);
      }
    }

    String value = valueWriter.getBuffer().toString();
    gen.writeRaw(name);
    gen.writeRaw("=");
    gen.writeRaw(encodeValues ? encode(value) : value);

    return true;
  }

  private String encode(String value) throws UnsupportedEncodingException {
    return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
  }
}
