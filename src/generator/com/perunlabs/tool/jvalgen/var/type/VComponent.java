package com.perunlabs.tool.jvalgen.var.type;

import java.util.Map;

import com.perunlabs.tool.jvalgen.java.type.JField;
import com.perunlabs.tool.jvalgen.java.type.JParam;
import com.perunlabs.tool.jvalgen.java.type.JType;

public abstract class VComponent {
  private final String name;
  private final String accessorName;
  private final Map<VComponentKind, JType> kindMap;

  public VComponent(String name, String accessorName, Map<VComponentKind, JType> kindMap) {
    this.name = name;
    this.accessorName = accessorName;
    this.kindMap = kindMap;
  }

  public String name() {
    return name;
  }

  public String accessor() {
    return accessorName;
  }

  public JType jType(VComponentKind kind) {
    return kindMap.get(kind);
  }

  public JParam jParam(VTypeKind kind) {
    return jParam(jType(kind));
  }

  public JParam jParam(VComponentKind kind) {
    return jParam(jType(kind));
  }

  private JParam jParam(JType jType) {
    return JParam.jParam(jType, name());
  }

  public JField jField(VComponentKind kind) {
    return toJField(jType(kind));
  }

  private JField toJField(JType jType) {
    return JField.jField(jType, name());
  }

  public abstract JType jType(VTypeKind kind);

  public abstract boolean isPrimitive();

  public abstract boolean isBasic();

  public abstract VType vType();
}
