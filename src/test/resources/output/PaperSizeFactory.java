package swordfish;

public final class PaperSizeFactory {

  private static final PaperSize instance = PaperConfigFactory.getInstance().paperSize();

  public static PaperSize getInstance() {
    return instance;
  }
}
