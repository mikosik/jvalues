package com.perunlabs.tool.jvalgen.var.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static com.perunlabs.tool.jvalgen.java.type.JExpressions.toVType;
import static com.perunlabs.tool.jvalgen.java.type.JStatements.jSet;
import static com.perunlabs.tool.jvalgen.var.expr.VStatements.vStatement;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.MostPrimitiveG;

import com.perunlabs.tool.jvalgen.java.type.JExpression;
import com.perunlabs.tool.jvalgen.var.type.VType;

public abstract class AbstractVExpression implements VExpression {
  private final VType vType;

  public AbstractVExpression(VType vType) {
    this.vType = vType;
  }

  public VType vType() {
    return vType;
  }

  @Override
  public VStatement assign(JExpression jDestination, ExpressionValues expressionValues) {
    checkArgument(toVType(jDestination).equals(vType()));

    VStatement vStatement = create(MostPrimitiveG, expressionValues);
    JExpression jSource = vStatement.jExpr();
    if (!jSource.code().equals(jDestination.code())) {
      expressionValues.addAssignmentJStatement(jSet(jDestination, jSource));
    }
    return vStatement(vType, jDestination);
  }
}
