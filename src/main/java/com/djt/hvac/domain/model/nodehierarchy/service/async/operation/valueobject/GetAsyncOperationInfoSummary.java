//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.service.async.operation.valueobject;

import static java.util.Objects.requireNonNull;
import java.util.Collections;
import java.util.function.Consumer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.List;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = GetAsyncOperationInfoSummary.Builder.class)
public class GetAsyncOperationInfoSummary {
  
  private final Integer numberOpenJobs;
  private final Integer numberCompleteJobs;
  private final Integer totalNumberJobs;
  private final List<AsyncOperationInfo> statuses;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (GetAsyncOperationInfoSummary getAsyncOperationInfoSummary) {
    return new Builder(getAsyncOperationInfoSummary);
  }

  private GetAsyncOperationInfoSummary (Builder builder) {
    this.numberOpenJobs = builder.numberOpenJobs;
    this.numberCompleteJobs = builder.numberCompleteJobs;
    this.totalNumberJobs = builder.totalNumberJobs;
    this.statuses = builder.statuses;
  }

  public Integer getNumberOpenJobs() {
    return numberOpenJobs;
  }

  public Integer getNumberCompleteJobs() {
    return numberCompleteJobs;
  }
  
  public Integer getTotalNumberJobs() {
    return totalNumberJobs;
  }

  public List<AsyncOperationInfo> getStatuses() {
    return statuses;
  }

  @JsonPOJOBuilder
  public static class Builder {
    private Integer numberOpenJobs;
    private Integer numberCompleteJobs;
    private Integer totalNumberJobs;
    private List<AsyncOperationInfo> statuses;

    private Builder() {}

    private Builder(GetAsyncOperationInfoSummary getAsyncOperationInfoSummary) {
      requireNonNull(getAsyncOperationInfoSummary, "getAsyncOperationsResponse cannot be null");
      this.numberOpenJobs = getAsyncOperationInfoSummary.numberOpenJobs;
      this.numberCompleteJobs = getAsyncOperationInfoSummary.numberCompleteJobs;
      this.totalNumberJobs = getAsyncOperationInfoSummary.totalNumberJobs;
      this.statuses = getAsyncOperationInfoSummary.statuses;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withNumberOpenJobs(Integer numberOpenJobs) {
      requireNonNull(numberOpenJobs, "numberOpenJobs cannot be null");
      this.numberOpenJobs = numberOpenJobs;
      return this;
    }

    public Builder withNumberCompleteJobs(Integer numberCompleteJobs) {
      requireNonNull(numberCompleteJobs, "numberCompleteJobs cannot be null");
      this.numberCompleteJobs = numberCompleteJobs;
      return this;
    }

    public Builder withTotalNumberJobs(Integer totalNumberJobs) {
      requireNonNull(totalNumberJobs, "totalNumberJobs cannot be null");
      this.totalNumberJobs = totalNumberJobs;
      return this;
    }    
    
    public Builder withStatuses(List<AsyncOperationInfo> statuses) {
      requireNonNull(statuses, "statuses cannot be null");
      List<AsyncOperationInfo> list = Lists.newArrayList();
      list.addAll(statuses);
      Collections.sort(list);
      this.statuses = ImmutableList.copyOf(list);
      return this;
    }

    public GetAsyncOperationInfoSummary build() {
      requireNonNull(numberOpenJobs, "numberOpenJobs cannot be null");
      requireNonNull(numberCompleteJobs, "numberCompleteJobs cannot be null");
      requireNonNull(totalNumberJobs, "totalNumberJobs cannot be null");
      requireNonNull(statuses, "statuses cannot be null");
      return new GetAsyncOperationInfoSummary(this);
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((numberCompleteJobs == null) ? 0 : numberCompleteJobs.hashCode());
    result = prime * result + ((numberOpenJobs == null) ? 0 : numberOpenJobs.hashCode());
    result = prime * result + ((statuses == null) ? 0 : statuses.hashCode());
    result = prime * result + ((totalNumberJobs == null) ? 0 : totalNumberJobs.hashCode());
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
    GetAsyncOperationInfoSummary other = (GetAsyncOperationInfoSummary) obj;
    if (numberCompleteJobs == null) {
      if (other.numberCompleteJobs != null)
        return false;
    } else if (!numberCompleteJobs.equals(other.numberCompleteJobs))
      return false;
    if (numberOpenJobs == null) {
      if (other.numberOpenJobs != null)
        return false;
    } else if (!numberOpenJobs.equals(other.numberOpenJobs))
      return false;
    if (statuses == null) {
      if (other.statuses != null)
        return false;
    } else if (!statuses.equals(other.statuses))
      return false;
    if (totalNumberJobs == null) {
      if (other.totalNumberJobs != null)
        return false;
    } else if (!totalNumberJobs.equals(other.totalNumberJobs))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "GetAsyncOperationInfoSummary [numberOpenJobs=" + numberOpenJobs
        + ", numberCompleteJobs=" + numberCompleteJobs + ", totalNumberJobs=" + totalNumberJobs
        + ", statuses=" + statuses + "]";
  }
}
//@formatter:on