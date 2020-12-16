package com.github.trickl.jackson.module.httpquery;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.github.trickl.jackson.module.httpquery.annotations.HttpQuery;
import com.github.trickl.jackson.module.httpquery.deser.HttpQueryDeserializer;
import com.github.trickl.jackson.module.httpquery.ser.HttpQuerySerializer;

public class HttpQueryAnnotationIntrospector extends AnnotationIntrospector {

  private static final long serialVersionUID = 1L;

  @Override
  public JsonSerializer<?> findSerializer(Annotated am) {
    if (am.hasAnnotation(HttpQuery.class)) {
      HttpQuery annotation = am.getAnnotation(HttpQuery.class);
      return new HttpQuerySerializer(
          am.getType(),
          annotation.includeQuestionMark(),
          annotation.encodeNames(),
          annotation.encodeValues());
    }
    return null;
  }

  @Override
  public Object findDeserializer(Annotated am) {
    if (am.hasAnnotation(HttpQuery.class)) {
      HttpQuery annotation = am.getAnnotation(HttpQuery.class);
      return new HttpQueryDeserializer(
          am.getType(),
          annotation.ignoreUnknown(),
          annotation.encodeNames(),
          annotation.encodeValues());
    }
    return null;
  }

  @Override
  public Version version() {
    return PackageVersion.VERSION;
  }
}
