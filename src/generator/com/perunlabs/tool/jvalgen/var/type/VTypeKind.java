package com.perunlabs.tool.jvalgen.var.type;

import static com.perunlabs.tool.jvalgen.Utils.list;

import com.google.common.collect.ImmutableList;
import com.perunlabs.tool.jvalgen.Utils;

public enum VTypeKind {
  XxxG("G", Utils.<VTypeKind> list()),

  XxxS("S", Utils.<VTypeKind> list()),

  XxxV("V", list(XxxG, XxxS)),

  AbstractXxxG("Abstract", "G", list(XxxG)),

  AbstractXxxS("Abstract", "S", list(XxxS)),

  AbstractXxxV("Abstract", "V", list(XxxV, AbstractXxxG)),

  XxxGImpl("GImpl", list(AbstractXxxG)),

  XxxSImpl("SImpl", list(AbstractXxxS)),

  XxxVImpl("VImpl", list(AbstractXxxV)),

  XxxC("C", list(AbstractXxxG)),

  IffXxxG("Iff", "G", list(AbstractXxxG)),

  MostPrimitiveG(null, null, null),

  MostPrimitiveC(null, null, null);

  public static final ImmutableList<VTypeKind> ALL_TYPES_TO_GENERATE = list(XxxG, XxxS, XxxV,
      AbstractXxxG, AbstractXxxS, AbstractXxxV, XxxGImpl, XxxSImpl, XxxVImpl, XxxC, IffXxxG);

  public static final ImmutableList<VTypeKind> ALL_TYPES = list(XxxG, XxxS, XxxV, AbstractXxxG,
      AbstractXxxS, AbstractXxxV, XxxGImpl, XxxSImpl, XxxVImpl, XxxC, IffXxxG, MostPrimitiveG,
      MostPrimitiveC);

  private final String prefix;
  private final String suffix;
  private final ImmutableList<VTypeKind> superTypes;

  private VTypeKind(String suffix, ImmutableList<VTypeKind> superTypes) {
    this("", suffix, superTypes);
  }

  private VTypeKind(String prefix, String suffix, ImmutableList<VTypeKind> superTypes) {
    this.prefix = prefix;
    this.suffix = suffix;
    this.superTypes = superTypes;
  }

  public String nameFor(String vTypeName) {
    if (prefix == null) {
      throw new RuntimeException("This VTypeJType has unknown name");
    }
    return prefix + vTypeName + suffix;
  }

  public ImmutableList<VTypeKind> superTypes() {
    return superTypes;
  }
}
