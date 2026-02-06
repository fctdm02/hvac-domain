package com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = AdFunctionInstanceInputPoint.Builder.class)
public class AdFunctionInstanceInputPoint {
  private final String name;
  private final Integer pointId;
  private final String nodePath;
  private final Boolean isRequired;
  private final Boolean isArray;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (AdFunctionInstanceInputPoint adFunctionInstanceInputPoint) {
    return new Builder(adFunctionInstanceInputPoint);
  }

  private AdFunctionInstanceInputPoint (Builder builder) {
    this.name = builder.name;
    this.pointId = builder.pointId;
    this.nodePath = builder.nodePath;
    this.isRequired = builder.isRequired;
    this.isArray = builder.isArray;
  }

  public String getName() {
    return name;
  }

  public Integer getPointId() {
    return pointId;
  }

  public String getNodePath() {
    return nodePath;
  }

  public Boolean getIsRequired() {
    return isRequired;
  }

  public Boolean getIsArray() {
    return isArray;
  }

  @JsonPOJOBuilder
  public static class Builder {
    private String name;
    private Integer pointId;
    private String nodePath;
    private Boolean isRequired;
    private Boolean isArray;

    private Builder() {}

    private Builder(AdFunctionInstanceInputPoint adFunctionInstanceInputPoint) {
      requireNonNull(adFunctionInstanceInputPoint, "adFunctionInstanceInputPoint cannot be null");
      this.name = adFunctionInstanceInputPoint.name;
      this.pointId = adFunctionInstanceInputPoint.pointId;
      this.nodePath = adFunctionInstanceInputPoint.nodePath;
      this.isRequired = adFunctionInstanceInputPoint.isRequired;
      this.isArray = adFunctionInstanceInputPoint.isArray;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withName(String name) {
      requireNonNull(name, "name cannot be null");
      this.name = name;
      return this;
    }

    public Builder withPointId(Integer pointId) {
      requireNonNull(pointId, "pointId cannot be null");
      this.pointId = pointId;
      return this;
    }

    public Builder withNodePath(String nodePath) {
      requireNonNull(nodePath, "nodePath cannot be null");
      this.nodePath = nodePath;
      return this;
    }

    public Builder withIsRequired(Boolean isRequired) {
      requireNonNull(isRequired, "isRequired cannot be null");
      this.isRequired = isRequired;
      return this;
    }

    public Builder withIsArray(Boolean isArray) {
      requireNonNull(isArray, "isArray cannot be null");
      this.isArray = isArray;
      return this;
    }

    public AdFunctionInstanceInputPoint build() {
      requireNonNull(name, "name cannot be null");
      requireNonNull(pointId, "pointId cannot be null");
      requireNonNull(nodePath, "nodePath cannot be null");
      requireNonNull(isRequired, "isRequired cannot be null");
      requireNonNull(isArray, "isArray cannot be null");
      return new AdFunctionInstanceInputPoint(this);
    }
  }
}