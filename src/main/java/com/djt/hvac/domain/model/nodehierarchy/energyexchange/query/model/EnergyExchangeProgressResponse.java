
package com.djt.hvac.domain.model.nodehierarchy.energyexchange.query.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "overall",
    "type",
    "parent"
})
public class EnergyExchangeProgressResponse {

  @JsonProperty("overall")
  private EnergyExchangeProgressResponseOverall overall;
  @JsonProperty("type")
  private EnergyExchangeProgressResponseType type;
  @JsonProperty("parent")
  private EnergyExchangeProgressResponseParent parent;

  @JsonProperty("overall")
  public EnergyExchangeProgressResponseOverall getOverall() {
    return overall;
  }

  @JsonProperty("overall")
  public void setOverall(EnergyExchangeProgressResponseOverall overall) {
    this.overall = overall;
  }

  @JsonProperty("type")
  public EnergyExchangeProgressResponseType getType() {
    return type;
  }

  @JsonProperty("type")
  public void setType(EnergyExchangeProgressResponseType type) {
    this.type = type;
  }

  @JsonProperty("parent")
  public EnergyExchangeProgressResponseParent getParent() {
    return parent;
  }

  @JsonProperty("parent")
  public void setParent(EnergyExchangeProgressResponseParent parent) {
    this.parent = parent;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("EnergyExchangeProgressResponse [overall=").append(overall).append(", type=")
        .append(type).append(", parent=").append(parent).append(", getClass()=").append(getClass())
        .append(", hashCode()=").append(hashCode()).append(", toString()=").append(super.toString())
        .append("]");
    return builder.toString();
  }
}
