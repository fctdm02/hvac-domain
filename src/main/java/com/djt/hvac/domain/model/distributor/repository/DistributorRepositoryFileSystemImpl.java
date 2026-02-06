//@formatter:off
package com.djt.hvac.domain.model.distributor.repository;

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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.customer.repository.CustomerRepository;
import com.djt.hvac.domain.model.customer.repository.CustomerRepositoryFileSystemImpl;
import com.djt.hvac.domain.model.dictionary.enums.UnitSystem;
import com.djt.hvac.domain.model.distributor.AbstractDistributorEntity;
import com.djt.hvac.domain.model.distributor.OnlineDistributorEntity;
import com.djt.hvac.domain.model.distributor.OutOfBandDistributorEntity;
import com.djt.hvac.domain.model.distributor.dto.DistributorDto;
import com.djt.hvac.domain.model.distributor.enums.DistributorStatus;
import com.djt.hvac.domain.model.distributor.enums.DistributorType;
import com.djt.hvac.domain.model.distributor.enums.PaymentMethodType;
import com.djt.hvac.domain.model.distributor.paymentmethod.AbstractPaymentMethodEntity;
import com.djt.hvac.domain.model.distributor.paymentmethod.CreditCardPaymentMethodEntity;
import com.djt.hvac.domain.model.distributor.service.model.CreatePaymentMethodRequest;
import com.djt.hvac.domain.model.rawpoint.repository.RawPointRepository;
import com.djt.hvac.domain.model.rawpoint.repository.RawPointRepositoryFileSystemImpl;
import com.djt.hvac.domain.model.user.DistributorUserEntity;
import com.djt.hvac.domain.model.user.enums.UserRoleType;
import com.djt.hvac.domain.model.user.repository.UserRepository;
import com.djt.hvac.domain.model.user.repository.UserRepositoryFileSystemImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

public class DistributorRepositoryFileSystemImpl implements DistributorRepository {

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
  private UserRepository userRepository;

  public DistributorRepositoryFileSystemImpl() {
    this(null, null, null);
  }

  public DistributorRepositoryFileSystemImpl(String basePath) {
    this(basePath, null, null);
  }

  public DistributorRepositoryFileSystemImpl(
      String basePath,
      UserRepository userRepository) {
    this(basePath, null, userRepository);
  }
  
  public DistributorRepositoryFileSystemImpl(
      String basePath,
      CustomerRepository customerRepository,
      UserRepository userRepository) {
    
    if (basePath != null) {
      this.basePath = basePath;
    } else {
      this.basePath = System.getProperty("user.home") + "/";      
    }
    
    if (userRepository != null) {
      this.userRepository = userRepository;
    } else {
      if (customerRepository == null) {
	RawPointRepository rawPointRepository = new RawPointRepositoryFileSystemImpl(this.basePath);
        customerRepository = new CustomerRepositoryFileSystemImpl(this.basePath, rawPointRepository, this);
      }
      this.userRepository = new UserRepositoryFileSystemImpl(this.basePath, customerRepository);
    }
  }
  
  public String getBasePath() {
    return basePath;
  }
  
  public void setBasePath(String basePath) {
    this.basePath = basePath;
  }
  
  public UserRepository getUserRepository() {
    return this.userRepository;
  }
  
  public void setUserRepository(UserRepository userRepository) {
    this.userRepository = userRepository;
  }
  
  @Override
  public Map<Integer, AbstractDistributorEntity> loadAllDistributors() {
    return loadAllDistributors(
        false,  // loadDistributorPaymentMethods
        false); // loadDistributorUsers
  }
  
  @Override
  public AbstractDistributorEntity getRootDistributor()
  throws 
      EntityDoesNotExistException {
    return getRootDistributor(
        false,  // loadDistributorPaymentMethods
        false); // loadDistributorUsers
  }
  
  @Override
  public Map<Integer, AbstractDistributorEntity> loadAllDistributors(
      boolean loadDistributorPaymentMethods,
      boolean loadDistributorUsers) {
    
    Map<Integer, AbstractDistributorEntity> distributorEntityMap = new HashMap<>();
    
    AbstractDistributorEntity rootDistributor = null;
    DistributorDto rootDistributorDto = null;
    
    // We always want to load all distributors so that we can properly build the 
    // distributor hierarchy for the given target distributor.
    List<DistributorDto> distributorDtoList = loadDistributorDtoList();
    
    Map<Integer, DistributorDto> distributorDtoMap = new HashMap<>();
    Iterator<DistributorDto> distributorDtoIterator = distributorDtoList.iterator();
    while (distributorDtoIterator.hasNext()) {
      
      DistributorDto dto = distributorDtoIterator.next();
      distributorDtoMap.put(dto.getId(), dto);
      
      if (dto.getParentId() == null) {
        rootDistributorDto = dto;
      }
    }

    rootDistributor = AbstractDistributorEntity
        .Mapper
        .getInstance()
        .mapDtoToEntity(rootDistributor, rootDistributorDto);
    
    rootDistributor.loadDistributorUsers = loadDistributorUsers;
    
    if (loadDistributorUsers) {
      userRepository.loadDistributorUsers(rootDistributor);
    }
    
    distributorEntityMap.put(rootDistributor.getPersistentIdentity(), rootDistributor);
    
    distributorDtoIterator = distributorDtoList.iterator();
    while (distributorDtoIterator.hasNext()) {
      
      DistributorDto dto = distributorDtoIterator.next();
      
      Integer parentId = dto.getParentId();
      if (parentId != null) {

        while (!parentId.equals(ROOT_DISTRIBUTOR_ID)) {
          
          DistributorDto parentDto = distributorDtoMap.get(parentId);

          parentId = parentDto.getParentId();
          
          AbstractDistributorEntity parentDistributor = distributorEntityMap.get(parentId);
          if (parentDistributor == null) {

            parentDistributor = AbstractDistributorEntity
                .Mapper
                .getInstance()
                .mapDtoToEntity(rootDistributor, parentDto);
            
            parentDistributor.loadDistributorPaymentMethods = loadDistributorPaymentMethods;
            parentDistributor.loadDistributorUsers = loadDistributorUsers;
            
            if (loadDistributorUsers) {
              userRepository.loadDistributorUsers(parentDistributor);  
            }
            
            distributorEntityMap.put(parentId, parentDistributor);
          }
        }

        Integer childId = dto.getId();
        AbstractDistributorEntity childDistributor = distributorEntityMap.get(childId);
        if (childDistributor == null) {

          childDistributor = AbstractDistributorEntity
              .Mapper
              .getInstance()
              .mapDtoToEntity(rootDistributor, dto);
          
          childDistributor.loadDistributorPaymentMethods = loadDistributorPaymentMethods;
          childDistributor.loadDistributorUsers = loadDistributorUsers;
          
          if (loadDistributorUsers) {
            userRepository.loadDistributorUsers(childDistributor);  
          }
          
          distributorEntityMap.put(childId, childDistributor);
        }
      }
    }    

    return distributorEntityMap;
  }

  @Override
  public AbstractDistributorEntity getRootDistributor(
      boolean loadDistributorPaymentMethods,
      boolean loadDistributorUsers) 
  throws 
      EntityDoesNotExistException {

    return loadDistributor(
        ROOT_DISTRIBUTOR_ID,
        loadDistributorPaymentMethods,
        loadDistributorUsers);
  }  

  @Override
  public AbstractDistributorEntity getRootDistributor(
      int customerId,
      boolean loadDistributorPaymentMethods,
      boolean loadDistributorUsers) 
  throws 
      EntityDoesNotExistException {
    
    // we don't care about filtering here.
    return loadDistributor(
        ROOT_DISTRIBUTOR_ID,
        loadDistributorPaymentMethods,
        loadDistributorUsers);
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
    
    Map<Integer, AbstractDistributorEntity> distributorEntityMap = loadAllDistributors(
        loadDistributorPaymentMethods,
        loadDistributorUsers);
    
    AbstractDistributorEntity distributor = distributorEntityMap.get(Integer.valueOf(distributorId));
    if (distributor == null) {
      throw new EntityDoesNotExistException("Distributor with id: ["
          + distributorId
          + "] not found in distributor id list: "
          + distributorEntityMap.keySet()
          + "]");
    }
    
    return distributor;
  }  
    
  @Override
  public void storeDistributor(AbstractDistributorEntity distributor) {
    
    Map<Integer, AbstractDistributorEntity> distributors = loadAllDistributors(true, true);
    distributors.remove(distributor.getPersistentIdentity());
    distributors.put(distributor.getPersistentIdentity(), distributor);
    storeDistributors(distributors.values());
  }
  
  @Override
  public void softDeleteDistributor(AbstractDistributorEntity distributor) {

    if (distributor.getParentDistributor() == null) {
      
      throw new IllegalArgumentException("The root distributor: ["
          + distributor
          + "] cannot be soft deleted");
    }  
    
    if (distributor instanceof OnlineDistributorEntity) {

      DistributorStatus distributorStatus = distributor.getDistributorStatus();
      
      if (distributorStatus.equals(DistributorStatus.DELETED)) {
        
        // There is nothing to do, as we are already in the soft deleted state.
        return;
        
      } else if (!distributorStatus.equals(DistributorStatus.CREATED)) {

        // The state is BILLABLE.
        throw new IllegalStateException("Online distributor: ["
            + distributor
            + "] cannot be soft deleted because it is not in the CREATED state. Its current state is: ["
            + distributorStatus
            + "]");
      }
    }
    
    try {

      distributor.setDistributorStatus(DistributorStatus.DELETED);
      Map<Integer, AbstractDistributorEntity> distributors = loadAllDistributors(true, true);
      distributors.remove(distributor.getPersistentIdentity());
      distributors.put(distributor.getPersistentIdentity(), distributor);
      storeDistributors(distributors.values());
      
    } catch (Exception e) {
      throw new IllegalStateException("Unable to soft delete distributor: ["
          + distributor
          + "].", e);
    }    
  }
  
  @Override
  public void hardDeleteDistributor(AbstractDistributorEntity distributor) {

    if (distributor.getParentDistributor() == null) {
      
      throw new IllegalArgumentException("The root distributor: ["
          + distributor
          + "] cannot be hard deleted");
    }
    
    DistributorStatus distributorStatus = distributor.getDistributorStatus();
    if (!distributorStatus.equals(DistributorStatus.DELETED)) {

      throw new IllegalArgumentException("Distributor: ["
          + distributor
          + "] cannot be hard deleted because it is not in the DELETED state. Its current state is: ["
          + distributorStatus
          + "]");
    }
    
    try {
      
      distributor.getParentDistributor().removeChildDistributor(distributor);
      Map<Integer, AbstractDistributorEntity> distributors = loadAllDistributors(true, true);
      distributors.remove(distributor.getPersistentIdentity());
      storeDistributors(distributors.values());
      
    } catch (Exception e) {
      throw new IllegalStateException("Unable to hard delete distributor: ["
          + distributor
          + "].", e);
    }
  }  
  
  @Override
  public void storeDistributors(Collection<AbstractDistributorEntity> distributors) {
    
    List<DistributorDto> distributorDtoList = new ArrayList<>();
    Iterator<AbstractDistributorEntity> distributorIterator = distributors.iterator();
    while (distributorIterator.hasNext()) {
      distributorDtoList.add(AbstractDistributorEntity
          .Mapper
          .getInstance()
          .mapEntityToDto(distributorIterator.next()));
    }
    storeDistributorDtoList(distributorDtoList);
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
    
    AbstractDistributorEntity rootDistributor = getRootDistributor(false, false);
    
    AbstractDistributorEntity parentDistributor = rootDistributor.getDescendantDistributorNullIfNotExists(parentDistributorId);
    if (parentDistributor == null) {
      throw new EntityDoesNotExistException("Parent distributor with id: ["
          + parentDistributorId
          + "] does not exist.");
    }
    
    String parentDistributorType = parentDistributor.getDistributorTypeDescription();
    if (parentDistributor.getParentDistributor() != null 
        && !distributorType.name().equals(parentDistributorType)) {
      
      throw new IllegalArgumentException("The given distributor type: ["
          + distributorType
          + "] is not compatible with the parent distributor: ["
          + parentDistributor
          + "], as its type is: ["
          + parentDistributorType
          + "]");
    }
    
    UnitSystem unitSystem = null;
    if (unitSystemName != null) {
      unitSystem = UnitSystem.get(unitSystemName);
    } else {
      unitSystem = parentDistributor.getUnitSystem();
    }
    
    AbstractDistributorEntity distributor = null;
    if (distributorType.equals(DistributorType.ONLINE)) {

      distributor = new OnlineDistributorEntity(
          parentDistributor,
          name,
          unitSystem,
          allowOutOfBandBuildings);
      
    } else {
      
      if (allowOutOfBandBuildings) {
        throw new IllegalArgumentException("'allowOutOfBandBuildings' cannot be set to true for OUT_OF_BAND distributor: ["
            + name
            + "]");
      }

      distributor = new OutOfBandDistributorEntity(
          parentDistributor,
          name,
          unitSystem);
    }
    
    distributor.setPersistentIdentity(getNextPersistentIdentityValue());
    
    parentDistributor.addChildDistributor(distributor);
   
    storeDistributor(distributor);
   
    return distributor;
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
      EntityDoesNotExistException {

    return userRepository.createDistributorUser(
        parentDistributor, 
        userRole, 
        email, 
        firstName, 
        lastName, 
        isAccountManager);
  }
  
  @Override
  public AbstractPaymentMethodEntity createPaymentMethod(
      OnlineDistributorEntity parentDistributor,
      CreatePaymentMethodRequest request) 
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException {
    
    if (!parentDistributor.getPersistentIdentity().equals(request.getParentDistributorId())) {
      throw new IllegalStateException("The given parent distributor's id: "
          + parentDistributor.getPersistentIdentity()
          + " does not match that of the request: "
          + request);
    }
    OnlineDistributorEntity onlineDistributor = parentDistributor;
    
    AbstractPaymentMethodEntity paymentMethod = new CreditCardPaymentMethodEntity(
        getNextPersistentIdentityValue(),
        onlineDistributor,
        PaymentMethodType.valueOf(request.getPaymentMethodType()),
        request.getName(),
        request.getStripeSourceId(),
        request.getAccountHolderName(),
        request.getAddress(),
        request.getCity(),
        request.getState(),
        request.getZipCode(),
        request.getPhoneNumber(),
        request.getCardBrand(),
        request.getCardExpiry(),
        request.getCardLastFour());
    
    onlineDistributor.addChildPaymentMethod(paymentMethod);
    
    storeDistributor(onlineDistributor);
    
    return paymentMethod;
  }
 
  @Override
  public void deletePaymentMethod(
      OnlineDistributorEntity parentDistributor,
      int paymentMethodId) 
  throws 
      EntityDoesNotExistException {
    
    AbstractPaymentMethodEntity paymentMethod = parentDistributor.getChildPaymentMethod(Integer.valueOf(paymentMethodId));
    
    paymentMethod.setIsDeleted();
    
    storeDistributor(parentDistributor);
  }
  
  @Override
  public Map<Integer, Integer> getPaymentMethodRefCounts(
      int distributorId)
  throws 
      EntityDoesNotExistException {
    
    throw new IllegalStateException("This method is not needed, as everything is done at the service level when a file system repository exists.");
  }
  
  private List<DistributorDto> loadDistributorDtoList() {
    
    List<DistributorDto> dtoList = null;
    
    File file = new File(basePath + "/Distributors.json");
    if (file.exists()) {
      try (InputStream in = new BufferedInputStream(new FileInputStream(file));
          
          Scanner s = new Scanner(in).useDelimiter("\\A")) {
          String fullJson = s.hasNext() ? s.next() : "";
  
          JsonNode jsonNode = AbstractEntity.OBJECT_MAPPER.get().readValue(fullJson, JsonNode.class);
          String compactJson = jsonNode.toString();
          
          dtoList = AbstractEntity.OBJECT_MAPPER.get().readValue(compactJson, new TypeReference<List<DistributorDto>>() {});
  
      } catch (IOException e) {
        throw new IllegalStateException(e);
      }
    }
    
    if (dtoList == null || dtoList.isEmpty()) {
      
      dtoList = new ArrayList<>();
      DistributorDto dto = new DistributorDto();
      dto.setId(ROOT_DISTRIBUTOR_ID);
      dto.setName("Resolute");
      dto.setCreatedAt(AbstractEntity.formatTimestamp(AbstractEntity.getTimeKeeper().getCurrentTimestamp()));
      dto.setUpdatedAt(AbstractEntity.formatTimestamp(AbstractEntity.getTimeKeeper().getCurrentTimestamp()));
      dto.setUuid("fdc03f69-e923-4304-a825-83738a65d88e");
      dto.setReferralAgentId(1);
      dto.setStatus("CREATED");
      dto.setStatusUpdatedAt(AbstractEntity.formatTimestamp(AbstractEntity.getTimeKeeper().getCurrentTimestamp()));
      dto.setPaymentStatus("UP_TO_DATE");
      dto.setPaymentStatusUpdatedAt(AbstractEntity.formatTimestamp(AbstractEntity.getTimeKeeper().getCurrentTimestamp()));
      dtoList.add(dto);      
    }
    return dtoList;
  }
  
  private void storeDistributorDtoList(List<DistributorDto> dtoList) {
    
    File file = new File(basePath + "/Distributors.json");
    OutputStream out = null;
    try {
      
      out = new BufferedOutputStream(new FileOutputStream(file));
      AbstractEntity.OBJECT_WRITER.get().writeValue(out, dtoList);

    } catch (IOException e) {
      throw new IllegalStateException(e);
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