package com.github.agmcc.swordfish.processor;

import static com.google.testing.compile.JavaFileObjects.forResource;

import com.google.common.truth.Truth;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourcesSubjectFactory;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.processing.Processor;
import javax.tools.JavaFileObject;
import org.junit.Test;

public class MultiModuleTest {

  private static final Processor PROCESSOR = new SwordfishProcessor();

  @Test
  public void testFactoriesGenerated() {
    final List<JavaFileObject> input =
        Stream.of(
                "input/multi-module/multi/api/ApiModule.java",
                "input/multi-module/multi/api/Controller.java",
                "input/multi-module/multi/api/ExceptionHandler.java",
                "input/multi-module/multi/api/Logger.java",
                "input/multi-module/multi/data/Database.java",
                "input/multi-module/multi/data/DataConnection.java",
                "input/multi-module/multi/data/DataModule.java",
                "input/multi-module/multi/App.java")
            .map(JavaFileObjects::forResource)
            .collect(Collectors.toList());

    Truth.assert_()
        .about(JavaSourcesSubjectFactory.javaSources())
        .that(input)
        .processedWith(PROCESSOR)
        .compilesWithoutError()
        .and()
        .generatesSources(
            forResource("output/multi-module/multi/api/ControllerFactory.java"),
            forResource("output/multi-module/multi/api/ExceptionHandlerFactory.java"),
            forResource("output/multi-module/multi/api/LoggerFactory.java"),
            forResource("output/multi-module/multi/api/SwordfishApiModule.java"),
            forResource("output/multi-module/multi/data/DatabaseFactory.java"),
            forResource("output/multi-module/multi/data/DataConnectionFactory.java"),
            forResource("output/multi-module/multi/data/SwordfishDataModule.java"));
  }
}
