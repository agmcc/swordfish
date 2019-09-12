package rocket;

import javax.inject.Inject;
import javax.inject.Named;
import rocket.propulsion.Booster;

@Named
public class Rocket {

  @Inject
  public Rocket(final FuelTank fuelTank, final Booster booster) {}
}
