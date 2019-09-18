package com.github.agmcc.swordfish.util;

import java.util.HashSet;
import java.util.Set;

public final class SetUtils {

  private SetUtils() {
    /* Static access */
  }

  public static <T> Set<T> subtract(final Set<T> a, final Set<T> b) {
    final Set<T> diff = new HashSet<>(a);
    diff.removeAll(b);
    return diff;
  }
}
