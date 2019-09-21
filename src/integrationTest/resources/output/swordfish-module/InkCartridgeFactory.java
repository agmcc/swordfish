package swordfish;

public final class InkCartridgeFactory {

  private static final InkCartridge instance =
      new InkCartridge(InkFactory.getInstance(), NozzleFactory.getInstance());

  private InkCartridgeFactory() {
    // Static access
  }

  public static InkCartridge getInstance() {
    return instance;
  }
}