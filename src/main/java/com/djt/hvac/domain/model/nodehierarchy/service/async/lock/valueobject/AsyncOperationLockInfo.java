package com.djt.hvac.domain.model.nodehierarchy.service.async.lock.valueobject;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = AsyncOperationLockInfo.Builder.class)
public class AsyncOperationLockInfo {
  private final Integer lockedEntityId;
  private final String operationCategory;
  private final String operationType;
  private final String submittedBy;
  private final Boolean loggedInUserHasLock;
  private final Integer lockAgeInSeconds;
  private final Integer lockIdleTimeInSeconds;
  private final Boolean lockExpired;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (AsyncOperationLockInfo asyncOperationLockInfo) {
    return new Builder(asyncOperationLockInfo);
  }

  private AsyncOperationLockInfo (Builder builder) {
    this.lockedEntityId = builder.lockedEntityId;
    this.operationCategory = builder.operationCategory;
    this.operationType = builder.operationType;
    this.submittedBy = builder.submittedBy;
    this.loggedInUserHasLock = builder.loggedInUserHasLock;
    this.lockAgeInSeconds = builder.lockAgeInSeconds;
    this.lockIdleTimeInSeconds = builder.lockIdleTimeInSeconds;
    this.lockExpired = builder.lockExpired;
  }

  public Integer getLockedEntityId() {
    return lockedEntityId;
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
  
  public Boolean getLoggedInUserHasLock() {
    return loggedInUserHasLock;
  }

  public Integer getLockAgeInSeconds() {
    return lockAgeInSeconds;
  }
  
  public Integer getLockIdleTimeInSeconds() {
    return lockIdleTimeInSeconds;
  }
  
  public Boolean getLockExpired() {
    return lockExpired;
  }

  @Override
  public int hashCode() {
    return lockedEntityId.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    
    return this.lockedEntityId.equals(((AsyncOperationLockInfo)obj).lockedEntityId);
  }
  
  @Override
  public String toString() {
    return new StringBuilder()
        .append("[lockedEntityId=")
        .append(lockedEntityId)
        .append(", operationCategory=")
        .append(operationCategory)
        .append(", operationType=")
        .append(operationType)
        .append(", submittedBy=")
        .append(submittedBy)
        .append(", lockAgeInSeconds=")
        .append(lockAgeInSeconds)
        .append(", lockIdleTimeInSeconds=")
        .append(lockIdleTimeInSeconds)
        .append(", lockExpired=")
        .append(lockExpired)
        .append("]")
        .toString();
  }
  
  @JsonPOJOBuilder
  public static class Builder {
    private Integer lockedEntityId;
    private String operationCategory;
    private String operationType;
    private String submittedBy;
    private Boolean loggedInUserHasLock = Boolean.FALSE;
    private Integer lockAgeInSeconds;
    private Integer lockIdleTimeInSeconds;
    private Boolean lockExpired = Boolean.FALSE;

    private Builder() {}

    private Builder(AsyncOperationLockInfo asyncOperationLockInfo) {
      requireNonNull(asyncOperationLockInfo, "asyncOperationLockInfo cannot be null");
      this.lockedEntityId = asyncOperationLockInfo.lockedEntityId;
      this.operationCategory = asyncOperationLockInfo.operationCategory;
      this.operationType = asyncOperationLockInfo.operationType;
      this.submittedBy = asyncOperationLockInfo.submittedBy;
      this.loggedInUserHasLock = asyncOperationLockInfo.loggedInUserHasLock;
      this.lockAgeInSeconds = asyncOperationLockInfo.lockAgeInSeconds;
      this.lockIdleTimeInSeconds = asyncOperationLockInfo.lockIdleTimeInSeconds;
      this.lockExpired = asyncOperationLockInfo.lockExpired;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withLockedEntityId(Integer lockedEntityId) {
      requireNonNull(lockedEntityId, "lockedEntityId cannot be null");
      this.lockedEntityId = lockedEntityId;
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

    public Builder withLoggedInUserHasLock(Boolean loggedInUserHasLock) {
      requireNonNull(loggedInUserHasLock, "loggedInUserHasLock cannot be null");
      this.loggedInUserHasLock = loggedInUserHasLock;
      return this;
    }

    public Builder withLockAgeInSeconds(Integer lockAgeInSeconds) {
      requireNonNull(lockAgeInSeconds, "lockAgeInSeconds cannot be null");
      this.lockAgeInSeconds = lockAgeInSeconds;
      return this;
    }

    public Builder withLockIdleTimeInSeconds(Integer lockIdleTimeInSeconds) {
      requireNonNull(lockIdleTimeInSeconds, "lockIdleTimeInSeconds cannot be null");
      this.lockIdleTimeInSeconds = lockIdleTimeInSeconds;
      return this;
    }

    public Builder withLockExpired(Boolean lockExpired) {
      requireNonNull(lockExpired, "lockExpired cannot be null");
      this.lockExpired = lockExpired;
      return this;
    }
    
    public AsyncOperationLockInfo build() {
      requireNonNull(lockedEntityId, "lockedEntityId cannot be null");
      requireNonNull(operationCategory, "operationCategory cannot be null");
      requireNonNull(operationType, "operationType cannot be null");
      requireNonNull(submittedBy, "submittedBy cannot be null");
      requireNonNull(lockAgeInSeconds, "lockAgeInSeconds cannot be null");
      requireNonNull(lockIdleTimeInSeconds, "lockIdleTimeInSeconds cannot be null");
      return new AsyncOperationLockInfo(this);
    }
  }
}
