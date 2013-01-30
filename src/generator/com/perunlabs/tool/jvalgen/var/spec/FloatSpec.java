package com.perunlabs.tool.jvalgen.var.spec;

import static com.perunlabs.tool.jvalgen.Utils.list;
import static com.perunlabs.tool.jvalgen.var.spec.AllVTypes.VFLOAT;
import static com.perunlabs.tool.jvalgen.var.type.BasicVFunction.involutary;
import static com.perunlabs.tool.jvalgen.var.type.BasicVFunction.simple;

import com.google.common.collect.ImmutableList;
import com.perunlabs.common.jval.oper.FloatOps;
import com.perunlabs.tool.jvalgen.var.type.BasicVFunction;

public class FloatSpec {
  private FloatSpec() {}

  public static final BasicVFunction ADD = func("add");
  public static final BasicVFunction SUB = func("sub");
  public static final BasicVFunction MUL = func("mul");
  public static final BasicVFunction DIV = func("div");
  public static final BasicVFunction NEG = funcI("neg");
  public static final BasicVFunction DIF = func("dif");

  public static final BasicVFunction MODULO = func("modulo");
  public static final BasicVFunction MAX = func("max");
  public static final BasicVFunction MIN = func("min");
  public static final BasicVFunction CLAMP = func("clamp");
  public static final BasicVFunction FLOOR = func("floor");
  public static final BasicVFunction CEIL = func("ceil");
  public static final BasicVFunction ROUND = func("round");
  public static final BasicVFunction SIN = func("sin");
  public static final BasicVFunction COS = func("cos");
  public static final BasicVFunction ATAN2 = func("atan2");

  public static final BasicVFunction AVERAGE = func("average");
  public static final BasicVFunction SQR = func("sqr");
  public static final BasicVFunction SQRT = func("sqrt");
  public static final BasicVFunction IS_EQ = func("isEq");
  public static final BasicVFunction IS_LT = func("isLt");
  public static final BasicVFunction IS_LTE = func("isLte");
  public static final BasicVFunction IS_GT = func("isGt");
  public static final BasicVFunction IS_GTE = func("isGte");
  public static final BasicVFunction TO_INT = func("toInt");

  private static BasicVFunction func(String name) {
    return simple(VFLOAT, FloatOps.class, name);
  }

  private static BasicVFunction funcI(String name) {
    return involutary(VFLOAT, FloatOps.class, name);
  }

  public static final ImmutableList<BasicVFunction> ALL_OPERATIONS = list(ADD, SUB, MUL, DIV, NEG,
      DIF, MODULO, MAX, MIN, CLAMP, FLOOR, CEIL, ROUND, SIN, COS, ATAN2, AVERAGE, SQR, SQRT, IS_EQ,
      IS_LT, IS_LTE, IS_GT, IS_GTE, TO_INT);
}
