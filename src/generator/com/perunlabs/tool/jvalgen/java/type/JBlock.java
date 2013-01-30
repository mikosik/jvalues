package com.perunlabs.tool.jvalgen.java.type;

import com.google.common.collect.ImmutableList;

public interface JBlock {
  public ImmutableList<JStatement> jStatements();
}
