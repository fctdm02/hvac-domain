//@formatter:off
package com.djt.hvac.domain.model;

import com.djt.hvac.domain.model.cache.client.CacheClient;
import com.djt.hvac.domain.model.customer.service.CustomerService;
import com.djt.hvac.domain.model.dictionary.service.DictionaryService;
import com.djt.hvac.domain.model.distributor.service.DistributorHierarchyStateEvaluator;
import com.djt.hvac.domain.model.distributor.service.DistributorService;
import com.djt.hvac.domain.model.email.client.EmailClient;
import com.djt.hvac.domain.model.geocoding.client.GeocodingClient;
import com.djt.hvac.domain.model.nodehierarchy.service.NodeHierarchyQueryService;
import com.djt.hvac.domain.model.nodehierarchy.service.NodeHierarchyService;
import com.djt.hvac.domain.model.nodehierarchy.service.async.NodeHierarchyAsyncCommandService;
import com.djt.hvac.domain.model.notification.service.NotificationService;
import com.djt.hvac.domain.model.stripe.client.StripeClient;
import com.djt.hvac.domain.model.timeseries.client.TimeSeriesServiceClient;

/**
 * 
 * This is the entry point into the domain layer.  
 * Every interaction should must occur with one of
 * the service interfaces below
 * 
 * @author tommyers
 *
 */
public interface ModelServiceProvider {
  
  /**
   * 
   * @return DictionaryService
   */
  DictionaryService getDictonaryService();
  
  /**
   * 
   * @return DistributorService
   */
  DistributorService getDistributorService();
  
  /**
   * 
   * @return CustomerService
   */
  CustomerService getCustomerService();
  
  /**
   * 
   * @return NodeHierarchyService
   */
  NodeHierarchyService getNodeHierarchyService();
    
  /**
   * 
   * @return NotificationService
   */
  NotificationService getNotificationService();
  
  /**
   * This service interface wraps the node hierarchy service
   * interface, and incorporates a lock repository and 
   * async operation client in order to have a producer/consumer
   * around node hierarchy service commands.
   * 
   * @return NodeHierarchyAsyncCommandService
   */
  NodeHierarchyAsyncCommandService getNodeHierarchyAsyncCommandService();
  
  /**
   * This service wraps all the individual query DAOs that are used to 
   * implement the methods in the interface
   * 
   * @return NodeHierarchyQueryService
   */
  NodeHierarchyQueryService getNodeHierarchyQueryService();
  
  /**
   * 
   * @return This component is responsible for payment processing and all 
   * state transitions (e.g. payment status from ACTIVE to DELINQUENT) for 
   * billable buildings up to their parent online customer up to their 
   * online distributor hierarchy (up to the Root Resolute distributor)
   */
  DistributorHierarchyStateEvaluator getDistributorHierarchyStateEvaluator();

  /**
   * 
   * @return EmailClient
   */
  EmailClient getEmailClient();
  
  /**
   * 
   * @return StripeClient
   */
  StripeClient getStripeClient();
  
  /**
   * 
   * @return GeocodingClient
   */
  GeocodingClient getGeocodingClient();
  
  /**
   * 
   * @return TimeSeriesServiceClient
   */
  TimeSeriesServiceClient getTimeSeriesServiceClient();
  
  /**
   * 
   * @return CacheClient
   */
  CacheClient getCacheClient();
  
  /**
   * 
   * @return NodeHierarchyService that is injected with a caching node hierarchy repository impl.
   */
  NodeHierarchyService getNodeHierarchyServiceWithCaching();

  /**
   * 
   * @return NodeHierarchyAsyncCommandService that is injected with a caching node hierarchy repository impl.
   */
  NodeHierarchyAsyncCommandService getNodeHierarchyAsyncCommandServiceWithCaching();
}
//@formatter:on