package com.github.agmcc.swordfish;

import swordfish.PaperSize;

final class PaperSizeFactory {

  private static final PaperSize instance = PaperConfigFactory.getInstance().paperSize();

  private PaperSizeFactory() {
    // Static access
  }

  static PaperSize getInstance() {
    return instance;
  }
}
