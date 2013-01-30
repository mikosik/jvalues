package com.perunlabs.tool.jvalgen.java.type;

import static com.google.common.base.Preconditions.checkArgument;
import static com.perunlabs.tool.jvalgen.Utils.list;
import static com.perunlabs.tool.jvalgen.Utils.transform;
import static com.perunlabs.tool.jvalgen.java.type.JStatements.argsListCode;
import static com.perunlabs.tool.jvalgen.java.type.JStatements.toImportsNeeded;
import static com.perunlabs.tool.jvalgen.java.type.JType.JCHECK;
import static com.perunlabs.tool.jvalgen.var.spec.AllVTypes.isImplementingXxxG;
import static com.perunlabs.tool.jvalgen.var.spec.NamingSpec.BASIC_TYPE_GET_METHOD;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.MostPrimitiveG;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.XxxC;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.XxxG;

import java.util.Iterator;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.perunlabs.tool.jvalgen.Utils;
import com.perunlabs.tool.jvalgen.var.gen.Api;
import com.perunlabs.tool.jvalgen.var.type.VComponent;
import com.perunlabs.tool.jvalgen.var.type.VType;

public class JExpressions {
  private JExpressions() {}

  public static Iterable<? extends JExpression> empty() {
    return list();
  }

  public static JExpression jConst(Class<?> klass, String jFieldName) {
    return JConst.jConst(klass, jFieldName);
  }

  public static JExpression jExpr(final JType jType, final String code) {
    return new JExpression(code, jType);
  }

  public static JExpression jExpr(JType jType, final String code, ImmutableList<JType> importsNeeded) {
    return new JExpression(jType, code, importsNeeded);
  }

  public static JExpression jCall(JType returnJType, String callString,
      Iterable<? extends JStatement> args) {
    return JExpressions.jCall(returnJType, callString, args, Utils.<JType> list());
  }

  public static JExpression jCall(JType returnJType, String callString,
      Iterable<? extends JStatement> args, Iterable<? extends JType> importsNeeded) {
    ImmutableList<JType> imports = ImmutableList.<JType> builder().addAll(toImportsNeeded(args))
        .addAll(importsNeeded).build();
    return jExpr(returnJType, callString + "(" + argsListCode(args) + ")", imports);
  }

  /*
   * boolean and + or
   */

  public static JExpression and(Iterable<? extends JExpression> jExpressions) {
    return jAndCallable().jCall(jExpressions);
  }

  public static JCallable jOrCallable() {
    return jBooleanOperationCallable("||");
  }

  public static JCallable jAndCallable() {
    return jBooleanOperationCallable("&&");
  }

  private static JCallable jBooleanOperationCallable(String operation) {
    return new JBooleanCallable(operation);
  }

  /*
   * convertedExpr()
   */

  public static ImmutableList<JExpression> convertedExpr(Iterable<? extends JExpression> jExprs,
      Iterable<? extends JType> jTypes) {
    checkArgument(Iterables.size(jTypes) == Iterables.size(jExprs));

    ImmutableList.Builder<JExpression> builder = ImmutableList.builder();

    Iterator<? extends JExpression> argIt = jExprs.iterator();
    for (JType jType : jTypes) {
      JExpression arg = argIt.next();
      builder.add(convertedExpr(arg, jType));
    }

    return builder.build();
  }

  public static ImmutableList<JExpression> convertedExpr(Iterable<? extends JExpression> jExprs,
      JType jType) {
    return transform(jExprs, convertedExprF(jType));
  }

  private static Function<JExpression, JExpression> convertedExprF(final JType jType) {
    return new Function<JExpression, JExpression>() {
      @Override
      public JExpression apply(JExpression jExpr) {
        return convertedExpr(jExpr, jType);
      }
    };
  }

  public static JExpression convertedExpr(JExpression jExpr, JType jType) {
    if (jExpr.jType().isSubTypeOf(jType)) {
      return jExpr;
    }

    if (jType.isPrimitive()) {
      JType compatibleXxxG = JTypes.toVType(jType).jType(XxxG);
      if (jExpr.jType().isSubTypeOf(compatibleXxxG)) {
        return JExpressions.getInvokedOn(jExpr);
      }
    }
    throw new RuntimeException("Could not convert jExpression " + jExpr + " to type " + jType);
  }

  /*
   * componentAccessOn() - access the same component on many statements
   */

  public static ImmutableList<JExpression> compAccessOn(Iterable<? extends JExpression> jExprs,
      VComponent vComp) {
    return transform(jExprs, compAccessOnF(vComp));
  }

  private static Function<JExpression, JExpression> compAccessOnF(final VComponent vComp) {
    return new Function<JExpression, JExpression>() {
      @Override
      public JExpression apply(JExpression jExpr) {
        return compAccessOn(jExpr, vComp);
      }
    };
  }

  public static JExpression compAccessOn(JExpression jExpr, VComponent vComp) {
    return toVType(jExpr).jComponentAccessOn(jExpr, vComp);
  }

  /*
   * getInvokedOn()
   */

  public static ImmutableList<JExpression> getInvokedOn(Iterable<? extends JExpression> jExpr) {
    return transform(jExpr, getInvokedOnF());
  }

  private static Function<JExpression, JExpression> getInvokedOnF() {
    return new Function<JExpression, JExpression>() {
      @Override
      public JExpression apply(JExpression jExpr) {
        return getInvokedOn(jExpr);
      }
    };
  }

  public static JExpression getInvokedOn(JExpression jExpr) {
    VType vType = toVType(jExpr);
    JType jType = jExpr.jType();

    checkArgument(jType.isSubTypeOf(vType.jType(XxxG)));
    checkArgument(vType.isBasic());

    return methodInvokedOn(jExpr, vType.jType(MostPrimitiveG), BASIC_TYPE_GET_METHOD);
  }

  /*
   * methodInvokedOn()
   */

  public static JExpression methodInvokedOn(JExpression jExpr, final JType returnJType,
      final String methodName) {
    return jExpr(returnJType, jExpr.code() + "." + methodName + "()");
  }

  /*
   * mostPrimitive
   */

  public static ImmutableList<JExpression> mostPrimitive(Iterable<? extends JExpression> jExprs) {
    return transform(jExprs, mostPrimitiveF());
  }

  private static Function<JExpression, JExpression> mostPrimitiveF() {
    return new Function<JExpression, JExpression>() {
      @Override
      public JExpression apply(JExpression jExpr) {
        return mostPrimitive(jExpr);
      }
    };
  }

  public static JExpression mostPrimitive(JExpression jExpr) {
    if (isImplementingXxxG(jExpr.jType())) {
      if (toVType(jExpr).isBasic()) {
        return getInvokedOn(jExpr);
      }
    }
    return jExpr;
  }

  /*
   * nonPrimitive()
   */

  public static ImmutableList<JExpression> nonPrimitive(Iterable<? extends JExpression> jExprs) {
    return transform(jExprs, nonPrimitiveF());
  }

  private static Function<JExpression, JExpression> nonPrimitiveF() {
    return new Function<JExpression, JExpression>() {
      @Override
      public JExpression apply(JExpression jExpr) {
        return nonPrimitive(jExpr);
      }
    };
  }

  private static JExpression nonPrimitive(JExpression jExpr) {
    if (jExpr.jType().isPrimitive()) {
      return Api.create(XxxC, jExpr);
    } else {
      return jExpr;
    }
  }

  /*
   * toVType()
   */

  public static ImmutableList<VType> toVType(Iterable<JExpression> jExprs) {
    return transform(jExprs, toVTypeF());
  }

  private static Function<JExpression, VType> toVTypeF() {
    return new Function<JExpression, VType>() {
      @Override
      public VType apply(JExpression jExpr) {
        return toVType(jExpr);
      }
    };
  }

  public static VType toVType(JExpression jExpr) {
    return JTypes.toVType(jExpr.jType());
  }

  /*
   * toJType()
   */

  public static ImmutableList<JType> toJType(Iterable<? extends JExpression> jExprs) {
    return transform(jExprs, toJTypeF());
  }

  private static Function<JExpression, JType> toJTypeF() {
    return new Function<JExpression, JType>() {
      @Override
      public JType apply(JExpression jExpr) {
        return toJType(jExpr);
      }
    };
  }

  public static JType toJType(JExpression jExpr) {
    return jExpr.jType();
  }

  /*
   * notNull()
   */

  public static JExpression notNull(JExpression jExpr) {
    return jCall(jExpr.jType(), "Check.notNull", list(jExpr), list(JCHECK));
  }
}
