package com.djt.hvac.domain.model.nodehierarchy.service.command;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.ImmutableList;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = MoveChildNodesRequest.Builder.class)
public class MoveChildNodesRequest extends AbstractNodeHierarchyCommandRequest {
  
  private static final long serialVersionUID = 1L;
  
  private final Integer newParentId;
  private final List<Integer> childIds;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (MoveChildNodesRequest commandRequest) {
    return new Builder(commandRequest);
  }

  private MoveChildNodesRequest (Builder builder) {
    super(builder);
    this.newParentId = builder.newParentId;
    this.childIds = builder.childIds;
  }
  public Integer getNewParentId() {
    return newParentId;
  }

  public List<Integer> getChildIds() {
    return childIds;
  }
      
  @Override
  public String getOperationCategory() {
    return NodeHierarchyCommandRequest.BULK_NODE_OPERATION_CATEGORY;
  }
  
  @Override
  public String getOperationType() {
    return NodeHierarchyCommandRequest.MOVE_OPERATION_TYPE;
  }

  @Override
  public String toString() {
    
    return new StringBuilder()
        .append(super.toString())
        .append(", newParentId=")
        .append(newParentId)
        .append(", childIds=")
        .append(childIds)
        .append("]")
        .toString();
  }
  
  @JsonPOJOBuilder
  public static class Builder extends AbstractNodeHierarchyCommandRequest.Builder<MoveChildNodesRequest, Builder> {
    
    private Integer newParentId;
    private List<Integer> childIds;

    private Builder() {}

    private Builder(MoveChildNodesRequest moveChildNodesRequest) {
      requireNonNull(moveChildNodesRequest, "moveChildNodesRequest cannot be null");
      this.newParentId = moveChildNodesRequest.newParentId;
      this.childIds = moveChildNodesRequest.childIds;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withNewParentId(Integer newParentId) {
      requireNonNull(newParentId, "newParentId cannot be null");
      this.newParentId = newParentId;
      return this;
    }

    public Builder withChildIds(List<Integer> childIds) {
      requireNonNull(childIds, "childIds cannot be null");
      this.childIds = ImmutableList.copyOf(childIds);
      return this;
    }

    @Override
    protected Builder getThis() {
      return this;
    }

    @Override
    protected MoveChildNodesRequest newInstance() {
      requireNonNull(newParentId, "newParentId cannot be null");
      requireNonNull(childIds, "childIds cannot be null");
      return new MoveChildNodesRequest(this);
    }
  }
}