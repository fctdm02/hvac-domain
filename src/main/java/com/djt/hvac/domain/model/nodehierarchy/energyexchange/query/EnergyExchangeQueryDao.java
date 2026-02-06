//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.energyexchange.query;

import java.util.List;

import com.djt.hvac.domain.model.common.dto.EntityIndex;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.query.model.EnergyExchangeProgressResponse;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.query.model.EquipmentChildrenCandidatesSearchCriteria;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.query.model.EquipmentChildrenCandidatesSearchResponse;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.query.model.EquipmentChildrenSearchCriteria;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.query.model.EquipmentChildrenSearchResponse;

/**
 * 
 * @author tmyers
 *
 */
public interface EnergyExchangeQueryDao<EnergyExchangeSearchCriteria, EnergyExchangeQueryResponse> {
  
  /**
   * 
   * Deals every type of energy exchange system entity/node:
   * <ul>
   *   <li>EQUIPMENT</li>
   *   <li>PLANT</li>
   *   <li>LOOP</li>
   * </ul>
   * 
   * @param searchCriteria The criteria for retrieving energy exchange nodes
   * <ul>
   *   <li>customerId</li>
   *   <li>buildingI</li>
   *   <li>nodeType</li>
   *   <li>nodePath</li>
   *   <li>displayName</li>
   *   <li>energyExchangeTypeId</li>
   *   <li>energyExchangeId</li>
   *   <li>systemType</li>
   *   <li>parentIds</li>
   *   <li>childIds</li>
   * </ul>
   * 
   * @return The data corresponding to the given search criteria
   */
  EnergyExchangeQueryResponse query(EnergyExchangeSearchCriteria searchCriteria);
  
  /**
   * 
   * @param nodeType The type of energy exchange node
   * <ul>
   *   <li>EQUIPMENT</li>
   *   <li>PLANT</li>
   *   <li>LOOP</li>
   * </ul>
   * 
   * @param systemType The energy exchange system type:
   * <ul>
   *   <li>AIR_SUPPLY</li>
   *   <li>CHILLED_WATER</li>
   *   <li>HOT_WATER</li>
   *   <li>STEAM</li>
   * </ul>
   * 
   * @param customerId The owning customer
   * 
   * @return The list of parents for the given node type, system type for the given customer
   */
  List<EntityIndex> getEnergyExchangeParentList(String nodeType, String systemType, int customerId);
  
  /**
   * 
   * @param nodeType The type of energy exchange node
   * <ul>
   *   <li>EQUIPMENT</li>
   *   <li>PLANT</li>
   *   <li>LOOP</li>
   * </ul>
   * 
   * @param systemType The energy exchange system type:
   * <ul>
   *   <li>AIR_SUPPLY</li>
   *   <li>CHILLED_WATER</li>
   *   <li>HOT_WATER</li>
   *   <li>STEAM</li>
   * </ul>
   * 
   * @param customerId The owning customer
   * 
   * @return The type/parent tagging progress for the given node type and system type
   */
  EnergyExchangeProgressResponse getEnergyExchangeConfigurationProgress(String nodeType, String systemType, int customerId);
  
  /**
   * 
   * @param customerId The owning customer
   * @param equipmentId The equipment id
   * 
   * @return The list of child equipment ids for the given equipment id
   */
  List<Integer> getEquipmentChildrenIds(int customerId, int equipmentId);

  /**
   * 
   * @param customerId The owning customer
   * @param equipmentId The equipment id
   * @param searchCriteria The equipment children search criteria
   * 
   * @return The list of child equipment ids for the given equipment children search criteria
   */
  EquipmentChildrenSearchResponse getEquipmentChildren(int customerId, int equipmentId, EquipmentChildrenSearchCriteria searchCriteria);

  /**
   * 
   * @param customerId The owning customer
   * @param searchCriteria The equipment children candidates search criteria
   * 
   * @return The list of equipment children candidates for the given search criteria
   */
  EquipmentChildrenCandidatesSearchResponse getEquipmentChildrenCandidates(int customerId, EquipmentChildrenCandidatesSearchCriteria searchCriteria);
}
//@formatter:on