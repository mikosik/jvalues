package com.perunlabs.tool.jvalgen.var.spec;

import static com.perunlabs.tool.jvalgen.Utils.list;
import static com.perunlabs.tool.jvalgen.var.expr.VComponentAccesss.vComponentAccess;
import static com.perunlabs.tool.jvalgen.var.expr.VCreate.vCreate;
import static com.perunlabs.tool.jvalgen.var.spec.AllVTypes.FRAME;
import static com.perunlabs.tool.jvalgen.var.spec.AllVTypes.QUAD;
import static com.perunlabs.tool.jvalgen.var.spec.AllVTypes.QUANTITY;
import static com.perunlabs.tool.jvalgen.var.spec.AllVTypes.VECTOR;
import static com.perunlabs.tool.jvalgen.var.spec.AllVTypes.VFLOAT;
import static com.perunlabs.tool.jvalgen.var.type.VFunctions.vFunction;
import static com.perunlabs.tool.jvalgen.var.type.VParam.vParam;

import com.google.common.collect.ImmutableList;
import com.perunlabs.tool.jvalgen.var.expr.VExpression;
import com.perunlabs.tool.jvalgen.var.type.ExpressionVFunction;
import com.perunlabs.tool.jvalgen.var.type.VFunction;
import com.perunlabs.tool.jvalgen.var.type.VParam;

public class QuadSpec {
  private QuadSpec() {}

  public static VFunction MOVE = move();
  public static VFunction TO_QUAD = toQuad();
  public static VFunction ROTATE = rotate();
  public static final VFunction TO_GLOBAL = toGlobal();

  public static final ImmutableList<VFunction> ALL_OPERATIONS = list(MOVE, TO_QUAD, ROTATE,
      TO_GLOBAL);

  private static ExpressionVFunction move() {
    VParam quad = vParam(QUAD, "quad");
    VParam vector = vParam(VECTOR, "vector");

    VExpression origV1 = vComponentAccess(quad, QUAD.v1);
    VExpression origV2 = vComponentAccess(quad, QUAD.v2);
    VExpression origV3 = vComponentAccess(quad, QUAD.v3);
    VExpression origV4 = vComponentAccess(quad, QUAD.v4);

    VExpression v1 = VectorSpec.ADD.vCall(origV1, vector);
    VExpression v2 = VectorSpec.ADD.vCall(origV2, vector);
    VExpression v3 = VectorSpec.ADD.vCall(origV3, vector);
    VExpression v4 = VectorSpec.ADD.vCall(origV4, vector);

    VExpression result = vCreate(QUAD, list(v1, v2, v3, v4));

    return vFunction(QUAD, QUAD, "move", list(quad, vector), result, false);
  }

  private static ExpressionVFunction toQuad() {
    VParam frame = vParam(FRAME, "frame");

    VExpression l = vComponentAccess(frame, FRAME.left);
    VExpression r = vComponentAccess(frame, FRAME.right);
    VExpression b = vComponentAccess(frame, FRAME.bottom);
    VExpression t = vComponentAccess(frame, FRAME.top);

    VExpression v1 = vCreate(VECTOR, list(l, b));
    VExpression v2 = vCreate(VECTOR, list(r, b));
    VExpression v3 = vCreate(VECTOR, list(r, t));
    VExpression v4 = vCreate(VECTOR, list(l, t));

    VExpression result = vCreate(QUAD, list(v1, v2, v3, v4));

    return vFunction(QUAD, QUAD, "toQuad", list(frame), result, false);
  }

  private static ExpressionVFunction rotate() {
    VParam quad = vParam(QUAD, "quad");
    VParam angle = vParam(VFLOAT, "angle");

    VExpression origV1 = vComponentAccess(quad, QUAD.v1);
    VExpression origV2 = vComponentAccess(quad, QUAD.v2);
    VExpression origV3 = vComponentAccess(quad, QUAD.v3);
    VExpression origV4 = vComponentAccess(quad, QUAD.v4);

    VExpression v1 = VectorSpec.ROTATE.vCall(origV1, angle);
    VExpression v2 = VectorSpec.ROTATE.vCall(origV2, angle);
    VExpression v3 = VectorSpec.ROTATE.vCall(origV3, angle);
    VExpression v4 = VectorSpec.ROTATE.vCall(origV4, angle);

    VExpression result = vCreate(QUAD, list(v1, v2, v3, v4));

    return vFunction(QUAD, QUAD, "rotate", list(quad, angle), result, false);
  }

  private static ExpressionVFunction toGlobal() {
    VParam quad = vParam(QUAD, "quad");
    VParam quantity = vParam(QUANTITY, "quantity");

    VExpression angle = vComponentAccess(quantity, QUANTITY.angle);
    VExpression rotated = ROTATE.vCall(quad, angle);

    VExpression vector = vComponentAccess(quantity, QUANTITY.vector);
    VExpression moved = QuadSpec.MOVE.vCall(rotated, vector);

    return vFunction(QUAD, QUAD, "toGlobal", list(quad, quantity), moved, false);
  }
}
