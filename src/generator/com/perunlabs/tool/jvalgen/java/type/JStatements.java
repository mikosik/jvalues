package com.perunlabs.tool.jvalgen.java.type;

import static com.google.common.base.Preconditions.checkArgument;
import static com.perunlabs.tool.jvalgen.Utils.list;
import static com.perunlabs.tool.jvalgen.Utils.transform;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

public class JStatements {

  private JStatements() {}

  public static Iterable<? extends JStatement> empty() {
    return list();
  }

  /*
   * creation methods
   */

  public static JStatement jStat(final JType jType, final String code) {
    return new JStatement(code);
  }

  public static JStatement jStat(final String code, ImmutableList<JType> importsNeeded) {
    return new JStatement(code, importsNeeded);
  }

  public static JStatement jAssign(JDeclarable jDecl, JExpression jExpr) {
    checkArgument(jExpr.jType().isSubTypeOf(jDecl.jType()));
    String code = jDecl.code() + " = " + jExpr.code();
    return jStat(code, jExpr.importsNeeded());
  }

  public static JStatement jSet(JExpression destination, JExpression source) {
    JType destinationJType = destination.jType();

    if (destinationJType.isPrimitive()) {
      // setting primitive is only allowed when statement is JDeclarable
      return jAssign((JDeclarable) destination, source);
    } else {
      destinationJType.assertThatCanBeSetFrom(source.jType());
      String code = destination.code() + ".set(" + source.code() + ")";
      return jStat(code, source.importsNeeded());
    }
  }

  /*
   * code(), argsListCode()
   */

  static String argsListCode(Iterable<? extends JStatement> jStats) {
    return Joiner.on(", ").join(toCode(jStats));
  }

  public static ImmutableList<String> toCode(Iterable<? extends JStatement> jStats) {
    return transform(jStats, codeF());
  }

  private static Function<JStatement, String> codeF() {
    return new Function<JStatement, String>() {
      @Override
      public String apply(JStatement jStat) {
        return jStat.code();
      }
    };
  }

  /*
   * toImportsNeeded
   */

  public static ImmutableList<JType> toImportsNeeded(Iterable<? extends JStatement> jStats) {
    ImmutableList.Builder<JType> builder = ImmutableList.builder();
    for (JStatement statement : jStats) {
      builder.addAll(statement.importsNeeded());
    }
    return builder.build();
  }

  /*
   * jReturn()
   */

  public static JReturn jReturn(JExpression jExpr) {
    return new JReturn(jExpr);
  }
}
