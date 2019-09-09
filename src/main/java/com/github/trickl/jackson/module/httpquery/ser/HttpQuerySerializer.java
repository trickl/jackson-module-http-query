package com.github.trickl.jackson.module.httpquery.ser;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializer;
import com.fasterxml.jackson.databind.ser.BeanSerializerBuilder;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.github.trickl.jackson.module.httpquery.annotations.HttpQueryDelimited;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
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
    
    int i = 0;
    boolean propertyWritten = false;
    try {
      for (final int len = props.length; i < len; ++i) {      
        if (i == 0 && includeQuestionMark) {
          gen.writeRaw("?");
        } else if (propertyWritten) {
          gen.writeRaw("&");
        }

        BeanPropertyWriter prop = props[i];
        if (prop != null) { // can have nulls in filtered list
          propertyWritten = serializeAsNameValue(bean, prop, gen, provider);
        }
      }
    } catch (Exception e) {
      String name = (i == props.length) ? "[anySetter]" : props[i].getName();
      wrapAndThrow(provider, e, bean, name);
    }
  }

  /** Write a property out as "name=value". */
  public boolean serializeAsNameValue(
      Object bean, BeanPropertyWriter prop, JsonGenerator gen, SerializerProvider provider)
      throws Exception {    
    JsonFactory jsonFactory = new JsonFactory();
    StringWriter valueWriter = new StringWriter();
    Object propValue = prop.get(bean);
    String propName = prop.getName();
    String name = encodeNames ? encode(propName) : propName;

    try (QuotelessStringGenerator valueGenerator =
        new QuotelessStringGenerator(jsonFactory.createGenerator(valueWriter))) {

      if (propValue == null) {
        if (prop.willSuppressNulls()) {
          return false;
        } else {
          provider.findNullValueSerializer(prop).serialize(propValue, valueGenerator, provider);
        }          
      } else if (prop.getType().isTypeOrSubTypeOf(Collection.class) 
          || prop.getType().isArrayType()) {
        Collection<?> collection = Collections.emptyList();
        if (prop.getType().isTypeOrSubTypeOf(Collection.class)) {
          collection = (Collection<?>) propValue;
        } else {
          AtomicInteger index = new AtomicInteger(0);
          collection = Stream.generate(() -> 
            Array.get(propValue, index.getAndIncrement())
          ).limit(Array.getLength(propValue))
          .collect(Collectors.toList());
        } 

        HttpQueryDelimited annotatedList = prop.findAnnotation(HttpQueryDelimited.class);
        if (annotatedList != null) {
          HttpQueryDelimitedSerializer serializer =
              new HttpQueryDelimitedSerializer(
                  prop.getType(),
                  annotatedList.delimiter(),
                  annotatedList.encodeDelimiter(),
                  encodeValues);
          gen.writeRaw(name);
          gen.writeRaw("=");
          serializer.serialize(collection, gen, provider);
          return true;
        } else {
          HttpQueryCollectionSerializer serializer =
              new HttpQueryCollectionSerializer(prop, encodeNames, encodeValues);
          serializer.serialize(collection, gen, provider);
          return true;
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

  private BeanSerializer getBeanSerializer(
      SerializerProvider provider, JavaType javaType) throws JsonMappingException {
    
    SerializationConfig config = provider.getConfig();
    BeanDescription beanDesc = config.introspect(javaType);
    BeanSerializerFactory factory = BeanSerializerFactory.instance;
    BeanSerializer serializer = (BeanSerializer)
        factory.findBeanSerializer(provider, javaType, beanDesc);
    serializer.resolve(provider);    
    return serializer;
  }
}
