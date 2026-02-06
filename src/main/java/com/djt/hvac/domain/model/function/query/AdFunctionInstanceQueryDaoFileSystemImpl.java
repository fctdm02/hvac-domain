//@formatter:off
package com.djt.hvac.domain.model.function.query;

import static java.util.Objects.requireNonNull;

import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.function.query.model.AdFunctionInstanceQueryResponse;
import com.djt.hvac.domain.model.function.query.model.AdFunctionInstanceSearchCriteria;
import com.djt.hvac.domain.model.nodehierarchy.repository.NodeHierarchyRepository;

/**
 * 
 * @author tmyers
 *
 */
public class AdFunctionInstanceQueryDaoFileSystemImpl implements AdFunctionInstanceQueryDao {
  
  private NodeHierarchyRepository nodeHierarchyRepository;
  
  public AdFunctionInstanceQueryDaoFileSystemImpl(NodeHierarchyRepository nodeHierarchyRepository) {
    requireNonNull(nodeHierarchyRepository, "nodeHierarchyRepository cannot be null");
    this.nodeHierarchyRepository = nodeHierarchyRepository;
  }

  @Override
  public AdFunctionInstanceQueryResponse query(AdFunctionInstanceSearchCriteria searchCriteria) {
    
    try {
      nodeHierarchyRepository.loadPortfolio(searchCriteria.getCustomerId());
    } catch (EntityDoesNotExistException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
    throw new RuntimeException("Not implemented yet!");
  }
}
//@formatter:on
