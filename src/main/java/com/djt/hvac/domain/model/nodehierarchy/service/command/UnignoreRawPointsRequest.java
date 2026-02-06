//@formatter:off
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
@JsonDeserialize(builder = UnignoreRawPointsRequest.Builder.class)
public class UnignoreRawPointsRequest extends AbstractNodeHierarchyCommandRequest {
  
  private static final long serialVersionUID = 1L;
  
  private final List<Integer> rawPoints;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (UnignoreRawPointsRequest commandRequest) {
    return new Builder(commandRequest);
  }

  private UnignoreRawPointsRequest(Builder builder) {
    super(builder);
    this.rawPoints = builder.rawPoints;
  }

  public List<Integer> getRawPoints() {
    return rawPoints;
  }
      
  @Override
  public String getOperationCategory() {
    return NodeHierarchyCommandRequest.BULK_POINT_MAPPING_OPERATION_CATEGORY;
  }
  
  @Override
  public String getOperationType() {
    return NodeHierarchyCommandRequest.UNIGNORE_RAW_POINT_OPERATION_TYPE;
  }
  
  @Override
  public String toString() {
    
    return new StringBuilder()
        .append(super.toString())
        .append(", rawPoints=")
        .append(rawPoints)
        .append("]")
        .toString();
  }
  
  @JsonPOJOBuilder
  public static class Builder extends AbstractNodeHierarchyCommandRequest.Builder<UnignoreRawPointsRequest, Builder> {
    
    private List<Integer> rawPoints;

    private Builder() {}

    private Builder(UnignoreRawPointsRequest request) {
      requireNonNull(request, "request cannot be null");
      this.rawPoints = request.rawPoints;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withRawPoints(List<Integer> rawPoints) {
      requireNonNull(rawPoints, "rawPoints cannot be null");
      this.rawPoints = ImmutableList.copyOf(rawPoints);
      return this;
    }

    @Override
    protected Builder getThis() {
      return this;
    }

    @Override
    protected UnignoreRawPointsRequest newInstance() {
      requireNonNull(rawPoints, "rawPoints cannot be null");
      return new UnignoreRawPointsRequest(this);
    }
  }
}
//@formatter:on