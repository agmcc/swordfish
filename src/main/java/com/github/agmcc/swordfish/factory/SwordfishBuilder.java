package com.github.agmcc.swordfish.factory;

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

public class SwordfishBuilder {

  public static final Name SWORDFISH_NAME = Name.from("com.github.agmcc.swordfish.Swordfish");

  public JavaFile createSwordfish(final Set<Module> modules) {

    final MethodSpec constructor =
        MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PRIVATE)
            .addComment("Static access")
            .build();

    final List<MethodSpec> methods = getModuleMethods(modules);

    final TypeSpec typeSpec =
        TypeSpec.classBuilder(SWORDFISH_NAME.getSimpleName())
            .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
            .addMethod(constructor)
            .addMethods(methods)
            .build();

    return JavaFile.builder(SWORDFISH_NAME.getPackageName(), typeSpec).build();
  }

  private List<MethodSpec> getModuleMethods(final Set<Module> modules) {
    return modules.stream().map(this::mapModuleMethod).collect(Collectors.toList());
  }

  private MethodSpec mapModuleMethod(final Module module) {
    final Name moduleName = module.getName();

    final ClassName moduleClassName =
        ClassName.get(moduleName.getPackageName(), moduleName.getSimpleName());

    final ClassName moduleFactoryClassName =
        ClassName.get("com.github.agmcc.swordfish", moduleName.getSimpleName().concat("Factory"));

    return MethodSpec.methodBuilder(StringUtils.toCamelCase(moduleName.getSimpleName()))
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
        .returns(moduleClassName)
        .addStatement("return $T.getInstance()", moduleFactoryClassName)
        .build();
  }
}
