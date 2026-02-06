//@formatter:off
package com.djt.hvac.domain.model.report.dto;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = ReportInstancePointDto.Builder.class)
public class ReportInstancePointDto implements Comparable<ReportInstancePointDto> {
  private Integer reportInstanceId;
  private final Integer reportTemplateEquipmentSpecId;
  private final Integer reportTemplatePointSpecId;
  private final Integer equipmentId;
  private final Integer pointId;
  private final Integer subscript;
  private final String type;
  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (ReportInstancePointDto reportInstancePointDto) {
    return new Builder(reportInstancePointDto);
  }

  private ReportInstancePointDto (Builder builder) {
    this.reportInstanceId = builder.reportInstanceId;
    this.reportTemplateEquipmentSpecId = builder.reportTemplateEquipmentSpecId;
    this.reportTemplatePointSpecId = builder.reportTemplatePointSpecId;
    this.equipmentId = builder.equipmentId;
    this.pointId = builder.pointId;
    this.subscript = builder.subscript;
    this.type = builder.type;
  }

  public Integer getReportInstanceId() {
    return reportInstanceId;
  }
  
  public void setReportInstanceId(Integer reportInstanceId) {
    this.reportInstanceId = reportInstanceId; 
  }
  
  public Integer getReportTemplatePointSpecId() {
    return reportTemplatePointSpecId;
  }

  public Integer getReportTemplateEquipmentSpecId() {
    return reportTemplateEquipmentSpecId;
  }
  
  public Integer getEquipmentId() {
    return equipmentId;
  }
  
  public Integer getPointId() {
    return pointId;
  }

  public Integer getSubscript() {
    return subscript;
  }

  public String getType() {
    return type;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((equipmentId == null) ? 0 : equipmentId.hashCode());
    result = prime * result + ((pointId == null) ? 0 : pointId.hashCode());
    result = prime * result + ((reportInstanceId == null) ? 0 : reportInstanceId.hashCode());
    result = prime * result
        + ((reportTemplateEquipmentSpecId == null) ? 0 : reportTemplateEquipmentSpecId.hashCode());
    result = prime * result
        + ((reportTemplatePointSpecId == null) ? 0 : reportTemplatePointSpecId.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
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
    ReportInstancePointDto other = (ReportInstancePointDto) obj;
    if (equipmentId == null) {
      if (other.equipmentId != null)
        return false;
    } else if (!equipmentId.equals(other.equipmentId))
      return false;
    if (pointId == null) {
      if (other.pointId != null)
        return false;
    } else if (!pointId.equals(other.pointId))
      return false;
    if (reportInstanceId == null) {
      if (other.reportInstanceId != null)
        return false;
    } else if (!reportInstanceId.equals(other.reportInstanceId))
      return false;
    if (reportTemplateEquipmentSpecId == null) {
      if (other.reportTemplateEquipmentSpecId != null)
        return false;
    } else if (!reportTemplateEquipmentSpecId.equals(other.reportTemplateEquipmentSpecId))
      return false;
    if (reportTemplatePointSpecId == null) {
      if (other.reportTemplatePointSpecId != null)
        return false;
    } else if (!reportTemplatePointSpecId.equals(other.reportTemplatePointSpecId))
      return false;
    if (type == null) {
      if (other.type != null)
        return false;
    } else if (!type.equals(other.type))
      return false;
    return true;
  }

  @Override
  public String toString() {
    
    return new StringBuilder()
        .append("ReportInstancePointDto [reportInstanceId=")
        .append(reportInstanceId)
        .append(", reportTemplateEquipmentSpecId=")
        .append(reportTemplateEquipmentSpecId)
        .append(", reportTemplatePointSpecId=")
        .append(reportTemplatePointSpecId)
        .append(", equipmentId=")
        .append(equipmentId)
        .append(", pointId=")
        .append(pointId)
        .append(", type=")
        .append(type)
        .append("]")
        .toString();
  }

  @Override
  public int compareTo(ReportInstancePointDto that) {
    return this.toString().compareTo(that.toString());
  }
  
  @JsonPOJOBuilder
  public static class Builder {
    private Integer reportInstanceId;
    private Integer reportTemplateEquipmentSpecId;
    private Integer reportTemplatePointSpecId;
    private Integer equipmentId;
    private Integer pointId;
    private Integer subscript;
    private String type;

    private Builder() {}

    private Builder(ReportInstancePointDto reportInstancePointDto) {
      requireNonNull(reportInstancePointDto, "reportInstancePointDto cannot be null");
      this.reportInstanceId = reportInstancePointDto.reportInstanceId;
      this.reportTemplateEquipmentSpecId = reportInstancePointDto.reportTemplateEquipmentSpecId;
      this.reportTemplatePointSpecId = reportInstancePointDto.reportTemplatePointSpecId;
      this.equipmentId = reportInstancePointDto.equipmentId;
      this.pointId = reportInstancePointDto.pointId;
      this.subscript = reportInstancePointDto.subscript;
      this.type = reportInstancePointDto.type;
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
    
    public Builder withReportTemplatePointSpecId(Integer reportTemplatePointSpecId) {
      requireNonNull(reportTemplatePointSpecId, "reportTemplatePointSpecId cannot be null");
      this.reportTemplatePointSpecId = reportTemplatePointSpecId;
      return this;
    }

    public Builder withPointId(Integer pointId) {
      requireNonNull(pointId, "pointId cannot be null");
      this.pointId = pointId;
      return this;
    }

    public Builder withEquipmentId(Integer equipmentId) {
      requireNonNull(equipmentId, "equipmentId cannot be null");
      this.equipmentId = equipmentId;
      return this;
    }
    
    public Builder withSubscript(Integer subscript) {
      this.subscript = subscript;
      return this;
    }

    public Builder withType(String type) {
      requireNonNull(type, "type cannot be null");
      this.type = type;
      return this;
    }

    public ReportInstancePointDto build() {
      requireNonNull(reportTemplatePointSpecId, "reportTemplatePointSpecId cannot be null");
      requireNonNull(reportTemplateEquipmentSpecId, "reportTemplateEquipmentSpecId cannot be null");
      requireNonNull(equipmentId, "equipmentId cannot be null");
      requireNonNull(pointId, "pointId cannot be null");
      requireNonNull(type, "type cannot be null");
      return new ReportInstancePointDto(this);
    }
  }
}
//@formatter:on