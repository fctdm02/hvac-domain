package com.djt.hvac.domain.model.common.dsl.computedpoint.v2.function;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.Lists;

import java.util.Collections;

@JsonDeserialize(builder = MeterDeltaFunctionState.Builder.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MeterDeltaFunctionState {
  private final long timestamp;
  private final double value;
  private final Optional<Double> negativeSlope;
  private final List<Double> historicValues;

private MeterDeltaFunctionState(Builder builder) {
	this.timestamp = builder.timestamp;
	this.value = builder.value;
	this.negativeSlope = builder.negativeSlope;
	this.historicValues = builder.historicValues;
}
  
  public long getTimestamp() {
    return timestamp;
  }

  public double getValue() {
    return value;
  }
  
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  public Optional<Double> getNegativeSlope() {
    return negativeSlope;
  }

  
  public List<Double> getHistoricValues() {
	return historicValues;
  }

@Override
public String toString() {
	return "MeterDeltaFunctionState [timestamp=" + timestamp + ", value=" + value + ", negativeSlope=" + negativeSlope
			+ ", historicValues=" + historicValues + "]";
}

  @Override
public int hashCode() {
	return Objects.hash(historicValues, negativeSlope, timestamp, value);
}

  @Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (getClass() != obj.getClass())
		return false;
	MeterDeltaFunctionState other = (MeterDeltaFunctionState) obj;
	return Objects.equals(historicValues, other.historicValues) && Objects.equals(negativeSlope, other.negativeSlope)
			&& timestamp == other.timestamp && Double.doubleToLongBits(value) == Double.doubleToLongBits(other.value);
}

  /**
 * Creates builder to build {@link MeterDeltaFunctionState}.
 * @return created builder
 */
public static Builder builder() {
	return new Builder();
}

/**
 * Builder to build {@link MeterDeltaFunctionState}.
 */
public static final class Builder {
	private long timestamp;
	private double value;
	private Optional<Double> negativeSlope = Optional.empty();
	private List<Double> historicValues = Lists.newArrayList();

	private Builder() {
	}

	/**
	* Builder method for timestamp parameter.
	* @param timestamp field to set
	* @return builder
	*/
	public Builder withTimestamp(long timestamp) {
		this.timestamp = timestamp;
		return this;
	}

	/**
	* Builder method for value parameter.
	* @param value field to set
	* @return builder
	*/
	public Builder withValue(double value) {
		this.value = value;
		return this;
	}

	/**
	* Builder method for negativeSlope parameter.
	* @param negativeSlope field to set
	* @return builder
	*/
	public Builder withNegativeSlope(Optional<Double> negativeSlope) {
		this.negativeSlope = negativeSlope;
		return this;
	}

	/**
	* Builder method for historicValues parameter.
	* @param historicValues field to set
	* @return builder
	*/
	public Builder withHistoricValues(List<Double> historicValues) {
		this.historicValues = historicValues;
		return this;
	}

	/**
	* Builder method of the builder.
	* @return built class
	*/
	public MeterDeltaFunctionState build() {
		return new MeterDeltaFunctionState(this);
	}
}
}
