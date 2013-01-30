package com.perunlabs.tool.jvalgen.var.type;

import static com.perunlabs.tool.jvalgen.Utils.anyMatches;
import static com.perunlabs.tool.jvalgen.Utils.transform;
import static com.perunlabs.tool.jvalgen.var.type.VTypes.toApiCreate;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.perunlabs.tool.jvalgen.java.type.JExpression;
import com.perunlabs.tool.jvalgen.java.type.JExpressions;
import com.perunlabs.tool.jvalgen.java.type.JField;
import com.perunlabs.tool.jvalgen.java.type.JParam;

public class VComponents {
  private VComponents() {}

  /*
   * isAnyBasic()
   */

  public static boolean isAnyBasic(ImmutableList<VComponent> vComps) {
    return anyMatches(vComps, isBasicP());
  }

  public static Predicate<VComponent> isBasicP() {
    return new Predicate<VComponent>() {
      @Override
      public boolean apply(VComponent vComp) {
        return vComp.isBasic();
      }
    };
  }

  /*
   * toJField()
   */

  public static ImmutableList<JField> toJField(Iterable<VComponent> vComps, VComponentKind kind) {
    return transform(vComps, toJFieldF(kind));
  }

  private static Function<VComponent, JField> toJFieldF(final VComponentKind kind) {
    return new Function<VComponent, JField>() {
      @Override
      public JField apply(VComponent vComp) {
        return vComp.jField(kind);
      }
    };
  }

  /*
   * toJParam()
   */

  public static ImmutableList<JParam> toJParam(Iterable<VComponent> vComps, VComponentKind kind) {
    return transform(vComps, toJParamF(kind));
  }

  private static Function<VComponent, JParam> toJParamF(final VComponentKind kind) {
    return new Function<VComponent, JParam>() {
      @Override
      public JParam apply(VComponent vComp) {
        return vComp.jParam(kind);
      }
    };
  }

  public static ImmutableList<JParam> toJParam(Iterable<VComponent> vComponents, VTypeKind kind) {
    return transform(vComponents, toJParamF(kind));
  }

  private static Function<VComponent, JParam> toJParamF(final VTypeKind kind) {
    return new Function<VComponent, JParam>() {
      @Override
      public JParam apply(VComponent vComp) {
        return vComp.jParam(kind);
      }
    };
  }

  /*
   * toVType()
   */
  public static ImmutableList<VType> toVType(Iterable<VComponent> vComps) {
    return transform(vComps, toVTypeF());
  }

  private static Function<VComponent, VType> toVTypeF() {
    return new Function<VComponent, VType>() {
      @Override
      public VType apply(VComponent vComp) {
        return vComp.vType();
      }
    };
  }

  /*
   * toParamlessApiCreate()
   */

  public static ImmutableList<JExpression> toParamlessApiCreate(ImmutableList<VComponent> vComps,
      VTypeKind kind) {
    return toApiCreate(toVType(vComps), kind, JExpressions.empty());
  }
}
