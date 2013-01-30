package com.perunlabs.tool.jvalgen.java.type;

/**
 * Java field.
 */
public class JField extends JDeclarable {
  public static JField jFieldConst(JType jType, String name, JExpression initialValue) {
    return new JField(JModifiers.constJField(), jType, name, initialValue);
  }

  public static JField jField(JType jType, String name) {
    return jField(JModifiers.privateJField(), jType, name);
  }

  public static JField jFieldFinal(JType jType, String name) {
    return jField(JModifiers.privateFinal(), jType, name);
  }

  public static JField jField(JModifiers modifiers, JType jType, String name) {
    return new JField(modifiers, jType, name, null);
  }

  private JField(JModifiers modifiers, JType jType, String name, JExpression initialValue) {
    super(modifiers, jType, name, initialValue);
  }

  @Override
  public String code() {
    return "this." + name();
  }

  public JField toFinal() {
    return jField(modifiers().toFinal(), jType(), name());
  }
}
