package com.perunlabs.tool.jvalgen.var.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static com.perunlabs.tool.jvalgen.Utils.append;
import static com.perunlabs.tool.jvalgen.Utils.skipOne;
import static com.perunlabs.tool.jvalgen.java.type.JExpressions.jCall;
import static com.perunlabs.tool.jvalgen.java.type.JExpressions.toVType;
import static com.perunlabs.tool.jvalgen.java.type.JStatements.jSet;
import static com.perunlabs.tool.jvalgen.java.type.JType.JVOID;
import static com.perunlabs.tool.jvalgen.var.expr.VExpressions.toVStatement;
import static com.perunlabs.tool.jvalgen.var.expr.VStatements.toJExpression;
import static com.perunlabs.tool.jvalgen.var.expr.VStatements.vStatement;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.MostPrimitiveG;

import com.google.common.collect.ImmutableList;
import com.perunlabs.tool.jvalgen.java.type.JExpression;
import com.perunlabs.tool.jvalgen.java.type.JType;
import com.perunlabs.tool.jvalgen.var.type.VFunction;
import com.perunlabs.tool.jvalgen.var.type.VParam;
import com.perunlabs.tool.jvalgen.var.type.VType;
import com.perunlabs.tool.jvalgen.var.type.VTypeKind;

public class VCall implements VExpression {
  private final VFunction vFunction;
  private final ImmutableList<? extends VExpression> paramExprs;

  public static VCall vCall(VFunction vFunction, ImmutableList<? extends VExpression> args) {
    return new VCall(vFunction, args);
  }

  private VCall(VFunction vFunction, ImmutableList<? extends VExpression> args) {
    this.vFunction = vFunction;
    this.paramExprs = args;

    ImmutableList<VParam> vParams = vFunction.vParams();
    checkArgument(vParams.size() == args.size());
    for (int i = 0; i < vParams.size(); i++) {
      VExpression arg = args.get(i);
      VParam vParam = vParams.get(i);

      VType argVType = arg.vType();
      VType paramVType = vParam.vType();
      checkArgument(argVType.equals(paramVType), "Incorrect argument type for vParam " + i
          + " expected " + paramVType + " actual " + argVType);
    }
  }

  @Override
  public VType vType() {
    return vFunction.returnVType();
  }

  @Override
  public VStatement create(VTypeKind kind, ExpressionValues expressionValues) {
    ImmutableList<VStatement> argVStatements = toVStatement(paramExprs, kind, expressionValues);
    ImmutableList<JExpression> argJStatements = toJExpression(argVStatements);
    VType retType = vFunction.returnVType();
    return vStatement(retType, vFunction.apiJCall(kind, argJStatements));
  }

  @Override
  public VStatement assign(JExpression destination, ExpressionValues expressionValues) {
    checkArgument(toVType(destination).equals(vType()));

    VType retType = vFunction.returnVType();
    if (expressionValues.hasValueFor(this)) {
      VStatement vStatement = expressionValues.valueFor(this);
      expressionValues.addAssignmentJStatement(jSet(destination, vStatement.jExpr()));
      return vStatement(retType, destination);
    } else {
      VExpression firstParam = paramExprs.get(0);
      if (vFunction.owner().equals(firstParam.vType()) && vFunction.isEndofunctionForOwner()) {

        VStatement firstV = firstParam.assign(destination, expressionValues);
        ImmutableList<VStatement> xxx = toVStatement(skipOne(paramExprs), MostPrimitiveG,
            expressionValues);
        ImmutableList<JExpression> restJ = toJExpression(xxx);

        JExpression firstJ = firstV.jExpr();
        String firstJCode = firstJ.code();
        if (destination.code().equals(firstJCode)) {
          String callString = firstJCode + "." + vFunction.name();
          ImmutableList<JType> imports = firstJ.importsNeeded();
          expressionValues.addAssignmentJStatement(jCall(JVOID, callString, restJ, imports));
          expressionValues.addVStatement(this, firstV);

          return firstV;
        }

        ImmutableList<JExpression> jArgs = append(firstJ, restJ);
        JExpression jExpr = vFunction.apiJCall(MostPrimitiveG, jArgs);
        expressionValues.addAssignmentJStatement(jSet(destination, jExpr));
        return vStatement(retType, destination);
      }

      ImmutableList<VStatement> vArgs = toVStatement(paramExprs, MostPrimitiveG, expressionValues);
      ImmutableList<JExpression> jArgs = toJExpression(vArgs);
      JExpression jExpr = vFunction.apiJCall(MostPrimitiveG, jArgs);
      expressionValues.addAssignmentJStatement(jSet(destination, jExpr));
      return vStatement(retType, destination);
    }
  }
}
