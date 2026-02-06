package com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = EnergyExchangeParentChildData.Builder.class)
public class EnergyExchangeParentChildData {
  private final Integer id;
  private final Integer nodeTypeId;
  private final Integer systemTypeId;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (EnergyExchangeParentChildData energyExchangeParentChildData) {
    return new Builder(energyExchangeParentChildData);
  }

  private EnergyExchangeParentChildData (Builder builder) {
    this.id = builder.id;
    this.nodeTypeId = builder.nodeTypeId;
    this.systemTypeId = builder.systemTypeId;
  }

  public Integer getId() {
    return id;
  }

  public Integer getNodeTypeId() {
    return nodeTypeId;
  }

  public Integer getSystemTypeId() {
    return systemTypeId;
  }

  @JsonPOJOBuilder
  public static class Builder {
    private Integer id;
    private Integer nodeTypeId;
    private Integer systemTypeId;

    private Builder() {}

    private Builder(EnergyExchangeParentChildData energyExchangeParentChildData) {
      requireNonNull(energyExchangeParentChildData, "energyExchangeParentChildData cannot be null");
      this.id = energyExchangeParentChildData.id;
      this.nodeTypeId = energyExchangeParentChildData.nodeTypeId;
      this.systemTypeId = energyExchangeParentChildData.systemTypeId;
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

    public Builder withNodeTypeId(Integer nodeTypeId) {
      requireNonNull(nodeTypeId, "nodeTypeId cannot be null");
      this.nodeTypeId = nodeTypeId;
      return this;
    }

    public Builder withSystemTypeId(Integer systemTypeId) {
      requireNonNull(systemTypeId, "systemTypeId cannot be null");
      this.systemTypeId = systemTypeId;
      return this;
    }

    public EnergyExchangeParentChildData build() {
      requireNonNull(id, "id cannot be null");
      requireNonNull(nodeTypeId, "nodeTypeId cannot be null");
      requireNonNull(systemTypeId, "systemTypeId cannot be null");
      return new EnergyExchangeParentChildData(this);
    }
  }
}