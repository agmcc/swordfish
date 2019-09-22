package com.github.agmcc.swordfish;

import swordfish.Printer;

final class PrinterFactory {

  private static final Printer instance =
      new Printer(InkCartridgeFactory.getInstance(), PaperFactory.getInstance());

  private PrinterFactory() {
    // Static access
  }

  static Printer getInstance() {
    return instance;
  }
}
