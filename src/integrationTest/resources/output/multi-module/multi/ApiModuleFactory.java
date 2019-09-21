package com.github.agmcc.swordfish;

import multi.api.ApiModule;

final class ApiModuleFactory {

  private static final ApiModule instance = new ApiModuleImpl();

  private ApiModuleFactory() {
    // Static access
  }

  static ApiModule getInstance() {
    return instance;
  }
}
