package com.perunlabs.tool.jvalgen.java.type;

import static com.perunlabs.tool.jvalgen.Utils.transform;
import static com.perunlabs.tool.jvalgen.java.type.JParam.jParam;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;

public class JFields {
  private JFields() {}

  /*
   * toFinal()
   */

  public static ImmutableList<JField> toFinal(Iterable<JField> jFields) {
    return transform(jFields, toFinalF());
  }

  private static Function<JField, JField> toFinalF() {
    return new Function<JField, JField>() {
      @Override
      public JField apply(JField jField) {
        return jField.toFinal();
      }
    };
  }

  /*
   * toJParam()
   */

  public static ImmutableList<JParam> toJParam(List<JField> jFields) {
    return transform(jFields, toJParamF());
  }

  private static Function<JField, JParam> toJParamF() {
    return new Function<JField, JParam>() {
      @Override
      public JParam apply(JField jField) {
        return jParam(jField.jType(), jField.name());
      }
    };
  }
}
