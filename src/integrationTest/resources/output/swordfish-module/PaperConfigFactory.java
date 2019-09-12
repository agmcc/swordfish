package swordfish;

public final class PaperConfigFactory {

  private static final PaperConfig instance = new PaperConfig();

  private PaperConfigFactory() {
    // Static access
  }

  public static PaperConfig getInstance() {
    return instance;
  }
}
