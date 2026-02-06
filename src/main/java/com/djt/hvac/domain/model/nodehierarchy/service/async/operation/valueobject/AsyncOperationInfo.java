//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.service.async.operation.valueobject;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = AsyncOperationInfo.Builder.class)
public class AsyncOperationInfo implements Comparable<AsyncOperationInfo> {
  
  private final Integer id;
  private final Integer customerId;
  private final String operationCategory;
  private final String operationType;
  private final String status;
  private final String reason;
  private final String submittedBy;
  private final String submissionTime;
  private final String completionTime;
  private Boolean isComplete = Boolean.FALSE;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (AsyncOperationInfo asyncOperationInfo) {
    return new Builder(asyncOperationInfo);
  }

  private AsyncOperationInfo (Builder builder) {
    this.id = builder.id;
    this.customerId = builder.customerId;
    this.operationCategory = builder.operationCategory;
    this.operationType = builder.operationType;
    this.status = builder.status;
    this.reason = builder.reason;
    this.submittedBy = builder.submittedBy;
    this.submissionTime = builder.submissionTime;
    this.completionTime = builder.completionTime;
    if (builder.isComplete != null) {
      this.isComplete = builder.isComplete;  
    }
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

  public String getStatus() {
    return status;
  }

  public String getReason() {
    return reason;
  }
  
  public String getSubmittedBy() {
    return submittedBy;
  }

  public String getSubmissionTime() {
    return submissionTime;
  }
  
  public String getCompletionTime() {
    return completionTime;
  }
  
  public void setIsComplete(Boolean isComplete) {
    this.isComplete = isComplete;
  }
  
  public Boolean getIsComplete() {
    return isComplete;
  }

  @Override
  public int compareTo(AsyncOperationInfo that) {
    return this.id.compareTo(that.id);
  }
  
  @JsonPOJOBuilder
  public static class Builder {
    private Integer id;
    private Integer customerId;
    private String operationCategory;
    private String operationType;
    private String status;
    private String reason;
    private String submittedBy;
    private String submissionTime;
    private String completionTime;
    private Boolean isComplete;

    private Builder() {}

    private Builder(AsyncOperationInfo asyncOperationInfo) {
      requireNonNull(asyncOperationInfo, "asyncOperationStatus cannot be null");
      this.id = asyncOperationInfo.id;
      this.customerId = asyncOperationInfo.customerId;
      this.operationCategory = asyncOperationInfo.operationCategory;
      this.operationType = asyncOperationInfo.operationType;
      this.status = asyncOperationInfo.status;
      this.reason = asyncOperationInfo.reason;
      this.submittedBy = asyncOperationInfo.submittedBy;
      this.submissionTime = asyncOperationInfo.submissionTime;
      this.isComplete = asyncOperationInfo.isComplete;
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

    public Builder withStatus(String status) {
      requireNonNull(status, "status cannot be null");
      this.status = status;
      return this;
    }

    public Builder withReason(String reason) {
      this.reason = reason;
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

    public Builder withCompletionTime(String completionTime) {
      requireNonNull(completionTime, "completionTime cannot be null");
      this.completionTime = completionTime;
      return this;
    }
    
    public Builder withIsComplete(Boolean isComplete) {
      requireNonNull(isComplete, "isComplete cannot be null");
      this.isComplete = isComplete;
      return this;
    }
    
    public AsyncOperationInfo build() {
      requireNonNull(id, "id cannot be null");
      requireNonNull(customerId, "customerId cannot be null");
      requireNonNull(operationCategory, "operationCategory cannot be null");
      requireNonNull(operationType, "operationType cannot be null");
      requireNonNull(status, "status cannot be null");
      requireNonNull(submittedBy, "submittedBy cannot be null");
      requireNonNull(submissionTime, "submissionTime cannot be null");
      return new AsyncOperationInfo(this);
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((customerId == null) ? 0 : customerId.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((isComplete == null) ? 0 : isComplete.hashCode());
    result = prime * result + ((operationCategory == null) ? 0 : operationCategory.hashCode());
    result = prime * result + ((operationType == null) ? 0 : operationType.hashCode());
    result = prime * result + ((status == null) ? 0 : status.hashCode());
    result = prime * result + ((submissionTime == null) ? 0 : submissionTime.hashCode());
    result = prime * result + ((submittedBy == null) ? 0 : submittedBy.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    AsyncOperationInfo other = (AsyncOperationInfo) obj;
    if (customerId == null) {
      if (other.customerId != null)
        return false;
    } else if (!customerId.equals(other.customerId))
      return false;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (isComplete == null) {
      if (other.isComplete != null)
        return false;
    } else if (!isComplete.equals(other.isComplete))
      return false;
    if (operationCategory == null) {
      if (other.operationCategory != null)
        return false;
    } else if (!operationCategory.equals(other.operationCategory))
      return false;
    if (operationType == null) {
      if (other.operationType != null)
        return false;
    } else if (!operationType.equals(other.operationType))
      return false;
    if (status == null) {
      if (other.status != null)
        return false;
    } else if (!status.equals(other.status))
      return false;
    if (submissionTime == null) {
      if (other.submissionTime != null)
        return false;
    } else if (!submissionTime.equals(other.submissionTime))
      return false;
    if (submittedBy == null) {
      if (other.submittedBy != null)
        return false;
    } else if (!submittedBy.equals(other.submittedBy))
      return false;
    return true;
  }
  
  @Override
  public String toString() {
    return "AsyncOperationInfo [id=" + id + ", customerId=" + customerId
        + ", operationCategory=" + operationCategory + ", operationType=" + operationType
        + ", status=" + status + ", submittedBy=" + submittedBy + ", submissionTime="
        + submissionTime + ", isComplete=" + isComplete + "]";
  }
}
//@formatter:on