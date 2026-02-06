//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.point.query;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Map;

import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.nodehierarchy.point.query.model.PointLastValue;
import com.djt.hvac.domain.model.nodehierarchy.point.query.model.PointPointTemplateResponse;
import com.djt.hvac.domain.model.nodehierarchy.point.query.model.PointQueryResponse;
import com.djt.hvac.domain.model.nodehierarchy.point.query.model.PointSearchCriteria;
import com.djt.hvac.domain.model.nodehierarchy.repository.NodeHierarchyRepository;

public class PointQueryDaoFileSystemImpl implements PointQueryDao<PointSearchCriteria, PointQueryResponse> {
  
  private NodeHierarchyRepository nodeHierarchyRepository;

  public PointQueryDaoFileSystemImpl(NodeHierarchyRepository nodeHierarchyRepository) {
    requireNonNull(nodeHierarchyRepository, "nodeHierarchyRepository cannot be null");
    this.nodeHierarchyRepository = nodeHierarchyRepository;
  }
  
  @Override
  public PointQueryResponse query(PointSearchCriteria searchCriteria) {
    
    try {
      nodeHierarchyRepository.loadPortfolio(searchCriteria.getCustomerId());
    } catch (EntityDoesNotExistException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
    throw new RuntimeException("Not implemented yet!");
  }
  
  @Override
  public PointPointTemplateResponse getPointTemplatesForPoint(int customerId, int pointId) {
    throw new RuntimeException("Not implemented yet!");
  }
  
  @Override
  public Map<Integer, PointLastValue> getLastValueForMappablePoints(int customerId, List<Integer> buildingIds) {
    throw new RuntimeException("Not implemented yet!");
  }
  
  @Override
  public Map<Integer, PointLastValue> getLastValueForAsyncComputedPoints(int customerId, List<Integer> buildingIds) {
    throw new RuntimeException("Not implemented yet!");
  }
}
//@formatter:on