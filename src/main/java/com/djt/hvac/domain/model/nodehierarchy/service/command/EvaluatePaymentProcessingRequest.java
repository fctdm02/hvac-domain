package com.djt.hvac.domain.model.nodehierarchy.service.command;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = EvaluatePaymentProcessingRequest.Builder.class)
public class EvaluatePaymentProcessingRequest extends AbstractNodeHierarchyCommandRequest {
  
  private static final long serialVersionUID = 1L;
  
  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (EvaluatePaymentProcessingRequest commandRequest) {
    return new Builder(commandRequest);
  }

  private EvaluatePaymentProcessingRequest (Builder builder) {
    super(builder);
  }

  @Override
  public String getOperationCategory() {
    return NodeHierarchyCommandRequest.BULK_PORTFOLIO_OPERATION_CATEGORY;
  }
  
  @Override
  public String getOperationType() {
    return NodeHierarchyCommandRequest.EVALUATE_PAYMENT_PROCESSING_OPERATION_TYPE;
  }  
  
  @Override
  public String toString() {
    
    return new StringBuilder()
        .append(super.toString())
        .append("]")
        .toString();
  }
  
  @JsonPOJOBuilder
  public static class Builder extends AbstractNodeHierarchyCommandRequest.Builder<EvaluatePaymentProcessingRequest, Builder> {
    
    private Builder() {}

    private Builder(EvaluatePaymentProcessingRequest commandRequest) {
      requireNonNull(commandRequest, "commandRequest cannot be null");
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    @Override
    protected Builder getThis() {
      return this;
    }

    @Override
    protected EvaluatePaymentProcessingRequest newInstance() {
      return new EvaluatePaymentProcessingRequest(this);
    }
  }
}