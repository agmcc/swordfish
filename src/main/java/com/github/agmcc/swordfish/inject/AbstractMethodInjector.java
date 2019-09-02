package com.github.agmcc.swordfish.inject;

import com.github.agmcc.swordfish.domain.Name;
import java.util.List;

public abstract class AbstractMethodInjector extends AbstractInjector {

  private final Name methodClassName;

  private final String methodName;

  AbstractMethodInjector(
      final Name methodClassName, final String methodName, final List<Name> dependencies) {
    super(dependencies);
    this.methodClassName = methodClassName;
    this.methodName = methodName;
  }

  public Name getMethodClassName() {
    return methodClassName;
  }

  public String getMethodName() {
    return methodName;
  }
}
