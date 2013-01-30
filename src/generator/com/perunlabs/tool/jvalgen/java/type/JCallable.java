package com.perunlabs.tool.jvalgen.java.type;

public interface JCallable {
  public JExpression jCall(Iterable<? extends JExpression> args);

  public JType returnJType();
}
