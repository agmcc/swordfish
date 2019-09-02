package swordfish;

public final class NozzleFactory {

  private static final Nozzle instance = NozzleConfig.nozzle();

  private NozzleFactory() {
    // Static access
  }

  public static Nozzle getInstance() {
    return instance;
  }
}
