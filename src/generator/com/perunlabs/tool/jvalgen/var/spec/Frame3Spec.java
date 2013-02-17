package com.perunlabs.tool.jvalgen.var.spec;

import static com.perunlabs.tool.jvalgen.Utils.list;
import static com.perunlabs.tool.jvalgen.var.expr.ExtractedVExpr.extractedVExpr;
import static com.perunlabs.tool.jvalgen.var.expr.VComponentAccesss.vComponentAccess;
import static com.perunlabs.tool.jvalgen.var.expr.VCreate.vCreate;
import static com.perunlabs.tool.jvalgen.var.spec.AllVTypes.FRAME;
import static com.perunlabs.tool.jvalgen.var.spec.AllVTypes.FRAME3;
import static com.perunlabs.tool.jvalgen.var.spec.AllVTypes.VECTOR;
import static com.perunlabs.tool.jvalgen.var.spec.AllVTypes.VECTOR3;
import static com.perunlabs.tool.jvalgen.var.spec.AllVTypes.VFLOAT;
import static com.perunlabs.tool.jvalgen.var.type.VFunctions.compoundLowVFunction;
import static com.perunlabs.tool.jvalgen.var.type.VFunctions.vFunction;
import static com.perunlabs.tool.jvalgen.var.type.VParam.vParam;

import com.google.common.collect.ImmutableList;
import com.perunlabs.tool.jvalgen.var.expr.VExpression;
import com.perunlabs.tool.jvalgen.var.type.ExpressionVFunction;
import com.perunlabs.tool.jvalgen.var.type.VFunction;
import com.perunlabs.tool.jvalgen.var.type.VParam;

public class Frame3Spec {
  private Frame3Spec() {}

  public static final VFunction WIDTH = width();
  public static final VFunction HEIGHT = height();
  public static final VFunction DEPTH = depth();

  public static final VFunction CENTER = center();
  public static final VFunction SIZE = size();

  public static final VFunction XYFRAME = xyFrame();
  public static final VFunction XZFRAME = xzFrame();
  public static final VFunction ZYFRAME = zyFrame();

  public static final VFunction MOVE = move();
  public static final VFunction MUL = compoundLowVFunction(FRAME3, FloatSpec.MUL);

  public static final ImmutableList<VFunction> ALL_OPERATIONS = list(CENTER, SIZE, WIDTH, HEIGHT,
      DEPTH, XYFRAME, XZFRAME, ZYFRAME, MOVE, MUL);

  private static ExpressionVFunction center() {
    VParam frame = vParam(FRAME3, "frame");

    VExpression left = vComponentAccess(frame, FRAME3.left);
    VExpression right = vComponentAccess(frame, FRAME3.right);
    VExpression bottom = vComponentAccess(frame, FRAME3.bottom);
    VExpression top = vComponentAccess(frame, FRAME3.top);
    VExpression near = vComponentAccess(frame, FRAME3.near);
    VExpression far = vComponentAccess(frame, FRAME3.far);

    VExpression x = FloatSpec.AVERAGE.vCall(left, right);
    VExpression y = FloatSpec.AVERAGE.vCall(bottom, top);
    VExpression z = FloatSpec.AVERAGE.vCall(near, far);

    VExpression result = vCreate(VECTOR3, list(x, y, z));

    return vFunction(FRAME3, VECTOR3, "center", list(frame), result, false);
  }

  private static ExpressionVFunction size() {
    VParam frame = vParam(FRAME3, "frame");

    VExpression width = WIDTH.vCall(frame);
    VExpression height = HEIGHT.vCall(frame);
    VExpression depth = DEPTH.vCall(frame);

    VExpression result = vCreate(VECTOR3, list(width, height, depth));

    return vFunction(FRAME3, VECTOR3, "size", list(frame), result, false);
  }

  private static ExpressionVFunction width() {
    VParam frame = vParam(FRAME3, "frame");

    VExpression left = vComponentAccess(frame, FRAME3.left);
    VExpression right = vComponentAccess(frame, FRAME3.right);
    VExpression width = FloatSpec.DIF.vCall(left, right);

    return vFunction(FRAME3, VFLOAT, "width", list(frame), width, false);
  }

  private static ExpressionVFunction height() {
    VParam frame = vParam(FRAME3, "frame");

    VExpression bottom = vComponentAccess(frame, FRAME3.bottom);
    VExpression top = vComponentAccess(frame, FRAME3.top);

    VExpression result = FloatSpec.DIF.vCall(bottom, top);

    return vFunction(FRAME3, VFLOAT, "height", list(frame), result, false);
  }

  private static ExpressionVFunction depth() {
    VParam frame = vParam(FRAME3, "frame");

    VExpression near = vComponentAccess(frame, FRAME3.near);
    VExpression far = vComponentAccess(frame, FRAME3.far);

    VExpression result = FloatSpec.DIF.vCall(near, far);

    return vFunction(FRAME3, VFLOAT, "depth", list(frame), result, false);
  }

  private static ExpressionVFunction xyFrame() {
    VParam frame = vParam(FRAME3, "frame");

    VExpression l = vComponentAccess(frame, FRAME3.left);
    VExpression r = vComponentAccess(frame, FRAME3.right);
    VExpression b = vComponentAccess(frame, FRAME3.bottom);
    VExpression t = vComponentAccess(frame, FRAME3.top);

    VExpression result = vCreate(FRAME, list(l, r, b, t));

    return vFunction(FRAME3, FRAME, "xyFrame", list(frame), result, false);
  }

  private static ExpressionVFunction xzFrame() {
    VParam frame = vParam(FRAME3, "frame");

    VExpression l = vComponentAccess(frame, FRAME3.left);
    VExpression r = vComponentAccess(frame, FRAME3.right);
    VExpression n = vComponentAccess(frame, FRAME3.near);
    VExpression f = vComponentAccess(frame, FRAME3.far);

    VExpression result = vCreate(FRAME, list(l, r, n, f));

    return vFunction(FRAME3, FRAME, "xzFrame", list(frame), result, false);
  }

  private static ExpressionVFunction zyFrame() {
    VParam frame = vParam(FRAME3, "frame");

    VExpression b = vComponentAccess(frame, FRAME3.bottom);
    VExpression t = vComponentAccess(frame, FRAME3.top);
    VExpression n = vComponentAccess(frame, FRAME3.near);
    VExpression f = vComponentAccess(frame, FRAME3.far);

    VExpression result = vCreate(FRAME, list(n, f, b, t));

    return vFunction(FRAME3, FRAME, "zyFrame", list(frame), result, false);
  }

  private static ExpressionVFunction move() {
    VParam frame = vParam(FRAME3, "frame");
    VParam vector = vParam(VECTOR, "vector");

    VExpression origLeft = vComponentAccess(frame, FRAME3.left);
    VExpression origRight = vComponentAccess(frame, FRAME3.right);
    VExpression origBottom = vComponentAccess(frame, FRAME3.bottom);
    VExpression origTop = vComponentAccess(frame, FRAME3.top);
    VExpression origNear = vComponentAccess(frame, FRAME3.near);
    VExpression origFar = vComponentAccess(frame, FRAME3.far);

    VExpression x = extractedVExpr("x_", vComponentAccess(vector, VECTOR.x));
    VExpression y = extractedVExpr("y_", vComponentAccess(vector, VECTOR.y));

    VExpression l = FloatSpec.ADD.vCall(origLeft, x);
    VExpression r = FloatSpec.ADD.vCall(origRight, x);
    VExpression b = FloatSpec.ADD.vCall(origBottom, y);
    VExpression t = FloatSpec.ADD.vCall(origTop, y);

    VExpression result = vCreate(FRAME3, list(l, r, b, t, origNear, origFar));

    return vFunction(FRAME3, FRAME3, "move", list(frame, vector), result, false);
  }
}
