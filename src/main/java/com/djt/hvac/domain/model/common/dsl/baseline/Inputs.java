package com.djt.hvac.domain.model.common.dsl.baseline;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class Inputs {

  private final Map<Variable<?>, Object> inputs;

  public static Builder builder() {
    return new Builder();
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
    private Map<Variable<?>, Object> inputs = Maps.newHashMap();

    private Builder() {}

    public <T> Builder withInput(VariableId<T> id, T value) {
      inputs.put(Variable.create(id), value);
      return this;
    }

    public <T> Builder withInput(VariableId<T> id, List<?> args, T value) {
      inputs.put(Variable.create(id, args), value);
      return this;
    }

    public <T> Builder withInput(Variable<T> var, T value) {
      inputs.put(var, value);
      return this;
    }

    public Inputs build() {
      return new Inputs(this);
    }

  }
}
