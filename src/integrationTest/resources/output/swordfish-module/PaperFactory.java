package swordfish;

public final class PaperFactory {

  private static final Paper instance =
      PaperConfigFactory.getInstance().paper(PaperSizeFactory.getInstance());

  private PaperFactory() {
    // Static access
  }

  public static Paper getInstance() {
    return instance;
  }
}
