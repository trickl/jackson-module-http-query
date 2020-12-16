package com.github.trickl.jackson.module.httpquery.annotations;

import com.fasterxml.jackson.annotation.JacksonAnnotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface HttpQueryDelimited {
  /** The delimiter for this list.
   * @return The delimiter for this list.
   */
  String delimiter() default ",";

  /** Whether to URI encode parameter values.
   * @return Whether to URI encode parameter values.
   */
  boolean encodeValues() default true;

  /** Whether to URI encode the supplied delimiter. 
   * @return Whether to URI encode the supplied delimiter. 
  */
  boolean encodeDelimiter() default true;
}
