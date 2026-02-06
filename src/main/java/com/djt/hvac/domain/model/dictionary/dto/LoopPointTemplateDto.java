
package com.djt.hvac.domain.model.dictionary.dto;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "parentLoopTypeId",
    "pointTemplates"
})
public class LoopPointTemplateDto {

    @JsonProperty("parentLoopTypeId")
    private Integer parentLoopTypeId;
    
    @JsonProperty("pointTemplates")
    private List<PointTemplateDto> pointTemplates = new ArrayList<>();

    @JsonProperty("parentLoopTypeId")
    public Integer getParentLoopTypeId() {
        return parentLoopTypeId;
    }

    @JsonProperty("parentLoopTypeId")
    public void setParentLoopTypeId(Integer parentLoopTypeId) {
        this.parentLoopTypeId = parentLoopTypeId;
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
          .append("LoopPointTemplateDto [parentLoopTypeId=")
          .append(parentLoopTypeId)
          .append(", pointTemplates=")
          .append(pointTemplates)
          .append("]")
          .toString();
    }
}