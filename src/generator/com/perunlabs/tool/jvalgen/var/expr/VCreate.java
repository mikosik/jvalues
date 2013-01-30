package com.perunlabs.tool.jvalgen.var.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static com.perunlabs.tool.jvalgen.java.type.JExpressions.compAccessOn;
import static com.perunlabs.tool.jvalgen.java.type.JExpressions.toVType;
import static com.perunlabs.tool.jvalgen.var.expr.VExpressions.toVStatement;
import static com.perunlabs.tool.jvalgen.var.expr.VStatements.toJExpression;
import static com.perunlabs.tool.jvalgen.var.expr.VStatements.vStatement;

import com.google.common.collect.ImmutableList;
import com.perunlabs.tool.jvalgen.java.type.JExpression;
import com.perunlabs.tool.jvalgen.var.type.VComponent;
import com.perunlabs.tool.jvalgen.var.type.VType;
import com.perunlabs.tool.jvalgen.var.type.VTypeKind;

public class VCreate implements VExpression {
  private final VType vType;
  private final ImmutableList<VExpression> vExprs;

  public static VCreate vCreate(VType vType, ImmutableList<VExpression> vExprs) {
    return new VCreate(vType, vExprs);
  }

  protected VCreate(VType vType, ImmutableList<VExpression> vExprs) {
    this.vType = vType;
    this.vExprs = vExprs;

    ImmutableList<VComponent> vComps = vType.vComponents();
    checkArgument(vComps.size() == vExprs.size());
    for (int i = 0; i < vComps.size(); i++) {
      VComponent vComp = vComps.get(i);
      VExpression vExpr = vExprs.get(i);
      checkArgument(vComp.vType().equals(vExpr.vType()));
    }
  }

  @Override
  public VType vType() {
    return vType;
  }

  @Override
  public VStatement create(VTypeKind kind, ExpressionValues expressionValues) {
    ImmutableList<VStatement> vStats = toVStatement(vExprs, kind, expressionValues);
    ImmutableList<JExpression> jExprs = toJExpression(vStats);
    JExpression jExprResult = vType.apiCreate(kind, jExprs);
    return vStatement(vType, jExprResult);
  }

  @Override
  public VStatement assign(JExpression destination, ExpressionValues expressionValues) {
    checkArgument(toVType(destination).equals(vType));

    for (int i = 0; i < vType.vComponents().size(); i++) {
      VComponent vComp = vType.vComponents().get(i);
      VExpression vExpr = vExprs.get(i);
      vExpr.assign(compAccessOn(destination, vComp), expressionValues);
    }
    return vStatement(vType, destination);
  }
}
