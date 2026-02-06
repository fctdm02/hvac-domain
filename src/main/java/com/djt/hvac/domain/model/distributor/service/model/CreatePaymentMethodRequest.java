package com.djt.hvac.domain.model.distributor.service.model;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = CreatePaymentMethodRequest.Builder.class)
public class CreatePaymentMethodRequest {
  private final Integer parentDistributorId;
  private final String paymentMethodType;
  private final String name;
  private final String stripeSourceId;
  private final String accountHolderName;
  private final String address;
  private final String city;
  private final String state;
  private final String zipCode;
  private final String phoneNumber;
  private final String cardBrand;
  private final String cardExpiry;
  private final String cardLastFour;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (CreatePaymentMethodRequest createPaymentMethodRequest) {
    return new Builder(createPaymentMethodRequest);
  }

  private CreatePaymentMethodRequest (Builder builder) {
    this.parentDistributorId = builder.parentDistributorId;
    this.paymentMethodType = builder.paymentMethodType;
    this.name = builder.name;
    this.stripeSourceId = builder.stripeSourceId;
    this.accountHolderName = builder.accountHolderName;
    this.address = builder.address;
    this.city = builder.city;
    this.state = builder.state;
    this.zipCode = builder.zipCode;
    this.phoneNumber = builder.phoneNumber;
    this.cardBrand = builder.cardBrand;
    this.cardExpiry = builder.cardExpiry;
    this.cardLastFour = builder.cardLastFour;
  }

  public Integer getParentDistributorId() {
    return parentDistributorId;
  }

  public String getPaymentMethodType() {
    return paymentMethodType;
  }

  public String getName() {
    return name;
  }

  public String getStripeSourceId() {
    return stripeSourceId;
  }

  public String getAccountHolderName() {
    return accountHolderName;
  }

  public String getAddress() {
    return address;
  }

  public String getCity() {
    return city;
  }

  public String getState() {
    return state;
  }

  public String getZipCode() {
    return zipCode;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public String getCardBrand() {
    return cardBrand;
  }

  public String getCardExpiry() {
    return cardExpiry;
  }

  public String getCardLastFour() {
    return cardLastFour;
  }

  @JsonPOJOBuilder
  public static class Builder {
    private Integer parentDistributorId;
    private String paymentMethodType;
    private String name;
    private String stripeSourceId;
    private String accountHolderName;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String phoneNumber;
    private String cardBrand;
    private String cardExpiry;
    private String cardLastFour;

    private Builder() {}

    private Builder(CreatePaymentMethodRequest createPaymentMethodRequest) {
      requireNonNull(createPaymentMethodRequest, "createPaymentMethodRequest cannot be null");
      this.parentDistributorId = createPaymentMethodRequest.parentDistributorId;
      this.paymentMethodType = createPaymentMethodRequest.paymentMethodType;
      this.name = createPaymentMethodRequest.name;
      this.stripeSourceId = createPaymentMethodRequest.stripeSourceId;
      this.accountHolderName = createPaymentMethodRequest.accountHolderName;
      this.address = createPaymentMethodRequest.address;
      this.city = createPaymentMethodRequest.city;
      this.state = createPaymentMethodRequest.state;
      this.zipCode = createPaymentMethodRequest.zipCode;
      this.phoneNumber = createPaymentMethodRequest.phoneNumber;
      this.cardBrand = createPaymentMethodRequest.cardBrand;
      this.cardExpiry = createPaymentMethodRequest.cardExpiry;
      this.cardLastFour = createPaymentMethodRequest.cardLastFour;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withParentDistributorId(Integer parentDistributorId) {
      requireNonNull(parentDistributorId, "parentDistributorId cannot be null");
      this.parentDistributorId = parentDistributorId;
      return this;
    }

    public Builder withPaymentMethodType(String paymentMethodType) {
      requireNonNull(paymentMethodType, "paymentMethodType cannot be null");
      this.paymentMethodType = paymentMethodType;
      return this;
    }

    public Builder withName(String name) {
      requireNonNull(name, "name cannot be null");
      this.name = name;
      return this;
    }

    public Builder withStripeSourceId(String stripeSourceId) {
      requireNonNull(stripeSourceId, "stripeSourceId cannot be null");
      this.stripeSourceId = stripeSourceId;
      return this;
    }

    public Builder withAccountHolderName(String accountHolderName) {
      requireNonNull(accountHolderName, "accountHolderName cannot be null");
      this.accountHolderName = accountHolderName;
      return this;
    }

    public Builder withAddress(String address) {
      requireNonNull(address, "address cannot be null");
      this.address = address;
      return this;
    }

    public Builder withCity(String city) {
      requireNonNull(city, "city cannot be null");
      this.city = city;
      return this;
    }

    public Builder withState(String state) {
      requireNonNull(state, "state cannot be null");
      this.state = state;
      return this;
    }

    public Builder withZipCode(String zipCode) {
      requireNonNull(zipCode, "zipCode cannot be null");
      this.zipCode = zipCode;
      return this;
    }

    public Builder withPhoneNumber(String phoneNumber) {
      requireNonNull(phoneNumber, "phoneNumber cannot be null");
      this.phoneNumber = phoneNumber;
      return this;
    }

    public Builder withCardBrand(String cardBrand) {
      requireNonNull(cardBrand, "cardBrand cannot be null");
      this.cardBrand = cardBrand;
      return this;
    }

    public Builder withCardExpiry(String cardExpiry) {
      requireNonNull(cardExpiry, "cardExpiry cannot be null");
      this.cardExpiry = cardExpiry;
      return this;
    }

    public Builder withCardLastFour(String cardLastFour) {
      requireNonNull(cardLastFour, "cardLastFour cannot be null");
      this.cardLastFour = cardLastFour;
      return this;
    }

    public CreatePaymentMethodRequest build() {
      requireNonNull(parentDistributorId, "parentDistributorId cannot be null");
      requireNonNull(paymentMethodType, "paymentMethodType cannot be null");
      requireNonNull(name, "name cannot be null");
      requireNonNull(stripeSourceId, "stripeSourceId cannot be null");
      requireNonNull(accountHolderName, "accountHolderName cannot be null");
      requireNonNull(address, "address cannot be null");
      requireNonNull(city, "city cannot be null");
      requireNonNull(state, "state cannot be null");
      requireNonNull(zipCode, "zipCode cannot be null");
      requireNonNull(phoneNumber, "phoneNumber cannot be null");
      requireNonNull(cardBrand, "cardBrand cannot be null");
      requireNonNull(cardExpiry, "cardExpiry cannot be null");
      requireNonNull(cardLastFour, "cardLastFour cannot be null");
      return new CreatePaymentMethodRequest(this);
    }
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
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((parentDistributorId == null) ? 0 : parentDistributorId.hashCode());
    result = prime * result + ((paymentMethodType == null) ? 0 : paymentMethodType.hashCode());
    result = prime * result + ((phoneNumber == null) ? 0 : phoneNumber.hashCode());
    result = prime * result + ((state == null) ? 0 : state.hashCode());
    result = prime * result + ((stripeSourceId == null) ? 0 : stripeSourceId.hashCode());
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
    CreatePaymentMethodRequest other = (CreatePaymentMethodRequest) obj;
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
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (parentDistributorId == null) {
      if (other.parentDistributorId != null)
        return false;
    } else if (!parentDistributorId.equals(other.parentDistributorId))
      return false;
    if (paymentMethodType == null) {
      if (other.paymentMethodType != null)
        return false;
    } else if (!paymentMethodType.equals(other.paymentMethodType))
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
    if (zipCode == null) {
      if (other.zipCode != null)
        return false;
    } else if (!zipCode.equals(other.zipCode))
      return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder2 = new StringBuilder();
    builder2.append("CreatePaymentMethodRequest [parentDistributorId=").append(parentDistributorId)
        .append(", paymentMethodType=").append(paymentMethodType).append(", name=").append(name)
        .append(", stripeSourceId=").append(stripeSourceId).append(", accountHolderName=")
        .append(accountHolderName).append(", address=").append(address).append(", city=")
        .append(city).append(", state=").append(state).append(", zipCode=").append(zipCode)
        .append(", phoneNumber=").append(phoneNumber).append(", cardBrand=").append(cardBrand)
        .append(", cardExpiry=").append(cardExpiry).append(", cardLastFour=").append(cardLastFour)
        .append("]");
    return builder2.toString();
  }
}