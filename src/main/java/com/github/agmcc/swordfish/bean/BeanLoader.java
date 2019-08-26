package com.github.agmcc.swordfish.bean;

import com.github.agmcc.swordfish.domain.Bean;
import com.github.agmcc.swordfish.domain.Name;
import com.github.agmcc.swordfish.graph.GraphUtils;
import com.google.common.graph.Graph;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.processing.RoundEnvironment;
import javax.inject.Named;

@SuppressWarnings("UnstableApiUsage")
public class BeanLoader {

  private final BeanMapper beanMapper;

  private final GraphBuilder graphBuilder;

  private final GraphUtils graphUtils;

  public BeanLoader(
      final BeanMapper beanMapper, final GraphBuilder graphBuilder, final GraphUtils graphUtils) {

    this.beanMapper = beanMapper;
    this.graphBuilder = graphBuilder;
    this.graphUtils = graphUtils;
  }

  public Set<Bean> loadBeans(final RoundEnvironment roundEnv) {
    final Set<Bean> beans =
        roundEnv.getElementsAnnotatedWith(Named.class).stream()
            .map(beanMapper::mapBean)
            .collect(Collectors.toSet());

    final Map<Name, Bean> beanDefinitions = buildBeanDefinitionMap(beans);

    final Map<Bean, List<Name>> missing = findMissingDependencies(beans, beanDefinitions);

    if (!missing.isEmpty()) {
      throw new RuntimeException("Missing bean definition(s): " + missing);
    }

    final Graph<Bean> dependencyGraph = graphBuilder.buildDependencyGraph(beans, beanDefinitions);

    if (graphUtils.isCyclic(dependencyGraph)) {
      throw new RuntimeException("Cyclic dependencies");
    }

    return beans;
  }

  private Map<Name, Bean> buildBeanDefinitionMap(final Set<Bean> beans) {
    return beans.stream()
        .collect(
            Collectors.toMap(
                Bean::getName,
                b -> b,
                (b1, b2) -> {
                  if (Objects.equals(b1, b2)) {
                    throw new RuntimeException("Duplicate bean entry: " + b2);
                  }
                  return b1;
                }));
  }

  private Map<Bean, List<Name>> findMissingDependencies(
      final Set<Bean> beans, final Map<Name, Bean> beanDefinitions) {

    final Map<Bean, List<Name>> missing = new HashMap<>();

    for (final Bean bean : beans) {
      for (final Name dependency : bean.getInjector().getDependencies()) {
        if (!beanDefinitions.containsKey(dependency)) {
          missing.computeIfAbsent(bean, k -> new ArrayList<>()).add(dependency);
        }
      }
    }
    return missing;
  }
}
