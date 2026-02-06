//@formatter:off
package com.djt.hvac.domain.model.customer.service;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.customer.AbstractCustomerEntity;
import com.djt.hvac.domain.model.customer.enums.CustomerType;
import com.djt.hvac.domain.model.customer.repository.CustomerRepository;
import com.djt.hvac.domain.model.customer.repository.CustomerRepositoryFileSystemImpl;
import com.djt.hvac.domain.model.dictionary.enums.UnitSystem;
import com.djt.hvac.domain.model.distributor.AbstractDistributorEntity;
import com.djt.hvac.domain.model.rawpoint.RawPointEntity;
import com.djt.hvac.domain.model.rawpoint.repository.RawPointRepository;

public class CustomerServiceImpl implements CustomerService {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(CustomerServiceImpl.class);

  private final CustomerRepository customerRepository;
  private final RawPointRepository rawPointRepository;

  public CustomerServiceImpl(
      CustomerRepository customerRepository,
      RawPointRepository rawPointRepository) {
    
    requireNonNull(customerRepository, "customerRepository cannot be null");
    requireNonNull(rawPointRepository, "rawPointRepository cannot be null");
    this.customerRepository = customerRepository;
    this.rawPointRepository = rawPointRepository;
  }
  
  @Override
  public Collection<AbstractCustomerEntity> loadAllCustomers(
      boolean loadDistributorPaymentMethods,
      boolean loadDistributorUsers) {
    
    try {
      return this.customerRepository.loadAllCustomers(
          loadDistributorPaymentMethods,
          loadDistributorUsers)
          .values();
    } catch (EntityDoesNotExistException e) {
      throw new RuntimeException("Unable to load all customers, error: "
          + e.getMessage(), e);
    }
  }
  
  @Override
  public AbstractCustomerEntity loadCustomer(
      int customerId,
      boolean loadDistributorPaymentMethods,
      boolean loadDistributorUsers) 
  throws 
      EntityDoesNotExistException {
    
    AbstractCustomerEntity customer = loadCustomer(
        customerId, 
        loadDistributorPaymentMethods,
        loadDistributorUsers,
        false);
    
    customer.setNotModified();
    customer.getParentDistributor().setNotModified();
    
    return customer;
  }
  
  @Override
  public AbstractCustomerEntity loadCustomer(
      int customerId,
      boolean loadDistributorPaymentMethods,
      boolean loadDistributorUsers, 
      boolean loadRawPoints) 
  throws 
      EntityDoesNotExistException {

    AbstractCustomerEntity customer = customerRepository.loadCustomer(
        customerId,
        loadDistributorPaymentMethods,
        loadDistributorUsers);
    
    if (loadRawPoints) {
      Collection<RawPointEntity> rawPoints = rawPointRepository.loadRawPoints(customer);
      customer.addRawPoints(rawPoints);
    }
    
    customer.setNotModified();
    customer.getParentDistributor().setNotModified();
    
    return customer;
  }
  
  @Override
  public AbstractCustomerEntity loadCustomer(
      int customerId,
      boolean loadDistributorPaymentMethods,
      boolean loadDistributorUsers, 
      boolean loadUnmappedRawPointsOnly,
      boolean loadIgnoredRawPoints,
      boolean loadDeletedRawPoints) 
  throws 
      EntityDoesNotExistException {

    AbstractCustomerEntity customer = customerRepository.loadCustomer(
        customerId,
        loadDistributorPaymentMethods,
        loadDistributorUsers);
    
    Collection<RawPointEntity> rawPoints = rawPointRepository.loadRawPoints(
        customer, 
        loadUnmappedRawPointsOnly, 
        loadIgnoredRawPoints, 
        loadDeletedRawPoints);
    
    customer.addRawPoints(rawPoints);
    
    customer.setNotModified();
    customer.getParentDistributor().setNotModified();
    
    return customer;
  }

  @Override
  public AbstractCustomerEntity createCustomer(
      AbstractDistributorEntity parentDistributor,
      CustomerType customerType,
      String name) 
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException {
  
    long start = System.currentTimeMillis();
    AbstractCustomerEntity customer = customerRepository.createCustomer(
        parentDistributor, 
        customerType, 
        name,
        UnitSystem.IP.toString());
    
    customer.setNotModified();
    
    LOGGER.info("createCustomer(): parentDistributor: {} customerType: {}  name: {} elapsed(ms): {}",
        parentDistributor,
        customerType,
        name,
        (System.currentTimeMillis()-start));
    return customer;
  }

  @Override
  public AbstractCustomerEntity createCustomer(
      AbstractDistributorEntity parentDistributor,
      CustomerType customerType,
      String name,
      String unitSystemName) 
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException {
  
    long start = System.currentTimeMillis();
    AbstractCustomerEntity customer = customerRepository.createCustomer(
        parentDistributor, 
        customerType, 
        name,
        unitSystemName);
    
    customer.setNotModified();
    
    LOGGER.info("createCustomer(): parentDistributor: {} customerType: {} name: {} unitSystemName: {} elapsed(ms): {}",
        parentDistributor,
        customerType,
        name,
        unitSystemName,
        (System.currentTimeMillis()-start));
    return customer;
  }
  
  @Override
  public AbstractCustomerEntity createDemoCustomer(
      AbstractDistributorEntity parentDistributor,
      String name,
      Boolean isExpires,
      Boolean isInternal) 
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException {
    
    long start = System.currentTimeMillis();
    AbstractCustomerEntity customer = customerRepository.createCustomer(
        parentDistributor, 
        CustomerType.DEMO, 
        name, 
        UnitSystem.IP.toString(), 
        isExpires, 
        isInternal);
    
    customer.setNotModified();
    
    LOGGER.info("createDemoCustomer(): parentDistributor: {} name: {} elapsed(ms): {}",
        parentDistributor,
        name,
        (System.currentTimeMillis()-start));
    return customer;
  }
  
  @Override
  public AbstractCustomerEntity updateCustomer(AbstractCustomerEntity customer) {
    
    return updateCustomer(customer, false);
  }
  
  @Override
  public AbstractCustomerEntity updateCustomer(AbstractCustomerEntity customer, boolean storeRawPoints) {

    long start = System.currentTimeMillis();
    Set<String> modifiedAttributeNames = customer.getModifiedAttributes();
    if (customer.getIsModified()) {
      
      customerRepository.storeCustomer(customer);
      
      customer.setNotModified();
      
      if (storeRawPoints) {
        
        rawPointRepository.storeRawPoints(
            customer.getPersistentIdentity(),
            customer.getRawPoints());
      }
    }
    customer.setNotModified();
    LOGGER.info("updateCustomer(): customer: {} modified: {} elapsed(ms): {}",
        customer,
        modifiedAttributeNames,
        (System.currentTimeMillis()-start));
    return customer;
  }
  
  @Override
  public void softDeleteCustomer(AbstractCustomerEntity customer) {
    
    if (!customer.shouldBeSoftDeleted()) {
      throw new IllegalStateException("Customer: ["
          + customer.getName()
          + "] is not eligible for soft deletion.");
      
    }
    
    long start = System.currentTimeMillis();
    customerRepository.softDeleteCustomer(customer);
    LOGGER.info("softDeleteCustomer(): customer: {} elapsed(ms): {}",
        customer,
        (System.currentTimeMillis()-start));
  }

  @Override
  public void hardDeleteCustomer(AbstractCustomerEntity customer) {

    if (!customer.shouldBeHardDeleted()) {
      throw new IllegalStateException("Customer: ["
          + customer.getName()
          + "] is not eligible for hard deletion.");
      
    }
    
    try {
      long start = System.currentTimeMillis();
      customerRepository.hardDeleteCustomer(customer);
      LOGGER.info("hardDeleteCustomer(): customer: {} elapsed(ms): {}",
          customer,
          (System.currentTimeMillis()-start));
    } catch (Throwable t) {
      LOGGER.error("Unable to hard delete customer: {}", customer, t);
    }
  }

  @Override
  public List<RawPointEntity> loadRawPoints(
      Integer customerId,
      List<Integer> rawPointIds) {
    
    long start = System.currentTimeMillis();
    List<RawPointEntity> rawPoints = rawPointRepository.loadRawPoints(
        customerId, 
        rawPointIds);
    LOGGER.info("loadRawPoints(): customer: {} elapsed(ms): {}",
        customerId,
        (System.currentTimeMillis()-start));
    return rawPoints;
  }
  
  @Override
  public void ignoreRawPoints(
      Integer customerId, 
      List<Integer> rawPointIds) 
  throws 
      EntityDoesNotExistException {

    long start = System.currentTimeMillis();
    
    AbstractCustomerEntity customer = loadCustomer(
        customerId,
        false,  // loadDistributorPaymentMethods
        false); // loadDistributorUsers
    rawPointRepository.ignoreRawPoints(customer, rawPointIds);
    
    LOGGER.info("ignoreRawPoints(): customer: {} elapsed(ms): {}",
        customer,
        (System.currentTimeMillis()-start));
  }
  
  @Override
  public void unignoreRawPoints(
      Integer customerId, 
      List<Integer> rawPointIds) 
  throws 
      EntityDoesNotExistException {

    long start = System.currentTimeMillis();
    
    AbstractCustomerEntity customer = loadCustomer(
        customerId,
        false,  // loadDistributorPaymentMethods
        false); // loadDistributorUsers
    rawPointRepository.unignoreRawPoints(customer, rawPointIds);
    
    LOGGER.info("unignoreRawPoints(): customer: {} elapsed(ms): {}",
        customer,
        (System.currentTimeMillis()-start));
  }
  
  @Override 
  public boolean hasDatabaseRepository() {

    if (customerRepository instanceof CustomerRepositoryFileSystemImpl) {
      return false;  
    }
    return true;
  }
}
//@formatter:on
