package multi.api;

public final class SwordfishApiModule {

  private SwordfishApiModule() {
    // Static access
  }

  public static Controller controller() {
    return ControllerFactory.getInstance();
  }
}
