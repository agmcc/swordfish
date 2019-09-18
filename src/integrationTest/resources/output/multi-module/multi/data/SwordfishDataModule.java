package multi.data;

public final class SwordfishDataModule {

  private SwordfishDataModule() {
    // Static access
  }

  public static Database database() {
    return DatabaseFactory.getInstance();
  }
}
