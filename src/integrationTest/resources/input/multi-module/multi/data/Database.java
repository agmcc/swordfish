package multi.data;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class Database {

  @Inject
  public Database(final DataConnection connection) {}
}
