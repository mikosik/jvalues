package com.perunlabs.tool.jvalgen.var.spec;

import static com.perunlabs.tool.jvalgen.Utils.list;
import static com.perunlabs.tool.jvalgen.var.spec.AllVTypes.VLONG;
import static com.perunlabs.tool.jvalgen.var.type.BasicVFunction.involutary;
import static com.perunlabs.tool.jvalgen.var.type.BasicVFunction.simple;

import com.google.common.collect.ImmutableList;
import com.perunlabs.common.jval.oper.LongOps;
import com.perunlabs.tool.jvalgen.var.type.BasicVFunction;

public class LongSpec {

  private LongSpec() {}

  public static final BasicVFunction ADD = func("add");
  public static final BasicVFunction SUB = func("sub");
  public static final BasicVFunction MUL = func("mul");
  public static final BasicVFunction DIV = func("div");
  public static final BasicVFunction NEG = funcI("neg");
  public static final BasicVFunction DIF = func("dif");

  public static final BasicVFunction INC = func("inc");
  public static final BasicVFunction DEC = func("dec");

  public static final BasicVFunction MODULO = func("modulo");
  public static final BasicVFunction MAX = func("max");
  public static final BasicVFunction MIN = func("min");

  public static final BasicVFunction IS_EQ = func("isEq");
  public static final BasicVFunction IS_LT = func("isLt");
  public static final BasicVFunction IS_LTE = func("isLte");
  public static final BasicVFunction IS_GT = func("isGt");
  public static final BasicVFunction IS_GTE = func("isGte");

  private static BasicVFunction func(String name) {
    return simple(VLONG, LongOps.class, name);
  }

  private static BasicVFunction funcI(String name) {
    return involutary(VLONG, LongOps.class, name);
  }

  public static final ImmutableList<BasicVFunction> ALL_OPERATIONS = list(ADD, SUB, MUL, DIV, NEG,
      DIF, INC, DEC, MODULO, MAX, MIN, IS_EQ, IS_LT, IS_LTE, IS_GT, IS_GTE);
}
