package rocket;

public final class FuelTankFactory {

  private static final FuelTank instance = new FuelTank();

  private FuelTankFactory() {
    // Static access
  }

  public static FuelTank getInstance() {
    return instance;
  }
}
