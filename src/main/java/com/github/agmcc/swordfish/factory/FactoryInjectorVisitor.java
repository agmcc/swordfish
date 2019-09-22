package com.github.agmcc.swordfish.factory;

import com.github.agmcc.swordfish.domain.Constants;
import com.github.agmcc.swordfish.domain.Name;
import com.github.agmcc.swordfish.inject.BeanMethodInjector;
import com.github.agmcc.swordfish.inject.ConstructorInjector;
import com.github.agmcc.swordfish.inject.InjectorVisitor;
import com.github.agmcc.swordfish.inject.StaticMethodInjector;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FactoryInjectorVisitor implements InjectorVisitor {

  private final ClassName className;

  private final FieldSpec.Builder builder;

  FactoryInjectorVisitor(final ClassName className, final FieldSpec.Builder builder) {
    this.className = className;
    this.builder = builder;
  }

  @Override
  public void visit(final ConstructorInjector injector) {
    final List<ClassName> dependencyClassNames =
        getDependencyClassNames(injector.getDependencies());

    final StringJoiner constructorJoiner = new StringJoiner(", ", "new $T(", ")");
    dependencyClassNames.forEach(d -> constructorJoiner.add("$T.getInstance()"));
    final String constructorPattern = constructorJoiner.toString();

    final Object[] args =
        Stream.concat(Stream.of(className), dependencyClassNames.stream()).toArray();

    builder.initializer(constructorPattern, args);
  }

  @Override
  public void visit(final BeanMethodInjector injector) {
    final List<ClassName> dependencyClassNames =
        getDependencyClassNames(injector.getDependencies());

    final Name beanName = injector.getMethodClassName();
    final ClassName beanClassName =
        ClassName.get(Constants.GENERATED_PACKAGE, beanName.getSimpleName().concat("Factory"));

    final StringJoiner methodJoiner = new StringJoiner(", ", "$T.getInstance().$L(", ")");
    dependencyClassNames.forEach(d -> methodJoiner.add("$T.getInstance()"));
    final String methodPattern = methodJoiner.toString();

    final Object[] args =
        Stream.concat(
                Stream.of(beanClassName, injector.getMethodName()), dependencyClassNames.stream())
            .toArray();

    builder.initializer(methodPattern, args);
  }

  @Override
  public void visit(final StaticMethodInjector injector) {
    final List<ClassName> dependencyClassNames =
        getDependencyClassNames(injector.getDependencies());

    final Name staticClassName = injector.getMethodClassName();
    final ClassName className =
        ClassName.get(staticClassName.getPackageName(), staticClassName.getSimpleName());

    final StringJoiner methodJoiner = new StringJoiner(", ", "$T.$L(", ")");
    dependencyClassNames.forEach(d -> methodJoiner.add("$T.getInstance()"));
    final String methodPattern = methodJoiner.toString();

    final Object[] args =
        Stream.concat(Stream.of(className, injector.getMethodName()), dependencyClassNames.stream())
            .toArray();

    builder.initializer(methodPattern, args);
  }

  private List<ClassName> getDependencyClassNames(final List<Name> dependencies) {
    return dependencies.stream()
        .map(d -> ClassName.get(Constants.GENERATED_PACKAGE, d.getSimpleName().concat("Factory")))
        .collect(Collectors.toList());
  }
}
