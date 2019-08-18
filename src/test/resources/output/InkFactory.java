package swordfish;

public final class InkFactory {

  private static final Ink instance = new Ink();

  public static Ink getInstance() {
    return instance;
  }
}
