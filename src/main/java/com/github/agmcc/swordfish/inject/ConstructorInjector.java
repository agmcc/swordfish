package com.github.agmcc.swordfish.inject;

import com.github.agmcc.swordfish.domain.Name;
import java.util.List;

public class ConstructorInjector extends AbstractInjector {

  public ConstructorInjector(final List<Name> dependencies) {
    super(dependencies);
  }

  @Override
  public void accept(final InjectorVisitor visitor) {
    visitor.visit(this);
  }
}
