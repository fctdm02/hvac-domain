
package com.djt.hvac.domain.model.dictionary.dto;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "parentEquipmentTypeId",
    "pointTemplates"
})
public class EquipmentPointTemplateDto {

    @JsonProperty("parentEquipmentTypeId")
    private Integer parentEquipmentTypeId;
    
    @JsonProperty("pointTemplates")
    private List<PointTemplateDto> pointTemplates = new ArrayList<>();

    @JsonProperty("parentEquipmentTypeId")
    public Integer getParentEquipmentTypeId() {
        return parentEquipmentTypeId;
    }

    @JsonProperty("parentEquipmentTypeId")
    public void setParentEquipmentTypeId(Integer parentEquipmentTypeId) {
        this.parentEquipmentTypeId = parentEquipmentTypeId;
    }

    @JsonProperty("pointTemplates")
    public List<PointTemplateDto> getPointTemplates() {
        return pointTemplates;
    }

    public boolean addPointTemplate(PointTemplateDto pointTemplateDto) {
      return pointTemplates.add(pointTemplateDto);
    }
    
    @JsonProperty("pointTemplates")
    public void setPointTemplates(List<PointTemplateDto> pointTemplates) {
        this.pointTemplates = pointTemplates;
    }

    @Override
    public String toString() {
      return new StringBuilder()
          .append("EquipmentPointTemplateDto [parentEquipmentTypeId=")
          .append(parentEquipmentTypeId)
          .append(", pointTemplates=")
          .append(pointTemplates)
          .append("]")
          .toString();
    }
}