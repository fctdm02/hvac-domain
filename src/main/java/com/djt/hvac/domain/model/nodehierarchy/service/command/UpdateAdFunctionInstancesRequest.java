//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.service.command;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.function.Consumer;

import com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject.AdFunctionInstanceData;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.ImmutableList;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = UpdateAdFunctionInstancesRequest.Builder.class)
public class UpdateAdFunctionInstancesRequest extends AbstractNodeHierarchyCommandRequest {
  
  private static final long serialVersionUID = 1L;
  
  private final Integer equipmentId;
  private final String functionType;
  private final List<AdFunctionInstanceData> data;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (UpdateAdFunctionInstancesRequest commandRequest) {
    return new Builder(commandRequest);
  }

  private UpdateAdFunctionInstancesRequest (Builder builder) {
    super(builder);
    this.equipmentId = builder.equipmentId;
    this.functionType = builder.functionType;
    this.data = builder.data;
  }
  
  public Integer getEquipmentId() {
    return equipmentId;
  }
  
  public String getFunctionType() {
    return functionType;
  }

  public List<AdFunctionInstanceData> getData() {
    return data;
  }
      
  @Override
  public String getOperationCategory() {
    return NodeHierarchyCommandRequest.BULK_AD_FUNCTION_INSTANCE_OPERATION_CATEGORY; 
  }
  
  @Override
  public String getOperationType() {
    if (functionType.equals(NodeHierarchyCommandRequest.RULE)) {
      return NodeHierarchyCommandRequest.UPDATE_AD_FUNCTION_INSTANCE_RULES_OPERATION_TYPE; 
    } else if (functionType.equals(NodeHierarchyCommandRequest.COMPUTED_POINT)) {
      return NodeHierarchyCommandRequest.UPDATE_AD_FUNCTION_INSTANCE_COMPUTED_POINTS_OPERATION_TYPE;
    } else {
      throw new IllegalStateException("Unsupported operation type by functionType: ["
          + functionType
          + "]");
    }
  }
  
  @Override
  public String toString() {
    
    return new StringBuilder()
        .append(super.toString())
        .append(", equipmentId=")
        .append(equipmentId)
        .append(", functionType=")
        .append(functionType)
        .append(", data=")
        .append(data)
        .append("]")
        .toString();
  }
  
  @JsonPOJOBuilder
  public static class Builder extends AbstractNodeHierarchyCommandRequest.Builder<UpdateAdFunctionInstancesRequest, Builder> {
    
    private Integer equipmentId;
    private String functionType;
    private List<AdFunctionInstanceData> data;

    private Builder() {}

    private Builder(UpdateAdFunctionInstancesRequest request) {
      requireNonNull(request, "request cannot be null");
      this.equipmentId = request.equipmentId;
      this.functionType = request.functionType;      
      this.data = request.data;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withEquipmentId(Integer equipmentId) {
      this.equipmentId = equipmentId;
      return this;
    }
    
    public Builder withFunctionType(String functionType) {
      requireNonNull(functionType, "functionType cannot be null");
      if (functionType.equals(NodeHierarchyCommandRequest.RULE) 
          && functionType.equals(NodeHierarchyCommandRequest.COMPUTED_POINT)) {
        throw new IllegalArgumentException("Function type: ["
            + functionType
            + "] must be one of: [RULE or COMPUTED_POINT]");
      }
      this.functionType = functionType;
      return this;
    }

    public Builder withData(List<AdFunctionInstanceData> data) {
      requireNonNull(data, "data cannot be null");
      this.data = ImmutableList.copyOf(data);
      return this;
    }
    
    @Override
    protected Builder getThis() {
      return this;
    }

    @Override
    protected UpdateAdFunctionInstancesRequest newInstance() {
      requireNonNull(functionType, "functionType cannot be null");
      requireNonNull(data, "data cannot be null");
      return new UpdateAdFunctionInstancesRequest(this);
    }
  }
}
//@formatter:on