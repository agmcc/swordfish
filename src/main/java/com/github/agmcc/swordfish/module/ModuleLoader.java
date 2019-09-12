package com.github.agmcc.swordfish.module;

import com.github.agmcc.swordfish.domain.Bean;
import com.github.agmcc.swordfish.domain.Module;
import com.github.agmcc.swordfish.domain.Name;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.processing.RoundEnvironment;

public class ModuleLoader {

  private static final Name DEFAULT_MODULE_NAME =
      Name.from("com.github.agmcc.swordfish.SwordfishDefaultModule");

  private final ModuleMapper moduleMapper;

  public ModuleLoader(final ModuleMapper moduleMapper) {
    this.moduleMapper = moduleMapper;
  }

  public Set<Module> loadModules(final RoundEnvironment roundEnv, final Set<Bean> beans) {
    final Set<Module> modules =
        roundEnv.getElementsAnnotatedWith(com.github.agmcc.swordfish.annotation.Module.class)
            .stream()
            .map(e -> moduleMapper.mapModule(e, beans))
            .collect(Collectors.toSet());

    final Map<Bean, Name> moduleMap = createModuleMap(beans, modules);

    final Set<Bean> defaultModuleBeans = getDefaultModuleBeans(moduleMap);

    if (!defaultModuleBeans.isEmpty()) {
      modules.add(createDefaultModule(defaultModuleBeans));
    }

    return modules;
  }

  private Map<Bean, Name> createModuleMap(final Set<Bean> beans, final Set<Module> modules) {
    final Map<Bean, Name> moduleMap = new HashMap<>();

    beans:
    for (final Bean bean : beans) {
      for (final Module module : modules) {
        if (module.getBeans().contains(bean)) {
          final Name moduleName = module.getName();
          if (!moduleMap.containsKey(bean)) {
            moduleMap.put(bean, moduleName);
            continue beans;
          } else {
            final Name existing = moduleMap.get(bean);
            throw new RuntimeException(
                String.format(
                    "Duplicate module registration [%s and %s] for bean %s",
                    existing, moduleName, bean));
          }
        }
      }
      moduleMap.put(bean, DEFAULT_MODULE_NAME);
    }

    return moduleMap;
  }

  private Set<Bean> getDefaultModuleBeans(final Map<Bean, Name> moduleMap) {
    return moduleMap.entrySet().stream()
        .filter(e -> e.getValue().equals(DEFAULT_MODULE_NAME))
        .map(Entry::getKey)
        .collect(Collectors.toSet());
  }

  private Module createDefaultModule(final Set<Bean> beans) {
    return new Module(DEFAULT_MODULE_NAME, beans, beans);
  }
}
