package com.perunlabs.tool.jvalgen.var.spec;

import static com.perunlabs.tool.jvalgen.Utils.list;
import static com.perunlabs.tool.jvalgen.var.expr.ExtractedVExpr.extractedVExpr;
import static com.perunlabs.tool.jvalgen.var.expr.VComponentAccesss.vComponentAccess;
import static com.perunlabs.tool.jvalgen.var.expr.VConst.vConst;
import static com.perunlabs.tool.jvalgen.var.expr.VCreate.vCreate;
import static com.perunlabs.tool.jvalgen.var.spec.AllVTypes.FRAME;
import static com.perunlabs.tool.jvalgen.var.spec.AllVTypes.QUANTITY;
import static com.perunlabs.tool.jvalgen.var.spec.AllVTypes.VECTOR;
import static com.perunlabs.tool.jvalgen.var.spec.AllVTypes.VFLOAT;
import static com.perunlabs.tool.jvalgen.var.spec.FloatSpec.COS;
import static com.perunlabs.tool.jvalgen.var.spec.FloatSpec.SIN;
import static com.perunlabs.tool.jvalgen.var.spec.FloatSpec.SQR;
import static com.perunlabs.tool.jvalgen.var.spec.FloatSpec.SQRT;
import static com.perunlabs.tool.jvalgen.var.type.VFunctions.compoundHighVFunction;
import static com.perunlabs.tool.jvalgen.var.type.VFunctions.compoundLowVFunction;
import static com.perunlabs.tool.jvalgen.var.type.VFunctions.vFunction;
import static com.perunlabs.tool.jvalgen.var.type.VParam.vParam;

import com.google.common.collect.ImmutableList;
import com.perunlabs.common.jval.oper.FloatOps;
import com.perunlabs.tool.jvalgen.java.type.JExpressions;
import com.perunlabs.tool.jvalgen.var.expr.VExpression;
import com.perunlabs.tool.jvalgen.var.type.ExpressionVFunction;
import com.perunlabs.tool.jvalgen.var.type.VFunction;
import com.perunlabs.tool.jvalgen.var.type.VParam;

public class VectorSpec {
  private VectorSpec() {}

  public static final VFunction ADD = compoundHighVFunction(VECTOR, FloatSpec.ADD);
  public static final VFunction SUB = compoundHighVFunction(VECTOR, FloatSpec.SUB);

  public static final VFunction MUL = compoundLowVFunction(VECTOR, FloatSpec.MUL);
  public static final VFunction DIV = compoundLowVFunction(VECTOR, FloatSpec.DIV);
  public static final VFunction NEG = compoundHighVFunction(VECTOR, FloatSpec.NEG);

  public static final VFunction CLAMP = clamp();

  public static final VFunction ROTATE = rotate();
  public static final VFunction VERSOR_AT_ANGLE = toVersorAtAngle();
  public static final VFunction LENGTH = length();
  public static final VFunction ANGLE = angle();
  public static final VFunction TO_GLOBAL_POINT = toGlobalPoint();
  public static final VFunction DOT_PRODUCT = dotProduct();
  public static final VFunction CROSS_PRODUCT = crossProduct();

  public static final ImmutableList<VFunction> ALL_OPERATIONS = list(ADD, SUB, MUL, DIV, NEG,
      CLAMP, ROTATE, VERSOR_AT_ANGLE, LENGTH, ANGLE, TO_GLOBAL_POINT, DOT_PRODUCT, CROSS_PRODUCT);

  private static ExpressionVFunction clamp() {
    VParam vector = vParam(VECTOR, "vector");
    VParam frame = vParam(FRAME, "frame");

    VExpression x = vComponentAccess(vector, VECTOR.x);
    VExpression y = vComponentAccess(vector, VECTOR.y);

    VExpression left = vComponentAccess(frame, FRAME.left);
    VExpression right = vComponentAccess(frame, FRAME.right);
    VExpression bottom = vComponentAccess(frame, FRAME.bottom);
    VExpression top = vComponentAccess(frame, FRAME.top);

    VExpression xClamp = FloatSpec.CLAMP.vCall(x, left, right);
    VExpression yClamp = FloatSpec.CLAMP.vCall(y, bottom, top);

    VExpression result = vCreate(VECTOR, list(xClamp, yClamp));

    return vFunction(VECTOR, VECTOR, "clamp", list(vector, frame), result, false);
  }

  private static ExpressionVFunction toVersorAtAngle() {
    VParam angle = vParam(VFLOAT, "angle");

    VExpression x = FloatSpec.COS.vCall(angle);
    VExpression y = FloatSpec.SIN.vCall(angle);

    VExpression result = vCreate(VECTOR, list(x, y));

    return vFunction(VECTOR, VECTOR, "toVersorAtAngle", list(angle), result, false);
  }

  private static ExpressionVFunction length() {
    VParam vector = vParam(VECTOR, "vector");

    VExpression x = vComponentAccess(vector, VECTOR.x);
    VExpression y = vComponentAccess(vector, VECTOR.y);

    VExpression sum = FloatSpec.ADD.vCall(SQR.vCall(x), SQR.vCall(y));

    VExpression result = SQRT.vCall(sum);

    return vFunction(VECTOR, VFLOAT, "length", list(vector), result, false);
  }

  private static ExpressionVFunction angle() {
    VParam vector = vParam(VECTOR, "vector");

    VExpression x = vComponentAccess(vector, VECTOR.x);
    VExpression y = vComponentAccess(vector, VECTOR.y);

    VExpression atan2 = FloatSpec.ATAN2.vCall(y, x);
    VExpression pix2 = vConst(JExpressions.jConst(FloatOps.class, "PIx2"));

    VExpression result = FloatSpec.MODULO.vCall(atan2, pix2);

    return vFunction(VECTOR, VFLOAT, "angle", list(vector), result, false);
  }

  private static ExpressionVFunction rotate() {
    VParam vector = vParam(VECTOR, "vector");
    VParam angle = vParam(VFLOAT, "angle");

    VExpression cos = extractedVExpr("cos", COS.vCall(angle));
    VExpression sin = extractedVExpr("sin", SIN.vCall(angle));

    VExpression origX = extractedVExpr("origX", vComponentAccess(vector, VECTOR.x));
    VExpression origY = extractedVExpr("origY", vComponentAccess(vector, VECTOR.y));

    VExpression xcos = FloatSpec.MUL.vCall(origX, cos);
    VExpression ysin = FloatSpec.MUL.vCall(origY, sin);
    VExpression xsin = FloatSpec.MUL.vCall(origX, sin);
    VExpression ycos = FloatSpec.MUL.vCall(origY, cos);

    VExpression x = extractedVExpr("resultX", FloatSpec.SUB.vCall(xcos, ysin));
    VExpression y = extractedVExpr("resultY", FloatSpec.ADD.vCall(xsin, ycos));

    VExpression result = vCreate(VECTOR, list(x, y));

    return vFunction(VECTOR, VECTOR, "rotate", list(vector, angle), result, false);
  }

  private static VFunction toGlobalPoint() {
    VParam vector = vParam(VECTOR, "vector");
    VParam position = vParam(QUANTITY, "position");

    VExpression location = vComponentAccess(position, QUANTITY.vector);
    VExpression angle = vComponentAccess(position, QUANTITY.angle);

    VExpression rotated = ROTATE.vCall(vector, angle);

    VExpression result = ADD.vCall(rotated, location);

    return vFunction(VECTOR, VECTOR, "toGlobalPoint", list(vector, position), result, false);
  }

  private static VFunction dotProduct() {
    VParam vector1 = vParam(VECTOR, "vector1");
    VParam vector2 = vParam(VECTOR, "vector2");

    VExpression x1 = vComponentAccess(vector1, VECTOR.x);
    VExpression y1 = vComponentAccess(vector1, VECTOR.y);

    VExpression x2 = vComponentAccess(vector2, VECTOR.x);
    VExpression y2 = vComponentAccess(vector2, VECTOR.y);

    VExpression mulX = FloatSpec.MUL.vCall(x1, x2);
    VExpression mulY = FloatSpec.MUL.vCall(y1, y2);

    VExpression result = FloatSpec.ADD.vCall(mulX, mulY);

    return vFunction(VECTOR, VFLOAT, "dotProduct", list(vector1, vector2), result, false);
  }

  private static VFunction crossProduct() {
    VParam vector1 = vParam(VECTOR, "vector1");
    VParam vector2 = vParam(VECTOR, "vector2");

    VExpression x1 = vComponentAccess(vector1, VECTOR.x);
    VExpression y1 = vComponentAccess(vector1, VECTOR.y);

    VExpression x2 = vComponentAccess(vector2, VECTOR.x);
    VExpression y2 = vComponentAccess(vector2, VECTOR.y);

    VExpression a = FloatSpec.MUL.vCall(x1, y2);
    VExpression b = FloatSpec.MUL.vCall(y1, x2);

    VExpression result = FloatSpec.SUB.vCall(a, b);

    return vFunction(VECTOR, VFLOAT, "crossProduct", list(vector1, vector2), result, false);
  }
}
