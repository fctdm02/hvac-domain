//@formatter:off
package com.djt.hvac.domain.model.timeseries.client;

import java.util.Collection;
import java.util.Map;

import com.djt.hvac.domain.model.customer.AbstractCustomerEntity;
import com.djt.hvac.domain.model.timeseries.exception.TimeSeriesClientException;

/**
 * 
 * @author tommyers
 *
 */
public interface TimeSeriesServiceClient {

  long NUM_MILLISECONDS_IN_15_MINUTES = 15 * 60000;
  long NUM_MILLISECONDS_IN_1_DAY = 86400000L;
  long NUM_MILLISECONDS_IN_90_DAYS = 90 * 86400000L;
  long NUM_MILLISECONDS_IN_9_MONTHS = 9 * 30 * 86400000L;
  
  int NUM_SECONDS_IN_15_MINUTES = 60 * 15;
  int NUM_SECONDS_IN_1_DAY   = 60 * 60 * 24;
  
  String METRIC_ID_DELIMITER = ".";
  String TIMEZONE_TAG = "tz";
  String METRIC_QUERY_AGGREGATOR_SUM = "sum:";
  String METRIC_QUERY_DOWNSAMPLER_FIFTEEN_MIN_INTERVAL = "15mc-avg:";
  String METRIC_QUERY_DOWNSAMPLER_ONE_DAY_INTERVAL = "1dc-avg:";
  String METRIC_QUERY_DOWNSAMPLER_ONE_MONTH_INTERVAL = "1nc-avg:";
  
  int FIFTEEN_MINUTE_INTERVAL_NUM_DAYS_TO_PROCESS_AT_A_TIME = 30;
  int DAILY_INTERVAL_NUM_DAYS_TO_PROCESS_AT_A_TIME = 180;
  long MONTHLY_INTERVAL_NUM_MONTHS_TO_PROCESS_AT_A_TIME = 48;
  
  int NUM_METRICS_FOR_TSDB_BATCH = 2400;
  
  String TAG = "tag";
  String DUMMY = "dummy";
  
  /**
   * 
   * @param customer The owning customer
   * @param metricIds The list of metricIds to retrieve metric values for
   * @param start The start date to retrieve metric values for
   * @param end The end date to retrieve metric values for
   * 
   * @return The metric values for the given points from start to end
   * 
   * @throws TimeSeriesClientException If there was a problem retrieving the time series metric values
   */
  Map<String, Map<Long, Double>> retrieveMetricValuesFromTsdb(
      AbstractCustomerEntity customer,
      Collection<String> metricIds,
      Long start,
      Long end)
  throws 
      TimeSeriesClientException;

  /**
   * 
   * @param customer The owning customer
   * @param metricId The point's metricId
   * @param tags The tags to submit with the upsert
   * @param metricValues The time series metric values to store
   * 
   * @return <code>true</code> if the store was successful; <code>false</code> otherwise.
   * 
   * @throws TimeSeriesClientException If there was a problem storing the time series metric values
   */
  boolean submitMetricValuesToTsdb(
      AbstractCustomerEntity customer,
      String metricId,
      Map<String, String> tags,
      Map<Long, Double> metricValues)
  throws 
      TimeSeriesClientException;

  /**
   * 
   * @param customer The owning customer
   * @param tags The tags to submit with the upsert
   * @param metricValues The time series metric values to store, keyed by metricId
   * 
   * @return <code>true</code> if the store was successful; <code>false</code> otherwise.
   * 
   * @throws TimeSeriesClientException If there was a problem storing the time series metric values
   */
  boolean submitMetricValuesBatchToTsdb(
      AbstractCustomerEntity customer,
      Map<String, String> tags,
      Map<String, Map<Long, Double>> metricValues)
  throws 
      TimeSeriesClientException;

  /**
   * 
   * @param customer The owning customer to delete all values for
   * 
   * @throws TimeSeriesClientException If there was a problem deleting the time series metric values
   */
  void deleteCustomerMetricValues(
      AbstractCustomerEntity customer)
  throws 
      TimeSeriesClientException;

  /**
   * 
   * @param customer The owning customer
   * @param metricId The point metricId to delete all values for
   * @throws TimeSeriesClientException
   */
  void deletePointMetricValues(
      AbstractCustomerEntity customer,
      String metricId)
  throws 
      TimeSeriesClientException;
  
  /**
   * ONLY USED FOR TESTING PURPOSES
   * 
   * @param customer The owning customer
   * @param metricIds The list of metricIds to retrieve metric values for
   * @param start The start date to retrieve metric values for
   * @param end The end date to retrieve metric values for
   * 
   * @return The metric values for the given points from start to end
   * 
   * @throws TimeSeriesClientException If there was a problem retrieving the time series metric values
   */
  Map<String, Map<Long, Double>> retrieveMetricValuesFromKafka(
      AbstractCustomerEntity customer,
      Collection<String> metricIds,
      Long start,
      Long end)
  throws 
      TimeSeriesClientException;
  
  /**
   * 
   * @param customer The owning customer
   * @param jobId The job id to use
   * @param metricId The point's metricId
   * @param tags The tags to submit with the upsert
   * @param metricValues The time series metric values to store
   * 
   * @return <code>true</code> if the store was successful; <code>false</code> otherwise.
   * 
   * @throws TimeSeriesClientException If there was a problem storing the time series metric values
   */
  boolean submitMetricValuesToKafka(
      AbstractCustomerEntity customer,
      String jobId,
      String metricId,
      Map<String, String> tags,
      Map<Long, Double> metricValues)
  throws 
      TimeSeriesClientException;
}
//@formatter:on