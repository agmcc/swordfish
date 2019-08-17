package swordfish;

import javax.annotation.processing.Generated;

@Generated("com.github.agmcc.swordfish.processor.Processor")
public final class InkCartridgeFactory {

  private static final InkCartridge instance = new InkCartridge(InkFactory.getInstance());

  public static InkCartridge getInstance() {
    return instance;
  }
}
