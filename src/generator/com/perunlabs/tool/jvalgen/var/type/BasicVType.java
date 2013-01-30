package com.perunlabs.tool.jvalgen.var.type;

import static com.google.common.base.Preconditions.checkArgument;
import static com.perunlabs.tool.jvalgen.var.type.PrimitiveVComponent.vComponent;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.MostPrimitiveC;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.MostPrimitiveG;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.perunlabs.tool.jvalgen.Utils;
import com.perunlabs.tool.jvalgen.java.type.JType;

public class BasicVType extends VType {
  private final PrimitiveVComponent primitiveVComponent;

  public static BasicVType vType(String packageDotted, String name, JType primitiveJType) {
    checkArgument(primitiveJType.isPrimitive());
    Map<VTypeKind, JType> mapping = createMapping(packageDotted, name);
    mapping.put(MostPrimitiveG, primitiveJType);
    mapping.put(MostPrimitiveC, primitiveJType);

    return new BasicVType(packageDotted, name, vComponent(primitiveJType), ImmutableMap.copyOf(mapping));
  }

  private BasicVType(String packageDotted, String name, PrimitiveVComponent vComponent,
      ImmutableMap<VTypeKind, JType> mapping) {
    super(packageDotted, name, Utils.<VComponent> list(vComponent), mapping, true);
    this.primitiveVComponent = vComponent;
  }

  public PrimitiveVComponent primitiveVComponent() {
    return primitiveVComponent;
  }
}
