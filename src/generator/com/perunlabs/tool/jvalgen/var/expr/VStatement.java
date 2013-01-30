package com.perunlabs.tool.jvalgen.var.expr;

import com.perunlabs.tool.jvalgen.java.type.JExpression;
import com.perunlabs.tool.jvalgen.var.type.VComponent;

public interface VStatement {

  public JExpression jExpr();

  public VStatement componentAccess(VComponent vComponent);
}
