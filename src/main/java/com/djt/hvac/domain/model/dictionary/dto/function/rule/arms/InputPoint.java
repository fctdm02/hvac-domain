
package com.djt.hvac.domain.model.dictionary.dto.function.rule.arms;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "array",
    "label",
    "required",
    "point_template_id",
    "current_object_expression",
    "name"
})
public class InputPoint implements Serializable {

  @JsonProperty("id")
  private Integer id;
  @JsonProperty("array")
  private Boolean array;
  @JsonProperty("label")
  private String label;
  @JsonProperty("required")
  private Boolean required;
  @JsonProperty("point_template_id")
  private Integer pointTemplateId;
  @JsonProperty("current_object_expression")
  private String currentObjectExpression;
  @JsonProperty("name")
  private String name;
  @JsonIgnore
  private Map<String, Object> additionalProperties = new HashMap<String, Object>();
  private final static long serialVersionUID = 80908762570293842L;

  @JsonProperty("id")
  public Integer getId() {
    return id;
  }

  @JsonProperty("id")
  public void setId(Integer id) {
    this.id = id;
  }

  @JsonProperty("array")
  public Boolean getArray() {
    if (array == null) {
      return Boolean.FALSE;
    }
    return array;
  }

  @JsonProperty("array")
  public void setArray(Boolean array) {
    this.array = array;
  }

  @JsonProperty("label")
  public String getLabel() {
    return label;
  }

  @JsonProperty("label")
  public void setLabel(String label) {
    this.label = label;
  }

  @JsonProperty("required")
  public Boolean getRequired() {
    if (required == null) {
      return Boolean.FALSE;
    }
    return required;
  }

  @JsonProperty("required")
  public void setRequired(Boolean required) {
    this.required = required;
  }

  @JsonProperty("point_template_id")
  public Integer getPointTemplateId() {
    return pointTemplateId;
  }

  @JsonProperty("point_template_id")
  public void setPointTemplateId(Integer pointTemplateId) {
    this.pointTemplateId = pointTemplateId;
  }

  @JsonProperty("current_object_expression")
  public String getCurrentObjectExpression() {
    return currentObjectExpression;
  }

  @JsonProperty("current_object_expression")
  public void setCurrentObjectExpression(String currentObjectExpression) {
    this.currentObjectExpression = currentObjectExpression;
  }

  @JsonProperty("name")
  public String getName() {
    return name;
  }

  @JsonProperty("name")
  public void setName(String name) {
    this.name = name;
  }
  
  @JsonAnyGetter
  public Map<String, Object> getAdditionalProperties() {
    return this.additionalProperties;
  }

  @JsonAnySetter
  public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }
}
