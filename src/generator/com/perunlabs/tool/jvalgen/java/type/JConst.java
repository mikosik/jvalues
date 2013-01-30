package com.perunlabs.tool.jvalgen.java.type;

import static com.google.common.base.Preconditions.checkArgument;
import static com.perunlabs.tool.jvalgen.Utils.list;
import static java.lang.reflect.Modifier.isPublic;
import static java.lang.reflect.Modifier.isStatic;

import java.lang.reflect.Field;

public class JConst extends JExpression {

  public static JConst jConst(Class<?> klass, String fieldName) {
    Field field = getField(klass, fieldName);
    checkArgument(isStatic(field.getModifiers()));
    checkArgument(isPublic(field.getModifiers()));
    JType owner = JType.jType(klass);
    return new JConst(owner, fieldName, JType.jType(field.getType()));
  }

  private static Field getField(Class<?> klass, String name) {
    try {
      return klass.getDeclaredField(name);
    } catch (SecurityException e) {
      throw new RuntimeException(e);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }

  public JConst(JType owner, String name, JType jType) {
    super(jType, owner.name() + "." + name, list(owner));
  }
}
