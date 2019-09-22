# Swordfish

[![Build Status](https://travis-ci.org/agmcc/swordfish.svg?branch=master)](https://travis-ci.org/agmcc/swordfish) ![GitHub release](https://img.shields.io/github/release/agmcc/swordfish) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=agmcc_swordfish&metric=alert_status)](https://sonarcloud.io/dashboard?id=agmcc_swordfish)

A compile-time dependency injection framework for Java, using JSR-330 annotations.

## Requirements

* JDK 8+

## Installation

### Example with Gradle (5.x)

The project can be built using Gradle *source dependencies* 
(see: https://blog.gradle.org/introducing-source-dependencies).

Add the following to *settings.gradle*:

```groovy
sourceControl {
    gitRepository("https://github.com/agmcc/swordfish.git") {
        producesModule("com.github.agmcc:swordfish")
    }
}
```

Gradle will check out the source from Github and compile it.  You can then add the following to 
your *build.gradle* file:

```groovy
dependencies {
    implementation 'com.github.agmcc:swordfish:0.4.0'
    annotationProcessor 'com.github.agmcc:swordfish:0.4.0'
}
```

You can also specify a different version, in which case Gradle will try to build against that Git tag.

Alternatively, you can build from source (see *Building*).

## Usage

### Basics

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

A factory class is generated for each bean, which creates a fully constructed instance
(including transitive dependencies).

The bean is then accessible via the default module in `com.github.agmcc.swordfish.Swordfish`.

```java
public static void main(String[] args) {
  Engine engine = Swordfish.defaultModule().engine();
}
```

### Bean methods

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
enclosing class as a bean and the method doesn't need to access the class instance.

```java
public class NozzleConfig {

  @Named
  public static Nozzle nozzle() {
    return new Nozzle();
  }
}
```

### Modules

In addition to the default module, custom modules can be created by annotating an interface with 
`com.github.agmcc.swordfish.annotation.Module`.

This allows beans to be grouped together and only a subset of those beans exposed, which allows 
for better encapsulation. 

Beans are added to the module's scope with the `packages` field and exposed by declaring abstract
methods. 

```java
@Module(packages = "swordfish")
public interface EngineModule {
  
   Engine engine();
}
```

In the example above, all beans in the `swordfish` package are added to the `EngineModule` scope, but only
`Engine` is exposed. This bean can then be retrieved using the generated method on  the `Swordfish` class: 

```java
Engine engine = Swordfish.engineModule().engine();
```

Beans that are added to custom modules are not available in other modules, including the 
default module. However, any beans that are not explicitly added to a module declaration will
still be accessible from the default module.

Modules can also import other modules, via `uses`:

```java
// ApiModule.java
@Module(packages = "api", uses = DataModule.class)
public interface ApiModule {

   Controller controller();
}

// DataModule.java
@Module(packages = "data")
public interface DataModule {

   Database database();
}
```

In the above example, all beans in the ApiModule can access the Database bean defined by DataModule,
as it is imported into the current module scope via `uses`. However, none of the other beans from the DataModule
are exposed, so these aren't leaked to the ApiModule.

## Building

Clone Swordfish and run the following in the root directory:

```bash
./gradlew publishToMavenLocal
```

Then add the following to your project:

```groovy
repositories {
    mavenLocal()
}

dependencies {
    implementation 'com.github.agmcc:swordfish:0.4.0'
    annotationProcessor 'com.github.agmcc:swordfish:0.4.0'
}
```

Or if you just want to compile and run the tests:

```bash
./gradlew build
```
