package com.djt.hvac.domain.model.common.dsl.baseline;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import static java.util.Objects.requireNonNull;

public class BaselineExpression {

  private final Expression<Double> expr;

  public static BaselineExpression parse(String expression) {
    Parser parser = Parser.create();
    Expression<Double> expr = parser.parse(expression);
    return new BaselineExpression(expr);
  }

  public static BaselineExpression parse(Reader in) throws IOException {
    Parser parser = Parser.create();
    Expression<Double> expr = parser.parse(in);
    return new BaselineExpression(expr);
  }


  private BaselineExpression(Expression<Double> expr) {
    this.expr = requireNonNull(expr, "expr cannot be null");
  }

  public Set<Variable<?>> getVariables() {
    GetVariablesVisitor visitor = new GetVariablesVisitor();
    expr.accept(visitor);
    return ImmutableSet.copyOf(visitor.variable);
  }

  public double evaluate(Inputs inputs) {
    validateInputs(inputs);

    return expr.evaluate(inputs);
  }

  Expression<Double> getExpr() {
    return expr;
  }

  private void validateInputs(Inputs inputs) {
    Set<Variable<?>> expectedVariables = getVariables();
    Set<Variable<?>> variables = inputs.getVariables();
    if (variables.size() != expectedVariables.size()) {
      List<Variable<?>> missing = Lists.newArrayList();
      for (Variable<?> id : expectedVariables) {
        if (!variables.contains(id)) {
          missing.add(id);
        }
      }
      if (!missing.isEmpty()) {
        throw new IllegalArgumentException(
            "Values are missing for all of the following variables: " + missing);
      }
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((expr == null) ? 0 : expr.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    BaselineExpression other = (BaselineExpression) obj;
    if (expr == null) {
      if (other.expr != null)
        return false;
    } else if (!expr.equals(other.expr))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return expr.toString();
  }

  private static final class GetVariablesVisitor implements ExpressionVisitor {
    Set<Variable<?>> variable = Sets.newLinkedHashSet();

    @Override
    public void visit(Expression<?> expr) {
      if (Variable.class.isInstance(expr)) {
        variable.add(Variable.class.cast(expr));
      }
    }
  }
}
