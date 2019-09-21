package com.github.agmcc.swordfish;

final class DefaultModuleFactory {

  private static final DefaultModule instance = new DefaultModuleImpl();

  private DefaultModuleFactory() {
    // Static access
  }

  static DefaultModule getInstance() {
    return instance;
  }
}
