//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.service.command;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.function.Consumer;

import com.djt.hvac.domain.model.dictionary.enums.FunctionType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = FindAdFunctionInstanceCandidatesRequest.Builder.class)
public class FindAdFunctionInstanceCandidatesRequest extends AbstractNodeHierarchyCommandRequest {
  
  private static final long serialVersionUID = 1L;
  
  private final FunctionType functionType;
  private final List<Integer> buildingIds;

  @JsonCreator
  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder (FindAdFunctionInstanceCandidatesRequest commandRequest) {
    return new Builder(commandRequest);
  }
  
  private FindAdFunctionInstanceCandidatesRequest(Builder builder) {
    super(builder);
    this.functionType = builder.functionType;
    this.buildingIds = builder.buildingIds;
  }
  
  public FunctionType getFunctionType() {
    return functionType;
  }
  
  public List<Integer> getBuildingIds() {
    return this.buildingIds;
  }
  
  @Override
  public String getOperationCategory() {
    return NodeHierarchyCommandRequest.BULK_AD_FUNCTION_INSTANCE_OPERATION_CATEGORY; 
  }
  
  @Override
  public String getOperationType() {
    if (functionType.equals(FunctionType.RULE)) {
      return NodeHierarchyCommandRequest.FIND_AD_FUNCTION_CANDIDATE_RULES_OPERATION_TYPE; 
    } else if (functionType.equals(FunctionType.COMPUTED_POINT)) {
      return NodeHierarchyCommandRequest.FIND_AD_FUNCTION_CANDIDATE_COMPUTED_POINTS_OPERATION_TYPE;
    } else {
      throw new RuntimeException("Unsupported operation category");
    }
  }  
  
  @Override
  public String toString() {
    
    return new StringBuilder()
        .append(super.toString())
        .append(", functionType=")
        .append(functionType)
        .append(", buildingIds=")
        .append(buildingIds)
        .append("]")
        .toString();
  }
  
  @JsonPOJOBuilder
  public static class Builder extends AbstractNodeHierarchyCommandRequest.Builder<FindAdFunctionInstanceCandidatesRequest, Builder> {
    
    private FunctionType functionType;
    private List<Integer> buildingIds;

    private Builder() {}

    private Builder(FindAdFunctionInstanceCandidatesRequest findAdFunctionInstanceCandidatesRequest) {
      requireNonNull(findAdFunctionInstanceCandidatesRequest, "findAdFunctionInstanceCandidatesRequest cannot be null");
      this.functionType = findAdFunctionInstanceCandidatesRequest.functionType;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withFunctionType(FunctionType functionType) {
      requireNonNull(functionType, "functionType cannot be null");
      this.functionType = functionType;
      return this;
    }

    public Builder withBuildingIds(List<Integer> buildingIds) {
      requireNonNull(buildingIds, "buildingIds cannot be null");
      this.buildingIds = buildingIds;
      return this;
    }
    
    @Override
    protected Builder getThis() {
      return this;
    }

    @Override
    protected FindAdFunctionInstanceCandidatesRequest newInstance() {
      requireNonNull(functionType, "functionType cannot be null");
      return new FindAdFunctionInstanceCandidatesRequest(this);
    }
  }
}
//@formatter:on