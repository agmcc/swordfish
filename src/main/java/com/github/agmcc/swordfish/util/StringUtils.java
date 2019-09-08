package com.github.agmcc.swordfish.util;

public final class StringUtils {

  private StringUtils() {
    /* Static access */
  }

  public static String toCamelCase(final String str) {
    if (str == null || str.length() == 0) {
      return str;
    }

    final String first = str.substring(0, 1);
    return str.replace(first, first.toLowerCase());
  }
}
