//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.service.command;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = EvaluateReportsRequest.Builder.class)
public class EvaluateReportsRequest extends AbstractNodeHierarchyCommandRequest {
  
  private static final long serialVersionUID = 1L;
  
  private final Integer reportTemplateId;

  @JsonCreator
  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder (EvaluateReportsRequest commandRequest) {
    return new Builder(commandRequest);
  }
  
  private EvaluateReportsRequest(Builder builder) {
    super(builder);
    this.reportTemplateId = builder.reportTemplateId;
  }

  public Integer getReportTemplateId() {
    return reportTemplateId;
  }

  @Override
  public String getOperationCategory() {
    return NodeHierarchyCommandRequest.BULK_PORTFOLIO_OPERATION_CATEGORY;
  }
  
  @Override
  public String getOperationType() {
    return NodeHierarchyCommandRequest.EVALUATE_REPORTS_OPERATION_TYPE;
  }
  
  @Override
  public String toString() {
    
    return new StringBuilder()
        .append(super.toString())
        .append(", reportTemplateId=")
        .append(reportTemplateId)
        .append("]")
        .toString();
  }
  
  @JsonPOJOBuilder
  public static class Builder extends AbstractNodeHierarchyCommandRequest.Builder<EvaluateReportsRequest, Builder> {
    
    private Integer reportTemplateId;

    private Builder() {}

    private Builder(EvaluateReportsRequest evaluateReportsRequest) {
      requireNonNull(evaluateReportsRequest, "evaluateReportsRequest cannot be null");
      this.reportTemplateId = evaluateReportsRequest.reportTemplateId;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withReportTemplateId(Integer reportTemplateId) {
      this.reportTemplateId = reportTemplateId;
      return this;
    }

    @Override
    protected Builder getThis() {
      return this;
    }

    @Override
    protected EvaluateReportsRequest newInstance() {
      return new EvaluateReportsRequest(this);
    }
  }
}
//@formatter:on