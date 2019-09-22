package com.github.agmcc.swordfish.bean;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.ElementKind.CONSTRUCTOR;
import static javax.lang.model.element.ElementKind.INTERFACE;
import static javax.lang.model.element.ElementKind.METHOD;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.agmcc.swordfish.domain.Bean;
import com.github.agmcc.swordfish.domain.Name;
import com.github.agmcc.swordfish.inject.ConstructorInjector;
import com.github.agmcc.swordfish.inject.Injector;
import com.github.agmcc.swordfish.test.ElementFake;
import com.github.agmcc.swordfish.test.ExecutableElementFake;
import com.github.agmcc.swordfish.test.TypeElementFake;
import com.github.agmcc.swordfish.test.TypeMirrorFake;
import com.github.agmcc.swordfish.test.VariableElementFake;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;
import javax.inject.Inject;
import javax.lang.model.element.Element;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class BeanMapperTest {

  private BeanMapper beanMapper = new BeanMapper();

  protected static Stream<List<? extends Element>> missingInjectProvider() {
    return Stream.of(
        // No constructors
        Collections.emptyList(),
        // 1 arg, no @Inject
        Collections.singletonList(
            ExecutableElementFake.builder()
                .parameters(Collections.singletonList(VariableElementFake.builder().build()))
                .kind(CONSTRUCTOR)
                .build()),
        // Private, no-args
        Collections.singletonList(
            ExecutableElementFake.builder().kind(CONSTRUCTOR).modifier(PRIVATE).build()));
  }

  @Test
  void mapBean_unsupportedElementKind_throws() {
    final Element namedElement = ElementFake.builder().kind(INTERFACE).build();

    assertThrows(RuntimeException.class, () -> beanMapper.mapBean(namedElement));
  }

  @Nested
  class ClassElement {

    private static final String QUALIFIED_NAME = "swordfish.Printer";

    @Test
    void mapBean_classAbstract_throws() {
      final Element classElement =
          TypeElementFake.builder()
              .kind(CLASS)
              .modifiers(new HashSet<>(Arrays.asList(PUBLIC, ABSTRACT)))
              .build();

      assertThatExceptionOfType(RuntimeException.class)
          .isThrownBy(() -> beanMapper.mapBean(classElement))
          .withMessage("Named classes cannot be abstract");
    }

    @Test
    void mapBean_classWithoutConstructorElement_throws() {
      final Element classElement =
          TypeElementFake.builder()
              .qualifiedName(QUALIFIED_NAME)
              .kind(CLASS)
              .modifier(PUBLIC)
              .enclosingElements(
                  Collections.singletonList(ElementFake.builder().kind(METHOD).build()))
              .build();

      assertThatExceptionOfType(RuntimeException.class)
          .isThrownBy(() -> beanMapper.mapBean(classElement))
          .withMessage("Missing @Inject constructor for bean: swordfish.Printer");
    }

    @Test
    void mapBean_privateClass_throws() {
      final Element classElement =
          TypeElementFake.builder()
              .qualifiedName(QUALIFIED_NAME)
              .kind(CLASS)
              .modifier(PRIVATE)
              .enclosingElements(
                  Collections.singletonList(
                      ExecutableElementFake.builder()
                          .kind(CONSTRUCTOR)
                          .modifier(PUBLIC)
                          .annotationClass(Inject.class)
                          .build()))
              .build();

      assertThatExceptionOfType(RuntimeException.class)
          .isThrownBy(() -> beanMapper.mapBean(classElement))
          .withMessage("Named classes must be public");
    }

    @Test
    void mapBean_defaultConstructor_mapped() {
      // Given
      final Element classElement =
          TypeElementFake.builder()
              .qualifiedName(QUALIFIED_NAME)
              .kind(CLASS)
              .modifier(PUBLIC)
              .enclosingElements(
                  Collections.singletonList(
                      ExecutableElementFake.builder()
                          .parameters(Collections.emptyList())
                          .kind(CONSTRUCTOR)
                          .modifier(PUBLIC)
                          .build()))
              .build();

      // When
      final Bean bean = beanMapper.mapBean(classElement);

      // Then
      assertThat(bean).isNotNull();

      final Name name = bean.getName();
      assertNotNull(name);
      assertEquals(QUALIFIED_NAME, name.toString());

      final Injector injector = bean.getInjector();
      assertThat(injector).isInstanceOf(ConstructorInjector.class);
      assertThat(injector.getDependencies()).isEmpty();
    }

    @ParameterizedTest
    @MethodSource(" com.github.agmcc.swordfish.bean.BeanMapperTest#missingInjectProvider")
    void mapBean_missingInjectConstructor_throws(final List<? extends Element> enclosingElements) {
      final Element classElement =
          TypeElementFake.builder()
              .qualifiedName(QUALIFIED_NAME)
              .kind(CLASS)
              .modifier(PUBLIC)
              .enclosingElements(enclosingElements)
              .build();

      assertThatExceptionOfType(RuntimeException.class)
          .isThrownBy(() -> beanMapper.mapBean(classElement))
          .withMessage("Missing @Inject constructor for bean: swordfish.Printer");
    }

    @Test
    void mapBean_multipleInjectConstructors_throws() {
      final Element classElement =
          TypeElementFake.builder()
              .qualifiedName(QUALIFIED_NAME)
              .kind(CLASS)
              .modifier(PUBLIC)
              .enclosingElements(
                  Arrays.asList(
                      ExecutableElementFake.builder()
                          .parameters(Collections.emptyList())
                          .kind(CONSTRUCTOR)
                          .modifier(PUBLIC)
                          .annotationClass(Inject.class)
                          .build(),
                      ExecutableElementFake.builder()
                          .parameters(Collections.emptyList())
                          .kind(CONSTRUCTOR)
                          .modifier(PUBLIC)
                          .annotationClass(Inject.class)
                          .build()))
              .build();

      assertThatExceptionOfType(RuntimeException.class)
          .isThrownBy(() -> beanMapper.mapBean(classElement))
          .withMessage("Multiple @Inject constructors present for bean: swordfish.Printer");
    }

    @Test
    void mapBean_singleInjectConstructor_mapped() {
      // Given
      final String dependencyQualifiedName = "swordfish.InkCartridge";

      final Element classElement =
          TypeElementFake.builder()
              .qualifiedName(QUALIFIED_NAME)
              .kind(CLASS)
              .modifier(PUBLIC)
              .enclosingElements(
                  Collections.singletonList(
                      ExecutableElementFake.builder()
                          .parameters(
                              Collections.singletonList(
                                  VariableElementFake.builder()
                                      .type(
                                          TypeMirrorFake.builder()
                                              .qualifiedName(dependencyQualifiedName)
                                              .build())
                                      .build()))
                          .annotationClass(Inject.class)
                          .kind(CONSTRUCTOR)
                          .modifier(PUBLIC)
                          .build()))
              .build();

      // When
      final Bean bean = beanMapper.mapBean(classElement);

      // Then
      assertThat(bean).isNotNull();

      final Name name = bean.getName();
      assertNotNull(name);
      assertEquals(QUALIFIED_NAME, name.toString());

      final Injector injector = bean.getInjector();
      assertThat(injector).isInstanceOf(ConstructorInjector.class);
      assertThat(injector.getDependencies()).contains(Name.from(dependencyQualifiedName));
    }
  }
}
