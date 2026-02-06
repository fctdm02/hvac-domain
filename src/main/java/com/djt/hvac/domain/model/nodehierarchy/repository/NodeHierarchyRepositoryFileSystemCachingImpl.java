//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.repository;

import static java.util.Objects.requireNonNull;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.djt.hvac.domain.model.cache.client.CacheClient;
import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.common.exception.StaleDataException;
import com.djt.hvac.domain.model.customer.AbstractCustomerEntity;
import com.djt.hvac.domain.model.customer.repository.CustomerRepository;
import com.djt.hvac.domain.model.dictionary.PaymentPlanEntity;
import com.djt.hvac.domain.model.dictionary.enums.FunctionType;
import com.djt.hvac.domain.model.dictionary.repository.DictionaryRepository;
import com.djt.hvac.domain.model.distributor.paymentmethod.AbstractPaymentMethodEntity;
import com.djt.hvac.domain.model.function.status.AdFunctionErrorMessageSearchCriteria;
import com.djt.hvac.domain.model.function.status.AdFunctionErrorMessagesValueObject;
import com.djt.hvac.domain.model.nodehierarchy.AbstractNodeEntity;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BillableBuildingEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BuildingSubscriptionEntity;
import com.djt.hvac.domain.model.nodehierarchy.dto.AddNodeDto;
import com.djt.hvac.domain.model.nodehierarchy.dto.TagInfo;
import com.djt.hvac.domain.model.nodehierarchy.dto.custompoint.AsyncPoint;
import com.djt.hvac.domain.model.nodehierarchy.event.NodeHierarchyChangeEvent;
import com.djt.hvac.domain.model.nodehierarchy.service.command.NodeHierarchyCommandRequest;
import com.djt.hvac.domain.model.nodehierarchy.utils.LoadPortfolioOptions;
import com.djt.hvac.domain.model.rawpoint.repository.RawPointRepository;
import com.djt.hvac.domain.model.report.status.PortfolioReportSummaryValueObject;
import com.djt.hvac.domain.model.report.status.ReportEquipmentErrorMessageSearchCriteria;
import com.djt.hvac.domain.model.report.status.ReportEquipmentErrorMessageValueObject;
import com.fasterxml.jackson.core.type.TypeReference;

public class NodeHierarchyRepositoryFileSystemCachingImpl extends AbstractNodeHierarchyRepository {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(NodeHierarchyRepositoryFileSystemCachingImpl.class);
  
  private static final String LAST_UPDATED_CUSTOMER_ID_KEY_PREFIX = "last_updated_customer_id_";

  private final NodeHierarchyRepository nodeHierarchyRepository;
  private final CacheClient cacheClient;
  
  public NodeHierarchyRepositoryFileSystemCachingImpl(
      NodeHierarchyRepository nodeHierarchyRepository,
      CacheClient cacheClient,
      RawPointRepository rawPointRepository,
      CustomerRepository customerRepository,
      DictionaryRepository dictionaryRepository) {
    super(
	rawPointRepository,
        customerRepository,
        dictionaryRepository);

    requireNonNull(nodeHierarchyRepository, "nodeHierarchyRepository cannot be null");
    requireNonNull(cacheClient, "cacheClient cannot be null");
    this.nodeHierarchyRepository = nodeHierarchyRepository;
    this.cacheClient = cacheClient;
  }
  
  @Override
  public PortfolioEntity loadPortfolio(
      LoadPortfolioOptions loadPortfolioOptions) 
  throws 
      EntityDoesNotExistException {
    
    long start = System.currentTimeMillis();
    LOGGER.debug("loadPortfolio(): BEGIN");  
    
    // Attempt to load the portfolio (guaranteed to be updated), from the cache.
    Timestamp portfolioNodeUpdatedAt = getPortfolioNodeUpdatedAt(loadPortfolioOptions.getCustomerId());
    
    PortfolioEntity portfolio = cacheClient.loadPortfolio(
        loadPortfolioOptions,
        portfolioNodeUpdatedAt);
    
    // If the portfolio wasn't in the cache, or was stale, then load from the DB and then store in the cache.
    if (portfolio == null) {

      dictionaryRepository.ensureDictionaryDataIsLoaded();
      portfolio = nodeHierarchyRepository.loadPortfolio(loadPortfolioOptions);
      cacheClient.storePortfolio(loadPortfolioOptions, portfolio);
    }
    
    LOGGER.debug("loadPortfolio() END: elapsed(ms): {}", (System.currentTimeMillis()-start));  
    
    return portfolio;
  }
  
  @Override
  public Timestamp getPortfolioNodeUpdatedAt(int customerId) {
    
    String key = LAST_UPDATED_CUSTOMER_ID_KEY_PREFIX + customerId;
    byte[] value = cacheClient.get(key);
    if (value != null) {
      return AbstractEntity.parseTimestamp(new String(value));
    }
    return null;
  }
  
  @Override
  public Map<String, List<AbstractNodeEntity>> storePortfolio(
      PortfolioEntity portfolio,
      NodeHierarchyCommandRequest request,
      boolean reportsWereEvaluated) 
  throws 
      StaleDataException {
    
    // Remove all existing permutations of portfolios for the given customer id.
    cacheClient.removeAllCacheEntriesForCustomer(portfolio.getCustomerId());
    
    String key = LAST_UPDATED_CUSTOMER_ID_KEY_PREFIX + portfolio.getCustomerId();
    byte[] value = AbstractEntity.formatTimestamp(AbstractEntity.getTimeKeeper().getCurrentTimestamp()).getBytes();
    
    cacheClient.set(
        key,
        value,
        CacheClient.ONE_DAY_TIME_TO_LIVE);
    
    return nodeHierarchyRepository.storePortfolio(portfolio, request, reportsWereEvaluated);
  }
  
  @Override
  public PortfolioReportSummaryValueObject getReportConfigurationStatus(
      int customerId, 
      boolean noInternalReports,
      String rubyTimezoneLabel) {
    
    try {

      String key = CacheClient.KEY_PREFIX + "report_configuration_status_for_customer_id_" 
          + customerId
          + "_noInternalReports_" 
          + noInternalReports
          + "_rubyTimezoneLabel_"
          + rubyTimezoneLabel;
      
      PortfolioReportSummaryValueObject dto = null;
      byte[] value= cacheClient.get(key);
      if (value != null) {
        
        dto = AbstractEntity.OBJECT_MAPPER.get().readValue(value, new TypeReference<PortfolioReportSummaryValueObject>() {});
        
      } else {
        
        dto = nodeHierarchyRepository.getReportConfigurationStatus(customerId, noInternalReports, rubyTimezoneLabel);
        cacheClient.set(
            key,
            AbstractEntity.OBJECT_MAPPER.get().writeValueAsBytes(dto.toString().getBytes()),
            CacheClient.ONE_HOUR_TIME_TO_LIVE);
        
      }
      
      return dto;
      
    } catch (Exception e) {
      throw new RuntimeException("Error retrieving report configuration status for customerId: " + customerId, e);
    }    
  }
   
  @Override
  public int getReportEquipmentErrorMessagesCount(
      int customerId,
      ReportEquipmentErrorMessageSearchCriteria searchCriteria) {

    try {
      
      String key = CacheClient.KEY_PREFIX + "report_equipment_error_messages_count_for_customer_id_" 
          + customerId
          + "_searchCriteria_" 
          + searchCriteria;

      Integer dto = null;
      byte[] value = cacheClient.get(key);
      if (value != null) {
        
        dto = AbstractEntity.OBJECT_MAPPER.get().readValue(value, new TypeReference<Integer>() {});
        
      } else {
        
        dto = this.nodeHierarchyRepository.getReportEquipmentErrorMessagesCount(customerId, searchCriteria);
        value = AbstractEntity.OBJECT_MAPPER.get().writeValueAsBytes(dto);
        cacheClient.set(
            key, 
            value,
            CacheClient.ONE_HOUR_TIME_TO_LIVE);
        
      }
      
      return dto;
      
    } catch (Exception e) {
      throw new RuntimeException("Error retrieving report equipment error messages count for customerId: " + customerId, e);
    }
  }

  @Override
  public List<ReportEquipmentErrorMessageValueObject> getReportEquipmentErrorMessages(
      int customerId,
      ReportEquipmentErrorMessageSearchCriteria searchCriteria) {

    try {
      
      String key = CacheClient.KEY_PREFIX + "report_equipment_error_messages_for_customer_id_" 
          + customerId
          + "_searchCriteria_" 
          + searchCriteria;

      List<ReportEquipmentErrorMessageValueObject> dtoList = null;
      byte[] value = cacheClient.get(key);
      if (value != null) {
        
        dtoList = AbstractEntity.OBJECT_MAPPER.get().readValue(value, new TypeReference<List<ReportEquipmentErrorMessageValueObject>>() {});
        
      } else {
        
        dtoList = this.nodeHierarchyRepository.getReportEquipmentErrorMessages(customerId, searchCriteria);
        value = AbstractEntity.OBJECT_MAPPER.get().writeValueAsBytes(dtoList);
        cacheClient.set(
            key,
            value,
            CacheClient.ONE_HOUR_TIME_TO_LIVE);
        
      }
      
      return dtoList;
      
    } catch (Exception e) {
      throw new RuntimeException("Error retrieving report equipment error messages for customerId: " + customerId, e);
    }
  }
  
  @Override
  public PortfolioEntity createPortfolio(
      AbstractCustomerEntity parentCustomer,
      String name,
      String displayName)
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException {
    
    return nodeHierarchyRepository.createPortfolio(parentCustomer, name, displayName);
  }
  
  @Override
  public BuildingSubscriptionEntity createBuildingSubscription(
      BillableBuildingEntity parentBuilding,
      PaymentPlanEntity parentPaymentPlan,
      AbstractPaymentMethodEntity parentPaymentMethod,
      String stripeSubscriptionId) 
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException {
    
    return nodeHierarchyRepository.createBuildingSubscription(parentBuilding, parentPaymentPlan, parentPaymentMethod, stripeSubscriptionId);
  }
  
  @Override
  public Integer getBuildingIdForDescendantId(
      Integer customerId,
      Integer descendantId)
  throws 
      EntityDoesNotExistException {
    
    try {
      
      String key = CacheClient.KEY_PREFIX + "building_id_for_customer_id_" 
          + customerId 
          + "_descendantId_"
          + descendantId;

      Integer dto = null;
      byte[] value = cacheClient.get(key);
      if (value != null) {
        
        dto = AbstractEntity.OBJECT_MAPPER.get().readValue(value, new TypeReference<Integer>() {});
        
      } else {
        
        dto = this.nodeHierarchyRepository.getBuildingIdForDescendantId(customerId, descendantId);
        value = AbstractEntity.OBJECT_MAPPER.get().writeValueAsBytes(dto);
        cacheClient.set(
            key,
            value,
            CacheClient.ONE_HOUR_TIME_TO_LIVE);
        
      }
      
      return dto;
      
    } catch (Exception e) {
      throw new RuntimeException("Error retrieving buildingId for descendantId for customerId: " + customerId, e);
    }
  }
  
  @Override
  public Set<Integer> getBuildingIdsForDescendantIds(
      Integer customerId, 
      Collection<Integer> descendantIds)
  throws 
      EntityDoesNotExistException {
    
    try {
      
      String key = CacheClient.KEY_PREFIX + "building_ids_for_customer_id_" 
          + customerId 
          + "_descendantIds_"
          + descendantIds;

      Set<Integer> dto = null;
      byte[] value = cacheClient.get(key);
      if (value != null) {
        
        dto = AbstractEntity.OBJECT_MAPPER.get().readValue(value, new TypeReference<Set<Integer>>() {});
        
      } else {
        
        dto = this.nodeHierarchyRepository.getBuildingIdsForDescendantIds(customerId, descendantIds);
        value = AbstractEntity.OBJECT_MAPPER.get().writeValueAsBytes(dto);
        cacheClient.set(
            key,
            value,
            CacheClient.ONE_HOUR_TIME_TO_LIVE);
        
      }
      
      return dto;
      
    } catch (Exception e) {
      throw new RuntimeException("Error retrieving buildingId for descendantIds for customerId: " + customerId, e);
    }
  }
  
  @Override
  public Set<Integer> getBuildingIdsForAdFunctionInstanceIds(
      Integer customerId,
      Collection<Integer> instanceIds)
  throws 
      EntityDoesNotExistException {
    
    try {
      
      String key = CacheClient.KEY_PREFIX + "building_ids_for_customer_id_" 
          + customerId 
          + "_adFunctionInstanceIds_"
          + instanceIds;

      Set<Integer> dto = null;
      byte[] value = cacheClient.get(key);
      if (value != null) {
        
        dto = AbstractEntity.OBJECT_MAPPER.get().readValue(value, new TypeReference<Set<Integer>>() {});
        
      } else {
        
        dto = this.nodeHierarchyRepository.getBuildingIdsForAdFunctionInstanceIds(customerId, instanceIds);
        value = AbstractEntity.OBJECT_MAPPER.get().writeValueAsBytes(dto);
        cacheClient.set(
            key,
            value,
            CacheClient.ONE_HOUR_TIME_TO_LIVE);
        
      }
      
      return dto;
      
    } catch (Exception e) {
      throw new RuntimeException("Error retrieving buildingIds for instanceIds for customerId: " + customerId, e);
    }
  }
  
  @Override
  public Set<Integer> getBuildingIdsForRawPointIds(
      Integer customerId,
      Collection<Integer> rawPointIds)
  throws 
      EntityDoesNotExistException {
    
    try {
      
      String key = CacheClient.KEY_PREFIX + "building_ids_for_customer_id_" 
          + customerId 
          + "_rawPointIds_"
          + rawPointIds;

      Set<Integer> dto = null;
      byte[] value = cacheClient.get(key);
      if (value != null) {
        
        dto = AbstractEntity.OBJECT_MAPPER.get().readValue(value, new TypeReference<Set<Integer>>() {});
        
      } else {
        
        dto = this.nodeHierarchyRepository.getBuildingIdsForRawPointIds(customerId, rawPointIds);
        value = AbstractEntity.OBJECT_MAPPER.get().writeValueAsBytes(dto);
        cacheClient.set(
            key,
            value,
            CacheClient.ONE_HOUR_TIME_TO_LIVE);
        
      }
      
      return dto;
      
    } catch (Exception e) {
      throw new RuntimeException("Error retrieving buildingIds for rawPointIds for customerId: " + customerId, e);
    }
  }
  
  @Override
  public List<Integer> getBuildingIds(Integer customerId) {
    
    try {
      
      String key = CacheClient.KEY_PREFIX + "building_ids_for_customer_id_" 
          + customerId;

      List<Integer> dtoList = null;
      byte[] value = cacheClient.get(key);
      if (value != null) {
        
        dtoList = AbstractEntity.OBJECT_MAPPER.get().readValue(value, new TypeReference<List<Integer>>() {});
        
      } else {
        
        dtoList = this.nodeHierarchyRepository.getBuildingIds(customerId);
        value = AbstractEntity.OBJECT_MAPPER.get().writeValueAsBytes(dtoList);
        cacheClient.set(
            key,
            value,
            CacheClient.ONE_HOUR_TIME_TO_LIVE);
        
      }
      
      return dtoList;
      
    } catch (Exception e) {
      throw new RuntimeException("Error retrieving buildingIds for customerId: " + customerId, e);
    }
  }  
  
  @Override
  public Double getAdFunctionConfigurationStatusPercent(
      Integer customerId,
      FunctionType functionType)
  throws
      EntityDoesNotExistException {
    
    try {
      
      String key = CacheClient.KEY_PREFIX + "ad_function_error_configuration_status_percent_for_customer_id_" 
          + customerId 
          + "_functionType_"
          + functionType;

      Double dto = null;
      byte[] value = cacheClient.get(key);
      if (value != null) {
        
        dto = AbstractEntity.OBJECT_MAPPER.get().readValue(value, new TypeReference<Double>() {});
        
      } else {
        
        dto = this.nodeHierarchyRepository.getAdFunctionConfigurationStatusPercent(customerId, functionType);
        value = AbstractEntity.OBJECT_MAPPER.get().writeValueAsBytes(dto);
        cacheClient.set(
            key,
            value,
            CacheClient.ONE_HOUR_TIME_TO_LIVE);
        
      }
      return dto;
      
    } catch (Exception e) {
      throw new RuntimeException("Error retrieving ad function configuration status percent for customerId: " + customerId, e);
    }  
  }
  
  @Override
  public Double getEnabledAdFunctionInstancesPercent(
      Integer customerId,
      FunctionType functionType)
  throws
      EntityDoesNotExistException {

    try {
      
      String key = CacheClient.KEY_PREFIX + "enabled_ad_function_instances_percent_for_customer_id_" 
          + customerId 
          + "_functionType_"
          + functionType;

      Double dto = null;
      byte[] value = cacheClient.get(key);
      if (value != null) {
        
        dto = AbstractEntity.OBJECT_MAPPER.get().readValue(value, new TypeReference<Double>() {});
        
      } else {
        
        dto = this.nodeHierarchyRepository.getEnabledAdFunctionInstancesPercent(customerId, functionType);
        value = AbstractEntity.OBJECT_MAPPER.get().writeValueAsBytes(dto);
        cacheClient.set(
            key,
            value,
            CacheClient.ONE_HOUR_TIME_TO_LIVE);
        
      }
      return dto;
      
    } catch (Exception e) {
      throw new RuntimeException("Error retrieving ad function instances percent for customerId: " + customerId, e);
    }   
  }  
  
  @Override
  public int getAdFunctionErrorMessagesCount(
      int customerId,
      AdFunctionErrorMessageSearchCriteria searchCriteria) {
    
    try {
      
      String key = CacheClient.KEY_PREFIX + "ad_function_error_messages_count_for_customer_id_" 
          + customerId 
          + "_searchCriteria_"
          + searchCriteria;

      Integer dto = null;
      byte[] value = cacheClient.get(key);
      if (value != null) {
        
        dto = AbstractEntity.OBJECT_MAPPER.get().readValue(value, new TypeReference<Integer>() {});
        
      } else {
        
        dto = this.nodeHierarchyRepository.getAdFunctionErrorMessagesCount(customerId, searchCriteria);
        value = AbstractEntity.OBJECT_MAPPER.get().writeValueAsBytes(dto);
        cacheClient.set(
            key,
            value,
            CacheClient.ONE_HOUR_TIME_TO_LIVE);
        
      }
      return dto;
      
    } catch (Exception e) {
      throw new RuntimeException("Error retrieving ad function error messages count for customerId: " + customerId, e);
    } 
  }
  
  @Override
  public List<AdFunctionErrorMessagesValueObject> getAdFunctionErrorMessagesData(
      int customerId,
      AdFunctionErrorMessageSearchCriteria searchCriteria) {

    try {
      
      String key = CacheClient.KEY_PREFIX + "ad_function_error_messages_for_customer_id_" 
          + customerId 
          + "_searchCriteria_"
          + searchCriteria;

      List<AdFunctionErrorMessagesValueObject> dtoList = null;
      byte[] value = cacheClient.get(key);
      if (value != null) {
        
        dtoList = AbstractEntity.OBJECT_MAPPER.get().readValue(value, new TypeReference<List<AdFunctionErrorMessagesValueObject>>() {});
        
      } else {
        
        dtoList = this.nodeHierarchyRepository.getAdFunctionErrorMessagesData(customerId, searchCriteria);
        value = AbstractEntity.OBJECT_MAPPER.get().writeValueAsBytes(dtoList);
        cacheClient.set(
            key,
            value,
            CacheClient.ONE_HOUR_TIME_TO_LIVE);
        
      }
      return dtoList;
      
    } catch (Exception e) {
      throw new RuntimeException("Error retrieving ad function error messages data for customerId: " + customerId, e);
    } 
  }  
  
  @Override
  public void storeNodeHierarchyChangeEvent(NodeHierarchyChangeEvent e) {
    nodeHierarchyRepository.storeNodeHierarchyChangeEvent(e);
  }
  
  // FAST LANE WRITER OPERATIONS (I.E. DOES NOT LOAD PORTFOLIO, DOES STRAIGHT INSERT/UPDATE)
  @Override
  public TagInfo getTagInfo(int tagId) {
    return nodeHierarchyRepository.getTagInfo(tagId);
  }
  
  @Override
  public int insertNodeTag(int customerId, int nodeId, int tagId) {
    return nodeHierarchyRepository.insertNodeTag(customerId, nodeId, tagId);
  }   
  
  @Override
  public int deleteNodeTag(int customerId, int nodeId, int tagId) {
    return nodeHierarchyRepository.deleteNodeTag(customerId, nodeId, tagId);
  }   
  
  @Override
  public List<AddNodeDto> insertNodes(int customerId, List<AddNodeDto> dtoList) {
    return nodeHierarchyRepository.insertNodes(customerId, dtoList);
  }

  @Override
  public AddNodeDto insertNode(int customerId, AddNodeDto dto) {
    return nodeHierarchyRepository.insertNode(customerId, dto);
  }
  
  @Override
  public AsyncPoint insertCustomAsyncComputedPoint(AsyncPoint dto) {
    return nodeHierarchyRepository.insertCustomAsyncComputedPoint(dto);
  }

  @Override
  public AsyncPoint updateCustomAsyncComputedPoint(AsyncPoint dto) {
    return nodeHierarchyRepository.updateCustomAsyncComputedPoint(dto);
  }
  
  @Override
  public void updateNodeDisplayName(int customerId, int nodeId, int nodeTypeId, int pointTypeId, String nodeDisplayName) {
    nodeHierarchyRepository.updateNodeDisplayName(customerId, nodeId, nodeTypeId, pointTypeId, nodeDisplayName);
  }
}
//@formatter:on