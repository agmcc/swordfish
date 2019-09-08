package com.github.agmcc.swordfish.domain;

import java.util.Objects;

public class Name implements Comparable<Name> {

  private final String qualifiedName;

  private final String simpleName;

  private final String packageName;

  private Name(final String qualifiedName, final String simpleName, final String packageName) {
    this.qualifiedName = qualifiedName;
    this.simpleName = simpleName;
    this.packageName = packageName;
  }

  public static Name from(final String qualifiedName) {
    if (qualifiedName == null || qualifiedName.isEmpty() || qualifiedName.trim().isEmpty()) {
      throw new IllegalArgumentException("Name cannot be empty");
    }

    final String simpleName;
    final String packageName;

    final int separator = qualifiedName.lastIndexOf('.');

    if (separator == -1) {
      simpleName = qualifiedName;
      packageName = "";
    } else {
      simpleName = qualifiedName.substring(separator + 1);
      packageName = qualifiedName.substring(0, separator);
    }

    return new Name(qualifiedName, simpleName, packageName);
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
  public int compareTo(final Name o) {
    return qualifiedName.compareTo(o.qualifiedName);
  }

  @Override
  public String toString() {
    return qualifiedName;
  }
}
