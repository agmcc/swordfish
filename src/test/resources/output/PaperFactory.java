package swordfish;

import javax.annotation.processing.Generated;

@Generated("com.github.agmcc.swordfish.processor.Processor")
public final class PaperFactory {

  private static final Paper instance =
      swordfish.PaperConfigFactory.getInstance().paper(PaperSizeFactory.getInstance());

  public static Paper getInstance() {
    return instance;
  }
}
