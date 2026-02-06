package com.djt.hvac.domain.model.nodehierarchy.service.command;

import static java.util.Objects.requireNonNull;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.ImmutableSet;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = ValidatePortfolioRequest.Builder.class)
public class ValidatePortfolioRequest extends AbstractNodeHierarchyCommandRequest {
  
  private static final long serialVersionUID = 1L;
  
  private final Set<IssueType> issueTypes;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (ValidatePortfolioRequest commandRequest) {
    return new Builder(commandRequest);
  }

  private ValidatePortfolioRequest (Builder builder) {
    super(builder);
    
    if (builder.issueTypes != null) {
      
      this.issueTypes = builder.issueTypes;
      
    } else {
      
      issueTypes = new HashSet<>();
      issueTypes.addAll(ValidationMessage.extractPhaseOneIssueTypes());
      issueTypes.addAll(ValidationMessage.extractPhaseTwoIssueTypes());
    }
  }
  
  public Set<IssueType> getIssueTypes() {
    return issueTypes;
  }

  @Override
  public String getOperationCategory() {
    return NodeHierarchyCommandRequest.BULK_PORTFOLIO_OPERATION_CATEGORY;
  }
  
  @Override
  public String getOperationType() {
    return NodeHierarchyCommandRequest.VALIDATE_OPERATION_TYPE;
  }  
  
  @Override
  public String toString() {
    
    return new StringBuilder()
        .append(super.toString())
        .append(", issueTypes=")
        .append(issueTypes)
        .append("]")
        .toString();
  }  
  
  @JsonPOJOBuilder
  public static class Builder extends AbstractNodeHierarchyCommandRequest.Builder<ValidatePortfolioRequest, Builder> {
    
    private Set<IssueType> issueTypes;

    private Builder() {}

    private Builder(ValidatePortfolioRequest commandRequest) {
      requireNonNull(commandRequest, "commandRequest cannot be null");
      this.issueTypes = commandRequest.issueTypes;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withIssueTypes(Set<IssueType> issueTypes) {
      requireNonNull(issueTypes, "issueTypes cannot be null");
      this.issueTypes = ImmutableSet.copyOf(issueTypes);
      return this;
    }

    @Override
    protected Builder getThis() {
      return this;
    }

    @Override
    protected ValidatePortfolioRequest newInstance() {
      return new ValidatePortfolioRequest(this);
    }
  }
}