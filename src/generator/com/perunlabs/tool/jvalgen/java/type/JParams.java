package com.perunlabs.tool.jvalgen.java.type;

import static com.perunlabs.tool.jvalgen.Utils.list;
import static com.perunlabs.tool.jvalgen.Utils.transform;
import static com.perunlabs.tool.jvalgen.java.type.JParam.jParam;
import static com.perunlabs.tool.jvalgen.java.type.JType.jType;
import static com.perunlabs.tool.jvalgen.java.type.JTypes.toVType;
import static com.perunlabs.tool.jvalgen.var.type.VParam.vParam;

import java.lang.reflect.Method;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.perunlabs.tool.jvalgen.var.type.VParam;

public class JParams {
  private JParams() {}

  public static ImmutableList<JParam> empty() {
    return list();
  }

  /*
   * toFinal
   */

  public static ImmutableList<JParam> toFinal(Iterable<JParam> jParams) {
    return transform(jParams, toFinalF());
  }

  private static Function<JParam, JParam> toFinalF() {
    return new Function<JParam, JParam>() {
      @Override
      public JParam apply(JParam jParam) {
        return jParam.toFinal();
      }
    };
  }

  /*
   * toDeclaration
   */

  public static ImmutableList<String> toDeclaration(ImmutableList<JParam> jParams) {
    return transform(jParams, toDeclarationF());
  }

  private static Function<JParam, String> toDeclarationF() {
    return new Function<JParam, String>() {
      @Override
      public String apply(JParam jParam) {
        return jParam.declaration();
      }
    };
  }

  /*
   * toJParam()
   */

  public static ImmutableList<JParam> toJParam(Method method) {
    ImmutableList.Builder<JParam> builder = ImmutableList.builder();
    Class<?>[] klasses = method.getParameterTypes();
    int i = 0;
    for (Class<?> klass : klasses) {
      i++;
      builder.add(jParam(jType(klass), "v" + i));
    }
    return builder.build();
  }

  /*
   * toJType()
   */

  public static ImmutableList<JType> toJType(ImmutableList<JParam> jParams) {
    return transform(jParams, toJTypeF());
  }

  private static Function<JParam, JType> toJTypeF() {
    return new Function<JParam, JType>() {

      @Override
      public JType apply(JParam jParam) {
        return jParam.jType();
      }
    };
  }

  /*
   * toVParam()
   */

  public static ImmutableList<VParam> toVParam(ImmutableList<JParam> jParams) {
    return transform(jParams, toVParamF());
  }

  private static Function<JParam, VParam> toVParamF() {
    return new Function<JParam, VParam>() {
      @Override
      public VParam apply(JParam jParam) {
        return vParam(toVType(jParam.jType()), jParam.name());
      }
    };
  }
}
