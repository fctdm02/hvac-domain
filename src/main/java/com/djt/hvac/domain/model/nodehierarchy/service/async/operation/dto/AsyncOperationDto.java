package com.djt.hvac.domain.model.nodehierarchy.service.async.operation.dto;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = AsyncOperationDto.Builder.class)
public class AsyncOperationDto {
  private final Integer id;
  private final Integer customerId;
  private final String operationCategory;
  private final String operationType;
  private final String submittedBy;
  private final String submissionTime;
  private final String status;
  private final String reason;
  private final String requestJson;
  private final String endTime;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (AsyncOperationDto asyncOperationDto) {
    return new Builder(asyncOperationDto);
  }

  private AsyncOperationDto (Builder builder) {
    this.id = builder.id;
    this.customerId = builder.customerId;
    this.operationCategory = builder.operationCategory;
    this.operationType = builder.operationType;
    this.submittedBy = builder.submittedBy;
    this.submissionTime = builder.submissionTime;
    this.status = builder.status;
    this.reason = builder.reason;
    this.requestJson = builder.requestJson;
    this.endTime = builder.endTime;
  }

  public Integer getId() {
    return id;
  }
  
  public Integer getCustomerId() {
    return customerId;
  }

  public String getOperationCategory() {
    return operationCategory;
  }

  public String getOperationType() {
    return operationType;
  }

  public String getSubmittedBy() {
    return submittedBy;
  }

  public String getSubmissionTime() {
    return submissionTime;
  }
  
  public String getStatus() {
    return status;
  }

  public String getReason() {
    return reason;
  }
  
  public String getRequestJson() {
    return requestJson;
  }

  public String getEndTime() {
    return endTime;
  }

  @JsonPOJOBuilder
  public static class Builder {
    private Integer id;
    private Integer customerId;
    private String operationCategory;
    private String operationType;
    private String submittedBy;
    private String submissionTime;
    private String status;
    private String reason;
    private String requestJson;
    private String endTime;

    private Builder() {}

    private Builder(AsyncOperationDto asyncOperationDto) {
      requireNonNull(asyncOperationDto, "asyncOperationDto cannot be null");
      this.id = asyncOperationDto.id;
      this.customerId = asyncOperationDto.customerId;
      this.operationCategory = asyncOperationDto.operationCategory;
      this.operationType = asyncOperationDto.operationType;
      this.submittedBy = asyncOperationDto.submittedBy;
      this.submissionTime = asyncOperationDto.submissionTime;
      this.status = asyncOperationDto.status;
      this.reason = asyncOperationDto.reason;
      this.requestJson = asyncOperationDto.requestJson;
      this.endTime = asyncOperationDto.endTime;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withId(Integer id) {
      requireNonNull(id, "id cannot be null");
      this.id = id;
      return this;
    }
    
    public Builder withCustomerId(Integer customerId) {
      requireNonNull(customerId, "customerId cannot be null");
      this.customerId = customerId;
      return this;
    }

    public Builder withOperationCategory(String operationCategory) {
      requireNonNull(operationCategory, "operationCategory cannot be null");
      this.operationCategory = operationCategory;
      return this;
    }

    public Builder withOperationType(String operationType) {
      requireNonNull(operationType, "operationType cannot be null");
      this.operationType = operationType;
      return this;
    }

    public Builder withSubmittedBy(String submittedBy) {
      requireNonNull(submittedBy, "submittedBy cannot be null");
      this.submittedBy = submittedBy;
      return this;
    }

    public Builder withSubmissionTime(String submissionTime) {
      requireNonNull(submissionTime, "submissionTime cannot be null");
      this.submissionTime = submissionTime;
      return this;
    }

    public Builder withStatus(String status) {
      requireNonNull(status, "status cannot be null");
      this.status = status;
      return this;
    }

    public Builder withReason(String reason) {
      this.reason = reason;
      return this;
    }
    
    public Builder withRequestJson(String requestJson) {
      requireNonNull(requestJson, "requestJson cannot be null");
      this.requestJson = requestJson;
      return this;
    }

    public Builder withEndTime(String endTime) {
      this.endTime = endTime;
      return this;
    }

    public AsyncOperationDto build() {
      requireNonNull(id, "id cannot be null");
      requireNonNull(customerId, "customerId cannot be null");
      requireNonNull(operationCategory, "operationCategory cannot be null");
      requireNonNull(operationType, "operationType cannot be null");
      requireNonNull(submittedBy, "submittedBy cannot be null");
      requireNonNull(submissionTime, "submissionTime cannot be null");
      requireNonNull(status, "status cannot be null");
      requireNonNull(requestJson, "requestJson cannot be null");
      return new AsyncOperationDto(this);
    }
  }
}