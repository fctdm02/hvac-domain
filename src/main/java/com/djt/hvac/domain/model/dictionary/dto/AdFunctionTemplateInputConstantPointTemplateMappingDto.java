
package com.djt.hvac.domain.model.dictionary.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "ad_function_template_input_const_id",
    "point_template_id"
})
public class AdFunctionTemplateInputConstantPointTemplateMappingDto {

    @JsonProperty("ad_function_template_input_const_id")
    private Integer adFunctionTemplateInputConstId;
    @JsonProperty("point_template_id")
    private String pointTemplateId;

    public AdFunctionTemplateInputConstantPointTemplateMappingDto() {
    }
    
    public AdFunctionTemplateInputConstantPointTemplateMappingDto(
        Integer adFunctionTemplateInputConstId,
        String pointTemplateId) {
      this.adFunctionTemplateInputConstId = adFunctionTemplateInputConstId;
      this.pointTemplateId = pointTemplateId;
    }
    
    @JsonProperty("ad_function_template_input_const_id")
    public Integer getAdFunctionTemplateInputConstId() {
        return adFunctionTemplateInputConstId;
    }

    @JsonProperty("ad_function_template_input_const_id")
    public void setAdFunctionTemplateInputConstId(Integer adFunctionTemplateInputConstId) {
        this.adFunctionTemplateInputConstId = adFunctionTemplateInputConstId;
    }
    
    @JsonProperty("point_template_id")
    public String getPointTemplateId() {
        return pointTemplateId;
    }

    @JsonProperty("point_template_id")
    public void setPointTemplateId(String pointTemplateId) {
        this.pointTemplateId = pointTemplateId;
    }
}