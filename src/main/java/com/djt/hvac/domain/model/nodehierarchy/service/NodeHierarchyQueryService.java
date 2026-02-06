//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.service;

import java.util.List;
import java.util.Map;

import com.djt.hvac.domain.model.common.dto.EntityIndex;
import com.djt.hvac.domain.model.function.query.model.AdFunctionInstanceQueryResponse;
import com.djt.hvac.domain.model.function.query.model.AdFunctionInstanceSearchCriteria;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.query.model.EnergyExchangeProgressResponse;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.query.model.EnergyExchangeQueryResponse;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.query.model.EnergyExchangeSearchCriteria;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.query.model.EquipmentChildrenCandidatesSearchCriteria;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.query.model.EquipmentChildrenCandidatesSearchResponse;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.query.model.EquipmentChildrenSearchCriteria;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.query.model.EquipmentChildrenSearchResponse;
import com.djt.hvac.domain.model.nodehierarchy.point.query.model.PointLastValue;
import com.djt.hvac.domain.model.nodehierarchy.point.query.model.PointPointTemplateResponse;
import com.djt.hvac.domain.model.nodehierarchy.point.query.model.PointQueryResponse;
import com.djt.hvac.domain.model.nodehierarchy.point.query.model.PointSearchCriteria;
import com.djt.hvac.domain.model.rawpoint.query.model.RawPointQueryResponse;
import com.djt.hvac.domain.model.rawpoint.query.model.RawPointSearchCriteria;

/**
 * 
 * @author tmyers
 *
 */
public interface NodeHierarchyQueryService {
  
  /**
   * 
   * Deals with AD function instances in every state:
   * <ul>
   *   <li>Ignored</li>
   *   <li>Disabled (i.e. "candidate")</li>
   *   <li>Enabled (i.e. "instance")</li>
   * </ul>
   * 
   * @param searchCriteria The criteria for retrieving AD function instances
   * 
   * @return The data corresponding to the given search criteria
   */
  AdFunctionInstanceQueryResponse getAdFunctionData(AdFunctionInstanceSearchCriteria searchCriteria);
  
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
   * 
   * @return The data corresponding to the given search criteria
   */
  EnergyExchangeQueryResponse getEnergyExchangeData(EnergyExchangeSearchCriteria searchCriteria);
  
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
  
  /**
   * 
   * @param searchCriteria The search criteria
   * @return The point query response
   */
  PointQueryResponse getPointData(PointSearchCriteria searchCriteria);
  
  /**
   * 
   * @param customerId The owning customer
   * @param pointId The point id to retrieve available point templates for
   * @return The point templates that are available for the given point
   */
  PointPointTemplateResponse getPointTemplatesForPoint(int customerId, int pointId);
  
  /**
   * 
   * @param customerId The owning customer
   * @param buildingIds The list of parent buildings
   * @return A map whose key is the id for the point and whose value is a map keyed by last value timestamp and value is actual point value for that timestamp
   */
  Map<Integer, PointLastValue> getLastValueForMappablePoints(int customerId, List<Integer> buildingIds);

  /**
   * 
   * @param customerId The owning customer
   * @param buildingIds The list of parent buildings
   * @return A map whose key is the id for the point and whose value is a map keyed by last value timestamp and value is actual point value for that timestamp
   */
  Map<Integer, PointLastValue> getLastValueForAsyncComputedPoints(int customerId, List<Integer> buildingIds);
  
  /**
   * 
   * @param searchCriteria The search criteria
   * @return The raw point query response
   */
  RawPointQueryResponse getRawPointData(RawPointSearchCriteria searchCriteria);
}
//@formatter:on