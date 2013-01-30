package com.perunlabs.tool.jvalgen.var.expr;

import static com.perunlabs.tool.jvalgen.java.type.JTypes.toVType;
import static com.perunlabs.tool.jvalgen.var.expr.VStatements.vStatement;

import com.perunlabs.tool.jvalgen.java.type.JExpression;
import com.perunlabs.tool.jvalgen.var.type.VTypeKind;

public class VConst extends AbstractVExpression {
  private final JExpression jExpression;

  public static VConst vConst(JExpression jExpression) {
    return new VConst(jExpression);
  }

  private VConst(JExpression jExpression) {
    super(toVType(jExpression.jType()));
    this.jExpression = jExpression;
  }

  @Override
  public VStatement create(VTypeKind kind, ExpressionValues expressionValues) {
    return vStatement(vType(), jExpression);
  }
}
