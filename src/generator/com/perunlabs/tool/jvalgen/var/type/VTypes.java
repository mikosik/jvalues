package com.perunlabs.tool.jvalgen.var.type;

import static com.perunlabs.tool.jvalgen.Utils.anyMatches;
import static com.perunlabs.tool.jvalgen.Utils.transform;
import static com.perunlabs.tool.jvalgen.java.type.JParam.jParam;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.perunlabs.tool.jvalgen.java.type.JExpression;
import com.perunlabs.tool.jvalgen.java.type.JField;
import com.perunlabs.tool.jvalgen.java.type.JParam;
import com.perunlabs.tool.jvalgen.java.type.JType;
import com.perunlabs.tool.jvalgen.java.type.JTypes;

public class VTypes {
  private VTypes() {}

  /*
   * isAnyBasic
   */

  public static boolean isAnyBasic(Iterable<? extends VType> vTypes) {
    return anyMatches(vTypes, isBasicP());
  }

  public static Predicate<VType> isBasicP() {
    return new Predicate<VType>() {
      @Override
      public boolean apply(VType vType) {
        return vType.isBasic();
      }
    };
  }

  /*
   * toJParam
   */

  public static ImmutableList<JParam> toJParam(Iterable<? extends VType> vTypes, VTypeKind vTypeKind) {
    return JTypes.toJParam(toJType(vTypes, vTypeKind));
  }

  public static JParam toJParam(VType vType, VTypeKind vTypeKind) {
    return jParam(vType.jType(vTypeKind), "v1");
  }

  /*
   * toJField
   */

  public static ImmutableList<JField> toJField(Iterable<? extends VType> vTypes, VTypeKind vTypeKind) {
    return JTypes.toJField(toJType(vTypes, vTypeKind));
  }

  /*
   * toJType
   */

  public static ImmutableList<JType> toJType(Iterable<? extends VType> vTypes, VTypeKind vTypeKind) {
    return transform(vTypes, toJTypeF(vTypeKind));
  }

  private static Function<VType, JType> toJTypeF(final VTypeKind vTypeJType) {
    return new Function<VType, JType>() {
      @Override
      public JType apply(VType vType) {
        return vType.jType(vTypeJType);
      }
    };
  }

  /*
   * toApiCreate()
   */

  public static ImmutableList<JExpression> toApiCreate(ImmutableList<VType> vTypes, VTypeKind vTypeKind,
      Iterable<? extends JExpression> jArgs) {
    return transform(vTypes, toApiCreateF(vTypeKind, jArgs));
  }

  private static Function<VType, JExpression> toApiCreateF(final VTypeKind vTypeKind,
      final Iterable<? extends JExpression> jArgs) {
    return new Function<VType, JExpression>() {
      @Override
      public JExpression apply(VType vType) {
        return vType.apiCreate(vTypeKind, jArgs);
      }
    };
  }
}
