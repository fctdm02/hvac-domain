//@formatter:off
package com.djt.hvac.domain.model;

import com.djt.hvac.domain.model.cache.client.MockCacheClient;
import com.djt.hvac.domain.model.customer.repository.CustomerRepository;
import com.djt.hvac.domain.model.customer.repository.CustomerRepositoryFileSystemImpl;
import com.djt.hvac.domain.model.customer.service.CustomerServiceImpl;
import com.djt.hvac.domain.model.dictionary.repository.DictionaryRepository;
import com.djt.hvac.domain.model.dictionary.repository.DictionaryRepositoryFileSystemImpl;
import com.djt.hvac.domain.model.dictionary.service.DictionaryServiceImpl;
import com.djt.hvac.domain.model.distributor.repository.DistributorRepository;
import com.djt.hvac.domain.model.distributor.repository.DistributorRepositoryFileSystemImpl;
import com.djt.hvac.domain.model.distributor.service.DistributorHierarchyStateEvaluator;
import com.djt.hvac.domain.model.distributor.service.DistributorServiceImpl;
import com.djt.hvac.domain.model.email.client.MockEmailClient;
import com.djt.hvac.domain.model.function.query.AdFunctionInstanceQueryDaoFileSystemImpl;
import com.djt.hvac.domain.model.function.query.AdFunctionTemplateQueryDaoFileSystemImpl;
import com.djt.hvac.domain.model.geocoding.client.MockGeocodingClient;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.query.EnergyExchangeQueryDaoFileSystemImpl;
import com.djt.hvac.domain.model.nodehierarchy.event.ModelChangeEventPublisher;
import com.djt.hvac.domain.model.nodehierarchy.event.impl.MockModelChangeEventPublisher;
import com.djt.hvac.domain.model.nodehierarchy.point.query.PointQueryDaoFileSystemImpl;
import com.djt.hvac.domain.model.nodehierarchy.repository.NodeHierarchyRepository;
import com.djt.hvac.domain.model.nodehierarchy.repository.NodeHierarchyRepositoryFileSystemCachingImpl;
import com.djt.hvac.domain.model.nodehierarchy.repository.NodeHierarchyRepositoryFileSystemImpl;
import com.djt.hvac.domain.model.nodehierarchy.service.NodeHierarchyQueryServiceImpl;
import com.djt.hvac.domain.model.nodehierarchy.service.NodeHierarchyServiceImpl;
import com.djt.hvac.domain.model.nodehierarchy.service.async.NodeHierarchyAsyncCommandServiceImpl;
import com.djt.hvac.domain.model.nodehierarchy.service.async.lock.repository.AsyncOperationLockRepositoryFileSystemImpl;
import com.djt.hvac.domain.model.nodehierarchy.service.async.operation.repository.AsyncOperationRepository;
import com.djt.hvac.domain.model.nodehierarchy.service.async.operation.repository.AsyncOperationRepositoryFileSystemImpl;
import com.djt.hvac.domain.model.nodehierarchy.utils.MockModelServiceProviderOptions;
import com.djt.hvac.domain.model.notification.repository.NotificationRepository;
import com.djt.hvac.domain.model.notification.repository.NotificationRepositoryFileSystemImpl;
import com.djt.hvac.domain.model.notification.service.NotificationServiceImpl;
import com.djt.hvac.domain.model.rawpoint.query.RawPointQueryDaoFileSystemImpl;
import com.djt.hvac.domain.model.rawpoint.repository.RawPointRepository;
import com.djt.hvac.domain.model.rawpoint.repository.RawPointRepositoryFileSystemImpl;
import com.djt.hvac.domain.model.report.query.ReportQueryDaoFileSystemImpl;
import com.djt.hvac.domain.model.stripe.client.MockStripeClient;
import com.djt.hvac.domain.model.timeseries.client.MockTimeSeriesServiceClient;
import com.djt.hvac.domain.model.user.repository.UserRepository;
import com.djt.hvac.domain.model.user.repository.UserRepositoryFileSystemImpl;

public class MockModelServiceProvider extends AbstractModelServiceProvider {
  
  private final String basePath;

  public MockModelServiceProvider(MockModelServiceProviderOptions mockModelServiceProviderOptions) {
    
    String bp = mockModelServiceProviderOptions.getBasePath();
    DictionaryRepository dr = mockModelServiceProviderOptions.getDictionaryRepository();
    ModelChangeEventPublisher ep = mockModelServiceProviderOptions.getModelChangeEventPublisher();
    boolean performAutomaticConfiguration = mockModelServiceProviderOptions.getPerformAutomaticConfiguration();    

    if (ep != null) {
      eventPublisher = ep;
    } else {
      eventPublisher = MockModelChangeEventPublisher.getInstance();
    }
    
    if (bp != null) {
      basePath = bp;
    } else {
      basePath = System.getProperty("user.home") + "/";      
    }

    if (dr != null) {
      dictionaryRepository = dr;
    } else {
      DictionaryRepositoryFileSystemImpl.setPrettyPrint(true);
      dictionaryRepository = new DictionaryRepositoryFileSystemImpl(
          basePath);
    }
        
    DistributorRepositoryFileSystemImpl.setPrettyPrint(true);
    distributorRepository = new DistributorRepositoryFileSystemImpl(
        basePath,
        userRepository);

    RawPointRepositoryFileSystemImpl.setPrettyPrint(true);
    rawPointRepository = new RawPointRepositoryFileSystemImpl(
        basePath);
    
    CustomerRepositoryFileSystemImpl.setPrettyPrint(true);
    customerRepository = new CustomerRepositoryFileSystemImpl(
        basePath,
        rawPointRepository,
        distributorRepository);
    
    UserRepositoryFileSystemImpl.setPrettyPrint(true);
    userRepository = new UserRepositoryFileSystemImpl(
        basePath,
        customerRepository);
    
    NodeHierarchyRepositoryFileSystemImpl.setPrettyPrint(true);
    nodeHierarchyRepository = new NodeHierarchyRepositoryFileSystemImpl(
        basePath,
        rawPointRepository,
        customerRepository,
        dictionaryRepository);

    NotificationRepositoryFileSystemImpl.setPrettyPrint(true);
    notificationRepository = new NotificationRepositoryFileSystemImpl(
        basePath,
        distributorRepository,
        userRepository);
    
    stripeClient = MockStripeClient.getInstance();
    
    geocodingClient = MockGeocodingClient.getInstance();
    
    timeSeriesServiceClient = MockTimeSeriesServiceClient.getInstance();
    
    cacheClient = MockCacheClient.getInstance();
    
    emailClient = MockEmailClient.getInstance();

    adFunctionTemplateQueryDao = new AdFunctionTemplateQueryDaoFileSystemImpl();
    reportQueryDao = new ReportQueryDaoFileSystemImpl();
    dictionaryService = new DictionaryServiceImpl(
        dictionaryRepository,
        adFunctionTemplateQueryDao,
        reportQueryDao);
    
    distributorService = new DistributorServiceImpl(
        userRepository,
        customerRepository,
        distributorRepository,
        stripeClient);
    
    customerService = new CustomerServiceImpl(
        customerRepository,
        rawPointRepository);
    
    notificationService = new NotificationServiceImpl(
        distributorRepository,
        customerRepository,
        userRepository,
        notificationRepository,
        emailClient);
    
    nodeHierarchyService = new NodeHierarchyServiceImpl(
        distributorRepository,
        rawPointRepository,
        customerRepository,
        nodeHierarchyRepository,
        dictionaryRepository,
        stripeClient,
        geocodingClient,
        cacheClient,
        timeSeriesServiceClient,
        notificationService,
        eventPublisher,
        performAutomaticConfiguration);

    asyncOperationLockRepository = new AsyncOperationLockRepositoryFileSystemImpl(
        basePath);
    
    asyncOperationRepository = new AsyncOperationRepositoryFileSystemImpl(
        basePath,
        asyncOperationLockRepository,
        nodeHierarchyService);

    distributorHierarchyStateEvaluator = new DistributorHierarchyStateEvaluator(
        distributorService,
        customerService,
        nodeHierarchyService,
        emailClient);
    
    nodeHierarchyAsyncCommandService = new NodeHierarchyAsyncCommandServiceImpl(
        asyncOperationRepository,
        asyncOperationLockRepository);
    
    adFunctionInstanceQueryDao = new AdFunctionInstanceQueryDaoFileSystemImpl(nodeHierarchyRepository);
    energyExchangeQueryDao = new EnergyExchangeQueryDaoFileSystemImpl(nodeHierarchyRepository);
    pointQueryDao = new PointQueryDaoFileSystemImpl(nodeHierarchyRepository);
    rawPointQueryDao = new RawPointQueryDaoFileSystemImpl(rawPointRepository, nodeHierarchyRepository);
    
    nodeHierarchyQueryService = new NodeHierarchyQueryServiceImpl(
        adFunctionInstanceQueryDao,
        energyExchangeQueryDao,
        pointQueryDao,
        rawPointQueryDao);
    
    NodeHierarchyRepositoryFileSystemCachingImpl nodeHierarchyRepositoryFileSystemCachingImpl = new NodeHierarchyRepositoryFileSystemCachingImpl(
        nodeHierarchyRepository,
        cacheClient,
        rawPointRepository,
        customerRepository,
        dictionaryRepository);
    
    nodeHierarchyServiceWithCaching = new NodeHierarchyServiceImpl(
        distributorRepository,
        rawPointRepository,
        customerRepository,
        nodeHierarchyRepositoryFileSystemCachingImpl,
        dictionaryRepository,
        stripeClient,
        geocodingClient,
        cacheClient,
        timeSeriesServiceClient,
        notificationService,
        eventPublisher,
        Boolean.FALSE);     
    
    AsyncOperationRepository asyncOperationRepositoryWithCaching = new AsyncOperationRepositoryFileSystemImpl(
        basePath,
        asyncOperationLockRepository,
        nodeHierarchyServiceWithCaching);

    nodeHierarchyAsyncCommandServiceWithCaching = new NodeHierarchyAsyncCommandServiceImpl(
        asyncOperationRepositoryWithCaching,
        asyncOperationLockRepository);
    
    dictionaryService.ensureDictionaryDataIsLoaded();
  }
  
  public DictionaryRepository getDictionaryRepository() {
    return dictionaryRepository;
  }

  public UserRepository getUserRepository() {
    return userRepository;
  }

  public DistributorRepository getDistributorRepository() {
    return distributorRepository;
  }

  public RawPointRepository getRawPointRepository() {
    return rawPointRepository;
  }

  public CustomerRepository getCustomerRepository() {
    return customerRepository;
  }

  public NodeHierarchyRepository getNodeHierarchyRepository() {
    return nodeHierarchyRepository;
  }
  
  public NotificationRepository getNotificationRepository() {
    return notificationRepository;
  }
}
//@formatter:on