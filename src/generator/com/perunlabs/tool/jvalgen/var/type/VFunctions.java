package com.perunlabs.tool.jvalgen.var.type;

import static com.google.common.base.Preconditions.checkArgument;
import static com.perunlabs.tool.jvalgen.Utils.append;
import static com.perunlabs.tool.jvalgen.Utils.list;
import static com.perunlabs.tool.jvalgen.Utils.skipOne;
import static com.perunlabs.tool.jvalgen.var.expr.VComponentAccesss.vComponentAccess;
import static com.perunlabs.tool.jvalgen.var.expr.VCreate.vCreate;
import static com.perunlabs.tool.jvalgen.var.type.VParam.vParam;

import com.google.common.collect.ImmutableList;
import com.perunlabs.tool.jvalgen.var.expr.VComponentAccess;
import com.perunlabs.tool.jvalgen.var.expr.VExpression;

public class VFunctions {

  public static ExpressionVFunction vFunction(VType owner, VType returnVType, String name,
      ImmutableList<VParam> vParams, VExpression resultVExpr, boolean isInvolutary) {
    return new ExpressionVFunction(owner, returnVType, name, vParams, resultVExpr, isInvolutary);
  }

  public static ExpressionVFunction compoundLowVFunction(VType paramVType, VFunction innerOperation) {
    checkArgument(paramVType.areComponentTypesEqual(innerOperation.vParams().get(0).vType()));

    VParam p1 = vParam(paramVType, "v1");
    ImmutableList<VParam> rest = skipOne(innerOperation.vParams());

    ImmutableList.Builder<VExpression> builder = ImmutableList.builder();
    for (VComponent vComponent : paramVType.vComponents()) {
      VComponentAccess p1Component = vComponentAccess(p1, vComponent);
      ImmutableList<VExpression> args = append(p1Component, rest);
      builder.add(innerOperation.vCall(args));
    }

    ImmutableList<VExpression> vExpressions = builder.build();
    VExpression result = vCreate(paramVType, vExpressions);

    ImmutableList<VParam> vParams = append(p1, rest);
    return vFunction(paramVType, paramVType, innerOperation.name(), vParams, result,
        innerOperation.isInvolutary());
  }

  public static ExpressionVFunction compoundHighVFunction(VType paramVType, VFunction innerOperation) {
    int paramCount = innerOperation.vParams().size();
    checkArgument(paramCount <= 2);

    VParam p1 = vParam(paramVType, "v1");
    ImmutableList<VParam> rest = paramCount == 1 ? VParams.empty() : list(vParam(paramVType, "v2"));

    ImmutableList.Builder<VExpression> builder = ImmutableList.builder();
    for (VComponent vComponent : paramVType.vComponents()) {
      VComponentAccess p1Component = vComponentAccess(p1, vComponent);
      ImmutableList<VComponentAccess> restComponents = vComponentAccess(rest, vComponent);
      ImmutableList<VComponentAccess> args = append(p1Component, restComponents);
      builder.add(innerOperation.vCall(args));
    }

    ImmutableList<VExpression> vExpressions = builder.build();
    VExpression result = vCreate(paramVType, vExpressions);

    ImmutableList<VParam> vParam = append(p1, rest);
    return vFunction(paramVType, paramVType, innerOperation.name(), vParam, result,
        innerOperation.isInvolutary());
  }
}
