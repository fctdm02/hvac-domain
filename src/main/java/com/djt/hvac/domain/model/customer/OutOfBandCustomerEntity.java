//@formatter:off
package com.djt.hvac.domain.model.customer;

import java.sql.Timestamp;
import java.util.UUID;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.customer.enums.CustomerPaymentStatus;
import com.djt.hvac.domain.model.customer.enums.CustomerStatus;
import com.djt.hvac.domain.model.dictionary.enums.UnitSystem;
import com.djt.hvac.domain.model.distributor.AbstractDistributorEntity;
import com.djt.hvac.domain.model.distributor.OutOfBandDistributorEntity;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;

public class OutOfBandCustomerEntity extends AbstractCustomerEntity {
  private static final long serialVersionUID = 1L;
  public static OutOfBandCustomerEntity buildCustomerStubForPortfolio(Integer customerId) {
    
    return buildCustomerStubForPortfolio(
        customerId,
        UUID.randomUUID().toString(),
        null);
  }
  
  public static OutOfBandCustomerEntity buildCustomerStubForPortfolio(
      Integer customerId,
      String uuid,
      PortfolioEntity portfolio) {
    
    return new OutOfBandCustomerEntity(
        customerId,
        OutOfBandDistributorEntity.buildResoluteDistributorStub(),
        "Customer ID=" + customerId,
        uuid,
        UnitSystem.IP,
        AbstractEntity.getTimeKeeper().getCurrentTimestamp(),
        AbstractEntity.getTimeKeeper().getCurrentTimestamp(),
        AbstractEntity.getTimeKeeper().getCurrentTimestamp(),
        CustomerStatus.CREATED,
        AbstractEntity.getTimeKeeper().getCurrentTimestamp(),
        CustomerPaymentStatus.UP_TO_DATE,
        AbstractEntity.getTimeKeeper().getCurrentTimestamp(),
        portfolio);
  }
  
  // For new instances (i.e. have not been persisted yet)
  public OutOfBandCustomerEntity(
      AbstractDistributorEntity parentDistributor,
      String name,
      UnitSystem unitSystem) {
    super(
        parentDistributor,
        name,
        unitSystem);
  }
    
  public OutOfBandCustomerEntity(
      Integer persistentIdentity,
      AbstractDistributorEntity parentDistributor,
      String name,
      String uuid,
      UnitSystem unitSystem,
      Timestamp createdAt,
      Timestamp updatedAt,
      Timestamp startDate,
      CustomerStatus customerStatus,
      Timestamp customerStatusUpdatedAt,
      CustomerPaymentStatus customerPaymentStatus,
      Timestamp customerPaymentStatusUpdatedAt) {
    this(
        persistentIdentity,
        parentDistributor,
        name,
        uuid,
        unitSystem,
        createdAt,
        updatedAt,
        startDate,
        customerStatus,
        customerStatusUpdatedAt,
        customerPaymentStatus,
        customerPaymentStatusUpdatedAt,
        null);
  }
  
  public OutOfBandCustomerEntity(
      Integer persistentIdentity,
      AbstractDistributorEntity parentDistributor,
      String name,
      String uuid,
      UnitSystem unitSystem,
      Timestamp createdAt,
      Timestamp updatedAt,
      Timestamp startDate,
      CustomerStatus customerStatus,
      Timestamp customerStatusUpdatedAt,
      CustomerPaymentStatus customerPaymentStatus,
      Timestamp customerPaymentStatusUpdatedAt,
      PortfolioEntity portfolio) {
    super(
        persistentIdentity,
        parentDistributor,
        name,
        uuid,
        unitSystem,
        createdAt,
        updatedAt,
        startDate,
        customerStatus,
        customerStatusUpdatedAt,
        customerPaymentStatus,
        customerPaymentStatusUpdatedAt,
        portfolio);
  }
  
  @Override
  public boolean allowAutomaticConfiguration() {
    return false;
  }
}
//@formatter:on