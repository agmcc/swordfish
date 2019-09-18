package com.github.agmcc.swordfish.processor;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.github.agmcc.swordfish.bean.BeanLoader;
import com.github.agmcc.swordfish.domain.Bean;
import com.github.agmcc.swordfish.domain.Module;
import com.github.agmcc.swordfish.domain.Name;
import com.github.agmcc.swordfish.domain.Visibility;
import com.github.agmcc.swordfish.factory.FactoryBuilder;
import com.github.agmcc.swordfish.factory.ModuleBuilder;
import com.github.agmcc.swordfish.inject.ConstructorInjector;
import com.github.agmcc.swordfish.io.JavaFileWriter;
import com.github.agmcc.swordfish.module.ModuleLoader;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import java.util.Collections;
import java.util.Set;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProcessorDelegateTest {

  @InjectMocks private ProcessorDelegate processorDelegate;

  @Mock private BeanLoader beanLoader;

  @Mock private ModuleLoader moduleLoader;

  @Mock private JavaFileWriter javaFileWriter;

  @Mock private FactoryBuilder factoryBuilder;

  @Mock private ModuleBuilder moduleBuilder;

  @Test
  void process_annotationsEmpty_true() {
    assertTrue(processorDelegate.process(Collections.emptySet(), null));
  }

  @Test
  void process_beansLoaded_filesWritten(
      @Mock final Set<? extends TypeElement> annotations, @Mock final RoundEnvironment roundEnv) {

    // Given
    final Bean bean =
        new Bean(
            Name.from("swordfish.Ink"),
            new ConstructorInjector(Collections.emptyList()),
            Visibility.PUBLIC);

    final Set<Bean> beans = Collections.singleton(bean);

    final Module module =
        new Module(Name.from("com.github.agmcc.swordfish.DefaultModule"), beans, beans);

    final JavaFile factoryFile =
        JavaFile.builder("swordfish", TypeSpec.classBuilder("Ink").build()).build();

    final JavaFile moduleFile =
        JavaFile.builder(
                "com.github.agmcc.swordfish",
                TypeSpec.classBuilder("SwordfishDefaultModule").build())
            .build();

    given(beanLoader.loadBeans(roundEnv)).willReturn(beans);

    given(moduleLoader.loadModules(roundEnv, beans)).willReturn(Collections.singleton(module));

    given(factoryBuilder.createFactory(bean)).willReturn(factoryFile);

    given(moduleBuilder.createModule(module)).willReturn(moduleFile);

    // When
    final boolean result = processorDelegate.process(annotations, roundEnv);

    // Then
    assertTrue(result);

    then(javaFileWriter).should().writeJavaFile("swordfish.InkFactory", factoryFile);
    then(javaFileWriter)
        .should()
        .writeJavaFile("com.github.agmcc.swordfish.SwordfishDefaultModule", moduleFile);
  }
}
