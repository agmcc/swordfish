package com.github.agmcc.swordfish.inject;

public interface InjectorVisitor {

  void visit(ConstructorInjector injector);

  void visit(StaticProviderInjector injector);
}
