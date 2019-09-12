package com.github.agmcc.swordfish.domain;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public class Module {

  private final Name name;

  private final Set<Bean> beans = new TreeSet<>(Comparator.comparing(Bean::getName));

  private final Set<Bean> published = new TreeSet<>(Comparator.comparing(Bean::getName));

  public Module(final Name name, final Set<Bean> beans, final Set<Bean> published) {
    this.name = name;
    this.beans.addAll(beans);
    this.published.addAll(published);
  }

  public Name getName() {
    return name;
  }

  public Set<Bean> getBeans() {
    return beans;
  }

  public Set<Bean> getPublished() {
    return published;
  }

  @Override
  public String toString() {
    return name.toString();
  }
}
