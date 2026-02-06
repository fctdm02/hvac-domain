package com.djt.hvac.domain.model.nodehierarchy.point.query.model;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = PointLastValue.Builder.class)
public class PointLastValue  {
  
  private final String value;
  private final Long valueTimestamp;

  @JsonCreator
  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(PointLastValue pointTemplateIndex) {
    return new Builder(pointTemplateIndex);
  }

  private PointLastValue(Builder builder) {
    this.value = builder.value;
    this.valueTimestamp = builder.valueTimestamp;
  }

  public String getValue() {
    return value;
  }

  public Long getValueTimestamp() {
    return valueTimestamp;
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(value, valueTimestamp);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    PointLastValue other = (PointLastValue) obj;
    return Objects.equals(value, other.value)
        && Objects.equals(valueTimestamp, other.valueTimestamp);
  }

  @Override
  public String toString() {
    return "PointLastValue [value=" + value + ", valueTimestamp=" + valueTimestamp + "]";
  }

  @JsonPOJOBuilder
  public static class Builder {
    private String value;
    private Long valueTimestamp;

    private Builder() {}

    private Builder(PointLastValue pointLastValue) {
      requireNonNull(pointLastValue, "pointLastValue cannot be null");
      this.value = pointLastValue.value;
      this.valueTimestamp = pointLastValue.valueTimestamp;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withValue(String value) {
      requireNonNull(value, "value cannot be null");
      this.value = value;
      return this;
    }

    public Builder withValueTimestamp(Long valueTimestamp) {
      requireNonNull(valueTimestamp, "valueTimestamp cannot be null");
      this.valueTimestamp = valueTimestamp;
      return this;
    }

    public PointLastValue build() {
      requireNonNull(value, "value cannot be null");
      requireNonNull(valueTimestamp, "valueTimestamp cannot be null");
      return new PointLastValue(this);
    }
  }
}
