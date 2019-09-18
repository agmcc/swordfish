package multi.api;

public final class ExceptionHandlerFactory {

  private static final ExceptionHandler instance =
      new ExceptionHandler(LoggerFactory.getInstance());

  private ExceptionHandlerFactory() {
    // Static access
  }

  public static ExceptionHandler getInstance() {
    return instance;
  }
}
