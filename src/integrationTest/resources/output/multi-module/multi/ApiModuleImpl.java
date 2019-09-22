package com.github.agmcc.swordfish;

import multi.api.ApiModule;
import multi.api.Controller;

class ApiModuleImpl implements ApiModule {

  @Override
  public Controller controller() {
    return ControllerFactory.getInstance();
  }
}
