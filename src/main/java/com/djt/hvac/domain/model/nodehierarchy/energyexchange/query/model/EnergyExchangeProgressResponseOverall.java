
package com.djt.hvac.domain.model.nodehierarchy.energyexchange.query.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "percent"
})
public class EnergyExchangeProgressResponseOverall {

  @JsonProperty("percent")
  private Double percent;

  @JsonProperty("percent")
  public Double getPercent() {
    return percent;
  }

  @JsonProperty("percent")
  public void setPercent(Double percent) {
    this.percent = percent;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("EnergyExchangeProgressResponseOverall [percent=").append(percent).append("]");
    return builder.toString();
  }
}
