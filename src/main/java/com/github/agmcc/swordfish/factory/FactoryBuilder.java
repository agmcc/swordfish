package com.github.agmcc.swordfish.factory;

import com.github.agmcc.swordfish.domain.BeanDefinitionType;
import com.github.agmcc.swordfish.domain.BeanElement;
import com.github.agmcc.swordfish.domain.ConstructorDependency;
import com.github.agmcc.swordfish.domain.Dependency;
import com.github.agmcc.swordfish.domain.MethodDependency;
import com.github.agmcc.swordfish.domain.ProviderDependency;
import com.google.common.graph.ValueGraph;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

@SuppressWarnings("UnstableApiUsage")
public class FactoryBuilder {

  public JavaFile createFactory(
      final BeanElement currentBean, final ValueGraph<BeanElement, Dependency> allBeans) {

    final String simpleName = extractSimpleName(currentBean.getQualifiedName());
    final String packageName = extractPackageName(currentBean.getQualifiedName());

    final ClassName className = ClassName.get(packageName, simpleName);

    final FieldSpec.Builder instanceBuilder =
        FieldSpec.builder(className, "instance")
            .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL);

    final FieldSpec instance =
        addInitializer(instanceBuilder, className, currentBean, allBeans).build();

    final MethodSpec getInstance =
        MethodSpec.methodBuilder("getInstance")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(className)
            .addStatement("return instance")
            .build();

    final TypeSpec factory =
        TypeSpec.classBuilder(className.simpleName().concat("Factory"))
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addField(instance)
            .addMethod(getInstance)
            .build();

    return JavaFile.builder(packageName, factory).build();
  }

  private String extractPackageName(final String qualifiedName) {
    final int i = qualifiedName.lastIndexOf(".");
    if (i == -1) {
      return qualifiedName;
    } else {
      return qualifiedName.substring(0, i);
    }
  }

  private String extractSimpleName(final String qualifiedName) {
    final int i = qualifiedName.lastIndexOf(".");
    if (i == -1) {
      return qualifiedName;
    } else {
      return qualifiedName.substring(i + 1);
    }
  }

  private FieldSpec.Builder addInitializer(
      final FieldSpec.Builder builder,
      final ClassName className,
      final BeanElement bean,
      final ValueGraph<BeanElement, Dependency> dependencyGraph) {

    final BeanDefinitionType type = bean.getBeanDefinitionType();

    switch (type) {
      case CLASS:
        final List<ClassName> dependencyClassNames =
            dependencyGraph.successors(bean).stream()
                .sorted(
                    Comparator.comparingInt(
                        s ->
                            ((ConstructorDependency)
                                    Objects.requireNonNull(
                                        dependencyGraph.edgeValueOrDefault(bean, s, null)))
                                .getArgIndex()))
                .map(
                    b ->
                        ClassName.get(
                            extractPackageName(b.getQualifiedName()),
                            extractSimpleName(b.getQualifiedName()).concat("Factory")))
                .collect(Collectors.toList());

        final StringJoiner constructorJoiner = new StringJoiner(", ", "new $T(", ")");
        dependencyClassNames.forEach(d -> constructorJoiner.add("$T.getInstance()"));
        final String constructorPattern = constructorJoiner.toString();

        final List<Object> args = new ArrayList<>();
        args.add(className);
        args.addAll(dependencyClassNames);

        builder.initializer(constructorPattern, args.toArray());
        break;
      case METHOD:
        final List<ClassName> dependencyClassNames2 =
            dependencyGraph.successors(bean).stream()
                .filter(
                    s ->
                        !(dependencyGraph.edgeValueOrDefault(bean, s, null)
                            instanceof ProviderDependency))
                .sorted(
                    Comparator.comparingInt(
                        s ->
                            ((MethodDependency)
                                    Objects.requireNonNull(
                                        dependencyGraph.edgeValueOrDefault(bean, s, null)))
                                .getArgIndex()))
                .map(
                    b ->
                        ClassName.get(
                            extractPackageName(b.getQualifiedName()),
                            extractSimpleName(b.getQualifiedName()).concat("Factory")))
                .collect(Collectors.toList());

        final TypeElement providerType = (TypeElement) bean.getProvider().getEnclosingElement();
        final String providerQualifiedName = providerType.getQualifiedName().toString();
        final String providerPackageName = extractPackageName(providerQualifiedName);
        final String providerSimpleName =
            extractSimpleName(providerQualifiedName).concat("Factory");

        final TypeName providerName = ClassName.get(providerPackageName, providerSimpleName);

        final StringJoiner methodJoiner = new StringJoiner(", ", "$T.getInstance().$L(", ")");
        dependencyClassNames2.forEach(d -> methodJoiner.add("$T.getInstance()"));
        final String methodPattern = methodJoiner.toString();

        final List<Object> args2 = new ArrayList<>();
        args2.add(providerName);
        args2.add(bean.getProvider().getSimpleName().toString());
        args2.addAll(dependencyClassNames2);

        builder.initializer(methodPattern, args2.toArray());
        break;
      default:
        throw new RuntimeException("Unsupported bean definition type: " + type);
    }

    return builder;
  }
}
