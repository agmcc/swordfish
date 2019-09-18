package com.github.agmcc.swordfish.processor;

import com.github.agmcc.swordfish.bean.BeanLoader;
import com.github.agmcc.swordfish.bean.BeanMapper;
import com.github.agmcc.swordfish.bean.GraphBuilder;
import com.github.agmcc.swordfish.factory.FactoryBuilder;
import com.github.agmcc.swordfish.factory.ModuleBuilder;
import com.github.agmcc.swordfish.graph.GraphUtils;
import com.github.agmcc.swordfish.io.JavaFileWriter;
import com.github.agmcc.swordfish.module.ModuleLoader;
import com.github.agmcc.swordfish.module.ModuleMapper;
import javax.annotation.processing.ProcessingEnvironment;

final class ProcessorDelegateFactory {

  private ProcessorDelegateFactory() {
    /* Static access */
  }

  static ProcessorDelegate create(final ProcessingEnvironment processingEnv) {
    final GraphUtils graphUtils = new GraphUtils();

    return new ProcessorDelegate(
        new BeanLoader(new BeanMapper(), new GraphBuilder(), graphUtils),
        new ModuleLoader(new ModuleMapper(processingEnv.getTypeUtils()), graphUtils),
        new JavaFileWriter(processingEnv.getFiler()),
        new FactoryBuilder(),
        new ModuleBuilder());
  }
}
