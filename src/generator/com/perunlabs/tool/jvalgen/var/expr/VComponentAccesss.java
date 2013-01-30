package com.perunlabs.tool.jvalgen.var.expr;

import static com.perunlabs.tool.jvalgen.Utils.transform;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.perunlabs.tool.jvalgen.var.type.VComponent;

public class VComponentAccesss {
  private VComponentAccesss() {}

  public static ImmutableList<VComponentAccess> vComponentAccess(Iterable<? extends VExpression> expressions,
      VComponent field) {
    return transform(expressions, vComponentAccessF(field));
  }

  private static Function<VExpression, VComponentAccess> vComponentAccessF(final VComponent field) {
    return new Function<VExpression, VComponentAccess>() {
      @Override
      public VComponentAccess apply(VExpression expression) {
        return vComponentAccess(expression, field);
      }
    };
  }

  public static VComponentAccess vComponentAccess(VExpression expression, VComponent field) {
    return new VComponentAccess(expression, field);
  }
}
