package com.perunlabs.tool.jvalgen.var.gen;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Lists.newArrayList;
import static com.perunlabs.tool.jvalgen.Utils.list;
import static com.perunlabs.tool.jvalgen.java.type.JExpressions.getInvokedOn;
import static com.perunlabs.tool.jvalgen.java.type.JExpressions.jCall;
import static com.perunlabs.tool.jvalgen.java.type.JExpressions.jExpr;
import static com.perunlabs.tool.jvalgen.java.type.JExpressions.mostPrimitive;
import static com.perunlabs.tool.jvalgen.java.type.JField.jField;
import static com.perunlabs.tool.jvalgen.java.type.JField.jFieldConst;
import static com.perunlabs.tool.jvalgen.java.type.JField.jFieldFinal;
import static com.perunlabs.tool.jvalgen.java.type.JFields.toFinal;
import static com.perunlabs.tool.jvalgen.java.type.JLocalVar.jLocalVar;
import static com.perunlabs.tool.jvalgen.java.type.JParam.jParam;
import static com.perunlabs.tool.jvalgen.java.type.JParam.jParamFinal;
import static com.perunlabs.tool.jvalgen.java.type.JStatements.jAssign;
import static com.perunlabs.tool.jvalgen.java.type.JStatements.jReturn;
import static com.perunlabs.tool.jvalgen.java.type.JStatements.jSet;
import static com.perunlabs.tool.jvalgen.java.type.JType.JBOOLEAN;
import static com.perunlabs.tool.jvalgen.java.type.JType.JDOUBLE;
import static com.perunlabs.tool.jvalgen.java.type.JType.JFLOAT;
import static com.perunlabs.tool.jvalgen.java.type.JType.JINT;
import static com.perunlabs.tool.jvalgen.java.type.JType.JOBJECT;
import static com.perunlabs.tool.jvalgen.java.type.JType.JSTRING;
import static com.perunlabs.tool.jvalgen.java.type.JType.JVOID;
import static com.perunlabs.tool.jvalgen.var.gen.Api.constCast;
import static com.perunlabs.tool.jvalgen.var.gen.Api.create;
import static com.perunlabs.tool.jvalgen.var.gen.Api.iff;
import static com.perunlabs.tool.jvalgen.var.gen.Api.isConst;
import static com.perunlabs.tool.jvalgen.var.spec.AllVTypes.VBOOL;
import static com.perunlabs.tool.jvalgen.var.spec.NamingSpec.CONST_CHECK_METHOD;
import static com.perunlabs.tool.jvalgen.var.spec.NamingSpec.IFF_METHOD;
import static com.perunlabs.tool.jvalgen.var.spec.NamingSpec.PRIMITIVE_SUFFIX;
import static com.perunlabs.tool.jvalgen.var.type.VComponentKind.Immutable;
import static com.perunlabs.tool.jvalgen.var.type.VComponentKind.Mutable;
import static com.perunlabs.tool.jvalgen.var.type.VComponentKind.Readable;
import static com.perunlabs.tool.jvalgen.var.type.VComponentKind.Settable;
import static com.perunlabs.tool.jvalgen.var.type.VComponents.toJField;
import static com.perunlabs.tool.jvalgen.var.type.VComponents.toJParam;
import static com.perunlabs.tool.jvalgen.var.type.VComponents.toParamlessApiCreate;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.AbstractXxxG;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.AbstractXxxS;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.AbstractXxxV;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.IffXxxG;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.MostPrimitiveG;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.XxxC;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.XxxG;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.XxxGImpl;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.XxxS;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.XxxSImpl;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.XxxV;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.XxxVImpl;
import static com.perunlabs.tool.jvalgen.var.type.VTypes.toJParam;

import java.util.Iterator;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.perunlabs.tool.jvalgen.java.gen.ClassGenerator;
import com.perunlabs.tool.jvalgen.java.gen.CodeGenerator;
import com.perunlabs.tool.jvalgen.java.gen.MethodGenerator;
import com.perunlabs.tool.jvalgen.java.type.JAccess;
import com.perunlabs.tool.jvalgen.java.type.JExpression;
import com.perunlabs.tool.jvalgen.java.type.JExpressions;
import com.perunlabs.tool.jvalgen.java.type.JField;
import com.perunlabs.tool.jvalgen.java.type.JLocalVar;
import com.perunlabs.tool.jvalgen.java.type.JModifiers;
import com.perunlabs.tool.jvalgen.java.type.JParam;
import com.perunlabs.tool.jvalgen.java.type.JStatement;
import com.perunlabs.tool.jvalgen.java.type.JType;
import com.perunlabs.tool.jvalgen.var.type.VComponent;
import com.perunlabs.tool.jvalgen.var.type.VFunction;
import com.perunlabs.tool.jvalgen.var.type.VType;
import com.perunlabs.tool.jvalgen.var.type.VTypeKind;

public class VTypeGenerator {
  private final CodeGenerator gen;
  private final ClassGenerator vApi;
  private final ImmutableList<? extends VFunction> operations;
  private final VType type;

  /*
   * helper JType variables
   */

  private final JType xxxG;
  private final JType xxxS;
  private final JType xxxV;
  private final JType xxxC;
  private final JType abstXxxG;
  private final JType abstXxxS;
  private final JType abstXxxV;
  private final JType xxxGImpl;
  private final JType xxxSImpl;
  private final JType xxxVImpl;
  private final JType mostPG;
  private final JType iffXxxG;
  private final JType boolG;
  private final JType boolC;

  /*
   * helper JParam variables
   */

  private final JParam jParamG;
  private final ImmutableList<JParam> jParamGComps;
  private final ImmutableList<JParam> jParamVComps;
  private final ImmutableList<JParam> jParamCComps;
  private final ImmutableList<JParam> jParamReadComps;
  private final ImmutableList<JParam> jParamSetComps;
  private final ImmutableList<JParam> jParamMostPGComps;

  /*
   * names
   */

  private final String xxxGName;
  private final String xxxSName;
  private final String xxxVName;
  private final String xxxCName;

  public VTypeGenerator(CodeGenerator codeGenerator, ClassGenerator apiClassGen, VType type,
      ImmutableList<? extends VFunction> operations) {
    this.gen = codeGenerator;
    this.vApi = apiClassGen;
    this.type = type;
    this.operations = operations;

    this.xxxG = type.jType(XxxG);
    this.xxxS = type.jType(XxxS);
    this.xxxV = type.jType(XxxV);
    this.xxxC = type.jType(XxxC);
    this.abstXxxG = type.jType(AbstractXxxG);
    this.abstXxxS = type.jType(AbstractXxxS);
    this.abstXxxV = type.jType(AbstractXxxV);
    this.xxxGImpl = type.jType(XxxGImpl);
    this.xxxSImpl = type.jType(XxxSImpl);
    this.xxxVImpl = type.jType(XxxVImpl);
    this.mostPG = type.jType(MostPrimitiveG);
    this.iffXxxG = type.jType(IffXxxG);
    this.boolG = VBOOL.jType(XxxG);
    this.boolC = VBOOL.jType(XxxC);

    this.jParamG = toJParam(type, XxxG);
    this.jParamGComps = toJParam(vComponents(), XxxG);
    this.jParamVComps = toJParam(vComponents(), XxxV);
    this.jParamCComps = toJParam(vComponents(), XxxC);
    this.jParamReadComps = toJParam(vComponents(), Readable);
    this.jParamSetComps = toJParam(vComponents(), Settable);
    this.jParamMostPGComps = toJParam(vComponents(), MostPrimitiveG);

    this.xxxGName = type.apiCreateName(XxxG);
    this.xxxSName = type.apiCreateName(XxxS);
    this.xxxVName = type.apiCreateName(XxxV);
    this.xxxCName = type.apiCreateName(XxxC);
  }

  public void generate() {
    // GETTERS
    createXxxG();
    createAbstractXxxG();
    createXxxGImpl();
    createXxxGApiV();

    // SETTERS
    createXxxS();
    createAbstractXxxS();
    createXxxSImpl();
    createXxxSApiV();

    // CONSTS
    createXxxC();
    createXxxCApiV();

    // VALUES
    createXxxV();
    createAbstractXxxV();
    createXxxVImpl();
    createXxxVApiV();

    // TODO add SynchronizedXxxxValue

    createIffXxxG();
    createIffXxxGApiV();

    for (VFunction operation : operations) {
      operation.generateApi(gen, vApi);
    }
  }

  private void createXxxG() {
    ClassGenerator klass = gen.newInterface(xxxG);
    for (VComponent vComp : vComponents()) {
      klass.newMethod(vComp.jType(Readable), vComp.accessor());
      if (vComp.isBasic()) {
        JType primitiveJType = vComp.vType().jType(MostPrimitiveG);
        klass.newMethod(primitiveJType, vComp.accessor() + PRIMITIVE_SUFFIX);
      }
    }
  }

  private void createAbstractXxxG() {
    ClassGenerator klass = gen.newAbstractClass(abstXxxG).addImplements(xxxG);

    for (VComponent vComp : vComponents()) {
      klass.newMethod(vComp.jType(Readable), vComp.accessor()).addOverride();
      if (vComp.isBasic()) {
        JType primitiveJType = vComp.vType().jType(MostPrimitiveG);
        String nameP = vComp.accessor() + PRIMITIVE_SUFFIX;
        MethodGenerator met = klass.newMethod(primitiveJType, nameP);
        met.add(jReturn(mostPrimitive(type.jThisComponent(AbstractXxxG, vComp))));
      }
    }

    if (type.isBasic()) {
      {
        JParam jParam = jParam(JOBJECT, "object");
        MethodGenerator met = klass.newMethod(JBOOLEAN, "equals", list(jParam)).addOverride();

        String javaTypeName = xxxG.name();
        met.addToBody("if (object instanceof " + javaTypeName + ") {");
        met.addToBody("  " + javaTypeName + " that = (" + javaTypeName + ") " + jParam.code() + ";");
        met.addToBody("  return this.get() == that.get();");
        met.addToBody("}");
        met.addToBody("return false;");
      }

      generateHashCodeMethod(klass);

      {
        MethodGenerator met = klass.newMethod(JSTRING, "toString").addOverride();
        String typeSuffix = mostPG.equals(JFLOAT) ? " + \"f\"" : "";
        met.addToBody("return \"\" + this.get()" + typeSuffix + ";");
      }
    } else {
      {
        JParam jParam = jParam(JOBJECT, "object");
        MethodGenerator met = klass.newMethod(JBOOLEAN, "equals", list(jParam)).addOverride();
        String javaTypeName = xxxG.name();
        met.addToBody("if (object instanceof " + javaTypeName + ") {");
        met.addToBody("  " + javaTypeName + " that = (" + javaTypeName + ") " + jParam.code() + ";");
        met.addToBody("  return " + componentsToEquality() + ";");
        met.addToBody("}");
        met.addToBody("return false;");
      }

      {
        MethodGenerator met = klass.newMethod(JINT, "hashCode").addOverride();
        met.addToBody("int result = 1;");
        for (VComponent vComp : vComponents()) {
          met.addToBody("result = 31 * result + " + vComp.name() + "().hashCode();");
        }
        met.addToBody("return result;");
      }

      {
        MethodGenerator met = klass.newMethod(JSTRING, "toString").addOverride();
        String name = UPPER_CAMEL.to(LOWER_CAMEL, type.name());
        met.addToBody("return \"" + name + "(\" + " + componentsToString() + " + \")\";");
      }
    }
  }

  private void createXxxGImpl() {
    if (type.isCompound()) {
      ClassGenerator klass = gen.newClass(xxxGImpl).addExtends(abstXxxG);

      MethodGenerator cons = klass.newConstructor(list(jParamG));
      cons.add(JExpressions.jCall(JVOID, "this", type.jComponentAccessOn(jParamG)));

      ImmutableList<JField> jFields = toFinal(toJField(vComponents(), Readable));
      klass.addCopyConstructor(jFields);

      for (JField jField : jFields) {
        MethodGenerator met = klass.newMethod(jField.jType(), jField.name()).addOverride();
        met.add(jReturn(jField));
      }
    }
  }

  private void createXxxGApiV() {
    if (type.isCompound()) {
      {
        // public static XxxG xxxG(FieldG f1, FieldG f2);
        MethodGenerator met = vApi.newStaticMethod(xxxG, xxxGName, jParamGComps);
        JExpression condition = JExpressions.and(isConst(jParamGComps));
        JStatement ifClause = jReturn(type.apiCreate(XxxC, constCast(jParamGComps)));
        JStatement elseClause = jReturn(type.newInstance(XxxGImpl, jParamGComps));
        met.addIf(condition, ifClause, elseClause);
      }

      {
        // public static XxxC xxxC(primitive f1, primitive f2, ...);
        if (type.hasBasicComponent()) {
          MethodGenerator met = vApi.newStaticMethod(xxxC, xxxGName, jParamMostPGComps);
          met.add(jReturn(type.apiCreate(XxxC, jParamMostPGComps)));
        }
      }
    }
  }

  private void generateHashCodeMethod(ClassGenerator klass) {
    MethodGenerator met = klass.newMethod(JINT, "hashCode").addOverride();

    JType primitiveType = type.jType(MostPrimitiveG);
    if (primitiveType.equals(JBOOLEAN)) {
      met.addToBody("return this.get() ? 1231 : 1237;");
    } else if (primitiveType.equals(JFLOAT)) {
      met.addToBody("int rawIntBits = Float.floatToRawIntBits(this.get());");
      // hashCode for -0f should be the same as for 0f
      met.addToBody("if (rawIntBits == " + Float.floatToRawIntBits(-0f) + ") {");
      met.addToBody("   return 0;");
      met.addToBody("}");
      met.addToBody("return rawIntBits;");
    } else if (primitiveType.equals(JDOUBLE)) {
      met.addToBody("long rawIntBits = Double.doubleToRawLongBits(this.get());");
      // hashCode for -0f should be the same as for 0f
      met.addToBody("if (rawIntBits == " + Double.doubleToRawLongBits(-0) + ") {");
      met.addToBody("   return 0;");
      met.addToBody("}");
      met.addToBody("return (int)(rawIntBits ^ (rawIntBits >>> 32));");
    } else if (primitiveType.equals(JType.JLONG)) {
      met.addToBody(primitiveType.name() + " value = this.get();");
      met.addToBody("return (int)(value ^ (value >>> 32));");
    } else {
      met.addToBody("return this.get();");
    }
  }

  private String componentsToEquality() {
    JLocalVar thisVar = type.thisVar(AbstractXxxG);
    JLocalVar thatVar = jLocalVar(abstXxxG, "that");

    ImmutableList<JExpression> thisComponents = type.jComponentAccessOn(thisVar);
    ImmutableList<JExpression> thatComponents = type.jComponentAccessOn(thatVar);

    checkArgument(thisComponents.size() == thatComponents.size());

    List<String> strings = newArrayList();
    Iterator<JExpression> thatIterator = thatComponents.iterator();
    for (JStatement thisStatement : thisComponents) {
      JStatement thatStatement = thatIterator.next();
      strings.add(thisStatement.code() + ".equals(" + thatStatement.code() + ")");
    }
    return Joiner.on(" && ").join(strings);
  }

  private String componentsToString() {
    List<String> strings = newArrayList();
    for (VComponent vComp : vComponents()) {
      strings.add(vComp.accessor() + "()");
    }
    return Joiner.on(" + \", \" + ").join(strings);
  }

  private void createXxxS() {
    ClassGenerator klass = gen.newInterface(xxxS);

    for (VComponent vComp : vComponents()) {
      if (!vComp.isPrimitive()) {
        JType xxxSettable = vComp.jType(Settable);
        klass.newMethod(xxxSettable, vComp.accessor());
      }
    }

    klass.newMethodPair(xxxS, "set", list(jParamG));
    klass.newMethodPair(xxxS, "set", jParamReadComps);

    if (type.hasBasicComponent()) {
      klass.newMethodPair(xxxS, "set", jParamMostPGComps);
    }
  }

  private void createAbstractXxxS() {
    ClassGenerator klass = gen.newAbstractClass(abstXxxS).addImplements(xxxS);
    generateSetMethods(klass, XxxS);
  }

  private void createXxxSImpl() {
    if (type.isCompound()) {
      ClassGenerator klass = gen.newClass(xxxSImpl).addExtends(abstXxxS);

      ImmutableList<JField> jFields = toFinal(toJField(vComponents(), Settable));
      klass.addCopyConstructor(jFields);

      for (JField jField : jFields) {
        MethodGenerator met = klass.newMethod(jField.jType(), jField.name()).addOverride();
        met.add(jReturn(jField));
      }
    }
  }

  private void createXxxSApiV() {
    if (type.isCompound()) {
      // XxxS xxxS(FieldS f1, FieldS f2, ...)
      MethodGenerator met = vApi.newStaticMethod(xxxS, xxxSName, jParamSetComps);
      met.add(jReturn(type.newInstance(XxxSImpl, jParamSetComps)));
    }
  }

  private void createXxxC() {
    ClassGenerator klass = gen.newClass(xxxC).addExtends(abstXxxG);

    klass.addCopyConstructor(toFinal(toJField(vComponents(), Immutable)));

    for (VComponent vComp : vComponents()) {
      JType immutable = vComp.jType(Immutable);
      MethodGenerator met = klass.newMethod(immutable, vComp.accessor()).addOverride();
      met.setJModifiers(JModifiers.finalJModifiers(JAccess.PUBLIC));
      met.add(jReturn(jExpr(immutable, vComp.name())));
    }
  }

  private void createXxxCApiV() {
    {
      String isConstP = CONST_CHECK_METHOD + PRIMITIVE_SUFFIX;
      MethodGenerator met = vApi.newStaticMethod(JBOOLEAN, isConstP, list(jParamG));
      met.addToBody("return v1 instanceof " + xxxC.name() + ";");
    }

    {
      // public static XxxC xxxC(XxxC v1);
      JParam jParam = jParam(xxxC, "v1");
      MethodGenerator met = vApi.newStaticMethod(xxxC, xxxCName, list(jParam));
      met.add(jReturn(jParam));
    }

    {
      // public static XxxC xxxC(XxxG v1);
      MethodGenerator met = vApi.newStaticMethod(xxxC, xxxCName, list(jParamG));

      JExpression condition = isConst(jParamG);
      JStatement ifClause = jReturn(constCast(jParamG));
      JStatement elseClause = jReturn(type.apiCreate(XxxC, type.jComponentAccessOn(jParamG)));
      met.addIf(condition, ifClause, elseClause);
    }

    if (type.isBasic()) {
      // TODO hacky workaround for bool constants
      if (mostPG.equals(JBOOLEAN)) {
        JExpression falseValue = type.newInstance(XxxC, list(jExpr(JBOOLEAN, "false")));
        vApi.addJField(jFieldConst(xxxC, "FALSE", falseValue));
        JExpression trueValue = type.newInstance(XxxC, list(jExpr(JBOOLEAN, "true")));
        vApi.addJField(jFieldConst(xxxC, "TRUE", trueValue));

        ImmutableList<JParam> jParams = list(jParam(mostPG, "param"));
        MethodGenerator met = vApi.newStaticMethod(xxxC, xxxCName, jParams);
        met.addToBody("if (param) {");
        met.addToBody("  return TRUE;");
        met.addToBody("} else {");
        met.addToBody("  return FALSE;");
        met.addToBody("}");

      } else {
        ImmutableList<JParam> jParams = list(jParam(mostPG, "param"));
        MethodGenerator met = vApi.newStaticMethod(xxxC, xxxCName, jParams);
        met.add(jReturn(type.newInstance(XxxC, jParams)));
      }
    } else {
      {
        // public static XxxC xxxC(Field1G f1, Field2G f2, ...);
        MethodGenerator met = vApi.newStaticMethod(xxxC, xxxCName, jParamReadComps);
        met.add(jReturn(type.apiCreate(XxxC, create(XxxC, jParamReadComps))));
      }

      {
        // public static XxxC xxxC(Field1C f1, Field2C f2, ...);
        MethodGenerator met = vApi.newStaticMethod(xxxC, xxxCName, jParamCComps);
        met.add(jReturn(type.newInstance(XxxC, jParamCComps)));
      }

      {
        // public static XxxC xxxC(primitive f1, primitive f2, ...);
        if (type.hasBasicComponent()) {
          MethodGenerator met = vApi.newStaticMethod(xxxC, xxxCName, jParamMostPGComps);
          met.add(jReturn(type.apiCreate(XxxC, create(XxxC, jParamMostPGComps))));
        }
      }
    }
  }

  private void createXxxV() {
    ClassGenerator klass = gen.newInterface(xxxV).addExtends(xxxG).addExtends(xxxS);

    for (VComponent vComp : vComponents()) {
      if (!vComp.isPrimitive()) {
        klass.newMethod(vComp.jType(Mutable), vComp.accessor());
      }
    }

    klass.newMethodPairWithOverride(xxxV, "set", list(jParamG));
    klass.newMethodPairWithOverride(xxxV, "set", jParamReadComps);

    if (type.hasBasicComponent()) {
      klass.newMethodPairWithOverride(xxxV, "set", jParamMostPGComps);
    }

    for (VFunction operation : operations) {
      operation.createXxxVMethods(klass);
    }
  }

  private void createAbstractXxxV() {
    ClassGenerator klass = gen.newAbstractClass(abstXxxV).addExtends(abstXxxG).addImplements(xxxV);

    for (VComponent vComp : vComponents()) {
      if (!vComp.isPrimitive()) {
        klass.newMethod(vComp.jType(Mutable), vComp.accessor()).addOverride();
      }
    }

    generateSetMethods(klass, XxxV);

    for (VFunction operation : operations) {
      operation.createAbstractXxxVMethods(klass);
    }
  }

  private void generateSetMethods(ClassGenerator klass, VTypeKind kind) {
    JType retType = type.jType(kind);
    {
      // retType set(XxxG that)
      MethodGenerator met = klass.newMethodPairWithImpl(retType, "set", list(jParamG));
      met.add(jReturn(jCall(retType, "set", type.jComponentAccessOn(jParamG))));
    }

    {
      // retType set(Field1G f1, Field2G f2, ..)
      MethodGenerator met = klass.newMethodPairWithImpl(retType, "set", jParamReadComps);

      if (type.hasBasicComponent()) {
        met.add(jReturn(chainedSetCall(kind, mostPrimitive(jParamReadComps))));
      } else if (type.isCompound()) {
        for (int i = 0; i < vComponents().size(); i++) {
          VComponent vComp = vComponents().get(i);
          JParam jParam = jParamReadComps.get(i);
          met.add(jSet(type.jThisComponent(XxxS, vComp), jParam));
        }
        met.addToBody("return this;");
      }
    }

    if (type.hasBasicComponent()) {
      // retType set(CompoundG c1, ..., primitive1 f1, primitive2 f2, ..)
      MethodGenerator met = klass.newMethodPairWithImpl(retType, "set", jParamMostPGComps);
      for (int i = 0; i < vComponents().size(); i++) {
        VComponent vComp = vComponents().get(i);
        JParam jParam = jParamMostPGComps.get(i);
        met.add(jSet(type.jThisComponent(XxxS, vComp), jParam));
      }
      met.addToBody("return this;");
    }
  }

  private void createXxxVImpl() {
    ClassGenerator klass = gen.newClass(xxxVImpl).addExtends(abstXxxV);

    if (type.isBasic()) {
      JField jField = jField(mostPG, "value");
      klass.addCopyConstructor(list(jField));

      {
        MethodGenerator met = klass.newConstructor();
        met.addToBody("// default constructor");
      }

      {
        JParam jParam = jParam(jField.jType(), jField.name());
        MethodGenerator met = klass.newMethod(xxxV, "set", list(jParam)).addOverride();
        met.add(jAssign(jField, jParam));
        met.addToBody("return this;");
      }

      {
        MethodGenerator met = klass.newMethod(mostPG, "get").addOverride();
        met.add(jReturn(jField));
      }
    } else {
      ImmutableList<JField> jFields = toFinal(toJField(vComponents(), Mutable));
      klass.addCopyConstructor(jFields);

      for (JField jField : jFields) {
        MethodGenerator met = klass.newMethod(jField.jType(), jField.name()).addOverride();
        met.add(jReturn(jField));
      }
    }
  }

  private void createXxxVApiV() {
    if (type.isBasic()) {
      MethodGenerator met = vApi.newStaticMethod(xxxV, xxxVName);
      met.addToBody("return new " + xxxVImpl.name() + "();");
    } else {
      {
        MethodGenerator met = vApi.newStaticMethod(xxxV, xxxVName);
        met.add(jReturn(type.apiCreate(XxxV, toParamlessApiCreate(vComponents(), XxxV))));
      }

      {
        MethodGenerator met = vApi.newStaticMethod(xxxV, xxxVName, jParamVComps);
        met.add(jReturn(type.newInstance(XxxVImpl, jParamVComps)));
      }
    }

    {
      MethodGenerator met = vApi.newStaticMethod(xxxV, xxxVName, list(jParamG));
      met.add(jReturn(type.apiCreate(XxxV, type.jComponentAccessOn(jParamG))));
    }

    {
      MethodGenerator met = vApi.newStaticMethod(xxxV, xxxVName, jParamReadComps);
      ImmutableList<? extends JExpression> args = type.isBasic() ? jParamReadComps : create(XxxV,
          jParamReadComps);
      met.add(jReturn(type.newInstance(XxxVImpl, args)));
    }

    if (type.hasBasicComponent()) {
      MethodGenerator met = vApi.newStaticMethod(xxxV, xxxVName, jParamMostPGComps);
      met.add(jReturn(type.newInstance(XxxVImpl, create(XxxV, jParamMostPGComps))));
    }
  }

  private void createIffXxxGApiV() {
    JParam jParam2 = jParam(xxxG, "v2");

    {
      JParam cond = jParam(boolG, "condition");
      ImmutableList<JParam> jParams = list(cond, jParamG, jParam2);
      MethodGenerator met = vApi.newStaticMethod(xxxG, IFF_METHOD, jParams);

      JExpression condition = isConst(cond);
      JStatement ifClause = jReturn(iff(constCast(cond), jParamG, jParam2));
      JStatement elseClause = jReturn(type.newInstance(IffXxxG, jParams));
      met.addIf(condition, ifClause, elseClause);
    }

    {
      JParam constCond = jParam(boolC, "condition");
      ImmutableList<JParam> jParams = list(constCond, jParamG, jParam2);
      MethodGenerator met = vApi.newStaticMethod(xxxG, IFF_METHOD, jParams);
      met.addIf(mostPrimitive(constCond), jReturn(jParamG), jReturn(jParam2));
    }
  }

  private void createIffXxxG() {
    if (type.isBasic()) {
      ClassGenerator klass = gen.newClass(iffXxxG).addExtends(abstXxxG);

      JField condField = jFieldFinal(boolG, "condition");
      JField jField1 = jFieldFinal(xxxG, "v1");
      JField jField2 = jFieldFinal(xxxG, "v2");
      klass.addCopyConstructor(list(condField, jField1, jField2));

      {
        MethodGenerator met = klass.newMethod(mostPG, "get").addOverride();
        JExpression condition = getInvokedOn(condField);
        JStatement ifClause = jReturn(getInvokedOn(jField1));
        JStatement elseClause = jReturn(getInvokedOn(jField2));
        met.addIf(condition, ifClause, elseClause);
      }
    } else {
      ClassGenerator klass = gen.newClass(iffXxxG).addExtends(xxxGImpl);

      JParam condition = jParamFinal(boolG, "condition");
      JParam jParam1 = jParamFinal(xxxG, "v1");
      JParam jParam2 = jParamFinal(xxxG, "v2");

      MethodGenerator cons = klass.newConstructor(list(condition, jParam1, jParam2));

      ImmutableList<JExpression> jParam1Fields = type.jComponentAccessOn(jParam1);
      ImmutableList<JExpression> jParam2Fields = type.jComponentAccessOn(jParam2);
      List<JStatement> args = Lists.newArrayList();

      for (int i = 0; i < vComponents().size(); i++) {
        JExpression p1 = jParam1Fields.get(i);
        JExpression p2 = jParam2Fields.get(i);
        args.add(iff(condition, p1, p2));
      }

      cons.add(JExpressions.jCall(JVOID, "super", args));
    }
  }

  public JExpression chainedSetCall(VTypeKind kind, ImmutableList<? extends JStatement> args) {
    return jCall(type.jType(kind), "set", args);
  }

  private ImmutableList<VComponent> vComponents() {
    return type.vComponents();
  }
}
