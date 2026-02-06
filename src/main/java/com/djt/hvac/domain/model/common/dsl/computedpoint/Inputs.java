package com.djt.hvac.domain.model.common.dsl.computedpoint;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import static java.util.Objects.requireNonNull;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

public class Inputs {
  private final Map<Variable<?>, Object> inputs;

  static Builder builder(Set<Variable<?>> vars) {
    return new Builder(vars);
  }

  private Inputs(Builder builder) {
    this.inputs = ImmutableMap.copyOf(builder.inputs);
  }

  Set<Variable<?>> getVariables() {
    return inputs.keySet();
  }

  @SuppressWarnings("unchecked")
  public <T> T getValue(Variable<T> id) {
    return (T) inputs.get(id);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((inputs == null) ? 0 : inputs.hashCode());
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
    Inputs other = (Inputs) obj;
    if (inputs == null) {
      if (other.inputs != null)
        return false;
    } else if (!inputs.equals(other.inputs))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Inputs [inputs=" + inputs + "]";
  }

  public static class Builder {
    private final Set<Variable<?>> vars;
    private final Map<String, Variable<?>> varsById;
    private Map<Variable<?>, Object> inputs = Maps.newHashMap();

    private Builder(Set<Variable<?>> vars) {
      requireNonNull(vars, "vars cannot be null");
      this.vars = ImmutableSet.copyOf(vars);
      this.varsById = ImmutableMap.copyOf(
          this.vars.stream().collect(Collectors.toMap(Variable::getId, Function.identity())));
    }

    public Set<Variable<?>> getVariables() {
      return vars;
    }

    public <T> Builder withInput(String id, T value) {
      requireNonNull(id, "id cannot be null");
      requireNonNull(value, "value cannot be null");
      checkArgument(varsById.containsKey(id), "Invalid variable id " + id);
      Variable<?> var = varsById.get(id);
      if (Number.class.isAssignableFrom(var.getType())) {
        checkArgument(Number.class.isAssignableFrom(value.getClass()),
            "Expected a numeric value for the " + id + " variable");
        Double doubleValue = Number.class.cast(value).doubleValue();
        inputs.put(Variable.create(Double.class, id), doubleValue);
      } else if (Boolean.class.isAssignableFrom(var.getType())) {
        checkArgument(Boolean.class.isAssignableFrom(value.getClass()),
            "Expected a boolean value for the " + id + " variable");
        Boolean boolValue = Boolean.class.cast(value);
        inputs.put(Variable.create(Boolean.class, id), boolValue);
      } else {
        throw new IllegalArgumentException(
            "Expected a boolean or numeric value for the " + id + " variable");
      }
      return this;
    }

    public <T> Builder withInput(Variable<T> var, T value) {
      requireNonNull(var, "var cannot be null");
      requireNonNull(value, "value cannot be null");
      checkArgument(varsById.containsKey(var.getId()), "Invalid variable id " + var.getId());
      inputs.put(var, value);
      return this;
    }

    public Inputs build() {
      for (Variable<?> var : varsById.values()) {
        checkState(inputs.containsKey(var),
            "Expected a value for the " + var.getId() + " variable");
      }
      return new Inputs(this);
    }

  }

}
