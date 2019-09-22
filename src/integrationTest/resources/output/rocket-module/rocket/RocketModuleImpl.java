package com.github.agmcc.swordfish;

import rocket.Rocket;
import rocket.RocketModule;

class RocketModuleImpl implements RocketModule {

  @Override
  public Rocket rocket() {
    return RocketFactory.getInstance();
  }
}
