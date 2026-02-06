package com.djt.hvac.domain.model.function.status;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.List;
import com.google.common.collect.ImmutableList;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = AdFunctionErrorMessageSearchResponse.Builder.class)
public class AdFunctionErrorMessageSearchResponse {
  private final AdFunctionErrorMessageSearchCriteria criteria;
  private final Integer count;
  private final List<AdFunctionErrorMessagesValueObject> data;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (AdFunctionErrorMessageSearchResponse adFunctionErrorMessageSearchResponse) {
    return new Builder(adFunctionErrorMessageSearchResponse);
  }

  private AdFunctionErrorMessageSearchResponse (Builder builder) {
    this.criteria = builder.criteria;
    this.count = builder.count;
    this.data = builder.data;
  }

  public AdFunctionErrorMessageSearchCriteria getCriteria() {
    return criteria;
  }

  public Integer getCount() {
    return count;
  }

  public List<AdFunctionErrorMessagesValueObject> getData() {
    return data;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((count == null) ? 0 : count.hashCode());
    result = prime * result + ((criteria == null) ? 0 : criteria.hashCode());
    result = prime * result + ((data == null) ? 0 : data.hashCode());
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
    AdFunctionErrorMessageSearchResponse other = (AdFunctionErrorMessageSearchResponse) obj;
    if (count == null) {
      if (other.count != null)
        return false;
    } else if (!count.equals(other.count))
      return false;
    if (criteria == null) {
      if (other.criteria != null)
        return false;
    } else if (!criteria.equals(other.criteria))
      return false;
    if (data == null) {
      if (other.data != null)
        return false;
    } else if (!data.equals(other.data))
      return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder2 = new StringBuilder();
    builder2.append("AdFunctionErrorMessageSearchResponse [criteria=").append(criteria)
        .append(", count=").append(count).append(", data=").append(data).append("]");
    return builder2.toString();
  }
  
  @JsonPOJOBuilder
  public static class Builder {
    private AdFunctionErrorMessageSearchCriteria criteria;
    private Integer count;
    private List<AdFunctionErrorMessagesValueObject> data;

    private Builder() {}

    private Builder(AdFunctionErrorMessageSearchResponse adFunctionErrorMessageSearchResponse) {
      requireNonNull(adFunctionErrorMessageSearchResponse, "adFunctionErrorMessageSearchResponse cannot be null");
      this.criteria = adFunctionErrorMessageSearchResponse.criteria;
      this.count = adFunctionErrorMessageSearchResponse.count;
      this.data = adFunctionErrorMessageSearchResponse.data;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withCriteria(AdFunctionErrorMessageSearchCriteria criteria) {
      requireNonNull(criteria, "criteria cannot be null");
      this.criteria = criteria;
      return this;
    }

    public Builder withCount(Integer count) {
      requireNonNull(count, "count cannot be null");
      this.count = count;
      return this;
    }

    public Builder withData(List<AdFunctionErrorMessagesValueObject> data) {
      requireNonNull(data, "data cannot be null");
      this.data = ImmutableList.copyOf(data);
      return this;
    }

    public AdFunctionErrorMessageSearchResponse build() {
      requireNonNull(criteria, "criteria cannot be null");
      requireNonNull(count, "count cannot be null");
      requireNonNull(data, "data cannot be null");
      return new AdFunctionErrorMessageSearchResponse(this);
    }
  }
}