package com.perunlabs.tool.jvalgen.java.type;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.perunlabs.tool.jvalgen.java.type.JTypes.toVType;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.XxxG;
import static com.perunlabs.tool.jvalgen.var.type.VTypeKind.XxxS;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.perunlabs.common.jval.oper.Check;
import com.perunlabs.tool.jvalgen.var.type.VType;

public class JType {
  public static final JType JVOID = jType(Void.TYPE);
  public static final JType JOBJECT = jType(Object.class);
  public static final JType JSTRING = jType(String.class);
  public static final JType JRUNNABLE = jType(Runnable.class);

  public static final JType JBOOLEAN = jType(Boolean.TYPE);
  public static final JType JINT = jType(Integer.TYPE);
  public static final JType JLONG = jType(Long.TYPE);
  public static final JType JFLOAT = jType(Float.TYPE);
  public static final JType JDOUBLE = jType(Double.TYPE);

  public static final JType JCHECK = jType(Check.class);

  private final String packageDotted;
  private final String name;
  private final ImmutableList<JType> superTypes;

  public static JType jType(Class<?> klass) {
    if (klass.isPrimitive()) {
      return new JType(null, klass.toString(), JTypes.empty());
    } else {
      return new JType(klass.getPackage().getName(), klass.getSimpleName(), JTypes.empty());
    }
  }

  public static JType jType(String packageDotted, String name, ImmutableList<JType> superTypes) {
    return new JType(packageDotted, name, superTypes);
  }

  private JType(String packageDotted, String name, ImmutableList<JType> superTypes) {
    this.packageDotted = packageDotted;
    this.name = checkNotNull(name);
    this.superTypes = superTypes;
  }

  public String name() {
    return name;
  }

  public String fullName() {
    return packageDotted + "." + name;
  }

  public boolean isPrimitive() {
    return packageDotted == null;
  }

  public String packageDotted() {
    return packageDotted;
  }

  public String packageSlashed() {
    return packageDotted.replace('.', '/') + "/";
  }

  public boolean requiresImport() {
    return packageDotted != null && !packageDotted.equals("java.lang");
  }

  public boolean isSubTypeOf(JType type) {
    if (this.equals(type)) {
      return true;
    }
    for (JType superType : superTypes) {
      if (superType.isSubTypeOf(type)) {
        return true;
      }
    }
    return false;
  }

  public void assertThatCanBeSetFrom(JType jType) {
    VType desinationVType = toVType(this);
    VType sourceVType = toVType(jType);

    checkArgument(desinationVType.equals(sourceVType));
    checkArgument(this.isSubTypeOf(desinationVType.jType(XxxS)));
    if (!jType.isPrimitive()) {
      checkArgument(jType.isSubTypeOf(sourceVType.jType(XxxG)));
    }
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof JType) {
      JType that = (JType) object;
      return Objects.equal(this.packageDotted, that.packageDotted) && this.name.equals(that.name);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(packageDotted, name);
  }

  @Override
  public String toString() {
    return packageDotted + "." + name;
  }
}
