package com.djt.hvac.domain.model.report.status;

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
@JsonDeserialize(builder = ReportEquipmentErrorMessageListResponse.Builder.class)
public class ReportEquipmentErrorMessageListResponse {
  private final ReportEquipmentErrorMessageSearchCriteria searchCriteria;
  private final Integer count;
  private final List<ReportEquipmentErrorMessageValueObject> data;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (ReportEquipmentErrorMessageListResponse reportEquipmentErrorMessageListResponse) {
    return new Builder(reportEquipmentErrorMessageListResponse);
  }

  private ReportEquipmentErrorMessageListResponse (Builder builder) {
    this.searchCriteria = builder.searchCriteria;
    this.count = builder.count;
    this.data = builder.data;
  }

  public ReportEquipmentErrorMessageSearchCriteria getSearchCriteria() {
    return searchCriteria;
  }

  public Integer getCount() {
    return count;
  }

  public List<ReportEquipmentErrorMessageValueObject> getData() {
    return data;
  }

  @Override
  public String toString() {
    StringBuilder builder2 = new StringBuilder();
    builder2.append("ReportEquipmentErrorMessageListResponse [searchCriteria=")
        .append(searchCriteria).append(", count=").append(count).append(", data=").append(data)
        .append("]");
    return builder2.toString();
  }

  @JsonPOJOBuilder
  public static class Builder {
    private ReportEquipmentErrorMessageSearchCriteria searchCriteria;
    private Integer count;
    private List<ReportEquipmentErrorMessageValueObject> data;

    private Builder() {}

    private Builder(ReportEquipmentErrorMessageListResponse reportEquipmentErrorMessageListResponse) {
      requireNonNull(reportEquipmentErrorMessageListResponse, "reportEquipmentErrorMessageListResponse cannot be null");
      this.searchCriteria = reportEquipmentErrorMessageListResponse.searchCriteria;
      this.count = reportEquipmentErrorMessageListResponse.count;
      this.data = reportEquipmentErrorMessageListResponse.data;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withSearchCriteria(ReportEquipmentErrorMessageSearchCriteria searchCriteria) {
      requireNonNull(searchCriteria, "searchCriteria cannot be null");
      this.searchCriteria = searchCriteria;
      return this;
    }

    public Builder withCount(Integer count) {
      requireNonNull(count, "count cannot be null");
      this.count = count;
      return this;
    }

    public Builder withData(List<ReportEquipmentErrorMessageValueObject> data) {
      requireNonNull(data, "data cannot be null");
      this.data = ImmutableList.copyOf(data);
      return this;
    }

    public ReportEquipmentErrorMessageListResponse build() {
      requireNonNull(searchCriteria, "searchCriteria cannot be null");
      requireNonNull(count, "count cannot be null");
      requireNonNull(data, "data cannot be null");
      return new ReportEquipmentErrorMessageListResponse(this);
    }
  }
}