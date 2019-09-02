package com.github.agmcc.swordfish.bean;

import com.github.agmcc.swordfish.domain.Bean;
import com.github.agmcc.swordfish.domain.Name;
import com.github.agmcc.swordfish.inject.BeanMethodInjector;
import com.github.agmcc.swordfish.inject.Injector;
import com.google.common.graph.Graph;
import com.google.common.graph.MutableGraph;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
public class GraphBuilder {

  Graph<Bean> buildDependencyGraph(final Set<Bean> beans, final Map<Name, Bean> beanDefinitionMap) {
    final MutableGraph<Bean> graph =
        com.google.common.graph.GraphBuilder.directed().allowsSelfLoops(true).build();

    for (final Bean bean : beans) {
      graph.addNode(bean);

      final Injector injector = bean.getInjector();
      injector.getDependencies().forEach(d -> graph.putEdge(bean, beanDefinitionMap.get(d)));

      if (injector instanceof BeanMethodInjector) {
        final Name beanClassName = ((BeanMethodInjector) injector).getMethodClassName();
        graph.putEdge(bean, beanDefinitionMap.get(beanClassName));
      }
    }
    return graph;
  }
}
