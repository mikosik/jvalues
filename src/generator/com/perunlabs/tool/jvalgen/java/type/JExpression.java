package com.perunlabs.tool.jvalgen.java.type;

import com.google.common.collect.ImmutableList;

public class JExpression extends JStatement {
  private final JType jType;

  public JExpression(String code, JType type) {
    this(type, code, JTypes.empty());
  }

  public JExpression(JType jType, String code, ImmutableList<JType> importsNeeded) {
    super(code, importsNeeded);
    this.jType = jType;
  }

  public JType jType() {
    return jType;
  }

  @Override
  public int hashCode() {
    return jType.hashCode() + super.hashCode();
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof JExpression) {
      JExpression that = (JExpression) object;
      return super.equals(object) && this.jType().equals(that.jType());
    }
    return false;
  }

  @Override
  public String toString() {
    return jType.toString() + super.toString();
  }
}
