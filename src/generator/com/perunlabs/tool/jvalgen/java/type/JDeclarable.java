package com.perunlabs.tool.jvalgen.java.type;

import static com.google.common.base.Preconditions.checkArgument;
import static com.perunlabs.tool.jvalgen.Utils.append;
import static com.perunlabs.tool.jvalgen.java.type.JStatements.jStat;

import com.google.common.collect.ImmutableList;

public class JDeclarable extends JExpression {
  private final JModifiers modifiers;
  private final String name;
  private final JExpression initialValue;
  private final boolean addSemicolon;

  public JDeclarable(JModifiers modifiers, JType jType, String name, boolean addSemicolon) {
    this(modifiers, jType, name, null, addSemicolon);
  }

  public JDeclarable(JModifiers modifiers, JType jType, String name, JExpression initialValue) {
    this(modifiers, jType, name, initialValue, true);
  }

  private JDeclarable(JModifiers modifiers, JType jType, String name, JExpression initialValue,
      boolean addSemicolon) {
    super(jType, name, calculateImportsNeeded(jType, initialValue));

    if (initialValue != null) {
      checkArgument(initialValue.jType().isSubTypeOf(jType));
    }

    this.modifiers = modifiers;
    this.name = name;
    this.initialValue = initialValue;
    this.addSemicolon = addSemicolon;
  }

  public JModifiers modifiers() {
    return modifiers;
  }

  public String name() {
    return name;
  }

  private static ImmutableList<JType> calculateImportsNeeded(JType jType, JStatement initialValue) {
    ImmutableList<JType> imports = initialValue == null ? JTypes.empty() : initialValue
        .importsNeeded();
    return append(jType, imports);
  }

  public String declaration() {
    return declarationRaw() + (addSemicolon ? ";" : "");
  }

  public JStatement jDeclaration() {
    return jStat(declarationRaw(), importsNeeded());
  }

  private String declarationRaw() {
    String initialization = initialValue == null ? "" : " = " + initialValue.code();
    return modifiers.code() + jType().name() + " " + name + initialization;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "(" + jType().name() + ")";
  }
}
