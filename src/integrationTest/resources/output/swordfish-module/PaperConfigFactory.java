package com.github.agmcc.swordfish;

import swordfish.PaperConfig;

final class PaperConfigFactory {

  private static final PaperConfig instance = new PaperConfig();

  private PaperConfigFactory() {
    // Static access
  }

  static PaperConfig getInstance() {
    return instance;
  }
}
