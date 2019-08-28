package swordfish;

public final class PaperSizeFactory {

  private static final PaperSize instance = PaperConfigFactory.getInstance().paperSize();

  private PaperSizeFactory() {
    // Static access
  }

  public static PaperSize getInstance() {
    return instance;
  }
}
