package com.github.agmcc.swordfish;

import multi.api.ApiModule;
import multi.api.Controller;
import multi.api.ControllerFactory;

class ApiModuleImpl implements ApiModule {

  @Override
  public Controller controller() {
    return ControllerFactory.getInstance();
  }
}
