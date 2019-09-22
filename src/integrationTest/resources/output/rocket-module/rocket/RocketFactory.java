package com.github.agmcc.swordfish;

import rocket.Rocket;

final class RocketFactory {

  private static final Rocket instance =
      new Rocket(FuelTankFactory.getInstance(), BoosterFactory.getInstance());

  private RocketFactory() {
    // Static access
  }

  static Rocket getInstance() {
    return instance;
  }
}
