package com.github.agmcc.swordfish;

import multi.api.ExceptionHandler;

final class ExceptionHandlerFactory {

  private static final ExceptionHandler instance =
      new ExceptionHandler(LoggerFactory.getInstance());

  private ExceptionHandlerFactory() {
    // Static access
  }

  static ExceptionHandler getInstance() {
    return instance;
  }
}
