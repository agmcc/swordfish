package swordfish;

final class InkFactory {

  private static final Ink instance = new Ink();

  private InkFactory() {
    // Static access
  }

  static Ink getInstance() {
    return instance;
  }
}
