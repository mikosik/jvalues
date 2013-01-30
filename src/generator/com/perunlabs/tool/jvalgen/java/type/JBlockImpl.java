package com.perunlabs.tool.jvalgen.java.type;

import com.google.common.collect.ImmutableList;

public class JBlockImpl implements JBlock {
  private final ImmutableList<JStatement> statements;

  protected JBlockImpl(ImmutableList<JStatement> statements) {
    this.statements = statements;
  }

  public ImmutableList<JStatement> jStatements() {
    return statements;
  }
}
