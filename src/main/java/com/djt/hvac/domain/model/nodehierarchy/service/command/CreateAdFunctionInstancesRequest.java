//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.service.command;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = CreateAdFunctionInstancesRequest.Builder.class)
public class CreateAdFunctionInstancesRequest extends AbstractNodeHierarchyCommandRequest {
  
  public static final String NODE_PATH_ALL = "*";
  public static final Integer EQUIPMENT_TYPE_ID_ALL = Integer.valueOf(-2);
  public static final Integer AD_FUNCTION_TEMPLATE_ID_ALL = Integer.valueOf(-2);
  
  private static final long serialVersionUID = 1L;
  
  private final String nodePath; // Used for filtering
  private final Integer equipmentTypeId; // Used for filtering
  private final Integer adFunctionTemplateId; // Used for filtering
  private final Integer equipmentId;
  private final String functionType;
  private final Map<Integer, Set<Integer>> candidateTemplateEquipmentIds;
  private final List<Integer> candidateIds;
  private final Integer fastTrackTimeSeriesDataAsyncOperationId; // Used for time-to-value orchestration of generating time-series data.

  @JsonCreator
  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder (CreateAdFunctionInstancesRequest commandRequest) {
    return new Builder(commandRequest);
  }
  
  private CreateAdFunctionInstancesRequest(Builder builder) {
    super(builder);
    this.nodePath = builder.nodePath;
    this.equipmentTypeId = builder.equipmentTypeId;
    this.adFunctionTemplateId = builder.adFunctionTemplateId;
    this.equipmentId = builder.equipmentId;
    this.functionType = builder.functionType;
    this.candidateTemplateEquipmentIds = builder.candidateTemplateEquipmentIds;
    this.candidateIds = builder.candidateIds;
    this.fastTrackTimeSeriesDataAsyncOperationId = builder.fastTrackTimeSeriesDataAsyncOperationId;
  }
  
  public String getNodePath() {
    return nodePath;
  }

  public Integer getEquipmentTypeId() {
    return equipmentTypeId;
  }

  public Integer getAdFunctionTemplateId() {
    return adFunctionTemplateId;
  }
  
  public Integer getEquipmentId() {
    return equipmentId;
  }
  
  public Map<Integer, Set<Integer>> getCandidateTemplateEquipmentIds() {
    return this.candidateTemplateEquipmentIds;
  }

  public List<Integer> getCandidateIds() {
    return candidateIds;
  }
  
  public Integer getFastTrackTimeSeriesDataAsyncOperationId() {
    return fastTrackTimeSeriesDataAsyncOperationId;
  }

  public String getFunctionType() {
    return functionType;
  }
  
  @Override
  public String getOperationCategory() {
    return NodeHierarchyCommandRequest.BULK_AD_FUNCTION_INSTANCE_OPERATION_CATEGORY; 
  }
  
  @Override
  public String getOperationType() {
    if (functionType.equals(NodeHierarchyCommandRequest.RULE)) {
      return NodeHierarchyCommandRequest.CREATE_AD_FUNCTION_INSTANCE_RULES_OPERATION_TYPE; 
    } else if (functionType.equals(NodeHierarchyCommandRequest.COMPUTED_POINT)) {
      return NodeHierarchyCommandRequest.CREATE_AD_FUNCTION_INSTANCE_COMPUTED_POINTS_OPERATION_TYPE;
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
        .append(", candidateTemplateEquipmentIds=")
        .append(candidateTemplateEquipmentIds)
        .append(", candidateIds=")
        .append(candidateIds)
        .append("]")
        .toString();
  }
  
  @JsonPOJOBuilder
  public static class Builder extends AbstractNodeHierarchyCommandRequest.Builder<CreateAdFunctionInstancesRequest, Builder> {
    
    private String nodePath = NODE_PATH_ALL;
    private Integer equipmentTypeId = EQUIPMENT_TYPE_ID_ALL;
    private Integer adFunctionTemplateId = AD_FUNCTION_TEMPLATE_ID_ALL;
    private Integer equipmentId;
    private String functionType;
    private Map<Integer, Set<Integer>> candidateTemplateEquipmentIds;
    private List<Integer> candidateIds;
    private Integer fastTrackTimeSeriesDataAsyncOperationId;
    
    private Builder () {}
    
    private Builder(CreateAdFunctionInstancesRequest request) {
      requireNonNull(request, "request cannot be null");
      this.nodePath = request.nodePath;
      this.equipmentTypeId = request.equipmentTypeId;
      this.adFunctionTemplateId = request.adFunctionTemplateId;
      this.equipmentId = request.equipmentId;
      this.functionType = request.functionType;
      this.candidateTemplateEquipmentIds = request.candidateTemplateEquipmentIds;
      this.candidateIds = request.candidateIds;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }
    
    public Builder withNodePath(String nodePath) {
      this.nodePath = nodePath;
      return this;
    }

    public Builder withEquipmentTypeId(Integer equipmentTypeId) {
      this.equipmentTypeId = equipmentTypeId;
      return this;
    }

    public Builder withAdFunctionTemplateId(Integer adFunctionTemplateId) {
      this.adFunctionTemplateId = adFunctionTemplateId;
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
    
    public Builder withCandidateTemplateEquipmentIds(Map<Integer, Set<Integer>> candidateTemplateEquipmentIds) {
      requireNonNull(candidateTemplateEquipmentIds, "candidateTemplateEquipmentIds cannot be null");
      this.candidateTemplateEquipmentIds = ImmutableMap.copyOf(candidateTemplateEquipmentIds);
      return this;
    }
    
    public Builder withCandidateIds(List<Integer> candidateIds) {
      requireNonNull(candidateIds, "candidateIds cannot be null");
      this.candidateIds = ImmutableList.copyOf(candidateIds);
      return this;
    }
    
    public Builder withFastTrackTimeSeriesDataAsyncOperationId(Integer fastTrackTimeSeriesDataAsyncOperationId) {
      this.fastTrackTimeSeriesDataAsyncOperationId = fastTrackTimeSeriesDataAsyncOperationId;
      return this;
    }
    
    @Override
    protected Builder getThis() {
      return this;
    }

    @Override
    protected CreateAdFunctionInstancesRequest newInstance() {
      requireNonNull(functionType, "functionType cannot be null");
      return new CreateAdFunctionInstancesRequest(this);
    }
  }
}
//@formatter:on