//@formatter:off
package com.djt.hvac.domain.model;

import com.djt.hvac.domain.model.cache.client.CacheClient;
import com.djt.hvac.domain.model.common.timekeeper.impl.TestTimeKeeperImpl;
import com.djt.hvac.domain.model.customer.repository.CustomerRepository;
import com.djt.hvac.domain.model.customer.service.CustomerService;
import com.djt.hvac.domain.model.dictionary.repository.DictionaryRepository;
import com.djt.hvac.domain.model.dictionary.service.DictionaryService;
import com.djt.hvac.domain.model.distributor.repository.DistributorRepository;
import com.djt.hvac.domain.model.distributor.service.DistributorHierarchyStateEvaluator;
import com.djt.hvac.domain.model.distributor.service.DistributorService;
import com.djt.hvac.domain.model.email.client.EmailClient;
import com.djt.hvac.domain.model.function.query.AdFunctionInstanceQueryDao;
import com.djt.hvac.domain.model.function.query.AdFunctionTemplateQueryDao;
import com.djt.hvac.domain.model.geocoding.client.GeocodingClient;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.query.EnergyExchangeQueryDao;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.query.model.EnergyExchangeQueryResponse;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.query.model.EnergyExchangeSearchCriteria;
import com.djt.hvac.domain.model.nodehierarchy.event.ModelChangeEventPublisher;
import com.djt.hvac.domain.model.nodehierarchy.point.query.PointQueryDao;
import com.djt.hvac.domain.model.nodehierarchy.point.query.model.PointQueryResponse;
import com.djt.hvac.domain.model.nodehierarchy.point.query.model.PointSearchCriteria;
import com.djt.hvac.domain.model.nodehierarchy.repository.NodeHierarchyRepository;
import com.djt.hvac.domain.model.nodehierarchy.service.NodeHierarchyQueryService;
import com.djt.hvac.domain.model.nodehierarchy.service.NodeHierarchyService;
import com.djt.hvac.domain.model.nodehierarchy.service.async.NodeHierarchyAsyncCommandService;
import com.djt.hvac.domain.model.nodehierarchy.service.async.lock.repository.AsyncOperationLockRepository;
import com.djt.hvac.domain.model.nodehierarchy.service.async.operation.repository.AsyncOperationRepository;
import com.djt.hvac.domain.model.notification.repository.NotificationRepository;
import com.djt.hvac.domain.model.notification.service.NotificationService;
import com.djt.hvac.domain.model.rawpoint.query.RawPointQueryDao;
import com.djt.hvac.domain.model.rawpoint.query.model.RawPointQueryResponse;
import com.djt.hvac.domain.model.rawpoint.query.model.RawPointSearchCriteria;
import com.djt.hvac.domain.model.rawpoint.repository.RawPointRepository;
import com.djt.hvac.domain.model.report.query.ReportQueryDao;
import com.djt.hvac.domain.model.stripe.client.StripeClient;
import com.djt.hvac.domain.model.timeseries.client.TimeSeriesServiceClient;
import com.djt.hvac.domain.model.user.repository.UserRepository;

public abstract class AbstractModelServiceProvider implements ModelServiceProvider {
  
  protected DictionaryRepository dictionaryRepository;
  protected UserRepository userRepository;
  protected DistributorRepository distributorRepository;
  protected RawPointRepository rawPointRepository;
  protected CustomerRepository customerRepository;
  protected NodeHierarchyRepository nodeHierarchyRepository;
  protected NotificationRepository notificationRepository;
  
  protected EmailClient emailClient;
  protected StripeClient stripeClient;
  protected GeocodingClient geocodingClient;
  protected TimeSeriesServiceClient timeSeriesServiceClient;
  protected CacheClient cacheClient;
  
  protected ModelChangeEventPublisher eventPublisher;
  
  protected AdFunctionInstanceQueryDao adFunctionInstanceQueryDao;
  protected EnergyExchangeQueryDao<EnergyExchangeSearchCriteria, EnergyExchangeQueryResponse> energyExchangeQueryDao;
  protected PointQueryDao<PointSearchCriteria, PointQueryResponse> pointQueryDao;
  protected RawPointQueryDao<RawPointSearchCriteria, RawPointQueryResponse> rawPointQueryDao;
  protected ReportQueryDao reportQueryDao;
  protected AdFunctionTemplateQueryDao adFunctionTemplateQueryDao;
  
  protected DictionaryService dictionaryService;
  protected DistributorService distributorService;
  protected CustomerService customerService;
  protected NodeHierarchyService nodeHierarchyService;
  protected NodeHierarchyService nodeHierarchyServiceWithCaching;
  protected NotificationService notificationService;
  
  protected AsyncOperationRepository asyncOperationRepository;
  protected AsyncOperationLockRepository asyncOperationLockRepository;
  protected NodeHierarchyAsyncCommandService nodeHierarchyAsyncCommandService;
  protected NodeHierarchyAsyncCommandService nodeHierarchyAsyncCommandServiceWithCaching;
  
  protected NodeHierarchyQueryService nodeHierarchyQueryService;
  
  protected DistributorHierarchyStateEvaluator distributorHierarchyStateEvaluator;
  
  // Used for test mode
  protected TestTimeKeeperImpl testTimeKeeper;
  protected DistributorHierarchyStateEvaluator testDistributorHierarchyStateEvaluator;

  @Override
  public DictionaryService getDictonaryService() {
    return dictionaryService;
  }
  
  @Override
  public DistributorService getDistributorService() {
    return distributorService;
  }
  
  @Override
  public CustomerService getCustomerService() {
    return customerService;
  }

  @Override
  public NodeHierarchyService getNodeHierarchyService() {
    return nodeHierarchyService;
  }
  
  @Override
  public NotificationService getNotificationService() {
    return notificationService;
  }
  
  @Override 
  public NodeHierarchyAsyncCommandService getNodeHierarchyAsyncCommandService() {
    return nodeHierarchyAsyncCommandService;
  }
  
  @Override 
  public NodeHierarchyQueryService getNodeHierarchyQueryService() {
    return nodeHierarchyQueryService;
  }
  
  @Override
  public DistributorHierarchyStateEvaluator getDistributorHierarchyStateEvaluator() {
    return distributorHierarchyStateEvaluator;
  }
    
  @Override
  public EmailClient getEmailClient() {
    return emailClient;
  }
  
  @Override
  public StripeClient getStripeClient() {
    return stripeClient;
  }
  
  @Override
  public GeocodingClient getGeocodingClient() {
    return geocodingClient;
  }

  @Override
  public TimeSeriesServiceClient getTimeSeriesServiceClient() {
    return timeSeriesServiceClient;
  }
  
  @Override
  public CacheClient getCacheClient() {
    return cacheClient;
  }
  
  @Override
  public NodeHierarchyService getNodeHierarchyServiceWithCaching() {
    return nodeHierarchyServiceWithCaching;
  }
  
  @Override
  public NodeHierarchyAsyncCommandService getNodeHierarchyAsyncCommandServiceWithCaching() {
   return nodeHierarchyAsyncCommandServiceWithCaching; 
  }
}
//@formatter:on