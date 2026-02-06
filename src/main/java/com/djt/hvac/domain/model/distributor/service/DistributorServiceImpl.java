//@formatter:off
package com.djt.hvac.domain.model.distributor.service;

import static java.util.Objects.requireNonNull;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.customer.AbstractCustomerEntity;
import com.djt.hvac.domain.model.customer.enums.CustomerStatus;
import com.djt.hvac.domain.model.customer.repository.CustomerRepository;
import com.djt.hvac.domain.model.dictionary.enums.UnitSystem;
import com.djt.hvac.domain.model.distributor.AbstractDistributorEntity;
import com.djt.hvac.domain.model.distributor.OnlineDistributorEntity;
import com.djt.hvac.domain.model.distributor.OutOfBandDistributorEntity;
import com.djt.hvac.domain.model.distributor.enums.DistributorType;
import com.djt.hvac.domain.model.distributor.paymentmethod.AbstractPaymentMethodEntity;
import com.djt.hvac.domain.model.distributor.repository.DistributorRepository;
import com.djt.hvac.domain.model.distributor.repository.DistributorRepositoryFileSystemImpl;
import com.djt.hvac.domain.model.distributor.service.model.CreatePaymentMethodRequest;
import com.djt.hvac.domain.model.stripe.client.StripeClient;
import com.djt.hvac.domain.model.stripe.dto.StripeClientResponse;
import com.djt.hvac.domain.model.stripe.exception.StripeClientException;
import com.djt.hvac.domain.model.user.DistributorUserEntity;
import com.djt.hvac.domain.model.user.enums.UserRoleType;
import com.djt.hvac.domain.model.user.repository.UserRepository;

public class DistributorServiceImpl implements DistributorService {

  private static final Logger LOGGER = LoggerFactory.getLogger(DistributorServiceImpl.class);
  
  private final UserRepository userRepository;
  private final CustomerRepository customerRepository;
  private final StripeClient stripeClient;
  private final DistributorRepository distributorRepository;

  public DistributorServiceImpl(
      UserRepository userRepository,
      CustomerRepository customerRepository,
      DistributorRepository distributorRepository,
      StripeClient stripeClient) {
    
    requireNonNull(userRepository, "userRepository cannot be null");
    requireNonNull(customerRepository, "customerRepository cannot be null");
    requireNonNull(distributorRepository, "distributorRepository cannot be null");
    requireNonNull(stripeClient, "stripeClient cannot be null");
    this.userRepository = userRepository;
    this.customerRepository = customerRepository;
    this.distributorRepository = distributorRepository;
    this.stripeClient = stripeClient;
  }
  
  @Override
  public OutOfBandDistributorEntity getResoluteRootDistributor() {
    
    return getResoluteRootDistributor(
        false); //loadDistributorUsers
  }
  
  @Override
  public OutOfBandDistributorEntity getResoluteRootDistributor(boolean loadDistributorUsers) {
    
    try {
      return (OutOfBandDistributorEntity)distributorRepository.getRootDistributor(
          false,  // loadDistributorPaymentMethods
          loadDistributorUsers);
    } catch (EntityDoesNotExistException e) {
      throw new IllegalStateException("Unable to load Resolute/Root distributor", e);
    }
  }
  
  @Override
  public AbstractDistributorEntity loadDistributor(
      int distributorId)
  throws 
      EntityDoesNotExistException {
    
    return loadDistributor(
        distributorId,
        false,  // loadDistributorPaymentMethods
        false); // loadDistributorUsers
  }
  
  @Override
  public AbstractDistributorEntity loadDistributor(
      int distributorId,
      boolean loadDistributorPaymentMethods,
      boolean loadDistributorUsers)
  throws 
      EntityDoesNotExistException {
    
    AbstractDistributorEntity distributor = loadDistributor(
        distributorId, 
        loadDistributorPaymentMethods, 
        loadDistributorUsers,
        false);
    
    distributor.resetAllIsModified();
    
    return distributor;
  }

  @Override
  public AbstractDistributorEntity loadDistributor(
      int distributorId,
      boolean loadDistributorPaymentMethods,
      boolean loadDistributorUsers,
      boolean loadChildCustomers) 
  throws 
      EntityDoesNotExistException {

    long start = System.currentTimeMillis();
    AbstractDistributorEntity distributor = distributorRepository.loadDistributor(
        distributorId,
        loadDistributorPaymentMethods,
        loadDistributorUsers);
    
    if (loadChildCustomers) {
      customerRepository.loadChildCustomers(distributor);
    }
    
    distributor.resetAllIsModified();
    
    LOGGER.debug("loadDistributor(): id: {} loadChildCustomers: {} elapsed(ms): {}",
        distributorId,
        loadChildCustomers,
        (System.currentTimeMillis()-start));
    return distributor;
  }
  
  @Override
  public AbstractDistributorEntity createDistributor(
      int parentDistributorId,
      DistributorType distributorType,
      String name) 
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException {
    
    return createDistributor(
        parentDistributorId, 
        distributorType, 
        name, 
        UnitSystem.IP.toString(), 
        false);
  }
  
  @Override
  public AbstractDistributorEntity createDistributor(
      int parentDistributorId,
      DistributorType distributorType,
      String name,
      String unitSystemName,
      boolean allowOutOfBandBuildings) 
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException {
      
    long start = System.currentTimeMillis();
    AbstractDistributorEntity distributor = distributorRepository.createDistributor(
        parentDistributorId, 
        distributorType, 
        name,
        unitSystemName,
        allowOutOfBandBuildings);
    
    distributor.loadDistributorPaymentMethods = true;
    distributor.loadDistributorUsers = true;
    
    LOGGER.info("createDistributor(): name: {}, unitSystemName: {}, allowOutOfBandBuildings: {} elapsed(ms): {}",
        name,
        unitSystemName,
        allowOutOfBandBuildings,
        (System.currentTimeMillis()-start));
    return distributor;
  }
  
  @Override
  public void updateDistributor(AbstractDistributorEntity distributor) {
    
    long start = System.currentTimeMillis();
    Set<String> modifiedAttributeNames = distributor.getModifiedAttributes();
    if (distributor.getIsModified()) {
      
      distributorRepository.storeDistributor(distributor);
      
      distributor.setNotModified();
    }
    LOGGER.info("updateDistributor(): distributor: {} modified: {} elapsed(ms): {}",
        distributor,
        modifiedAttributeNames,
        (System.currentTimeMillis()-start));
  }

  @Override
  public void softDeleteDistributor(AbstractDistributorEntity parentDistributor) {
    
    if (!parentDistributor.shouldBeSoftDeleted()) {
      throw new IllegalStateException("Distributor: ["
          + parentDistributor.getName()
          + "] is not eligible for soft deletion.");
      
    }
    
    try {
      
      for (AbstractDistributorEntity childDistributor: parentDistributor.getChildDistributors()) {
        
        for (AbstractCustomerEntity childCustomer: childDistributor.getChildCustomers()) {
          customerRepository.softDeleteCustomer(childCustomer);
        }        
        
        distributorRepository.softDeleteDistributor(childDistributor);
      }

      for (AbstractCustomerEntity childCustomer: parentDistributor.getChildCustomers()) {
        customerRepository.softDeleteCustomer(childCustomer);
      }    
      
      distributorRepository.softDeleteDistributor(parentDistributor);
      
    } catch (Exception t) {
      LOGGER.error("Unable to soft delete distributor: {}", parentDistributor, t);
    }
  }
  
  @Override
  public void hardDeleteDistributor(AbstractDistributorEntity parentDistributor ) {
    
    if (!parentDistributor.shouldBeHardDeleted()) {
      throw new IllegalStateException("Distributor: ["
          + parentDistributor.getName()
          + "] is not eligible for hard deletion.");
      
    }
    
    try {
      
      for (AbstractDistributorEntity childDistributor: parentDistributor.getChildDistributors()) {
        
        for (AbstractCustomerEntity childCustomer: childDistributor.getChildCustomers()) {
          
          if (!childCustomer.getCustomerStatus().equals(CustomerStatus.DELETED)) {
            customerRepository.softDeleteCustomer(childCustomer);
          }
          customerRepository.hardDeleteCustomer(childCustomer);
        }        
        
        distributorRepository.hardDeleteDistributor(childDistributor);  
      }
      
      for (AbstractCustomerEntity childCustomer: parentDistributor.getChildCustomers()) {
        
        if (!childCustomer.getCustomerStatus().equals(CustomerStatus.DELETED)) {
          customerRepository.softDeleteCustomer(childCustomer);
        }
        customerRepository.hardDeleteCustomer(childCustomer);
      }
      
      distributorRepository.hardDeleteDistributor(parentDistributor);
      
    } catch (Exception t) {
      LOGGER.error("Unable to hard delete distributor: {}", parentDistributor, t);
    }
  }
  
  @Override
  public DistributorUserEntity createDistributorUser(
      AbstractDistributorEntity parentDistributor,
      UserRoleType userRole,
      String email,
      String firstName,
      String lastName,
      Boolean isAccountManager)
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException, 
      StripeClientException {

    if (!parentDistributor.loadDistributorUsers) {
      
      throw new IllegalStateException("Cannot create a distributor user for distributor: ["
          + parentDistributor
          + "] whose distributor users were not loaded.");
    }
    
    long start = System.currentTimeMillis();
    DistributorUserEntity distributorUser = distributorRepository.createDistributorUser(
        parentDistributor, 
        userRole, 
        email, 
        firstName, 
        lastName, 
        isAccountManager);
    
    if (isAccountManager.booleanValue()) {
      setDistributorAccountManager(
          parentDistributor, 
          distributorUser.getPersistentIdentity());
    }
    
    LOGGER.info("createDistributorUser(): email: {} elapsed(ms): {}",
        email,
        (System.currentTimeMillis()-start));
    
    return distributorUser;
  }  
  
  @Override
  public DistributorUserEntity setDistributorAccountManager(
      AbstractDistributorEntity parentDistributor,
      Integer accountManagerId)
  throws 
      EntityDoesNotExistException,
      StripeClientException {
    
    if (!parentDistributor.loadDistributorUsers) {
      
      throw new IllegalStateException("Cannot create a distributor user for distributor: ["
          + parentDistributor
          + "] whose distributor users were not loaded.");
    }    
    
    long start = System.currentTimeMillis();
    DistributorUserEntity newAccountManager = parentDistributor.changeDistributorAccountManager(
        accountManagerId);
    
    if (parentDistributor instanceof OnlineDistributorEntity) {
      
      OnlineDistributorEntity onlineDistributor = (OnlineDistributorEntity)parentDistributor;
      String stripeCustomerId = onlineDistributor.getStripeCustomerId();
      
      if (stripeCustomerId == null || stripeCustomerId.trim().isEmpty()) {

        DistributorUserEntity accountManager = onlineDistributor.getAccountManagerDistributorUser();
        
        String name = onlineDistributor.getName();
        String email = accountManager.getEmail();
        
        StripeClientResponse customerResponse = stripeClient.createStripeCustomer(
            name, 
            email);
        
        if (customerResponse.getResult().equals(StripeClientResponse.RESULT_FAILURE)) {
          
          throw new StripeClientException("Unable to create Stripe customer for distributor: ["
              + onlineDistributor
              + "] and account manager: ["
              + accountManager
              + "], error: "
              + customerResponse.getReason());
        }

        stripeCustomerId = customerResponse
            .getResponseObjects()
            .get(StripeClient.STRIPE_CUSTOMER_ID)
            .toString();
        
        onlineDistributor.setStripeCustomerId(stripeCustomerId);
        distributorRepository.storeDistributor(onlineDistributor);
        
      } else {

        stripeClient.updateStripeCustomerForNewAccountManager(
            stripeCustomerId, 
            newAccountManager.getEmail());
        
      }
    }
    userRepository.storeDistributorUsers(parentDistributor);
    LOGGER.info("setDistributorAccountManager(): newAccountManager: {} elapsed(ms): {}",
        newAccountManager,
        (System.currentTimeMillis()-start));
    return newAccountManager;
  }
  
  @Override
  public AbstractPaymentMethodEntity createPaymentMethod(
      OnlineDistributorEntity onlineDistributor,
      CreatePaymentMethodRequest request) 
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException,
      StripeClientException {
    
    if (!onlineDistributor.loadDistributorPaymentMethods) {
      
      throw new IllegalStateException("Cannot add a payment method to a distributor: ["
          + onlineDistributor
          + "] whose payment methods were not loaded.");
    }
    
    long start = System.currentTimeMillis();

    // An account manager is required for everything.
    DistributorUserEntity accountManager = onlineDistributor.getAccountManagerDistributorUserNullIfNotExists();
    if (accountManager == null) {
      
      throw new EntityDoesNotExistException("Cannot add a payment method to a distributor: ["
          + onlineDistributor
          + "] that does not have an account manager assigned yet.");
    }
    
    // See if we need to create a stripe customer account first.
    String stripeCustomerId = onlineDistributor.getStripeCustomerId();
    if (stripeCustomerId == null || stripeCustomerId.trim().isEmpty()) {

      String name = onlineDistributor.getName();
      String email = accountManager.getEmail();
      
      StripeClientResponse customerResponse = stripeClient.createStripeCustomer(
          name, 
          email);
      
      if (customerResponse.getResult().equals(StripeClientResponse.RESULT_FAILURE)) {
        
        throw new StripeClientException("Unable to create Stripe customer for distributor: ["
            + onlineDistributor
            + "] and account manager: ["
            + accountManager
            + "], error: "
            + customerResponse.getReason());
      }

      stripeCustomerId = customerResponse
          .getResponseObjects()
          .get(StripeClient.STRIPE_CUSTOMER_ID)
          .toString();
    }
    
    // Attach the stripe payment method to the stripe customer.
    String stripeSourceId = request.getStripeSourceId();
    StripeClientResponse attachResponse = stripeClient.attachStripePaymentMethod(stripeCustomerId, stripeSourceId);
    if (attachResponse.getResult().equals(StripeClientResponse.RESULT_FAILURE)) {
      
      throw new StripeClientException("Unable to attach payment method to: ["
          + onlineDistributor
          + "], account manager: ["
          + accountManager
          + "],  stripeCustomerId: ["
          + stripeCustomerId
          + "],  request: ["
          + request
          + "], error: "
          + attachResponse.getReason());
    }
    
    // This is to support the tests where the test card name is actually supplied in the request.
    String id = attachResponse.getResponseObjects().get(StripeClient.STRIPE_SOURCE_ID).toString();
    if (!id.equals(stripeSourceId)) {
      
      request = CreatePaymentMethodRequest
          .builder(request)
          .withStripeSourceId(id)
          .build();
    }

    // Finally, create the resolute payment method by storing the distributor.
    onlineDistributor.setStripeCustomerId(stripeCustomerId);
    distributorRepository.storeDistributor(onlineDistributor);
    
    AbstractPaymentMethodEntity paymentMethod = distributorRepository.createPaymentMethod(
        onlineDistributor,
        request);
    
    LOGGER.info("createPaymentMethod(): request: {} elapsed(ms): {}",
        request,
        (System.currentTimeMillis()-start));
    
    return paymentMethod;
  }
 
  @Override
  public void deletePaymentMethod(
      OnlineDistributorEntity onlineDistributor,
      int paymentMethodId) 
  throws 
      EntityDoesNotExistException, 
      StripeClientException {
    
    if (!onlineDistributor.loadDistributorPaymentMethods) {
      
      throw new IllegalStateException("Cannot remove a payment method from a distributor: ["
          + onlineDistributor
          + "] whose payment methods were not loaded.");
    }
    
    long start = System.currentTimeMillis();
    String stripeCustomerId = onlineDistributor.getStripeCustomerId();

    AbstractPaymentMethodEntity paymentMethod = onlineDistributor.getChildPaymentMethod(Integer.valueOf(paymentMethodId));
    
    String stripeSourceId = paymentMethod.getStripeSourceId();
    
    stripeClient.deleteStripePaymentMethod(stripeCustomerId, stripeSourceId);
    
    distributorRepository.deletePaymentMethod(
        onlineDistributor, 
        paymentMethodId);
    
    LOGGER.info("deletePaymentMethod(): paymentMethod: {} elapsed(ms): {}",
        paymentMethod,
        (System.currentTimeMillis()-start));
  }    
  
  @Override 
  public boolean hasDatabaseRepository() {

    boolean hasDatabaseRepository = true;
    if (distributorRepository instanceof DistributorRepositoryFileSystemImpl) {
      hasDatabaseRepository = false;  
    }
    return hasDatabaseRepository;
  }
}
//@formatter:on