package multi;

import multi.api.Controller;
import multi.api.SwordfishApiModule;

public class App {

  public static void main(final String[] args) {
    final Controller controller = SwordfishApiModule.controller();
  }
}
