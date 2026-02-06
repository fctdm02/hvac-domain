//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.service.command;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.function.Consumer;

import com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject.EnergyExchangeSystemNodeData;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.ImmutableList;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = UpdateEnergyExchangeSystemNodesRequest.Builder.class)
public class UpdateEnergyExchangeSystemNodesRequest extends AbstractNodeHierarchyCommandRequest {
  
  private static final long serialVersionUID = 1L;
  
  private final Integer energyExchangeSystemNodeId;
  private final List<EnergyExchangeSystemNodeData> data;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (UpdateEnergyExchangeSystemNodesRequest commandRequest) {
    return new Builder(commandRequest);
  }

  private UpdateEnergyExchangeSystemNodesRequest (Builder builder) {
    super(builder);
    this.energyExchangeSystemNodeId = builder.energyExchangeSystemNodeId;
    this.data = builder.data;
  }
  
  public Integer getEnergyExchangeSystemNodeId() {
    return energyExchangeSystemNodeId;
  }
  
  public List<EnergyExchangeSystemNodeData> getData() {
    return data;
  }

  @Override
  public String getOperationCategory() {
    return NodeHierarchyCommandRequest.BULK_ENERGY_EXCHANGE_OPERATION_CATEGORY;
  }
  
  @Override
  public String getOperationType() {
    return NodeHierarchyCommandRequest.UPDATE_OPERATION_TYPE;
  }
  
  @Override
  public String toString() {
    
    return new StringBuilder()
        .append(super.toString())
        .append(", energyExchangeSystemNodeId=")
        .append(energyExchangeSystemNodeId)
        .append(", data=")
        .append(data)
        .append("]")
        .toString();
  }
  
  @JsonPOJOBuilder
  public static class Builder extends AbstractNodeHierarchyCommandRequest.Builder<UpdateEnergyExchangeSystemNodesRequest, Builder> {
    
    private Integer energyExchangeSystemNodeId;
    private List<EnergyExchangeSystemNodeData> data;

    private Builder() {}

    private Builder(UpdateEnergyExchangeSystemNodesRequest commandRequest) {
      requireNonNull(commandRequest, "commandRequest cannot be null");
      this.data = commandRequest.data;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withEnergyExchangeSystemNodeId(Integer energyExchangeSystemNodeId) {
      this.energyExchangeSystemNodeId = energyExchangeSystemNodeId;
      return this;
    }

    public Builder withData(List<EnergyExchangeSystemNodeData> data) {
      requireNonNull(data, "data cannot be null");
      this.data = ImmutableList.copyOf(data);
      return this;
    }
    
    @Override
    protected Builder getThis() {
      return this;
    }

    @Override
    protected UpdateEnergyExchangeSystemNodesRequest newInstance() {
      requireNonNull(data, "data cannot be null");
      return new UpdateEnergyExchangeSystemNodesRequest(this);
    }
  }
}
//@formatter:on