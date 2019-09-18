package multi.api;

import multi.data.DatabaseFactory;

public final class ControllerFactory {

  private static final Controller instance =
      new Controller(ExceptionHandlerFactory.getInstance(), DatabaseFactory.getInstance());

  private ControllerFactory() {
    // Static access
  }

  public static Controller getInstance() {
    return instance;
  }
}
