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

public class SwordfishDefaultModuleTest {

  private static final Processor PROCESSOR = new SwordfishProcessor();

  @Test
  public void testFactoriesGenerated() {
    final List<JavaFileObject> input =
        Stream.of(
                "input/swordfish-module/Printer.java",
                "input/swordfish-module/InkCartridge.java",
                "input/swordfish-module/Ink.java",
                "input/swordfish-module/Paper.java",
                "input/swordfish-module/PaperSize.java",
                "input/swordfish-module/PaperConfig.java",
                "input/swordfish-module/Nozzle.java",
                "input/swordfish-module/NozzleConfig.java",
                "input/swordfish-module/App.java")
            .map(JavaFileObjects::forResource)
            .collect(Collectors.toList());

    Truth.assert_()
        .about(JavaSourcesSubjectFactory.javaSources())
        .that(input)
        .processedWith(PROCESSOR)
        .compilesWithoutError()
        .and()
        .generatesSources(
            forResource("output/swordfish-module/PrinterFactory.java"),
            forResource("output/swordfish-module/InkCartridgeFactory.java"),
            forResource("output/swordfish-module/InkFactory.java"),
            forResource("output/swordfish-module/PaperConfigFactory.java"),
            forResource("output/swordfish-module/PaperFactory.java"),
            forResource("output/swordfish-module/PaperSizeFactory.java"),
            forResource("output/swordfish-module/NozzleFactory.java"),
            forResource("output/swordfish-module/Swordfish.java"),
            forResource("output/swordfish-module/DefaultModuleFactory.java"),
            forResource("output/swordfish-module/DefaultModule.java"),
            forResource("output/swordfish-module/DefaultModuleImpl.java"));
  }
}
