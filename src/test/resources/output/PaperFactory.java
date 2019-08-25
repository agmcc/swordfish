package swordfish;

public final class PaperFactory {

  private static final Paper instance =
      PaperConfigFactory.getInstance().paper(PaperSizeFactory.getInstance());

  public static Paper getInstance() {
    return instance;
  }
}
