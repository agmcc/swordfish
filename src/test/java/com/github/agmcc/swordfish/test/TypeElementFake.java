package com.github.agmcc.swordfish.test;

import java.util.List;
import javax.lang.model.element.Name;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.TypeMirror;

public class TypeElementFake extends ElementFake implements TypeElement {

  private String qualifiedName;

  private TypeElementFake(final Builder builder) {
    super(builder);
    this.qualifiedName = builder.qualifiedName;
  }

  public static Builder builder() {
    return new Builder();
  }

  @Override
  public NestingKind getNestingKind() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Name getQualifiedName() {
    throw new UnsupportedOperationException();
  }

  @Override
  public TypeMirror getSuperclass() {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<? extends TypeMirror> getInterfaces() {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<? extends TypeParameterElement> getTypeParameters() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String toString() {
    return qualifiedName;
  }

  public static class Builder extends ElementFake.Builder {

    private String qualifiedName;

    private Builder() {}

    public Builder qualifiedName(final String qualifiedName) {
      this.qualifiedName = qualifiedName;
      return this;
    }

    public TypeElementFake build() {
      return new TypeElementFake(this);
    }
  }
}
