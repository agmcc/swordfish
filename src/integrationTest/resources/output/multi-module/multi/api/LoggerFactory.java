package multi.api;

public final class LoggerFactory {

  private static final Logger instance = new Logger();

  private LoggerFactory() {
    // Static access
  }

  public static Logger getInstance() {
    return instance;
  }
}
