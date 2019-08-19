package com.github.agmcc.swordfish.bean;

import com.github.agmcc.swordfish.domain.Bean;
import com.github.agmcc.swordfish.domain.BeanType;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.RoundEnvironment;
import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

public class BeanLoader {

  public List<Bean> loadBeans(final RoundEnvironment roundEnv) {
    final List<Bean> beans = new ArrayList<>();

    // Load Named beans
    for (final Element namedElement : roundEnv.getElementsAnnotatedWith(Named.class)) {
      if (namedElement.getKind() == ElementKind.CLASS) {
        final TypeElement typeElement = (TypeElement) namedElement;

        final Bean bean = new Bean(typeElement.asType(), BeanType.CLASS);

        for (final Element enclosedElement : typeElement.getEnclosedElements()) {
          final Set<Modifier> modifiers = enclosedElement.getModifiers();

          // Find public, @Inject-annotated constructor
          if (enclosedElement.getKind() == ElementKind.CONSTRUCTOR
              && enclosedElement.getAnnotation(Inject.class) != null
              && modifiers.contains(Modifier.PUBLIC)) {

            final ExecutableElement constructorElement = (ExecutableElement) enclosedElement;

            // Add constructor dependencies
            for (final VariableElement parameter : constructorElement.getParameters()) {
              bean.addDependency(parameter.asType());
            }
          }

          // Method providers - must be @Named-annotated, public and static
          if (enclosedElement.getKind() == ElementKind.METHOD
              && enclosedElement.getAnnotation(Named.class) != null) {

            final ExecutableElement methodElement = (ExecutableElement) enclosedElement;
            bean.addProvider(methodElement);

            final Bean provided = new Bean(methodElement.getReturnType(), BeanType.PROVIDED);
            beans.add(provided);
          }
        }
        beans.add(bean);
      }
    }
    return beans;
  }
}
