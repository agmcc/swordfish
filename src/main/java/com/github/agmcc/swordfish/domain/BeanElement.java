package com.github.agmcc.swordfish.domain;

import java.util.List;
import java.util.Objects;
import javax.lang.model.element.ExecutableElement;

public class BeanElement {

  private BeanDefinitionType beanDefinitionType;

  private String qualifiedName;

  private List<String> dependencies;

  private ExecutableElement provider;

  public BeanElement(
      final BeanDefinitionType beanDefinitionType,
      final String qualifiedName,
      final List<String> dependencies) {

    this.beanDefinitionType = beanDefinitionType;
    this.qualifiedName = qualifiedName;
    this.dependencies = dependencies;
  }

  public BeanDefinitionType getBeanDefinitionType() {
    return beanDefinitionType;
  }

  public String getQualifiedName() {
    return qualifiedName;
  }

  public List<String> getDependencies() {
    return dependencies;
  }

  public ExecutableElement getProvider() {
    return provider;
  }

  public void setProvider(final ExecutableElement provider) {
    this.provider = provider;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final BeanElement that = (BeanElement) o;
    return qualifiedName.equals(that.qualifiedName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(qualifiedName);
  }

  @Override
  public String toString() {
    return qualifiedName;
  }
}
