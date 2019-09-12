package rocket;

import rocket.propulsion.BoosterFactory;

public final class RocketFactory {

  private static final Rocket instance =
      new Rocket(FuelTankFactory.getInstance(), BoosterFactory.getInstance());

  private RocketFactory() {
    // Static access
  }

  public static Rocket getInstance() {
    return instance;
  }
}
