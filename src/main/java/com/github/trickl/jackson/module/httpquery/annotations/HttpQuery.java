package com.github.trickl.jackson.module.httpquery.annotations;

import com.fasterxml.jackson.annotation.JacksonAnnotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface HttpQuery {
  /**
  * Whether a question mark is included at the beginning
  * of the query.
  */
  boolean includeQuestionMark() default true;

  /**
   * Whether to URI encode parameter names.
   */
  boolean encodeNames() default true;

  /**
   * Whether to URI encode parameter values.
   */
  boolean encodeValues() default true;

  /**
   * Whether to ignore unknown parameters when deserializing.
   * @return
   */
  boolean ignoreUnknown() default true;
}
