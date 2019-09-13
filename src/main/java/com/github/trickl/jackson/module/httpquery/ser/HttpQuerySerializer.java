package com.github.trickl.jackson.module.httpquery.ser;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializer;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.github.trickl.jackson.module.httpquery.annotations.HttpQueryDelimited;
import com.github.trickl.jackson.module.httpquery.annotations.HttpQueryNoValue;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HttpQuerySerializer extends StdSerializer<Object> {

  private static final long serialVersionUID = 1L;

  private final JavaType javaType;
  private final boolean includeQuestionMark;
  private final boolean encodeNames;
  private final boolean encodeValues;

  /** Create a serializer for converting an object to an Http query string. */
  public HttpQuerySerializer(
      JavaType javaType, boolean includeQuestionMark, boolean encodeNames, boolean encodeValues) {
    super(Object.class);
    this.javaType = javaType;
    this.includeQuestionMark = includeQuestionMark;
    this.encodeNames = encodeNames;
    this.encodeValues = encodeValues;
  }

  @Override
  public final void serialize(Object bean, JsonGenerator gen, SerializerProvider provider)
      throws IOException {
    BeanSerializer beanSerializer = getBeanSerializer(provider, javaType);
    List<BeanPropertyWriter> propList = new ArrayList<>();
    beanSerializer.properties().forEachRemaining((prop) -> propList.add((BeanPropertyWriter) prop));
    BeanPropertyWriter[] props = propList.toArray(new BeanPropertyWriter[0]);

    List<String> nameValueParams = new ArrayList<>();
    int i = 0;
    try {
      for (final int len = props.length; i < len; ++i) {
        BeanPropertyWriter prop = props[i];
        if (prop != null) { // can have nulls in filtered list
          String nameValueParam = writeNameValueAsString(bean, prop, provider);
          if (nameValueParam != null && nameValueParam.length() > 0) {
            nameValueParams.add(nameValueParam);
          }
        }
      }
    } catch (Exception e) {
      String name = (i == props.length) ? "[anySetter]" : props[i].getName();
      wrapAndThrow(provider, e, bean, name);
    }

    String query = nameValueParams.stream().collect(Collectors.joining("&"));
    if (includeQuestionMark) {
      query = "?" + query;
    }
    gen.writeString(query);
  }

  /** Write a property out as "name=value". */
  public String writeNameValueAsString(
      Object bean, BeanPropertyWriter prop, SerializerProvider provider) throws Exception {
    StringWriter writer = new StringWriter();
    writeNameValue(writer, bean, prop, provider);
    return writer.toString();
  }

  /** Write a property out as "name=value". */
  public void writeNameValue(
      Writer nameValueWriter, Object bean, BeanPropertyWriter prop, SerializerProvider provider)
      throws Exception {

    JsonFactory jsonFactory = new JsonFactory();
    Object propValue = prop.get(bean);
    String propName = prop.getName();
    JavaType propType = prop.getType();
    String name = encodeNames ? encode(propName) : propName;

    StringWriter valueWriter = new StringWriter();
    try (JsonGenerator gen = jsonFactory.createGenerator(nameValueWriter)) {
      try (QuotelessStringGenerator valueGenerator =
          new QuotelessStringGenerator(jsonFactory.createGenerator(valueWriter))) {

        HttpQueryNoValue annotatedNoValue = prop.findAnnotation(HttpQueryNoValue.class);
        if (annotatedNoValue != null) {
          if (propValue != null && !Boolean.FALSE.equals(propValue)) {
            gen.writeRaw(name);
            return;
          } else {
            return;
          }
        } else if (propValue == null) {
          if (prop.willSuppressNulls()) {
            return;
          } else {
            provider.findNullValueSerializer(prop).serialize(propValue, valueGenerator, provider);
          }
        } else if (propType.isTypeOrSubTypeOf(Collection.class) || propType.isArrayType()) {
          Collection<?> collection = Collections.emptyList();
          if (propType.isTypeOrSubTypeOf(Collection.class)) {
            collection = (Collection<?>) propValue;
          } else {
            AtomicInteger index = new AtomicInteger(0);
            collection =
                Stream.generate(() -> Array.get(propValue, index.getAndIncrement()))
                    .limit(Array.getLength(propValue))
                    .collect(Collectors.toList());
          }

          HttpQueryDelimited annotatedList = prop.findAnnotation(HttpQueryDelimited.class);
          if (annotatedList != null && !collection.isEmpty()) {             
            HttpQueryDelimitedSerializer serializer =
                new HttpQueryDelimitedSerializer(
                    propType,
                    annotatedList.delimiter(),
                    annotatedList.encodeDelimiter(),
                    encodeValues);
            gen.writeRaw(name);
            gen.writeRaw("=");
            serializer.serialize(collection, gen, provider);
            return;
          } else {
            HttpQueryCollectionSerializer serializer =
                new HttpQueryCollectionSerializer(prop, encodeNames, encodeValues);
            serializer.serialize(collection, gen, provider);
            return;
          }
        } else {
          Class<?> cls = propValue.getClass();
          provider
              .findTypedValueSerializer(cls, true, prop)
              .serialize(propValue, valueGenerator, provider);
        }
      }

      String value = valueWriter.toString();
      gen.writeRaw(name);
      gen.writeRaw("=");
      gen.writeRaw(encodeValues ? encode(value) : value);
    }
  }

  private String encode(String value) throws UnsupportedEncodingException {
    return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
  }

  private BeanSerializer getBeanSerializer(SerializerProvider provider, JavaType javaType)
      throws JsonMappingException {

    SerializationConfig config = provider.getConfig();
    BeanDescription beanDesc = config.introspect(javaType);
    BeanSerializerFactory factory = BeanSerializerFactory.instance;
    BeanSerializer serializer =
        (BeanSerializer) factory.findBeanSerializer(provider, javaType, beanDesc);
    serializer.resolve(provider);
    return serializer;
  }
}
