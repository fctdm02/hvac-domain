//@formatter:off
package com.djt.hvac.domain.model.customer.repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.customer.AbstractCustomerEntity;
import com.djt.hvac.domain.model.customer.enums.CustomerType;
import com.djt.hvac.domain.model.distributor.AbstractDistributorEntity;

/**
 * 
 * @author tommyers
 * 
 */
public interface CustomerRepository {

  /**
   * 
   * @param parentDistributor The parent distributor
   * @param customerType Must be one of: : 'ONLINE', 'OUT_OF_BAND' or DEMO
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
   * @param customerType Must be one of: : 'ONLINE', 'OUT_OF_BAND' or DEMO
   * @param name The customer name (must be unique among the parent distributor's child customers)
   * @param unitSystemName The default unit system (IP or SI)
   * @param isExpires If customerType is DEMO, whether or not the demo customer expires after being in CREATED state for 90 days
   * @param isInternal If customerType is DEMO, whether or not the demo customer is considered to be for "internal use"
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
      String unitSystemName,
      Boolean isExpires,
      Boolean isInternal) 
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException;
  
  /**
   * 
   * @param loadDistributorPaymentMethods Whether or not to load distributor payment methods
   * @param loadDistributorUsers Whether or not to load distributor users
   * 
   * @return all customers.
   * 
   * @throws EntityDoesNotExistException If the root distributor cannot be found
   */
  Map<Integer, AbstractCustomerEntity> loadAllCustomers(
      boolean loadDistributorPaymentMethods,
      boolean loadDistributorUsers)
  throws 
      EntityDoesNotExistException;

  /**
   * 
   * @param parentDistributor The parent distributor
   * 
   * @return all customers for the given parent distributor.
   */
  List<AbstractCustomerEntity> loadChildCustomers(AbstractDistributorEntity parentDistributor);
  
  /**
   * Loads a customer from the repository
   * 
   * @param customerId The customer to load
   * @param loadDistributorPaymentMethods Whether or not to load distributor payment methods
   * @param loadDistributorUsers Whether or not to load distributor users
   * 
   * @return The customer identified by <code>customerId</code>
   * 
   * @throws EntityDoesNotExistException If the specified customer does not exist
   */
  AbstractCustomerEntity loadCustomer(
      int customerId,
      boolean loadDistributorPaymentMethods,
      boolean loadDistributorUsers) 
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
   * @param customer The customer to store
   */
  void storeCustomer(AbstractCustomerEntity customer);

  /**
   * @param customer The customer to soft delete (i.e. mark status as 'DELETED')
   */
  void softDeleteCustomer(AbstractCustomerEntity customer);

  /**
   * @param customer The customer to hard delete
   */
  void hardDeleteCustomer(AbstractCustomerEntity customer);
  
  /**
   * 
   * @param customers The customers to store
   */
  void storeCustomers(Collection<AbstractCustomerEntity> customers);
}
//@formatter:on