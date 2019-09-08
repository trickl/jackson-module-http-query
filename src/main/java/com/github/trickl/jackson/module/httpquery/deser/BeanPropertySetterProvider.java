package com.github.trickl.jackson.module.httpquery.deser;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.cfg.DeserializerFactoryConfig;
import com.fasterxml.jackson.databind.deser.BeanDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBuilder;
import com.fasterxml.jackson.databind.deser.BeanDeserializerFactory;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;

public class BeanPropertySetterProvider extends BeanDeserializerFactory {

  private static final long serialVersionUID = 1542811325586920895L;

  private final DeserializationContext context;

  /**
   * Create a bean introspector.
   *
   * @param context The deserialization context
   */
  public BeanPropertySetterProvider(DeserializationContext context) {
    super(new DeserializerFactoryConfig());        
    this.context = context;
  }

  /** Get the serializable properties for a bean. */
  public BeanDeserializer getBeanDeserializer(JavaType javaType) throws JsonMappingException {
    DeserializationConfig config = context.getConfig();
    BeanDescription beanDesc = config.introspect(javaType);
    BeanDeserializerBuilder builder = constructBeanDeserializerBuilder(context, beanDesc);    
    ValueInstantiator valueInstantiator = findValueInstantiator(context, beanDesc);
    builder.setValueInstantiator(valueInstantiator);    
    addBeanProps(context, beanDesc, builder);
    addInjectables(context, beanDesc, builder);

    BeanDeserializer deserializer = (BeanDeserializer) builder.build();
    deserializer.resolve(context);
    return deserializer;
  }
}
