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
   * VECTOR3
   */

  public static final VVector3 VECTOR3 = new VVector3();

  public static class VVector3 extends VType {
    public final VComponent x;
    public final VComponent y;
    public final VComponent z;

    public VVector3() {
      this(new ObjectiveVComponent(VFLOAT, "x"), new ObjectiveVComponent(VFLOAT, "y"),
          new ObjectiveVComponent(VFLOAT, "z"));
    }

    public VVector3(VComponent x, VComponent y, VComponent z) {
      super(p("vector3"), "Vector3", list(x, y, z));

      this.x = x;
      this.y = y;
      this.z = z;
    }
  }

  /*
   * VECTOR4
   */

  public static final VMatrix4 MATRIX4 = new VMatrix4();

  public static class VMatrix4 extends VType {
    public final VComponent c11;
    public final VComponent c12;
    public final VComponent c13;
    public final VComponent c14;
    public final VComponent c21;
    public final VComponent c22;
    public final VComponent c23;
    public final VComponent c24;
    public final VComponent c31;
    public final VComponent c32;
    public final VComponent c33;
    public final VComponent c34;
    public final VComponent c41;
    public final VComponent c42;
    public final VComponent c43;
    public final VComponent c44;

    public VMatrix4() {
      this(new ObjectiveVComponent(VFLOAT, "c11"), new ObjectiveVComponent(VFLOAT, "c12"),
          new ObjectiveVComponent(VFLOAT, "c13"), new ObjectiveVComponent(VFLOAT, "c14"),

          new ObjectiveVComponent(VFLOAT, "c21"), new ObjectiveVComponent(VFLOAT, "c22"),
          new ObjectiveVComponent(VFLOAT, "c23"), new ObjectiveVComponent(VFLOAT, "c24"),

          new ObjectiveVComponent(VFLOAT, "c31"), new ObjectiveVComponent(VFLOAT, "c32"),
          new ObjectiveVComponent(VFLOAT, "c33"), new ObjectiveVComponent(VFLOAT, "c34"),

          new ObjectiveVComponent(VFLOAT, "c41"), new ObjectiveVComponent(VFLOAT, "c42"),
          new ObjectiveVComponent(VFLOAT, "c43"), new ObjectiveVComponent(VFLOAT, "c44"));
    }

    public VMatrix4(VComponent c11, VComponent c12, VComponent c13, VComponent c14, VComponent c21,
        VComponent c22, VComponent c23, VComponent c24, VComponent c31, VComponent c32,
        VComponent c33, VComponent c34, VComponent c41, VComponent c42, VComponent c43,
        VComponent c44) {
      super(p("matrix4"), "Matrix4", list(c11, c12, c13, c14, c21, c22, c23, c24, c31, c32, c33,
          c34, c41, c42, c43, c44));
      this.c11 = c11;
      this.c12 = c12;
      this.c13 = c13;
      this.c14 = c14;
      this.c21 = c21;
      this.c22 = c22;
      this.c23 = c23;
      this.c24 = c24;
      this.c31 = c31;
      this.c32 = c32;
      this.c33 = c33;
      this.c34 = c34;
      this.c41 = c41;
      this.c42 = c42;
      this.c43 = c43;
      this.c44 = c44;
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
   * FRAME3
   */

  public static final VFrame3 FRAME3 = new VFrame3();

  public static class VFrame3 extends VType {
    public final VComponent left;
    public final VComponent right;
    public final VComponent bottom;
    public final VComponent top;
    public final VComponent near;
    public final VComponent far;

    public VFrame3() {
      this(new ObjectiveVComponent(VFLOAT, "left"), new ObjectiveVComponent(VFLOAT, "right"),
          new ObjectiveVComponent(VFLOAT, "bottom"), new ObjectiveVComponent(VFLOAT, "top"),
          new ObjectiveVComponent(VFLOAT, "near"), new ObjectiveVComponent(VFLOAT, "far"));
    }

    public VFrame3(VComponent left, VComponent right, VComponent bottom, VComponent top,
        VComponent near, VComponent far) {
      super(p("frame3"), "Frame3", list(left, right, bottom, top, near, far));

      this.left = left;
      this.right = right;
      this.bottom = bottom;
      this.top = top;
      this.near = near;
      this.far = far;
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

  public static final ImmutableList<VType> COMPOUND_VAR_TYPES = list(VECTOR, VECTOR3, MATRIX4,
      FRAME, FRAME3, QUANTITY, QUAD);

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
