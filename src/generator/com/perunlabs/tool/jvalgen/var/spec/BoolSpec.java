package com.perunlabs.tool.jvalgen.var.spec;

import static com.perunlabs.tool.jvalgen.Utils.list;
import static com.perunlabs.tool.jvalgen.java.type.JParam.jParam;
import static com.perunlabs.tool.jvalgen.java.type.JType.JBOOLEAN;
import static com.perunlabs.tool.jvalgen.var.spec.AllVTypes.VBOOL;
import static com.perunlabs.tool.jvalgen.var.type.BasicVFunction.inline;
import static com.perunlabs.tool.jvalgen.var.type.BasicVFunction.involutary;
import static com.perunlabs.tool.jvalgen.var.type.BasicVFunction.simple;

import com.google.common.collect.ImmutableList;
import com.perunlabs.common.jval.oper.BoolOps;
import com.perunlabs.tool.jvalgen.java.type.JCallable;
import com.perunlabs.tool.jvalgen.java.type.JExpressions;
import com.perunlabs.tool.jvalgen.java.type.JParam;
import com.perunlabs.tool.jvalgen.var.type.BasicVFunction;

public class BoolSpec {
  private BoolSpec() {}

  /*
   * BasicVFunction.inlined() is used for AND, OR operations so this way
   * generated code can be correctly short-circuited by JVM in runtime.
   */

  public static final BasicVFunction AND = inlinedFunc("and", JExpressions.jAndCallable());
  public static final BasicVFunction OR = inlinedFunc("or", JExpressions.jOrCallable());

  public static final BasicVFunction XOR = func("xor");
  public static final BasicVFunction NOT = involutaryFunc("not");

  private static BasicVFunction func(String name) {
    return simple(VBOOL, BoolOps.class, name);
  }

  private static BasicVFunction inlinedFunc(String name, JCallable operationCallable) {
    ImmutableList<JParam> jParams = list(jParam(JBOOLEAN, "v1"), jParam(JBOOLEAN, "v2"));
    return inline(VBOOL, name, jParams, operationCallable);
  }

  private static BasicVFunction involutaryFunc(String name) {
    return involutary(VBOOL, BoolOps.class, name);
  }

  public static final ImmutableList<BasicVFunction> ALL_OPERATIONS = list(AND, OR, XOR, NOT);
}
