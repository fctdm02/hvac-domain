package com.djt.hvac.domain.model.dictionary.dto;

import static java.util.Objects.requireNonNull;
import java.util.function.Consumer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = PointTemplateDto.Builder.class)
@JsonPropertyOrder({
  "id",
  "name",
  "displayName",
  "unitId"
})
public class PointTemplateDto {
  
  private final Integer id;
  private final String name;
  private final String displayName;
  private final Integer unitId;

  @JsonCreator
  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(PointTemplateDto entityIndex) {
    return new Builder(entityIndex);
  }

  private PointTemplateDto(Builder builder) {
    this.id = builder.id;
    this.name = builder.name;
    this.displayName = builder.displayName;
    this.unitId = builder.unitId;
  }

  public Integer getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDisplayName() {
    return displayName;
  }  

  public Integer getUnitId() {
    return unitId;
  }
  
  @Override
  public String toString() {
    return new StringBuilder()
        .append("PointTemplateDto [id=")
        .append(id)
        .append(", name=")
        .append(name)
        .append(", displayName=")
        .append(displayName)
        .append(", unitId=")
        .append(unitId)
        .append("]")
        .toString();
  }

  @JsonPOJOBuilder
  public static class Builder {
    private Integer id;
    private String name;
    private String displayName;
    private Integer unitId;
    
    private Builder() {}

    private Builder(PointTemplateDto entityIndex) {
      requireNonNull(entityIndex, "entityIndex cannot be null");
      this.id = entityIndex.id;
      this.name = entityIndex.name;
      this.displayName = entityIndex.displayName;
      this.unitId = entityIndex.unitId;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withId(Integer id) {
      requireNonNull(id, "id cannot be null");
      this.id = id;
      return this;
    }

    public Builder withName(String name) {
      requireNonNull(name, "name cannot be null");
      this.name = name;
      return this;
    }
    
    public Builder withDisplayName(String displayName) {
      requireNonNull(displayName, "displayName cannot be null");
      this.displayName = displayName;
      return this;
    }
    
    public Builder withUnitId(Integer unitId) {
      requireNonNull(unitId, "unitId cannot be null");
      this.unitId = unitId;
      return this;
    }
    
    public PointTemplateDto build() {
      requireNonNull(id, "id cannot be null");
      requireNonNull(name, "name cannot be null");
      requireNonNull(displayName, "displayName cannot be null");
      requireNonNull(unitId, "unitId cannot be null");
      return new PointTemplateDto(this);
    }
  }
}
