package com.perunlabs.tool.jvalgen.var.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static com.perunlabs.tool.jvalgen.java.type.JExpressions.toVType;
import static com.perunlabs.tool.jvalgen.java.type.JStatements.toCode;
import static com.perunlabs.tool.jvalgen.var.expr.VStatements.vStatement;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.XxxG;

import java.util.Map;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.perunlabs.tool.jvalgen.java.type.JExpression;
import com.perunlabs.tool.jvalgen.java.type.JExpressions;
import com.perunlabs.tool.jvalgen.var.type.VComponent;
import com.perunlabs.tool.jvalgen.var.type.VType;

public class FakeVStatement implements VStatement {
  private final VType vType;
  private final Map<VComponent, JExpression> map;

  protected static VStatement fakeVStatement(VType vType,
      ImmutableList<? extends JExpression> jExpressions) {
    ImmutableList<VComponent> vComponents = vType.vComponents();
    checkArgument(vComponents.size() == jExpressions.size());

    ImmutableMap.Builder<VComponent, JExpression> builder = ImmutableMap.builder();
    for (int i = 0; i < vComponents.size(); i++) {
      VComponent vComponent = vComponents.get(i);
      JExpression jExpression = jExpressions.get(i);
      checkArgument(toVType(jExpression).equals(vComponent.vType()));
      builder.put(vComponent, jExpression);
    }
    return new FakeVStatement(vType, builder.build());
  }

  private FakeVStatement(VType vType, ImmutableMap<VComponent, JExpression> map) {
    this.vType = vType;
    this.map = map;
  }

  @Override
  public JExpression jExpr() {
    String code = Joiner.on(", ").join(toCode(map.values()));
    return JExpressions.jExpr(vType.jType(XxxG), code);
  }

  @Override
  public VStatement componentAccess(VComponent vComponent) {
    checkArgument(map.containsKey(vComponent));
    return vStatement(vComponent.vType(), map.get(vComponent));
  }
}
