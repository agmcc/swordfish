package com.github.agmcc.swordfish.domain;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public class Module {

  private final Name name;

  private final Set<Bean> beans = new TreeSet<>(Comparator.comparing(Bean::getName));

  public Module(final Name name, final Set<Bean> beans) {
    this.name = name;
    this.beans.addAll(beans);
  }

  public Name getName() {
    return name;
  }

  public Set<Bean> getBeans() {
    return beans;
  }

  @Override
  public String toString() {
    return name.toString();
  }
}
