package swordfish;

import javax.annotation.processing.Generated;

@Generated("com.github.agmcc.swordfish.processor.Processor")
public final class PrinterFactory {

  private static final Printer instance =
      new Printer(InkCartridgeFactory.getInstance(), PaperFactory.getInstance());

  public static Printer getInstance() {
    return instance;
  }
}
