package com.github.agmcc.swordfish.inject;

import com.github.agmcc.swordfish.domain.Name;
import java.util.List;

public class BeanMethodInjector extends AbstractMethodInjector {

  public BeanMethodInjector(
      final Name methodClassName, final String methodName, final List<Name> dependencies) {
    super(methodClassName, methodName, dependencies);
  }

  @Override
  public void accept(final InjectorVisitor visitor) {
    visitor.visit(this);
  }
}
