package com.perunlabs.tool.jvalgen.var.spec;

import static com.perunlabs.tool.jvalgen.Utils.list;
import static com.perunlabs.tool.jvalgen.var.expr.VComponentAccesss.vComponentAccess;
import static com.perunlabs.tool.jvalgen.var.spec.AllVTypes.VECTOR3;
import static com.perunlabs.tool.jvalgen.var.spec.AllVTypes.VFLOAT;
import static com.perunlabs.tool.jvalgen.var.spec.FloatSpec.SQR;
import static com.perunlabs.tool.jvalgen.var.spec.FloatSpec.SQRT;
import static com.perunlabs.tool.jvalgen.var.type.VFunctions.compoundHighVFunction;
import static com.perunlabs.tool.jvalgen.var.type.VFunctions.compoundLowVFunction;
import static com.perunlabs.tool.jvalgen.var.type.VFunctions.vFunction;
import static com.perunlabs.tool.jvalgen.var.type.VParam.vParam;

import com.google.common.collect.ImmutableList;
import com.perunlabs.tool.jvalgen.var.expr.VExpression;
import com.perunlabs.tool.jvalgen.var.type.ExpressionVFunction;
import com.perunlabs.tool.jvalgen.var.type.VFunction;
import com.perunlabs.tool.jvalgen.var.type.VParam;

public class Vector3Spec {
  private Vector3Spec() {}

  public static final VFunction ADD = compoundHighVFunction(VECTOR3, FloatSpec.ADD);
  public static final VFunction SUB = compoundHighVFunction(VECTOR3, FloatSpec.SUB);

  public static final VFunction MUL = compoundLowVFunction(VECTOR3, FloatSpec.MUL);
  public static final VFunction DIV = compoundLowVFunction(VECTOR3, FloatSpec.DIV);
  public static final VFunction NEG = compoundHighVFunction(VECTOR3, FloatSpec.NEG);

  public static final VFunction LENGTH = length();

  public static final ImmutableList<VFunction> ALL_OPERATIONS = list(ADD, SUB, MUL, DIV, NEG,
      LENGTH);

  private static ExpressionVFunction length() {
    VParam vector = vParam(VECTOR3, "vector");

    VExpression x = vComponentAccess(vector, VECTOR3.x);
    VExpression y = vComponentAccess(vector, VECTOR3.y);
    VExpression z = vComponentAccess(vector, VECTOR3.z);

    VExpression sum1 = FloatSpec.ADD.vCall(SQR.vCall(x), SQR.vCall(y));
    VExpression sum2 = FloatSpec.ADD.vCall(sum1, SQR.vCall(z));
    VExpression result = SQRT.vCall(sum2);

    return vFunction(VECTOR3, VFLOAT, "length", list(vector), result, false);
  }
}
