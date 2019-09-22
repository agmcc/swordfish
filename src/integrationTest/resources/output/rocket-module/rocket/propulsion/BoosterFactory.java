package com.github.agmcc.swordfish;

import rocket.propulsion.Booster;

final class BoosterFactory {

  private static final Booster instance = new Booster();

  private BoosterFactory() {
    // Static access
  }

  static Booster getInstance() {
    return instance;
  }
}
