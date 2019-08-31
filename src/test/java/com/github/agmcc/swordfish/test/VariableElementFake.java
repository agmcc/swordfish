package com.github.agmcc.swordfish.test;

import javax.lang.model.element.VariableElement;

public class VariableElementFake extends ElementFake implements VariableElement {

  private VariableElementFake(final Builder builder) {
    super(builder);
  }

  public static Builder builder() {
    return new Builder();
  }

  @Override
  public Object getConstantValue() {
    throw new UnsupportedOperationException();
  }

  public static class Builder extends ElementFake.Builder {

    private Builder() {}

    public VariableElementFake build() {
      return new VariableElementFake(this);
    }
  }
}
