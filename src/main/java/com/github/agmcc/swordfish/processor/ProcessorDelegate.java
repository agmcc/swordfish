package com.github.agmcc.swordfish.processor;

import com.github.agmcc.swordfish.bean.BeanLoader;
import com.github.agmcc.swordfish.domain.Bean;
import com.github.agmcc.swordfish.domain.Module;
import com.github.agmcc.swordfish.domain.Name;
import com.github.agmcc.swordfish.factory.FactoryBuilder;
import com.github.agmcc.swordfish.factory.ModuleBuilder;
import com.github.agmcc.swordfish.io.JavaFileWriter;
import com.squareup.javapoet.JavaFile;
import java.util.Set;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;

class ProcessorDelegate {

  private static final Name SWORDFISH_MODULE_NAME =
      Name.from("com.github.agmcc.swordfish.SwordfishModule");

  private final BeanLoader beanLoader;

  private final JavaFileWriter javaFileWriter;

  private final FactoryBuilder factoryBuilder;

  private final ModuleBuilder moduleBuilder;

  ProcessorDelegate(
      final BeanLoader beanLoader,
      final JavaFileWriter javaFileWriter,
      final FactoryBuilder factoryBuilder,
      final ModuleBuilder moduleBuilder) {

    this.beanLoader = beanLoader;
    this.javaFileWriter = javaFileWriter;
    this.factoryBuilder = factoryBuilder;
    this.moduleBuilder = moduleBuilder;
  }

  boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
    if (annotations.isEmpty()) {
      return true;
    }

    final Set<Bean> beans = beanLoader.loadBeans(roundEnv);

    for (final Bean bean : beans) {
      final JavaFile factory = factoryBuilder.createFactory(bean);
      javaFileWriter.writeJavaFile(bean.toString().concat("Factory"), factory);
    }

    final Module swordfishModule = new Module(SWORDFISH_MODULE_NAME, beans);
    final JavaFile swordfishModuleFile = moduleBuilder.createModule(swordfishModule);
    javaFileWriter.writeJavaFile(SWORDFISH_MODULE_NAME.toString(), swordfishModuleFile);

    return true;
  }
}
