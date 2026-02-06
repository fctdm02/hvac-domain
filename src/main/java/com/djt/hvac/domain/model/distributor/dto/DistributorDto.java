
package com.djt.hvac.domain.model.distributor.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "parent_id",
    "name",
    "created_at",
    "updated_at",
    "uuid",
    "unit_system",
    "referral_agent_id",
    "type",
    "payment_status",
    "payment_status_updated_at",
    "status",
    "status_updated_at",
    "billing_start_date",
    "billing_renewal_date",
    "stripe_customer_id",
    "payment_method_id",
    "payment_method_type",
    "payment_method_name",
    "payment_method_stripe_source_id",
    "payment_method_account_holder_name",
    "payment_method_address",
    "payment_method_city",
    "payment_method_state",
    "payment_method_zip_code",
    "payment_method_phone_number",
    "payment_method_card_brand",
    "payment_method_card_expiry",
    "payment_method_card_last_four",
    "allow_out_of_band_buildings"
})
public class DistributorDto {

  @JsonProperty("id")
  private Integer id;
  
  @JsonProperty("parent_id")
  private Integer parentId;
  
  @JsonProperty("name")
  private String name;
  
  @JsonProperty("created_at")
  private String createdAt;
  
  @JsonProperty("updated_at")
  private String updatedAt;
  
  @JsonProperty("uuid")
  private String uuid;

  @JsonProperty("unit_system")
  private String unitSystem;
  
  @JsonProperty("referral_agent_id")
  private Integer referralAgentId;
  
  @JsonProperty("type")
  private String type;
  
  @JsonProperty("payment_status")
  private String paymentStatus;
  
  @JsonProperty("payment_status_updated_at")
  private String paymentStatusUpdatedAt;
  
  @JsonProperty("status")
  private String status;
  
  @JsonProperty("status_updated_at")
  private String statusUpdatedAt;
  
  @JsonProperty("billing_start_date")
  private String billingStartDate;
  
  @JsonProperty("billing_renewal_date")
  private String billingRenewalDate;
  
  @JsonProperty("stripe_customer_id")
  private String stripeCustomerId;
  
  @JsonProperty("payment_method_id")
  private String paymentMethodId;

  @JsonProperty("payment_method_type")
  private String paymentMethodType;

  @JsonProperty("payment_method_name")
  private String paymentMethodName;

  @JsonProperty("payment_method_stripe_source_id")
  private String paymentMethodStripeSourceId;

  @JsonProperty("payment_method_account_holder_name")
  private String paymentMethodAccountHolderName;

  @JsonProperty("payment_method_address")
  private String paymentMethodAddress;

  @JsonProperty("payment_method_city")
  private String paymentMethodCity;

  @JsonProperty("payment_method_state")
  private String paymentMethodState;

  @JsonProperty("payment_method_zip_code")
  private String paymentMethodZipCode;

  @JsonProperty("payment_method_phone_number")
  private String paymentMethodPhoneNumber;

  @JsonProperty("payment_method_card_brand")
  private String paymentMethodCardBrand;

  @JsonProperty("payment_method_card_expiry")
  private String paymentMethodCardExpiry;

  @JsonProperty("payment_method_card_last_four")
  private String paymentMethodCardLastFour;

  @JsonProperty("allow_out_of_band_buildings")
  private boolean allowOutOfBandBuildings;
  
  @JsonProperty("id")
  public Integer getId() {
    return id;
  }

  @JsonProperty("id")
  public void setId(Integer id) {
    this.id = id;
  }

  
  @JsonProperty("parent_id")
  public Integer getParentId() {
    return parentId;
  }

  @JsonProperty("parent_id")
  public void setParentId(Integer parentId) {
    this.parentId = parentId;
  }

  
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  @JsonProperty("name")
  public void setName(String name) {
    this.name = name;
  }

  
  @JsonProperty("created_at")
  public String getCreatedAt() {
    return createdAt;
  }

  @JsonProperty("created_at")
  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  
  @JsonProperty("updated_at")
  public String getUpdatedAt() {
    return updatedAt;
  }

  @JsonProperty("updated_at")
  public void setUpdatedAt(String updatedAt) {
    this.updatedAt = updatedAt;
  }

  
  @JsonProperty("uuid")
  public String getUuid() {
    return uuid;
  }

  @JsonProperty("uuid")
  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  
  @JsonProperty("unit_system")
  public String getUnitSystem() {
    return unitSystem;
  }

  @JsonProperty("unit_system")
  public void setUnitSystem(String unitSystem) {
    this.unitSystem = unitSystem;
  }
  
  
  @JsonProperty("referral_agent_id")
  public Integer getReferralAgentId() {
    return referralAgentId;
  }

  @JsonProperty("referral_agent_id")
  public void setReferralAgentId(Integer referralAgentId) {
    this.referralAgentId = referralAgentId;
  }

  
  @JsonProperty("type")
  public String getType() {
    return type;
  }

  @JsonProperty("type")
  public void setType(String type) {
    this.type = type;
  }

  
  @JsonProperty("payment_status")
  public String getPaymentStatus() {
    return paymentStatus;
  }

  @JsonProperty("payment_status")
  public void setPaymentStatus(String paymentStatus) {
    this.paymentStatus = paymentStatus;
  }

  
  @JsonProperty("payment_status_updated_at")
  public String getPaymentStatusUpdatedAt() {
    return paymentStatusUpdatedAt;
  }

  @JsonProperty("payment_status_updated_at")
  public void setPaymentStatusUpdatedAt(String paymentStatusUpdatedAt) {
    this.paymentStatusUpdatedAt = paymentStatusUpdatedAt;
  }

  
  @JsonProperty("status")
  public String getStatus() {
    return status;
  }

  @JsonProperty("status")
  public void setStatus(String status) {
    this.status = status;
  }

  
  @JsonProperty("status_updated_at")
  public String getStatusUpdatedAt() {
    return statusUpdatedAt;
  }

  @JsonProperty("status_updated_at")
  public void setStatusUpdatedAt(String statusUpdatedAt) {
    this.statusUpdatedAt = statusUpdatedAt;
  }

  
  @JsonProperty("billing_start_date")
  public String getBillingStartDate() {
    return billingStartDate;
  }

  @JsonProperty("billing_start_date")
  public void setBillingStartDate(String billingStartDate) {
    this.billingStartDate = billingStartDate;
  }

  
  @JsonProperty("billing_renewal_date")
  public String getBillingRenewalDate() {
    return billingRenewalDate;
  }

  @JsonProperty("billing_renewal_date")
  public void setBillingRenewalDate(String billingRenewalDate) {
    this.billingRenewalDate = billingRenewalDate;
  }

  
  @JsonProperty("stripe_customer_id")
  public String getStripeCustomerId() {
    return stripeCustomerId;
  }

  @JsonProperty("stripe_customer_id")
  public void setStripeCustomerId(String stripeCustomerId) {
    this.stripeCustomerId = stripeCustomerId;
  }
  
  
  @JsonProperty("payment_method_id")
  public String getPaymentMethodId() {
    return paymentMethodId;
  }

  @JsonProperty("payment_method_id")
  public void setPaymentMethodId(String paymentMethodId) {
    this.paymentMethodId = paymentMethodId;
  }
  

  @JsonProperty("payment_method_type")
  public String getPaymentMethodType() {
    return paymentMethodType;
  }

  @JsonProperty("payment_method_type")
  public void setPaymentMethodType(String paymentMethodType) {
    this.paymentMethodType = paymentMethodType;
  }

  
  @JsonProperty("payment_method_name")
  public String getPaymentMethodName() {
    return paymentMethodName;
  }

  @JsonProperty("payment_method_name")
  public void setPaymentMethodName(String paymentMethodName) {
    this.paymentMethodName = paymentMethodName;
  }

  
  @JsonProperty("payment_method_stripe_source_id")
  public String getPaymentMethodStripeSourceId() {
    return paymentMethodStripeSourceId;
  }

  @JsonProperty("payment_method_stripe_source_id")
  public void setPaymentMethodStripeSourceId(String paymentMethodStripeSourceId) {
    this.paymentMethodStripeSourceId = paymentMethodStripeSourceId;
  }

  
  @JsonProperty("payment_method_account_holder_name")
  public String getPaymentMethodAccountHolderName() {
    return paymentMethodAccountHolderName;
  }

  @JsonProperty("payment_method_account_holder_name")
  public void setPaymentMethodAccountHolderName(String paymentMethodAccountHolderName) {
    this.paymentMethodAccountHolderName = paymentMethodAccountHolderName;
  }

  
  @JsonProperty("payment_method_address")
  public String getPaymentMethodAddress() {
    return paymentMethodAddress;
  }

  @JsonProperty("payment_method_address")
  public void setPaymentMethodAddress(String paymentMethodAddress) {
    this.paymentMethodAddress = paymentMethodAddress;
  }

  
  @JsonProperty("payment_method_city")
  public String getPaymentMethodCity() {
    return paymentMethodCity;
  }

  @JsonProperty("payment_method_city")
  public void setPaymentMethodCity(String paymentMethodCity) {
    this.paymentMethodCity = paymentMethodCity;
  }

  
  @JsonProperty("payment_method_state")
  public String getPaymentMethodState() {
    return paymentMethodState;
  }

  @JsonProperty("payment_method_state")
  public void setPaymentMethodState(String paymentMethodState) {
    this.paymentMethodState = paymentMethodState;
  }

  
  @JsonProperty("payment_method_zip_code")
  public String getPaymentMethodZipCode() {
    return paymentMethodZipCode;
  }

  @JsonProperty("payment_method_zip_code")
  public void setPaymentMethodZipCode(String paymentMethodZipCode) {
    this.paymentMethodZipCode = paymentMethodZipCode;
  }

  
  @JsonProperty("payment_method_phone_number")
  public String getPaymentMethodPhoneNumber() {
    return paymentMethodPhoneNumber;
  }

  @JsonProperty("payment_method_phone_number")
  public void setPaymentMethodPhoneNumber(String paymentMethodPhoneNumber) {
    this.paymentMethodPhoneNumber = paymentMethodPhoneNumber;
  }
  
  
  @JsonProperty("payment_method_card_brand")
  public String getPaymentMethodCardBrand() {
    return paymentMethodCardBrand;
  }

  @JsonProperty("payment_method_card_brand")
  public void setPaymentMethodCardBrand(String paymentMethodCardBrand) {
    this.paymentMethodCardBrand = paymentMethodCardBrand;
  }
  
  
  @JsonProperty("payment_method_card_expiry")
  public String getPaymentMethodCardExpiry() {
    return paymentMethodCardExpiry;
  }

  @JsonProperty("payment_method_card_expiry")
  public void setPaymentMethodCardExpiry(String paymentMethodCardExpiry) {
    this.paymentMethodCardExpiry = paymentMethodCardExpiry;
  }
  
  
  @JsonProperty("payment_method_card_last_four")
  public String getPaymentMethodCardLastFour() {
    return paymentMethodCardLastFour;
  }

  @JsonProperty("payment_method_card_last_four")
  public void setPaymentMethodCardLastFour(String paymentMethodCardLastFour) {
    this.paymentMethodCardLastFour = paymentMethodCardLastFour;
  }

  
  @JsonProperty("allow_out_of_band_buildings")
  public boolean getAllowOutOfBandBuildings() {
    return allowOutOfBandBuildings;
  }

  @JsonProperty("allow_out_of_band_buildings")
  public void setAllowOutOfBandBuildings(boolean allowOutOfBandBuildings) {
    this.allowOutOfBandBuildings = allowOutOfBandBuildings;
  }
  
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((billingRenewalDate == null) ? 0 : billingRenewalDate.hashCode());
    result = prime * result + ((billingStartDate == null) ? 0 : billingStartDate.hashCode());
    result = prime * result + ((createdAt == null) ? 0 : createdAt.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((parentId == null) ? 0 : parentId.hashCode());
    result = prime * result + ((paymentMethodAccountHolderName == null) ? 0
        : paymentMethodAccountHolderName.hashCode());
    result =
        prime * result + ((paymentMethodAddress == null) ? 0 : paymentMethodAddress.hashCode());
    result =
        prime * result + ((paymentMethodCardBrand == null) ? 0 : paymentMethodCardBrand.hashCode());
    result = prime * result
        + ((paymentMethodCardExpiry == null) ? 0 : paymentMethodCardExpiry.hashCode());
    result = prime * result
        + ((paymentMethodCardLastFour == null) ? 0 : paymentMethodCardLastFour.hashCode());
    result = prime * result + ((paymentMethodCity == null) ? 0 : paymentMethodCity.hashCode());
    result = prime * result + ((paymentMethodId == null) ? 0 : paymentMethodId.hashCode());
    result = prime * result + ((paymentMethodName == null) ? 0 : paymentMethodName.hashCode());
    result = prime * result
        + ((paymentMethodPhoneNumber == null) ? 0 : paymentMethodPhoneNumber.hashCode());
    result = prime * result + ((paymentMethodState == null) ? 0 : paymentMethodState.hashCode());
    result = prime * result
        + ((paymentMethodStripeSourceId == null) ? 0 : paymentMethodStripeSourceId.hashCode());
    result = prime * result + ((paymentMethodType == null) ? 0 : paymentMethodType.hashCode());
    result =
        prime * result + ((paymentMethodZipCode == null) ? 0 : paymentMethodZipCode.hashCode());
    result = prime * result + ((paymentStatus == null) ? 0 : paymentStatus.hashCode());
    result =
        prime * result + ((paymentStatusUpdatedAt == null) ? 0 : paymentStatusUpdatedAt.hashCode());
    result = prime * result + ((referralAgentId == null) ? 0 : referralAgentId.hashCode());
    result = prime * result + ((status == null) ? 0 : status.hashCode());
    result = prime * result + ((statusUpdatedAt == null) ? 0 : statusUpdatedAt.hashCode());
    result = prime * result + ((stripeCustomerId == null) ? 0 : stripeCustomerId.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    result = prime * result + ((updatedAt == null) ? 0 : updatedAt.hashCode());
    result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
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
    DistributorDto other = (DistributorDto) obj;
    if (billingRenewalDate == null) {
      if (other.billingRenewalDate != null)
        return false;
    } else if (!billingRenewalDate.equals(other.billingRenewalDate))
      return false;
    if (billingStartDate == null) {
      if (other.billingStartDate != null)
        return false;
    } else if (!billingStartDate.equals(other.billingStartDate))
      return false;
    if (createdAt == null) {
      if (other.createdAt != null)
        return false;
    } else if (!createdAt.equals(other.createdAt))
      return false;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (parentId == null) {
      if (other.parentId != null)
        return false;
    } else if (!parentId.equals(other.parentId))
      return false;
    if (paymentMethodAccountHolderName == null) {
      if (other.paymentMethodAccountHolderName != null)
        return false;
    } else if (!paymentMethodAccountHolderName.equals(other.paymentMethodAccountHolderName))
      return false;
    if (paymentMethodAddress == null) {
      if (other.paymentMethodAddress != null)
        return false;
    } else if (!paymentMethodAddress.equals(other.paymentMethodAddress))
      return false;
    if (paymentMethodCardBrand == null) {
      if (other.paymentMethodCardBrand != null)
        return false;
    } else if (!paymentMethodCardBrand.equals(other.paymentMethodCardBrand))
      return false;
    if (paymentMethodCardExpiry == null) {
      if (other.paymentMethodCardExpiry != null)
        return false;
    } else if (!paymentMethodCardExpiry.equals(other.paymentMethodCardExpiry))
      return false;
    if (paymentMethodCardLastFour == null) {
      if (other.paymentMethodCardLastFour != null)
        return false;
    } else if (!paymentMethodCardLastFour.equals(other.paymentMethodCardLastFour))
      return false;
    if (paymentMethodCity == null) {
      if (other.paymentMethodCity != null)
        return false;
    } else if (!paymentMethodCity.equals(other.paymentMethodCity))
      return false;
    if (paymentMethodId == null) {
      if (other.paymentMethodId != null)
        return false;
    } else if (!paymentMethodId.equals(other.paymentMethodId))
      return false;
    if (paymentMethodName == null) {
      if (other.paymentMethodName != null)
        return false;
    } else if (!paymentMethodName.equals(other.paymentMethodName))
      return false;
    if (paymentMethodPhoneNumber == null) {
      if (other.paymentMethodPhoneNumber != null)
        return false;
    } else if (!paymentMethodPhoneNumber.equals(other.paymentMethodPhoneNumber))
      return false;
    if (paymentMethodState == null) {
      if (other.paymentMethodState != null)
        return false;
    } else if (!paymentMethodState.equals(other.paymentMethodState))
      return false;
    if (paymentMethodStripeSourceId == null) {
      if (other.paymentMethodStripeSourceId != null)
        return false;
    } else if (!paymentMethodStripeSourceId.equals(other.paymentMethodStripeSourceId))
      return false;
    if (paymentMethodType == null) {
      if (other.paymentMethodType != null)
        return false;
    } else if (!paymentMethodType.equals(other.paymentMethodType))
      return false;
    if (paymentMethodZipCode == null) {
      if (other.paymentMethodZipCode != null)
        return false;
    } else if (!paymentMethodZipCode.equals(other.paymentMethodZipCode))
      return false;
    if (paymentStatus == null) {
      if (other.paymentStatus != null)
        return false;
    } else if (!paymentStatus.equals(other.paymentStatus))
      return false;
    if (paymentStatusUpdatedAt == null) {
      if (other.paymentStatusUpdatedAt != null)
        return false;
    } else if (!paymentStatusUpdatedAt.equals(other.paymentStatusUpdatedAt))
      return false;
    if (referralAgentId == null) {
      if (other.referralAgentId != null)
        return false;
    } else if (!referralAgentId.equals(other.referralAgentId))
      return false;
    if (status == null) {
      if (other.status != null)
        return false;
    } else if (!status.equals(other.status))
      return false;
    if (statusUpdatedAt == null) {
      if (other.statusUpdatedAt != null)
        return false;
    } else if (!statusUpdatedAt.equals(other.statusUpdatedAt))
      return false;
    if (stripeCustomerId == null) {
      if (other.stripeCustomerId != null)
        return false;
    } else if (!stripeCustomerId.equals(other.stripeCustomerId))
      return false;
    if (type == null) {
      if (other.type != null)
        return false;
    } else if (!type.equals(other.type))
      return false;
    if (updatedAt == null) {
      if (other.updatedAt != null)
        return false;
    } else if (!updatedAt.equals(other.updatedAt))
      return false;
    if (uuid == null) {
      if (other.uuid != null)
        return false;
    } else if (!uuid.equals(other.uuid))
      return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("DistributorDto [id=").append(id).append(", parentId=").append(parentId)
        .append(", name=").append(name).append(", createdAt=").append(createdAt)
        .append(", updatedAt=").append(updatedAt).append(", uuid=").append(uuid)
        .append(", referralAgentId=").append(referralAgentId).append(", type=").append(type)
        .append(", paymentStatus=").append(paymentStatus).append(", paymentStatusUpdatedAt=")
        .append(paymentStatusUpdatedAt).append(", status=").append(status)
        .append(", statusUpdatedAt=").append(statusUpdatedAt).append(", billingStartDate=")
        .append(billingStartDate).append(", billingRenewalDate=").append(billingRenewalDate)
        .append(", stripeCustomerId=").append(stripeCustomerId).append(", paymentMethodId=")
        .append(paymentMethodId).append(", paymentMethodType=").append(paymentMethodType)
        .append(", paymentMethodName=").append(paymentMethodName)
        .append(", paymentMethodStripeSourceId=").append(paymentMethodStripeSourceId)
        .append(", paymentMethodAccountHolderName=").append(paymentMethodAccountHolderName)
        .append(", paymentMethodAddress=").append(paymentMethodAddress)
        .append(", paymentMethodCity=").append(paymentMethodCity).append(", paymentMethodState=")
        .append(paymentMethodState).append(", paymentMethodZipCode=").append(paymentMethodZipCode)
        .append(", paymentMethodPhoneNumber=").append(paymentMethodPhoneNumber)
        .append(", paymentMethodCardBrand=").append(paymentMethodCardBrand)
        .append(", paymentMethodCardExpiry=").append(paymentMethodCardExpiry)
        .append(", paymentMethodCardLastFour=").append(paymentMethodCardLastFour)
        .append(", allowOutOfBandBuildings=").append(allowOutOfBandBuildings)
        .append("]");
    return builder.toString();
  }
}
