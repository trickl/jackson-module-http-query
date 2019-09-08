package com.github.trickl.jackson.module.httpquery.ser;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerBuilder;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;
import java.util.List;

public class BeanPropertyWriterProvider extends BeanSerializerFactory {

  private static final long serialVersionUID = 1L;

  private SerializerProvider provider;

  public BeanPropertyWriterProvider(SerializerProvider provider) {
    super(null);
    this.provider = provider;
  }

  /**
   * Get the serializable properties for a bean.
   */
  public List<BeanPropertyWriter> getProperties(JavaType javaType)
      throws JsonMappingException {
    SerializationConfig config = provider.getConfig();
    BeanDescription beanDesc = config.introspect(javaType);
    BeanSerializerBuilder builder = new BeanSerializerBuilder(beanDesc);    
    List<BeanPropertyWriter> props = findBeanProperties(provider, beanDesc, builder);
    props.stream().forEach(prop -> prop.fixAccess(config));
    return props;
  }
}
