package rocket.propulsion;

public final class BoosterFactory {

  private static final Booster instance = new Booster();

  private BoosterFactory() {
    // Static access
  }

  public static Booster getInstance() {
    return instance;
  }
}
