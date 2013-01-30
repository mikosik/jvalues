package com.perunlabs.tool.jvalgen.java.type;

import static com.perunlabs.tool.jvalgen.java.type.JExpressions.convertedExpr;
import static com.perunlabs.tool.jvalgen.java.type.JExpressions.jExpr;
import static com.perunlabs.tool.jvalgen.java.type.JStatements.toCode;
import static com.perunlabs.tool.jvalgen.java.type.JStatements.toImportsNeeded;
import static com.perunlabs.tool.jvalgen.java.type.JType.JBOOLEAN;

import com.google.common.base.Joiner;

public class JBooleanCallable implements JCallable {
  private final String operation;

  public JBooleanCallable(String operation) {
    this.operation = " " + operation + " ";
  }

  @Override
  public JExpression jCall(Iterable<? extends JExpression> args) {
    String code = Joiner.on(operation).join(toCode(convertedExpr(args, JBOOLEAN)));
    return jExpr(JBOOLEAN, code, toImportsNeeded(args));
  }

  @Override
  public JType returnJType() {
    return JBOOLEAN;
  }
}
