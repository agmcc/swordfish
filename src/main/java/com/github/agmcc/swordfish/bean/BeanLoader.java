package com.github.agmcc.swordfish.bean;

import com.github.agmcc.swordfish.domain.BeanDefinitionType;
import com.github.agmcc.swordfish.domain.BeanElement;
import com.github.agmcc.swordfish.domain.ConstructorDependency;
import com.github.agmcc.swordfish.domain.Dependency;
import com.github.agmcc.swordfish.domain.MethodDependency;
import com.github.agmcc.swordfish.domain.ProviderDependency;
import com.github.agmcc.swordfish.graph.GraphUtils;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.processing.RoundEnvironment;
import javax.inject.Named;
import javax.lang.model.element.Element;

@SuppressWarnings("UnstableApiUsage")
public class BeanLoader {

  private final BeanMapper beanMapper;

  private final GraphUtils graphUtils;

  public BeanLoader(final BeanMapper beanMapper, final GraphUtils graphUtils) {
    this.beanMapper = beanMapper;
    this.graphUtils = graphUtils;
  }

  public ValueGraph<BeanElement, Dependency> loadBeans(final RoundEnvironment roundEnv) {
    final Set<? extends Element> namedElements = roundEnv.getElementsAnnotatedWith(Named.class);

    final Set<BeanElement> beanElements =
        namedElements.stream().map(beanMapper::mapBeanElement).collect(Collectors.toSet());

    final Map<String, BeanElement> beanDefinitions = beanDefinitionMap(beanElements);

    final Map<BeanElement, List<String>> missing =
        missingDependencies(beanElements, beanDefinitions);

    if (!missing.isEmpty()) {
      throw new RuntimeException("Missing bean definition(s): " + missing);
    }

    final ValueGraph<BeanElement, Dependency> dependencyValueGraph =
        dependencyValueGraph(beanElements, beanDefinitions);

    if (graphUtils.isCyclic(dependencyValueGraph)) {
      throw new RuntimeException("Cyclic dependencies");
    }

    return dependencyValueGraph;
  }

  private Map<String, BeanElement> beanDefinitionMap(final Set<BeanElement> beanElements) {
    return beanElements.stream()
        .collect(
            Collectors.toMap(
                BeanElement::getQualifiedName,
                b -> b,
                (b1, b2) -> {
                  if (Objects.equals(b1, b2)) {
                    throw new RuntimeException("Duplicate bean entry: " + b2);
                  }
                  return b1;
                }));
  }

  private Map<BeanElement, List<String>> missingDependencies(
      final Set<BeanElement> beanElements, final Map<String, BeanElement> beanDefinitions) {
    final Map<BeanElement, List<String>> missing = new HashMap<>();
    for (final BeanElement beanElement : beanElements) {
      for (final String dependency : beanElement.getDependencies()) {
        if (!beanDefinitions.containsKey(dependency)) {
          missing.computeIfAbsent(beanElement, k -> new ArrayList<>()).add(dependency);
        }
      }
    }
    return missing;
  }

  private ValueGraph<BeanElement, Dependency> dependencyValueGraph(
      final Set<BeanElement> beanElements, final Map<String, BeanElement> beanDefinitionMap) {
    final MutableValueGraph<BeanElement, Dependency> graph =
        ValueGraphBuilder.directed().allowsSelfLoops(true).build();

    for (final BeanElement beanElement : beanElements) {
      graph.addNode(beanElement);
      beanElement
          .getDependencies()
          .forEach(d -> addBean(beanElement, beanDefinitionMap.get(d), graph));

      if (beanElement.getBeanDefinitionType() == BeanDefinitionType.METHOD) {
        graph.putEdgeValue(
            beanElement,
            beanDefinitionMap.get(beanElement.getProvider().getEnclosingElement().toString()),
            new ProviderDependency());
      }
    }

    return graph;
  }

  private void addBean(
      final BeanElement bean,
      final BeanElement dependency,
      final MutableValueGraph<BeanElement, Dependency> graph) {
    final int index;

    switch (bean.getBeanDefinitionType()) {
      case CLASS:
        index = bean.getDependencies().indexOf(dependency.getQualifiedName());
        graph.putEdgeValue(bean, dependency, new ConstructorDependency(index));
        break;
      case METHOD:
        index = bean.getDependencies().indexOf(dependency.getQualifiedName());
        graph.putEdgeValue(
            bean,
            dependency,
            new MethodDependency(index, dependency.getProvider().getSimpleName().toString(), ""));
        break;
      default:
        throw new RuntimeException(
            "Unsupported bean definition type: " + bean.getBeanDefinitionType());
    }
  }
}
