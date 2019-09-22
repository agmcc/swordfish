package com.github.agmcc.swordfish;

import multi.api.Logger;

final class LoggerFactory {

  private static final Logger instance = new Logger();

  private LoggerFactory() {
    // Static access
  }

  static Logger getInstance() {
    return instance;
  }
}
