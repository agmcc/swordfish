package com.github.agmcc.swordfish.inject;

import com.github.agmcc.swordfish.domain.Name;
import java.util.List;

public interface Injector {

  void accept(InjectorVisitor visitor);

  List<Name> getDependencies();
}
