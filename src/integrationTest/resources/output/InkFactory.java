package swordfish;

public final class InkFactory {

  private static final Ink instance = new Ink();

  private InkFactory() {
    // Static access
  }

  public static Ink getInstance() {
    return instance;
  }
}
