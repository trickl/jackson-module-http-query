package com.github.trickl.jackson.module.httpquery.deser;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.json.ReaderBasedJsonParser;
import com.fasterxml.jackson.core.sym.CharsToNameCanonicalizer;
import com.fasterxml.jackson.core.util.BufferRecycler;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.cfg.DeserializerFactoryConfig;
import com.fasterxml.jackson.databind.deser.BeanDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBuilder;
import com.fasterxml.jackson.databind.deser.BeanDeserializerFactory;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.github.trickl.jackson.module.httpquery.annotations.HttpQueryDelimited;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpQueryDeserializer extends StdDeserializer<Object> {

  private static final long serialVersionUID = 1L;

  private final JavaType javaType;
  private final boolean ignoreUnknown;

  /** Create a deserializer for converting a Http query string to a typed object. */
  public HttpQueryDeserializer(JavaType javaType, boolean ignoreUnknown) {
    super(Object.class);
    this.javaType = javaType;
    this.ignoreUnknown = ignoreUnknown;
  }

  @Override
  public Object deserialize(JsonParser jp, DeserializationContext ctxt)
      throws IOException, JsonProcessingException {
    JsonNode node = jp.getCodec().readTree(jp);
    if (!node.isTextual()) {
      String errorMessage =
          MessageFormat.format(
              "HttpQuery objects must be a textual type, but {0} supplied.",
              new Object[] {node.getNodeType()});
      throw new JsonParseException(jp, errorMessage);
    }

    String queryString = node.asText();
    if (queryString.startsWith("?")) {
      queryString = queryString.substring(1, queryString.length());
    }
    
    BeanDeserializer beanDeserializer = getBeanDeserializer(ctxt, javaType);
    ValueInstantiator valueInstantiator = beanDeserializer.getValueInstantiator();
    final Object bean = valueInstantiator.createUsingDefault(ctxt);

    String[] nameValueParams = queryString.split("&");
    Map<String, List<String>> params = new HashMap<>();
    for (String nameValueParam : nameValueParams) {      
      String name;
      String value = null;
      if (nameValueParam.contains("=")) {
        name = decode(nameValueParam.substring(0, nameValueParam.indexOf('=')));
        value = decode(nameValueParam.substring(
            nameValueParam.indexOf('=') + 1, nameValueParam.length()));              
      } else {
        name = decode(nameValueParam);        
      }
      params.computeIfAbsent(name, n -> new ArrayList<>());
      params.get(name).add(value);
    }

    for (Map.Entry<String, List<String>> param : params.entrySet()) {
      SettableBeanProperty prop = beanDeserializer.findProperty(new PropertyName(param.getKey()));
      if (prop == null) {
        if (ignoreUnknown) {
          continue;
        } else {
          String errorMessage =
              MessageFormat.format(
              "Unknown parameter \"{0}\" supplied.",
              new Object[] {param.getKey()});
          throw new JsonParseException(jp, errorMessage);
        }
      }

      deserializeNameValue(param.getValue(), prop, jp, ctxt, bean);
    }

    return bean;
  }

  private String decode(String value) throws UnsupportedEncodingException {
    return URLDecoder.decode(value, StandardCharsets.UTF_8.toString());
  }

  /**
   * Set an object value using the supplied query param.
   */
  public void deserializeNameValue(
      List<String> values,
      SettableBeanProperty prop,
      JsonParser p,
      DeserializationContext ctxt,
      Object bean)
      throws IOException {

    String jsonifiedParam = "";
    JavaType propType = prop.getType();
    boolean isArrayOrCollection = 
        prop.getType().isTypeOrSubTypeOf(Collection.class) 
        || prop.getType().isArrayType();    
    if (isArrayOrCollection) {
      HttpQueryDelimited delimited = prop.getAnnotation(HttpQueryDelimited.class);
      if (delimited != null) {
        String lastValue = values.get(values.size() - 1);
        values = Arrays.asList(lastValue.split(delimited.delimiter()));
      } 
      jsonifiedParam = wrapAsArray(values);     
    } else {
      String lastValue = values.get(values.size() - 1);
      if (propType.isPrimitive()) {
        jsonifiedParam = lastValue;
      } else {
        jsonifiedParam = quote(lastValue);
      }
    }

    StringReader reader = new StringReader(jsonifiedParam);
    JsonParser parser = new ReaderBasedJsonParser(
        getIoContext(),
        p.getFeatureMask(),
        reader,
        p.getCodec(),
        getCharsToNameCanonicalizer());
    parser.nextToken();
    prop.deserializeAndSet(parser, ctxt, bean);
  }

  private BeanDeserializer getBeanDeserializer(
      DeserializationContext context, JavaType javaType) throws JsonMappingException {
    DeserializationConfig config = context.getConfig();
    BeanDescription beanDesc = config.introspect(javaType);
    BeanDeserializerFactory factory = BeanDeserializerFactory.instance;
    BeanDeserializer deserializer = (BeanDeserializer)
        factory.createBeanDeserializer(context, javaType, beanDesc);
    deserializer.resolve(context);
    return deserializer;
  }

  private IOContext getIoContext() {
    BufferRecycler recycler = new BufferRecycler();
    return new IOContext(recycler, null, false);
  }

  private CharsToNameCanonicalizer getCharsToNameCanonicalizer() {
    return CharsToNameCanonicalizer.createRoot();
  }

  private String quote(String value) {
    return '"' + value + '"';
  }

  private String wrapAsArray(List<String> values) {
    return "[" + values.stream().map(this::quote)
        .collect(Collectors.joining(" ,")) + "]";
  }
}
