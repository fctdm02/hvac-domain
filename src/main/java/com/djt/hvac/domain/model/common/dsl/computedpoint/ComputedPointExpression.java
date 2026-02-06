package com.djt.hvac.domain.model.common.dsl.computedpoint;

import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import static java.util.Objects.requireNonNull;

public class ComputedPointExpression<T> implements Expression<T> {

  private final Expression<T> delegate;

  public static <T> ComputedPointExpression<T> parse(String expression) {
    Parser parser = Parser.create();
    Expression<T> expr = parser.parse(expression);
    return new ComputedPointExpression<T>(expr);
  }

  private ComputedPointExpression(Expression<T> delegate) {
    this.delegate = requireNonNull(delegate, "delegate cannot be null");
  }


  @Override
  public T evaluate(Inputs inputs) {
    validateInputs(inputs);
    return delegate.evaluate(inputs);
  }

  @Override
  public Class<T> getType() {
    return delegate.getType();
  }

  @Override
  public void accept(ExpressionVisitor visitor) {
    delegate.accept(visitor);
  }

  public Inputs.Builder inputsBuilder() {
    Set<Variable<?>> vars = getVariables();
    return Inputs.builder(vars);
  }

  public Set<Variable<?>> getVariables() {
    GetVariablesVisitor visitor = new GetVariablesVisitor();
    delegate.accept(visitor);
    return ImmutableSet.copyOf(visitor.variables);
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

  private static final class GetVariablesVisitor implements ExpressionVisitor {
    Set<Variable<?>> variables = Sets.newLinkedHashSet();

    @Override
    public void visit(Expression<?> expr) {
      if (Variable.class.isInstance(expr)) {
        variables.add(Variable.class.cast(expr));
      }
    }
  }

}
