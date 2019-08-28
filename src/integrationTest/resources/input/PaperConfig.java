package swordfish;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class PaperConfig {

  @Inject
  public PaperConfig() {}

  @Named
  public PaperSize paperSize() {
    return PaperSize.A4;
  }

  @Named
  public Paper paper(final PaperSize paperSize) {
    return new Paper(paperSize);
  }
}
