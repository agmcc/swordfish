package com.github.agmcc.swordfish.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class StringUtilsTest {

  @ParameterizedTest
  @CsvSource({
    "Apple,apple",
    "apple,apple",
    "A,a",
    "a,a",
    ",",
    "' ',' '",
    "'',''",
    "' apple',' apple'",
    "'apple ','apple '",
    "' apple ',' apple '",
    "1Apple,1Apple",
    "?Apple,?Apple",
  })
  void toCamelCase_string_camelCase(final String str, final String expected) {
    assertEquals(expected, StringUtils.toCamelCase(str));
  }
}
