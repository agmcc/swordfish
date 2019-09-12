package com.github.agmcc.swordfish.module;

import com.github.agmcc.swordfish.domain.Bean;
import com.github.agmcc.swordfish.domain.Module;
import com.github.agmcc.swordfish.domain.Name;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

public class ModuleMapper {

  Module mapModule(final Element moduleElement, final Set<Bean> beans) {

    final ElementKind kind = moduleElement.getKind();

    if (kind != ElementKind.INTERFACE) {
      throw new RuntimeException(
          String.format("Invalid module kind: %s (expected %s)", kind, ElementKind.INTERFACE));
    }

    if (!moduleElement.getModifiers().contains(Modifier.PUBLIC)) {
      throw new RuntimeException("Modules must be public");
    }

    final TypeElement typeElement = (TypeElement) moduleElement;

    if (typeElement.getNestingKind().isNested()) {
      throw new RuntimeException("Modules cannot be nested");
    }

    final com.github.agmcc.swordfish.annotation.Module moduleAnnotation =
        typeElement.getAnnotation(com.github.agmcc.swordfish.annotation.Module.class);

    final Set<Bean> moduleBeans = getModuleBeans(moduleAnnotation, beans);

    final Set<Bean> published = getPublishedBeans(typeElement.getEnclosedElements(), moduleBeans);

    final Name moduleName = Name.from(typeElement.getQualifiedName().toString());
    return new com.github.agmcc.swordfish.domain.Module(
        Name.from(
            new StringJoiner(".")
                .add(moduleName.getPackageName())
                .add("Swordfish".concat(moduleName.getSimpleName()))
                .toString()),
        moduleBeans,
        published);
  }

  private Set<Bean> getModuleBeans(
      final com.github.agmcc.swordfish.annotation.Module module, final Set<Bean> beans) {

    final List<String> packages = Arrays.asList(module.packages());

    return beans.stream().filter(b -> isIncluded(b, packages)).collect(Collectors.toSet());
  }

  private boolean isIncluded(final Bean bean, final List<String> packages) {
    return packages.contains(bean.getName().getPackageName());
  }

  private Set<Bean> getPublishedBeans(
      final List<? extends Element> enclosedElements, final Set<Bean> moduleBeans) {

    return enclosedElements.stream()
        .filter(e -> e.getKind() == ElementKind.METHOD)
        .filter(e -> e.getModifiers().contains(Modifier.PUBLIC))
        .map(ExecutableElement.class::cast)
        .map(e -> lookupBean(e, moduleBeans))
        .collect(Collectors.toSet());
  }

  private Bean lookupBean(final ExecutableElement executableElement, final Set<Bean> moduleBeans) {

    final Name publishedName = Name.from(executableElement.getReturnType().toString());

    return moduleBeans.stream()
        .filter(b -> b.getName().equals(publishedName))
        .findAny()
        .orElseThrow(
            () ->
                new RuntimeException(
                    String.format("Published bean %s not found in module scope", publishedName)));
  }
}
