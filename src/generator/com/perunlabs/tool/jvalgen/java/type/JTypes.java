package com.perunlabs.tool.jvalgen.java.type;

import static com.perunlabs.tool.jvalgen.Utils.anyMatches;
import static com.perunlabs.tool.jvalgen.Utils.transform;
import static com.perunlabs.tool.jvalgen.java.type.JField.jField;
import static com.perunlabs.tool.jvalgen.java.type.JParam.jParam;
import static com.perunlabs.tool.jvalgen.var.spec.AllVTypes.jTypeToVTypeF;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.perunlabs.tool.jvalgen.var.type.VType;

public class JTypes {
  private JTypes() {}

  public static ImmutableList<JType> empty() {
    return ImmutableList.of();
  }

  /*
   * isAnyNonPrimitive()
   */

  public static boolean isAnyNonPrimitive(Iterable<JType> jTypes) {
    return anyMatches(jTypes, Predicates.not(isPrimitiveF()));
  }

  /*
   * isAnyPrimitive()
   */

  public static boolean isAnyPrimitive(Iterable<JType> jTypes) {
    return anyMatches(jTypes, isPrimitiveF());
  }

  private static Predicate<JType> isPrimitiveF() {
    return new Predicate<JType>() {
      @Override
      public boolean apply(JType jType) {
        return jType.isPrimitive();
      }
    };
  }

  /*
   * toJParam
   */

  public static ImmutableList<JParam> toJParam(Iterable<JType> jTypes) {
    return transform(jTypes, toJParamF());
  }

  public static Function<JType, JParam> toJParamF() {
    return new Function<JType, JParam>() {
      private int count = 0;

      @Override
      public JParam apply(JType jType) {
        count++;
        return jParam(jType, "v" + count);
      }
    };
  }

  /*
   * toJField
   */

  public static ImmutableList<JField> toJField(Iterable<JType> jTypes) {
    return transform(jTypes, toJFieldF());
  }

  private static Function<JType, JField> toJFieldF() {
    return new Function<JType, JField>() {
      private int count = 0;

      @Override
      public JField apply(JType jType) {
        count++;
        return jField(jType, "v" + count);
      }
    };
  }

  /*
   * toVType
   */

  public static ImmutableList<VType> toVType(Iterable<JType> jTypes) {
    return transform(jTypes, jTypeToVTypeF);
  }

  public static VType toVType(JType jType) {
    return jTypeToVTypeF.apply(jType);
  }

  /*
   * toFullName
   */

  public static Function<JType, String> toFullNameF() {
    return new Function<JType, String>() {
      @Override
      public String apply(JType jType) {
        return jType.fullName();
      }
    };
  }
}
