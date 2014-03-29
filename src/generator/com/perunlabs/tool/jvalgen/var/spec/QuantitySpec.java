package com.perunlabs.tool.jvalgen.var.spec;

import static com.perunlabs.tool.jvalgen.Utils.list;
import static com.perunlabs.tool.jvalgen.var.expr.VComponentAccesss.vComponentAccess;
import static com.perunlabs.tool.jvalgen.var.expr.VCreate.vCreate;
import static com.perunlabs.tool.jvalgen.var.spec.AllVTypes.QUANTITY;
import static com.perunlabs.tool.jvalgen.var.type.VFunctions.vFunction;
import static com.perunlabs.tool.jvalgen.var.type.VParam.vParam;

import com.google.common.collect.ImmutableList;
import com.perunlabs.tool.jvalgen.var.expr.VExpression;
import com.perunlabs.tool.jvalgen.var.type.VFunction;
import com.perunlabs.tool.jvalgen.var.type.VParam;

public class QuantitySpec {
  private QuantitySpec() {}

  public static final VFunction TO_GLOBAL_POSITION = toGlobalPosition();
  public static final VFunction TO_LOCAL_POSITION = toLocalPosition();

  public static final ImmutableList<VFunction> ALL_OPERATIONS = ImmutableList.of(
      TO_GLOBAL_POSITION, TO_LOCAL_POSITION);

  private static VFunction toGlobalPosition() {
    VParam quantity = vParam(QUANTITY, "quantity");
    VParam position = vParam(QUANTITY, "position");

    VExpression quantityVector = vComponentAccess(quantity, QUANTITY.vector);
    VExpression quantityAngle = vComponentAccess(quantity, QUANTITY.angle);

    VExpression positionAngle = vComponentAccess(position, QUANTITY.angle);

    VExpression positionedVector = VectorSpec.TO_GLOBAL_POINT.vCall(quantityVector, position);
    VExpression positionedAngle = FloatSpec.ADD.vCall(quantityAngle, positionAngle);

    VExpression result = vCreate(QUANTITY, list(positionedVector, positionedAngle));

    return vFunction(QUANTITY, QUANTITY, "toGlobalPosition", list(quantity, position), result,
        false);
  }

  private static VFunction toLocalPosition() {
    VParam quantity = vParam(QUANTITY, "quantity");
    VParam position = vParam(QUANTITY, "position");

    VExpression quantityVector = vComponentAccess(quantity, QUANTITY.vector);
    VExpression quantityAngle = vComponentAccess(quantity, QUANTITY.angle);

    VExpression positionAngle = vComponentAccess(position, QUANTITY.angle);

    VExpression positionedVector = VectorSpec.TO_LOCAL_POINT.vCall(quantityVector, position);
    VExpression positionedAngle = FloatSpec.SUB.vCall(quantityAngle, positionAngle);

    VExpression result = vCreate(QUANTITY, list(positionedVector, positionedAngle));

    return vFunction(QUANTITY, QUANTITY, "toLocalPosition", list(quantity, position), result, false);
  }
}
