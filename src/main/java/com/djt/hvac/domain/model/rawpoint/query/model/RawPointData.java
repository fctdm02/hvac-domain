//@formatter:off
package com.djt.hvac.domain.model.rawpoint.query.model;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Consumer;

import com.djt.hvac.domain.model.common.query.model.QueryResponseItem;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = RawPointData.Builder.class)
@JsonPropertyOrder({
  "numTotalPoints",
  "numIgnoredPoints"
})
public class RawPointData extends QueryResponseItem implements Serializable {
  
  private static final long serialVersionUID = 7127557192164174644L;
  
  private final Integer numTotalPoints;
  private final Integer numIgnoredPoints;

  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(RawPointData data) {
    return new Builder(data);
  }

  private RawPointData(Builder builder) {
    this.numTotalPoints = builder.numTotalPoints;
    this.numIgnoredPoints = builder.numIgnoredPoints;
  }
  
  public Integer getNumTotalPoints() {
    return numTotalPoints;
  }
  
  public Integer getNumIgnoredPoints() {
    return numIgnoredPoints;
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(numIgnoredPoints, numTotalPoints);
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
    return Objects.equals(numIgnoredPoints, other.numIgnoredPoints)
	&& Objects.equals(numTotalPoints, other.numTotalPoints);
  }

  @Override
  public String toString() {
    return "RawPointData [numTotalPoints=" + numTotalPoints + ", numIgnoredPoints=" + numIgnoredPoints + "]";
  }

  public static class Builder {
    private Integer numTotalPoints;
    private Integer numIgnoredPoints;

    private Builder() {}

    private Builder(RawPointData request) {
      requireNonNull(request, "request cannot be null");
      this.numTotalPoints = request.numTotalPoints;
      this.numIgnoredPoints = request.numIgnoredPoints;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withNumTotalPoints(Integer numTotalPoints) {
      requireNonNull(numTotalPoints, "numTotalPoints cannot be null");
      this.numTotalPoints = numTotalPoints;
      return this;
    }

    public Builder withNumIgnoredPoints(Integer numIgnoredPoints) {
      requireNonNull(numIgnoredPoints, "numIgnoredPoints cannot be null");
      this.numIgnoredPoints = numIgnoredPoints;
      return this;
    }

    public RawPointData build() {
      requireNonNull(numTotalPoints, "numTotalPoints cannot be null");
      requireNonNull(numIgnoredPoints, "numIgnoredPoints cannot be null");
      return new RawPointData(this);
    }
  }
}
//@formatter:on