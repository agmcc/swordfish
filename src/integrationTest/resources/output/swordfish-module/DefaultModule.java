package com.github.agmcc.swordfish;

import swordfish.Ink;
import swordfish.InkCartridge;
import swordfish.Nozzle;
import swordfish.Paper;
import swordfish.PaperConfig;
import swordfish.PaperSize;
import swordfish.Printer;

public interface DefaultModule {

  Ink ink();

  InkCartridge inkCartridge();

  Nozzle nozzle();

  Paper paper();

  PaperConfig paperConfig();

  PaperSize paperSize();

  Printer printer();
}
