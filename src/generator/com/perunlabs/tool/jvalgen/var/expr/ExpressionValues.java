package com.perunlabs.tool.jvalgen.var.expr;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.perunlabs.tool.jvalgen.java.type.JStatement;

public class ExpressionValues {
  private final Map<VExpression, VStatement> values;
  private final List<JStatement> extractedJStatements = newArrayList();
  private final List<JStatement> assignmentJStatements = newArrayList();

  public ExpressionValues(Map<VExpression, VStatement> vParamToVStatementMap) {
    values = vParamToVStatementMap;
  }

  public boolean hasValueFor(VExpression expression) {
    return values.containsKey(expression);
  }

  public VStatement valueFor(VExpression expression) {
    VStatement vStatement = values.get(expression);
    checkState(vStatement != null, "No Value stored for expression " + expression);
    return vStatement;
  }

  public void addVStatement(VExpression expression, VStatement vStatement) {
    values.put(expression, vStatement);
  }

  public void addExtractedJStatement(JStatement jStatement) {
    extractedJStatements.add(jStatement);
  }

  public void addAssignmentJStatement(JStatement jStatement) {
    assignmentJStatements.add(jStatement);
  }

  public ImmutableList<JStatement> allJStatements() {
    ImmutableList.Builder<JStatement> builder = ImmutableList.builder();
    builder.addAll(extractedJStatements);
    builder.addAll(assignmentJStatements);
    return builder.build();
  }
}
