package com.djt.hvac.domain.model.common.dsl.baseline;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;

import static java.util.Objects.requireNonNull;

import static com.google.common.base.Preconditions.checkArgument;

public class Variable<T> extends AbstractExpression<T> {

  private final VariableId<T> id;
  private final List<?> args;
  private final String varString;

  static <T> Variable<T> create(VariableId<T> id) {
    return new Variable<>(id);
  }

  static <T> Variable<T> create(VariableId<T> id, List<?> args) {
    return new Variable<>(id, args);
  }

  private Variable(VariableId<T> id) {
    this.id = id;
    this.args = ImmutableList.of();
    this.varString = asString();

  }

  private Variable(VariableId<T> id, List<?> args) {
    this.id = id;
    List<?> adjustedArgs = validateArgs(id, args);
    this.args = ImmutableList.copyOf(adjustedArgs);
    this.varString = asString();
  }

  @Override
  public T evaluate(Inputs inputs) {
    requireNonNull(inputs, "inputs cannot be null");
    return inputs.getValue(this);
  }

  @Override
  public Class<T> getType() {
    return id.getType();
  }

  public VariableId<T> getId() {
    return id;
  }

  public List<?> getArguments() {
    return args;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((args == null) ? 0 : args.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
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
    Variable<?> other = (Variable<?>) obj;
    if (args == null) {
      if (other.args != null)
        return false;
    } else if (!args.equals(other.args))
      return false;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return varString;
  }

  private List<?> validateArgs(VariableId<T> id, List<?> args) {
    List<Class<? extends Literal<?>>> params = id.getParameterTypes();
    List<Object> adjustedArgs = Lists.newArrayList();
    checkArgument(params.size() == args.size(), "Expected " + params.size() + " arguments");
    for (int i = 0; i < params.size(); i++) {
      Object arg = args.get(i);
      if (params.get(i).equals(NumericLiteral.class)) {
        checkArgument(arg != null && Doubles.tryParse(String.valueOf(arg)) != null,
            "Expected a numeric literal for argument " + (i + 1));
        arg = ((Number) arg).doubleValue();
      } else if (params.get(i).equals(BooleanLiteral.class)) {
        checkArgument(
            arg != null
                && (arg.getClass().equals(Boolean.class) || arg.getClass().equals(boolean.class)),
            "Expected a boolean literal for argument " + (i + 1));
      }
      adjustedArgs.add(arg);
    }
    return adjustedArgs;
  }

  private String asString() {
    StringBuilder buf = new StringBuilder();
    if (args.size() > 0) {
      buf.append("(");
      boolean firstLoop = true;
      for (int i = 0; i < args.size(); i++) {
        if (firstLoop) {
          firstLoop = false;
        } else {
          buf.append(", ");
        }
        buf.append(args.get(i));
      }
      buf.append(")");
    }
    return id.toString() + buf.toString();
  }

}
