package com.djt.hvac.domain.model.nodehierarchy.dto.custompoint;

import static java.util.Objects.requireNonNull;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = TemporalAsyncPointVar.Builder.class)
public class TemporalAsyncPointVar {
  private final Integer temporalAsyncPointConfigId;
  private final Integer pointId;
  private final FillPolicy fillPolicy;
  private final String variableName;
  private final String metricId;
  private final String nodePath;

  private TemporalAsyncPointVar(Builder builder) {
    this.temporalAsyncPointConfigId = builder.temporalAsyncPointConfigId;
    this.pointId = builder.pointId;
    this.fillPolicy = builder.fillPolicy;
    this.variableName = builder.variableName;
    this.metricId = builder.metricId;
    this.nodePath = builder.nodePath;
  }

  /**
   * Creates builder to build {@link TemporalAsyncPointVar}.
   *
   * @return created builder
   */
  @JsonCreator
  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(TemporalAsyncPointVar other) {
    return new Builder(other);
  }

  public Integer getTemporalAsyncPointConfigId() {
    return temporalAsyncPointConfigId;
  }

  public Integer getPointId() {
    return pointId;
  }

  public FillPolicy getFillPolicy() {
    return fillPolicy;
  }

  public String getVariableName() {
    return variableName;
  }

  public String getMetricId() {
    return metricId;
  }

  public String getNodePath() {
    return nodePath;
  }

  @Override
  public String toString() {
    return "TemporalAsyncPointVar [temporalAsyncPointConfigId=" + temporalAsyncPointConfigId
        + ", pointId=" + pointId + ", fillPolicy=" + fillPolicy + ", variableName=" + variableName
        + ", metricId=" + metricId + ", nodePath=" + nodePath + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fillPolicy == null) ? 0 : fillPolicy.hashCode());
    result = prime * result + ((metricId == null) ? 0 : metricId.hashCode());
    result = prime * result + ((nodePath == null) ? 0 : nodePath.hashCode());
    result = prime * result + ((pointId == null) ? 0 : pointId.hashCode());
    result = prime * result
        + ((temporalAsyncPointConfigId == null) ? 0 : temporalAsyncPointConfigId.hashCode());
    result = prime * result + ((variableName == null) ? 0 : variableName.hashCode());
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
    TemporalAsyncPointVar other = (TemporalAsyncPointVar) obj;
    if (fillPolicy != other.fillPolicy)
      return false;
    if (metricId == null) {
      if (other.metricId != null)
        return false;
    } else if (!metricId.equals(other.metricId))
      return false;
    if (nodePath == null) {
      if (other.nodePath != null)
        return false;
    } else if (!nodePath.equals(other.nodePath))
      return false;
    if (pointId == null) {
      if (other.pointId != null)
        return false;
    } else if (!pointId.equals(other.pointId))
      return false;
    if (temporalAsyncPointConfigId == null) {
      if (other.temporalAsyncPointConfigId != null)
        return false;
    } else if (!temporalAsyncPointConfigId.equals(other.temporalAsyncPointConfigId))
      return false;
    if (variableName == null) {
      if (other.variableName != null)
        return false;
    } else if (!variableName.equals(other.variableName))
      return false;
    return true;
  }


  /**
   * Builder to build {@link TemporalAsyncPointVar}.
   */
  @JsonPOJOBuilder
  public static final class Builder {
    private Integer temporalAsyncPointConfigId;
    private Integer pointId;
    private FillPolicy fillPolicy;
    private String variableName;
    private String metricId;
    private String nodePath;

    private Builder() {}

    private Builder(TemporalAsyncPointVar other) {
      this.temporalAsyncPointConfigId = other.temporalAsyncPointConfigId;
      this.pointId = other.pointId;
      this.fillPolicy = other.fillPolicy;
      this.variableName = other.variableName;
      this.metricId = other.metricId;
      this.nodePath = other.nodePath;
    }

    public Builder withTemporalAsyncPointConfigId(Integer temporalAsyncPointConfigId) {
      this.temporalAsyncPointConfigId =
          requireNonNull(temporalAsyncPointConfigId, "TemporalAsyncPointConfigId is required");
      return this;
    }

    @NotNull(message = "{AsyncPointVar.0}")
    public Builder withPointId(Integer pointId) {
      this.pointId = requireNonNull(pointId, "A point id is required");
      return this;
    }

    @NotNull(message = "{AsyncPointVar.1}")
    public Builder withFillPolicy(FillPolicy fillPolicy) {
      this.fillPolicy = requireNonNull(fillPolicy, "The fill policy is required");
      return this;
    }

    @NotNull(message = "{AsyncPointVar.2}")
    public Builder withVariableName(String variableName) {
      this.variableName = requireNonNull(variableName, "The variable nameis requiured!");
      return this;
    }

    public Builder withMetricId(String metricId) {
      this.metricId = metricId;
      return this;
    }

    public Builder withNodePath(String nodePath) {
      this.nodePath = nodePath;
      return this;
    }

    public TemporalAsyncPointVar build() {
      requireNonNull(pointId, "A point id is required");
      requireNonNull(fillPolicy, "The fill policy is required");
      requireNonNull(variableName, "The variable nameis requiured!");

      return new TemporalAsyncPointVar(this);
    }
  }
}
