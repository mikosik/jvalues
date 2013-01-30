package com.perunlabs.tool.jvalgen.var.gen;

import static com.google.common.base.Preconditions.checkArgument;
import static com.perunlabs.tool.jvalgen.Utils.append;
import static com.perunlabs.tool.jvalgen.Utils.list;
import static com.perunlabs.tool.jvalgen.Utils.transform;
import static com.perunlabs.tool.jvalgen.java.type.JExpressions.jCall;
import static com.perunlabs.tool.jvalgen.java.type.JExpressions.jExpr;
import static com.perunlabs.tool.jvalgen.java.type.JExpressions.nonPrimitive;
import static com.perunlabs.tool.jvalgen.java.type.JExpressions.toJType;
import static com.perunlabs.tool.jvalgen.java.type.JTypes.isAnyNonPrimitive;
import static com.perunlabs.tool.jvalgen.java.type.JTypes.isAnyPrimitive;
import static com.perunlabs.tool.jvalgen.java.type.JTypes.toVType;
import static com.perunlabs.tool.jvalgen.var.spec.AllVTypes.VBOOL;
import static com.perunlabs.tool.jvalgen.var.spec.AllVTypes.V_API_CLASS;
import static com.perunlabs.tool.jvalgen.var.spec.NamingSpec.CONST_CHECK_METHOD;
import static com.perunlabs.tool.jvalgen.var.spec.NamingSpec.IFF_METHOD;
import static com.perunlabs.tool.jvalgen.var.spec.NamingSpec.PRIMITIVE_SUFFIX;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.XxxC;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.XxxG;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.perunlabs.tool.jvalgen.java.type.JExpression;
import com.perunlabs.tool.jvalgen.java.type.JType;
import com.perunlabs.tool.jvalgen.var.type.VType;
import com.perunlabs.tool.jvalgen.var.type.VTypeKind;

public class Api {

  /*
   * create()
   */

  public static ImmutableList<JExpression> create(VTypeKind kind,
      Iterable<? extends JExpression> jArgs) {
    return transform(jArgs, createF(kind));
  }

  private static Function<JExpression, JExpression> createF(final VTypeKind kind) {
    return new Function<JExpression, JExpression>() {
      @Override
      public JExpression apply(JExpression jArg) {
        return create(kind, jArg);
      }
    };
  }

  public static JExpression create(VTypeKind kind, JExpression jArg) {
    return toVType(jArg.jType()).apiCreate(kind, list(jArg));
  }

  /*
   * isConst()
   */

  public static ImmutableList<JExpression> isConst(Iterable<? extends JExpression> jExprs) {
    return transform(jExprs, isConstF());
  }

  private static Function<JExpression, JExpression> isConstF() {
    return new Function<JExpression, JExpression>() {
      @Override
      public JExpression apply(JExpression jExpr) {
        return isConst(jExpr);
      }
    };
  }

  public static JExpression isConst(JExpression jExpr) {
    return createCall(JType.JBOOLEAN, CONST_CHECK_METHOD, list(jExpr));
  }

  /*
   * constCast()
   */

  public static ImmutableList<JExpression> constCast(Iterable<? extends JExpression> jExprs) {
    return transform(jExprs, constCastF());
  }

  private static Function<JExpression, JExpression> constCastF() {
    return new Function<JExpression, JExpression>() {
      @Override
      public JExpression apply(JExpression jExpr) {
        return constCast(jExpr);
      }
    };
  }

  public static JExpression constCast(JExpression jExprs) {
    VType vType = toVType(jExprs.jType());
    JType xxxG = vType.jType(XxxG);
    JType xxxC = vType.jType(XxxC);

    checkArgument(jExprs.jType().isSubTypeOf(xxxG));

    ImmutableList<JType> imports = append(xxxC, jExprs.importsNeeded());
    String code = "(" + xxxC.name() + ") " + jExprs.code();
    return jExpr(xxxC, code, imports);
  }

  /*
   * iff()
   */

  public static JExpression iff(JExpression condition, JExpression p1, JExpression p2) {
    checkArgument(p1.jType().equals(p2.jType()));
    checkArgument(condition.jType().isSubTypeOf(VBOOL.jType(XxxG)));

    return createCall(p1.jType(), IFF_METHOD, list(condition, p1, p2));
  }

  /*
   * createCall()
   */

  public static JExpression createCall(JType returnJType, String methodName,
      Iterable<? extends JExpression> args) {
    Iterable<JType> argJTypes = toJType(args);
    if (isAnyNonPrimitive(argJTypes) && isAnyPrimitive(argJTypes)) {
      // so far V api method with mixed signatures (XxxX and primitive) are not
      // generated so we must convert primitive args to XxxC
      args = nonPrimitive(args);
    }

    // Cannot use jFunction here as we do not really know required type of args.
    // Actually statement created this way represents group of overloaded
    // methods so exact types are not known.
    String methodNameCore = V_API_CLASS.name() + "." + methodName;
    if (returnJType.isPrimitive()) {
      return jCall(returnJType, methodNameCore + PRIMITIVE_SUFFIX, args, list(V_API_CLASS));
    } else {
      return jCall(returnJType, methodNameCore, args, list(V_API_CLASS));
    }
  }
}
