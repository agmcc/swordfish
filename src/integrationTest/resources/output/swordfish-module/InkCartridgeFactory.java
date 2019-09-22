package com.github.agmcc.swordfish;

import swordfish.InkCartridge;

final class InkCartridgeFactory {

  private static final InkCartridge instance =
      new InkCartridge(InkFactory.getInstance(), NozzleFactory.getInstance());

  private InkCartridgeFactory() {
    // Static access
  }

  static InkCartridge getInstance() {
    return instance;
  }
}
