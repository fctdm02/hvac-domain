
package com.djt.hvac.domain.model.nodehierarchy.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/*
<pre>
SELECT
  id as building_id, -- PK is FK to to building_tbl
  payment_plan_id,
  payment_method_id,
  stripe_subscription_id,
  started_at,
  current_interval_started_at,
  pending_payment_plan_id,
  pending_payment_plan_updated_at
FROM  
  building_subscriptions 
WHERE
  id IN 
  (
    SELECT id FROM buildings WHERE customer_id = 10
  );
</pre>  
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "building_id",
    "payment_plan_id",
    "payment_method_id",
    "stripe_subscription_id",
    "started_at",
    "current_interval_started_at",
    "pending_payment_plan_id",
    "pending_payment_plan_updated_at"
})
public class BuildingSubscriptionDto implements Serializable {

  private final static long serialVersionUID = -1890385919793031403L;
  
  @JsonProperty("building_id")
  private Integer buildingId;
  
  @JsonProperty("payment_plan_id")
  private Integer paymentPlanId;
  
  @JsonProperty("payment_method_id")
  private Integer paymentMethodId;
  
  @JsonProperty("stripe_subscription_id")
  private String stripeSubscriptionId;

  @JsonProperty("started_at")
  private String startedAt;
  
  @JsonProperty("current_interval_started_at")
  private String currentIntervalStartedAt;
  
  @JsonProperty("pending_payment_plan_id")
  private Integer pendingPaymentPlanId;
  
  @JsonProperty("pending_payment_plan_updated_at")
  private String pendingPaymentPlanUpdatedAt;

  
  @JsonProperty("building_id")
  public Integer getBuildingId() {
    return buildingId;
  }

  @JsonProperty("building_id")
  public void setBuildingId(Integer buildingId) {
    this.buildingId = buildingId;
  }

  
  @JsonProperty("payment_plan_id")
  public Integer getPaymentPlanId() {
    return paymentPlanId;
  }

  @JsonProperty("payment_plan_id")
  public void setPaymentPlanId(Integer paymentPlanId) {
    this.paymentPlanId = paymentPlanId;
  }

  
  @JsonProperty("payment_method_id")
  public Integer getPaymentMethodId() {
    return paymentMethodId;
  }

  @JsonProperty("payment_method_id")
  public void setPaymentMethodId(Integer paymentMethodId) {
    this.paymentMethodId = paymentMethodId;
  }
  
  
  @JsonProperty("stripe_subscription_id")
  public String getStripeSubscriptionId() {
    return stripeSubscriptionId;
  }

  @JsonProperty("stripe_subscription_id")
  public void setStripeSubscriptionId(String stripeSubscriptionId) {
    this.stripeSubscriptionId = stripeSubscriptionId;
  }

  
  @JsonProperty("started_at")
  public String getStartedAt() {
    return startedAt;
  }

  @JsonProperty("startedAt")
  public void setStartedAt(String startedAt) {
    this.startedAt = startedAt;
  }

  
  @JsonProperty("current_interval_started_at")
  public String getCurrentIntervalStartedAt() {
    return currentIntervalStartedAt;
  }

  @JsonProperty("current_interval_started_at")
  public void setCurrentIntervalStartedAt(String currentIntervalStartedAt) {
    this.currentIntervalStartedAt = currentIntervalStartedAt;
  }
  
  
  @JsonProperty("pending_payment_plan_id")
  public Integer getPendingPaymentPlanId() {
    return pendingPaymentPlanId;
  }

  @JsonProperty("pending_payment_plan_id")
  public void setPendingPaymentPlanId(Integer pendingPaymentPlanId) {
    this.pendingPaymentPlanId = pendingPaymentPlanId;
  }
  
  
  @JsonProperty("pending_payment_plan_updated_at")
  public String getPendingPaymentPlanUpdatedAt() {
    return pendingPaymentPlanUpdatedAt;
  }

  @JsonProperty("pending_payment_plan_updated_at")
  public void setPendingPaymentPlanUpdatedAt(String pendingPaymentPlanUpdatedAt) {
    this.pendingPaymentPlanUpdatedAt = pendingPaymentPlanUpdatedAt;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((buildingId == null) ? 0 : buildingId.hashCode());
    result = prime * result
        + ((currentIntervalStartedAt == null) ? 0 : currentIntervalStartedAt.hashCode());
    result = prime * result + ((paymentMethodId == null) ? 0 : paymentMethodId.hashCode());
    result = prime * result + ((paymentPlanId == null) ? 0 : paymentPlanId.hashCode());
    result =
        prime * result + ((pendingPaymentPlanId == null) ? 0 : pendingPaymentPlanId.hashCode());
    result = prime * result
        + ((pendingPaymentPlanUpdatedAt == null) ? 0 : pendingPaymentPlanUpdatedAt.hashCode());
    result = prime * result + ((startedAt == null) ? 0 : startedAt.hashCode());
    result =
        prime * result + ((stripeSubscriptionId == null) ? 0 : stripeSubscriptionId.hashCode());
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
    BuildingSubscriptionDto other = (BuildingSubscriptionDto) obj;
    if (buildingId == null) {
      if (other.buildingId != null)
        return false;
    } else if (!buildingId.equals(other.buildingId))
      return false;
    if (currentIntervalStartedAt == null) {
      if (other.currentIntervalStartedAt != null)
        return false;
    } else if (!currentIntervalStartedAt.equals(other.currentIntervalStartedAt))
      return false;
    if (paymentMethodId == null) {
      if (other.paymentMethodId != null)
        return false;
    } else if (!paymentMethodId.equals(other.paymentMethodId))
      return false;
    if (paymentPlanId == null) {
      if (other.paymentPlanId != null)
        return false;
    } else if (!paymentPlanId.equals(other.paymentPlanId))
      return false;
    if (pendingPaymentPlanId == null) {
      if (other.pendingPaymentPlanId != null)
        return false;
    } else if (!pendingPaymentPlanId.equals(other.pendingPaymentPlanId))
      return false;
    if (pendingPaymentPlanUpdatedAt == null) {
      if (other.pendingPaymentPlanUpdatedAt != null)
        return false;
    } else if (!pendingPaymentPlanUpdatedAt.equals(other.pendingPaymentPlanUpdatedAt))
      return false;
    if (startedAt == null) {
      if (other.startedAt != null)
        return false;
    } else if (!startedAt.equals(other.startedAt))
      return false;
    if (stripeSubscriptionId == null) {
      if (other.stripeSubscriptionId != null)
        return false;
    } else if (!stripeSubscriptionId.equals(other.stripeSubscriptionId))
      return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("BuildingSubscriptionDto [buildingId=").append(buildingId)
        .append(", paymentPlanId=").append(paymentPlanId).append(", paymentMethodId=")
        .append(paymentMethodId).append(", stripeSubscriptionId=").append(stripeSubscriptionId)
        .append(", startedAt=").append(startedAt).append(", currentIntervalStartedAt=")
        .append(currentIntervalStartedAt).append(", pendingPaymentPlanId=")
        .append(pendingPaymentPlanId).append(", pendingPaymentPlanUpdatedAt=")
        .append(pendingPaymentPlanUpdatedAt).append("]");
    return builder.toString();
  }
 } 
