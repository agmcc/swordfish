package com.github.agmcc.swordfish.test;

import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

public class ExecutableElementFake extends ElementFake implements ExecutableElement {

  private List<? extends Element> parameters;

  private ExecutableElementFake(final Builder builder) {
    super(builder);
    this.parameters = builder.parameters;
  }

  public static Builder builder() {
    return new Builder();
  }

  @Override
  public List<? extends TypeParameterElement> getTypeParameters() {
    throw new UnsupportedOperationException();
  }

  @Override
  public TypeMirror getReturnType() {
    throw new UnsupportedOperationException();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<? extends VariableElement> getParameters() {
    return (List<? extends VariableElement>) parameters;
  }

  @Override
  public TypeMirror getReceiverType() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isVarArgs() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isDefault() {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<? extends TypeMirror> getThrownTypes() {
    throw new UnsupportedOperationException();
  }

  @Override
  public AnnotationValue getDefaultValue() {
    throw new UnsupportedOperationException();
  }

  public static class Builder extends ElementFake.Builder {

    private List<? extends Element> parameters = new ArrayList<>();

    private Builder() {}

    public Builder parameters(final List<? extends Element> parameters) {
      this.parameters = parameters;
      return this;
    }

    public ExecutableElementFake build() {
      return new ExecutableElementFake(this);
    }
  }
}
