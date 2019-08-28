package com.github.agmcc.swordfish.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class NameTest {

  private static Stream<Arguments> equalsProvider() {
    return Stream.of(
        arguments(Name.from("Rocket"), Name.from("Rocket"), true),
        arguments(Name.from("Rocket"), Name.from("other.Rocket"), false),
        arguments(Name.from("a.Rocket"), Name.from("b.Rocket"), false),
        arguments(Name.from("example.Car"), Name.from("example.Bike"), false));
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {" ", "  "})
  void from_qualifiedNameNullOrEmptyOrBlank_throws(final String qualifiedName) {
    assertThrows(IllegalArgumentException.class, () -> Name.from(qualifiedName));
  }

  @ParameterizedTest
  @CsvSource({"message.Greeting,message,Greeting", "com.example.Foo,com.example,Foo", "Car,'',Car"})
  void from_qualifiedNameProvider_created(
      final String qualifiedName, final String packageName, final String simpleName) {
    // Given When
    final Name name = Name.from(qualifiedName);

    // Then
    assertEquals(packageName, name.getPackageName());
    assertEquals(simpleName, name.getSimpleName());
  }

  @ParameterizedTest
  @MethodSource("equalsProvider")
  void equals_equalsProvider_expected(final Name a, final Name b, final boolean expected) {
    assertEquals(expected, a.equals(b));
  }
}
