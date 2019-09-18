package multi.api;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class ExceptionHandler {

  @Inject
  public ExceptionHandler(final Logger logger) {}
}
