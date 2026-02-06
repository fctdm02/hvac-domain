package com.djt.hvac.domain.model.common.dsl.computedpoint.v2;

import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;

import static java.util.Objects.requireNonNull;

class BooleanResult {
  private final Optional<Boolean> value;
  private final Map<String, String> state;

  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(BooleanResult result) {
    return new Builder(result);
  }

  private BooleanResult(Builder builder) {
    this.value = builder.value;
    this.state = builder.state;
  }

  public Optional<Boolean> getValue() {
    return value;
  }

  public Map<String, String> getState() {
    return state;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((state == null) ? 0 : state.hashCode());
    result = prime * result + ((value == null) ? 0 : value.hashCode());
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
    BooleanResult other = (BooleanResult) obj;
    if (state == null) {
      if (other.state != null)
        return false;
    } else if (!state.equals(other.state))
      return false;
    if (value == null) {
      if (other.value != null)
        return false;
    } else if (!value.equals(other.value))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Result [value=" + value + ", state=" + state + "]";
  }

  public static class Builder {
    private Optional<Boolean> value;
    private Map<String, String> state;

    private Builder(BooleanResult result) {
      this.value = result.value;
      this.state = result.state;
    }

    private Builder() {}

    public Builder withValue(Optional<Boolean> value) {
      this.value = requireNonNull(value, "value cannot be null");
      return this;
    }

    public Builder withState(Map<String, String> state) {
      this.state = ImmutableMap.copyOf(requireNonNull(state, "state cannot be null"));
      return this;
    }

    public BooleanResult build() {
      requireNonNull(value, "value cannot be null");
      requireNonNull(state, "state cannot be null");
      return new BooleanResult(this);
    }

  }
}
