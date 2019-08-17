package com.github.agmcc.swordfish.processor;

import com.github.agmcc.swordfish.processor.Processor.Bean.BeanType;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

/**
 * Minimal implementation of DI using annotation processing.
 *
 * <p>Only supports constructor injection with {@link Inject}. Beans are declared via {@link Named}.
 * Qualifying via names is not supported.
 *
 * <p>Beans can also be provided via named methods declared in a named bean. They must be public and
 * non-static. Parameters for such methods will be injected.
 */
public class Processor extends AbstractProcessor {

  private Elements elementUtils;

  private Types typeUtils;

  private Filer filer;

  private Messager messager;

  @Override
  public synchronized void init(final ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    elementUtils = processingEnv.getElementUtils();
    typeUtils = processingEnv.getTypeUtils();
    filer = processingEnv.getFiler();
    messager = processingEnv.getMessager();
  }

  @Override
  public boolean process(
      final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {

    final List<Bean> beans = new ArrayList<>();

    // Load Named beans
    for (final Element namedElement : roundEnv.getElementsAnnotatedWith(Named.class)) {
      if (namedElement.getKind() == ElementKind.CLASS) {
        final TypeElement typeElement = (TypeElement) namedElement;

        final Bean bean = new Bean(typeElement.asType(), BeanType.CLASS);

        for (final Element enclosedElement : typeElement.getEnclosedElements()) {
          final Set<Modifier> modifiers = enclosedElement.getModifiers();

          // Find public, @Inject-annotated constructor
          if (enclosedElement.getKind() == ElementKind.CONSTRUCTOR
              && enclosedElement.getAnnotation(Inject.class) != null
              && modifiers.contains(Modifier.PUBLIC)) {

            final ExecutableElement constructorElement = (ExecutableElement) enclosedElement;

            // Add constructor dependencies
            for (final VariableElement parameter : constructorElement.getParameters()) {
              bean.addDependency(parameter.asType());
            }
          }

          // Method providers - must be @Named-annotated, public and static
          if (enclosedElement.getKind() == ElementKind.METHOD
              && enclosedElement.getAnnotation(Named.class) != null) {

            final ExecutableElement methodElement = (ExecutableElement) enclosedElement;
            bean.addProvider(methodElement);

            final Bean provided = new Bean(methodElement.getReturnType(), BeanType.PROVIDED);
            beans.add(provided);
          }
        }

        beans.add(bean);
      }
    }

    // Generate factories
    final MustacheFactory mustacheFactory = new DefaultMustacheFactory();
    final Mustache template = mustacheFactory.compile("templates/Factory.mustache");

    for (final Bean bean : beans) {
      // Create template model
      final Name type = typeUtils.asElement(bean.getType()).getSimpleName();
      final Name packageName =
          elementUtils.getPackageOf(typeUtils.asElement(bean.getType())).getQualifiedName();

      final String creator;

      if (bean.getBeanType() == BeanType.CLASS) {
        // Class bean - invoke constructor with factories args
        final String args =
            bean.getDependencies().stream()
                .map(
                    t -> {
                      final Bean dependency =
                          beans.stream()
                              .filter(b -> b.getType().equals(t))
                              .findAny()
                              .orElseThrow(
                                  () ->
                                      new RuntimeException(
                                          String.format("Missing dependency for %s: %s", bean, t)));

                      return String.format(
                          "%sFactory.getInstance()",
                          typeUtils.asElement(dependency.getType()).getSimpleName());
                    })
                .collect(Collectors.joining(", "));

        creator = String.format("new %s(%s)", type, args);
      } else {
        // Provided bean - call method on provider method
        final ExecutableElement provider =
            beans.stream()
                .map(Bean::getProviders)
                .flatMap(List::stream)
                .filter(p -> p.getReturnType().equals(bean.getType()))
                .findAny()
                .orElseThrow(NoSuchElementException::new);

        final String args =
            provider.getParameters().stream()
                .map(Element::asType)
                .map(
                    t -> {
                      final Bean dependency =
                          beans.stream()
                              .filter(b -> b.getType().equals(t))
                              .findAny()
                              .orElseThrow(NoSuchElementException::new);

                      return String.format(
                          "%sFactory.getInstance()",
                          typeUtils.asElement(dependency.getType()).getSimpleName());
                    })
                .collect(Collectors.joining(", "));

        creator =
            String.format(
                "%sFactory.getInstance().%s(%s)",
                provider.getEnclosingElement(), provider.getSimpleName(), args);
      }

      final Factory factory = new Factory(type, packageName, creator);

      // Execute template and write file
      try {
        final JavaFileObject jfo = filer.createSourceFile(bean.toString().concat("Factory"));
        try (final Writer writer = jfo.openWriter()) {
          template.execute(writer, factory);
        }
      } catch (final IOException e) {
        e.printStackTrace();
      }
    }
    return false;
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return Stream.of(Named.class, Inject.class)
        .map(Class::getCanonicalName)
        .collect(Collectors.toSet());
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  private void error(final String msg, final Object... args) {
    messager.printMessage(Kind.ERROR, String.format(msg, args));
  }

  public static class Bean {

    private final TypeMirror type;

    private final List<TypeMirror> dependencies = new ArrayList<>();

    private final List<ExecutableElement> providers = new ArrayList<>();

    private final BeanType beanType;

    public Bean(final TypeMirror type, final BeanType beanType) {
      this.type = type;
      this.beanType = beanType;
    }

    public void addDependency(final TypeMirror type) {
      dependencies.add(type);
    }

    public void addProvider(final ExecutableElement element) {
      providers.add(element);
    }

    public TypeMirror getType() {
      return type;
    }

    public List<TypeMirror> getDependencies() {
      return dependencies;
    }

    public List<ExecutableElement> getProviders() {
      return providers;
    }

    public BeanType getBeanType() {
      return beanType;
    }

    @Override
    public String toString() {
      return type.toString();
    }

    public enum BeanType {
      CLASS,
      PROVIDED
    }
  }

  public static class Factory {

    private final Name type;

    private final Name packageName;

    private final String creator;

    private final String processor;

    public Factory(final Name type, final Name packageName, final String creator) {
      this.type = type;
      this.packageName = packageName;
      this.creator = creator;
      processor = Processor.class.getCanonicalName();
    }

    public Name getType() {
      return type;
    }

    public Name getPackageName() {
      return packageName;
    }

    public String getCreator() {
      return creator;
    }

    public String getProcessor() {
      return processor;
    }
  }
}
