package com.github.agmcc.swordfish.inject;

import com.github.agmcc.swordfish.domain.Name;
import java.util.List;

public class StaticProviderInjector extends AbstractInjector {

  private final Name providerName;

  private final String providerMethod;

  public StaticProviderInjector(
      final Name providerName, final String providerMethod, final List<Name> dependencies) {
    super(dependencies);
    this.providerName = providerName;
    this.providerMethod = providerMethod;
  }

  @Override
  public void accept(final InjectorVisitor visitor) {
    visitor.visit(this);
  }

  public Name getProviderName() {
    return providerName;
  }

  public String getProviderMethod() {
    return providerMethod;
  }
}
