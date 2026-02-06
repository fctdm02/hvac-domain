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
@JsonDeserialize(builder = DeleteChildNodesRequest.Builder.class)
public class DeleteChildNodesRequest extends AbstractNodeHierarchyCommandRequest {
  
  private static final long serialVersionUID = 1L;
  
  private final List<Integer> childIds;
  private final Boolean ignoreMappablePointRawPoint;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (DeleteChildNodesRequest commandRequest) {
    return new Builder(commandRequest);
  }

  private DeleteChildNodesRequest (Builder builder) {
    super(builder);
    this.childIds = builder.childIds;
    this.ignoreMappablePointRawPoint = builder.ignoreMappablePointRawPoint;
  }  

  public List<Integer> getChildIds() {
    return childIds;
  }
  
  public Boolean getIgnoreMappablePointRawPoint() {
    return ignoreMappablePointRawPoint;
  }

  @Override
  public String getOperationCategory() {
    return NodeHierarchyCommandRequest.BULK_NODE_OPERATION_CATEGORY;
  }
  
  @Override
  public String getOperationType() {
    return NodeHierarchyCommandRequest.DELETE_OPERATION_TYPE;
  }

  @Override
  public String toString() {
    
    return new StringBuilder()
        .append(super.toString())
        .append(", childIds=")
        .append(childIds)
        .append(", ignoreMappablePointRawPoint=")
        .append(ignoreMappablePointRawPoint)
        .append("]")
        .toString();
  }
  
  @JsonPOJOBuilder
  public static class Builder extends AbstractNodeHierarchyCommandRequest.Builder<DeleteChildNodesRequest, Builder> {
  
    private List<Integer> childIds;
    private Boolean ignoreMappablePointRawPoint = Boolean.FALSE;

    private Builder() {}

    private Builder(DeleteChildNodesRequest deleteChildNodesRequest) {
      requireNonNull(deleteChildNodesRequest, "deleteChildNodesRequest cannot be null");
      this.childIds = deleteChildNodesRequest.childIds;
      this.ignoreMappablePointRawPoint = deleteChildNodesRequest.ignoreMappablePointRawPoint;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }
    
    public Builder withChildIds(List<Integer> childIds) {
      requireNonNull(childIds, "childIds cannot be null");
      this.childIds = ImmutableList.copyOf(childIds);
      return this;
    }
    
    public Builder withIgnoreMappablePointRawPoint(Boolean ignoreMappablePointRawPoint) {
      requireNonNull(ignoreMappablePointRawPoint, "ignoreMappablePointRawPoint cannot be null");
      this.ignoreMappablePointRawPoint = ignoreMappablePointRawPoint;
      return this;
    }

    @Override
    protected Builder getThis() {
      return this;
    }

    @Override
    protected DeleteChildNodesRequest newInstance() {
      requireNonNull(childIds, "childIds cannot be null");
      requireNonNull(ignoreMappablePointRawPoint, "ignoreMappablePointRawPoint cannot be null");
      return new DeleteChildNodesRequest(this);
    }
  }
}
//@formatter:on