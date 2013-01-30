package com.perunlabs.tool.jvalgen.var.type;

import static com.perunlabs.tool.jvalgen.java.type.JTypes.toVType;
import static com.perunlabs.tool.jvalgen.var.spec.NamingSpec.BASIC_TYPE_GET_METHOD;
import static com.perunlabs.tool.jvalgen.var.type.VComponentKind.Immutable;
import static com.perunlabs.tool.jvalgen.var.type.VComponentKind.Mutable;
import static com.perunlabs.tool.jvalgen.var.type.VComponentKind.Readable;
import static com.perunlabs.tool.jvalgen.var.type.VComponentKind.Settable;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.perunlabs.tool.jvalgen.java.type.JType;

public class PrimitiveVComponent extends VComponent {
  private final JType primitiveJType;

  public static PrimitiveVComponent vComponent(JType primitiveJType) {
    return new PrimitiveVComponent(primitiveJType);
  }

  private PrimitiveVComponent(JType primitiveJType) {
    super("value", BASIC_TYPE_GET_METHOD, createKindMap(primitiveJType));
    this.primitiveJType = primitiveJType;
  }

  private static Map<VComponentKind, JType> createKindMap(JType jType) {
    ImmutableMap.Builder<VComponentKind, JType> builder = ImmutableMap.builder();
    builder.put(Readable, jType);
    builder.put(Settable, jType);
    builder.put(Mutable, jType);
    builder.put(Immutable, jType);
    return builder.build();
  }

  @Override
  public JType jType(VTypeKind kind) {
    return vType().jType(kind);
  }

  @Override
  public boolean isPrimitive() {
    return true;
  }

  @Override
  public boolean isBasic() {
    return false;
  }

  @Override
  public VType vType() {
    return toVType(primitiveJType);
  }
}
