package com.perunlabs.tool.jvalgen.var.expr;

import static com.perunlabs.tool.jvalgen.Utils.transform;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.perunlabs.tool.jvalgen.var.type.VTypeKind;

public class VExpressions {

  /*
   * toVStatement()
   */

  public static ImmutableList<VStatement> toVStatement(Iterable<? extends VExpression> values,
      VTypeKind kind, ExpressionValues expressionValues) {
    return transform(values, toVStatementF(kind, expressionValues));
  }

  private static Function<VExpression, VStatement> toVStatementF(final VTypeKind kind,
      final ExpressionValues expressionValues) {
    return new Function<VExpression, VStatement>() {
      @Override
      public VStatement apply(VExpression expression) {
        return expression.create(kind, expressionValues);
      }
    };
  }
}
