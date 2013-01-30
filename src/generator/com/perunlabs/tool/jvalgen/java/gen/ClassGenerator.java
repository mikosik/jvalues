package com.perunlabs.tool.jvalgen.java.gen;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.perunlabs.tool.jvalgen.java.type.JFields.toJParam;
import static com.perunlabs.tool.jvalgen.java.type.JParams.toFinal;
import static com.perunlabs.tool.jvalgen.java.type.JStatements.jAssign;
import static com.perunlabs.tool.jvalgen.java.type.JType.JRUNNABLE;
import static com.perunlabs.tool.jvalgen.java.type.JTypes.toFullNameF;
import static com.perunlabs.tool.jvalgen.var.spec.NamingSpec.RUNNABLE_SUFFIX;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.perunlabs.tool.jvalgen.Utils;
import com.perunlabs.tool.jvalgen.java.type.JAccess;
import com.perunlabs.tool.jvalgen.java.type.JExpressions;
import com.perunlabs.tool.jvalgen.java.type.JField;
import com.perunlabs.tool.jvalgen.java.type.JParam;
import com.perunlabs.tool.jvalgen.java.type.JParams;
import com.perunlabs.tool.jvalgen.java.type.JType;

public class ClassGenerator {
  private final JType jType;
  private final boolean isInterface;
  private final boolean isAbstractClass;

  private final Set<JType> typesToImport = Sets.newHashSet();
  private final List<String> extendList = Lists.newArrayList();
  private final List<String> implementList = Lists.newArrayList();
  private final List<JField> jFields = Lists.newArrayList();
  private final List<MethodGenerator> methodGenerators = Lists.newArrayList();

  public static ClassGenerator newInterfaceGenerator(JType jType) {
    return new ClassGenerator(jType, true, false);
  }

  public static ClassGenerator newClassGenerator(JType jType) {
    return new ClassGenerator(jType, false, false);
  }

  public static ClassGenerator newAbstractClassGenerator(JType jType) {
    return new ClassGenerator(jType, false, true);
  }

  private ClassGenerator(JType jType, boolean isInterface, boolean isAbstractClass) {
    this.jType = jType;
    this.isInterface = isInterface;
    this.isAbstractClass = isAbstractClass;
  }

  public JType jType() {
    return jType;
  }

  public boolean isInterface() {
    return isInterface;
  }

  public boolean isAbstractClass() {
    return isAbstractClass;
  }

  public boolean isAbstractMethodAllowed() {
    return isInterface || isAbstractClass;
  }

  public void addImports(ImmutableList<JType> jTypes) {
    for (JType typeToImport : jTypes) {
      addImport(typeToImport);
    }
  }

  public void addImport(JType jTypeToImport) {
    if (jTypeToImport.requiresImport()) {
      typesToImport.add(jTypeToImport);
    }
  }

  public ClassGenerator addExtends(JType superJType) {
    checkState(isInterface || extendList.size() == 0, "Class cannot have more than one supreclass");
    typesToImport.add(superJType);
    extendList.add(superJType.name());
    return this;
  }

  public ClassGenerator addImplements(JType interfaceJType) {
    implementList.add(interfaceJType.name());
    return this;
  }

  // fields / methods

  public void addJFields(Iterable<JField> jFieldsToAdd) {
    for (JField jField : jFieldsToAdd) {
      addJField(jField);
    }
  }

  public void addJField(JField jField) {
    jFields.add(checkNotNull(jField));
  }

  public MethodGenerator newConstructor() {
    return newConstructor(JParams.empty());
  }

  public MethodGenerator newConstructor(ImmutableList<JParam> jParams) {
    return newConstructor(JAccess.PUBLIC, jParams);
  }

  public MethodGenerator newConstructor(JAccess jAccess, ImmutableList<JParam> jParams) {
    MethodGenerator methodGenerator = new MethodGenerator(jAccess, null, jType.name(), jParams,
        this, false);
    methodGenerators.add(methodGenerator);
    return methodGenerator;
  }

  public MethodGenerator newMethodPairWithImpl(JType returnJType, String name,
      ImmutableList<JParam> jParams) {
    MethodGenerator metR = newMethod(JRUNNABLE, name, toFinal(jParams));
    metR.addOverride();
    metR.wrapBodyInsideRunnable();
    metR.add(JExpressions.jCall(returnJType, name, jParams));

    MethodGenerator met = newMethod(returnJType, name, jParams);
    met.addOverride();
    return met;
  }

  public MethodGenerator newMethodPairWithOverride(JType returnJType, String name,
      ImmutableList<JParam> jParams) {
    MethodGenerator metR = newMethod(JRUNNABLE, name, jParams);
    metR.addOverride();

    MethodGenerator met = newMethod(returnJType, name, jParams);
    met.addOverride();
    return met;
  }

  public MethodGenerator newMethodPair(JType returnJType, String name, ImmutableList<JParam> jParams) {
    newMethod(JRUNNABLE, name, jParams);
    return newMethod(returnJType, name, jParams);
  }

  public MethodGenerator newMethod(JType returnJType, String name) {
    return newMethod(returnJType, name, Utils.<JParam> list());
  }

  public MethodGenerator newMethod(JType returnJType, String name, ImmutableList<JParam> jParams) {
    String methodName = returnJType.equals(JRUNNABLE) ? name + RUNNABLE_SUFFIX : name;
    MethodGenerator met = new MethodGenerator(returnJType, methodName, jParams, this, false);
    methodGenerators.add(met);
    return met;
  }

  public MethodGenerator newStaticMethod(JType returnJType, String methodName) {
    return newStaticMethod(returnJType, methodName, Utils.<JParam> list());
  }

  public MethodGenerator newStaticMethod(JType returnJType, String methodName,
      ImmutableList<JParam> jParams) {
    MethodGenerator met = new MethodGenerator(returnJType, methodName, jParams, this, true);
    methodGenerators.add(met);
    return met;
  }

  public void addCopyConstructor(ImmutableList<JField> jFieldsToAdd) {
    addCopyConstructor(JAccess.PUBLIC, jFieldsToAdd);
  }

  public void addCopyConstructor(JAccess jAccess, ImmutableList<JField> jFieldsToAdd) {
    addJFields(jFieldsToAdd);

    ImmutableList<JParam> jParams = toJParam(jFieldsToAdd);
    MethodGenerator constructor = newConstructor(jAccess, jParams);

    for (int i = 0; i < jFieldsToAdd.size(); i++) {
      JField jField = jFieldsToAdd.get(i);
      JParam jParam = jParams.get(i);

      if (jField.jType().isPrimitive()) {
        constructor.add(jAssign(jField, jParam));
      } else {
        constructor.add(jAssign(jField, JExpressions.notNull(jParam)));
      }
    }
  }

  public void generate(String sourceDir) throws FileNotFoundException {
    createPackageDir(sourceDir);
    PrintWriter writer = openWriter(sourceDir);
    writer.println("package " + jType.packageDotted() + ";");
    writer.println();

    List<JType> imports = Ordering.natural().onResultOf(toFullNameF()).sortedCopy(typesToImport);
    for (JType toImport : imports) {
      writer.println("import " + toImport + ";");
    }
    writer.println();

    writer.println("/**");
    writer.println(" * AUTOGENERATED CLASS. DO NOT MODIFY.");
    writer.println(" */");

    writer.println(modifiers() + " " + interfaceOrClassString() + " " + jType.name()
        + extendImplementClause() + " {");

    for (JField jField : jFields) {
      new IndentingWriter(writer).println(jField.declaration());
    }

    writer.println();

    for (MethodGenerator generator : methodGenerators) {
      generator.generate(new IndentingWriter(writer));
      writer.println();
    }

    writer.println("}");
    writer.close();
  }

  private PrintWriter openWriter(String sourceDir) throws FileNotFoundException {
    String filePath = packageDir(sourceDir) + jType.name() + ".java";
    return new PrintWriter(new FileOutputStream(filePath));
  }

  private void createPackageDir(String sourceDir) {
    new File(packageDir(sourceDir)).mkdirs();
  }

  private String packageDir(String sourceDir) {
    return sourceDir + "/" + jType.packageSlashed();
  }

  private String extendImplementClause() {
    String extendClause = superclassList("extends", extendList);
    String implementClause = superclassList("implements", implementList);

    return space(extendClause + space(implementClause));
  }

  private String superclassList(String keyword, List<String> list) {
    if (list.isEmpty()) {
      return "";
    }
    return keyword + " " + Joiner.on(", ").join(list);
  }

  private String modifiers() {
    return "public" + (isAbstractClass ? " abstract" : "");
  }

  private String interfaceOrClassString() {
    return isInterface ? "interface" : "class";
  }

  private static String space(String string) {
    if (0 < string.length() && string.charAt(0) != ' ') {
      return " " + string;
    } else {
      return string;
    }
  }
}
