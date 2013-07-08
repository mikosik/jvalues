package com.perunlabs.tool.jvalgen.var.spec;

import static com.perunlabs.tool.jvalgen.Utils.list;
import static com.perunlabs.tool.jvalgen.var.expr.ExtractedVExpr.extractedVExpr;
import static com.perunlabs.tool.jvalgen.var.expr.VComponentAccesss.vComponentAccess;
import static com.perunlabs.tool.jvalgen.var.expr.VCreate.vCreate;
import static com.perunlabs.tool.jvalgen.var.spec.AllVTypes.MATRIX4;
import static com.perunlabs.tool.jvalgen.var.type.VFunctions.vFunction;
import static com.perunlabs.tool.jvalgen.var.type.VParam.vParam;

import com.google.common.collect.ImmutableList;
import com.perunlabs.tool.jvalgen.var.expr.VCall;
import com.perunlabs.tool.jvalgen.var.expr.VCreate;
import com.perunlabs.tool.jvalgen.var.expr.VExpression;
import com.perunlabs.tool.jvalgen.var.type.VFunction;
import com.perunlabs.tool.jvalgen.var.type.VParam;

public class Matrix4Spec {
  private Matrix4Spec() {}

  public static final VFunction MUL = mul();

  public static final ImmutableList<VFunction> ALL_OPERATIONS = list(MUL);

  private static VFunction mul() {
    VParam matrix1 = vParam(MATRIX4, "matrix1");
    VParam matrix2 = vParam(MATRIX4, "matrix2");

    // matrx1 components
    VExpression m1c11 = vComponentAccess(matrix1, MATRIX4.c11);
    VExpression m1c12 = vComponentAccess(matrix1, MATRIX4.c12);
    VExpression m1c13 = vComponentAccess(matrix1, MATRIX4.c13);
    VExpression m1c14 = vComponentAccess(matrix1, MATRIX4.c14);

    VExpression m1c21 = vComponentAccess(matrix1, MATRIX4.c21);
    VExpression m1c22 = vComponentAccess(matrix1, MATRIX4.c22);
    VExpression m1c23 = vComponentAccess(matrix1, MATRIX4.c23);
    VExpression m1c24 = vComponentAccess(matrix1, MATRIX4.c24);

    VExpression m1c31 = vComponentAccess(matrix1, MATRIX4.c31);
    VExpression m1c32 = vComponentAccess(matrix1, MATRIX4.c32);
    VExpression m1c33 = vComponentAccess(matrix1, MATRIX4.c33);
    VExpression m1c34 = vComponentAccess(matrix1, MATRIX4.c34);

    VExpression m1c41 = vComponentAccess(matrix1, MATRIX4.c41);
    VExpression m1c42 = vComponentAccess(matrix1, MATRIX4.c42);
    VExpression m1c43 = vComponentAccess(matrix1, MATRIX4.c43);
    VExpression m1c44 = vComponentAccess(matrix1, MATRIX4.c44);

    // matrx2 components
    VExpression m2c11 = vComponentAccess(matrix2, MATRIX4.c11);
    VExpression m2c12 = vComponentAccess(matrix2, MATRIX4.c12);
    VExpression m2c13 = vComponentAccess(matrix2, MATRIX4.c13);
    VExpression m2c14 = vComponentAccess(matrix2, MATRIX4.c14);

    VExpression m2c21 = vComponentAccess(matrix2, MATRIX4.c21);
    VExpression m2c22 = vComponentAccess(matrix2, MATRIX4.c22);
    VExpression m2c23 = vComponentAccess(matrix2, MATRIX4.c23);
    VExpression m2c24 = vComponentAccess(matrix2, MATRIX4.c24);

    VExpression m2c31 = vComponentAccess(matrix2, MATRIX4.c31);
    VExpression m2c32 = vComponentAccess(matrix2, MATRIX4.c32);
    VExpression m2c33 = vComponentAccess(matrix2, MATRIX4.c33);
    VExpression m2c34 = vComponentAccess(matrix2, MATRIX4.c34);

    VExpression m2c41 = vComponentAccess(matrix2, MATRIX4.c41);
    VExpression m2c42 = vComponentAccess(matrix2, MATRIX4.c42);
    VExpression m2c43 = vComponentAccess(matrix2, MATRIX4.c43);
    VExpression m2c44 = vComponentAccess(matrix2, MATRIX4.c44);

    // result matrix

    // result matrix column 1
    VCall rc11a = m(m1c11, m2c11);
    VCall rc11b = m(m1c21, m2c12);
    VCall rc11c = m(m1c31, m2c13);
    VCall rc11d = m(m1c41, m2c14);
    VExpression rc11 = extractedVExpr("v11", a(rc11a, a(rc11b, a(rc11c, rc11d))));

    VCall rc12a = m(m1c12, m2c11);
    VCall rc12b = m(m1c22, m2c12);
    VCall rc12c = m(m1c32, m2c13);
    VCall rc12d = m(m1c42, m2c14);
    VExpression rc12 = extractedVExpr("v12", a(rc12a, a(rc12b, a(rc12c, rc12d))));

    VCall rc13a = m(m1c13, m2c11);
    VCall rc13b = m(m1c23, m2c12);
    VCall rc13c = m(m1c33, m2c13);
    VCall rc13d = m(m1c43, m2c14);
    VExpression rc13 = extractedVExpr("v13", a(rc13a, a(rc13b, a(rc13c, rc13d))));

    VCall rc14a = m(m1c14, m2c11);
    VCall rc14b = m(m1c24, m2c12);
    VCall rc14c = m(m1c34, m2c13);
    VCall rc14d = m(m1c44, m2c14);
    VExpression rc14 = extractedVExpr("v14", a(rc14a, a(rc14b, a(rc14c, rc14d))));

    // result matrix column 2
    VCall rc21a = m(m1c11, m2c21);
    VCall rc21b = m(m1c21, m2c22);
    VCall rc21c = m(m1c31, m2c23);
    VCall rc21d = m(m1c41, m2c24);
    VExpression rc21 = extractedVExpr("v21", a(rc21a, a(rc21b, a(rc21c, rc21d))));

    VCall rc22a = m(m1c12, m2c21);
    VCall rc22b = m(m1c22, m2c22);
    VCall rc22c = m(m1c32, m2c23);
    VCall rc22d = m(m1c42, m2c24);
    VExpression rc22 = extractedVExpr("v22", a(rc22a, a(rc22b, a(rc22c, rc22d))));

    VCall rc23a = m(m1c13, m2c21);
    VCall rc23b = m(m1c23, m2c22);
    VCall rc23c = m(m1c33, m2c23);
    VCall rc23d = m(m1c43, m2c24);
    VExpression rc23 = extractedVExpr("v23", a(rc23a, a(rc23b, a(rc23c, rc23d))));

    VCall rc24a = m(m1c14, m2c21);
    VCall rc24b = m(m1c24, m2c22);
    VCall rc24c = m(m1c34, m2c23);
    VCall rc24d = m(m1c44, m2c24);
    VExpression rc24 = extractedVExpr("v24", a(rc24a, a(rc24b, a(rc24c, rc24d))));

    // result matrix column 3
    VCall rc31a = m(m1c11, m2c31);
    VCall rc31b = m(m1c21, m2c32);
    VCall rc31c = m(m1c31, m2c33);
    VCall rc31d = m(m1c41, m2c34);
    VExpression rc31 = extractedVExpr("v31", a(rc31a, a(rc31b, a(rc31c, rc31d))));

    VCall rc32a = m(m1c12, m2c31);
    VCall rc32b = m(m1c22, m2c32);
    VCall rc32c = m(m1c32, m2c33);
    VCall rc32d = m(m1c42, m2c34);
    VExpression rc32 = extractedVExpr("v32", a(rc32a, a(rc32b, a(rc32c, rc32d))));

    VCall rc33a = m(m1c13, m2c31);
    VCall rc33b = m(m1c23, m2c32);
    VCall rc33c = m(m1c33, m2c33);
    VCall rc33d = m(m1c43, m2c34);
    VExpression rc33 = extractedVExpr("v33", a(rc33a, a(rc33b, a(rc33c, rc33d))));

    VCall rc34a = m(m1c14, m2c31);
    VCall rc34b = m(m1c24, m2c32);
    VCall rc34c = m(m1c34, m2c33);
    VCall rc34d = m(m1c44, m2c34);
    VExpression rc34 = extractedVExpr("v34", a(rc34a, a(rc34b, a(rc34c, rc34d))));

    // result matrix column 4
    VCall rc41a = m(m1c11, m2c41);
    VCall rc41b = m(m1c21, m2c42);
    VCall rc41c = m(m1c31, m2c43);
    VCall rc41d = m(m1c41, m2c44);
    VExpression rc41 = extractedVExpr("v41", a(rc41a, a(rc41b, a(rc41c, rc41d))));

    VCall rc42a = m(m1c12, m2c41);
    VCall rc42b = m(m1c22, m2c42);
    VCall rc42c = m(m1c32, m2c43);
    VCall rc42d = m(m1c42, m2c44);
    VExpression rc42 = extractedVExpr("v42", a(rc42a, a(rc42b, a(rc42c, rc42d))));

    VCall rc43a = m(m1c13, m2c41);
    VCall rc43b = m(m1c23, m2c42);
    VCall rc43c = m(m1c33, m2c43);
    VCall rc43d = m(m1c43, m2c44);
    VExpression rc43 = extractedVExpr("v43", a(rc43a, a(rc43b, a(rc43c, rc43d))));

    VCall rc44a = m(m1c14, m2c41);
    VCall rc44b = m(m1c24, m2c42);
    VCall rc44c = m(m1c34, m2c43);
    VCall rc44d = m(m1c44, m2c44);
    VExpression rc44 = extractedVExpr("v44", a(rc44a, a(rc44b, a(rc44c, rc44d))));

    VCreate result = vCreate(
        MATRIX4,
        list(rc11, rc12, rc13, rc14, rc21, rc22, rc23, rc24, rc31, rc32, rc33, rc34, rc41, rc42,
            rc43, rc44));

    return vFunction(MATRIX4, MATRIX4, "mul", list(matrix1, matrix2), result, false);
  }

  private static VCall m(VExpression m1c11, VExpression m2c11) {
    return FloatSpec.MUL.vCall(m1c11, m2c11);
  }

  private static VCall a(VExpression m1c11, VExpression m2c11) {
    return FloatSpec.ADD.vCall(m1c11, m2c11);
  }
}
