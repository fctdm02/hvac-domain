//@formatter:off
package com.djt.hvac.domain.model.component.service;

import java.util.Collection;

import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.component.AbstractCustomerComponentEntity;

/**
 * 
 * @author tommyers
 * 
 */
public interface CustomerComponentService {

  /**
   * @param customerId
   * @return
   */
  Collection<AbstractCustomerComponentEntity> loadCustomerComponents(int customerId);

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
   * 
   * @param customerId
   * @param componentType
   * <pre>
      RESOLUTE_AGENT_1, 
      RESOLUTE_SECURITY_GATEWAY_2, 
      RESOLUTE_NIAGARA_MODULES_3, 
      RESOLUTE_PROVISIONING_SERVICE_4, 
      RESOLUTE_SSO_PROXY_5, 
   * </pre>
   * @param name
   * @param ipAddress
   * @return AbstractCustomerComponentEntity
   * @throws EntityAlreadyExistsException
   */
  AbstractCustomerComponentEntity createLegacyCustomerComponent(
      int customerId,
      String componentType,
      String name,
      String ipAddress)
  throws 
      EntityAlreadyExistsException;

  /**
   * 
   * @param customerId
   * @param componentType
   * <pre>
  CLOUDFILL_AGENT_6_Verdigris_1, 
  CLOUDFILL_AGENT_6_Demo_4,
   * </pre>
   * @param name
   * @param ipAddress
   * @param active
   * @param deleted
   * @return AbstractCustomerComponentEntity
   * @throws EntityAlreadyExistsException
   * @throws EntityDoesNotExistException
   */
  AbstractCustomerComponentEntity createNonConfigurableCloudfillCustomerComponent(
      int customerId,
      String componentType,
      String name,
      String ipAddress,
      Boolean active,
      Boolean deleted)
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException;
  
  /**
   * 
   * @param customerId
   * @param componentType
   * <pre>
  CLOUDFILL_AGENT_6_KMC_2,
  CLOUDFILL_AGENT_6_NHaystack_3,
  CLOUDFILL_AGENT_6_Fin_5,
  CLOUDFILL_AGENT_6_Desigo_6,
  CLOUDFILL_AGENT_6_Hawken_AQ_7
   * </pre>
   * @param name
   * @param ipAddress
   * @param active
   * @param deleted
   * @param configJson
   * @return AbstractCustomerComponentEntity
   * @throws EntityAlreadyExistsException
   * @throws EntityDoesNotExistException
   */
  AbstractCustomerComponentEntity createConfigurableCloudfillCustomerComponent(
      int customerId,
      String componentType,
      String name,
      String ipAddress,
      Boolean active,
      Boolean deleted,
      String configJson)
  throws
      EntityAlreadyExistsException,
      EntityDoesNotExistException;
  
  /**
   * 
   * @param customerId
   * @param customerComponentId
   * @param componentType
   * @param name
   * @param ipAddress
   * @param active
   * @param deleted
   * @param configJson
   * @return AbstractCustomerComponentEntity
   * @throws EntityDoesNotExistException
   */
  AbstractCustomerComponentEntity updateCustomerComponent(
      int customerId,
      int customerComponentId,
      String componentType,
      String name,
      String ipAddress,
      Boolean active,
      Boolean deleted,
      String configJson)
  throws
      EntityDoesNotExistException;
  
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