//@formatter:off
package com.djt.hvac.domain.model.customer.service;

import java.util.Collection;
import java.util.List;

import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.customer.AbstractCustomerEntity;
import com.djt.hvac.domain.model.customer.enums.CustomerType;
import com.djt.hvac.domain.model.distributor.AbstractDistributorEntity;
import com.djt.hvac.domain.model.rawpoint.RawPointEntity;

/**
 * 
 * @author tommyers
 * 
 */
public interface CustomerService {

  /**
   * 
   * @param loadDistributorPaymentMethods Whether or not to load distributor payment methods
   * @param loadDistributorUsers Whether or not to load distributor users
   * 
   * @return All customers
   */
  Collection<AbstractCustomerEntity> loadAllCustomers(
      boolean loadDistributorPaymentMethods,
      boolean loadDistributorUsers);
  
  /**
   * 
   * @param customerId The customer to load
   * @param loadDistributorPaymentMethods Whether or not to load distributor payment methods
   * @param loadDistributorUsers Whether or not to load distributor users
   * 
   * @return The given customer
   * 
   * @throws EntityDoesNotExistException If the customer does not exist
   */
  AbstractCustomerEntity loadCustomer(
      int customerId,
      boolean loadDistributorPaymentMethods,
      boolean loadDistributorUsers) 
  throws 
      EntityDoesNotExistException;
  
  /**
   * 
   * @param customerId The customer to load
   * @param loadDistributorPaymentMethods Whether or not to load distributor payment methods
   * @param loadDistributorUsers Whether or not to load distributor users
   * @param loadRawPoints If <code>true</code>, loads the raw points for the given customer
   * 
   * @return The given customer
   * 
   * @throws EntityDoesNotExistException If the customer does not exist
   */
  AbstractCustomerEntity loadCustomer(
      int customerId,
      boolean loadDistributorPaymentMethods,
      boolean loadDistributorUsers, 
      boolean loadRawPoints)  
  throws 
      EntityDoesNotExistException;

  /**
   * This method loads raw points for the customer, but with filtering ability
   * 
   * @param customerId The customer to load
   * @param loadDistributorPaymentMethods Whether or not to load distributor payment methods
   * @param loadDistributorUsers Whether or not to load distributor users
   * @param loadUnmappedRawPointsOnly If <code>true</code>, loads unmapped raw points for the given customer
   * @param loadIgnoredRawPoints If <code>true</code>, only raw points marked as ignored are returned (assumes unmapped)
   * @param loadDeletedRawPoints If <code>true</code>, only raw points marked as deleted are returned (assumes unmapped)
   * 
   * @return The given customer
   * 
   * @throws EntityDoesNotExistException If the customer does not exist
   */
  AbstractCustomerEntity loadCustomer(
      int customerId,
      boolean loadDistributorPaymentMethods,
      boolean loadDistributorUsers, 
      boolean loadUnmappedRawPointsOnly,
      boolean loadIgnoredRawPoints,
      boolean loadDeletedRawPoints) 
  throws 
      EntityDoesNotExistException;
  
  /**
   * 
   * @param parentDistributor The parent distributor
   * @param customerType Must be one of: 'ONLINE', 'OUT_OF_BAND' or DEMO
   * @param name The customer name (must be unique among the parent distributor's child customers)
   * 
   * @return The newly created customer
   * 
   * @throws EntityAlreadyExistsException If the customer already exists
   * @throws EntityDoesNotExistException If the parent distributor does not exist
   */
  AbstractCustomerEntity createCustomer(
      AbstractDistributorEntity parentDistributor,
      CustomerType customerType,
      String name) 
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException;

  /**
   * 
   * @param parentDistributor The parent distributor
   * @param customerType Must be one of: 'ONLINE', 'OUT_OF_BAND' or DEMO
   * @param name The customer name (must be unique among the parent distributor's child customers)
   * @param unitSystemName The default unit system (IP or SI)
   * 
   * @return The newly created customer
   * 
   * @throws EntityAlreadyExistsException If the customer already exists
   * @throws EntityDoesNotExistException If the parent distributor does not exist
   */
  AbstractCustomerEntity createCustomer(
      AbstractDistributorEntity parentDistributor,
      CustomerType customerType,
      String name,
      String unitSystemName) 
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException;
  
  /**
   * 
   * @param parentDistributor The parent distributor
   * @param name The customer name (must be unique among the parent distributor's child customers)
   * @param isExpires Whether or not the demo customer expires after being in a CREATED state for 90 days
   * @param isInternal Whether or not the demo customer is considered to be for "internal use"
   * 
   * @return The newly created demo customer
   * 
   * @throws EntityAlreadyExistsException If the customer already exists
   * @throws EntityDoesNotExistException If the parent distributor does not exist
   */
  AbstractCustomerEntity createDemoCustomer(
      AbstractDistributorEntity parentDistributor,
      String name,
      Boolean isExpires,
      Boolean isInternal) 
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException;
  
  /**
   * 
   * @param customer The customer to update
   */
  AbstractCustomerEntity updateCustomer(AbstractCustomerEntity customer);

  /**
   * 
   * @param customer The customer to update
   * @param storeRawPoints If <code>true</code>, stores the raw points for the given customer
   */
  AbstractCustomerEntity updateCustomer(AbstractCustomerEntity customer, boolean storeRawPoints);
  
  /**
   * 
   * NOTE: This method is to support the use case of the user "deleting"
   * customer through the Synergy UI.  The customer is not hard deleted,
   * rather it is "soft deleted" by setting the CustomerStatus to DELETED.
   * 
   * After 30 days of being in the DELETED state, an archival job will 
   * then hard delete the customer from the repository.
   * 
   * @param customer The customer to soft delete
   */
  void softDeleteCustomer(AbstractCustomerEntity customer);
  
  /**
   * NOTE: This method should only be invoked by the DistributorStateEvaluator, which
   * is the domain layer entry point for the system scheduled portfolio maintenance 
   * (a.k.a. payment processing job), as hard deletions occur after a customer has 
   * been in the soft deleted state for 30 days (see above)
   * 
   * @param customer The customer to hard delete
   */
  void hardDeleteCustomer(AbstractCustomerEntity customer);
  
  /**
   * 
   * @param customerId The customer id
   * @param rawPointIds The raw point ids
   * 
   * @return The raw points that were loaded
   */
  List<RawPointEntity> loadRawPoints(
      Integer customerId,
      List<Integer> rawPointIds);
  
  /**
   * 
   * @param customerId The owning customer
   * @param rawPointIds The raw points to ignore
   * 
   * @throws EntityDoesNotExistException If the customer does not exist
   */
  void ignoreRawPoints(
      Integer customerId, 
      List<Integer> rawPointIds)
  throws
      EntityDoesNotExistException;
  
  /**
   * 
   * @param customerId The owning customer
   * @param rawPointIds The raw points to unignore
   * 
   * @throws EntityDoesNotExistException If the customer does not exist
   */
  void unignoreRawPoints(
      Integer customerId, 
      List<Integer> rawPointIds)
  throws
      EntityDoesNotExistException;
  
  /**
   * 
   * @return true if the underlying repository is JDBC 
   * (needed as some operations aren't needed)
   */
  boolean hasDatabaseRepository();
}
//@formatter:on