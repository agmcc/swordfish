package com.github.agmcc.swordfish;

import rocket.RocketModule;

final class RocketModuleFactory {

  private static final RocketModule instance = new RocketModuleImpl();

  private RocketModuleFactory() {
    // Static access
  }

  static RocketModule getInstance() {
    return instance;
  }
}
