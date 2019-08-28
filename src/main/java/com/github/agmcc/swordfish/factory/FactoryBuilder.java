package com.github.agmcc.swordfish.factory;

import com.github.agmcc.swordfish.domain.Bean;
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

    final ClassName className = ClassName.get(packageName, simpleName);

    final FieldSpec.Builder instanceBuilder =
        FieldSpec.builder(className, "instance")
            .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL);

    setInitializer(bean, className, instanceBuilder);

    final MethodSpec constructor =
        MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PRIVATE)
            .addComment("Static access")
            .build();

    final MethodSpec getInstance =
        MethodSpec.methodBuilder("getInstance")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(className)
            .addStatement("return instance")
            .build();

    final TypeSpec factory =
        TypeSpec.classBuilder(className.simpleName().concat("Factory"))
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addField(instanceBuilder.build())
            .addMethod(constructor)
            .addMethod(getInstance)
            .build();

    return JavaFile.builder(packageName, factory).build();
  }

  private void setInitializer(
      final Bean bean, final ClassName className, final FieldSpec.Builder builder) {
    final InjectorVisitor visitor = new FactoryInjectorVisitor(className, builder);
    bean.getInjector().accept(visitor);
  }
}
