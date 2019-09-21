package com.github.agmcc.swordfish;

import multi.data.DataModule;

final class DataModuleFactory {

  private static final DataModule instance = new DataModuleImpl();

  private DataModuleFactory() {
    // Static access
  }

  static DataModule getInstance() {
    return instance;
  }
}
