//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.service;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Map;

import com.djt.hvac.domain.model.common.dto.EntityIndex;
import com.djt.hvac.domain.model.function.query.AdFunctionInstanceQueryDao;
import com.djt.hvac.domain.model.function.query.model.AdFunctionInstanceQueryResponse;
import com.djt.hvac.domain.model.function.query.model.AdFunctionInstanceSearchCriteria;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.query.EnergyExchangeQueryDao;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.query.model.EnergyExchangeProgressResponse;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.query.model.EnergyExchangeQueryResponse;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.query.model.EnergyExchangeSearchCriteria;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.query.model.EquipmentChildrenCandidatesSearchCriteria;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.query.model.EquipmentChildrenCandidatesSearchResponse;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.query.model.EquipmentChildrenSearchCriteria;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.query.model.EquipmentChildrenSearchResponse;
import com.djt.hvac.domain.model.nodehierarchy.point.query.PointQueryDao;
import com.djt.hvac.domain.model.nodehierarchy.point.query.model.PointLastValue;
import com.djt.hvac.domain.model.nodehierarchy.point.query.model.PointPointTemplateResponse;
import com.djt.hvac.domain.model.nodehierarchy.point.query.model.PointQueryResponse;
import com.djt.hvac.domain.model.nodehierarchy.point.query.model.PointSearchCriteria;
import com.djt.hvac.domain.model.rawpoint.query.RawPointQueryDao;
import com.djt.hvac.domain.model.rawpoint.query.model.RawPointQueryResponse;
import com.djt.hvac.domain.model.rawpoint.query.model.RawPointSearchCriteria;

/**
 * 
 * @author tmyers
 *
 */
public class NodeHierarchyQueryServiceImpl implements NodeHierarchyQueryService {
  
  private final AdFunctionInstanceQueryDao adFunctionInstanceQueryDao;
  private final EnergyExchangeQueryDao<EnergyExchangeSearchCriteria, EnergyExchangeQueryResponse> energyExchangeQueryDao;
  private final PointQueryDao<PointSearchCriteria, PointQueryResponse> pointQueryDao;
  private final RawPointQueryDao<RawPointSearchCriteria, RawPointQueryResponse> rawPointQueryDao;
  
  public NodeHierarchyQueryServiceImpl(
      AdFunctionInstanceQueryDao adFunctionInstanceQueryDao,
      EnergyExchangeQueryDao<EnergyExchangeSearchCriteria, EnergyExchangeQueryResponse> energyExchangeQueryDao,
      PointQueryDao<PointSearchCriteria, PointQueryResponse> pointQueryDao,
      RawPointQueryDao<RawPointSearchCriteria, RawPointQueryResponse> rawPointQueryDao) {
    
    requireNonNull(adFunctionInstanceQueryDao, "adFunctionInstanceQueryDao cannot be null");
    requireNonNull(energyExchangeQueryDao, "energyExchangeQueryDao cannot be null");
    requireNonNull(pointQueryDao, "pointQueryDao cannot be null");
    requireNonNull(rawPointQueryDao, "rawPpointQueryDao cannot be null");
    this.adFunctionInstanceQueryDao = adFunctionInstanceQueryDao;
    this.energyExchangeQueryDao = energyExchangeQueryDao;
    this.pointQueryDao = pointQueryDao;
    this.rawPointQueryDao = rawPointQueryDao;
  }

  @Override
  public AdFunctionInstanceQueryResponse getAdFunctionData(AdFunctionInstanceSearchCriteria searchCriteria) {
    return adFunctionInstanceQueryDao.query(searchCriteria);
  }
  
  @Override
  public EnergyExchangeQueryResponse getEnergyExchangeData(EnergyExchangeSearchCriteria searchCriteria) {
    return energyExchangeQueryDao.query(searchCriteria);
  }
  
  @Override
  public List<EntityIndex> getEnergyExchangeParentList(String nodeType, String systemType, int customerId) {
    return energyExchangeQueryDao.getEnergyExchangeParentList(nodeType, systemType, customerId);
  }
  
  @Override
  public EnergyExchangeProgressResponse getEnergyExchangeConfigurationProgress(String nodeType, String systemType, int customerId) {
    return energyExchangeQueryDao.getEnergyExchangeConfigurationProgress(nodeType, systemType, customerId);
  }
  
  @Override
  public List<Integer> getEquipmentChildrenIds(int customerId, int equipmentId) {
    return energyExchangeQueryDao.getEquipmentChildrenIds(customerId, equipmentId);
  }
  
  @Override
  public EquipmentChildrenSearchResponse getEquipmentChildren(int customerId, int equipmentId, EquipmentChildrenSearchCriteria searchCriteria) {
    return energyExchangeQueryDao.getEquipmentChildren(customerId, equipmentId, searchCriteria);
  }
  
  @Override
  public EquipmentChildrenCandidatesSearchResponse getEquipmentChildrenCandidates(int customerId, EquipmentChildrenCandidatesSearchCriteria searchCriteria) {
    return energyExchangeQueryDao.getEquipmentChildrenCandidates(customerId, searchCriteria);
  }
  
  @Override
  public PointQueryResponse getPointData(PointSearchCriteria searchCriteria) {
    return pointQueryDao.query(searchCriteria);
  }
  
  @Override
  public PointPointTemplateResponse getPointTemplatesForPoint(int customerId, int pointId) {
    return pointQueryDao.getPointTemplatesForPoint(customerId, pointId);
  }
  
  @Override
  public Map<Integer, PointLastValue> getLastValueForMappablePoints(int customerId, List<Integer> buildingIds) {
    return pointQueryDao.getLastValueForMappablePoints(customerId, buildingIds);
  }

  @Override
  public Map<Integer, PointLastValue> getLastValueForAsyncComputedPoints(int customerId, List<Integer> buildingIds) {
    return pointQueryDao.getLastValueForAsyncComputedPoints(customerId, buildingIds);
  }
  
  @Override
  public RawPointQueryResponse getRawPointData(RawPointSearchCriteria searchCriteria) {
    return rawPointQueryDao.query(searchCriteria);
  }
}
//@formatter:on