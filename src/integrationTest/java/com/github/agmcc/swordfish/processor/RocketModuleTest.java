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

public class RocketModuleTest {

  private static final Processor PROCESSOR = new SwordfishProcessor();

  @Test
  public void testFactoriesGenerated() {
    final List<JavaFileObject> input =
        Stream.of(
                "input/rocket-module/rocket/Rocket.java",
                "input/rocket-module/rocket/FuelTank.java",
                "input/rocket-module/rocket/propulsion/Booster.java",
                "input/rocket-module/rocket/RocketModule.java",
                "input/rocket-module/rocket/Launchpad.java")
            .map(JavaFileObjects::forResource)
            .collect(Collectors.toList());

    Truth.assert_()
        .about(JavaSourcesSubjectFactory.javaSources())
        .that(input)
        .processedWith(PROCESSOR)
        .compilesWithoutError()
        .and()
        .generatesSources(
            forResource("output/rocket-module/rocket/RocketFactory.java"),
            forResource("output/rocket-module/rocket/FuelTankFactory.java"),
            forResource("output/rocket-module/rocket/propulsion/BoosterFactory.java"),
            forResource("output/rocket-module/rocket/Swordfish.java"),
            forResource("output/rocket-module/rocket/RocketModuleFactory.java"),
            forResource("output/rocket-module/rocket/RocketModuleImpl.java"));
  }
}
