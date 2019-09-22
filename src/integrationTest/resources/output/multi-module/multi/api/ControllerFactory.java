package com.github.agmcc.swordfish;

import multi.api.Controller;

final class ControllerFactory {

  private static final Controller instance =
      new Controller(ExceptionHandlerFactory.getInstance(), DatabaseFactory.getInstance());

  private ControllerFactory() {
    // Static access
  }

  static Controller getInstance() {
    return instance;
  }
}
