package com.perunlabs.tool.jvalgen.var.spec;

import static com.google.common.base.Functions.forMap;
import static com.google.common.base.Preconditions.checkState;
import static com.perunlabs.tool.jvalgen.Utils.list;
import static com.perunlabs.tool.jvalgen.java.type.JType.JBOOLEAN;
import static com.perunlabs.tool.jvalgen.java.type.JType.JFLOAT;
import static com.perunlabs.tool.jvalgen.java.type.JType.JINT;
import static com.perunlabs.tool.jvalgen.java.type.JType.JLONG;
import static com.perunlabs.tool.jvalgen.java.type.JType.jType;
import static com.perunlabs.tool.jvalgen.var.type.BasicVType.vType;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.AbstractXxxG;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.AbstractXxxS;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.AbstractXxxV;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.MostPrimitiveG;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.XxxC;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.XxxG;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.XxxS;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.XxxV;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.XxxVImpl;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.perunlabs.tool.jvalgen.Utils;
import com.perunlabs.tool.jvalgen.java.type.JType;
import com.perunlabs.tool.jvalgen.var.type.BasicVType;
import com.perunlabs.tool.jvalgen.var.type.ObjectiveVComponent;
import com.perunlabs.tool.jvalgen.var.type.VComponent;
import com.perunlabs.tool.jvalgen.var.type.VType;

public class AllVTypes {
  public static final String PACKAGE_ROOT = "com.perunlabs.common.jval";

  public static final JType V_API_CLASS = jType(PACKAGE_ROOT, "V", Utils.<JType> list());

  /*
   * TODO can this be fixed in more cleaner way?
   * 
   * All VType instances have to be declared here so jTypeToVTypeF are created
   * correctly before operations (from XxxSpec classes) are instantiated.
   * Operation creation code uses these mapping and would not work correctly
   * when each VType is created in the same file as its operations.
   */

  /*
   * basic VTypes
   */
  public static final BasicVType VBOOL = vType(p("bool_"), "Bool", JBOOLEAN);
  public static final BasicVType VINT = vType(p("int_"), "Int", JINT);
  public static final BasicVType VLONG = vType(p("long_"), "Long", JLONG);
  public static final BasicVType VFLOAT = vType(p("float_"), "Float", JFLOAT);

  public static final ImmutableList<BasicVType> BASIC_VAR_TYPES = list(VBOOL, VINT, VLONG, VFLOAT);

  /*
   * VECTOR
   */

  public static final VVector VECTOR = new VVector();

  public static class VVector extends VType {
    public final VComponent x;
    public final VComponent y;

    public VVector() {
      this(new ObjectiveVComponent(VFLOAT, "x"), new ObjectiveVComponent(VFLOAT, "y"));
    }

    public VVector(VComponent x, VComponent y) {
      super(p("vector"), "Vector", list(x, y));

      this.x = x;
      this.y = y;
    }
  }

  /*
   * FRAME
   */

  public static final VFrame FRAME = new VFrame();

  public static class VFrame extends VType {
    public final VComponent left;
    public final VComponent right;
    public final VComponent bottom;
    public final VComponent top;

    public VFrame() {
      this(new ObjectiveVComponent(VFLOAT, "left"), new ObjectiveVComponent(VFLOAT, "right"),
          new ObjectiveVComponent(VFLOAT, "bottom"), new ObjectiveVComponent(VFLOAT, "top"));
    }

    public VFrame(VComponent left, VComponent right, VComponent bottom, VComponent top) {
      super(p("frame"), "Frame", list(left, right, bottom, top));

      this.left = left;
      this.right = right;
      this.bottom = bottom;
      this.top = top;
    }
  }

  /*
   * QUANTITY
   */

  public static final VQuantity QUANTITY = new VQuantity();

  public static class VQuantity extends VType {
    public final VComponent vector;
    public final VComponent angle;

    public VQuantity() {
      this(new ObjectiveVComponent(VECTOR, "vector"), new ObjectiveVComponent(VFLOAT, "angle"));
    }

    public VQuantity(VComponent vector, VComponent angle) {
      super(p("quan"), "Quantity", list(vector, angle));

      this.vector = vector;
      this.angle = angle;
    }
  }

  /*
   * QUAD
   */

  public static final VQuad QUAD = new VQuad();

  public static class VQuad extends VType {
    public final VComponent v1;
    public final VComponent v2;
    public final VComponent v3;
    public final VComponent v4;

    public VQuad() {
      this(new ObjectiveVComponent(VECTOR, "v1"), new ObjectiveVComponent(VECTOR, "v2"),
          new ObjectiveVComponent(VECTOR, "v3"), new ObjectiveVComponent(VECTOR, "v4"));
    }

    public VQuad(VComponent v1, VComponent v2, VComponent v3, VComponent v4) {
      super(p("quad"), "Quad", list(v1, v2, v3, v4));

      this.v1 = v1;
      this.v2 = v2;
      this.v3 = v3;
      this.v4 = v4;
    }
  }

  public static final ImmutableList<VType> COMPOUND_VAR_TYPES = list(VECTOR, FRAME, QUANTITY, QUAD);

  public static final ImmutableList<VType> ALL_VAR_TYPES = ImmutableList.copyOf(Iterables.concat(
      BASIC_VAR_TYPES, COMPOUND_VAR_TYPES));

  /*
   * JType -> VType mapping
   */
  public static final Function<JType, VType> jTypeToVTypeF = createJTypeToVTypeMapping();

  private static Function<JType, VType> createJTypeToVTypeMapping() {
    ImmutableMap.Builder<JType, VType> map = ImmutableMap.builder();

    for (VType basicVType : BASIC_VAR_TYPES) {
      JType primitiveG = basicVType.jType(MostPrimitiveG);
      checkState(primitiveG.isPrimitive());
      map.put(primitiveG, basicVType);
    }

    for (VType vType : ALL_VAR_TYPES) {
      map.put(vType.jType(AbstractXxxG), vType);
      map.put(vType.jType(AbstractXxxS), vType);
      map.put(vType.jType(AbstractXxxV), vType);
      map.put(vType.jType(XxxC), vType);
      map.put(vType.jType(XxxG), vType);
      map.put(vType.jType(XxxS), vType);
      map.put(vType.jType(XxxV), vType);
      map.put(vType.jType(XxxVImpl), vType);
    }

    return forMap(map.build());
  }

  private static String p(String subPackage) {
    return PACKAGE_ROOT + "." + subPackage;
  }

  public static boolean isImplementingXxxG(JType type) {
    for (VType vType : ALL_VAR_TYPES) {
      if (type.isSubTypeOf(vType.jType(XxxG))) {
        return true;
      }
    }
    return false;
  }
}
