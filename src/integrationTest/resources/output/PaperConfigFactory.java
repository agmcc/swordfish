package swordfish;

public final class PaperConfigFactory {

  private static final PaperConfig instance = new PaperConfig();

  public static PaperConfig getInstance() {
    return instance;
  }
}
