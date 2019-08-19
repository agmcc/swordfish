package com.github.agmcc.swordfish.factory;

import com.github.agmcc.swordfish.domain.Bean;
import com.github.agmcc.swordfish.domain.BeanType;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public class FactoryBuilder {

  private final Elements elementUtils;

  private final Types typeUtils;

  public FactoryBuilder(final Elements elementUtils, final Types typeUtils) {
    this.elementUtils = elementUtils;
    this.typeUtils = typeUtils;
  }

  public JavaFile createFactory(final Bean currentBean, final List<Bean> allBeans) {
    final Name type = typeUtils.asElement(currentBean.getType()).getSimpleName();
    final Name packageName =
        elementUtils.getPackageOf(typeUtils.asElement(currentBean.getType())).getQualifiedName();

    final String creator;

    if (currentBean.getBeanType() == BeanType.CLASS) {
      // Class bean - invoke constructor with factories args
      final String args =
          currentBean.getDependencies().stream()
              .map(
                  t -> {
                    final Bean dependency =
                        allBeans.stream()
                            .filter(b -> b.getType().equals(t))
                            .findAny()
                            .orElseThrow(
                                () ->
                                    new RuntimeException(
                                        String.format(
                                            "Missing dependency for %s: %s", currentBean, t)));

                    return String.format(
                        "%sFactory.getInstance()",
                        typeUtils.asElement(dependency.getType()).getSimpleName());
                  })
              .collect(Collectors.joining(", "));

      creator = String.format("new %s(%s)", type, args);
    } else {
      // Provided bean - call method on provider method
      final ExecutableElement provider =
          allBeans.stream()
              .map(Bean::getProviders)
              .flatMap(List::stream)
              .filter(p -> p.getReturnType().equals(currentBean.getType()))
              .findAny()
              .orElseThrow(NoSuchElementException::new);

      final String args =
          provider.getParameters().stream()
              .map(Element::asType)
              .map(
                  t -> {
                    final Bean dependency =
                        allBeans.stream()
                            .filter(b -> b.getType().equals(t))
                            .findAny()
                            .orElseThrow(NoSuchElementException::new);

                    return String.format(
                        "%sFactory.getInstance()",
                        typeUtils.asElement(dependency.getType()).getSimpleName());
                  })
              .collect(Collectors.joining(", "));

      creator =
          String.format(
              "%sFactory.getInstance().%s(%s)",
              provider.getEnclosingElement(), provider.getSimpleName(), args);
    }

    final FieldSpec instance =
        FieldSpec.builder(TypeName.get(currentBean.getType()), "instance")
            .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
            .initializer(creator)
            .build();

    final MethodSpec getInstance =
        MethodSpec.methodBuilder("getInstance")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(TypeName.get(currentBean.getType()))
            .addStatement("return instance")
            .build();

    final TypeSpec factory =
        TypeSpec.classBuilder(type.toString().concat("Factory"))
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addField(instance)
            .addMethod(getInstance)
            .build();

    return JavaFile.builder(packageName.toString(), factory).build();
  }
}
