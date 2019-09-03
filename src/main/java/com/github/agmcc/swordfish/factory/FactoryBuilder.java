package com.github.agmcc.swordfish.factory;

import com.github.agmcc.swordfish.domain.Bean;
import com.github.agmcc.swordfish.domain.Name;
import com.github.agmcc.swordfish.domain.Visibility;
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

    final ClassName className = ClassName.get(packageName, simpleName);
    final FieldSpec instance = buildInstance(className, bean);
    final MethodSpec constructor = buildConstructor();

    final Visibility visibility = bean.getVisibility();

    final MethodSpec getInstance = buildGetInstance(className, visibility);
    final TypeSpec factory =
        buildFactory(className, instance, constructor, getInstance, visibility);

    return JavaFile.builder(packageName, factory).build();
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

  private MethodSpec buildGetInstance(final ClassName className, final Visibility visibility) {
    final MethodSpec.Builder getInstanceBuilder =
        MethodSpec.methodBuilder("getInstance")
            .addModifiers(Modifier.STATIC)
            .returns(className)
            .addStatement("return instance");

    if (visibility == Visibility.PUBLIC) {
      getInstanceBuilder.addModifiers(Modifier.PUBLIC);
    }

    return getInstanceBuilder.build();
  }

  private TypeSpec buildFactory(
      final ClassName className,
      final FieldSpec instance,
      final MethodSpec constructor,
      final MethodSpec getInstance,
      final Visibility visibility) {

    final TypeSpec.Builder factoryBuilder =
        TypeSpec.classBuilder(className.simpleName().concat("Factory"))
            .addModifiers(Modifier.FINAL)
            .addField(instance)
            .addMethod(constructor)
            .addMethod(getInstance);

    if (visibility == Visibility.PUBLIC) {
      factoryBuilder.addModifiers(Modifier.PUBLIC);
    }

    return factoryBuilder.build();
  }
}
