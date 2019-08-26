package com.github.agmcc.swordfish.domain;

import java.util.Objects;

public class Name {

  private final String qualifiedName;

  private final String simpleName;

  private final String packageName;

  public Name(final String qualifiedName) {
    if (qualifiedName == null || qualifiedName.isEmpty() || qualifiedName.trim().isEmpty()) {
      throw new IllegalArgumentException("Name cannot be empty");
    }

    this.qualifiedName = qualifiedName;

    final int separator = qualifiedName.lastIndexOf(".");

    if (separator == -1) {
      simpleName = qualifiedName;
      packageName = "";
    } else {
      simpleName = qualifiedName.substring(separator + 1);
      packageName = qualifiedName.substring(0, separator);
    }
  }

  public String getSimpleName() {
    return simpleName;
  }

  public String getPackageName() {
    return packageName;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final Name name = (Name) o;
    return qualifiedName.equals(name.qualifiedName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(qualifiedName);
  }

  @Override
  public String toString() {
    return qualifiedName;
  }
}
