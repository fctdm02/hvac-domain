//@formatter:off
package com.djt.hvac.domain.model.timeseries.client;

import java.util.Collection;
import java.util.Map;

import com.djt.hvac.domain.model.customer.AbstractCustomerEntity;
import com.djt.hvac.domain.model.timeseries.exception.TimeSeriesClientException;

public abstract class AbstractTimeSeriesServiceClient implements TimeSeriesServiceClient {

  @Override
  public  Map<String, Map<Long, Double>> retrieveMetricValuesFromTsdb(
      AbstractCustomerEntity customer,
      Collection<String> metricIds,
      Long start,
      Long end)
  throws 
      TimeSeriesClientException {
        
    return retrieveMetricValuesFromTsdb(
        customer.getPersistentIdentity(), 
        validateMetricIds(true, customer, metricIds),
        start, 
        end);
  }
  
  @Override
  public boolean submitMetricValuesToTsdb(
      AbstractCustomerEntity customer,
      String metricId,
      Map<String, String> tags,
      Map<Long, Double> metricValues)
  throws 
      TimeSeriesClientException {
    
    return submitMetricValuesToTsdb(
        customer.getPersistentIdentity(), 
        validateMetricIds(false, customer, metricId), 
        tags, 
        metricValues);
  }
  
  @Override
  public boolean submitMetricValuesBatchToTsdb(
      AbstractCustomerEntity customer,
      Map<String, String> tags,
      Map<String, Map<Long, Double>> incomingMetricValues)
  throws 
      TimeSeriesClientException {
    
    validateMetricIds(false, customer, incomingMetricValues.keySet());

    return submitMetricValuesBatchToTsdb(
        customer.getPersistentIdentity(), 
        tags, 
        incomingMetricValues);
  }
  
  @Override
  public void deleteCustomerMetricValues(
      AbstractCustomerEntity customer)
  throws 
      TimeSeriesClientException {
    
    deleteCustomerMetricValues(
        customer.getPersistentIdentity(),
        customer.getUuid());
  }
  
  @Override
  public void deletePointMetricValues(
      AbstractCustomerEntity customer,
      String metricId)
  throws 
      TimeSeriesClientException {
    
    deleteCustomerMetricValues(
        customer.getPersistentIdentity(),
        customer.getUuid());
  }
  
  @Override
  public  Map<String, Map<Long, Double>> retrieveMetricValuesFromKafka(
      AbstractCustomerEntity customer,
      Collection<String> metricIds,
      Long start,
      Long end)
  throws 
      TimeSeriesClientException {
    
    return retrieveMetricValuesFromKafka(
        customer.getPersistentIdentity(), 
        validateMetricIds(false, customer, metricIds), 
        start, 
        end);
  }
  
  @Override
  public boolean submitMetricValuesToKafka(
      AbstractCustomerEntity customer,
      String jobId,
      String metricId,
      Map<String, String> tags,
      Map<Long, Double> metricValues)
  throws 
      TimeSeriesClientException {
    
    return submitMetricValuesToKafka(
        customer.getPersistentIdentity(), 
        jobId, 
        validateMetricIds(false, customer, metricId), 
        tags, 
        metricValues);
  }
  
  protected abstract Map<String, Map<Long, Double>> retrieveMetricValuesFromTsdb(
      Integer customerId,
      Collection<String> metricIds,
      Long start,
      Long end)
  throws 
      TimeSeriesClientException;
  
  protected abstract boolean submitMetricValuesToTsdb(
      Integer customerId,
      String metricId,
      Map<String, String> tags,
      Map<Long, Double> metricValues)
  throws 
      TimeSeriesClientException;
  
  protected abstract boolean submitMetricValuesBatchToTsdb(
      Integer customerId,
      Map<String, String> tags,
      Map<String, Map<Long, Double>> incomingMetricValues)
  throws 
      TimeSeriesClientException;
  
  protected abstract void deleteCustomerMetricValues(
      Integer customerId,
      String customerUuid)
  throws 
      TimeSeriesClientException;  

  protected abstract void deletePointMetricValues(
      Integer customerId,
      String metricId)
  throws 
      TimeSeriesClientException;  
  
  protected abstract Map<String, Map<Long, Double>> retrieveMetricValuesFromKafka(
      Integer customerId,
      Collection<String> metricIds,
      Long start,
      Long end)
  throws 
      TimeSeriesClientException;
  
  protected abstract boolean submitMetricValuesToKafka(
      Integer customerId,
      String jobId,
      String metricId,
      Map<String, String> tags,
      Map<Long, Double> metricValues)
  throws 
      TimeSeriesClientException;
  
  private Collection<String> validateMetricIds(
      boolean isQuery,
      AbstractCustomerEntity customer,
      Collection<String> metricIds) {
    
    String expectedPrefix = customer.getUuid() + TimeSeriesServiceClient.METRIC_ID_DELIMITER;
    for (String metricId: metricIds) {
      validateMetricIds(isQuery, expectedPrefix, metricId);
    }
    return metricIds;
  }

  private String validateMetricIds(
      boolean isQuery,
      AbstractCustomerEntity customer,
      String metricId) {
    
    String expectedPrefix = customer.getUuid() + TimeSeriesServiceClient.METRIC_ID_DELIMITER;
    validateMetricIds(isQuery, expectedPrefix, metricId);
    return metricId;
  }
  
  private void validateMetricIds(
      boolean isQuery,
      String expectedPrefix,
      String metricId) {
    
    if (!metricId.contains(expectedPrefix)) {
      
      throw new IllegalArgumentException("Invalid metricId encountered: ["
          + metricId
          +"], expected customer UUID and metric delimiter prefix of: ["
          + expectedPrefix);
    }
    
    if (isQuery && !metricId.startsWith(METRIC_QUERY_AGGREGATOR_SUM) && !(metricId.contains(METRIC_QUERY_DOWNSAMPLER_FIFTEEN_MIN_INTERVAL) || metricId.contains(METRIC_QUERY_DOWNSAMPLER_ONE_DAY_INTERVAL) || metricId.contains(METRIC_QUERY_DOWNSAMPLER_ONE_MONTH_INTERVAL))) {
      
      throw new IllegalArgumentException("Invalid query prefix metricId encountered: ["
          + metricId
          +"], expected [sum:] for aggregator and one of: [15mc-sum:, 1dc-sum:] for downsampling");
    }
    
  }  
}
//@formatter:on