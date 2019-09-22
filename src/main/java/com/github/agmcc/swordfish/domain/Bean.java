package com.github.agmcc.swordfish.domain;

import com.github.agmcc.swordfish.inject.Injector;
import java.util.Objects;

public class Bean {

  private final Name name;

  private final Injector injector;

  public Bean(final Name name, final Injector injector) {
    this.name = name;
    this.injector = injector;
  }

  public Name getName() {
    return name;
  }

  public Injector getInjector() {
    return injector;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final Bean bean = (Bean) o;
    return name.equals(bean.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  @Override
  public String toString() {
    return name.toString();
  }
}
