package multi;

import com.github.agmcc.swordfish.Swordfish;
import multi.api.Controller;

public class App {

  public static void main(final String[] args) {
    final Controller controller = Swordfish.apiModule().controller();
  }
}
