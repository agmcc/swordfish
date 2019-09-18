package multi.data;

public final class DataConnectionFactory {

  private static final DataConnection instance = new DataConnection();

  private DataConnectionFactory() {
    // Static access
  }

  public static DataConnection getInstance() {
    return instance;
  }
}
