package com.perunlabs.tool.jvalgen.java.type;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.perunlabs.tool.jvalgen.Utils.list;
import static com.perunlabs.tool.jvalgen.java.type.JExpressions.convertedExpr;
import static com.perunlabs.tool.jvalgen.java.type.JParams.toJParam;
import static com.perunlabs.tool.jvalgen.java.type.JParams.toJType;
import static com.perunlabs.tool.jvalgen.java.type.JType.jType;

import java.lang.reflect.Method;

import com.google.common.collect.ImmutableList;

/**
 * Java static method.
 */
public class JFunction implements JCallable {
  private final JType owner;
  private final String name;
  private final JType returnJType;
  private final ImmutableList<JParam> jParams;

  public static JFunction jFunction(Class<?> klass, String methodName) {
    Method method = methodWithName(klass, methodName);

    JType owner = jType(klass);
    JType returnJType = jType(method.getReturnType());
    ImmutableList<JParam> jParams = toJParam(method);

    return jFunction(owner, returnJType, methodName, jParams);
  }

  private static Method methodWithName(Class<?> klass, String methodName) {
    Method result = null;
    for (Method method : klass.getDeclaredMethods()) {
      if (methodName.equals(method.getName())) {
        if (result != null) {
          throw new RuntimeException("Method " + methodName + " is overloaded in class " + klass
              + " so can't know which one choose.");
        }
        result = method;
      }
    }
    if (result == null) {
      throw new RuntimeException("Couldn't find method " + methodName + " in class " + klass);
    }
    return result;
  }

  private static JFunction jFunction(JType owner, JType returnJType, String name,
      ImmutableList<JParam> jParams) {
    return new JFunction(owner, returnJType, name, jParams);
  }

  private JFunction(JType owner, JType returnJType, String name, ImmutableList<JParam> jParams) {
    this.owner = checkNotNull(owner);
    this.name = checkNotNull(name);
    this.returnJType = checkNotNull(returnJType);
    this.jParams = checkNotNull(jParams);
  }

  public String name() {
    return name;
  }

  public JType returnJType() {
    return returnJType;
  }

  public ImmutableList<JParam> jParams() {
    return jParams;
  }

  @Override
  public JExpression jCall(Iterable<? extends JExpression> arguments) {
    ImmutableList<JExpression> args = convertedExpr(arguments, toJType(jParams));
    return JExpressions.jCall(returnJType, fullName(), args, list(owner));
  }

  private String fullName() {
    return owner.name() + "." + name;
  }
}
