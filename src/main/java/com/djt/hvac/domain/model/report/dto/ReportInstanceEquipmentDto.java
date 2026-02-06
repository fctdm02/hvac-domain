//@formatter:off
package com.djt.hvac.domain.model.report.dto;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.ImmutableList;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = ReportInstanceEquipmentDto.Builder.class)
public class ReportInstanceEquipmentDto implements Comparable<ReportInstanceEquipmentDto> {
  private Integer reportInstanceId;
  private final Integer reportTemplateEquipmentSpecId;
  private final Integer equipmentId;
  private final List<ReportInstancePointDto> reportInstancePoints;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (ReportInstanceEquipmentDto reportInstanceEquipmentDto) {
    return new Builder(reportInstanceEquipmentDto);
  }

  private ReportInstanceEquipmentDto (Builder builder) {
    this.reportInstanceId = builder.reportInstanceId;
    this.reportTemplateEquipmentSpecId = builder.reportTemplateEquipmentSpecId;
    this.equipmentId = builder.equipmentId;
    this.reportInstancePoints = builder.reportInstancePoints;
  }
  
  public Integer getReportInstanceId() {
    return reportInstanceId;
  }
  
  public void setReportInstanceId(Integer reportInstanceId) {
    this.reportInstanceId = reportInstanceId; 
  }
  
  public Integer getReportTemplateEquipmentSpecId() {
    return reportTemplateEquipmentSpecId;
  }

  public Integer getEquipmentId() {
    return equipmentId;
  }

  public List<ReportInstancePointDto> getReportInstancePoints() {
    return reportInstancePoints;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((equipmentId == null) ? 0 : equipmentId.hashCode());
    result = prime * result + ((reportInstanceId == null) ? 0 : reportInstanceId.hashCode());
    result =
        prime * result + ((reportInstancePoints == null) ? 0 : reportInstancePoints.hashCode());
    result = prime * result
        + ((reportTemplateEquipmentSpecId == null) ? 0 : reportTemplateEquipmentSpecId.hashCode());
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
    ReportInstanceEquipmentDto other = (ReportInstanceEquipmentDto) obj;
    if (equipmentId == null) {
      if (other.equipmentId != null)
        return false;
    } else if (!equipmentId.equals(other.equipmentId))
      return false;
    if (reportInstanceId == null) {
      if (other.reportInstanceId != null)
        return false;
    } else if (!reportInstanceId.equals(other.reportInstanceId))
      return false;
    if (reportInstancePoints == null) {
      if (other.reportInstancePoints != null)
        return false;
    } else if (!reportInstancePoints.equals(other.reportInstancePoints))
      return false;
    if (reportTemplateEquipmentSpecId == null) {
      if (other.reportTemplateEquipmentSpecId != null)
        return false;
    } else if (!reportTemplateEquipmentSpecId.equals(other.reportTemplateEquipmentSpecId))
      return false;
    return true;
  }

  @Override
  public String toString() {
    
    return new StringBuilder()
        .append("ReportInstanceEquipmentDto [reportInstanceId=")
        .append(reportInstanceId)
        .append(", reportTemplateEquipmentSpecId=")
        .append(reportTemplateEquipmentSpecId)
        .append(", equipmentId=")
        .append(equipmentId)
        .append(", reportInstancePoints=")
        .append(reportInstancePoints)
        .append("]")
        .toString();
  }

  @Override
  public int compareTo(ReportInstanceEquipmentDto that) {
    return this.toString().compareTo(that.toString());
  }
  
  @JsonPOJOBuilder
  public static class Builder {
    private Integer reportInstanceId;
    private Integer reportTemplateEquipmentSpecId;
    private Integer equipmentId;
    private List<ReportInstancePointDto> reportInstancePoints;

    private Builder() {}

    private Builder(ReportInstanceEquipmentDto reportInstanceEquipmentDto) {
      requireNonNull(reportInstanceEquipmentDto, "reportInstanceEquipmentDto cannot be null");
      this.reportInstanceId = reportInstanceEquipmentDto.reportInstanceId;
      this.reportTemplateEquipmentSpecId = reportInstanceEquipmentDto.reportTemplateEquipmentSpecId;
      this.equipmentId = reportInstanceEquipmentDto.equipmentId;
      this.reportInstancePoints = reportInstanceEquipmentDto.reportInstancePoints;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withReportInstanceId(Integer reportInstanceId) {
      this.reportInstanceId = reportInstanceId;
      return this;
    }

    public Builder withReportTemplateEquipmentSpecId(Integer reportTemplateEquipmentSpecId) {
      requireNonNull(reportTemplateEquipmentSpecId, "reportTemplateEquipmentSpecId cannot be null");
      this.reportTemplateEquipmentSpecId = reportTemplateEquipmentSpecId;
      return this;
    }

    public Builder withEquipmentId(Integer equipmentId) {
      requireNonNull(equipmentId, "equipmentId cannot be null");
      this.equipmentId = equipmentId;
      return this;
    }

    public Builder withReportInstancePoints(List<ReportInstancePointDto> reportInstancePoints) {
      if (reportInstancePoints != null) {
        this.reportInstancePoints = ImmutableList.copyOf(reportInstancePoints);  
      } else {
        List<ReportInstancePointDto> points = new ArrayList<>();
        this.reportInstancePoints = ImmutableList.copyOf(points);
      }
      return this;
    }
    
    public ReportInstanceEquipmentDto build() {
      requireNonNull(reportTemplateEquipmentSpecId, "reportTemplateEquipmentSpecId cannot be null");
      requireNonNull(equipmentId, "equipmentId cannot be null");
      return new ReportInstanceEquipmentDto(this);
    }
  }
}
//@formatter:on