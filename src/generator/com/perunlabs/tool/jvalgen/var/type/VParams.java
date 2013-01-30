package com.perunlabs.tool.jvalgen.var.type;

import static com.perunlabs.tool.jvalgen.Utils.transform;
import static com.perunlabs.tool.jvalgen.java.type.JField.jField;
import static com.perunlabs.tool.jvalgen.java.type.JParam.jParam;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.perunlabs.tool.jvalgen.java.type.JField;
import com.perunlabs.tool.jvalgen.java.type.JParam;

public class VParams {
  private VParams() {}

  /*
   * empty()
   */

  public static ImmutableList<VParam> empty() {
    return ImmutableList.of();
  }

  /*
   * toVType()
   */

  public static ImmutableList<VType> toVType(Iterable<? extends VParam> vParams) {
    return transform(vParams, toVTypeF());
  }

  private static Function<VParam, VType> toVTypeF() {
    return new Function<VParam, VType>() {
      @Override
      public VType apply(VParam vParam) {
        return vParam.vType();
      }
    };
  }

  /*
   * toJParam
   */

  public static ImmutableList<JParam> toJParam(Iterable<VParam> vParams, VTypeKind vTypeKind) {
    return transform(vParams, toJParamF(vTypeKind));
  }

  public static Function<VParam, JParam> toJParamF(final VTypeKind vTypeKind) {
    return new Function<VParam, JParam>() {
      @Override
      public JParam apply(VParam vParam) {
        return jParam(vParam.vType().jType(vTypeKind), vParam.name());
      }
    };
  }

  /*
   * toJParam
   */

  public static ImmutableList<JField> toJField(Iterable<VParam> vParams, VTypeKind vTypeKind) {
    return transform(vParams, toJFieldF(vTypeKind));
  }

  public static Function<VParam, JField> toJFieldF(final VTypeKind vTypeKind) {
    return new Function<VParam, JField>() {
      @Override
      public JField apply(VParam vParam) {
        return jField(vParam.vType().jType(vTypeKind), vParam.name());
      }
    };
  }
}
