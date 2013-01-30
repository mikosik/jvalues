package com.perunlabs.tool.jvalgen.java.type;

public class JLocalVar extends JDeclarable {

  public static JLocalVar jLocalVar(JType jType, String name) {
    return new JLocalVar(jType, name, null);
  }

  public static JLocalVar jLocalVar(String name, JExpression initialValue) {
    return new JLocalVar(initialValue.jType(), name, initialValue);
  }

  public static JLocalVar jLocalVar(JType jType, String name, JExpression initialValue) {
    return new JLocalVar(jType, name, initialValue);
  }

  private JLocalVar(JType jType, String name, JExpression initialValue) {
    super(JModifiers.noModifiers(), jType, name, initialValue);
  }
}
