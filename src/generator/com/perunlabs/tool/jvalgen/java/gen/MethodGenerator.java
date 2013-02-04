package com.perunlabs.tool.jvalgen.java.gen;

import static com.google.common.base.Preconditions.checkArgument;
import static com.perunlabs.tool.jvalgen.java.gen.IndentingWriter.INDENT;
import static com.perunlabs.tool.jvalgen.java.gen.IndentingWriter.INDENT3;
import static com.perunlabs.tool.jvalgen.java.type.JModifiers.jModifiers;
import static com.perunlabs.tool.jvalgen.java.type.JParams.toDeclaration;

import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.perunlabs.tool.jvalgen.java.type.JAccess;
import com.perunlabs.tool.jvalgen.java.type.JBlock;
import com.perunlabs.tool.jvalgen.java.type.JDeclarable;
import com.perunlabs.tool.jvalgen.java.type.JExpression;
import com.perunlabs.tool.jvalgen.java.type.JModifiers;
import com.perunlabs.tool.jvalgen.java.type.JParam;
import com.perunlabs.tool.jvalgen.java.type.JReturn;
import com.perunlabs.tool.jvalgen.java.type.JStatement;
import com.perunlabs.tool.jvalgen.java.type.JType;

public class MethodGenerator {
  private JModifiers jModifiers;
  private final JType returnJType;
  private final String name;
  private final ImmutableList<JParam> jParams;

  private final ClassGenerator classGenerator;

  private final boolean isStatic;
  private boolean hasOverrideAnnotation = false;
  private final List<String> bodyLines = Lists.newArrayList();
  private boolean isBodyWrappedInsideRunnable = false;

  public MethodGenerator(JType returnJType, String name, ImmutableList<JParam> jParams,
      ClassGenerator classGenerator, boolean isStatic) {
    this(jModifiers(JAccess.PUBLIC), returnJType, name, jParams, classGenerator, isStatic);
  }

  public MethodGenerator(JModifiers jModifiers, JType returnJType, String name,
      ImmutableList<JParam> jParams, ClassGenerator classGenerator, boolean isStatic) {
    this.jModifiers = jModifiers;
    this.returnJType = returnJType;
    this.name = name;
    this.jParams = jParams;

    this.classGenerator = classGenerator;
    this.isStatic = isStatic;

    if (returnJType != null) {
      classGenerator.addImport(returnJType);
    }
    for (JParam jParam : jParams) {
      addImportsFor(jParam);
    }
  }

  public void setJModifiers(JModifiers jModifiers) {
    this.jModifiers = jModifiers;
  }

  private void addImportsFor(JDeclarable jDecl) {
    classGenerator.addImports(jDecl.importsNeeded());
  }

  public MethodGenerator addOverride() {
    hasOverrideAnnotation = true;
    return this;
  }

  public void addToBody(String line) {
    if (classGenerator.isInterface()) {
      throw new IllegalArgumentException("Cannot set body for method in interface");
    }
    bodyLines.add(line);
  }

  public void addEmptyLine() {
    addToBody("");
  }

  public void addDeclaration(JDeclarable jDecl) {
    addImportsFor(jDecl);
    addToBody(jDecl.declaration());
  }

  public void addIf(JExpression condition, JBlock ifJBlock, JBlock elseJBlock) {
    classGenerator.addImports(condition.importsNeeded());

    addToBody("if (" + condition.code() + ") {");
    add(INDENT, ifJBlock);
    addToBody("} else {");
    add(INDENT, elseJBlock);
    addToBody("}");
  }

  public void add(JReturn jReturn) {
    assertCorrectReturnStatement(jReturn);
    add((JStatement) jReturn);
  }

  private void assertCorrectReturnStatement(JReturn jReturn) {
    JType actualReturn = jReturn.returnedJType();
    checkArgument(actualReturn.isSubTypeOf(returnJType), "Expected subtype of " + returnJType
        + " got " + actualReturn);
  }

  public void add(JBlock jBlock) {
    add("", jBlock);
  }

  private void add(String indent, JBlock jBlock) {
    for (JStatement jStatement : jBlock.jStatements()) {
      classGenerator.addImports(jStatement.importsNeeded());
      addToBody(indent + jStatement.code() + ";");
    }
  }

  public void add(JStatement jStat) {
    classGenerator.addImports(jStat.importsNeeded());
    addToBody(jStat.code() + ";");
  }

  public void wrapBodyInsideRunnable() {
    isBodyWrappedInsideRunnable = true;
  }

  public void generate(IndentingWriter writer) {
    failIfAbstractMethodInNonAbstractClass();

    if (hasOverrideAnnotation) {
      writer.println("@Override");
    }
    if (bodyLines.isEmpty()) {
      writer.println(signature() + ";");
    } else {
      writer.println(signature() + " {");

      if (isBodyWrappedInsideRunnable) {
        writer.println("  return new Runnable() {");
        writer.println("    @Override");
        writer.println("    public void run() {");
      }

      String indent = isBodyWrappedInsideRunnable ? INDENT3 : INDENT;
      for (String line : bodyLines) {
        writer.println(indent + line);
      }

      if (isBodyWrappedInsideRunnable) {
        writer.println("    }");
        writer.println("  };");
      }
      writer.println("}");
    }
  }

  private void failIfAbstractMethodInNonAbstractClass() {
    if (!classGenerator.isAbstractMethodAllowed() && bodyLines.isEmpty()) {
      throw new IllegalStateException("Method " + name
          + "(...) have no body in non abstract class: " + classGenerator);
    }
  }

  private String signature() {
    String returnJTypeString = returnJType == null ? "" : returnJType.name() + " ";
    return modifiers() + returnJTypeString + name + "(" + jParamsString() + ")";
  }

  private String jParamsString() {
    return Joiner.on(", ").join(toDeclaration(jParams));
  }

  private String modifiers() {
    String result = jModifiers.code();
    if (isStatic) {
      result += "static ";
    }
    if (bodyLines.isEmpty() && !classGenerator.isInterface()) {
      result += "abstract ";
    }
    return result;
  }
}
