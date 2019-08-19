package com.github.agmcc.swordfish.processor;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

/**
 * Minimal implementation of DI using annotation processing.
 *
 * <p>Only supports constructor injection with {@link Inject}. Beans are declared via {@link Named}.
 * Qualifying via names is not supported.
 *
 * <p>Beans can also be provided via named methods declared in a named bean. They must be public and
 * non-static. Parameters for such methods will be injected.
 */
public class SwordfishProcessor extends AbstractProcessor {

  private ProcessorDelegate delegate;

  @Override
  public synchronized void init(final ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    delegate = ProcessorDelegateFactory.create(processingEnv);
  }

  @Override
  public boolean process(
      final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
    return delegate.process(annotations, roundEnv);
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return Stream.of(Named.class, Inject.class)
        .map(Class::getCanonicalName)
        .collect(Collectors.toSet());
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }
}
