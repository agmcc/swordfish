package com.github.agmcc.swordfish.factory;

import com.github.agmcc.swordfish.domain.Bean;
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

public class DefaultModuleBuilder {

  public static final Name DEFAULT_MODULE_NAME =
      Name.from("com.github.agmcc.swordfish.DefaultModule");

  public JavaFile createDefaultModule(final Set<Bean> beans) {

    final List<MethodSpec> methods = mapBeanMethods(beans);

    final TypeSpec typeSpec =
        TypeSpec.interfaceBuilder(DEFAULT_MODULE_NAME.getSimpleName())
            .addModifiers(Modifier.PUBLIC)
            .addMethods(methods)
            .build();

    return JavaFile.builder(DEFAULT_MODULE_NAME.getPackageName(), typeSpec).build();
  }

  private List<MethodSpec> mapBeanMethods(final Set<Bean> beans) {
    return beans.stream().map(this::mapBeanMethod).collect(Collectors.toList());
  }

  private MethodSpec mapBeanMethod(final Bean bean) {
    final Name beanName = bean.getName();

    final ClassName beanClassName =
        ClassName.get(beanName.getPackageName(), beanName.getSimpleName());

    return MethodSpec.methodBuilder(StringUtils.toCamelCase(beanName.getSimpleName()))
        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
        .returns(beanClassName)
        .build();
  }
}
