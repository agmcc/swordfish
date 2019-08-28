package swordfish;

public final class PrinterFactory {

  private static final Printer instance =
      new Printer(InkCartridgeFactory.getInstance(), PaperFactory.getInstance());

  private PrinterFactory() {
    // Static access
  }

  public static Printer getInstance() {
    return instance;
  }
}
