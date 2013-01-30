package com.perunlabs.tool.jvalgen.var.type;

import static com.google.common.base.Preconditions.checkArgument;
import static com.perunlabs.tool.jvalgen.Utils.append;
import static com.perunlabs.tool.jvalgen.Utils.list;
import static com.perunlabs.tool.jvalgen.java.type.JFields.toFinal;
import static com.perunlabs.tool.jvalgen.java.type.JFunction.jFunction;
import static com.perunlabs.tool.jvalgen.java.type.JParams.toVParam;
import static com.perunlabs.tool.jvalgen.java.type.JStatements.jReturn;
import static com.perunlabs.tool.jvalgen.java.type.JTypes.toVType;
import static com.perunlabs.tool.jvalgen.var.gen.Api.createCall;
import static com.perunlabs.tool.jvalgen.var.type.VParams.toJField;
import static com.perunlabs.tool.jvalgen.var.type.VParams.toJParam;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.AbstractXxxG;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.AbstractXxxS;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.AbstractXxxV;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.MostPrimitiveC;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.MostPrimitiveG;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.XxxC;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.XxxG;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.XxxS;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.XxxV;

import com.google.common.collect.ImmutableList;
import com.perunlabs.tool.jvalgen.java.gen.ClassGenerator;
import com.perunlabs.tool.jvalgen.java.gen.CodeGenerator;
import com.perunlabs.tool.jvalgen.java.gen.MethodGenerator;
import com.perunlabs.tool.jvalgen.java.type.JBlock;
import com.perunlabs.tool.jvalgen.java.type.JCallable;
import com.perunlabs.tool.jvalgen.java.type.JExpression;
import com.perunlabs.tool.jvalgen.java.type.JExpressions;
import com.perunlabs.tool.jvalgen.java.type.JField;
import com.perunlabs.tool.jvalgen.java.type.JFunction;
import com.perunlabs.tool.jvalgen.java.type.JParam;
import com.perunlabs.tool.jvalgen.java.type.JStatement;
import com.perunlabs.tool.jvalgen.java.type.JType;

/**
 * Function on Basic VType which implementation redirects to JCallable. All
 * parameters in such method must be java primitives - this way it can accept
 * VType.jType(MostPrimitiveG) and also all JTypes that implement
 * VType.jType(XxxG).
 */
public class BasicVFunction extends VFunction {
  private final JCallable jCallable;

  public static BasicVFunction involutary(VType owner, Class<?> klass, String method) {
    return new BasicVFunction(owner, klass, method, true);
  }

  public static BasicVFunction simple(VType owner, Class<?> klass, String method) {
    return new BasicVFunction(owner, klass, method);
  }

  public static BasicVFunction inline(VType owner, String name, ImmutableList<JParam> jParams,
      JCallable jCallable) {
    return new BasicVFunction(owner, toVType(jCallable.returnJType()), name, jCallable,
        toVParam(jParams), false);
  }

  private BasicVFunction(VType owner, Class<?> klass, String methodName) {
    this(owner, klass, methodName, false);
  }

  private BasicVFunction(VType owner, Class<?> klass, String methodName, boolean isInvolutary) {
    this(owner, jFunction(klass, methodName), isInvolutary);
  }

  private BasicVFunction(VType owner, JFunction jFunction, boolean isInvolutary) {
    this(owner, toVType(jFunction.returnJType()), jFunction.name(), jFunction, toVParam(jFunction
        .jParams()), isInvolutary);
  }

  private BasicVFunction(VType owner, VType returnVType, String name, JCallable jCallable,
      ImmutableList<VParam> jParams, boolean isInvolutary) {
    super(owner, returnVType, name, jParams, isInvolutary);

    this.jCallable = jCallable;

    if (isInvolutary) {
      String shouldHave = "Method implementation for involutary funtion should have ";
      checkArgument(jParams.size() == 1, shouldHave + "exactly one argument.");
      checkArgument(returnVType.equals(jParams.get(0).vType()), shouldHave
          + "return vType and jParam vType equal.");
    }
  }

  @Override
  public void generateClasses(CodeGenerator gen) {
    JType aFunctionXxxG = owner().jType(aName(), XxxG);
    JType aFunctionXxxS = owner().jType(aName(), XxxS);
    JType aFunctionXxxV = owner().jType(aName(), XxxV);
    JType returnAbstXxxG = returnJType(AbstractXxxG);
    JType returnAbstXxxS = returnJType(AbstractXxxS);
    JType returnAbstXxxV = returnJType(AbstractXxxV);

    // FunctionXxxG class
    {
      ClassGenerator classGen = gen.newClass(aFunctionXxxG).addExtends(returnAbstXxxG);
      ImmutableList<JField> jFields = vParamsToFinalJFields(XxxG);
      classGen.addCopyConstructor(jFields);
      generatedGetMethod(classGen, jFields);
    }

    if (isInvolutary()) {
      // FuntionXxxS class
      {
        ClassGenerator classGen = gen.newClass(aFunctionXxxS).addExtends(returnAbstXxxS);
        classGen.addCopyConstructor(vParamsToFinalJFields(XxxS));
        generateSetMethod(classGen, XxxS);
      }

      // FunctionXxxV class
      {
        ClassGenerator classGen = gen.newClass(aFunctionXxxV).addExtends(returnAbstXxxV);
        ImmutableList<JField> jFields = vParamsToFinalJFields(XxxV);
        classGen.addCopyConstructor(jFields);
        generatedGetMethod(classGen, jFields);
        generateSetMethod(classGen, XxxV);
      }
    }
  }

  private ImmutableList<JField> vParamsToFinalJFields(VTypeKind kind) {
    return toFinal(toJField(vParams(), kind));
  }

  private void generatedGetMethod(ClassGenerator classGen, ImmutableList<JField> jFields) {
    MethodGenerator get = classGen.newMethod(returnJType(MostPrimitiveG), "get");
    get.addOverride();
    get.add(jReturn(calc(jFields)));
  }

  private void generateSetMethod(ClassGenerator classGen, VTypeKind returnKind) {
    ImmutableList<JParam> jParams = toJParam(vParams(), MostPrimitiveG);
    MethodGenerator met = classGen.newMethod(returnJType(returnKind), "set", jParams);
    met.addOverride();
    JStatement call = calc(jParams);
    classGen.addImports(call.importsNeeded());
    met.addToBody("return this.v1.set(" + call.code() + ");");
  }

  @Override
  public ImmutableList<JStatement> generateJComponentAssignments(ImmutableList<JParam> jParams) {
    ImmutableList.Builder<JStatement> builder = ImmutableList.builder();
    ImmutableList<JExpression> args = append(owner().thisVar(XxxV), jParams);
    for (VComponent vComponent : owner().vComponents()) {
      builder.add(invokeOnComponent(vComponent, args));
    }
    return builder.build();
  }

  // second argument is passed in decomposed form
  @Override
  public ImmutableList<JStatement> generateJComponentAssignments2(ImmutableList<JParam> jParams) {
    ImmutableList.Builder<JStatement> builder = ImmutableList.builder();
    for (int i = 0; i < owner().vComponents().size(); i++) {
      VComponent vComponent = owner().vComponents().get(i);
      JParam jParam = jParams.get(i);
      ImmutableList<JExpression> args = append(owner().thisVar(XxxV), list(jParam));
      builder.add(invokeOnComponent(vComponent, args));
    }
    return builder.build();
  }

  private JExpression invokeOnComponent(VComponent vComponent, ImmutableList<? extends JExpression> args) {
    JType returnJType = vComponent.vType().jType(XxxV);
    return JExpressions.jCall(returnJType, "set", list(calc(args)));
  }

  @Override
  public JExpression apiJCall(VTypeKind kind, ImmutableList<? extends JExpression> args) {
    if (kind == XxxC || kind == MostPrimitiveC || kind == MostPrimitiveG) {
      return calc(args);
    } else {
      return createCall(returnJType(kind), name(), args);
    }
  }

  @Override
  public JBlock apiJCallImplementation(VTypeKind kind, ImmutableList<? extends JExpression> args) {
    if (kind == XxxC || kind == MostPrimitiveC) {
      return jReturn(returnVType().apiCreate(kind, list(calc(args))));
    } else {
      return jReturn((owner().newInstance(aName(), kind, args)));
    }
  }

  public JExpression calc(JExpression... args) {
    return calc(ImmutableList.copyOf(args));
  }

  private JExpression calc(ImmutableList<? extends JExpression> args) {
    return jCallable.jCall(args);
  }

  @Override
  public String toString() {
    return "BasicVFunction: " + name();
  }
}
