package com.github.agmcc.swordfish.module;

import com.github.agmcc.swordfish.domain.Bean;
import com.github.agmcc.swordfish.domain.Module;
import com.github.agmcc.swordfish.domain.ModuleElement;
import com.github.agmcc.swordfish.domain.Name;
import com.github.agmcc.swordfish.graph.GraphUtils;
import com.github.agmcc.swordfish.util.SetUtils;
import com.google.common.graph.Graph;
import com.google.common.graph.MutableGraph;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import javax.annotation.processing.RoundEnvironment;

@SuppressWarnings("UnstableApiUsage")
public class ModuleLoader {

  private static final Name DEFAULT_MODULE_NAME =
      Name.from("com.github.agmcc.swordfish.DefaultModule");

  private final ModuleMapper moduleMapper;

  private final GraphUtils graphUtils;

  public ModuleLoader(final ModuleMapper moduleMapper, final GraphUtils graphUtils) {
    this.moduleMapper = moduleMapper;
    this.graphUtils = graphUtils;
  }

  public Set<Module> loadModules(final RoundEnvironment roundEnv, final Set<Bean> globalBeans) {

    final Set<ModuleElement> moduleElements =
        roundEnv.getElementsAnnotatedWith(com.github.agmcc.swordfish.annotation.Module.class)
            .stream()
            .map(moduleMapper::mapModuleElement)
            .collect(Collectors.toSet());

    final Graph<ModuleElement> moduleElementGraph = buildModuleElementGraph(moduleElements);

    if (graphUtils.isCyclic(moduleElementGraph)) {
      throw new RuntimeException("Cyclic module dependencies");
    }

    final Map<Name, Bean> globalNameBeanMap =
        globalBeans.stream().collect(Collectors.toMap(Bean::getName, b -> b));

    final Set<Module> modules = new TreeSet<>(Comparator.comparing(Module::getName));

    for (final ModuleElement moduleElement : moduleElementGraph.nodes()) {
      final Set<Bean> localBeans =
          globalBeans.stream()
              .filter(b -> isIncluded(b, moduleElement))
              .collect(Collectors.toSet());

      final Iterator<ModuleElement> iterator =
          graphUtils.dfsIterator(moduleElement, moduleElementGraph);

      final Set<Bean> localPublishedBeans = new HashSet<>();
      final Set<Bean> transitivePublishedBeans = new HashSet<>();

      // Load local and transitive published beans - all must exist in global scope
      while (iterator.hasNext()) {
        final ModuleElement next = iterator.next();

        final Set<Bean> published =
            next.getPublished().stream()
                .map(
                    p ->
                        Optional.ofNullable(globalNameBeanMap.get(p))
                            .orElseThrow(
                                () ->
                                    new RuntimeException(
                                        "Published bean not defined globally: " + p)))
                .collect(Collectors.toSet());

        if (next == moduleElement) {
          localPublishedBeans.addAll(published);
        } else {
          transitivePublishedBeans.addAll(published);
        }
      }

      // Local published beans must exist in local bean scope or transitive published scope
      final Set<Bean> moduleScope = new HashSet<>(localBeans);
      moduleScope.addAll(transitivePublishedBeans);

      localPublishedBeans.forEach(
          p -> {
            if (!moduleScope.contains(p)) {
              throw new RuntimeException(
                  String.format(
                      "Published bean %s does not exist in module %s's scope", p, moduleElement));
            }
          });

      final Graph<Bean> localBeanGraph = buildModuleBeanGraph(localBeans, globalNameBeanMap);

      // For each local bean, check if all transitive dependencies exist in module scope
      for (final Bean localBean : localBeans) {
        final Iterator<Bean> localBeanIterator = graphUtils.dfsIterator(localBean, localBeanGraph);

        while (localBeanIterator.hasNext()) {
          final Bean next = localBeanIterator.next();

          if (!moduleScope.contains(next)) {
            throw new RuntimeException(
                String.format(
                    "Dependency %s for bean %s does not exist in module %s's scope",
                    next, localBean, moduleElement));
          }
        }
      }

      modules.add(new Module(moduleElement.getName(), localBeans, localPublishedBeans));
    }

    final Set<Bean> moduleRegisteredBeans =
        modules.stream().map(Module::getBeans).flatMap(Set::stream).collect(Collectors.toSet());

    final Set<Bean> unregisteredBeans = SetUtils.subtract(globalBeans, moduleRegisteredBeans);

    if (!unregisteredBeans.isEmpty()) {
      modules.add(createDefaultModule(unregisteredBeans));
    }

    return modules;
  }

  private boolean isIncluded(final Bean bean, final ModuleElement moduleElement) {
    return moduleElement.getPackages().contains(bean.getName().getPackageName());
  }

  private Graph<ModuleElement> buildModuleElementGraph(final Set<ModuleElement> moduleElements) {
    final MutableGraph<ModuleElement> graph =
        com.google.common.graph.GraphBuilder.directed().allowsSelfLoops(true).build();

    final Map<Name, ModuleElement> nameModuleElementMap =
        moduleElements.stream().collect(Collectors.toMap(ModuleElement::getName, m -> m));

    for (final ModuleElement moduleElement : moduleElements) {
      graph.addNode(moduleElement);

      for (final Name use : moduleElement.getUses()) {
        graph.putEdge(
            moduleElement,
            Optional.ofNullable(nameModuleElementMap.get(use))
                .orElseThrow(() -> new RuntimeException("Module dependency not found: " + use)));
      }
    }

    return graph;
  }

  private Graph<Bean> buildModuleBeanGraph(
      final Set<Bean> moduleBeans, final Map<Name, Bean> globalBeanMap) {
    final MutableGraph<Bean> graph =
        com.google.common.graph.GraphBuilder.directed().allowsSelfLoops(true).build();

    for (final Bean bean : moduleBeans) {
      graph.addNode(bean);

      for (final Name dependency : bean.getInjector().getDependencies()) {
        graph.putEdge(
            bean,
            Optional.ofNullable(globalBeanMap.get(dependency))
                .orElseThrow(
                    () ->
                        new RuntimeException("Bean dependency not found globally: " + dependency)));
      }
    }

    return graph;
  }

  private Module createDefaultModule(final Set<Bean> beans) {
    return new Module(DEFAULT_MODULE_NAME, beans, beans);
  }
}
