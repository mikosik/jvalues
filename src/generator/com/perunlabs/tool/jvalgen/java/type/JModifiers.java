package com.perunlabs.tool.jvalgen.java.type;

public class JModifiers {
  private final JAccess access;
  private final boolean isStatic;
  private final boolean isFinal;

  public static JModifiers noModifiers() {
    return new JModifiers(JAccess.NONE, false, false);
  }

  public static JModifiers constJField() {
    return new JModifiers(JAccess.PUBLIC, true, true);
  }

  public static JModifiers privateFinal() {
    return new JModifiers(JAccess.PRIVATE, false, true);
  }

  public static JModifiers privateJField() {
    return new JModifiers(JAccess.PRIVATE, false, false);
  }

  public static JModifiers finalJParam() {
    return new JModifiers(JAccess.NONE, false, true);
  }

  private JModifiers(JAccess access, boolean isStatic, boolean isFinal) {
    this.access = access;
    this.isStatic = isStatic;
    this.isFinal = isFinal;
  }

  public String code() {
    String staticness = isStatic ? "static " : "";
    String mutability = isFinal ? "final " : "";
    return access.code() + staticness + mutability;
  }

  public JModifiers toFinal() {
    return new JModifiers(access, isStatic, true);
  }
}
