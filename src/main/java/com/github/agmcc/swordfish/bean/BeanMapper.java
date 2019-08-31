package com.github.agmcc.swordfish.bean;

import com.github.agmcc.swordfish.domain.Bean;
import com.github.agmcc.swordfish.domain.Name;
import com.github.agmcc.swordfish.inject.ConstructorInjector;
import com.github.agmcc.swordfish.inject.StaticProviderInjector;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.TypeElement;

public class BeanMapper {

  Bean mapBean(final Element namedElement) {
    switch (namedElement.getKind()) {
      case CLASS:
        return mapClassElement((TypeElement) namedElement);
      case METHOD:
        return mapMethodElement((ExecutableElement) namedElement);
      default:
        throw new RuntimeException("Unsupported @Named kind: " + namedElement.getKind());
    }
  }

  private Bean mapClassElement(final TypeElement classElement) {
    final Set<Modifier> classModifiers = classElement.getModifiers();
    if (!classModifiers.contains(Modifier.PUBLIC)) {
      throw new RuntimeException("Named classes must be public");
    }
    if (classModifiers.contains(Modifier.ABSTRACT)) {
      throw new RuntimeException("Named classes cannot be abstract");
    }

    final ExecutableElement constructorElement = getBeanConstructor(classElement);
    final List<Name> constructorArgs = getArgumentTypes(constructorElement);

    // TODO: Hardcoded to constructor injection
    return new Bean(Name.from(classElement.toString()), new ConstructorInjector(constructorArgs));
  }

  private Bean mapMethodElement(final ExecutableElement methodElement) {
    final Set<Modifier> classModifiers = methodElement.getModifiers();
    if (!classModifiers.contains(Modifier.PUBLIC)) {
      throw new RuntimeException("Named methods must be public");
    }
    if (classModifiers.contains(Modifier.ABSTRACT)) {
      throw new RuntimeException("Named methods cannot be abstract");
    }
    if (classModifiers.contains(Modifier.STATIC)) {
      throw new RuntimeException("Named methods cannot be static");
    }

    final List<Name> methodArgs = getArgumentTypes(methodElement);

    final Element enclosingElement = methodElement.getEnclosingElement();
    final Set<Modifier> enclosingElementModifiers = enclosingElement.getModifiers();
    if (enclosingElement.getKind() != ElementKind.CLASS) {
      throw new RuntimeException("Provider method must be declared in a class");
    }
    if (!enclosingElementModifiers.contains(Modifier.PUBLIC)) {
      throw new RuntimeException("Named classes must be public");
    }
    if (enclosingElementModifiers.contains(Modifier.ABSTRACT)) {
      throw new RuntimeException("Named classes cannot be abstract");
    }
    if (enclosingElement.getAnnotation(Named.class) == null) {
      throw new RuntimeException("Provider method must be declared in a Named class");
    }
    final TypeElement enclosingTypeElement = (TypeElement) enclosingElement;
    if (enclosingTypeElement.getNestingKind() != NestingKind.TOP_LEVEL) {
      throw new RuntimeException("Provider method classes cannot be declared in inner classes");
    }

    // TODO: Hardcoded to static method provider injection
    return new Bean(
        Name.from(methodElement.getReturnType().toString()),
        new StaticProviderInjector(
            Name.from(enclosingElement.toString()),
            methodElement.getSimpleName().toString(),
            methodArgs));
  }

  private List<Name> getArgumentTypes(final ExecutableElement executableElement) {
    return executableElement.getParameters().stream()
        .map(e -> Name.from(e.asType().toString()))
        .collect(Collectors.toList());
  }

  private ExecutableElement getBeanConstructor(final TypeElement classElement) {
    final List<ExecutableElement> constructors =
        classElement.getEnclosedElements().stream()
            .filter(e -> (e.getKind() == ElementKind.CONSTRUCTOR))
            .map(e -> (ExecutableElement) e)
            .collect(Collectors.toList());

    if (hasDefaultConstructor(constructors)) {
      return constructors.get(0);
    } else {
      return getInjectConstructor(constructors, classElement);
    }
  }

  private boolean hasDefaultConstructor(final List<ExecutableElement> constructors) {
    if (constructors.size() == 1) {
      final ExecutableElement constructor = constructors.get(0);
      return constructor.getParameters().isEmpty()
          && constructor.getModifiers().contains(Modifier.PUBLIC);
    } else {
      return false;
    }
  }

  private ExecutableElement getInjectConstructor(
      final List<ExecutableElement> constructors, final TypeElement classElement) {
    final List<ExecutableElement> injectConstructors =
        constructors.stream()
            .filter(e -> e.getAnnotation(Inject.class) != null)
            .filter(e -> e.getModifiers().contains(Modifier.PUBLIC))
            .collect(Collectors.toList());

    if (injectConstructors.isEmpty()) {
      throw new RuntimeException("Missing public, @Inject constructor for bean: " + classElement);
    } else if (injectConstructors.size() > 1) {
      throw new RuntimeException("Multiple @Inject constructors present for bean: " + classElement);
    } else {
      return injectConstructors.get(0);
    }
  }
}
