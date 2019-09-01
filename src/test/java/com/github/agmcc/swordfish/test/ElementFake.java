package com.github.agmcc.swordfish.test;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.type.TypeMirror;

public class ElementFake implements Element {

  private ElementKind kind;

  private Set<Modifier> modifiers;

  private List<? extends Element> enclosingElements;

  private Set<Class<? extends Annotation>> annotationClasses;

  private TypeMirror type;

  ElementFake(final Builder builder) {
    this.kind = builder.kind;
    this.modifiers = builder.modifiers;
    this.enclosingElements = builder.enclosingElements;
    this.annotationClasses = builder.annotationClasses;
    this.type = builder.type;
  }

  public static Builder builder() {
    return new Builder();
  }

  @Override
  public TypeMirror asType() {
    return type;
  }

  @Override
  public ElementKind getKind() {
    return kind;
  }

  @Override
  public Set<Modifier> getModifiers() {
    return modifiers;
  }

  @Override
  public Name getSimpleName() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Element getEnclosingElement() {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<? extends Element> getEnclosedElements() {
    return enclosingElements;
  }

  @Override
  public List<? extends AnnotationMirror> getAnnotationMirrors() {
    throw new UnsupportedOperationException();
  }

  @Override
  @SuppressWarnings("unchecked")
  public <A extends Annotation> A getAnnotation(final Class<A> annotationType) {
    return (A)
        annotationClasses.stream()
            .filter(c -> c.isAssignableFrom(annotationType))
            .findAny()
            .map(c -> (Annotation) () -> c)
            .orElse(null);
  }

  @Override
  public <A extends Annotation> A[] getAnnotationsByType(final Class<A> annotationType) {
    throw new UnsupportedOperationException();
  }

  @Override
  public <R, P> R accept(final ElementVisitor<R, P> v, final P p) {
    throw new UnsupportedOperationException();
  }

  public static class Builder {

    private ElementKind kind;

    private Set<Modifier> modifiers = new HashSet<>();

    private List<? extends Element> enclosingElements = new ArrayList<>();

    private Set<Class<? extends Annotation>> annotationClasses = new HashSet<>();

    private TypeMirror type;

    Builder() {}

    public Builder kind(final ElementKind kind) {
      this.kind = kind;
      return this;
    }

    public Builder modifiers(final Set<Modifier> modifiers) {
      this.modifiers = modifiers;
      return this;
    }

    public Builder modifier(final Modifier modifier) {
      modifiers.add(modifier);
      return this;
    }

    public Builder enclosingElements(final List<? extends Element> enclosingElements) {
      this.enclosingElements = enclosingElements;
      return this;
    }

    public Builder annotationClass(final Class<? extends Annotation> annotationClass) {
      annotationClasses.add(annotationClass);
      return this;
    }

    public Builder type(final TypeMirror type) {
      this.type = type;
      return this;
    }

    public ElementFake build() {
      return new ElementFake(this);
    }
  }
}
