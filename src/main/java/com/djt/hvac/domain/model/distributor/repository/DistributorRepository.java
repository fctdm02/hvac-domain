//@formatter:off
package com.djt.hvac.domain.model.distributor.repository;

import java.util.Collection;
import java.util.Map;

import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.distributor.AbstractDistributorEntity;
import com.djt.hvac.domain.model.distributor.OnlineDistributorEntity;
import com.djt.hvac.domain.model.distributor.enums.DistributorType;
import com.djt.hvac.domain.model.distributor.paymentmethod.AbstractPaymentMethodEntity;
import com.djt.hvac.domain.model.distributor.service.model.CreatePaymentMethodRequest;
import com.djt.hvac.domain.model.user.DistributorUserEntity;
import com.djt.hvac.domain.model.user.enums.UserRoleType;

/**
 * 
 * @author tommyers
 * 
 */
public interface DistributorRepository {
  
  Integer ROOT_DISTRIBUTOR_ID = Integer.valueOf(1);

  /**
   * 
   * Loads all distributors.
   */
  Map<Integer, AbstractDistributorEntity> loadAllDistributors();
  
  /**
   * 
   * @return The root distributor (i.e. Resolute)
   * 
   * @throws EntityDoesNotExistException If the specified distributor does not exist
   */
  AbstractDistributorEntity getRootDistributor()
  throws 
      EntityDoesNotExistException;
  
  /**
   * 
   * Loads all distributors.
   * 
   * @param loadDistributorPaymentMethods Whether or not to load distributor payment methods
   * @param loadDistributorUsers Whether or not to load distributor users
   * 
   */
  Map<Integer, AbstractDistributorEntity> loadAllDistributors(
      boolean loadDistributorPaymentMethods,
      boolean loadDistributorUsers);
  
  /**
   * 
   * @param loadDistributorPaymentMethods Whether or not to load distributor payment methods
   * @param loadDistributorUsers Whether or not to load distributor users
   * 
   * @return The root distributor (i.e. Resolute)
   * 
   * @throws EntityDoesNotExistException If the specified distributor does not exist
   */
  AbstractDistributorEntity getRootDistributor(
      boolean loadDistributorPaymentMethods,
      boolean loadDistributorUsers)
  throws 
      EntityDoesNotExistException;

  /**
   * 
   * @param customerId The customer id to filter for.  That is, if specified, then only
   *                   the distributor hierarchy for the given customer is loaded.
   * @param loadDistributorPaymentMethods Whether or not to load distributor payment methods
   * @param loadDistributorUsers Whether or not to load distributor users
   * 
   * @return The root distributor (i.e. Resolute)
   * 
   * @throws EntityDoesNotExistException If the specified distributor does not exist
   */
  AbstractDistributorEntity getRootDistributor(
      int customerId,
      boolean loadDistributorPaymentMethods,
      boolean loadDistributorUsers)
  throws 
      EntityDoesNotExistException;

  /**
   * Loads a distributor from the repository, including its child distributors
   * and all child customers
   * 
   * @param distributorId The distributor to load
   * 
   * @return The distributor identified by <code>distributorId</code>
   * 
   * @throws EntityDoesNotExistException If the specified distributor does not exist
   */
  AbstractDistributorEntity loadDistributor(
      int distributorId)
  throws 
      EntityDoesNotExistException;
  
  /**
   * Loads a distributor from the repository, including its child distributors
   * and all child customers
   * 
   * @param distributorId The distributor to load
   * @param loadDistributorPaymentMethods Whether or not to load distributor payment methods
   * @param loadDistributorUsers Whether or not to load distributor users
   * 
   * @return The distributor identified by <code>distributorId</code>
   * 
   * @throws EntityDoesNotExistException If the specified distributor does not exist
   */
  AbstractDistributorEntity loadDistributor(
      int distributorId,
      boolean loadDistributorPaymentMethods,
      boolean loadDistributorUsers)
  throws 
      EntityDoesNotExistException;
  
  /**
   * 
   * @param distributor the distributor to store
   */
  void storeDistributor(AbstractDistributorEntity distributor);

  /**
   * NOTE: The "root" Resolute distributor cannot be soft deleted.
   * 
   * @param distributor The distributor to soft delete (i.e. mark status as 'DELETED')
   */
  void softDeleteDistributor(AbstractDistributorEntity distributor);
  
  /**
   * NOTE: The "root" Resolute distributor cannot be hard deleted.
   * 
   * @param distributor The distributor to hard delete
   */
  void hardDeleteDistributor(AbstractDistributorEntity distributor);
  
  /**
   * 
   * @param distributors The distributors to store
   */
  void storeDistributors(Collection<AbstractDistributorEntity> distributors);
  
  /**
   * 
   * NOTE: Unless the parent distributor is the "root" Resolute distributor,
   * then the distributor type must match that of the parent distributor.
   * 
   * @param parentDistributorId The parent distributor
   * @param distributorType Must be either: 'ONLINE' or 'OUT_OF_BAND'
   * @param name Must be a unique name among the parent distributor's children
   * @param unitSystemName The default unit system (IP or SI)
   * @param allowOutOfBandBuildings Allow out of band buildings to be created 
   *        Only has meaning for ONLINE distributors. 
   * 
   * @return The newly created distributor
   * 
   * @throws EntityAlreadyExistsException If the distributor already exists
   * @throws EntityDoesNotExistException If the parent distributor does not exist
   */
  AbstractDistributorEntity createDistributor(
      int parentDistributorId,
      DistributorType distributorType,
      String name,
      String unitSystemName,
      boolean allowOutOfBandBuildings) 
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException;

  /**
   * For now, this is only used by the tests, but can be refactored to support production use.
   * 
   * @param parentDistributor The parent distributor
   * @param userRole Must be either Distributor Admin or Distributor User
   * @param email The user email - must be unique among all users for any distributor (global)
   * @param firstName The first name
   * @param lastName The last name
   * @param isAccountManager Whether or not the user is an account manager or not 
   * (role must be 'ADMIN'
   * 
   * @return Thew newly created distributor user
   * 
   * @throws EntityAlreadyExistsException If the distributor user already exists
   * @throws EntityDoesNotExistException If the parent distributor does not exist
   */
  DistributorUserEntity createDistributorUser(
      AbstractDistributorEntity parentDistributor,
      UserRoleType userRole,
      String email,
      String firstName,
      String lastName,
      Boolean isAccountManager)
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException;
  
  /**
   * 
   * @param parentDistributor The parent distributor
   * @param request All the info needed to create the Resolute payment method
   * 
   * @return The newly created Resolute payment method
   * 
   * @throws EntityAlreadyExistsException If the payment method already exists
   * (i.e. name for a given distributor conveys uniqueness
   * @throws EntityDoesNotExistException If the parent distributor does not exist
   */
  AbstractPaymentMethodEntity createPaymentMethod(
      OnlineDistributorEntity parentDistributor,
      CreatePaymentMethodRequest request) 
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException;
  
  /**
   * 
   * @param parentDistributor The parent distributor
   * @param paymentMethodId The payment method to delete
   * 
   * @throws EntityDoesNotExistException If the payment method does not exist
   * 
   * NOTE: Payment methods cannot be deleted when they have a non-zero ref count.
   */
  void deletePaymentMethod(
      OnlineDistributorEntity parentDistributor,
      int paymentMethodId) 
  throws 
      EntityDoesNotExistException;
  
  /**
   * 
   * @param distributorId The distributor
   * 
   * @return A map, whose keys are the payment method ids, and whose values are
   * the "ref count" for those payment methods.  That is, if a payment method is
   * associated with 3 building subscriptions, then its ref count would be 3.
   * 
   * NOTE: Payment methods cannot be deleted when they have a non-zero ref count.
   * 
   * @throws EntityDoesNotExistException If the distributor does not exist
   */
  Map<Integer, Integer> getPaymentMethodRefCounts(
      int distributorId)
  throws 
      EntityDoesNotExistException;
  
  //@formatter:on  
}