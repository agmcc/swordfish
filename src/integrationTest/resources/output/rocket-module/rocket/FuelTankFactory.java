package com.github.agmcc.swordfish;

import rocket.FuelTank;

final class FuelTankFactory {

  private static final FuelTank instance = new FuelTank();

  private FuelTankFactory() {
    // Static access
  }

  static FuelTank getInstance() {
    return instance;
  }
}
