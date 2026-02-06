//@formatter:off
package com.djt.hvac.domain.model.component.repository;

import java.util.Collection;
import java.util.Set;

import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.component.AbstractCustomerComponentEntity;

/**
 * 
 * @author tommyers
 * 
 */
public interface CustomerComponentRepository {

  /**
   * 
   * @param customerId The owning customer
   * 
   * @return The customer components that were loaded
   */
  Set<AbstractCustomerComponentEntity> loadCustomerComponents(Integer customerId);
  
  /**
   * @param customerId
   * @param customerComponentId
   * @return
   * @throws EntityDoesNotExistException
   */
  AbstractCustomerComponentEntity loadCustomerComponent(
      int customerId, 
      int customerComponentId)
  throws 
      EntityDoesNotExistException;

  /**
   * @param customerComponent
   * @return
   */
  AbstractCustomerComponentEntity storeCustomerComponent(AbstractCustomerComponentEntity customerComponent);
  
  /**
   *  
   * @param customerId The owning customer
   * @param customerComponents The customer components to insert (either with or without a persistent
   *         identity that has been pre-assigned.
   */
  void storeCustomerComponents(Integer customerId, Collection<AbstractCustomerComponentEntity> customerComponents);
  
  /**
   * 
   * @param customerId
   * @param customerComponentId
   * @throws EntityDoesNotExistException
   */
  void deleteCustomerComponent(
      int customerId,
      int customerComponentId)
  throws
      EntityDoesNotExistException;  
}
//@formatter:on