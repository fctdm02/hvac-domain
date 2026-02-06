//@formatter:off
package com.djt.hvac.domain.model.component.service;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.component.AbstractCloudfillCustomerComponentEntity;
import com.djt.hvac.domain.model.component.AbstractConfigurableCloudfillCustomerComponentEntity;
import com.djt.hvac.domain.model.component.AbstractCustomerComponentEntity;
import com.djt.hvac.domain.model.component.DemoCloudfillCustomerComponentEntity;
import com.djt.hvac.domain.model.component.DesigoCcCloudfillCustomerComponentEntity;
import com.djt.hvac.domain.model.component.FinCloudfillCustomerComponentEntity;
import com.djt.hvac.domain.model.component.HawkenAcCloudfillCustomerComponentEntity;
import com.djt.hvac.domain.model.component.KmcCloudfillCustomerComponentEntity;
import com.djt.hvac.domain.model.component.LegacyCustomerComponentEntity;
import com.djt.hvac.domain.model.component.NHaystackCloudfillCustomerComponentEntity;
import com.djt.hvac.domain.model.component.VerdigrisCloudfillCustomerComponentEntity;
import com.djt.hvac.domain.model.component.enums.ComponentType;
import com.djt.hvac.domain.model.component.repository.CustomerComponentRepository;

/**
 * 
 * @author tommyers
 * 
 */
public final class CustomerComponentServiceImpl implements CustomerComponentService {

  private final CustomerComponentRepository customerComponentRepository;

  public CustomerComponentServiceImpl(CustomerComponentRepository customerComponentRepository) {
    
    requireNonNull(customerComponentRepository, "customerComponentRepository cannot be null");
    this.customerComponentRepository = customerComponentRepository;
  }
  
  @Override
  public AbstractCustomerComponentEntity loadCustomerComponent(
      int customerId, 
      int customerComponentId)
  throws 
      EntityDoesNotExistException {
    
    return customerComponentRepository.loadCustomerComponent(customerId, customerComponentId);
  }
  
  @Override
  public Collection<AbstractCustomerComponentEntity> loadCustomerComponents(int customerId) {
    
    return customerComponentRepository.loadCustomerComponents(customerId);
  }

  @Override
  public AbstractCustomerComponentEntity createLegacyCustomerComponent(
      int customerId,
      String componentType,
      String name,
      String ipAddress)
  throws 
      EntityAlreadyExistsException {
    
    AbstractCustomerComponentEntity customerComponent = new LegacyCustomerComponentEntity(
        null,
        customerId,
        ComponentType.valueOf(componentType),
        UUID.randomUUID().toString(),
        name,
        ipAddress,
        AbstractEntity.getTimeKeeper().getCurrentTimestamp(),
        AbstractEntity.getTimeKeeper().getCurrentTimestamp());
    
    return storeLegacyCustomerComponent(customerId, customerComponent);
  }

  @Override
  public AbstractCustomerComponentEntity createNonConfigurableCloudfillCustomerComponent(
      int customerId,
      String componentType,
      String name,
      String ipAddress,
      Boolean active,
      Boolean deleted)
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException {
    
    AbstractCustomerComponentEntity customerComponent = null;
    
    ComponentType ct = ComponentType.valueOf(componentType);
    if (ct.equals(ComponentType.CLOUDFILL_AGENT_6_Verdigris_1)) {
      
      customerComponent = new VerdigrisCloudfillCustomerComponentEntity(
          null,
          customerId,
          ct,
          UUID.randomUUID().toString(),
          name,
          ipAddress,
          AbstractEntity.getTimeKeeper().getCurrentTimestamp(),
          AbstractEntity.getTimeKeeper().getCurrentTimestamp(),
          active,
          deleted);
      
    } else if (ct.equals(ComponentType.CLOUDFILL_AGENT_6_Demo_4)) {
      
      customerComponent = new DemoCloudfillCustomerComponentEntity(
          null,
          customerId,
          ct,
          UUID.randomUUID().toString(),
          name,
          ipAddress,
          AbstractEntity.getTimeKeeper().getCurrentTimestamp(),
          AbstractEntity.getTimeKeeper().getCurrentTimestamp(),
          active,
          deleted);
      
    } else {
      throw new RuntimeException("Component type: ["
          + ct
          + "] invalid for creating a non-configurable cloudfill component, only DEMO and VERDIGRIS are supported.");
    }
    
    return storeLegacyCustomerComponent(customerId, customerComponent);
  }
  
  @Override
  public AbstractCustomerComponentEntity createConfigurableCloudfillCustomerComponent(
      int customerId,
      String componentType,
      String name,
      String ipAddress,
      Boolean active,
      Boolean deleted,
      String configJson)
  throws
      EntityAlreadyExistsException,
      EntityDoesNotExistException {
    
    AbstractCustomerComponentEntity customerComponent = null;
    
    ComponentType ct = ComponentType.valueOf(componentType);
    if (ct.equals(ComponentType.CLOUDFILL_AGENT_6_KMC_2)) {
      
      customerComponent = new KmcCloudfillCustomerComponentEntity(
          null,
          customerId,
          ct,
          UUID.randomUUID().toString(),
          name,
          ipAddress,
          AbstractEntity.getTimeKeeper().getCurrentTimestamp(),
          AbstractEntity.getTimeKeeper().getCurrentTimestamp(),
          active,
          deleted,
          configJson);
      
    } else if (ct.equals(ComponentType.CLOUDFILL_AGENT_6_NHaystack_3)) {
      
      customerComponent = new NHaystackCloudfillCustomerComponentEntity(
          null,
          customerId,
          ct,
          UUID.randomUUID().toString(),
          name,
          ipAddress,
          AbstractEntity.getTimeKeeper().getCurrentTimestamp(),
          AbstractEntity.getTimeKeeper().getCurrentTimestamp(),
          active,
          deleted,
          configJson);
      
    } else if (ct.equals(ComponentType.CLOUDFILL_AGENT_6_Fin_5)) {
      
      customerComponent = new FinCloudfillCustomerComponentEntity(
          null,
          customerId,
          ct,
          UUID.randomUUID().toString(),
          name,
          ipAddress,
          AbstractEntity.getTimeKeeper().getCurrentTimestamp(),
          AbstractEntity.getTimeKeeper().getCurrentTimestamp(),
          active,
          deleted,
          configJson);      
      
    } else if (ct.equals(ComponentType.CLOUDFILL_AGENT_6_Desigo_6)) {
      
      customerComponent = new DesigoCcCloudfillCustomerComponentEntity(
          null,
          customerId,
          ct,
          UUID.randomUUID().toString(),
          name,
          ipAddress,
          AbstractEntity.getTimeKeeper().getCurrentTimestamp(),
          AbstractEntity.getTimeKeeper().getCurrentTimestamp(),
          active,
          deleted,
          configJson);      

    } else if (ct.equals(ComponentType.CLOUDFILL_AGENT_6_Hawken_AQ_7)) {
      
      customerComponent = new HawkenAcCloudfillCustomerComponentEntity(
          null,
          customerId,
          ct,
          UUID.randomUUID().toString(),
          name,
          ipAddress,
          AbstractEntity.getTimeKeeper().getCurrentTimestamp(),
          AbstractEntity.getTimeKeeper().getCurrentTimestamp(),
          active,
          deleted,
          configJson);      
      
    } else {
      throw new RuntimeException("Component type: ["
          + ct
          + "] invalid for creating a configurable cloudfill component, only KMC, NHAYSTACK, FIN, DESIGO and HAWKEN are supported.");
    }
    
    return storeLegacyCustomerComponent(customerId, customerComponent);
  }
  
  @Override
  public AbstractCustomerComponentEntity updateCustomerComponent(
      int customerId,
      int customerComponentId,
      String componentType,
      String name,
      String ipAddress,
      Boolean active,
      Boolean deleted,
      String configJson)
  throws
      EntityDoesNotExistException {
    
    AbstractCustomerComponentEntity customerComponent = customerComponentRepository.loadCustomerComponent(customerId, customerComponentId);
    
    // TODO: Deal with name conflict
    customerComponent.setName(name);
    
    customerComponent.setIpAddress(ipAddress);
    
    if (customerComponent instanceof AbstractCloudfillCustomerComponentEntity) {
      
      ((AbstractCloudfillCustomerComponentEntity)customerComponent).setActive(active);
      ((AbstractCloudfillCustomerComponentEntity)customerComponent).setDeleted(deleted);
      
      if (customerComponent instanceof AbstractConfigurableCloudfillCustomerComponentEntity) {
        
        ((AbstractConfigurableCloudfillCustomerComponentEntity)customerComponent).setConfigJson(configJson);
      }
    }
    
    return customerComponentRepository.storeCustomerComponent(customerComponent);
  }
  
  @Override
  public void deleteCustomerComponent(
      int customerId,
      int customerComponentId)
  throws
      EntityDoesNotExistException {
    
    customerComponentRepository.deleteCustomerComponent(customerId, customerComponentId);
  }
  
  private AbstractCustomerComponentEntity storeLegacyCustomerComponent(
      int customerId,
      AbstractCustomerComponentEntity customerComponent)
  throws 
      EntityAlreadyExistsException {
    
    Set<AbstractCustomerComponentEntity> customerComponents = customerComponentRepository.loadCustomerComponents(customerId);
    if (customerComponents.contains(customerComponent)) {
      throw new EntityAlreadyExistsException("Customer component with name: "
          + customerComponent.getName()
          + "] already exists for customer with id: ["
          + customerId
          + "].");
    }
    
    customerComponents.add(customerComponent);
    
    customerComponentRepository.storeCustomerComponents(customerId, customerComponents);
    
    return customerComponent;
  }
}  
//@formatter:on