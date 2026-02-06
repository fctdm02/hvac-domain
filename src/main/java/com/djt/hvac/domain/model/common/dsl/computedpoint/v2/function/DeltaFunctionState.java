package com.djt.hvac.domain.model.common.dsl.computedpoint.v2.function;

import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import static java.util.Objects.requireNonNull;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = DeltaFunctionState.Builder.class)
public class DeltaFunctionState {
  private final long timestamp;
  private final double value;

  @JsonCreator
  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(DeltaFunctionState state) {
    return new Builder(state);
  }

  private DeltaFunctionState(Builder builder) {
    this.timestamp = builder.timestamp;
    this.value = builder.value;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public double getValue() {
    return value;
  }

  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
    long temp;
    temp = Double.doubleToLongBits(value);
    result = prime * result + (int) (temp ^ (temp >>> 32));
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
    DeltaFunctionState other = (DeltaFunctionState) obj;
    if (timestamp != other.timestamp)
      return false;
    if (Double.doubleToLongBits(value) != Double.doubleToLongBits(other.value))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "DeltaFunctionState [timestamp=" + timestamp + ", value=" + value + "]";
  }

  @JsonPOJOBuilder
  public static class Builder {
    private Long timestamp;
    private Double value;

    private Builder() {}

    private Builder(DeltaFunctionState state) {
      requireNonNull(state, "state cannot be null");
      this.timestamp = state.timestamp;
      this.value = state.value;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withTimestamp(long timestamp) {
      this.timestamp = timestamp;
      return this;
    }

    public Builder withValue(double value) {
      this.value = Math.round(value * 100000.0) / 100000.0;
      return this;
    }

    public DeltaFunctionState build() {
      requireNonNull(timestamp, "timestamp cannot be null");
      requireNonNull(value, "value cannot be null");
      return new DeltaFunctionState(this);
    }
  }
}
