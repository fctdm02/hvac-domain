
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
    "name",
    "label",
    "required",
    "default_value"
})
public class InputConst implements Serializable {

  @JsonProperty("id")
  private Integer id;
  @JsonProperty("name")
  private String name;
  @JsonProperty("label")
  private String label;
  @JsonProperty("required")
  private Boolean required;
  @JsonProperty("default_value")
  private String defaultValue;
  @JsonIgnore
  private Map<String, Object> additionalProperties = new HashMap<String, Object>();
  private final static long serialVersionUID = 2458281196991630237L;

  @JsonProperty("id")
  public Integer getId() {
    return id;
  }

  @JsonProperty("id")
  public void setId(Integer id) {
    this.id = id;
  }

  @JsonProperty("name")
  public String getName() {
    return name;
  }

  @JsonProperty("name")
  public void setName(String name) {
    this.name = name.trim().toUpperCase();
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

  @JsonProperty("default_value")
  public String getDefaultValue() {
    return defaultValue;
  }

  @JsonProperty("default_value")
  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
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
