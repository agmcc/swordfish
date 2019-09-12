package com.github.agmcc.swordfish;

import swordfish.Ink;
import swordfish.InkCartridge;
import swordfish.InkCartridgeFactory;
import swordfish.InkFactory;
import swordfish.Nozzle;
import swordfish.NozzleFactory;
import swordfish.Paper;
import swordfish.PaperConfig;
import swordfish.PaperConfigFactory;
import swordfish.PaperFactory;
import swordfish.PaperSize;
import swordfish.PaperSizeFactory;
import swordfish.Printer;
import swordfish.PrinterFactory;

public final class SwordfishDefaultModule {

  private SwordfishDefaultModule() {
    // Static access
  }

  public static Ink ink() {
    return InkFactory.getInstance();
  }

  public static InkCartridge inkCartridge() {
    return InkCartridgeFactory.getInstance();
  }

  public static Nozzle nozzle() {
    return NozzleFactory.getInstance();
  }

  public static Paper paper() {
    return PaperFactory.getInstance();
  }

  public static PaperConfig paperConfig() {
    return PaperConfigFactory.getInstance();
  }

  public static PaperSize paperSize() {
    return PaperSizeFactory.getInstance();
  }

  public static Printer printer() {
    return PrinterFactory.getInstance();
  }
}
