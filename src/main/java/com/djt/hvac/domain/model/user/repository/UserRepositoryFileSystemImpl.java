//@formatter:off
package com.djt.hvac.domain.model.user.repository;

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
import java.util.List;
import java.util.Scanner;

import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.common.repository.AbstractRepositoryFileSystemImpl;
import com.djt.hvac.domain.model.customer.AbstractCustomerEntity;
import com.djt.hvac.domain.model.customer.repository.CustomerRepository;
import com.djt.hvac.domain.model.distributor.AbstractDistributorEntity;
import com.djt.hvac.domain.model.user.AbstractUserEntity;
import com.djt.hvac.domain.model.user.CustomerUserEntity;
import com.djt.hvac.domain.model.user.DistributorUserEntity;
import com.djt.hvac.domain.model.user.dto.UserDto;
import com.djt.hvac.domain.model.user.enums.UserRoleType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

public class UserRepositoryFileSystemImpl extends AbstractRepositoryFileSystemImpl implements UserRepository {

  private CustomerRepository customerRepository;
  
  public UserRepositoryFileSystemImpl(
      CustomerRepository customerRepository) {
    this(
        null,
        customerRepository);
  }

  public UserRepositoryFileSystemImpl(
      String basePath,
      CustomerRepository customerRepository) {
    super(basePath);
    requireNonNull(customerRepository, "customerRepository cannot be null");
    this.customerRepository = customerRepository;
  }
  
  @Override
  public DistributorUserEntity createDistributorUser(
      AbstractDistributorEntity parentDistributor,
      UserRoleType userRoleType,
      String email,
      String firstName,
      String lastName,
      Boolean isAccountManager)
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException {
    
    DistributorUserEntity distributorUser = new DistributorUserEntity(
        getNextPersistentIdentityValue(),
        userRoleType,
        email,
        firstName,
        lastName,
        false,
        true,
        parentDistributor,
        isAccountManager);
    
    parentDistributor.addChildDistributorUser(distributorUser);

    storeDistributorUsers(parentDistributor);
    
    return distributorUser;
  }
  
  @Override
  public CustomerUserEntity createCustomerUser(
      AbstractCustomerEntity parentCustomer,
      UserRoleType userRoleType,
      String email,
      String firstName,
      String lastName)
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException {
    
    CustomerUserEntity customerUser = new CustomerUserEntity(
        getNextPersistentIdentityValue(),
        userRoleType,
        email,
        firstName,
        lastName,
        false,
        true,
        parentCustomer);
    
    parentCustomer.addChildCustomerUser(customerUser);

    storeCustomerUsers(parentCustomer);
    
    return customerUser;
  }
  
  @Override
  public Collection<AbstractUserEntity> loadDistributorUsers(AbstractDistributorEntity parentDistributor) {
    
    List<AbstractUserEntity> entityList = new ArrayList<>();
    
    List<UserDto> dtoList = loadDistributorUserDtoList(parentDistributor.getPersistentIdentity());
    for (UserDto dto: dtoList) {
      
      AbstractUserEntity distributorUser = AbstractUserEntity
          .Mapper
          .getInstance()
          .mapDtoToEntity(parentDistributor, dto); 
      
      entityList.add(distributorUser);
      
      if (parentDistributor.getChildDistributorUserNullIfNotExists(distributorUser.getEmail()) == null) {
        try {
          parentDistributor.addChildDistributorUser((DistributorUserEntity)distributorUser);
        } catch (EntityAlreadyExistsException e) {
          throw new RuntimeException("Unable to add user: " + distributorUser + " to distributor: " + parentDistributor);
        }
      }
    }
    
    return entityList;
  }
  
  @Override
  public Collection<AbstractUserEntity> loadCustomerUsers(AbstractCustomerEntity parentCustomer) {

    List<AbstractUserEntity> entityList = new ArrayList<>();
    List<UserDto> dtoList = loadCustomerUserDtoList(parentCustomer.getPersistentIdentity());
    for (UserDto dto: dtoList) {
      
      AbstractUserEntity customerUser = AbstractUserEntity
          .Mapper
          .getInstance()
          .mapDtoToEntity(parentCustomer.getParentDistributor(), dto); 
      
      entityList.add(customerUser);
      
      try {
        ((CustomerUserEntity)customerUser).setParentCustomer(parentCustomer);
        parentCustomer.addChildCustomerUser((CustomerUserEntity)customerUser);
      } catch (EntityAlreadyExistsException e) {
        throw new RuntimeException("Unable to add user: " + customerUser + " to customer: " + parentCustomer);
      }
    }
    return entityList;
  }
  
  @Override
  public void loadAllDistributorUsers(Collection<AbstractDistributorEntity> parentDistributors) {
    
    for (AbstractDistributorEntity parentDistributor: parentDistributors) {
      
      loadDistributorUsers(parentDistributor);
    }
  }

  @Override
  public void loadAllCustomerUsers(Collection<AbstractCustomerEntity> parentCustomers) {
    
    for (AbstractCustomerEntity parentCustomer: parentCustomers) {
      
      loadCustomerUsers(parentCustomer);
    }
  }
  
  @Override
  public void storeDistributorUsers(AbstractDistributorEntity parentDistributor) {
     
    List<UserDto> dtoList = new ArrayList<>();
    for (AbstractUserEntity user: parentDistributor.getChildDistributorUsers()) {
      
      dtoList.add(AbstractUserEntity
          .Mapper
          .getInstance()
          .mapEntityToDto(user));
    }
    storeDistributorUserDtoList(parentDistributor.getPersistentIdentity(), dtoList);
  }

  @Override
  public void storeCustomerUsers(AbstractCustomerEntity parentCustomer) {
     
    List<UserDto> dtoList = new ArrayList<>();
    for (AbstractUserEntity user: parentCustomer.getChildCustomerUsers()) {
      
      dtoList.add(AbstractUserEntity
          .Mapper
          .getInstance()
          .mapEntityToDto(user));
    }
    storeCustomerUserDtoList(parentCustomer.getPersistentIdentity(), dtoList);
  }
  
  @Override
  public AbstractUserEntity loadUser(Integer userId, AbstractDistributorEntity rootDistributor) throws EntityDoesNotExistException {
    
    Collection<AbstractCustomerEntity> customers = new ArrayList<>();
    customers.addAll(customerRepository.loadChildCustomers(rootDistributor));

    Collection<AbstractDistributorEntity> distributors = rootDistributor.getAllDescendantDistributors();
    for (AbstractDistributorEntity distributor: distributors) {
    
      customers.addAll(customerRepository.loadChildCustomers(distributor));
    }
    
    loadAllDistributorUsers(distributors);
    loadAllCustomerUsers(customers);
    
    AbstractUserEntity user = null;
    
    for (AbstractDistributorEntity distributor: distributors) {
      for (AbstractUserEntity u: distributor.getChildDistributorUsers()) {
        if (u.getPersistentIdentity().equals(userId)) {
          user = u;
          break;
        }
      }
    }
    
    if (user == null) {
      for (AbstractCustomerEntity customer: customers) {

        for (AbstractUserEntity u: customer.getChildCustomerUsers()) {
          if (u.getPersistentIdentity().equals(userId)) {
            user = u;
            break;
          }
        }
      }    
    }
    
    if (user == null) {
      throw new EntityDoesNotExistException("User with id: ["
          + userId
          + "] does not exist");
    }
    
    return user;    
  }
  
  @Override
  public void storeUser(AbstractUserEntity user) {
    
    if (user instanceof DistributorUserEntity) {
      
      storeDistributorUsers(((DistributorUserEntity)user).getParentDistributor());
      
    } else {
      
      storeCustomerUsers(((CustomerUserEntity)user).getParentCustomer());

    }    
  }
  
  @Override
  public Collection<AbstractUserEntity> loadAllUsers(AbstractDistributorEntity rootDistributor) {
    
    Collection<AbstractCustomerEntity> customers = new ArrayList<>();
    customers.addAll(customerRepository.loadChildCustomers(rootDistributor));

    Collection<AbstractDistributorEntity> distributors = rootDistributor.getAllDescendantDistributors();
    for (AbstractDistributorEntity distributor: distributors) {
    
      customers.addAll(customerRepository.loadChildCustomers(distributor));
    }
    
    loadAllDistributorUsers(distributors);
    loadAllCustomerUsers(customers);
    
    Collection<AbstractUserEntity> users = new ArrayList<>();
    
    for (AbstractDistributorEntity distributor: distributors) {
      
      users.addAll(distributor.getChildDistributorUsers());
    }    

    for (AbstractCustomerEntity customer: customers) {
      
      users.addAll(customer.getChildCustomerUsers());
    }    
    
    return users;
  }
  
  private List<UserDto> loadDistributorUserDtoList(int distributorId) {
    
    File file = new File(basePath + "/Distributor_" + Integer.toString(distributorId) + "_Users.json");
    if (file.exists()) {
      try (InputStream in = new BufferedInputStream(new FileInputStream(file));
          
          Scanner s = new Scanner(in).useDelimiter("\\A")) {
          String fullJson = s.hasNext() ? s.next() : "";
  
          JsonNode jsonNode = MAPPER.get().readValue(fullJson, JsonNode.class);
          String compactJson = jsonNode.toString();
          
          List<UserDto> dtoList = MAPPER.get().readValue(compactJson, new TypeReference<List<UserDto>>() {});
          return dtoList;
  
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return new ArrayList<>();
  }
  
  private void storeDistributorUserDtoList(int distributorId, List<UserDto> dtoList) {
    
    if (dtoList != null && !dtoList.isEmpty()) {

      File file = new File(basePath + "/Distributor_" + Integer.toString(distributorId) + "_Users.json");
      OutputStream out = null;
      try {
        
        out = new BufferedOutputStream(new FileOutputStream(file));
        getObjectWriter().writeValue(out, dtoList);

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
  
  private List<UserDto> loadCustomerUserDtoList(int customerId) {
    
    File file = new File(basePath + "/Customer_" + Integer.toString(customerId) + "_Users.json");
    if (file.exists()) {
      try (InputStream in = new BufferedInputStream(new FileInputStream(file));
          
          Scanner s = new Scanner(in).useDelimiter("\\A")) {
          String fullJson = s.hasNext() ? s.next() : "";
  
          JsonNode jsonNode = MAPPER.get().readValue(fullJson, JsonNode.class);
          String compactJson = jsonNode.toString();
          
          List<UserDto> dtoList = MAPPER.get().readValue(compactJson, new TypeReference<List<UserDto>>() {});
          return dtoList;
  
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return new ArrayList<>();
  }
  
  private void storeCustomerUserDtoList(int customerId, List<UserDto> dtoList) {
    
    if (dtoList != null && !dtoList.isEmpty()) {

      File file = new File(basePath + "/Customer_" + Integer.toString(customerId) + "_Users.json");
      OutputStream out = null;
      try {
        
        out = new BufferedOutputStream(new FileOutputStream(file));
        getObjectWriter().writeValue(out, dtoList);

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
}
//@formatter:on