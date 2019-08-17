package com.github.agmcc.swordfish.processor;

import com.google.common.truth.Truth;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourcesSubjectFactory;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.tools.JavaFileObject;
import org.junit.Test;

public class ProcessorTest {

  private static final javax.annotation.processing.Processor PROCESSOR = new Processor();

  @Test
  public void testFactoriesGenerated() {
    final List<JavaFileObject> input =
        Stream.of(
                "input/Printer.java",
                "input/InkCartridge.java",
                "input/Ink.java",
                "input/Paper.java",
                "input/PaperSize.java",
                "input/PaperConfig.java")
            .map(JavaFileObjects::forResource)
            .collect(Collectors.toList());

    Truth.assert_()
        .about(JavaSourcesSubjectFactory.javaSources())
        .that(input)
        .processedWith(PROCESSOR)
        .compilesWithoutError()
        .and()
        .generatesSources(
            JavaFileObjects.forResource("output/PrinterFactory.java"),
            JavaFileObjects.forResource("output/InkCartridgeFactory.java"),
            JavaFileObjects.forResource("output/InkFactory.java"),
            JavaFileObjects.forResource("output/PaperConfigFactory.java"),
            JavaFileObjects.forResource("output/PaperFactory.java"),
            JavaFileObjects.forResource("output/PaperSizeFactory.java"));
  }
}
