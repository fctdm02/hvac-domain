
package com.djt.hvac.domain.model.nodehierarchy.energyexchange.query.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "complete",
    "total",
    "percent"
})
public class EnergyExchangeProgressResponseParent {

  @JsonProperty("complete")
  private Integer complete;
  @JsonProperty("total")
  private Integer total;
  @JsonProperty("percent")
  private Double percent;

  @JsonProperty("complete")
  public Integer getComplete() {
    return complete;
  }

  @JsonProperty("complete")
  public void setComplete(Integer complete) {
    this.complete = complete;
  }

  @JsonProperty("total")
  public Integer getTotal() {
    return total;
  }

  @JsonProperty("total")
  public void setTotal(Integer total) {
    this.total = total;
  }

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
    builder.append("EnergyExchangeProgressResponseParent [complete=").append(complete)
        .append(", total=").append(total).append(", percent=").append(percent)
        .append(", getClass()=").append(getClass()).append(", hashCode()=").append(hashCode())
        .append(", toString()=").append(super.toString()).append("]");
    return builder.toString();
  }
}
