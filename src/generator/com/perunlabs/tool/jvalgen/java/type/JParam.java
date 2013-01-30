package com.perunlabs.tool.jvalgen.java.type;


public class JParam extends JDeclarable {
  public static JParam jParam(JType jType, String name) {
    return new JParam(JModifiers.noModifiers(), jType, name);
  }

  public static JParam jParamFinal(JType jType, String name) {
    return new JParam(JModifiers.finalJParam(), jType, name);
  }

  private JParam(JModifiers modifiers, JType jType, String name) {
    super(modifiers, jType, name, false);
  }

  public JParam toFinal() {
    return jParamFinal(jType(), name());
  }
}
