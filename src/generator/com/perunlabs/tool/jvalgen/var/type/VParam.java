package com.perunlabs.tool.jvalgen.var.type;

import com.perunlabs.tool.jvalgen.var.expr.AbstractVExpression;
import com.perunlabs.tool.jvalgen.var.expr.ExpressionValues;
import com.perunlabs.tool.jvalgen.var.expr.VStatement;

public class VParam extends AbstractVExpression {
  private final String name;

  public static VParam vParam(VType vType, String name) {
    return new VParam(vType, name);
  }

  private VParam(VType vType, String name) {
    super(vType);
    this.name = name;
  }

  public String name() {
    return name;
  }

  @Override
  public VStatement create(VTypeKind kind, ExpressionValues expressionValues) {
    return expressionValues.valueFor(this);
  }

  @Override
  public String toString() {
    return "VParam(" + vType().toString() + ", " + name + ")";
  }
}
