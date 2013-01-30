package com.perunlabs.tool.jvalgen.java.type;

import static com.perunlabs.tool.jvalgen.Utils.list;

import com.google.common.collect.ImmutableList;

public class JStatement implements JBlock {
  private final String code;
  private final ImmutableList<JType> importsNeeded;

  public JStatement(String code) {
    this(code, JTypes.empty());
  }

  public JStatement(String code, ImmutableList<JType> importsNeeded) {
    this.code = code;
    this.importsNeeded = importsNeeded;
  }

  public String code() {
    return code;
  }

  public ImmutableList<JType> importsNeeded() {
    return importsNeeded;
  }

  @Override
  public ImmutableList<JStatement> jStatements() {
    return list(this);
  }

  @Override
  public int hashCode() {
    return code.hashCode();
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof JStatement) {
      JStatement that = (JStatement) object;
      return this.code.equals(that.code);
    }
    return false;
  }

  @Override
  public String toString() {
    return "'" + code + "'";
  }
}
