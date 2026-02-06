package com.djt.hvac.domain.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;

import com.djt.hvac.domain.model.MockModelServiceProvider;
import com.djt.hvac.domain.model.cache.client.MockCacheClient;
import com.djt.hvac.domain.model.common.utils.ObjectMappers;
import com.djt.hvac.domain.model.customer.repository.CustomerRepository;
import com.djt.hvac.domain.model.customer.service.CustomerService;
import com.djt.hvac.domain.model.dictionary.container.DictionaryContext;
import com.djt.hvac.domain.model.dictionary.repository.DictionaryRepository;
import com.djt.hvac.domain.model.dictionary.service.DictionaryService;
import com.djt.hvac.domain.model.distributor.repository.DistributorRepository;
import com.djt.hvac.domain.model.distributor.service.DistributorService;
import com.djt.hvac.domain.model.email.client.EmailClient;
import com.djt.hvac.domain.model.function.dto.AdFunctionErrorMessagesDto;
import com.djt.hvac.domain.model.function.dto.AdFunctionInstanceDto;
import com.djt.hvac.domain.model.geocoding.client.GeocodingClient;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;
import com.djt.hvac.domain.model.nodehierarchy.dto.AsyncComputedPointNodeDto;
import com.djt.hvac.domain.model.nodehierarchy.dto.CustomAsyncComputedPointNodeDto;
import com.djt.hvac.domain.model.nodehierarchy.dto.EnergyExchangeSystemEdgeDto;
import com.djt.hvac.domain.model.nodehierarchy.dto.MappablePointNodeDto;
import com.djt.hvac.domain.model.nodehierarchy.dto.NodeTagDto;
import com.djt.hvac.domain.model.nodehierarchy.dto.NonPointNodeDto;
import com.djt.hvac.domain.model.nodehierarchy.dto.ScheduledAsyncComputedPointNodeDto;
import com.djt.hvac.domain.model.nodehierarchy.event.impl.MockModelChangeEventPublisher;
import com.djt.hvac.domain.model.nodehierarchy.repository.NodeHierarchyRepository;
import com.djt.hvac.domain.model.nodehierarchy.service.NodeHierarchyQueryService;
import com.djt.hvac.domain.model.nodehierarchy.service.NodeHierarchyService;
import com.djt.hvac.domain.model.nodehierarchy.service.async.NodeHierarchyAsyncCommandService;
import com.djt.hvac.domain.model.nodehierarchy.utils.MockModelServiceProviderOptions;
import com.djt.hvac.domain.model.nodehierarchy.utils.NodeHierarchyTestDataBuilder;
import com.djt.hvac.domain.model.notification.repository.NotificationRepository;
import com.djt.hvac.domain.model.notification.service.NotificationService;
import com.djt.hvac.domain.model.rawpoint.RawPointEntity;
import com.djt.hvac.domain.model.rawpoint.dto.RawPointDto;
import com.djt.hvac.domain.model.rawpoint.repository.RawPointRepository;
import com.djt.hvac.domain.model.report.dto.ReportInstanceDto;
import com.djt.hvac.domain.model.report.dto.ReportInstanceStatusDto;
import com.djt.hvac.domain.model.stripe.client.StripeClient;
import com.djt.hvac.domain.model.timeseries.client.TimeSeriesServiceClient;
import com.djt.hvac.domain.model.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class AbstractResoluteDomainModelTest {

  public static final String TEST_DATA_PATH = "src/test/resources/com/resolute/services/domain/model";

  protected static final ObjectMapper MAPPER = ObjectMappers.create();
  protected static final ObjectWriter OBJECT_WRITER_WITH_PRETTY_PRINTER = ObjectMappers.create().writerWithDefaultPrettyPrinter();
  
  protected static final Integer RESOLUTE_DISTRIBUTOR_ID = Integer.valueOf(1);

  protected static final Integer MCLAREN_CUSTOMER_ID = Integer.valueOf(4);
  protected static final Integer REDICO_CUSTOMER_ID = Integer.valueOf(9);
  protected static final Integer USA_HOCKEY_ARENA_CUSTOMER_ID = Integer.valueOf(10);
  protected static final Integer KTB_FLORIDA_SPORTS_ARENA_CUSTOMER_ID = Integer.valueOf(11);
  protected static final Integer DOMINOS_CUSTOMER_ID = Integer.valueOf(12);

  protected Integer distributorId = RESOLUTE_DISTRIBUTOR_ID;
  protected Integer customerId = USA_HOCKEY_ARENA_CUSTOMER_ID;

  protected List<RawPointDto> rawPointDtoList;
  protected List<NonPointNodeDto> nonPointNodeDtoList;
  protected List<MappablePointNodeDto> mappablePointNodeDtoList;
  protected List<CustomAsyncComputedPointNodeDto> customAsyncComputedPointNodeDtoList;
  protected List<ScheduledAsyncComputedPointNodeDto> scheduledAsyncComputedPointNodeDtoList;
  protected List<AsyncComputedPointNodeDto> asyncComputedPointNodeDtoList;
  protected List<NodeTagDto> nodeTagDtoList;
  protected List<EnergyExchangeSystemEdgeDto> energyExchangeSystemEdgeDtoList;
  protected List<ReportInstanceDto> reportInstanceDtoList;
  protected List<ReportInstanceStatusDto> reportInstanceStatusDtoList;
  protected List<AdFunctionInstanceDto> adFunctionInstanceCandidateDtoList;
  protected List<AdFunctionInstanceDto> adFunctionInstanceDtoList;
  protected List<AdFunctionErrorMessagesDto> adFunctionErrorMessagesDtoList;
  
  protected static DictionaryRepository dictionaryRepository;
  protected static UserRepository userRepository;
  protected static DistributorRepository distributorRepository;
  protected static RawPointRepository rawPointRepository;
  protected static CustomerRepository customerRepository;
  protected static NodeHierarchyRepository nodeHierarchyRepository;
  protected static NotificationRepository notificationRepository;
  
  protected static EmailClient emailClient;
  protected static StripeClient stripeClient;
  protected static GeocodingClient geocodingClient;
  protected static TimeSeriesServiceClient timeSeriesServiceClient;
  
  protected static DictionaryService dictionaryService;
  protected static DistributorService distributorService;
  protected static CustomerService customerService;
  protected static NodeHierarchyService nodeHierarchyService;
  protected static NotificationService notificationService;
  protected static NodeHierarchyQueryService nodeHierarchyQueryService;
  
  protected static NodeHierarchyAsyncCommandService nodeHierarchyAsyncCommandService;
  
  protected static MockModelServiceProvider modelServiceProvider;
  
  protected static NodeHierarchyTestDataBuilder nodeHierarchyTestDataBuilder;
  
  @BeforeClass
  public static void beforeClass() throws Exception {
    
    deleteOldDataFiles();
    unzipTestData();
    
    try {
      TimeZone.setDefault(TimeZone.getTimeZone("GMT"));    
      System.err.println("Using default time zone:" + TimeZone.getDefault());
      System.err.println();
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    boolean performAutomaticConfiguration = true;
    
    modelServiceProvider = new MockModelServiceProvider(MockModelServiceProviderOptions
        .builder()
        .withBasePath(TEST_DATA_PATH)
        .withDictionaryRepository(dictionaryRepository)
        .withModelChangeEventPublisher(MockModelChangeEventPublisher.getInstance())
        .withPerformAutomaticConfiguration(performAutomaticConfiguration)
        .build());

    nodeHierarchyTestDataBuilder = new NodeHierarchyTestDataBuilder(modelServiceProvider);
    
    dictionaryRepository = modelServiceProvider.getDictionaryRepository();
    userRepository = modelServiceProvider.getUserRepository();
    distributorRepository = modelServiceProvider.getDistributorRepository();
    rawPointRepository = modelServiceProvider.getRawPointRepository();
    customerRepository = modelServiceProvider.getCustomerRepository();
    nodeHierarchyRepository = modelServiceProvider.getNodeHierarchyRepository();
    notificationRepository = modelServiceProvider.getNotificationRepository();
    
    emailClient = modelServiceProvider.getEmailClient();
    stripeClient = modelServiceProvider.getStripeClient();
    geocodingClient = modelServiceProvider.getGeocodingClient();
    timeSeriesServiceClient = modelServiceProvider.getTimeSeriesServiceClient();
    
    dictionaryService = modelServiceProvider.getDictonaryService();
    distributorService = modelServiceProvider.getDistributorService();
    customerService = modelServiceProvider.getCustomerService();
    nodeHierarchyService = modelServiceProvider.getNodeHierarchyServiceWithCaching();
    notificationService = modelServiceProvider.getNotificationService();
    
    nodeHierarchyQueryService = modelServiceProvider.getNodeHierarchyQueryService();
    
    nodeHierarchyAsyncCommandService = modelServiceProvider.getNodeHierarchyAsyncCommandServiceWithCaching();
    
    nodeHierarchyService.ensureDictionaryDataIsLoaded();
  }

  @Before
  public void before() throws Exception {

    deleteOldDataFiles();
    unzipTestData();
    
    MockModelChangeEventPublisher.PUBLISHED_EVENTS.clear();
    MockCacheClient.getInstance().removeAllCacheEntries();
  }
  
  public static void deleteOldDataFiles() throws IOException {
    
    File[] files = new File(TEST_DATA_PATH).listFiles();
    for (File file : files) {
      if (file.isFile() && file.getName().endsWith(".json")) {
        Files.deleteIfExists(file.toPath());
      }
    }
  }

  public static void unzipTestData() throws IOException {

    unzipTestFile("dictionary-data.zip");
    unzipTestFile("distributor-list.zip");
    unzipTestFile("customer-list.zip");
    unzipTestFile("customer-data.zip");
  }

  public static void validateUsaHockeyAreanaPortfolioNodeCounts(PortfolioEntity portfolio) {

    Assert.assertNotNull("portfolio is null", portfolio);
    Assert.assertEquals("getNumBuildingsProcessed is incorrect", "1", Integer.toString(portfolio.getNumBuildingsProcessed()));
    Assert.assertEquals("getNumSubBuildingsProcessed is incorrect", "0", Integer.toString(portfolio.getNumSubBuildingsProcessed()));
    Assert.assertEquals("getNumFloorsProcessed is incorrect", "1", Integer.toString(portfolio.getNumFloorsProcessed()));
    Assert.assertEquals("getNumEquipmentProcessed is incorrect", "39", Integer.toString(portfolio.getNumEquipmentProcessed()));
    Assert.assertEquals("getNumMappablePointsProcessed is incorrect", "639", Integer.toString(portfolio.getNumMappablePointsProcessed()));
    Assert.assertEquals("getNumCustomAsyncComputedPointsProcessed is incorrect", "90", Integer.toString(portfolio.getNumCustomAsyncComputedPointsProcessed()));
    Assert.assertEquals("getNumScheduledAsyncComputedPointsProcessed is incorrect", "0", Integer.toString(portfolio.getNumScheduledAsyncComputedPointsProcessed()));
    Assert.assertEquals("getNumAsyncComputedPointsProcessed is incorrect", "68", Integer.toString(portfolio.getNumAsyncComputedPointsProcessed()));
    Assert.assertTrue("getNumReportInstancesProcessed wasn't non-zero", portfolio.getNumReportInstancesProcessed() > 0);
    Assert.assertEquals("getNumAdFunctionInstanceCandidatesProcessed is incorrect", "194", Integer.toString(portfolio.getNumAdFunctionInstanceCandidatesProcessed()));
    Assert.assertEquals("getNumAdFunctionInstancesProcessed is incorrect", "66", Integer.toString(portfolio.getNumAdFunctionInstancesProcessed()));
  }

  public static RawPointEntity buildMockRawPoint(int customerId, String metricId) {
    
    return buildMockRawPoint(
        customerId,
        Integer.valueOf(999),
        metricId);     
  }  
  
  public static RawPointEntity buildMockRawPoint(int customerId, int componentId, String metricId) {
    
    return new RawPointEntity(
        null,
        customerId,
        Integer.valueOf(componentId),
        metricId,
        "NumericPoint",
        "",
        "kWh",
        Boolean.FALSE,
        Boolean.FALSE,
        null);     
  }  

  public static void unzipTestFile(String directory, String filename) throws IOException {

    File fileZip = new File(directory + "/" + filename);
    if (!fileZip.exists()) {
      throw new IllegalStateException("Data file not found: [" + fileZip.getAbsolutePath() + "]");
    }
    File destDir = new File(directory);
    byte[] buffer = new byte[1024];
    ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
    ZipEntry zipEntry = zis.getNextEntry();
    while (zipEntry != null) {

      File newFile = newFile(destDir, zipEntry);
      FileOutputStream fos = new FileOutputStream(newFile);
      int len;
      while ((len = zis.read(buffer)) > 0) {
        fos.write(buffer, 0, len);
      }
      fos.close();
      zipEntry = zis.getNextEntry();
    }
    zis.closeEntry();
    zis.close();
  }
  
  public static void unzipTestFile(String filename) throws IOException {

    String fileZip = TEST_DATA_PATH + "/" + filename;
    File destDir = new File(TEST_DATA_PATH);
    byte[] buffer = new byte[1024];
    ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
    ZipEntry zipEntry = zis.getNextEntry();
    while (zipEntry != null) {

      File newFile = newFile(destDir, zipEntry);
      FileOutputStream fos = new FileOutputStream(newFile);
      int len;
      while ((len = zis.read(buffer)) > 0) {
        fos.write(buffer, 0, len);
      }
      fos.close();
      zipEntry = zis.getNextEntry();
    }
    zis.closeEntry();
    zis.close();
  }

  // https://wphosting.tv/how-to-remove-__macosx-from-zip-archives/
  // zip -d dictionary-data.zip "__MACOSX*"
  // zip -d customer-data.zip "__MACOSX*"
  // zip -d distributor-list.zip "__MACOSX*"
  // zip -d customer-list.zip "__MACOSX*"
  public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {

    File destFile = null;
    String filename = zipEntry.getName();
    destFile = new File(destinationDir, filename);

    String destDirPath = destinationDir.getCanonicalPath();
    String destFilePath = destFile.getCanonicalPath();

    if (!destFilePath.startsWith(destDirPath + File.separator)) {
      throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
    }
    return destFile;
  }
  
  public static void clearDictionaryContext() {
    
    DictionaryContext.setTagsContainer(null);
    DictionaryContext.setUnitsContainer(null);
    DictionaryContext.setNodeTagTemplatesContainer(null);
    DictionaryContext.setScheduledEventTypesContainer(null);
    DictionaryContext.setAdFunctionTemplatesContainer(null);
    DictionaryContext.setReportTemplatesContainer(null);
    DictionaryContext.setPaymentPlansContainer(null);
  }
}
