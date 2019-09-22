package com.github.agmcc.swordfish;

import swordfish.Nozzle;
import swordfish.NozzleConfig;

final class NozzleFactory {

  private static final Nozzle instance = NozzleConfig.nozzle();

  private NozzleFactory() {
    // Static access
  }

  static Nozzle getInstance() {
    return instance;
  }
}
