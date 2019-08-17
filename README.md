# Swordfish [![Build Status](https://travis-ci.org/agmcc/swordfish.svg?branch=master)](https://travis-ci.org/agmcc/swordfish)

A compile-time dependency injection framework for Java, using JSR-330 annotations.

## Requirements

* JDK 8+

## Installation

Currently the project must be built from source (see *Building*).

### Example with Gradle (5.x)

Add the following to your *build.gradle* file:

```groovy
repositories {
    mavenLocal()
}

dependencies {
    implementation 'com.github.agmcc:swordfish:0.1.0'
    annotationProcessor 'com.github.agmcc:swordfish:0.1.0'
}
```

## Usage

Classes managed by the framework (beans) are annotated with `javax.inject.Named`.
A public constructor, annotated with `javax.inject.Inject` is also required.

```java
// Package declaration and imports
@Named
public class Engine {

  @Inject
  public Engine(Piston piston) {
    // Use injected Piston
  } 
}
```

A factory class is generated for each bean, which can be used to retrieve a fully constructed instance
(including transitive dependencies).

Typically, this will be done for the root object in the dependency graph.

```java
public static void main(String[] args) {
  Engine engine = EngineFactory.getInstance();
}
```

Beans can also be provided via methods, which allows the instance to be configured.

This is useful for 3rd party classes which aren't annotated with `@Named`.

Any parameters on the method will be injected as bean dependencies.

```java
@Named
public class PistonConfig {
  
  @Inject
  public PistonConfig(){}
  
  @Named
  public Piston piston(Oil oil) {
    return new Piston(oil);
  }
}
```

## Building

Clone the project and run the following in the root directory:

```bash
./gradlew publishToMavenLocal
```

Or if you want to run the tests:

```bash
./gradlew test
```
