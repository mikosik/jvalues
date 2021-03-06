package com.perunlabs.tool.jvalgen.var.spec;

import static com.perunlabs.tool.jvalgen.Utils.list;
import static com.perunlabs.tool.jvalgen.var.expr.ExtractedVExpr.extractedVExpr;
import static com.perunlabs.tool.jvalgen.var.expr.VComponentAccesss.vComponentAccess;
import static com.perunlabs.tool.jvalgen.var.expr.VCreate.vCreate;
import static com.perunlabs.tool.jvalgen.var.spec.AllVTypes.FRAME;
import static com.perunlabs.tool.jvalgen.var.spec.AllVTypes.QUAD;
import static com.perunlabs.tool.jvalgen.var.spec.AllVTypes.QUANTITY;
import static com.perunlabs.tool.jvalgen.var.spec.AllVTypes.VBOOL;
import static com.perunlabs.tool.jvalgen.var.spec.AllVTypes.VECTOR;
import static com.perunlabs.tool.jvalgen.var.spec.AllVTypes.VFLOAT;
import static com.perunlabs.tool.jvalgen.var.type.VFunctions.compoundLowVFunction;
import static com.perunlabs.tool.jvalgen.var.type.VFunctions.vFunction;
import static com.perunlabs.tool.jvalgen.var.type.VParam.vParam;

import com.google.common.collect.ImmutableList;
import com.perunlabs.tool.jvalgen.var.expr.VExpression;
import com.perunlabs.tool.jvalgen.var.type.ExpressionVFunction;
import com.perunlabs.tool.jvalgen.var.type.VFunction;
import com.perunlabs.tool.jvalgen.var.type.VParam;

public class FrameSpec {
  private FrameSpec() {}

  public static final VFunction CENTER = center();
  public static final VFunction WIDTH = width();
  public static final VFunction HEIGHT = height();
  public static final VFunction SIZE = size();
  public static final VFunction ASPEC_RATIO = aspectRatio();

  public static final VFunction IS_OVERLAPPING = isOverlapping();
  public static final VFunction CONTAINS_POINT = containsPoint();

  public static final VFunction MOVE = move();
  public static final VFunction TO_GLOBAL = toGlobal();
  public static final VFunction MUL = compoundLowVFunction(FRAME, FloatSpec.MUL);

  public static final ImmutableList<VFunction> ALL_OPERATIONS = list(CENTER, WIDTH, HEIGHT, SIZE,
      ASPEC_RATIO, IS_OVERLAPPING, MOVE, TO_GLOBAL, MUL);

  private static ExpressionVFunction center() {
    VParam frame = vParam(FRAME, "frame");

    VExpression left = vComponentAccess(frame, FRAME.left);
    VExpression right = vComponentAccess(frame, FRAME.right);
    VExpression bottom = vComponentAccess(frame, FRAME.bottom);
    VExpression top = vComponentAccess(frame, FRAME.top);

    VExpression x = FloatSpec.AVERAGE.vCall(left, right);
    VExpression y = FloatSpec.AVERAGE.vCall(bottom, top);

    VExpression result = vCreate(VECTOR, list(x, y));

    return vFunction(FRAME, VECTOR, "center", list(frame), result, false);
  }

  private static ExpressionVFunction width() {
    VParam frame = vParam(FRAME, "frame");

    VExpression left = vComponentAccess(frame, FRAME.left);
    VExpression right = vComponentAccess(frame, FRAME.right);
    VExpression width = FloatSpec.DIF.vCall(left, right);

    return vFunction(FRAME, VFLOAT, "width", list(frame), width, false);
  }

  private static ExpressionVFunction height() {
    VParam frame = vParam(FRAME, "frame");

    VExpression bottom = vComponentAccess(frame, FRAME.bottom);
    VExpression top = vComponentAccess(frame, FRAME.top);

    VExpression result = FloatSpec.DIF.vCall(bottom, top);

    return vFunction(FRAME, VFLOAT, "height", list(frame), result, false);
  }

  private static ExpressionVFunction size() {
    VParam frame = vParam(FRAME, "frame");

    VExpression width = WIDTH.vCall(frame);
    VExpression height = HEIGHT.vCall(frame);

    VExpression result = vCreate(VECTOR, list(width, height));

    return vFunction(FRAME, VECTOR, "size", list(frame), result, false);
  }

  private static ExpressionVFunction aspectRatio() {
    VParam frame = vParam(FRAME, "frame");

    VExpression width = WIDTH.vCall(frame);
    VExpression height = HEIGHT.vCall(frame);

    VExpression result = FloatSpec.DIV.vCall(width, height);

    return vFunction(FRAME, VFLOAT, "aspectRatio", list(frame), result, false);
  }

  private static ExpressionVFunction isOverlapping() {
    VParam frame = vParam(FRAME, "frame");
    VParam frame2 = vParam(FRAME, "frame2");

    VExpression f1Left = vComponentAccess(frame, FRAME.left);
    VExpression f1Right = vComponentAccess(frame, FRAME.right);
    VExpression f1Bottom = vComponentAccess(frame, FRAME.bottom);
    VExpression f1Top = vComponentAccess(frame, FRAME.top);

    VExpression f2Left = vComponentAccess(frame2, FRAME.left);
    VExpression f2Right = vComponentAccess(frame2, FRAME.right);
    VExpression f2Bottom = vComponentAccess(frame2, FRAME.bottom);
    VExpression f2Top = vComponentAccess(frame2, FRAME.top);

    VExpression cond1 = FloatSpec.IS_GTEQ.vCall(f1Right, f2Left);
    VExpression cond2 = FloatSpec.IS_GTEQ.vCall(f2Right, f1Left);
    VExpression cond3 = FloatSpec.IS_GTEQ.vCall(f1Top, f2Bottom);
    VExpression cond4 = FloatSpec.IS_GTEQ.vCall(f2Top, f1Bottom);

    VExpression and1 = BoolSpec.AND.vCall(cond1, cond2);
    VExpression and2 = BoolSpec.AND.vCall(and1, cond3);

    VExpression result = BoolSpec.AND.vCall(and2, cond4);

    return vFunction(FRAME, VBOOL, "isOverlapping", list(frame, frame2), result, false);
  }

  private static ExpressionVFunction containsPoint() {
    VParam frame = vParam(FRAME, "frame");
    VParam point = vParam(VECTOR, "point");

    VExpression l = vComponentAccess(frame, FRAME.left);
    VExpression r = vComponentAccess(frame, FRAME.right);
    VExpression b = vComponentAccess(frame, FRAME.bottom);
    VExpression t = vComponentAccess(frame, FRAME.top);

    VExpression x = vComponentAccess(point, VECTOR.x);
    VExpression y = vComponentAccess(point, VECTOR.y);

    VExpression cond1 = FloatSpec.IS_GTEQ.vCall(r, x);
    VExpression cond2 = FloatSpec.IS_GTEQ.vCall(x, l);
    VExpression cond3 = FloatSpec.IS_GTEQ.vCall(t, y);
    VExpression cond4 = FloatSpec.IS_GTEQ.vCall(y, b);

    VExpression and1 = BoolSpec.AND.vCall(cond1, cond2);
    VExpression and2 = BoolSpec.AND.vCall(and1, cond3);

    VExpression result = BoolSpec.AND.vCall(and2, cond4);

    return vFunction(FRAME, VBOOL, "isOverlapping", list(frame, point), result, false);
  }

  private static ExpressionVFunction move() {
    VParam frame = vParam(FRAME, "frame");
    VParam vector = vParam(VECTOR, "vector");

    VExpression origLeft = vComponentAccess(frame, FRAME.left);
    VExpression origRight = vComponentAccess(frame, FRAME.right);
    VExpression origBottom = vComponentAccess(frame, FRAME.bottom);
    VExpression origTop = vComponentAccess(frame, FRAME.top);

    // TODO it generate some unnecessary variables in AbstractVectorV that are
    // neither needed nor lower performance. It would be better if instead of
    // jLocalVar we have jLocalVarInline which would not create extracted jVar
    // when jExpression is primitive and is not compound statement (=is just
    // jLocalVar or jParam or jField ...) - because it doesn't make sense to
    // extract such simple jExpressions

    VExpression x = extractedVExpr("x_", vComponentAccess(vector, VECTOR.x));
    VExpression y = extractedVExpr("y_", vComponentAccess(vector, VECTOR.y));

    VExpression l = FloatSpec.ADD.vCall(origLeft, x);
    VExpression r = FloatSpec.ADD.vCall(origRight, x);
    VExpression b = FloatSpec.ADD.vCall(origBottom, y);
    VExpression t = FloatSpec.ADD.vCall(origTop, y);

    VExpression result = vCreate(FRAME, list(l, r, b, t));

    return vFunction(FRAME, FRAME, "move", list(frame, vector), result, false);
  }

  private static ExpressionVFunction toGlobal() {
    VParam frame = vParam(FRAME, "frame");
    VParam quantity = vParam(QUANTITY, "quantity");

    VExpression quad = QuadSpec.TO_QUAD.vCall(frame);
    VExpression toGlobaled = QuadSpec.TO_GLOBAL.vCall(quad, quantity);
    return vFunction(FRAME, QUAD, "toGlobal", list(frame, quantity), toGlobaled, false);
  }
}
