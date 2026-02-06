package com.djt.hvac.domain.model.nodehierarchy.service.command;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = PerformPortfolioMaintenanceRequest.Builder.class)
public class PerformPortfolioMaintenanceRequest extends AbstractNodeHierarchyCommandRequest {
  
  private static final long serialVersionUID = 1L;
  
  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (PerformPortfolioMaintenanceRequest commandRequest) {
    return new Builder(commandRequest);
  }

  private PerformPortfolioMaintenanceRequest (Builder builder) {
    super(builder);
  }

  @Override
  public String getOperationCategory() {
    return NodeHierarchyCommandRequest.BULK_PORTFOLIO_OPERATION_CATEGORY;
  }
  
  @Override
  public String getOperationType() {
    return NodeHierarchyCommandRequest.MAINTENANCE_OPERATION_TYPE;
  }  
  
  @Override
  public String toString() {
    
    return new StringBuilder()
        .append(super.toString())
        .append("]")
        .toString();
  }
  
  @JsonPOJOBuilder
  public static class Builder extends AbstractNodeHierarchyCommandRequest.Builder<PerformPortfolioMaintenanceRequest, Builder> {
    
    private Builder() {}

    private Builder(PerformPortfolioMaintenanceRequest commandRequest) {
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
    protected PerformPortfolioMaintenanceRequest newInstance() {
      return new PerformPortfolioMaintenanceRequest(this);
    }
  }
}