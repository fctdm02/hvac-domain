package com.djt.hvac.domain.model.dictionary.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/*
 <pre>
  SELECT * FROM payment_plans WHERE deprecated = false ORDER BY id;
  
  CREATE TABLE payment_plan_tbl (
    id SERIAL PRIMARY KEY,
    point_cap INTEGER NOT NULL,
    interval payment_interval NOT NULL,
    cost_per_interval MONEY NOT NULL,
    stripe_product_id CHARACTER VARYING,
    stripe_plan_id CHARACTER VARYING,
    stripe_test_product_id CHARACTER VARYING,
    stripe_test_plan_id CHARACTER VARYING,
    deprecated BOOL NOT NULL DEFAULT FALSE
  ); 
 </pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "point_cap",
    "interval",
    "cost_per_interval",
    "stripe_product_id",
    "stripe_plan_id",
    "stripe_test_product_id",
    "stripe_test_plan_id",
    "deprecated"
})
public class PaymentPlanDto {

    @JsonProperty("id")
    private Integer id;
    
    @JsonProperty("point_cap")
    private Integer pointCap;
    
    @JsonProperty("interval")
    private String interval;
    
    @JsonProperty("cost_per_interval")
    private Double costPerInterval;
    
    @JsonProperty("stripe_product_id")
    private String stripeProductId;

    @JsonProperty("stripe_plan_id")
    private String stripePlanId;

    @JsonProperty("stripe_test_product_id")
    private String stripeTestProductId;

    @JsonProperty("stripe_test_plan_id")
    private String stripeTestPlanId;
    
    @JsonProperty("deprecated")
    private Boolean deprecated;

    
    
    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    
    @JsonProperty("point_cap")
    public Integer getPointCap() {
        return pointCap;
    }

    @JsonProperty("point_cap")
    public void setPointCap(Integer pointCap) {
        this.pointCap = pointCap;
    }

    
    @JsonProperty("interval")
    public String getInterval() {
        return interval;
    }

    @JsonProperty("interval")
    public void setInterval(String interval) {
        this.interval = interval;
    }

    
    @JsonProperty("cost_per_interval")
    public Double getCostPerInterval() {
        return costPerInterval;
    }

    @JsonProperty("cost_per_interval")
    public void setCostPerInterval(Double costPerInterval) {
        this.costPerInterval = costPerInterval;
    }

    
    @JsonProperty("stripe_product_id")
    public String getStripeProductId() {
        return stripeProductId;
    }

    @JsonProperty("stripe_product_id")
    public void setStripeProductId(String stripeProductId) {
        this.stripeProductId = stripeProductId;
    }

    
    @JsonProperty("stripe_plan_id")
    public String getStripePlanId() {
        return stripePlanId;
    }

    @JsonProperty("stripe_plan_id")
    public void setStripePlanId(String stripePlanId) {
        this.stripePlanId = stripePlanId;
    }

    
    @JsonProperty("stripe_test_product_id")
    public String getStripeTestProductId() {
        return stripeTestProductId;
    }

    @JsonProperty("stripe_test_product_id")
    public void setStripeTestProductId(String stripeTestProductId) {
        this.stripeTestProductId = stripeTestProductId;
    }

    
    @JsonProperty("stripe_test_plan_id")
    public String getStripeTestPlanId() {
        return stripeTestPlanId;
    }

    @JsonProperty("stripe_test_plan_id")
    public void setStripeTestPlanId(String stripeTestPlanId) {
        this.stripeTestPlanId = stripeTestPlanId;
    }
    
    
    @JsonProperty("deprecated")
    public Boolean getDeprecated() {
        return deprecated;
    }

    @JsonProperty("deprecated")
    public void setDeprecated(Boolean deprecated) {
        this.deprecated = deprecated;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((costPerInterval == null) ? 0 : costPerInterval.hashCode());
      result = prime * result + ((deprecated == null) ? 0 : deprecated.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((interval == null) ? 0 : interval.hashCode());
      result = prime * result + ((pointCap == null) ? 0 : pointCap.hashCode());
      result = prime * result + ((stripePlanId == null) ? 0 : stripePlanId.hashCode());
      result = prime * result + ((stripeProductId == null) ? 0 : stripeProductId.hashCode());
      result = prime * result + ((stripeTestPlanId == null) ? 0 : stripeTestPlanId.hashCode());
      result =
          prime * result + ((stripeTestProductId == null) ? 0 : stripeTestProductId.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      PaymentPlanDto other = (PaymentPlanDto) obj;
      if (costPerInterval == null) {
        if (other.costPerInterval != null)
          return false;
      } else if (!costPerInterval.equals(other.costPerInterval))
        return false;
      if (deprecated == null) {
        if (other.deprecated != null)
          return false;
      } else if (!deprecated.equals(other.deprecated))
        return false;
      if (id == null) {
        if (other.id != null)
          return false;
      } else if (!id.equals(other.id))
        return false;
      if (interval == null) {
        if (other.interval != null)
          return false;
      } else if (!interval.equals(other.interval))
        return false;
      if (pointCap == null) {
        if (other.pointCap != null)
          return false;
      } else if (!pointCap.equals(other.pointCap))
        return false;
      if (stripePlanId == null) {
        if (other.stripePlanId != null)
          return false;
      } else if (!stripePlanId.equals(other.stripePlanId))
        return false;
      if (stripeProductId == null) {
        if (other.stripeProductId != null)
          return false;
      } else if (!stripeProductId.equals(other.stripeProductId))
        return false;
      if (stripeTestPlanId == null) {
        if (other.stripeTestPlanId != null)
          return false;
      } else if (!stripeTestPlanId.equals(other.stripeTestPlanId))
        return false;
      if (stripeTestProductId == null) {
        if (other.stripeTestProductId != null)
          return false;
      } else if (!stripeTestProductId.equals(other.stripeTestProductId))
        return false;
      return true;
    }

    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("PaymentPlanDto [id=").append(id).append(", pointCap=").append(pointCap)
          .append(", interval=").append(interval).append(", costPerInterval=")
          .append(costPerInterval).append(", stripeProductId=").append(stripeProductId)
          .append(", stripePlanId=").append(stripePlanId).append(", stripeTestProductId=")
          .append(stripeTestProductId).append(", stripeTestPlanId=").append(stripeTestPlanId)
          .append(", deprecated=").append(deprecated).append("]");
      return builder.toString();
    }
}
