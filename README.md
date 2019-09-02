# Swordfish

[![Build Status](https://travis-ci.org/agmcc/swordfish.svg?branch=master)](https://travis-ci.org/agmcc/swordfish) ![GitHub release](https://img.shields.io/github/release/agmcc/swordfish) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=agmcc_swordfish&metric=alert_status)](https://sonarcloud.io/dashboard?id=agmcc_swordfish)

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
    implementation 'com.github.agmcc:swordfish:0.2.0'
    annotationProcessor 'com.github.agmcc:swordfish:0.2.0'
}
```

## Usage

Classes managed by the framework (beans) are annotated with `javax.inject.Named`.
A public constructor, annotated with `javax.inject.Inject` is used to declare dependencies.

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

If a bean has no dependencies, the default constructor is sufficient.

```java
@Named
public class Piston {}
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
  
   @Named
   public Oil () {
     return new Oil();
   }

  @Named
  public Piston piston(Oil oil) {
    return new Piston(oil);
  }
}
```

Beans can also be declared with static methods. This can be useful if you don't want to expose the
enclosing class as a bean and the method doesn't need access to the class instance.

```java
public class NozzleConfig {

  @Named
  public static Nozzle nozzle() {
    return new Nozzle();
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
