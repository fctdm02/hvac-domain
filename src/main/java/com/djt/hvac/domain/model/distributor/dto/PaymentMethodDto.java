
package com.djt.hvac.domain.model.distributor.dto;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/*
 <pre>
 
 </pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "distributor_id",
    "type",
    "name",
    "stripe_source_id",
    "account_holder_name",
    "address",
    "city",
    "state",
    "zip_code",
    "phone_number",
    "card_brand",
    "card_expiry",
    "card_last_four"
})
public class PaymentMethodDto implements Serializable {

  private final static long serialVersionUID = -628752462960824827L;
  
  @JsonProperty("id")
  private Integer id;
  
  @JsonProperty("distributor_id")
  private Integer distributorId;
  
  @JsonProperty("type")
  private String type;

  @JsonProperty("name")
  private String name;
  
  @JsonProperty("stripe_source_id")
  private String stripeSourceId;
  
  @JsonProperty("account_holder_name")
  private String accountHolderName;
  
  @JsonProperty("address")
  private String address;
  
  @JsonProperty("city")
  private String city;
  
  @JsonProperty("state")
  private String state;
  
  @JsonProperty("zip_code")
  private String zipCode;
  
  @JsonProperty("phone_number")
  private String phoneNumber;
  
  @JsonProperty("card_brand")
  private String cardBrand;

  @JsonProperty("card_expiry")
  private String cardExpiry;

  @JsonProperty("card_last_four")
  private String cardLastFour;
  

  @JsonProperty("id")
  public Integer getId() {
    return id;
  }

  @JsonProperty("id")
  public void setId(Integer id) {
    this.id = id;
  }

  
  @JsonProperty("distributor_id")
  public Integer getDistributorId() {
    return distributorId;
  }

  @JsonProperty("distributor_id")
  public void setDistributorId(Integer distributorId) {
    this.distributorId = distributorId;
  }

  
  @JsonProperty("type")
  public String getType() {
    return type;
  }

  @JsonProperty("type")
  public void setType(String type) {
    this.type = type;
  }

  
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  @JsonProperty("name")
  public void setName(String name) {
    this.name = name;
  }

  
  @JsonProperty("stripe_source_id")
  public String getStripeSourceId() {
    return stripeSourceId;
  }

  @JsonProperty("stripe_source_id")
  public void setStripeSourceId(String stripeSourceId) {
    this.stripeSourceId = stripeSourceId;
  }

  
  @JsonProperty("account_holder_name")
  public String getAccountHolderName() {
    return accountHolderName;
  }

  @JsonProperty("account_holder_name")
  public void setAccountHolderName(String accountHolderName) {
    this.accountHolderName = accountHolderName;
  }

  
  @JsonProperty("address")
  public String getAddress() {
    return address;
  }

  @JsonProperty("address")
  public void setAddress(String address) {
    this.address = address;
  }

  
  @JsonProperty("city")
  public String getCity() {
    return city;
  }

  @JsonProperty("city")
  public void setCity(String city) {
    this.city = city;
  }

  
  @JsonProperty("state")
  public String getState() {
    return state;
  }

  @JsonProperty("state")
  public void setState(String state) {
    this.state = state;
  }

  
  @JsonProperty("zip_code")
  public String getZipCode() {
    return zipCode;
  }

  @JsonProperty("zip_code")
  public void setZipCode(String zipCode) {
    this.zipCode = zipCode;
  }

  
  @JsonProperty("phone_number")
  public String getPhoneNumber() {
    return phoneNumber;
  }

  @JsonProperty("phone_number")
  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  
  @JsonProperty("card_brand")
  public String getCardBrand() {
    return cardBrand;
  }

  @JsonProperty("card_brand")
  public void setCardBrand(String cardBrand) {
    this.cardBrand = cardBrand;
  }
  
  
  @JsonProperty("card_expiry")
  public String getCardExpiry() {
    return cardExpiry;
  }

  @JsonProperty("card_expiry")
  public void setCardExpiry(String cardExpiry) {
    this.cardExpiry = cardExpiry;
  }
  
  
  @JsonProperty("card_last_four")
  public String getCardLastFour() {
    return cardLastFour;
  }

  @JsonProperty("card_last_four")
  public void setCardLastFour(String cardLastFour) {
    this.cardLastFour = cardLastFour;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((accountHolderName == null) ? 0 : accountHolderName.hashCode());
    result = prime * result + ((address == null) ? 0 : address.hashCode());
    result = prime * result + ((cardBrand == null) ? 0 : cardBrand.hashCode());
    result = prime * result + ((cardExpiry == null) ? 0 : cardExpiry.hashCode());
    result = prime * result + ((cardLastFour == null) ? 0 : cardLastFour.hashCode());
    result = prime * result + ((city == null) ? 0 : city.hashCode());
    result = prime * result + ((distributorId == null) ? 0 : distributorId.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((phoneNumber == null) ? 0 : phoneNumber.hashCode());
    result = prime * result + ((state == null) ? 0 : state.hashCode());
    result = prime * result + ((stripeSourceId == null) ? 0 : stripeSourceId.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    result = prime * result + ((zipCode == null) ? 0 : zipCode.hashCode());
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
    PaymentMethodDto other = (PaymentMethodDto) obj;
    if (accountHolderName == null) {
      if (other.accountHolderName != null)
        return false;
    } else if (!accountHolderName.equals(other.accountHolderName))
      return false;
    if (address == null) {
      if (other.address != null)
        return false;
    } else if (!address.equals(other.address))
      return false;
    if (cardBrand == null) {
      if (other.cardBrand != null)
        return false;
    } else if (!cardBrand.equals(other.cardBrand))
      return false;
    if (cardExpiry == null) {
      if (other.cardExpiry != null)
        return false;
    } else if (!cardExpiry.equals(other.cardExpiry))
      return false;
    if (cardLastFour == null) {
      if (other.cardLastFour != null)
        return false;
    } else if (!cardLastFour.equals(other.cardLastFour))
      return false;
    if (city == null) {
      if (other.city != null)
        return false;
    } else if (!city.equals(other.city))
      return false;
    if (distributorId == null) {
      if (other.distributorId != null)
        return false;
    } else if (!distributorId.equals(other.distributorId))
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
    if (phoneNumber == null) {
      if (other.phoneNumber != null)
        return false;
    } else if (!phoneNumber.equals(other.phoneNumber))
      return false;
    if (state == null) {
      if (other.state != null)
        return false;
    } else if (!state.equals(other.state))
      return false;
    if (stripeSourceId == null) {
      if (other.stripeSourceId != null)
        return false;
    } else if (!stripeSourceId.equals(other.stripeSourceId))
      return false;
    if (type == null) {
      if (other.type != null)
        return false;
    } else if (!type.equals(other.type))
      return false;
    if (zipCode == null) {
      if (other.zipCode != null)
        return false;
    } else if (!zipCode.equals(other.zipCode))
      return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("PaymentMethodDto [id=").append(id).append(", distributorId=")
        .append(distributorId).append(", type=").append(type).append(", name=").append(name)
        .append(", stripeSourceId=").append(stripeSourceId).append(", accountHolderName=")
        .append(accountHolderName).append(", address=").append(address).append(", city=")
        .append(city).append(", state=").append(state).append(", zipCode=").append(zipCode)
        .append(", phoneNumber=").append(phoneNumber).append(", cardBrand=").append(cardBrand)
        .append(", cardExpiry=").append(cardExpiry).append(", cardLastFour=").append(cardLastFour)
        .append("]");
    return builder.toString();
  }
}
