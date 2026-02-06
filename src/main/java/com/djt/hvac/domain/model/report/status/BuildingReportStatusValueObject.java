package com.djt.hvac.domain.model.report.status;

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import com.djt.hvac.domain.model.report.ReportInstanceEntity;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = BuildingReportStatusValueObject.Builder.class)
public class BuildingReportStatusValueObject implements Comparable<BuildingReportStatusValueObject> {
  private final Integer buildingId;
  private final String buildingName;
  private Integer numGreen;
  private Integer numYellow;
  private Integer numRed;
  private final List<ReportStatusValueObject> reports;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (BuildingReportStatusValueObject buildingReportStatusValueObject) {
    return new Builder(buildingReportStatusValueObject);
  }

  private BuildingReportStatusValueObject (Builder builder) {
    this.buildingId = builder.buildingId;
    this.buildingName = builder.buildingName;
    this.numGreen = builder.numGreen;
    this.numYellow = builder.numYellow;
    this.numRed = builder.numRed;
    this.reports = builder.reports;
  }

  public Integer getBuildingId() {
    return buildingId;
  }

  public String getBuildingName() {
    return buildingName;
  }

  public Integer getNumGreen() {
    return numGreen;
  }

  public Integer getNumYellow() {
    return numYellow;
  }

  public Integer getNumRed() {
    return numRed;
  }

  @Override
  public String toString() {
    StringBuilder builder2 = new StringBuilder();
    builder2.append("BuildingReportStatusValueObject [buildingId=").append(buildingId)
        .append(", buildingName=").append(buildingName).append(", numGreen=").append(numGreen)
        .append(", numYellow=").append(numYellow).append(", numRed=").append(numRed)
        .append(", reports=").append(reports).append("]");
    return builder2.toString();
  }

  public boolean addReport(ReportStatusValueObject report) {
    
    String status = report.getStatus();
    if (status.equals(ReportInstanceEntity.STATUS_GREEN)) {
      numGreen = Integer.valueOf(numGreen.intValue() + 1);
    } else if (status.equals(ReportInstanceEntity.STATUS_YELLOW)) {
      numYellow = Integer.valueOf(numYellow.intValue() + 1);
    } else {
      numRed = Integer.valueOf(numRed.intValue() + 1); 
    }
    boolean result = reports.add(report);
    Collections.sort(reports);
    return result;
  }
  
  public List<ReportStatusValueObject> getReports() {
    return reports;
  }

  @Override
  public int compareTo(BuildingReportStatusValueObject that) {
    return this.buildingName.compareTo(that.buildingName);
  }
  
  @JsonPOJOBuilder
  public static class Builder {
    private Integer buildingId;
    private String buildingName;
    private Integer numGreen;
    private Integer numYellow;
    private Integer numRed;
    private List<ReportStatusValueObject> reports;

    private Builder() {}

    private Builder(BuildingReportStatusValueObject buildingReportStatusValueObject) {
      requireNonNull(buildingReportStatusValueObject, "buildingReportStatusValueObject cannot be null");
      this.buildingId = buildingReportStatusValueObject.buildingId;
      this.buildingName = buildingReportStatusValueObject.buildingName;
      this.numGreen = buildingReportStatusValueObject.numGreen;
      this.numYellow = buildingReportStatusValueObject.numYellow;
      this.numRed = buildingReportStatusValueObject.numRed;
      this.reports = buildingReportStatusValueObject.reports;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withBuildingId(Integer buildingId) {
      requireNonNull(buildingId, "buildingId cannot be null");
      this.buildingId = buildingId;
      return this;
    }

    public Builder withBuildingName(String buildingName) {
      requireNonNull(buildingName, "buildingName cannot be null");
      this.buildingName = buildingName;
      return this;
    }

    public Builder withNumGreen(Integer numGreen) {
      requireNonNull(numGreen, "numGreen cannot be null");
      this.numGreen = numGreen;
      return this;
    }

    public Builder withNumYellow(Integer numYellow) {
      requireNonNull(numYellow, "numYellow cannot be null");
      this.numYellow = numYellow;
      return this;
    }

    public Builder withNumRed(Integer numRed) {
      requireNonNull(numRed, "numRed cannot be null");
      this.numRed = numRed;
      return this;
    }

    public Builder withReports(List<ReportStatusValueObject> reports) {
      requireNonNull(reports, "reports cannot be null");
      this.reports = reports;
      return this;
    }

    public BuildingReportStatusValueObject build() {
      requireNonNull(buildingId, "buildingId cannot be null");
      requireNonNull(buildingName, "buildingName cannot be null");
      requireNonNull(numGreen, "numGreen cannot be null");
      requireNonNull(numYellow, "numYellow cannot be null");
      requireNonNull(numRed, "numRed cannot be null");
      requireNonNull(reports, "reports cannot be null");
      return new BuildingReportStatusValueObject(this);
    }
  }
}