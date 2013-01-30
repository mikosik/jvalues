package com.perunlabs.tool.jvalgen.java.gen;

import static com.perunlabs.tool.jvalgen.java.gen.ClassGenerator.newAbstractClassGenerator;
import static com.perunlabs.tool.jvalgen.java.gen.ClassGenerator.newClassGenerator;
import static com.perunlabs.tool.jvalgen.java.gen.ClassGenerator.newInterfaceGenerator;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.perunlabs.tool.jvalgen.java.type.JType;

public class CodeGenerator {
  private final Set<JType> createdClasses;
  private final List<ClassGenerator> generators;

  public CodeGenerator() {
    this.createdClasses = Sets.newHashSet();
    this.generators = Lists.newArrayList();
  }

  public ClassGenerator newInterface(JType jType) {
    return addAndReturn(newInterfaceGenerator(jType));
  }

  public ClassGenerator newClass(JType jType) {
    return addAndReturn(newClassGenerator(jType));
  }

  public ClassGenerator newAbstractClass(JType jType) {
    return addAndReturn(newAbstractClassGenerator(jType));
  }

  private ClassGenerator addAndReturn(ClassGenerator classGenerator) {
    if (createdClasses.add(classGenerator.jType())) {
      generators.add(classGenerator);
    } else {
      throw new IllegalStateException("Cannot add the same JType to CodeGenerator twice: "
          + classGenerator.jType());
    }
    return classGenerator;
  }

  public void generate(String sourceDir) throws IOException {
    for (ClassGenerator generator : generators) {
      generator.generate(sourceDir);
    }
  }
}
