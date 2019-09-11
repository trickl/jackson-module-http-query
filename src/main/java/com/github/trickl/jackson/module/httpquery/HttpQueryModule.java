package com.github.trickl.jackson.module.httpquery;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;

public class HttpQueryModule extends Module {

  @Override
  public String getModuleName() {
    return getClass().getSimpleName();
  }

  @Override
  public Version version() {
    return PackageVersion.VERSION;
  }

  @Override
  public void setupModule(SetupContext context) {
    context.appendAnnotationIntrospector(new HttpQueryAnnotationIntrospector());
  }
}
