//@formatter:off
package com.djt.hvac.domain.model.distributor.service;

import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.distributor.AbstractDistributorEntity;
import com.djt.hvac.domain.model.distributor.OnlineDistributorEntity;
import com.djt.hvac.domain.model.distributor.OutOfBandDistributorEntity;
import com.djt.hvac.domain.model.distributor.enums.DistributorType;
import com.djt.hvac.domain.model.distributor.paymentmethod.AbstractPaymentMethodEntity;
import com.djt.hvac.domain.model.distributor.service.model.CreatePaymentMethodRequest;
import com.djt.hvac.domain.model.stripe.exception.StripeClientException;
import com.djt.hvac.domain.model.user.DistributorUserEntity;
import com.djt.hvac.domain.model.user.enums.UserRoleType;

/**
 * 
 * @author tommyers
 * 
 */
public interface DistributorService {
  
  /**
   * The Resolute distributor is a special out-of-band distributor that is 
   * the parent/ancestor distributor of all other distributors.  It is the
   * only distributor that does not have a parent distributor. 
   */
  Integer RESOLUTE_ROOT_DISTRIBUTOR_ID = Integer.valueOf(1);

  /**
   * NOTE: Payment methods do not apply for the root distributor
   *       Distributor users are not loaded.
   * 
   * @return The Resolute/root distributor
   */
  OutOfBandDistributorEntity getResoluteRootDistributor();
  
  /**
   * NOTE: Payment methods do not apply for the root distributor
   * 
   * @param loadDistributorUsers Whether or not to load distributor users
   * 
   * @return The Resolute/root distributor
   */
  OutOfBandDistributorEntity getResoluteRootDistributor(boolean loadDistributorUsers);

  /**
   * 
   * NOTE: Distributor payment methods and users are not loaded.
   * 
   * @param distributorId The distributor to load
   * 
   * @return The given distributor
   * 
   * @throws EntityDoesNotExistException If the distributor does not exist
   */
  AbstractDistributorEntity loadDistributor(
      int distributorId)
  throws 
      EntityDoesNotExistException;
  
  /**
   * 
   * @param distributorId The distributor to load
   * @param loadDistributorPaymentMethods Whether or not to load distributor payment methods
   * @param loadDistributorUsers Whether or not to load distributor users
   * 
   * @return The given distributor
   * 
   * @throws EntityDoesNotExistException If the distributor does not exist
   */
  AbstractDistributorEntity loadDistributor(
      int distributorId,
      boolean loadDistributorPaymentMethods,
      boolean loadDistributorUsers)
  throws 
      EntityDoesNotExistException;

  /**
   * 
   * @param distributorId The distributor to load
   * @param loadDistributorPaymentMethods Whether or not to load distributor payment methods
   * @param loadDistributorUsers Whether or not to load distributor users
   * @param loadChildCustomers If true, loads child customers
   * 
   * @return The given distributor
   * 
   * @throws EntityDoesNotExistException If the distributor does not exist
   */
  AbstractDistributorEntity loadDistributor(
      int distributorId,
      boolean loadDistributorPaymentMethods,
      boolean loadDistributorUsers,
      boolean loadChildCustomers) 
  throws 
      EntityDoesNotExistException;
  
  /**
   * 
   * NOTE: Unless the parent distributor is the "root" Resolute distributor,
   * then the distributor type must match that of the parent distributor.
   * 
   * @param parentDistributorId The parent distributor
   * @param distributorType Must be either: 'ONLINE' or 'OUT_OF_BAND'
   * @param name Must be a unique name among the parent distributor's children 
   * 
   * @return The newly created distributor
   * 
   * @throws EntityAlreadyExistsException If the distributor already exists
   * @throws EntityDoesNotExistException If the parent distributor does not exist
   */
  AbstractDistributorEntity createDistributor(
      int parentDistributorId,
      DistributorType distributorType,
      String name) 
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException;
  
  /**
   * 
   * NOTE: Unless the parent distributor is the "root" Resolute distributor,
   * then the distributor type must match that of the parent distributor.
   * 
   * @param parentDistributorId The parent distributor
   * @param distributorType Must be either: 'ONLINE' or 'OUT_OF_BAND'
   * @param name Must be a unique name among the parent distributor's children
   * @param unitSystemName The default unit system (IP or SI)
   * @param allowOutOfBandBuildings Allow out of band buildings to be created.
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
   * 
   * @param distributor The distributor to update
   */
  void updateDistributor(AbstractDistributorEntity distributor);

  /**
   * 
   * NOTES: 
   * 1: The "root" Resolute distributor cannot be soft deleted.
   * 2: This method is to support the use case of the scheduled
   * evaluation job soft deleting the distributor after having been in
   * the CREATED state for 90 days. When this happens the distributor state
   * is changed to DELETED. (i.e. soft delete)
   * 
   * After 30 days of being in the DELETED state, an archival job will 
   * then hard delete the distributor from the repository.
   * 
   * @param distributor The distributor to soft delete
   */
  void softDeleteDistributor(AbstractDistributorEntity distributor);
  
  /**
   * NOTES: 
   * 1: The "root" Resolute distributor cannot be hard deleted.
   * 2: This method should only be invoked by the DistributorStateEvaluator, which
   * is the domain layer entry point for the system scheduled portfolio maintenance 
   * (a.k.a. payment processing job), as hard deletions occur after a distributor has 
   * been in the soft deleted state for 90 days (see above)
   * 
   * @param distributor The distributor to hard delete
   */
  void hardDeleteDistributor(AbstractDistributorEntity distributor);
  
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
   * @return The newly created distributor user
   * 
   * @throws EntityAlreadyExistsException If the distributor user already exists
   * @throws EntityDoesNotExistException If the parent distributor does not exist
   * @throws StripeClientException If there was a problem updating the stripe customer account
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
      EntityDoesNotExistException,
      StripeClientException;
  
  /**
   * The assumption currently is that there is only one account manager per distributor.
   * Therefore, the UI should support this activity, so the application layer will need
   * to invoke this service method in order to update the Stripe customer email that
   * corresponds to the given distributor.
   * 
   * If there already exists a distributor account manager and stripe customer id, then
   * the current distributor account manager is set to be a regular admin and the stripe
   * customer account email is changed to be that of the new resolute account manager.
   * 
   * @param parentDistributor The parent distributor
   * @param accountManagerId The id of the distributor user that is to be the new
   * account manager.  NOTE: The user must be of type 'DISTRIBUTOR_ADMIN' in order to be
   * eligible to be the new account manager.  The old account manager for the distributor
   * is changed back to be a regular admin user.
   * 
   * @return The new distributor user that has the account_manager flag set to true
   * 
   * @throws EntityDoesNotExistException If the distributor user does not exist
   * @throws StripeClientException If the distributor is online and the stripe customer
   * for it has been created and there was a failure updating the Stripe customer email
   */
  DistributorUserEntity setDistributorAccountManager(
      AbstractDistributorEntity parentDistributor,
      Integer accountManagerId)
  throws 
      EntityDoesNotExistException,
      StripeClientException;  
  
  /**
   * 
   * @param parentDistributor The parent distributor
   * @param request All the info needed to create the Resolute payment method
   * 
   * @return The newly created Resolute payment method
   * 
   * @throws EntityAlreadyExistsException If the payment method already exists
   * (i.e. name for a given distributor conveys uniqueness
   * @throws EntityDoesNotExistException If an entity does not exist
   * @throws StripeClientException If there was a failure creating the stripe customer
   */
  AbstractPaymentMethodEntity createPaymentMethod(
      OnlineDistributorEntity parentDistributor,
      CreatePaymentMethodRequest request) 
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException,
      StripeClientException;
  
  /**
   * 
   * @param parentDistributor The parent distributor
   * @param paymentMethodId The payment method to delete
   * 
   * NOTE: A payment method cannot be deleted if it is is associated with 
   * an active building subscription.
   * 
   * @throws EntityDoesNotExistException If an entity does not exist
   * @throws StripeClientException If there was a failure deleting the Stripe payment method
   */
  void deletePaymentMethod(
      OnlineDistributorEntity parentDistributor,
      int paymentMethodId) 
  throws 
      EntityDoesNotExistException,
      StripeClientException; 
  
  /**
   * 
   * @return true if the underlying repository is JDBC 
   * (needed as some operations aren't needed)
   */
  boolean hasDatabaseRepository();
}
//@formatter:on