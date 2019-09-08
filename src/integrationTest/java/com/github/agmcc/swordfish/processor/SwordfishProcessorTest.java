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

public class SwordfishProcessorTest {

  private static final Processor PROCESSOR = new SwordfishProcessor();

  @Test
  public void testFactoriesGenerated() {
    final List<JavaFileObject> input =
        Stream.of(
                "input/Printer.java",
                "input/InkCartridge.java",
                "input/Ink.java",
                "input/Paper.java",
                "input/PaperSize.java",
                "input/PaperConfig.java",
                "input/Nozzle.java",
                "input/NozzleConfig.java",
                "input/App.java")
            .map(JavaFileObjects::forResource)
            .collect(Collectors.toList());

    Truth.assert_()
        .about(JavaSourcesSubjectFactory.javaSources())
        .that(input)
        .processedWith(PROCESSOR)
        .compilesWithoutError()
        .and()
        .generatesSources(
            forResource("output/PrinterFactory.java"),
            forResource("output/InkCartridgeFactory.java"),
            forResource("output/InkFactory.java"),
            forResource("output/PaperConfigFactory.java"),
            forResource("output/PaperFactory.java"),
            forResource("output/PaperSizeFactory.java"),
            forResource("output/NozzleFactory.java"),
            forResource("output/SwordfishModule.java"));
  }
}
