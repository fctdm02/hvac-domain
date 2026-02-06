//@formatter:off
package com.djt.hvac.domain.model.timeseries.client;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.djt.hvac.domain.model.AbstractResoluteDomainModelTest;
import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.customer.AbstractCustomerEntity;
import com.djt.hvac.domain.model.customer.enums.CustomerType;
import com.djt.hvac.domain.model.dictionary.enums.UnitSystem;
import com.djt.hvac.domain.model.distributor.AbstractDistributorEntity;
import com.djt.hvac.domain.model.timeseries.client.MockTimeSeriesServiceClient;
import com.djt.hvac.domain.model.timeseries.client.TimeSeriesServiceClient;

public class MockTimeSeriesServiceClientTest extends AbstractResoluteDomainModelTest {

  private AbstractCustomerEntity customer;
  private final String metricIdSuffix = "metric1";
  private String metricId;
  
  private final Map<String, String> tags = new HashMap<>();
  private final TreeMap<Long, Double> metricValues = new TreeMap<>();
  
  private LocalDate currentLocalDate = AbstractEntity.getTimeKeeper().getCurrentLocalDate();
  private LocalDateTime startLocalDateTime = currentLocalDate.atStartOfDay();
  private Timestamp startTimestamp = Timestamp.valueOf(startLocalDateTime);
  private long millis = startTimestamp.getTime();
  private String metricQueryPrefix = TimeSeriesServiceClient.METRIC_QUERY_AGGREGATOR_SUM + TimeSeriesServiceClient.METRIC_QUERY_DOWNSAMPLER_FIFTEEN_MIN_INTERVAL;
  
  private TimeSeriesServiceClient timeSeriesServiceClient = MockTimeSeriesServiceClient.getInstance();

  @Before
  public void before() throws Exception {
    
    tags.put("tags", "dummy");
    
    metricValues.put(millis -  TimeSeriesServiceClient.NUM_MILLISECONDS_IN_15_MINUTES     , 1.1); // OUT OF BOUNDS
    metricValues.put(millis                                                               , 1.2);
    metricValues.put(millis +  TimeSeriesServiceClient.NUM_MILLISECONDS_IN_15_MINUTES     , 1.3);
    metricValues.put(millis + (TimeSeriesServiceClient.NUM_MILLISECONDS_IN_15_MINUTES * 2), 1.4);
    metricValues.put(millis + (TimeSeriesServiceClient.NUM_MILLISECONDS_IN_15_MINUTES * 3), 1.5); // OUT OF BOUNDS
    
    AbstractDistributorEntity parentDistributor = distributorService.loadDistributor(RESOLUTE_DISTRIBUTOR_ID);
    
    customer = customerService.createCustomer(
        parentDistributor, 
        CustomerType.OUT_OF_BAND,
        "Test Customer Name",
        UnitSystem.IP.toString());
    
    metricId = customer.getUuid() + TimeSeriesServiceClient.METRIC_ID_DELIMITER + metricIdSuffix;
    
    timeSeriesServiceClient.deleteCustomerMetricValues(customer);
  }
  
  @Test
  public void deleteCustomerMetricValues() throws Exception {
    
    // STEP 1: ARRANGE
    boolean result = timeSeriesServiceClient.submitMetricValuesToTsdb(customer, metricId, tags, metricValues);
    
    
    // STEP 2: ACT
    timeSeriesServiceClient.deleteCustomerMetricValues(customer);
    
    
    // STEP 3: ASSERT
    Assert.assertTrue("result is incorrect", result);
    Collection<String> metricIds = Arrays.asList(metricQueryPrefix + metricId);
    Long start = millis;
    Long end = millis + (TimeSeriesServiceClient.NUM_MILLISECONDS_IN_15_MINUTES * 2);
    Map<String, Map<Long, Double>> retrievedAllMetricValues = timeSeriesServiceClient.retrieveMetricValuesFromTsdb(customer, metricIds, start, end);
    Assert.assertNotNull("retrievedAllMetricValues is null", retrievedAllMetricValues);
    Map<Long, Double> retrievedMetricValues = retrievedAllMetricValues.get(metricId);
    Assert.assertNull("retrievedMetricValues is not null", retrievedMetricValues);
  }

  @Test
  public void deletePointMetricValues() throws Exception {
    
    // STEP 1: ARRANGE
    boolean result = timeSeriesServiceClient.submitMetricValuesToTsdb(customer, metricId, tags, metricValues);
    
    
    // STEP 2: ACT
    timeSeriesServiceClient.deletePointMetricValues(customer, metricId);;
    
    
    // STEP 3: ASSERT
    Assert.assertTrue("result is incorrect", result);
    Collection<String> metricIds = Arrays.asList(metricQueryPrefix + metricId);
    Long start = millis;
    Long end = millis + (TimeSeriesServiceClient.NUM_MILLISECONDS_IN_15_MINUTES * 2);
    Map<String, Map<Long, Double>> retrievedAllMetricValues = timeSeriesServiceClient.retrieveMetricValuesFromTsdb(customer, metricIds, start, end);
    Assert.assertNotNull("retrievedAllMetricValues is null", retrievedAllMetricValues);
    Map<Long, Double> retrievedMetricValues = retrievedAllMetricValues.get(metricId);
    Assert.assertNull("retrievedMetricValues is not null", retrievedMetricValues);
  }
  
  @Test
  public void retrieveMetricValuesFromTsdb() throws Exception {
    
    // STEP 1: ARRANGE
    boolean result = timeSeriesServiceClient.submitMetricValuesToTsdb(customer, metricId, tags, metricValues);
    Assert.assertTrue("result is incorrect", result);
    
    Collection<String> metricIds = Arrays.asList(metricQueryPrefix + metricId);
    Long start = millis;
    Long end = millis + (TimeSeriesServiceClient.NUM_MILLISECONDS_IN_15_MINUTES * 2);
    
    
    // STEP 2: ACT
    Map<String, Map<Long, Double>> retrievedAllMetricValues = timeSeriesServiceClient.retrieveMetricValuesFromTsdb(customer, metricIds, start, end);
    
    
    // STEP 3: ASSERT
    Assert.assertNotNull("retrievedAllMetricValues is null", retrievedAllMetricValues);
    Map<Long, Double> retrievedMetricValues = retrievedAllMetricValues.get(metricId);
    Assert.assertNotNull("retrievedMetricValues is null", retrievedMetricValues);
    Assert.assertEquals("retrievedMetricValues size is incorrect", Integer.toString(metricValues.size()-2), Integer.toString(retrievedMetricValues.size()));
  }
  
  @Test
  public void submitMetricValuesToTsdb() throws Exception {
    
    // STEP 1: ARRANGE

    
    // STEP 2: ACT
    boolean result = timeSeriesServiceClient.submitMetricValuesToTsdb(customer, metricId, tags, metricValues);
    
    
    // STEP 3: ASSERT
    Assert.assertTrue("result is incorrect", result);
    Collection<String> metricIds = Arrays.asList(metricQueryPrefix + metricId);
    Long start = millis;
    Long end = millis + (TimeSeriesServiceClient.NUM_MILLISECONDS_IN_15_MINUTES * 2);
    Map<String, Map<Long, Double>> retrievedAllMetricValues = timeSeriesServiceClient.retrieveMetricValuesFromTsdb(customer, metricIds, start, end);
    Assert.assertNotNull("retrievedAllMetricValues is null", retrievedAllMetricValues);
    Map<Long, Double> retrievedMetricValues = retrievedAllMetricValues.get(metricId);
    Assert.assertNotNull("retrievedMetricValues is null", retrievedMetricValues);
    Assert.assertEquals("retrievedMetricValues size is incorrect", Integer.toString(metricValues.size() - 2), Integer.toString(retrievedMetricValues.size()));
  }

  @Test
  public void submitMetricValuesBatchToTsdb() throws Exception {
    
    // STEP 1: ARRANGE
    Map<String, Map<Long, Double>> allMetricValues = new TreeMap<>();
    allMetricValues.put(metricId, metricValues);
    
    
    // STEP 2: ACT
    boolean result = timeSeriesServiceClient.submitMetricValuesBatchToTsdb(customer, tags, allMetricValues);
    
    
    // STEP 3: ASSERT
    Assert.assertTrue("result is incorrect", result);
    Collection<String> metricIds = Arrays.asList(metricQueryPrefix + metricId);
    Long start = millis;
    Long end = millis + (TimeSeriesServiceClient.NUM_MILLISECONDS_IN_15_MINUTES * 2);
    Map<String, Map<Long, Double>> retrievedAllMetricValues = timeSeriesServiceClient.retrieveMetricValuesFromTsdb(customer, metricIds, start, end);
    Assert.assertNotNull("retrievedAllMetricValues is null", retrievedAllMetricValues);
    Map<Long, Double> retrievedMetricValues = retrievedAllMetricValues.get(metricId);
    Assert.assertNotNull("retrievedMetricValues is null", retrievedMetricValues);
    Assert.assertEquals("retrievedMetricValues size is incorrect", Integer.toString(metricValues.size() - 2), Integer.toString(retrievedMetricValues.size()));
  }
  
  @Test
  public void submitMetricValuesToKafka() throws Exception {
    
    // STEP 1: ARRANGE
    String jobId = "JOB_ID";
    
    // STEP 2: ACT
    boolean result = timeSeriesServiceClient.submitMetricValuesToKafka(customer, jobId, metricId, tags, metricValues);
    
    
    // STEP 3: ASSERT
    Assert.assertTrue("result is incorrect", result);
  }  
}