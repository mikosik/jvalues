package com.perunlabs.tool.jvalgen.var.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static com.perunlabs.tool.jvalgen.java.type.JType.JVOID;
import static com.perunlabs.tool.jvalgen.java.type.JTypes.toVType;
import static com.perunlabs.tool.jvalgen.var.expr.VStatements.vStatement;

import com.perunlabs.tool.jvalgen.java.type.JExpression;
import com.perunlabs.tool.jvalgen.java.type.JType;
import com.perunlabs.tool.jvalgen.var.type.VComponent;
import com.perunlabs.tool.jvalgen.var.type.VType;

public class VStatementImpl implements VStatement {
  private final VType vType;
  private final JExpression jExpression;

  protected VStatementImpl(VType vType, JExpression jExpression) {
    JType exprJType = jExpression.jType();
    if (exprJType != JVOID) {
      checkArgument(vType.equals(toVType(exprJType)));
    }
    this.vType = vType;
    this.jExpression = jExpression;
  }

  @Override
  public JExpression jExpr() {
    return jExpression;
  }

  @Override
  public VStatement componentAccess(VComponent vComponent) {
    if (vType == null) {
      throw new RuntimeException("Non VType statement has no components");
    } else {
      return vStatement(vComponent.vType(), vType.jComponentAccessOn(jExpression, vComponent));
    }
  }
}
