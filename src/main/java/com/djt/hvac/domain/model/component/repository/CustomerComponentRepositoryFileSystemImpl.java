package com.djt.hvac.domain.model.component.repository;

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
import java.util.Set;
import java.util.TreeSet;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.component.AbstractCustomerComponentEntity;
import com.djt.hvac.domain.model.component.dto.CustomerComponentDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

public class CustomerComponentRepositoryFileSystemImpl implements CustomerComponentRepository {

  //@formatter:off
  private static Integer MAX_PERSISTENT_IDENTITY_VALUE = Integer.valueOf(0);
  synchronized private static Integer getNextPersistentIdentityValue() {
    
    Integer nextValue = Integer.valueOf((MAX_PERSISTENT_IDENTITY_VALUE.intValue()-1));
    MAX_PERSISTENT_IDENTITY_VALUE = nextValue;
    return nextValue;
  }
  
  private static boolean USE_PRETTY_PRINT = false;
  public static boolean getPrettyPrint() {
    return USE_PRETTY_PRINT;
  }
  public static void setPrettyPrint(boolean prettyPrint) {
    USE_PRETTY_PRINT = prettyPrint;
  }

  private String basePath;

  public CustomerComponentRepositoryFileSystemImpl() {
    this(null);
  }

  public CustomerComponentRepositoryFileSystemImpl(String basePath) {
    super();
    if (basePath != null) {
      this.basePath = basePath;
    } else {
      this.basePath = System.getProperty("user.home") + "/";      
    }
  }
  
  public String basePath() {
    return basePath;
  }
  
  public void setBasePath(String basePath) {
    this.basePath = basePath;
  }

  @Override
  public Set<AbstractCustomerComponentEntity> loadCustomerComponents(Integer customerId) {
    
    Set<AbstractCustomerComponentEntity> entities = new TreeSet<>();
    List<CustomerComponentDto> dtoList = loadCustomerComponentDtoList(customerId);
    for (CustomerComponentDto dto: dtoList) {
      
      entities.add(AbstractCustomerComponentEntity
          .Mapper
          .getInstance()
          .mapDtoToEntity(customerId, dto));
    }
    return entities;
  }
  
  @Override
  public AbstractCustomerComponentEntity loadCustomerComponent(
      int customerId, 
      int customerComponentId)
  throws 
      EntityDoesNotExistException {
    
    Set<AbstractCustomerComponentEntity> entities = new TreeSet<>();
    List<CustomerComponentDto> dtoList = loadCustomerComponentDtoList(customerId);
    for (CustomerComponentDto dto: dtoList) {
      
      entities.add(AbstractCustomerComponentEntity
          .Mapper
          .getInstance()
          .mapDtoToEntity(customerId, dto));
    }
    for (AbstractCustomerComponentEntity entity: entities) {
      if (entity.getPersistentIdentity().equals(customerComponentId)) {
        return entity;
      }
    }
    throw new EntityDoesNotExistException("Customer component with id: ["
        + customerComponentId
        + "] does not exist for customer with id: ["
        + customerId
        + "].");
  }
  
  @Override
  public AbstractCustomerComponentEntity storeCustomerComponent(AbstractCustomerComponentEntity customerComponent) {
    
    Set<AbstractCustomerComponentEntity> entities = loadCustomerComponents(customerComponent.getCustomerId());
    entities.add(customerComponent);
    storeCustomerComponents(customerComponent.getCustomerId(), entities);
    return customerComponent;
  }
  
  @Override
  public void storeCustomerComponents(Integer customerId, Collection<AbstractCustomerComponentEntity> customerComponents) {

    List<CustomerComponentDto> dtoList = new ArrayList<>();
    for (AbstractCustomerComponentEntity customerComponent: customerComponents) {

      Integer id = customerComponent.getPersistentIdentity();
      if (id == null) {
        customerComponent.setPersistentIdentity(getNextPersistentIdentityValue());  
      }
      dtoList.add(AbstractCustomerComponentEntity
          .Mapper
          .getInstance()
          .mapEntityToDto(customerComponent));
    }
    storeCustomerComponentDtoList(customerId, dtoList);
  }
  
  @Override
  public void deleteCustomerComponent(
      int customerId,
      int customerComponentId)
  throws
      EntityDoesNotExistException {
    
    AbstractCustomerComponentEntity victim = null;
    Collection<AbstractCustomerComponentEntity> customerComponents = loadCustomerComponents(customerId);
    for (AbstractCustomerComponentEntity customerComponent: customerComponents) {
      if (customerComponent.getPersistentIdentity().equals(customerComponentId)) {
        victim = customerComponent;
        break;
      }
    }
    if (victim == null) {
      throw new EntityDoesNotExistException("Customer component with id: ["
          + customerComponentId
          + "] does not exist for customer with id: ["
          + customerId
          + "].");
    }
    customerComponents.remove(victim);
    storeCustomerComponents(customerId, customerComponents);
  }
  
  private List<CustomerComponentDto> loadCustomerComponentDtoList(int customerId) {
    
    File file = new File(basePath + "/Customer_" + Integer.toString(customerId) + "_CustomerComponents.json");
    if (file.exists()) {
      try (InputStream in = new BufferedInputStream(new FileInputStream(file));
          
          Scanner s = new Scanner(in).useDelimiter("\\A")) {
          String fullJson = s.hasNext() ? s.next() : "";
  
          JsonNode jsonNode = AbstractEntity.OBJECT_MAPPER.get().readValue(fullJson, JsonNode.class);
          String compactJson = jsonNode.toString();
          
          List<CustomerComponentDto> dtoList = AbstractEntity.OBJECT_MAPPER.get().readValue(compactJson, new TypeReference<List<CustomerComponentDto>>() {});
          return dtoList;
  
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return new ArrayList<>();
  }
  
  private void storeCustomerComponentDtoList(int customerId, List<CustomerComponentDto> dtoList) {
    
    File file = new File(basePath + "/Customer_" + Integer.toString(customerId) + "_CustomerComponents.json");
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
  //@formatter:off
}
