package com.djt.hvac.domain.model.dictionary.dto.function;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "json"
})
public class DatabaseWrapperDto {

  @JsonProperty("id")
  private String id;
  @JsonProperty("json")
  private String json;

  @JsonProperty("id")
  public String getId() {
    return id;
  }

  @JsonProperty("reference_number")
  public void setId(String id) {
    this.id = id;
  }

  @JsonProperty("json")
  public String getJson() {
    return json;
  }

  @JsonProperty("json")
  public void setJson(String json) {
    this.json = json;
  }
}
