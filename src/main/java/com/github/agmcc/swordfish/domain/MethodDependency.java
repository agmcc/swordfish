package com.github.agmcc.swordfish.domain;

public class MethodDependency implements Dependency {

  private int argIndex;

  private String methodName;

  private String providerQualifiedName;

  public MethodDependency(
      final int argIndex, final String methodName, final String providerQualifiedName) {
    this.argIndex = argIndex;
    this.methodName = methodName;
    this.providerQualifiedName = providerQualifiedName;
  }

  public int getArgIndex() {
    return argIndex;
  }

  public String getMethodName() {
    return methodName;
  }

  public String getProviderQualifiedName() {
    return providerQualifiedName;
  }

  @Override
  public String toString() {
    return "MethodDependency{"
        + "argIndex="
        + argIndex
        + ", methodName='"
        + methodName
        + '\''
        + ", providerQualifiedName='"
        + providerQualifiedName
        + '\''
        + '}';
  }
}
