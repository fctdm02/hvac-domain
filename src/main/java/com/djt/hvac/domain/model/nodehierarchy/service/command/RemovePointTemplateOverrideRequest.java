package com.djt.hvac.domain.model.nodehierarchy.service.command;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

import com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject.PointTemplateUnitMappingOverride;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = RemovePointTemplateOverrideRequest.Builder.class)
public class RemovePointTemplateOverrideRequest extends AbstractNodeHierarchyCommandRequest {
  
  private static final long serialVersionUID = 1L;
  
  private final PointTemplateUnitMappingOverride pointTemplateUnitMappingOverride;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (RemovePointTemplateOverrideRequest commandRequest) {
    return new Builder(commandRequest);
  }

  private RemovePointTemplateOverrideRequest (Builder builder) {
    super(builder);
    this.pointTemplateUnitMappingOverride = builder.pointTemplateUnitMappingOverride;
  }

  public PointTemplateUnitMappingOverride getPointTemplateUnitMappingOverride() {
    return pointTemplateUnitMappingOverride;
  }
      
  @Override
  public String getOperationCategory() {
    return NodeHierarchyCommandRequest.BULK_PORTFOLIO_OPERATION_CATEGORY;
  }
  
  @Override
  public String getOperationType() {
    return NodeHierarchyCommandRequest.DELETE_OPERATION_TYPE;
  }

  @Override
  public String toString() {
    
    return new StringBuilder()
        .append(super.toString())
        .append(", pointTemplateUnitMappingOverride=")
        .append(pointTemplateUnitMappingOverride)
        .append("]")
        .toString();
  }
  
  @JsonPOJOBuilder
  public static class Builder extends AbstractNodeHierarchyCommandRequest.Builder<RemovePointTemplateOverrideRequest, Builder> {
    
    private PointTemplateUnitMappingOverride pointTemplateUnitMappingOverride;

    private Builder() {}

    private Builder(RemovePointTemplateOverrideRequest request) {
      requireNonNull(request, "request cannot be null");
      this.pointTemplateUnitMappingOverride = request.pointTemplateUnitMappingOverride;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withPointTemplateUnitMappingOverride(PointTemplateUnitMappingOverride pointTemplateUnitMappingOverride) {
      requireNonNull(pointTemplateUnitMappingOverride, "pointTemplateUnitMappingOverride cannot be null");
      this.pointTemplateUnitMappingOverride = pointTemplateUnitMappingOverride;
      return this;
    }

    @Override
    protected Builder getThis() {
      return this;
    }

    @Override
    protected RemovePointTemplateOverrideRequest newInstance() {
      requireNonNull(pointTemplateUnitMappingOverride, "pointTemplateUnitMappingOverride cannot be null");
      return new RemovePointTemplateOverrideRequest(this);
    }
  }
}