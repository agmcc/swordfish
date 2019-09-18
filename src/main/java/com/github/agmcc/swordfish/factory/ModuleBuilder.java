package com.github.agmcc.swordfish.factory;

import com.github.agmcc.swordfish.domain.Bean;
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
    final String packageName = name.getPackageName();
    final String simpleName = "Swordfish" + name.getSimpleName();

    final MethodSpec constructor =
        MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PRIVATE)
            .addComment("Static access")
            .build();

    final List<MethodSpec> beanMethods = getBeanMethods(module.getPublished());

    final TypeSpec moduleSpec =
        TypeSpec.classBuilder(simpleName)
            .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
            .addMethod(constructor)
            .addMethods(beanMethods)
            .build();

    return JavaFile.builder(packageName, moduleSpec).build();
  }

  private List<MethodSpec> getBeanMethods(final Set<Bean> beans) {
    return beans.stream().map(this::getBeanMethod).collect(Collectors.toList());
  }

  private MethodSpec getBeanMethod(final Bean bean) {
    final Name name = bean.getName();
    final ClassName beanClassName = ClassName.get(name.getPackageName(), name.getSimpleName());
    final ClassName beanFactoryClassName =
        ClassName.get(name.getPackageName(), name.getSimpleName().concat("Factory"));
    return MethodSpec.methodBuilder(StringUtils.toCamelCase(name.getSimpleName()))
        .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
        .returns(beanClassName)
        .addStatement("return $T.getInstance()", beanFactoryClassName)
        .build();
  }
}
