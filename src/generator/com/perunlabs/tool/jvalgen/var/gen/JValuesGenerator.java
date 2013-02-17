package com.perunlabs.tool.jvalgen.var.gen;

import static com.perunlabs.tool.jvalgen.var.spec.AllVTypes.FRAME;
import static com.perunlabs.tool.jvalgen.var.spec.AllVTypes.QUAD;
import static com.perunlabs.tool.jvalgen.var.spec.AllVTypes.QUANTITY;
import static com.perunlabs.tool.jvalgen.var.spec.AllVTypes.VBOOL;
import static com.perunlabs.tool.jvalgen.var.spec.AllVTypes.VECTOR;
import static com.perunlabs.tool.jvalgen.var.spec.AllVTypes.VECTOR3;
import static com.perunlabs.tool.jvalgen.var.spec.AllVTypes.VFLOAT;
import static com.perunlabs.tool.jvalgen.var.spec.AllVTypes.VINT;
import static com.perunlabs.tool.jvalgen.var.spec.AllVTypes.VLONG;

import java.io.IOException;

import com.perunlabs.tool.jvalgen.java.gen.ClassGenerator;
import com.perunlabs.tool.jvalgen.java.gen.CodeGenerator;
import com.perunlabs.tool.jvalgen.var.spec.AllVTypes;
import com.perunlabs.tool.jvalgen.var.spec.BoolSpec;
import com.perunlabs.tool.jvalgen.var.spec.FloatSpec;
import com.perunlabs.tool.jvalgen.var.spec.FrameSpec;
import com.perunlabs.tool.jvalgen.var.spec.IntSpec;
import com.perunlabs.tool.jvalgen.var.spec.LongSpec;
import com.perunlabs.tool.jvalgen.var.spec.QuadSpec;
import com.perunlabs.tool.jvalgen.var.spec.QuantitySpec;
import com.perunlabs.tool.jvalgen.var.spec.Vector3Spec;
import com.perunlabs.tool.jvalgen.var.spec.VectorSpec;

public class JValuesGenerator {
  private final CodeGenerator generator;

  public JValuesGenerator() {
    this.generator = new CodeGenerator();
  }

  public void generate(String destinationDir) throws IOException {
    ClassGenerator apiClassGen = generator.newClass(AllVTypes.V_API_CLASS);

    new VTypeGenerator(generator, apiClassGen, VBOOL, BoolSpec.ALL_OPERATIONS).generate();
    new VTypeGenerator(generator, apiClassGen, VINT, IntSpec.ALL_OPERATIONS).generate();
    new VTypeGenerator(generator, apiClassGen, VLONG, LongSpec.ALL_OPERATIONS).generate();
    new VTypeGenerator(generator, apiClassGen, VFLOAT, FloatSpec.ALL_OPERATIONS).generate();

    new VTypeGenerator(generator, apiClassGen, VECTOR, VectorSpec.ALL_OPERATIONS).generate();
    new VTypeGenerator(generator, apiClassGen, VECTOR3, Vector3Spec.ALL_OPERATIONS).generate();
    new VTypeGenerator(generator, apiClassGen, FRAME, FrameSpec.ALL_OPERATIONS).generate();
    new VTypeGenerator(generator, apiClassGen, QUANTITY, QuantitySpec.ALL_OPERATIONS).generate();
    new VTypeGenerator(generator, apiClassGen, QUAD, QuadSpec.ALL_OPERATIONS).generate();

    generator.generate(destinationDir);
  }
}
