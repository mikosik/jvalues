package com.perunlabs.tool.jvalgen.var.type;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.Preconditions.checkArgument;
import static com.perunlabs.tool.jvalgen.Utils.skipOne;
import static com.perunlabs.tool.jvalgen.java.type.JExpressions.and;
import static com.perunlabs.tool.jvalgen.java.type.JExpressions.mostPrimitive;
import static com.perunlabs.tool.jvalgen.java.type.JStatements.jReturn;
import static com.perunlabs.tool.jvalgen.var.gen.Api.constCast;
import static com.perunlabs.tool.jvalgen.var.gen.Api.isConst;
import static com.perunlabs.tool.jvalgen.var.spec.NamingSpec.PRIMITIVE_SUFFIX;
import static com.perunlabs.tool.jvalgen.var.type.VComponents.isAnyBasic;
import static com.perunlabs.tool.jvalgen.var.type.VComponents.toJParam;
import static com.perunlabs.tool.jvalgen.var.type.VParams.toJParam;
import static com.perunlabs.tool.jvalgen.var.type.VParams.toVType;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.MostPrimitiveC;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.MostPrimitiveG;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.XxxC;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.XxxG;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.XxxS;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.XxxV;
import static com.perunlabs.tool.jvalgen.var.type.VTypes.isAnyBasic;
import static com.perunlabs.tool.jvalgen.var.type.VTypes.toJParam;

import com.google.common.collect.ImmutableList;
import com.perunlabs.tool.jvalgen.java.gen.ClassGenerator;
import com.perunlabs.tool.jvalgen.java.gen.CodeGenerator;
import com.perunlabs.tool.jvalgen.java.gen.MethodGenerator;
import com.perunlabs.tool.jvalgen.java.type.JBlock;
import com.perunlabs.tool.jvalgen.java.type.JExpression;
import com.perunlabs.tool.jvalgen.java.type.JExpressions;
import com.perunlabs.tool.jvalgen.java.type.JParam;
import com.perunlabs.tool.jvalgen.java.type.JStatement;
import com.perunlabs.tool.jvalgen.java.type.JType;
import com.perunlabs.tool.jvalgen.var.expr.VCall;
import com.perunlabs.tool.jvalgen.var.expr.VExpression;

public abstract class VFunction {
  private final VType owner;
  private final VType returnVType;
  private final String name;
  private final ImmutableList<VParam> vParams;
  private final boolean isInvolutary;

  public VFunction(VType owner, VType returnVType, String name, ImmutableList<VParam> vParams,
      boolean isInvolutary) {
    checkArgument(0 < vParams.size());

    this.owner = owner;
    this.returnVType = returnVType;
    this.name = name;
    this.vParams = vParams;
    this.isInvolutary = isInvolutary;
  }

  public VType owner() {
    return owner;
  }

  public VCall vCall(VExpression... args) {
    return vCall(ImmutableList.copyOf(args));
  }

  public VCall vCall(ImmutableList<? extends VExpression> args) {
    return VCall.vCall(this, args);
  }

  public String name() {
    return name;
  }

  public String aName() {
    return LOWER_CAMEL.to(UPPER_CAMEL, name());
  }

  public VType returnVType() {
    return returnVType;
  }

  public JType returnJType(VTypeKind kind) {
    return returnVType.jType(kind);
  }

  public ImmutableList<VParam> vParams() {
    return vParams;
  }

  public boolean isInvolutary() {
    return isInvolutary;
  }

  public boolean isEndofunctionForOwner() {
    return isEndofunctionForVType(owner);
  }

  public boolean isEndofunctionForVType(VType type) {
    return isEndofunction() && returnVType().equals(type);
  }

  private boolean isEndofunction() {
    return 0 < vParams.size() && returnVType().equals(vParams.get(0).vType());
  }

  public void generateApi(CodeGenerator codeGenerator, ClassGenerator apiClassGen) {
    generateClasses(codeGenerator);
    generateApiMethods(apiClassGen);
  }

  private void generateApiMethods(ClassGenerator apiClassGen) {
    JType xxxG = returnVType().jType(XxxG);
    JType xxxS = returnVType().jType(XxxS);
    JType xxxV = returnVType().jType(XxxV);
    JType xxxC = returnVType().jType(XxxC);

    String nameP = name() + PRIMITIVE_SUFFIX;

    {
      // XxxC V.operation(YyyC, ZzzC)
      ImmutableList<JParam> jParams = toJParam(vParams, XxxC);
      MethodGenerator met = apiClassGen.newStaticMethod(xxxC, name(), jParams);
      met.add(apiJCallImplementation(XxxC, jParams));
    }

    {
      // XxxG V.operation(YyyG, ZzzG)
      ImmutableList<JParam> jParams = toJParam(vParams, XxxG);
      MethodGenerator met = apiClassGen.newStaticMethod(xxxG, name(), jParams);

      JExpression condition = and(isConst(jParams));
      JStatement ifClause = jReturn(chainedCall(XxxG, constCast(jParams)));
      JBlock elseClause = apiJCallImplementation(XxxG, jParams);
      met.addIf(condition, ifClause, elseClause);
    }

    if (returnVType.isBasic()) {
      // primitive V.operation(YyyC, ZzzC)
      ImmutableList<JParam> jParams = toJParam(vParams, XxxG);
      JType primitiveC = returnVType().jType(MostPrimitiveC);
      MethodGenerator met = apiClassGen.newStaticMethod(primitiveC, nameP, jParams);
      met.add(apiJCallImplementation(MostPrimitiveC, jParams));
    }

    if (isInvolutary()) {
      {
        // XxxS V.operation(...)
        ImmutableList<JParam> jParams = toJParam(vParams, XxxS);
        MethodGenerator met = apiClassGen.newStaticMethod(xxxS, name(), jParams);
        met.add(apiJCallImplementation(XxxS, jParams));
      }

      {
        // XxxV V.operation(...)
        ImmutableList<JParam> jParams = toJParam(vParams, XxxV);
        MethodGenerator met = apiClassGen.newStaticMethod(xxxV, name(), jParams);
        met.add(apiJCallImplementation(XxxV, jParams));
      }
    }
  }

  public void createXxxVMethods(ClassGenerator classGen) {
    if (returnVType().equals(owner)) {
      JType xxxV = owner.jType(XxxV);
      ImmutableList<VType> rest = isEndofunctionForOwner() ? skipOne(toVType(vParams))
          : toVType(vParams);

      boolean isAnyBasicInRest = isAnyBasic(rest);
      boolean isRestOneCompoundVType = rest.size() == 1 && rest.get(0).isCompound();

      if (isAnyBasicInRest) {
        classGen.newMethodPair(xxxV, name(), toJParam(rest, MostPrimitiveG));
      }

      if (isRestOneCompoundVType) {
        ImmutableList<VComponent> vComponents = rest.get(0).vComponents();
        if (isAnyBasic(vComponents)) {
          classGen.newMethodPair(xxxV, name(), toJParam(vComponents, MostPrimitiveG));
        }
        classGen.newMethodPair(xxxV, name(), toJParam(vComponents, XxxG));
      }
      classGen.newMethodPair(xxxV, name(), toJParam(rest, XxxG));
    }
  }

  public void createAbstractXxxVMethods(ClassGenerator classGen) {
    if (returnVType().equals(owner)) {
      JType xxxV = owner.jType(XxxV);
      ImmutableList<VType> rest = isEndofunctionForOwner() ? skipOne(toVType(vParams))
          : toVType(vParams);

      boolean isAnyBasicInRest = isAnyBasic(rest);
      boolean isRestOneCompoundVType = rest.size() == 1 && rest.get(0).isCompound();

      if (isAnyBasicInRest) {
        // XxxV name(primitive v1);
        ImmutableList<JParam> jParams = toJParam(rest, MostPrimitiveG);
        MethodGenerator met = classGen.newMethodPairWithImpl(xxxV, name(), jParams);

        generateComponentAssigningMethodBody(met, generateJComponentAssignments(jParams));
      }

      if (isRestOneCompoundVType) {
        ImmutableList<VComponent> vComponents = rest.get(0).vComponents();
        boolean isImplementationGenerated = false;

        if (isAnyBasic(vComponents)) {
          // XxxV name(primitive field1, primitive field2, ...);
          ImmutableList<JParam> jParams = toJParam(vComponents, MostPrimitiveG);
          MethodGenerator met = classGen.newMethodPairWithImpl(xxxV, name(), jParams);

          generateComponentAssigningMethodBody(met, generateJComponentAssignments2(jParams));
          isImplementationGenerated = true;
        }

        // XxxV name(FieldG v1, FieldG v2, ...);
        ImmutableList<JParam> jParams = toJParam(vComponents, XxxG);
        MethodGenerator met = classGen.newMethodPairWithImpl(xxxV, name(), jParams);

        if (isImplementationGenerated) {
          met.add(jReturn(chainedCall(XxxV, mostPrimitive(jParams))));
        } else {
          generateComponentAssigningMethodBody(met, generateJComponentAssignments2(jParams));
        }
      }

      // XxxV name(XxxG v1);
      ImmutableList<JParam> jParams = toJParam(rest, XxxG);
      MethodGenerator met = classGen.newMethodPairWithImpl(xxxV, name(), jParams);

      if (isAnyBasicInRest) {
        met.add(jReturn(chainedCall(XxxV, mostPrimitive(jParams))));
      } else if (isRestOneCompoundVType) {
        ImmutableList<JExpression> args = rest.get(0).jComponentAccessOn(jParams.get(0));
        met.add(jReturn(chainedCall(XxxV, args)));
      } else {
        generateComponentAssigningMethodBody(met, generateJComponentAssignments(jParams));
      }
    }
  }

  private void generateComponentAssigningMethodBody(MethodGenerator met,
      ImmutableList<JStatement> jExpressions) {
    for (JStatement jExpression : jExpressions) {
      met.add(jExpression);
    }
    met.addToBody("return this;");
  }

  public JExpression chainedCall(VTypeKind kind, ImmutableList<? extends JExpression> args) {
    return chainedCall(kind, name(), args);
  }

  private JExpression chainedCall(VTypeKind kind, String metName,
      ImmutableList<? extends JExpression> args) {
    return JExpressions.jCall(returnVType().jType(kind), metName, args);
  }

  /**
   * Current owner object is implicitly taken as first argument.
   */
  public abstract ImmutableList<JStatement> generateJComponentAssignments(
      ImmutableList<JParam> jParams);

  /**
   * Current owner object is implicitly taken as first argument. Second argument
   * is passed in decomposed (list of its components).
   */
  public abstract ImmutableList<JStatement> generateJComponentAssignments2(
      ImmutableList<JParam> jParams);

  public abstract void generateClasses(CodeGenerator codeGenerator);

  public abstract JExpression apiJCall(VTypeKind kind, ImmutableList<? extends JExpression> args);

  public abstract JBlock apiJCallImplementation(VTypeKind kind,
      ImmutableList<? extends JExpression> args);

}
