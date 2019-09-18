package multi.data;

public final class DatabaseFactory {

  private static final Database instance = new Database(DataConnectionFactory.getInstance());

  private DatabaseFactory() {
    // Static access
  }

  public static Database getInstance() {
    return instance;
  }
}
