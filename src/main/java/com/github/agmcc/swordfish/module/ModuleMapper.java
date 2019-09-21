package com.github.agmcc.swordfish.module;

import com.github.agmcc.swordfish.annotation.Module;
import com.github.agmcc.swordfish.domain.ModuleElement;
import com.github.agmcc.swordfish.domain.Name;
import com.github.agmcc.swordfish.util.StringUtils;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.util.Types;

public class ModuleMapper {

  private final Types types;

  public ModuleMapper(final Types types) {
    this.types = types;
  }

  ModuleElement mapModuleElement(final Element element) {
    validateModuleElement(element);

    final TypeElement typeElement = (TypeElement) element;

    final Module module = typeElement.getAnnotation(Module.class);

    final Name name = Name.from(typeElement.getQualifiedName().toString());
    final Set<String> packages = new HashSet<>(Arrays.asList(module.packages()));
    final Set<Name> uses = mapUses(module);
    final Set<Name> published = mapPublished(element.getEnclosedElements());

    return new ModuleElement(name, packages, uses, published);
  }

  private void validateModuleElement(final Element moduleElement) {
    final ElementKind kind = moduleElement.getKind();

    if (kind != ElementKind.INTERFACE) {
      throw new RuntimeException(
          String.format("Invalid module kind: %s (expected %s)", kind, ElementKind.INTERFACE));
    }

    if (!moduleElement.getModifiers().contains(Modifier.PUBLIC)) {
      throw new RuntimeException("Modules must be public");
    }

    if (((TypeElement) moduleElement).getNestingKind().isNested()) {
      throw new RuntimeException("Modules cannot be nested");
    }
  }

  private Set<Name> mapUses(final Module module) {
    try {
      return Arrays.stream(module.uses())
          .map(c -> Name.from(c.getCanonicalName()))
          .collect(Collectors.toSet());
    } catch (final MirroredTypesException e) {
      return e.getTypeMirrors().stream()
          .map(types::asElement)
          .map(element -> Name.from(element.toString()))
          .collect(Collectors.toSet());
    }
  }

  private Set<Name> mapPublished(final List<? extends Element> enclosedElements) {

    return enclosedElements.stream()
        .filter(e -> e.getKind() == ElementKind.METHOD)
        .filter(e -> e.getModifiers().contains(Modifier.PUBLIC))
        .map(ExecutableElement.class::cast)
        .map(
            e -> {
              Name name = Name.from(e.getReturnType().toString());
              String expectedMethod = StringUtils.toCamelCase(name.getSimpleName());
              final String actualMethod = e.getSimpleName().toString();
              if (!actualMethod.equals(expectedMethod)) {
                throw new RuntimeException(
                    String.format(
                        "Invalid published method name '%s' (expected '%s') in %s",
                        actualMethod, expectedMethod, e.getEnclosingElement().toString()));
              }
              return name;
            })
        .collect(Collectors.toSet());
  }
}
