package com.perunlabs.tool.jvalgen.var.type;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Maps.newHashMap;
import static com.perunlabs.tool.jvalgen.Utils.append;
import static com.perunlabs.tool.jvalgen.Utils.skipOne;
import static com.perunlabs.tool.jvalgen.java.type.JBlocks.jBlock;
import static com.perunlabs.tool.jvalgen.var.expr.VStatements.fakeVStatement;
import static com.perunlabs.tool.jvalgen.var.expr.VStatements.vStatement;
import static com.perunlabs.tool.jvalgen.var.type.VParams.toVType;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.AbstractXxxV;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.MostPrimitiveC;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.XxxC;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.perunlabs.tool.jvalgen.java.gen.CodeGenerator;
import com.perunlabs.tool.jvalgen.java.type.JBlock;
import com.perunlabs.tool.jvalgen.java.type.JExpression;
import com.perunlabs.tool.jvalgen.java.type.JLocalVar;
import com.perunlabs.tool.jvalgen.java.type.JParam;
import com.perunlabs.tool.jvalgen.java.type.JStatement;
import com.perunlabs.tool.jvalgen.var.expr.ExpressionValues;
import com.perunlabs.tool.jvalgen.var.expr.VExpression;
import com.perunlabs.tool.jvalgen.var.expr.VStatement;
import com.perunlabs.tool.jvalgen.var.expr.VStatements;
import com.perunlabs.tool.jvalgen.var.gen.Api;

public class ExpressionVFunction extends VFunction {
  private final VType returnVType;
  private final String name;
  private final ImmutableList<VParam> vParams;
  private final VExpression resultVExpr;

  protected ExpressionVFunction(VType owner, VType returnVType, String name,
      ImmutableList<VParam> vParams, VExpression resultVExprs, boolean isInvolutary) {
    super(owner, returnVType, name, vParams, isInvolutary);
    this.returnVType = returnVType;
    this.name = name;
    this.vParams = vParams;
    this.resultVExpr = resultVExprs;

    checkArgument(resultVExprs.vType().equals(returnVType));
  }

  @Override
  public VType returnVType() {
    return returnVType;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public ImmutableList<VParam> vParams() {
    return vParams;
  }

  @Override
  public ImmutableList<JStatement> generateJComponentAssignments(ImmutableList<JParam> jParams) {
    checkArgument(returnVType.equals(owner()));

    ImmutableList<VType> paramVTypes = toVType(vParams);
    ImmutableList<VStatement> statements;

    if (isEndofunctionForOwner()) {
      JLocalVar thisVar = owner().thisVar(AbstractXxxV);
      ImmutableList<VStatement> rest = vStatement(skipOne(paramVTypes), jParams);
      VStatement thisVStatement = vStatement(paramVTypes.get(0), thisVar);
      statements = append(thisVStatement, rest);
    } else {
      ImmutableList<JParam> args = jParams;
      statements = vStatement(paramVTypes, args);
    }
    return componentAssignmentsImpl(createExpressionValues(statements));
  }

  @Override
  public ImmutableList<JStatement> generateJComponentAssignments2(ImmutableList<JParam> jParams) {
    return componentAssignmentsImpl(buildVParamToFakeVStatementMap(jParams));
  }

  private ImmutableList<JStatement> componentAssignmentsImpl(ExpressionValues expressionValues) {
    resultVExpr.assign(owner().thisVar(AbstractXxxV), expressionValues);
    return expressionValues.allJStatements();
  }

  @Override
  public void generateClasses(CodeGenerator codeGenerator) {
    // nothing to generate
  }

  @Override
  public JExpression apiJCall(VTypeKind kind, ImmutableList<? extends JExpression> args) {
    return Api.createCall(returnVType.jType(kind), name, args);
  }

  @Override
  public JBlock apiJCallImplementation(VTypeKind kind, ImmutableList<? extends JExpression> args) {
    ExpressionValues expressionValues = buildVParamToVStatementMap(args);
    List<JExpression> jCompExprs = Lists.newArrayList();
    VTypeKind compKind = mapKinds(kind);

    jCompExprs.add(resultVExpr.create(compKind, expressionValues).jExpr());

    return jBlock(expressionValues.allJStatements(), returnVType.apiCreate(kind, jCompExprs));
  }

  private VTypeKind mapKinds(VTypeKind kind) {
    if (kind == XxxC) {
      return MostPrimitiveC;
    } else {
      return kind;
    }
  }

  private ExpressionValues buildVParamToVStatementMap(ImmutableList<? extends JExpression> args) {
    return createExpressionValues(VStatements.vStatement(toVType(vParams), args));
  }

  private ExpressionValues createExpressionValues(ImmutableList<VStatement> vStatements) {
    Map<VExpression, VStatement> vParamToVStatementMap = Maps.newHashMap();
    for (int i = 0; i < vParams.size(); i++) {
      VParam vParam = vParams.get(i);
      VStatement vStatement = vStatements.get(i);
      vParamToVStatementMap.put(vParam, vStatement);
    }
    return new ExpressionValues(vParamToVStatementMap);
  }

  private ExpressionValues buildVParamToFakeVStatementMap(ImmutableList<JParam> jParams) {
    if (isEndofunctionForOwner()) {
      checkArgument(vParams.size() == 2, "Expected 2 params, actual = " + vParams);
      VParam vParam0 = vParams.get(0);
      VParam vParam1 = vParams.get(1);

      Map<VExpression, VStatement> vParamToVStatementMap = newHashMap();
      vParamToVStatementMap
          .put(vParam0, vStatement(vParam0.vType(), owner().thisVar(AbstractXxxV)));
      vParamToVStatementMap.put(vParam1, fakeVStatement(vParam1.vType(), jParams));
      return new ExpressionValues(vParamToVStatementMap);
    } else {
      checkArgument(vParams.size() == 1, "Expected 1 param, actual = " + vParams);
      VParam vParam0 = vParams.get(0);

      Map<VExpression, VStatement> vParamToVStatementMap = newHashMap();
      vParamToVStatementMap.put(vParam0, fakeVStatement(vParam0.vType(), jParams));
      return new ExpressionValues(vParamToVStatementMap);
    }
  }
}
