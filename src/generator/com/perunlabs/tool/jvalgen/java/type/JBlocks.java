package com.perunlabs.tool.jvalgen.java.type;

import static com.perunlabs.tool.jvalgen.java.type.JStatements.jReturn;

import com.google.common.collect.ImmutableList;

public class JBlocks {
  public static JBlock jBlock(ImmutableList<JStatement> statements) {
    return new JBlockImpl(statements);
  }

  public static JBlock jBlock(ImmutableList<JStatement> statements, JExpression toReturn) {
    ImmutableList.Builder<JStatement> builder = ImmutableList.builder();
    builder.addAll(statements);
    builder.add(jReturn(toReturn));
    return new JBlockImpl(builder.build());
  }
}
