
package com.djt.hvac.domain.model.dictionary.dto;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "parentPlantTypeId",
    "pointTemplates"
})
public class PlantPointTemplateDto {

    @JsonProperty("parentPlantTypeId")
    private Integer parentPlantTypeId;
    
    @JsonProperty("pointTemplates")
    private List<PointTemplateDto> pointTemplates = new ArrayList<>();

    @JsonProperty("parentPlantTypeId")
    public Integer getParentPlantTypeId() {
        return parentPlantTypeId;
    }

    @JsonProperty("parentPlantTypeId")
    public void setParentPlantTypeId(Integer parentPlantTypeId) {
        this.parentPlantTypeId = parentPlantTypeId;
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
          .append("PlantPointTemplateDto [parentPlantTypeId=")
          .append(parentPlantTypeId)
          .append(", pointTemplates=")
          .append(pointTemplates)
          .append("]")
          .toString();
    }
}