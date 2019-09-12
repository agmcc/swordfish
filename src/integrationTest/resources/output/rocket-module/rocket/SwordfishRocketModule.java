package rocket;

public final class SwordfishRocketModule {

  private SwordfishRocketModule() {
    // Static access
  }

  public static Rocket rocket() {
    return RocketFactory.getInstance();
  }
}
