package com.djt.hvac.domain.model.nodehierarchy.service.command;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.function.Consumer;

import com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject.UpdateBuildingNodeRequest;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.ImmutableList;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = UpdateBuildingNodesRequest.Builder.class)
public class UpdateBuildingNodesRequest extends AbstractNodeHierarchyCommandRequest {
  
  private static final long serialVersionUID = 1L;
  
  private final List<UpdateBuildingNodeRequest> data;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (UpdateBuildingNodesRequest updateBuildingNodesRequest) {
    return new Builder(updateBuildingNodesRequest);
  }

  private UpdateBuildingNodesRequest (Builder builder) {
    super(builder);
    this.data = builder.data;
  }

  public List<UpdateBuildingNodeRequest> getData() {
    return data;
  }

  @Override
  public String getOperationCategory() {
    return NodeHierarchyCommandRequest.BULK_BUILDINGS_OPERATION_CATEGORY;
  }
  
  @Override
  public String getOperationType() {
    return NodeHierarchyCommandRequest.UPDATE_OPERATION_TYPE;
  }
  
  @Override
  public String toString() {
    
    return new StringBuilder()
        .append(super.toString())
        .append(", data=")
        .append(data)
        .append("]")
        .toString();
  }
  
  @JsonPOJOBuilder
  public static class Builder extends AbstractNodeHierarchyCommandRequest.Builder<UpdateBuildingNodesRequest, Builder> {
  
    private List<UpdateBuildingNodeRequest> data;

    private Builder() {}

    private Builder(UpdateBuildingNodesRequest commandRequest) {
      requireNonNull(commandRequest, "commandRequest cannot be null");
      this.data = commandRequest.data;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withData(List<UpdateBuildingNodeRequest> data) {
      requireNonNull(data, "data cannot be null");
      this.data = ImmutableList.copyOf(data);
      return this;
    }
    
    @Override
    protected Builder getThis() {
      return this;
    }

    @Override
    protected UpdateBuildingNodesRequest newInstance() {
      requireNonNull(data, "data cannot be null");
      return new UpdateBuildingNodesRequest(this);
    }    
  }
}