package swordfish;

public final class InkCartridgeFactory {

  private static final InkCartridge instance = new InkCartridge(InkFactory.getInstance());

  public static InkCartridge getInstance() {
    return instance;
  }
}
