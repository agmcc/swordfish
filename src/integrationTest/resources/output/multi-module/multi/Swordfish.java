package com.github.agmcc.swordfish;

import multi.api.ApiModule;
import multi.data.DataModule;

public final class Swordfish {

  private Swordfish() {
    // Static access
  }

  public static ApiModule apiModule() {
    return ApiModuleFactory.getInstance();
  }

  public static DataModule dataModule() {
    return DataModuleFactory.getInstance();
  }
}
