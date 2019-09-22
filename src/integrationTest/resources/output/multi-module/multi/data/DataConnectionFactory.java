package com.github.agmcc.swordfish;

import multi.data.DataConnection;

final class DataConnectionFactory {

  private static final DataConnection instance = new DataConnection();

  private DataConnectionFactory() {
    // Static access
  }

  static DataConnection getInstance() {
    return instance;
  }
}
