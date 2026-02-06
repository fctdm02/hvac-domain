//@formatter:off
package com.djt.hvac.domain.model.rawpoint.query;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

import com.djt.hvac.domain.model.cache.client.CacheClient;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.customer.AbstractCustomerEntity;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;
import com.djt.hvac.domain.model.nodehierarchy.repository.NodeHierarchyRepository;
import com.djt.hvac.domain.model.nodehierarchy.utils.LoadPortfolioOptions;
import com.djt.hvac.domain.model.rawpoint.RawPointEntity;
import com.djt.hvac.domain.model.rawpoint.query.model.RawPointData;
import com.djt.hvac.domain.model.rawpoint.query.model.RawPointQueryResponse;
import com.djt.hvac.domain.model.rawpoint.query.model.RawPointSearchCriteria;
import com.djt.hvac.domain.model.rawpoint.repository.RawPointRepository;

public class RawPointQueryDaoFileSystemImpl implements RawPointQueryDao<RawPointSearchCriteria, RawPointQueryResponse> {
  
  private RawPointRepository rawPointRepository;
  private NodeHierarchyRepository nodeHierarchyRepository;

  public RawPointQueryDaoFileSystemImpl(
      RawPointRepository rawPointRepository,
      NodeHierarchyRepository nodeHierarchyRepository) {
    
    requireNonNull(rawPointRepository, "rawPointRepository cannot be null");
    requireNonNull(nodeHierarchyRepository, "nodeHierarchyRepository cannot be null");
    this.rawPointRepository = rawPointRepository;
    this.nodeHierarchyRepository = nodeHierarchyRepository;
  }
  
  @Override
  public RawPointQueryResponse query(RawPointSearchCriteria searchCriteria) {
    
    try {
      
      LoadPortfolioOptions loadPortfolioOptions = LoadPortfolioOptions
	  .builder()
	  .withCustomerId(searchCriteria.getCustomerId())
	  .withLoadUnmappedRawPointsOnly(Boolean.TRUE)
	  .withLoadIgnoredRawPoints(Boolean.TRUE)
	  .withTimeToLiveInSeconds(CacheClient.ONE_DAY_TIME_TO_LIVE)
	  .build();
      
      PortfolioEntity portfolio = nodeHierarchyRepository.loadPortfolio(loadPortfolioOptions);
      
      AbstractCustomerEntity parentCustomer = portfolio.getParentCustomer();
      
      boolean loadUnmappedOnly = false;
      boolean loadIgnored = true;
      boolean loadDeleted = false;
      List<RawPointEntity> rawPoints = rawPointRepository.loadRawPoints(
	  parentCustomer,
	  loadUnmappedOnly,
	  loadIgnored,
	  loadDeleted);
      
      int numIgnoredPoints = 0;
      for (RawPointEntity rawPoint: rawPoints) {
        if (rawPoint.getIgnored()) {
          numIgnoredPoints++;
	    }
      }
      
      List<RawPointData> data = new ArrayList<>();
      data.add(RawPointData
	  .builder()
	  .withNumTotalPoints(rawPoints.size())
	  .withNumIgnoredPoints(numIgnoredPoints)
	  .build());
      
      return new RawPointQueryResponse(
	  searchCriteria,
	  1,
	  data);
      
    } catch (EntityDoesNotExistException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
}
//@formatter:on