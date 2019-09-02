package swordfish;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class InkCartridge {

  @Inject
  public InkCartridge(final Ink ink, final Nozzle Nozzle) {}
}
