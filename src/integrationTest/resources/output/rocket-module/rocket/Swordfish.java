package com.github.agmcc.swordfish;

import rocket.RocketModule;

public final class Swordfish {

  private Swordfish() {
    // Static access
  }

  public static RocketModule rocketModule() {
    return RocketModuleFactory.getInstance();
  }
}
