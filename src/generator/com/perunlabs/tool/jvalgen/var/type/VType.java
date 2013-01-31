package com.perunlabs.tool.jvalgen.var.type;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.Preconditions.checkArgument;
import static com.perunlabs.tool.jvalgen.Utils.list;
import static com.perunlabs.tool.jvalgen.Utils.transform;
import static com.perunlabs.tool.jvalgen.java.type.JExpressions.jCall;
import static com.perunlabs.tool.jvalgen.java.type.JExpressions.jExpr;
import static com.perunlabs.tool.jvalgen.java.type.JLocalVar.jLocalVar;
import static com.perunlabs.tool.jvalgen.var.gen.Api.createCall;
import static com.perunlabs.tool.jvalgen.var.type.VComponentKind.Immutable;
import static com.perunlabs.tool.jvalgen.var.type.VComponentKind.Mutable;
import static com.perunlabs.tool.jvalgen.var.type.VComponentKind.Readable;
import static com.perunlabs.tool.jvalgen.var.type.VComponentKind.Settable;
import static com.perunlabs.tool.jvalgen.var.type.VComponents.isAnyBasic;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.MostPrimitiveC;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.MostPrimitiveG;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.XxxC;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.XxxG;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.XxxS;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.XxxV;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.perunlabs.tool.jvalgen.java.type.JExpression;
import com.perunlabs.tool.jvalgen.java.type.JLocalVar;
import com.perunlabs.tool.jvalgen.java.type.JType;

public class VType {
  private final String packageDotted;
  private final String name;
  private final ImmutableList<VComponent> vComponents;
  private final boolean isBasic;

  private final ImmutableMap<VTypeKind, JType> jTypes;

  public static VType vType(String packageDotted, String name, ImmutableList<VComponent> vComponents) {
    return new VType(packageDotted, name, vComponents);
  }

  public VType(String packageDotted, String name, ImmutableList<VComponent> vComponents) {
    this(packageDotted, name, vComponents, compoundVTypeMapping(packageDotted, name), false);
  }

  protected VType(String packageDotted, String name, ImmutableList<VComponent> vComponents,
      ImmutableMap<VTypeKind, JType> mapping, boolean isBasic) {
    this.packageDotted = packageDotted;
    this.name = name;
    this.vComponents = vComponents;
    this.isBasic = isBasic;
    this.jTypes = mapping;

    assertComponentNamesAreUnique(vComponents);
  }

  private static ImmutableMap<VTypeKind, JType> compoundVTypeMapping(String packageDotted,
      String name) {
    Map<VTypeKind, JType> mapping = createMapping(packageDotted, name);
    mapping.put(MostPrimitiveG, mapping.get(mappedCompoundKind(MostPrimitiveG)));
    mapping.put(MostPrimitiveC, mapping.get(mappedCompoundKind(MostPrimitiveC)));

    return ImmutableMap.copyOf(mapping);
  }

  public static void assertComponentNamesAreUnique(ImmutableList<VComponent> vComponents) {
    Set<String> names = Sets.newHashSet();
    for (VComponent vComponent : vComponents) {
      String name = vComponent.name();
      if (names.contains(name)) {
        throw new IllegalArgumentException("Duplicated component name = " + name);
      } else {
        names.add(name);
      }
    }
  }

  public static Map<VTypeKind, JType> createMapping(String packageDotted, String name) {
    Map<VTypeKind, JType> mapping = Maps.newHashMap();

    for (VTypeKind type : VTypeKind.ALL_TYPES_TO_GENERATE) {
      List<JType> superTypes = Lists.newArrayList();
      for (VTypeKind superType : type.superTypes()) {
        superTypes.add(mapping.get(superType));
      }

      JType newJType = JType.jType(packageDotted, type.nameFor(name),
          ImmutableList.copyOf(superTypes));
      mapping.put(type, newJType);
    }
    return mapping;
  }

  public boolean isBasic() {
    return isBasic;
  }

  public boolean hasBasicComponent() {
    return isCompound() && isAnyBasic(vComponents);
  }

  public boolean isCompound() {
    return !isBasic;
  }

  public String packageDotted() {
    return packageDotted;
  }

  public String name() {
    return name;
  }

  public ImmutableList<VComponent> vComponents() {
    return vComponents;
  }

  public boolean areComponentTypesEqual(VType vType) {
    for (VComponent vComponent : vComponents) {
      if (!vComponent.vType().equals(vType)) {
        return false;
      }
    }
    return true;
  }

  public boolean hasComponent(VComponent vComponent) {
    return vComponents.contains(vComponent);
  }

  public JType jType(VTypeKind kind) {
    return jTypes.get(kind);
  }

  public JType jType(String named, VTypeKind kind) {
    return createType(kind.nameFor(named + name), list(jType(kind)));
  }

  private JType createType(String newTypeName, ImmutableList<JType> superTypes) {
    return JType.jType(packageDotted, newTypeName, superTypes);
  }

  public JLocalVar thisVar(VTypeKind kind) {
    return jLocalVar(jType(kind), "this");
  }

  public JExpression jThisComponent(VTypeKind kind, VComponent vComp) {
    return jComponentAccessOn(thisVar(kind), vComp);
  }

  public ImmutableList<JExpression> jComponentAccessOn(JExpression jExprs) {
    return transform(vComponents(), jComponentAccessOnF(jExprs));
  }

  private Function<VComponent, JExpression> jComponentAccessOnF(final JExpression jExpr) {
    return new Function<VComponent, JExpression>() {
      @Override
      public JExpression apply(VComponent vComp) {
        return jComponentAccessOn(jExpr, vComp);
      }
    };
  }

  public JExpression jComponentAccessOn(JExpression jExpr, VComponent vComp) {
    checkArgument(hasComponent(vComp));

    JType type = vComp.jType(chooseComponentKind(jExpr));
    String code = jExpr.code() + "." + vComp.accessor() + "()";

    return jExpr(type, code, jExpr.importsNeeded());
  }

  private VComponentKind chooseComponentKind(JExpression jExpr) {
    return chooseComponentKind(jExpr.jType());
  }

  public VComponentKind chooseComponentKind(JType jType) {
    JType xxxG = jType(XxxG);
    JType xxxS = jType(XxxS);
    JType xxxV = jType(XxxV);
    JType xxxC = jType(XxxC);

    if (jType.isSubTypeOf(xxxV)) {
      return Mutable;
    }
    if (jType.isSubTypeOf(xxxG)) {
      return Readable;
    }
    if (jType.isSubTypeOf(xxxS)) {
      return Settable;
    }
    if (jType.isSubTypeOf(xxxC)) {
      return Immutable;
    }

    throw new IllegalArgumentException("JType = " + jType + " should be subclass of one of " + xxxV
        + ", " + xxxG + ", " + xxxS + ", " + xxxC);
  }

  public JExpression newInstance(VTypeKind kind, Iterable<? extends JExpression> args) {
    return newInstance("", kind, args);
  }

  public JExpression newInstance(String named, VTypeKind kind, Iterable<? extends JExpression> args) {
    JType returnType = jType(named, mappedKind(kind));
    if (kind == XxxC) {
      String callString = returnType.name() + "." + apiCreateName(kind);
      return jCall(returnType, callString, args, list(returnType));
    } else {
      String callString = "new " + returnType.name();
      return jCall(returnType, callString, args, list(returnType));
    }
  }

  public JExpression apiCreate(VTypeKind kind, Iterable<? extends JExpression> args) {
    JType requestedJType = jType(kind);
    if (Iterables.size(args) == 1) {
      JExpression onlyArg = args.iterator().next();
      if (onlyArg.jType().isSubTypeOf(requestedJType)) {
        return onlyArg;
      }
    }

    return createCall(requestedJType, apiCreateName(kind), args);
  }

  private VTypeKind mappedKind(VTypeKind kind) {
    if (!isBasic) {
      return mappedCompoundKind(kind);
    }
    return kind;
  }

  private static VTypeKind mappedCompoundKind(VTypeKind kind) {
    if (kind == MostPrimitiveG) {
      return XxxG;
    }
    if (kind == MostPrimitiveC) {
      return XxxC;
    }
    return kind;
  }

  public String apiCreateName(VTypeKind kind) {
    return UPPER_CAMEL.to(LOWER_CAMEL, jType(kind).name());
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof VType) {
      VType that = (VType) object;
      return this.name().equals(that.name()) && this.packageDotted().equals(that.packageDotted());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return name().hashCode() * 17 + packageDotted().hashCode();
  }

  @Override
  public String toString() {
    return packageDotted() + "." + name;
  }
}
