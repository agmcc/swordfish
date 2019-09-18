package multi.api;

import javax.inject.Inject;
import javax.inject.Named;
import multi.data.Database;

@Named
public class Controller {

  @Inject
  public Controller(final ExceptionHandler exceptionHandler, final Database database) {}
}
