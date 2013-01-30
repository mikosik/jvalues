package com.perunlabs.tool.jvalgen.var.type;

import static com.perunlabs.tool.jvalgen.var.type.VComponentKind.Immutable;
import static com.perunlabs.tool.jvalgen.var.type.VComponentKind.Mutable;
import static com.perunlabs.tool.jvalgen.var.type.VComponentKind.Readable;
import static com.perunlabs.tool.jvalgen.var.type.VComponentKind.Settable;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.XxxC;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.XxxG;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.XxxS;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.XxxV;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.perunlabs.tool.jvalgen.java.type.JType;

public class ObjectiveVComponent extends VComponent {
  private final VType vType;

  public static ObjectiveVComponent vComponent(VType vType, String name) {
    return new ObjectiveVComponent(vType, name);
  }

  public ObjectiveVComponent(VType vType, String name) {
    super(name, name, createKindMap(vType));
    this.vType = vType;
  }

  private static Map<VComponentKind, JType> createKindMap(VType vType) {
    ImmutableMap.Builder<VComponentKind, JType> builder = ImmutableMap.builder();
    builder.put(Readable, vType.jType(XxxG));
    builder.put(Settable, vType.jType(XxxS));
    builder.put(Mutable, vType.jType(XxxV));
    builder.put(Immutable, vType.jType(XxxC));
    return builder.build();
  }

  @Override
  public JType jType(VTypeKind kind) {
    return vType().jType(kind);
  }

  @Override
  public boolean isPrimitive() {
    return false;
  }

  @Override
  public boolean isBasic() {
    return vType.isBasic();
  }

  @Override
  public VType vType() {
    return vType;
  }
}
