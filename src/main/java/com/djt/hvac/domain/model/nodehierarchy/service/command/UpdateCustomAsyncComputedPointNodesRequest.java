//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.service.command;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.function.Consumer;

import com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject.CustomAsyncComputedPointNodeData;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.ImmutableList;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = UpdateCustomAsyncComputedPointNodesRequest.Builder.class)
public class UpdateCustomAsyncComputedPointNodesRequest extends AbstractNodeHierarchyCommandRequest {
  
  private static final long serialVersionUID = 1L;
  
  private final List<CustomAsyncComputedPointNodeData> dtoList;

  @JsonCreator
  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder (UpdateCustomAsyncComputedPointNodesRequest updateCustomAsyncComputedPointNodesRequest) {
    return new Builder(updateCustomAsyncComputedPointNodesRequest);
  }

  private UpdateCustomAsyncComputedPointNodesRequest(Builder builder) {
    super(builder);
    this.dtoList = builder.dtoList;
  }

  public List<CustomAsyncComputedPointNodeData> getDtoList() {
    return dtoList;
  }

  @Override
  public String getOperationCategory() {
    return NodeHierarchyCommandRequest.BULK_NODE_OPERATION_CATEGORY;
  }
  
  @Override
  public String getOperationType() {
    return NodeHierarchyCommandRequest.UPDATE_CUSTOM_POINT;
  }
  
  @Override
  public String toString() {
    
    return new StringBuilder()
        .append(super.toString())
        .append(", dtoList=")
        .append(dtoList)
        .append("]")
        .toString();
  }
  
  @JsonPOJOBuilder
  public static class Builder extends AbstractNodeHierarchyCommandRequest.Builder<UpdateCustomAsyncComputedPointNodesRequest, Builder> {
    
    private List<CustomAsyncComputedPointNodeData> dtoList;

    private Builder() {}

    private Builder(UpdateCustomAsyncComputedPointNodesRequest updateCustomAsyncComputedPointNodesRequest) {
      requireNonNull(updateCustomAsyncComputedPointNodesRequest, "updateCustomAsyncComputedPointNodesRequest cannot be null");
      this.dtoList = updateCustomAsyncComputedPointNodesRequest.dtoList;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withDtoList(List<CustomAsyncComputedPointNodeData> dtoList) {
      requireNonNull(dtoList, "dtoList cannot be null");
      this.dtoList = ImmutableList.copyOf(dtoList);
      return this;
    }

    @Override
    protected Builder getThis() {
      return this;
    }

    @Override
    protected UpdateCustomAsyncComputedPointNodesRequest newInstance() {
      requireNonNull(dtoList, "dtoList cannot be null");
      return new UpdateCustomAsyncComputedPointNodesRequest(this);
    }
  }
}
//@formatter:on