package com.github.agmcc.swordfish.processor;

import com.github.agmcc.swordfish.bean.BeanLoader;
import com.github.agmcc.swordfish.domain.Bean;
import com.github.agmcc.swordfish.factory.FactoryBuilder;
import com.github.agmcc.swordfish.io.JavaFileWriter;
import com.squareup.javapoet.JavaFile;
import java.util.List;
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
    final List<Bean> beans = beanLoader.loadBeans(roundEnv);

    for (final Bean bean : beans) {
      final JavaFile factory = factoryBuilder.createFactory(bean, beans);
      javaFileWriter.writeJavaFile(bean.toString().concat("Factory"), factory);
    }
    return false;
  }
}
