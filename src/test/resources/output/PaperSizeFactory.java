package swordfish;

import javax.annotation.processing.Generated;

@Generated("com.github.agmcc.swordfish.processor.Processor")
public final class PaperSizeFactory {

  private static final PaperSize instance = swordfish.PaperConfigFactory.getInstance().paperSize();

  public static PaperSize getInstance() {
    return instance;
  }
}
