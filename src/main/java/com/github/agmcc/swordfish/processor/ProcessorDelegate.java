package com.github.agmcc.swordfish.processor;

import com.github.agmcc.swordfish.bean.BeanLoader;
import com.github.agmcc.swordfish.domain.BeanElement;
import com.github.agmcc.swordfish.domain.Dependency;
import com.github.agmcc.swordfish.factory.FactoryBuilder;
import com.github.agmcc.swordfish.io.JavaFileWriter;
import com.google.common.graph.ValueGraph;
import com.squareup.javapoet.JavaFile;
import java.util.Set;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;

class ProcessorDelegate {

  private final BeanLoader beanLoader;

  private final JavaFileWriter javaFileWriter;

  private final FactoryBuilder factoryBuilder;

  ProcessorDelegate(
      final BeanLoader beanLoader,
      final JavaFileWriter javaFileWriter,
      final FactoryBuilder factoryBuilder) {
    this.beanLoader = beanLoader;
    this.javaFileWriter = javaFileWriter;
    this.factoryBuilder = factoryBuilder;
  }

  boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
    if (annotations.isEmpty()) {
      return true;
    }

    @SuppressWarnings("UnstableApiUsage")
    final ValueGraph<BeanElement, Dependency> beanGraph = beanLoader.loadBeans(roundEnv);

    for (final BeanElement bean : beanGraph.nodes()) {
      final JavaFile factory = factoryBuilder.createFactory(bean, beanGraph);
      javaFileWriter.writeJavaFile(bean.toString().concat("Factory"), factory);
    }
    return true;
  }
}
