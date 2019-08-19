package com.github.agmcc.swordfish.domain;

import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;

public class Bean {

  private final TypeMirror type;

  private final List<TypeMirror> dependencies = new ArrayList<>();

  private final List<ExecutableElement> providers = new ArrayList<>();

  private final BeanType beanType;

  public Bean(final TypeMirror type, final BeanType beanType) {
    this.type = type;
    this.beanType = beanType;
  }

  public void addDependency(final TypeMirror type) {
    dependencies.add(type);
  }

  public void addProvider(final ExecutableElement element) {
    providers.add(element);
  }

  public TypeMirror getType() {
    return type;
  }

  public List<TypeMirror> getDependencies() {
    return dependencies;
  }

  public List<ExecutableElement> getProviders() {
    return providers;
  }

  public BeanType getBeanType() {
    return beanType;
  }

  @Override
  public String toString() {
    return type.toString();
  }
}
