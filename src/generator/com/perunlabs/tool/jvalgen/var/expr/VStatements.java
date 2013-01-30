package com.perunlabs.tool.jvalgen.var.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static com.perunlabs.tool.jvalgen.Utils.transform;

import java.util.Iterator;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.perunlabs.tool.jvalgen.java.type.JExpression;
import com.perunlabs.tool.jvalgen.var.type.VType;

public class VStatements {

  public static VStatement vStatement(VType vType, JExpression jExpression) {
    return new VStatementImpl(vType, jExpression);
  }

  public static VStatement fakeVStatement(VType vType,
      ImmutableList<? extends JExpression> jExpressions) {
    return FakeVStatement.fakeVStatement(vType, jExpressions);
  }

  /*
   * vStatement()
   */

  public static ImmutableList<VStatement> vStatement(Iterable<VType> vTypes,
      Iterable<? extends JExpression> jExpressions) {
    int size = Iterables.size(vTypes);
    checkArgument(size == Iterables.size(jExpressions));

    ImmutableList.Builder<VStatement> builder = ImmutableList.builder();
    Iterator<VType> vTypeIt = vTypes.iterator();
    Iterator<? extends JExpression> jExpressionIt = jExpressions.iterator();
    for (int i = 0; i < size; i++) {
      VType vType = vTypeIt.next();
      JExpression jExpression = jExpressionIt.next();
      builder.add(vStatement(vType, jExpression));
    }
    return builder.build();
  }

  /*
   * toJExpression()
   */

  public static ImmutableList<JExpression> toJExpression(Iterable<? extends VStatement> vStatements) {
    return transform(vStatements, toJExpressionF());
  }

  private static Function<VStatement, JExpression> toJExpressionF() {
    return new Function<VStatement, JExpression>() {
      @Override
      public JExpression apply(VStatement vStatement) {
        return vStatement.jExpr();
      }
    };
  }
}
