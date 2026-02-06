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
@JsonDeserialize(builder = PortfolioReportSummaryValueObject.Builder.class)
public class PortfolioReportSummaryValueObject {
  private final List<BuildingReportStatusValueObject> buildingReportStatuses;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (PortfolioReportSummaryValueObject portfolioReportSummaryValueObject) {
    return new Builder(portfolioReportSummaryValueObject);
  }

  private PortfolioReportSummaryValueObject (Builder builder) {
    this.buildingReportStatuses = builder.buildingReportStatuses;
  }

  public List<BuildingReportStatusValueObject> getBuildingReportStatuses() {
    return buildingReportStatuses;
  }

  @Override
  public String toString() {
    StringBuilder builder2 = new StringBuilder();
    builder2.append("PortfolioReportSummaryValueObject [buildingReportStatuses=")
        .append(buildingReportStatuses).append("]");
    return builder2.toString();
  }

  @JsonPOJOBuilder
  public static class Builder {
    private List<BuildingReportStatusValueObject> buildingReportStatuses;

    private Builder() {}

    private Builder(PortfolioReportSummaryValueObject portfolioReportSummaryValueObject) {
      requireNonNull(portfolioReportSummaryValueObject, "portfolioReportSummaryValueObject cannot be null");
      this.buildingReportStatuses = portfolioReportSummaryValueObject.buildingReportStatuses;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withBuildingReportStatuses(List<BuildingReportStatusValueObject> buildingReportStatuses) {
      requireNonNull(buildingReportStatuses, "buildingReportStatuses cannot be null");
      this.buildingReportStatuses = ImmutableList.copyOf(buildingReportStatuses);
      return this;
    }

    public PortfolioReportSummaryValueObject build() {
      requireNonNull(buildingReportStatuses, "buildingReportStatuses cannot be null");
      return new PortfolioReportSummaryValueObject(this);
    }
  }
}