package com.github.agmcc.swordfish;

public final class Swordfish {

  private Swordfish() {
    // Static access
  }

  public static DefaultModule defaultModule() {
    return DefaultModuleFactory.getInstance();
  }
}
