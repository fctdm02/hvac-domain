//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = RawPointData.Builder.class)
@JsonPropertyOrder({
    "rawPointId",
    "metricId"
})
public class RawPointData {
  private final Integer rawPointId;
  private final String metricId;

  @JsonCreator
  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(RawPointData rawPointData) {
    return new Builder(rawPointData);
  }

  private RawPointData(Builder builder) {
    this.rawPointId = builder.rawPointId;
    this.metricId = builder.metricId;
  }

  public Integer getRawPointId() {
    return rawPointId;
  }

  public String getMetricId() {
    return metricId;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((metricId == null) ? 0 : metricId.hashCode());
    result = prime * result + ((rawPointId == null) ? 0 : rawPointId.hashCode());
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
    RawPointData other = (RawPointData) obj;
    if (metricId == null) {
      if (other.metricId != null)
        return false;
    } else if (!metricId.equals(other.metricId))
      return false;
    if (rawPointId == null) {
      if (other.rawPointId != null)
        return false;
    } else if (!rawPointId.equals(other.rawPointId))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return new StringBuilder()
        .append("RawPointData [rawPointId=")
        .append(rawPointId)
        .append(", metricId=")
        .append(metricId)
        .append("]")
        .toString();
  }

  @JsonPOJOBuilder
  public static class Builder {
    private Integer rawPointId;
    private String metricId;

    private Builder() {}

    private Builder(RawPointData rawPointData) {
      requireNonNull(rawPointData, "rawPointData cannot be null");
      this.rawPointId = rawPointData.rawPointId;
      this.metricId = rawPointData.metricId;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withRawPointId(Integer rawPointId) {
      requireNonNull(rawPointId, "rawPointId cannot be null");
      this.rawPointId = rawPointId;
      return this;
    }

    public Builder withMetricId(String metricId) {
      requireNonNull(metricId, "metricId cannot be null");
      this.metricId = metricId;
      return this;
    }

    public RawPointData build() {
      requireNonNull(rawPointId, "rawPointId cannot be null");
      requireNonNull(metricId, "metricId cannot be null");
      return new RawPointData(this);
    }
  }
}
//@formatter:on