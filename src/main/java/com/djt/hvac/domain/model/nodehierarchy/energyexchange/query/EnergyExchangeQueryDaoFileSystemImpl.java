//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.energyexchange.query;

import static java.util.Objects.requireNonNull;

import java.util.List;

import com.djt.hvac.domain.model.common.dto.EntityIndex;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.query.model.EnergyExchangeProgressResponse;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.query.model.EnergyExchangeQueryResponse;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.query.model.EnergyExchangeSearchCriteria;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.query.model.EquipmentChildrenCandidatesSearchCriteria;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.query.model.EquipmentChildrenCandidatesSearchResponse;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.query.model.EquipmentChildrenSearchCriteria;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.query.model.EquipmentChildrenSearchResponse;
import com.djt.hvac.domain.model.nodehierarchy.repository.NodeHierarchyRepository;

public class EnergyExchangeQueryDaoFileSystemImpl implements EnergyExchangeQueryDao<EnergyExchangeSearchCriteria, EnergyExchangeQueryResponse> {
  
  private NodeHierarchyRepository nodeHierarchyRepository;

  public EnergyExchangeQueryDaoFileSystemImpl(NodeHierarchyRepository nodeHierarchyRepository) {
    requireNonNull(nodeHierarchyRepository, "nodeHierarchyRepository cannot be null");
    this.nodeHierarchyRepository = nodeHierarchyRepository;
  }
  
  @Override
  public EnergyExchangeQueryResponse query(EnergyExchangeSearchCriteria searchCriteria) {
    
    try {
      nodeHierarchyRepository.loadPortfolio(searchCriteria.getCustomerId());
    } catch (EntityDoesNotExistException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
    throw new RuntimeException("Not implemented yet!");
  }
  
  @Override
  public List<EntityIndex> getEnergyExchangeParentList(String nodeType, String systemType, int customerId) {
    throw new RuntimeException("Not implemented yet!");
  }
  
  @Override
  public EnergyExchangeProgressResponse getEnergyExchangeConfigurationProgress(String nodeType, String systemType, int customerId) {
    throw new RuntimeException("Not implemented yet!");
  }
  
  @Override
  public List<Integer> getEquipmentChildrenIds(int customerId, int equipmentId) {
    throw new RuntimeException("Not implemented yet!");
  }
  
  @Override
  public EquipmentChildrenSearchResponse getEquipmentChildren(int customerId, int equipmentId, EquipmentChildrenSearchCriteria searchCriteria) {
    throw new RuntimeException("Not implemented yet!");
  }
  
  @Override
  public EquipmentChildrenCandidatesSearchResponse getEquipmentChildrenCandidates(int customerId, EquipmentChildrenCandidatesSearchCriteria searchCriteria) {
    throw new RuntimeException("Not implemented yet!");
  }
}
//@formatter:on