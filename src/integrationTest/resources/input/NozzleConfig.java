package swordfish;

import javax.inject.Named;

public class NozzleConfig {

  @Named
  public static Nozzle nozzle() {
    return new Nozzle();
  }
}
