package com.github.agmcc.swordfish.test;

import java.lang.annotation.Annotation;
import java.util.List;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;

public class TypeMirrorFake implements TypeMirror {

  private String qualifiedName;

  private TypeMirrorFake(final Builder builder) {
    this.qualifiedName = builder.qualifiedName;
  }

  public static Builder builder() {
    return new Builder();
  }

  @Override
  public TypeKind getKind() {
    throw new UnsupportedOperationException();
  }

  @Override
  public <R, P> R accept(final TypeVisitor<R, P> v, final P p) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<? extends AnnotationMirror> getAnnotationMirrors() {
    throw new UnsupportedOperationException();
  }

  @Override
  public <A extends Annotation> A getAnnotation(final Class<A> annotationType) {
    throw new UnsupportedOperationException();
  }

  @Override
  public <A extends Annotation> A[] getAnnotationsByType(final Class<A> annotationType) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String toString() {
    return qualifiedName;
  }

  public static class Builder {

    private String qualifiedName;

    private Builder() {}

    public Builder qualifiedName(final String qualifiedName) {
      this.qualifiedName = qualifiedName;
      return this;
    }

    public TypeMirrorFake build() {
      return new TypeMirrorFake(this);
    }
  }
}
