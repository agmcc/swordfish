package com.github.agmcc.swordfish.processor;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.github.agmcc.swordfish.bean.BeanLoader;
import com.github.agmcc.swordfish.domain.Bean;
import com.github.agmcc.swordfish.domain.Module;
import com.github.agmcc.swordfish.domain.Name;
import com.github.agmcc.swordfish.factory.DefaultModuleBuilder;
import com.github.agmcc.swordfish.factory.FactoryBuilder;
import com.github.agmcc.swordfish.factory.ModuleBuilder;
import com.github.agmcc.swordfish.factory.SwordfishBuilder;
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

  @Mock private SwordfishBuilder swordfishBuilder;

  @Mock private DefaultModuleBuilder defaultModuleBuilder;

  @Test
  void process_annotationsEmpty_true() {
    assertTrue(processorDelegate.process(Collections.emptySet(), null));
  }

  @Test
  void process_beansLoaded_filesWritten(
      @Mock final Set<? extends TypeElement> annotations, @Mock final RoundEnvironment roundEnv) {

    // Given
    final Bean bean =
        new Bean(Name.from("swordfish.Ink"), new ConstructorInjector(Collections.emptyList()));

    final Set<Bean> beans = Collections.singleton(bean);

    final JavaFile factoryFile =
        JavaFile.builder("swordfish", TypeSpec.classBuilder("Ink").build()).build();

    final Module module =
        new Module(Name.from("com.github.agmcc.swordfish.DefaultModule"), beans, beans);

    final Set<Module> modules = Collections.singleton(module);

    final JavaFile moduleFile =
        JavaFile.builder(
                "com.github.agmcc.swordfish", TypeSpec.classBuilder("DefaultModuleImpl").build())
            .build();

    final JavaFile defaultModuleFile =
        JavaFile.builder(
                "com.github.agmcc.swordfish", TypeSpec.interfaceBuilder("DefaultModule").build())
            .build();

    final JavaFile moduleFactoryFile =
        JavaFile.builder(
                "com.github.agmcc.swordfish", TypeSpec.classBuilder("DefaultModuleFactory").build())
            .build();

    final JavaFile swordfishFile =
        JavaFile.builder("com.github.agmcc.swordfish", TypeSpec.classBuilder("Swordfish").build())
            .build();

    given(beanLoader.loadBeans(roundEnv)).willReturn(beans);

    given(moduleLoader.loadModules(roundEnv, beans)).willReturn(modules);

    given(factoryBuilder.createFactory(bean)).willReturn(factoryFile);

    given(moduleBuilder.createModule(module)).willReturn(moduleFile);

    given(defaultModuleBuilder.createDefaultModule(beans)).willReturn(defaultModuleFile);

    given(factoryBuilder.createFactory(module)).willReturn(moduleFactoryFile);

    given(swordfishBuilder.createSwordfish(modules)).willReturn(swordfishFile);

    // When
    final boolean result = processorDelegate.process(annotations, roundEnv);

    // Then
    assertTrue(result);

    then(javaFileWriter)
        .should()
        .writeJavaFile("com.github.agmcc.swordfish.InkFactory", factoryFile);
    then(javaFileWriter)
        .should()
        .writeJavaFile("com.github.agmcc.swordfish.DefaultModuleImpl", moduleFile);
    then(javaFileWriter)
        .should()
        .writeJavaFile("com.github.agmcc.swordfish.DefaultModule", defaultModuleFile);
    then(javaFileWriter)
        .should()
        .writeJavaFile("com.github.agmcc.swordfish.DefaultModuleFactory", moduleFactoryFile);
    then(javaFileWriter)
        .should()
        .writeJavaFile("com.github.agmcc.swordfish.Swordfish", swordfishFile);
  }
}
