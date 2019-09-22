package com.github.agmcc.swordfish;

import swordfish.Paper;

final class PaperFactory {

  private static final Paper instance =
      PaperConfigFactory.getInstance().paper(PaperSizeFactory.getInstance());

  private PaperFactory() {
    // Static access
  }

  static Paper getInstance() {
    return instance;
  }
}
