package swordfish;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class Printer {

  @Inject
  public Printer(final InkCartridge inkCartridge, final Paper paper) {}
}
