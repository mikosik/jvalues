package com.perunlabs.tool.jvalgen.var.expr;

import com.perunlabs.tool.jvalgen.java.type.JExpression;
import com.perunlabs.tool.jvalgen.var.type.VType;
import com.perunlabs.tool.jvalgen.var.type.VTypeKind;

public interface VExpression {

  public VType vType();

  public VStatement create(VTypeKind kind, ExpressionValues expressionValues);

  public VStatement assign(JExpression destination, ExpressionValues expressionValues);
}
