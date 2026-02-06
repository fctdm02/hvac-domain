//@formatter:off
package com.djt.hvac.domain.model.customer.repository;

import static java.util.Objects.requireNonNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.customer.AbstractCustomerEntity;
import com.djt.hvac.domain.model.customer.DemoCustomerEntity;
import com.djt.hvac.domain.model.customer.OnlineCustomerEntity;
import com.djt.hvac.domain.model.customer.OutOfBandCustomerEntity;
import com.djt.hvac.domain.model.customer.dto.CustomerDto;
import com.djt.hvac.domain.model.customer.enums.CustomerStatus;
import com.djt.hvac.domain.model.customer.enums.CustomerType;
import com.djt.hvac.domain.model.dictionary.enums.UnitSystem;
import com.djt.hvac.domain.model.distributor.AbstractDistributorEntity;
import com.djt.hvac.domain.model.distributor.OutOfBandDistributorEntity;
import com.djt.hvac.domain.model.distributor.repository.DistributorRepository;
import com.djt.hvac.domain.model.rawpoint.repository.RawPointRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

public class CustomerRepositoryFileSystemImpl implements CustomerRepository {

  private static Integer MAX_PERSISTENT_IDENTITY_VALUE = Integer.valueOf(0);
  synchronized private static Integer getNextPersistentIdentityValue() {
    
    Integer nextValue = Integer.valueOf((MAX_PERSISTENT_IDENTITY_VALUE.intValue()-1));
    MAX_PERSISTENT_IDENTITY_VALUE = nextValue;
    return nextValue;
  }
  
  private static boolean USE_PRETTY_PRINT = true;
  public static boolean getPrettyPrint() {
    return USE_PRETTY_PRINT;
  }
  public static void setPrettyPrint(boolean prettyPrint) {
    USE_PRETTY_PRINT = prettyPrint;
  }

  private String basePath;
  private RawPointRepository rawPointRepository;
  private DistributorRepository distributorRepository;
  
  public CustomerRepositoryFileSystemImpl(
      RawPointRepository rawPointRepository,
      DistributorRepository distributorRepository) {
    
    this(
	null,
	rawPointRepository,
	distributorRepository);
  }

  public CustomerRepositoryFileSystemImpl(
      String basePath,
      RawPointRepository rawPointRepository,
      DistributorRepository distributorRepository) {

    requireNonNull(rawPointRepository, "rawPointRepository cannot be null");
    requireNonNull(distributorRepository, "distributorRepository cannot be null");
    
    if (basePath != null) {
      this.basePath = basePath;
    } else {
      this.basePath = System.getProperty("user.home") + "/";      
    }
    
    this.rawPointRepository = rawPointRepository;
    this.distributorRepository = distributorRepository;
  }
  
  public String basePath() {
    return basePath;
  }
  
  public void setBasePath(String basePath) {
    this.basePath = basePath;
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
    
    return createCustomer(
        parentDistributor, 
        customerType, 
        name, 
        unitSystemName,
        Boolean.FALSE,
        Boolean.FALSE);
  } 
  
  @Override
  public AbstractCustomerEntity createCustomer(
      AbstractDistributorEntity parentDistributor,
      CustomerType customerType,
      String name,
      String unitSystemName,
      Boolean isExpires,
      Boolean isInternal) 
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException {
    
    UnitSystem unitSystem = null;
    if (unitSystemName != null) {
      unitSystem = UnitSystem.get(unitSystemName);
    } else {
      unitSystem = parentDistributor.getUnitSystem(); 
    }
    
    AbstractCustomerEntity customer = null;
    if (customerType.equals(CustomerType.ONLINE)) {
      
      if (parentDistributor instanceof OutOfBandDistributorEntity) {
        throw new IllegalStateException("Cannot create online customer: ["
            +  name
            + "] with an out of band parent distributor: ["
            + parentDistributor
            + "]");
      }
      
      customer = new OnlineCustomerEntity(
          parentDistributor,
          name,
          unitSystem);
      
    } else if (customerType.equals(CustomerType.OUT_OF_BAND)) {

      customer = new OutOfBandCustomerEntity(
          parentDistributor,
          name,
          unitSystem);
      
    } else {

      customer = new DemoCustomerEntity(
          parentDistributor,
          name,
          unitSystem,
          isExpires,
          isInternal);
    }
    
    customer.setPersistentIdentity(getNextPersistentIdentityValue());
    
    parentDistributor.addChildCustomer(customer);
    
    storeCustomer(customer);
   
    return customer;
  }   
  
  @Override
  public Map<Integer, AbstractCustomerEntity> loadAllCustomers(
      boolean loadDistributorPaymentMethods,
      boolean loadDistributorUsers)
  throws 
      EntityDoesNotExistException {
    
    // Get the root distributor, as that has reach to any distributor/child.
    AbstractDistributorEntity rootDistributor = distributorRepository.getRootDistributor(
        loadDistributorPaymentMethods,
        loadDistributorUsers);
  
    Map<Integer, AbstractCustomerEntity> allCustomers = new TreeMap<>();
    
    List<CustomerDto> customerDtoList = loadCustomerDtoList();
    Iterator<CustomerDto> customerDtoIterator = customerDtoList.iterator();
    while (customerDtoIterator.hasNext()) {
      
      CustomerDto dto = customerDtoIterator.next();
      
      AbstractCustomerEntity customer = AbstractCustomerEntity
          .Mapper
          .getInstance()
          .mapDtoToEntity(rootDistributor, dto);

      allCustomers.put(customer.getPersistentIdentity(), customer);
    }
    
    return allCustomers;    
  }
  
  @Override
  public List<AbstractCustomerEntity> loadChildCustomers(AbstractDistributorEntity parentDistributor) {
    
    AbstractDistributorEntity rootDistributor = parentDistributor.getRootDistributor();
    
    List<AbstractCustomerEntity> childCustomers = new ArrayList<>();
    
    List<CustomerDto> customerDtoList = loadCustomerDtoList();
    Iterator<CustomerDto> customerDtoIterator = customerDtoList.iterator();
    while (customerDtoIterator.hasNext()) {
      
      CustomerDto dto = customerDtoIterator.next();
      
      AbstractCustomerEntity customer = AbstractCustomerEntity
          .Mapper
          .getInstance()
          .mapDtoToEntity(rootDistributor, dto);

      if (customer.getParentDistributor().equals(parentDistributor)) {
        childCustomers.add(customer);
      }
    }
    return childCustomers;
  }
  
  @Override
  public AbstractCustomerEntity loadCustomer(
      int customerId,
      boolean loadDistributorPaymentMethods,
      boolean loadDistributorUsers) 
  throws 
      EntityDoesNotExistException {
    
    Map<Integer, AbstractCustomerEntity> customerEntityMap = loadAllCustomers(
        loadDistributorPaymentMethods,
        loadDistributorUsers);
    
    AbstractCustomerEntity customer = customerEntityMap.get(Integer.valueOf(customerId));
    if (customer == null) {
      throw new EntityDoesNotExistException("Customer with id: ["
          + customerId
          + "] not found in customer id list: "
          + customerEntityMap.keySet()
          + "]");
    }
    
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
    
    Map<Integer, AbstractCustomerEntity> customerEntityMap = loadAllCustomers(
        loadDistributorPaymentMethods,
        loadDistributorUsers);
    
    AbstractCustomerEntity customer = customerEntityMap.get(Integer.valueOf(customerId));
    if (customer == null) {
      throw new EntityDoesNotExistException("Customer with id: ["
          + customerId
          + "] not found in customer id list: "
          + customerEntityMap.keySet()
          + "]");
    }
    
    if (loadUnmappedRawPointsOnly || loadIgnoredRawPoints || loadDeletedRawPoints) {
      
      rawPointRepository.loadRawPoints(
	  customer,
	  loadUnmappedRawPointsOnly,
	  loadIgnoredRawPoints,
	  loadDeletedRawPoints);
    }
    
    return customer;
  }
  
  @Override
  public void storeCustomer(AbstractCustomerEntity customer) {
    
    Map<Integer, AbstractCustomerEntity> customers = null;
    try {
      customers = loadAllCustomers(
          false,  // loadDistributorPaymentMethods
          false); // loadDistributorUsers
    } catch (EntityDoesNotExistException e) {
      throw new RuntimeException("Could not store customer: ["
          + customer
          + "], error: "
          + e.getMessage(), e);
    }
    customers.remove(customer.getPersistentIdentity());
    customers.put(customer.getPersistentIdentity(), customer);
    storeCustomers(customers.values());
  }

  @Override
  public void softDeleteCustomer(AbstractCustomerEntity customer) {

    if (customer instanceof OnlineCustomerEntity) {

      CustomerStatus customerStatus = customer.getCustomerStatus();
      
      if (customerStatus.equals(CustomerStatus.DELETED)) {
        
        // There is nothing to do, as we are already in the soft deleted state.
        return;
        
      } else if (!customerStatus.equals(CustomerStatus.CREATED)) {

        // The state is BILLABLE.
        throw new IllegalStateException("Online customer: ["
            + customer
            + "] cannot be soft deleted because it is not in the CREATED state. Its current state is: ["
            + customerStatus
            + "]");
      }
    }
    
    try {

      customer.setCustomerStatus(CustomerStatus.DELETED);
      Map<Integer, AbstractCustomerEntity> customers = loadAllCustomers(
          false,  // loadDistributorPaymentMethods
          false); // loadDistributorUsers
      customers.remove(customer.getPersistentIdentity());
      customers.put(customer.getPersistentIdentity(), customer);
      storeCustomers(customers.values());
      
    } catch (Exception e) {
      throw new RuntimeException("Unable to soft delete customer: ["
          + customer
          + "].", e);
    }
  }
  
  @Override
  public void hardDeleteCustomer(AbstractCustomerEntity customer) {

    CustomerStatus customerStatus = customer.getCustomerStatus();
    if (!customerStatus.equals(CustomerStatus.DELETED)) {

      throw new IllegalStateException("Customer: ["
          + customer
          + "] cannot be hard deleted because it is not in the DELETED state. Its current state is: ["
          + customerStatus
          + "]");
    }
    
    try {
      
      customer.getParentDistributor().removeChildCustomer(customer);
      Map<Integer, AbstractCustomerEntity> customers = loadAllCustomers(
          false,  // loadDistributorPaymentMethods
          false); // loadDistributorUsers
      customers.remove(customer.getPersistentIdentity());
      storeCustomers(customers.values());
      
    } catch (Exception e) {
      throw new RuntimeException("Unable to hard delete customer: ["
          + customer
          + "].", e);
    }
  }  
  
  @Override
  public void storeCustomers(Collection<AbstractCustomerEntity> customers) {
    
    List<CustomerDto> customerDtoList = new ArrayList<>();
    Iterator<AbstractCustomerEntity> customerIterator = customers.iterator();
    while (customerIterator.hasNext()) {
      customerDtoList.add(AbstractCustomerEntity
          .Mapper
          .getInstance()
          .mapEntityToDto(customerIterator.next()));
    }
    storeCustomerDtoList(customerDtoList);
  }
  
  private List<CustomerDto> loadCustomerDtoList() {
    
    File file = new File(basePath + "/Customers.json");
    if (file.exists()) {
      try (InputStream in = new BufferedInputStream(new FileInputStream(file));
          
          Scanner s = new Scanner(in).useDelimiter("\\A")) {
          String fullJson = s.hasNext() ? s.next() : "";
  
          JsonNode jsonNode = AbstractEntity.OBJECT_MAPPER.get().readValue(fullJson, JsonNode.class);
          String compactJson = jsonNode.toString();
          
          List<CustomerDto> dtoList = AbstractEntity.OBJECT_MAPPER.get().readValue(compactJson, new TypeReference<List<CustomerDto>>() {});
          return dtoList;
  
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return new ArrayList<>();
  }
  
  private void storeCustomerDtoList(List<CustomerDto> dtoList) {
    
    File file = new File(basePath + "/Customers.json");
    OutputStream out = null;
    try {
      
      out = new BufferedOutputStream(new FileOutputStream(file));
      AbstractEntity.OBJECT_WRITER.get().writeValue(out, dtoList);

    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (out != null) {
        try {
          out.flush();
          out.close();
        } catch (IOException ioe) {
        }
      }
    }
  }   
}
//@formatter:on