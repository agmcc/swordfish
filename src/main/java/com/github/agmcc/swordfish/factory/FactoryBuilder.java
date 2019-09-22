package com.github.agmcc.swordfish.factory;

import com.github.agmcc.swordfish.domain.Bean;
import com.github.agmcc.swordfish.domain.Constants;
import com.github.agmcc.swordfish.domain.Module;
import com.github.agmcc.swordfish.domain.Name;
import com.github.agmcc.swordfish.inject.InjectorVisitor;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import javax.lang.model.element.Modifier;

public class FactoryBuilder {

  public JavaFile createFactory(final Bean bean) {
    final Name name = bean.getName();
    final String simpleName = name.getSimpleName();
    final String packageName = name.getPackageName();

    final ClassName beanClassName = ClassName.get(packageName, simpleName);
    final ClassName factoryClassName = ClassName.get(Constants.GENERATED_PACKAGE, simpleName);

    final FieldSpec instance = buildInstance(beanClassName, bean);
    final MethodSpec constructor = buildConstructor();

    final MethodSpec getInstance = buildGetInstance(beanClassName);
    final TypeSpec factory = buildFactory(factoryClassName, instance, constructor, getInstance);

    return JavaFile.builder(factoryClassName.packageName(), factory).build();
  }

  public JavaFile createFactory(final Module module) {
    final Name name = module.getName();
    final String simpleName = name.getSimpleName();

    final ClassName className = ClassName.get(name.getPackageName(), simpleName);

    final ClassName moduleImplClassName =
        ClassName.get(Constants.GENERATED_PACKAGE, simpleName.concat("Impl"));

    final FieldSpec instance =
        FieldSpec.builder(className, "instance")
            .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
            .initializer("new $T()", moduleImplClassName)
            .build();

    final MethodSpec constructor = buildConstructor();

    final MethodSpec getInstance = buildGetInstance(className);
    final TypeSpec factory = buildFactory(className, instance, constructor, getInstance);

    return JavaFile.builder(Constants.GENERATED_PACKAGE, factory).build();
  }

  private FieldSpec buildInstance(final ClassName className, final Bean bean) {
    final FieldSpec.Builder instanceBuilder =
        FieldSpec.builder(className, "instance")
            .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL);

    final InjectorVisitor visitor = new FactoryInjectorVisitor(className, instanceBuilder);
    bean.getInjector().accept(visitor);

    return instanceBuilder.build();
  }

  private MethodSpec buildConstructor() {
    return MethodSpec.constructorBuilder()
        .addModifiers(Modifier.PRIVATE)
        .addComment("Static access")
        .build();
  }

  private MethodSpec buildGetInstance(final ClassName className) {
    final MethodSpec.Builder getInstanceBuilder =
        MethodSpec.methodBuilder("getInstance")
            .addModifiers(Modifier.STATIC)
            .returns(className)
            .addStatement("return instance");

    return getInstanceBuilder.build();
  }

  private TypeSpec buildFactory(
      final ClassName className,
      final FieldSpec instance,
      final MethodSpec constructor,
      final MethodSpec getInstance) {

    final TypeSpec.Builder factoryBuilder =
        TypeSpec.classBuilder(className.simpleName().concat("Factory"))
            .addModifiers(Modifier.FINAL)
            .addField(instance)
            .addMethod(constructor)
            .addMethod(getInstance);
    return factoryBuilder.build();
  }
}
