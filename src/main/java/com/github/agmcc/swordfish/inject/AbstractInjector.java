package com.github.agmcc.swordfish.inject;

import com.github.agmcc.swordfish.domain.Name;
import java.util.List;

public abstract class AbstractInjector implements Injector {

  private final List<Name> dependencies;

  AbstractInjector(final List<Name> dependencies) {
    this.dependencies = dependencies;
  }

  @Override
  public List<Name> getDependencies() {
    return dependencies;
  }
}
