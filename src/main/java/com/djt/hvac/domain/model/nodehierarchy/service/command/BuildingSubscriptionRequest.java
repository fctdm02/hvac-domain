package com.djt.hvac.domain.model.nodehierarchy.service.command;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = BuildingSubscriptionRequest.Builder.class)
public class BuildingSubscriptionRequest extends AbstractNodeHierarchyCommandRequest {
  
  public static final String CREATE_BUILDING_SUBSCRIPTION = "CREATE_BUILDING_SUBSCRIPTION";
  public static final String CANCEL_BUILDING_SUBSCRIPTION = "CANCEL_BUILDING_SUBSCRIPTION";
  public static final String UPDATE_BUILDING_SUBSCRIPTION_FOR_NEW_PAYMENT_METHOD = "UPDATE_BUILDING_SUBSCRIPTION_FOR_NEW_PAYMENT_METHOD";
  public static final String UPDATE_BUILDING_SUBSCRIPTION_FOR_NEW_PAYMENT_PLAN_SAME_INTERVAL = "UPDATE_BUILDING_SUBSCRIPTION_FOR_NEW_PAYMENT_PLAN_SAME_INTERVAL";
  public static final String UPDATE_BUILDING_SUBSCRIPTION_FOR_NEW_PAYMENT_PLAN_DIFFERENT_INTERVAL = "UPDATE_BUILDING_SUBSCRIPTION_FOR_NEW_PAYMENT_PLAN_DIFFERENT_INTERVAL";
  
  private static final long serialVersionUID = 1L;
  
  private final String operationSubType;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (BuildingSubscriptionRequest commandRequest) {
    return new Builder(commandRequest);
  }

  private BuildingSubscriptionRequest (Builder builder) {
    super(builder);
    this.operationSubType = builder.operationSubType;
  }
  public String getOperationSubType() {
    return operationSubType;
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
        .append(", operationSubType=")
        .append(operationSubType)
        .append("]")
        .toString();
  }
  
  @JsonPOJOBuilder
  public static class Builder extends AbstractNodeHierarchyCommandRequest.Builder<BuildingSubscriptionRequest, Builder> {
    
    private String operationSubType;

    private Builder() {}

    private Builder(BuildingSubscriptionRequest commandRequest) {
      requireNonNull(commandRequest, "commandRequest cannot be null");
      this.operationSubType = commandRequest.operationSubType;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withOperationSubType(String operationSubType) {
      requireNonNull(operationSubType, "operationSubType cannot be null");
      this.operationSubType = operationSubType;
      return this;
    }

    @Override
    protected Builder getThis() {
      return this;
    }

    @Override
    protected BuildingSubscriptionRequest newInstance() {
      requireNonNull(operationSubType, "operationSubType cannot be null");
      return new BuildingSubscriptionRequest(this);
    }
  }
}