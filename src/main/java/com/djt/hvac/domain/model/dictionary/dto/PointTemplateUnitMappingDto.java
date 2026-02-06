package com.djt.hvac.domain.model.dictionary.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class PointTemplateUnitMappingDto {
  
  @JsonProperty("point_template_id")
  private Integer pointTemplateId;
  
  @JsonProperty("unit_mapping_id")
  private Integer unitMappingId;
  
  @JsonProperty("priority")
  private Integer priority;

  public PointTemplateUnitMappingDto() {
  }
  
  public PointTemplateUnitMappingDto(
      Integer pointTemplateId,
      Integer unitMappingId,
      Integer priority) {
    this.pointTemplateId = pointTemplateId;
    this.unitMappingId = unitMappingId;
    this.priority = priority;
  }

  @JsonProperty("point_template_id")
  public Integer getPointTemplateId() {
    return pointTemplateId;
  }

  @JsonProperty("point_template_id")
  public void setPointTemplateId(Integer pointTemplateId) {
    this.pointTemplateId = pointTemplateId;
  }
  
  public Integer getUnitMappingId() {
    return unitMappingId;
  }

  @JsonProperty("unit_mapping_id")
  public void setUnitMappingId(Integer unitMappingId) {
    this.unitMappingId = unitMappingId;
  }
  
  @JsonProperty("priority")
  public Integer getPriority() {
    return priority;
  }

  @JsonProperty("priority")
  public void setPriority(Integer priority) {
    this.priority = priority;
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((pointTemplateId == null) ? 0 : pointTemplateId.hashCode());
    result = prime * result + ((priority == null) ? 0 : priority.hashCode());
    result = prime * result + ((unitMappingId == null) ? 0 : unitMappingId.hashCode());
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
    PointTemplateUnitMappingDto other = (PointTemplateUnitMappingDto) obj;
    if (pointTemplateId == null) {
      if (other.pointTemplateId != null)
        return false;
    } else if (!pointTemplateId.equals(other.pointTemplateId))
      return false;
    if (priority == null) {
      if (other.priority != null)
        return false;
    } else if (!priority.equals(other.priority))
      return false;
    if (unitMappingId == null) {
      if (other.unitMappingId != null)
        return false;
    } else if (!unitMappingId.equals(other.unitMappingId))
      return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder2 = new StringBuilder();
    builder2.append("PointTemplateUnitMappingDto [pointTemplateId=").append(pointTemplateId)
        .append(", unitMappingId=").append(unitMappingId).append(", priority=").append(priority)
        .append("]");
    return builder2.toString();
  }
}