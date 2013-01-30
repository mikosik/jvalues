package com.perunlabs.tool.jvalgen.java.type;


public class JReturn extends JStatement {
  private final JType returnedJType;

  protected JReturn(JExpression statement) {
    super("return " + statement.code(), statement.importsNeeded());
    this.returnedJType = statement.jType();
  }

  public JType returnedJType() {
    return returnedJType;
  }
}
