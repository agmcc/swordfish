package com.github.agmcc.swordfish;

import multi.data.Database;

final class DatabaseFactory {

  private static final Database instance = new Database(DataConnectionFactory.getInstance());

  private DatabaseFactory() {
    // Static access
  }

  static Database getInstance() {
    return instance;
  }
}
