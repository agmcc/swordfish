package com.github.agmcc.swordfish.domain;

public class ConstructorDependency implements Dependency {

  private int argIndex;

  public ConstructorDependency(final int argIndex) {
    this.argIndex = argIndex;
  }

  public int getArgIndex() {
    return argIndex;
  }

  @Override
  public String toString() {
    return "ConstructorDependency{" + "argIndex=" + argIndex + '}';
  }
}
