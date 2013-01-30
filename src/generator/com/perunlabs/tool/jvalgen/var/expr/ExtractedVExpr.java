package com.perunlabs.tool.jvalgen.var.expr;

import static com.perunlabs.tool.jvalgen.java.type.JExpressions.convertedExpr;
import static com.perunlabs.tool.jvalgen.java.type.JLocalVar.jLocalVar;
import static com.perunlabs.tool.jvalgen.var.expr.VStatements.vStatement;

import com.perunlabs.tool.jvalgen.java.type.JExpression;
import com.perunlabs.tool.jvalgen.java.type.JLocalVar;
import com.perunlabs.tool.jvalgen.java.type.JType;
import com.perunlabs.tool.jvalgen.var.type.VTypeKind;

public class ExtractedVExpr extends AbstractVExpression {
  private final String name;
  private final VExpression vExpr;

  public static VExpression extractedVExpr(String name, VExpression expression) {
    return new ExtractedVExpr(name, expression);
  }

  private ExtractedVExpr(String name, VExpression vExpr) {
    super(vExpr.vType());
    this.name = name;
    this.vExpr = vExpr;
  }

  @Override
  public VStatement create(VTypeKind kind, ExpressionValues expressionValues) {
    if (expressionValues.hasValueFor(this)) {
      return expressionValues.valueFor(this);
    } else {
      JExpression jExpression = vExpr.create(kind, expressionValues).jExpr();
      JType jType = vType().jType(kind);
      JLocalVar jVar = jLocalVar(jType, name, convertedExpr(jExpression, jType));
      expressionValues.addExtractedJStatement(jVar.jDeclaration());

      VStatement vStatement = vStatement(vType(), jVar);
      expressionValues.addVStatement(this, vStatement);
      return vStatement;
    }
  }
}
