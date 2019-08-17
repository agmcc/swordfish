package swordfish;

import javax.annotation.processing.Generated;

@Generated("com.github.agmcc.swordfish.processor.Processor")
public final class InkFactory {

  private static final Ink instance = new Ink();

  public static Ink getInstance() {
    return instance;
  }
}
