package com.github.agmcc.swordfish.processor;

import com.github.agmcc.swordfish.bean.BeanLoader;
import com.github.agmcc.swordfish.bean.BeanMapper;
import com.github.agmcc.swordfish.bean.GraphBuilder;
import com.github.agmcc.swordfish.factory.FactoryBuilder;
import com.github.agmcc.swordfish.graph.GraphUtils;
import com.github.agmcc.swordfish.io.JavaFileWriter;
import javax.annotation.processing.ProcessingEnvironment;

final class ProcessorDelegateFactory {

  private ProcessorDelegateFactory() {
    /* Static access */
  }

  static ProcessorDelegate create(final ProcessingEnvironment processingEnv) {
    return new ProcessorDelegate(
        new BeanLoader(new BeanMapper(), new GraphBuilder(), new GraphUtils()),
        new JavaFileWriter(processingEnv.getFiler()),
        new FactoryBuilder());
  }
}
