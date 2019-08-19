package com.github.agmcc.swordfish.processor;

import com.github.agmcc.swordfish.bean.BeanLoader;
import com.github.agmcc.swordfish.factory.FactoryBuilder;
import com.github.agmcc.swordfish.io.JavaFileWriter;
import javax.annotation.processing.ProcessingEnvironment;

final class ProcessorDelegateFactory {

  private ProcessorDelegateFactory() {
    /* Static access */
  }

  static ProcessorDelegate create(final ProcessingEnvironment processingEnv) {
    return new ProcessorDelegate(
        new BeanLoader(),
        new JavaFileWriter(processingEnv.getFiler()),
        new FactoryBuilder(processingEnv.getElementUtils(), processingEnv.getTypeUtils()));
  }
}
