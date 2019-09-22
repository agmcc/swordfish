package com.github.agmcc.swordfish.factory;

import com.github.agmcc.swordfish.domain.Bean;
import com.github.agmcc.swordfish.domain.Constants;
import com.github.agmcc.swordfish.domain.Module;
import com.github.agmcc.swordfish.domain.Name;
import com.github.agmcc.swordfish.util.StringUtils;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.lang.model.element.Modifier;

public class ModuleBuilder {

  public JavaFile createModule(final Module module) {

    final Name name = module.getName();

    final List<MethodSpec> beanMethods = getBeanMethods(module.getPublished());

    final ClassName moduleClassName = ClassName.get(name.getPackageName(), name.getSimpleName());

    final TypeSpec moduleSpec =
        TypeSpec.classBuilder(name.getSimpleName().concat("Impl"))
            .addSuperinterface(moduleClassName)
            .addMethods(beanMethods)
            .build();

    return JavaFile.builder(Constants.GENERATED_PACKAGE, moduleSpec)
        .skipJavaLangImports(true)
        .build();
  }

  private List<MethodSpec> getBeanMethods(final Set<Bean> beans) {
    return beans.stream().map(this::getBeanMethod).collect(Collectors.toList());
  }

  private MethodSpec getBeanMethod(final Bean bean) {
    final Name name = bean.getName();

    final ClassName beanClassName = ClassName.get(name.getPackageName(), name.getSimpleName());
    final ClassName beanFactoryClassName =
        ClassName.get(Constants.GENERATED_PACKAGE, name.getSimpleName().concat("Factory"));

    return MethodSpec.methodBuilder(StringUtils.toCamelCase(name.getSimpleName()))
        .addModifiers(Modifier.PUBLIC)
        .addAnnotation(Override.class)
        .returns(beanClassName)
        .addStatement("return $T.getInstance()", beanFactoryClassName)
        .build();
  }
}
