package com.github.agmcc.swordfish;

import multi.data.DataModule;
import multi.data.Database;
import multi.data.DatabaseFactory;

class DataModuleImpl implements DataModule {

  @Override
  public Database database() {
    return DatabaseFactory.getInstance();
  }
}
