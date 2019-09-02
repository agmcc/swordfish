package com.github.agmcc.swordfish.inject;

public interface InjectorVisitor {

  void visit(ConstructorInjector injector);

  void visit(BeanMethodInjector injector);

  void visit(StaticMethodInjector injector);
}
