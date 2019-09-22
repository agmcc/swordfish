package com.github.agmcc.swordfish;

import swordfish.Ink;
import swordfish.InkCartridge;
import swordfish.Nozzle;
import swordfish.Paper;
import swordfish.PaperConfig;
import swordfish.PaperSize;
import swordfish.Printer;

class DefaultModuleImpl implements DefaultModule {

  @Override
  public Ink ink() {
    return InkFactory.getInstance();
  }

  @Override
  public InkCartridge inkCartridge() {
    return InkCartridgeFactory.getInstance();
  }

  @Override
  public Nozzle nozzle() {
    return NozzleFactory.getInstance();
  }

  @Override
  public Paper paper() {
    return PaperFactory.getInstance();
  }

  @Override
  public PaperConfig paperConfig() {
    return PaperConfigFactory.getInstance();
  }

  @Override
  public PaperSize paperSize() {
    return PaperSizeFactory.getInstance();
  }

  @Override
  public Printer printer() {
    return PrinterFactory.getInstance();
  }
}
