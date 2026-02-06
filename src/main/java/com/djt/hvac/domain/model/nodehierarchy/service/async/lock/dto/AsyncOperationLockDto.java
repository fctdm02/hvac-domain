package com.djt.hvac.domain.model.nodehierarchy.service.async.lock.dto;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = AsyncOperationLockDto.Builder.class)
public class AsyncOperationLockDto {
  private final Integer customerId;
  private final String ownerUuid;
  private final String operationCategory;
  private final String operationType;
  private final String submittedBy;
  private final String lockAcquisitionTime;
  private final String lockUpdateTime;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (AsyncOperationLockDto asyncOperationLockDto) {
    return new Builder(asyncOperationLockDto);
  }

  private AsyncOperationLockDto (Builder builder) {
    this.customerId = builder.customerId;
    this.ownerUuid = builder.ownerUuid;
    this.operationCategory = builder.operationCategory;
    this.operationType = builder.operationType;
    this.submittedBy = builder.submittedBy;
    this.lockAcquisitionTime = builder.lockAcquisitionTime;
    this.lockUpdateTime = builder.lockUpdateTime;
  }

  public Integer getCustomerId() {
    return customerId;
  }

  public String getOwnerUuid() {
    return ownerUuid;
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

  public String getLockAcquisitionTime() {
    return lockAcquisitionTime;
  }

  public String getLockUpdateTime() {
    return lockUpdateTime;
  }

  @JsonPOJOBuilder
  public static class Builder {
    private Integer customerId;
    private String ownerUuid;
    private String operationCategory;
    private String operationType;
    private String submittedBy;
    private String lockAcquisitionTime;
    private String lockUpdateTime;

    private Builder() {}

    private Builder(AsyncOperationLockDto asyncOperationLockDto) {
      requireNonNull(asyncOperationLockDto, "asyncOperationLockDto cannot be null");
      this.customerId = asyncOperationLockDto.customerId;
      this.ownerUuid = asyncOperationLockDto.ownerUuid;
      this.operationCategory = asyncOperationLockDto.operationCategory;
      this.operationType = asyncOperationLockDto.operationType;
      this.submittedBy = asyncOperationLockDto.submittedBy;
      this.lockAcquisitionTime = asyncOperationLockDto.lockAcquisitionTime;
      this.lockUpdateTime = asyncOperationLockDto.lockUpdateTime;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withCustomerId(Integer customerId) {
      requireNonNull(customerId, "customerId cannot be null");
      this.customerId = customerId;
      return this;
    }

    public Builder withOwnerUuid(String ownerUuid) {
      requireNonNull(ownerUuid, "ownerUuid cannot be null");
      this.ownerUuid = ownerUuid;
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

    public Builder withLockAcquisitionTime(String lockAcquisitionTime) {
      requireNonNull(lockAcquisitionTime, "lockAcquisitionTime cannot be null");
      this.lockAcquisitionTime = lockAcquisitionTime;
      return this;
    }

    public Builder withLockUpdateTime(String lockUpdateTime) {
      requireNonNull(lockUpdateTime, "lockUpdateTime cannot be null");
      this.lockUpdateTime = lockUpdateTime;
      return this;
    }

    public AsyncOperationLockDto build() {
      requireNonNull(customerId, "customerId cannot be null");
      requireNonNull(ownerUuid, "ownerUuid cannot be null");
      requireNonNull(submittedBy, "submittedBy cannot be null");
      requireNonNull(lockAcquisitionTime, "lockAcquisitionTime cannot be null");
      requireNonNull(lockUpdateTime, "lockUpdateTime cannot be null");
      return new AsyncOperationLockDto(this);
    }
  }
}