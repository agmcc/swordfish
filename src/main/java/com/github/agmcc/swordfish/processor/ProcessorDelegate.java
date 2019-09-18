package com.github.agmcc.swordfish.processor;

import com.github.agmcc.swordfish.bean.BeanLoader;
import com.github.agmcc.swordfish.domain.Bean;
import com.github.agmcc.swordfish.domain.Module;
import com.github.agmcc.swordfish.domain.Name;
import com.github.agmcc.swordfish.factory.FactoryBuilder;
import com.github.agmcc.swordfish.factory.ModuleBuilder;
import com.github.agmcc.swordfish.io.JavaFileWriter;
import com.github.agmcc.swordfish.module.ModuleLoader;
import com.squareup.javapoet.JavaFile;
import java.util.Set;
import java.util.StringJoiner;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;

class ProcessorDelegate {

  private final BeanLoader beanLoader;

  private final ModuleLoader moduleLoader;

  private final JavaFileWriter javaFileWriter;

  private final FactoryBuilder factoryBuilder;

  private final ModuleBuilder moduleBuilder;

  ProcessorDelegate(
      final BeanLoader beanLoader,
      final ModuleLoader moduleLoader,
      final JavaFileWriter javaFileWriter,
      final FactoryBuilder factoryBuilder,
      final ModuleBuilder moduleBuilder) {

    this.beanLoader = beanLoader;
    this.moduleLoader = moduleLoader;
    this.javaFileWriter = javaFileWriter;
    this.factoryBuilder = factoryBuilder;
    this.moduleBuilder = moduleBuilder;
  }

  boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
    if (annotations.isEmpty()) {
      return true;
    }

    final Set<Bean> beans = beanLoader.loadBeans(roundEnv);
    final Set<Module> modules = moduleLoader.loadModules(roundEnv, beans);

    for (final Bean bean : beans) {
      final JavaFile factoryFile = factoryBuilder.createFactory(bean);
      javaFileWriter.writeJavaFile(bean.toString().concat("Factory"), factoryFile);
    }

    for (final Module module : modules) {
      final JavaFile moduleFile = moduleBuilder.createModule(module);
      javaFileWriter.writeJavaFile(getModuleFileName(module), moduleFile);
    }

    return true;
  }

  private String getModuleFileName(final Module module) {
    final Name name = module.getName();
    return new StringJoiner(".")
        .add(name.getPackageName())
        .add("Swordfish".concat(name.getSimpleName()))
        .toString();
  }
}
