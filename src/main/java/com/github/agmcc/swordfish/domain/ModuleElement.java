package com.github.agmcc.swordfish.domain;

import java.util.Set;

public class ModuleElement {

  private final Name name;

  private final Set<String> packages;

  private final Set<Name> uses;

  private final Set<Name> published;

  public ModuleElement(
      final Name name,
      final Set<String> packages,
      final Set<Name> uses,
      final Set<Name> published) {

    this.name = name;
    this.packages = packages;
    this.uses = uses;
    this.published = published;
  }

  public Name getName() {
    return name;
  }

  public Set<String> getPackages() {
    return packages;
  }

  public Set<Name> getUses() {
    return uses;
  }

  public Set<Name> getPublished() {
    return published;
  }

  @Override
  public String toString() {
    return name.toString();
  }
}
