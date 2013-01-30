package com.perunlabs.tool.jvalgen.var.expr;

import static com.google.common.base.Preconditions.checkArgument;

import com.perunlabs.tool.jvalgen.var.type.VComponent;
import com.perunlabs.tool.jvalgen.var.type.VType;
import com.perunlabs.tool.jvalgen.var.type.VTypeKind;

public class VComponentAccess extends AbstractVExpression {
  private final VExpression vExpression;
  private final VComponent vComponent;

  protected VComponentAccess(VExpression vExpression, VComponent vComponent) {
    super(vComponent.vType());
    this.vExpression = vExpression;
    this.vComponent = vComponent;

    VType vType = vExpression.vType();
    checkArgument(vType.hasComponent(vComponent), vType.toString() + " does not have component "
        + vComponent);
  }

  @Override
  public VStatement create(VTypeKind kind, ExpressionValues expressionValues) {
    return vExpression.create(kind, expressionValues).componentAccess(vComponent);
  }
}
