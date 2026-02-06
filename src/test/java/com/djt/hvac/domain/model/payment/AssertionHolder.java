package com.djt.hvac.domain.model.payment;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

import com.djt.hvac.domain.model.customer.enums.CustomerPaymentStatus;
import com.djt.hvac.domain.model.customer.enums.CustomerStatus;
import com.djt.hvac.domain.model.distributor.enums.DistributorPaymentStatus;
import com.djt.hvac.domain.model.distributor.enums.DistributorStatus;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.BuildingPaymentStatus;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.BuildingStatus;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = AssertionHolder.Builder.class)
public class AssertionHolder {
  private final DistributorStatus expectedDistributorConfigStatus;
  private final DistributorPaymentStatus expectedDistributorPaymentStatus;
  private final CustomerStatus expectedCustomerConfigStatus;
  private final CustomerPaymentStatus expectedCustomerPaymentStatus;
  private final BuildingStatus expectedBuildingConfigStatus;
  private final BuildingPaymentStatus expectedBuildingPaymentStatus;
  private final Integer expectedCustomerCount;
  private final Integer expectedBuildingCount;
  private final Integer expectedBuildingMappedPointCount;
  private final Boolean expectedBuildingPendingDeletion;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (AssertionHolder assertionHolder) {
    return new Builder(assertionHolder);
  }

  private AssertionHolder (Builder builder) {
    this.expectedDistributorConfigStatus = builder.expectedDistributorConfigStatus;
    this.expectedDistributorPaymentStatus = builder.expectedDistributorPaymentStatus;
    this.expectedCustomerConfigStatus = builder.expectedCustomerConfigStatus;
    this.expectedCustomerPaymentStatus = builder.expectedCustomerPaymentStatus;
    this.expectedBuildingConfigStatus = builder.expectedBuildingConfigStatus;
    this.expectedBuildingPaymentStatus = builder.expectedBuildingPaymentStatus;
    this.expectedCustomerCount = builder.expectedCustomerCount;
    this.expectedBuildingCount = builder.expectedBuildingCount;
    this.expectedBuildingMappedPointCount = builder.expectedBuildingMappedPointCount;
    this.expectedBuildingPendingDeletion = builder.expectedBuildingPendingDeletion;
  }

  public DistributorStatus getExpectedDistributorConfigStatus() {
    return expectedDistributorConfigStatus;
  }

  public DistributorPaymentStatus getExpectedDistributorPaymentStatus() {
    return expectedDistributorPaymentStatus;
  }

  public CustomerStatus getExpectedCustomerConfigStatus() {
    return expectedCustomerConfigStatus;
  }

  public CustomerPaymentStatus getExpectedCustomerPaymentStatus() {
    return expectedCustomerPaymentStatus;
  }

  public BuildingStatus getExpectedBuildingConfigStatus() {
    return expectedBuildingConfigStatus;
  }

  public BuildingPaymentStatus getExpectedBuildingPaymentStatus() {
    return expectedBuildingPaymentStatus;
  }

  public Integer getExpectedCustomerCount() {
    return expectedCustomerCount;
  }

  public Integer getExpectedBuildingCount() {
    return expectedBuildingCount;
  }

  public Integer getExpectedBuildingMappedPointCount() {
    return expectedBuildingMappedPointCount;
  }

  public Boolean getExpectedBuildingPendingDeletion() {
    return expectedBuildingPendingDeletion;
  }

  @JsonPOJOBuilder
  public static class Builder {
    private DistributorStatus expectedDistributorConfigStatus;
    private DistributorPaymentStatus expectedDistributorPaymentStatus;
    private CustomerStatus expectedCustomerConfigStatus;
    private CustomerPaymentStatus expectedCustomerPaymentStatus;
    private BuildingStatus expectedBuildingConfigStatus;
    private BuildingPaymentStatus expectedBuildingPaymentStatus;
    private Integer expectedCustomerCount;
    private Integer expectedBuildingCount;
    private Integer expectedBuildingMappedPointCount;
    private Boolean expectedBuildingPendingDeletion;

    private Builder() {}

    private Builder(AssertionHolder assertionHolder) {
      requireNonNull(assertionHolder, "assertionHolder cannot be null");
      this.expectedDistributorConfigStatus = assertionHolder.expectedDistributorConfigStatus;
      this.expectedDistributorPaymentStatus = assertionHolder.expectedDistributorPaymentStatus;
      this.expectedCustomerConfigStatus = assertionHolder.expectedCustomerConfigStatus;
      this.expectedCustomerPaymentStatus = assertionHolder.expectedCustomerPaymentStatus;
      this.expectedBuildingConfigStatus = assertionHolder.expectedBuildingConfigStatus;
      this.expectedBuildingPaymentStatus = assertionHolder.expectedBuildingPaymentStatus;
      this.expectedCustomerCount = assertionHolder.expectedCustomerCount;
      this.expectedBuildingCount = assertionHolder.expectedBuildingCount;
      this.expectedBuildingMappedPointCount = assertionHolder.expectedBuildingMappedPointCount;
      this.expectedBuildingPendingDeletion = assertionHolder.expectedBuildingPendingDeletion;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withExpectedDistributorConfigStatus(DistributorStatus expectedDistributorConfigStatus) {
      this.expectedDistributorConfigStatus = expectedDistributorConfigStatus;
      return this;
    }

    public Builder withExpectedDistributorPaymentStatus(DistributorPaymentStatus expectedDistributorPaymentStatus) {
      this.expectedDistributorPaymentStatus = expectedDistributorPaymentStatus;
      return this;
    }

    public Builder withExpectedCustomerConfigStatus(CustomerStatus expectedCustomerConfigStatus) {
      this.expectedCustomerConfigStatus = expectedCustomerConfigStatus;
      return this;
    }

    public Builder withExpectedCustomerPaymentStatus(CustomerPaymentStatus expectedCustomerPaymentStatus) {
      this.expectedCustomerPaymentStatus = expectedCustomerPaymentStatus;
      return this;
    }

    public Builder withExpectedBuildingConfigStatus(BuildingStatus expectedBuildingConfigStatus) {
      this.expectedBuildingConfigStatus = expectedBuildingConfigStatus;
      return this;
    }

    public Builder withExpectedBuildingPaymentStatus(BuildingPaymentStatus expectedBuildingPaymentStatus) {
      this.expectedBuildingPaymentStatus = expectedBuildingPaymentStatus;
      return this;
    }

    public Builder withExpectedCustomerCount(Integer expectedCustomerCount) {
      this.expectedCustomerCount = expectedCustomerCount;
      return this;
    }

    public Builder withExpectedBuildingCount(Integer expectedBuildingCount) {
      this.expectedBuildingCount = expectedBuildingCount;
      return this;
    }

    public Builder withExpectedBuildingMappedPointCount(Integer expectedBuildingMappedPointCount) {
      this.expectedBuildingMappedPointCount = expectedBuildingMappedPointCount;
      return this;
    }

    public Builder withExpectedBuildingPendingDeletion(Boolean expectedBuildingPendingDeletion) {
      this.expectedBuildingPendingDeletion = expectedBuildingPendingDeletion;
      return this;
    }

    public AssertionHolder build() {
      return new AssertionHolder(this);
    }
  }
}