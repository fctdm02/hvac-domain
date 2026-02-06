//@formatter:off
package com.djt.hvac.domain.model.timeseries.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.timeseries.exception.TimeSeriesClientException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

public class MockTimeSeriesServiceClient extends AbstractTimeSeriesServiceClient {
  
  public static final String ETC_UTC_TIMEZONE = "Etc/UTC";
  
  private static final Logger LOGGER = LoggerFactory.getLogger(MockTimeSeriesServiceClient.class);
  
  private static final MockTimeSeriesServiceClient INSTANCE = new MockTimeSeriesServiceClient();
  public static final MockTimeSeriesServiceClient getInstance() {
    return INSTANCE;
  }

  private static boolean USE_PRETTY_PRINT = false;
  public static boolean getPrettyPrint() {
    return USE_PRETTY_PRINT;
  }
  public static void setPrettyPrint(boolean prettyPrint) {
    USE_PRETTY_PRINT = prettyPrint;
  }

  private String basePath;

  public MockTimeSeriesServiceClient() {
    this(null);
  }

  public MockTimeSeriesServiceClient(String basePath) {
    super();
    if (basePath != null) {
      this.basePath = basePath;
    } else {
      this.basePath = System.getProperty("user.home") + "/";      
    }
  }
  
  public String basePath() {
    return basePath;
  }
  
  public void setBasePath(String basePath) {
    this.basePath = basePath;
  }  

  @Override
  public  Map<String, Map<Long, Double>> retrieveMetricValuesFromTsdb(
      Integer customerId,
      Collection<String> metricIds,
      Long start,
      Long end)
  throws 
      TimeSeriesClientException {
    
    LocalDateTime startLocalDateTime = Instant
        .ofEpochMilli(start)
        .atZone(TimeZone
            .getTimeZone(ETC_UTC_TIMEZONE)
            .toZoneId())
        .toLocalDateTime();
    
    if (end == null) {
      end = Long.valueOf(AbstractEntity.getTimeKeeper().getCurrentTimestamp().getTime());
    }
    LocalDateTime endLocalDateTime = Instant
        .ofEpochMilli(end)
        .atZone(TimeZone
            .getTimeZone(ETC_UTC_TIMEZONE)
            .toZoneId())
        .toLocalDateTime();
    
    LOGGER.info("retrieveMetricValuesFromTsdb(): metricIds: {} start: {}, end: {}", metricIds, startLocalDateTime, endLocalDateTime);

    Map<String, Map<Long, Double>> allMetricValues = loadTsdbMetricValuesFromRepository(customerId);
    
    Map<String, Map<Long, Double>> allMatchingMetricValues = new HashMap<>();
    for (String mid: metricIds) {
      
      String metricId = null;
      int index = mid.lastIndexOf(':');
      if (index != -1) {
        metricId = mid.substring(index+1);
      } else {
        metricId = mid;
      }
      
      Map<Long, Double> metricValues = allMetricValues.get(metricId);
      if (metricValues != null) {
      
        Map<Long, Double> filteredMetricValues = new TreeMap<>();
        for (Map.Entry<Long, Double> entry: metricValues.entrySet()) {
          
          Long millis = entry.getKey();
          if (millis >= start && millis <= end) {

            filteredMetricValues.put(millis / 1000, entry.getValue());
          }
        }
        allMatchingMetricValues.put(metricId, filteredMetricValues);
      }
    }
    
    return allMatchingMetricValues;
  }

  @Override
  public boolean submitMetricValuesToTsdb(
      Integer customerId,
      String metricId,
      Map<String, String> tags,
      Map<Long, Double> metricValues)
  throws 
      TimeSeriesClientException {
        
    LOGGER.info("submitMetricValuesToTsdb(): metricId: {} metricValues size: {}", metricId, metricValues.size());
    Map<String, Map<Long, Double>> incomingMetricValues = new TreeMap<>();
    incomingMetricValues.put(metricId, metricValues);
    return storeTsdbMetricValues(customerId, tags, incomingMetricValues);
  }
  
  @Override
  public boolean submitMetricValuesBatchToTsdb(
      Integer customerId,
      Map<String, String> tags,
      Map<String, Map<Long, Double>> incomingMetricValues)
  throws 
      TimeSeriesClientException {

    LOGGER.info("submitMetricValuesBatchToTsdb(): num metricIds: {}", incomingMetricValues.size());
    return storeTsdbMetricValues(customerId, tags, incomingMetricValues);
  }
  
  @Override
  public void deleteCustomerMetricValues(
      Integer customerId,
      String customerUuid)
  throws 
      TimeSeriesClientException {
   
    LOGGER.info("deleteCustomerMetricValues(): customerId: {}", customerId);
    
    try {
      
      storeTsdbMetricValuesToRepository(customerId, new TreeMap<>());
      storeKafkaMetricValuesToRepository(customerId, new TreeMap<>());
      
    } catch (Exception e) {
      throw new TimeSeriesClientException("Unable to delete metric values for customer: "
          + customerId, e);
    }
  }  
    
  @Override
  public void deletePointMetricValues(
      Integer customerId,
      String metricId)
  throws 
      TimeSeriesClientException {
   
    LOGGER.debug("deletePointMetricValues(): customer: {}", customerId);
    
    Map<String, Map<Long, Double>> repoMetricValues = loadTsdbMetricValuesFromRepository(customerId);
    
    repoMetricValues.remove(metricId);
    
    storeTsdbMetricValuesToRepository(customerId, repoMetricValues);    
  }
  
  @Override
  public  Map<String, Map<Long, Double>> retrieveMetricValuesFromKafka(
      Integer customerId,
      Collection<String> metricIds,
      Long start,
      Long end)
  throws 
      TimeSeriesClientException {
    
    LocalDateTime startLocalDateTime = Instant
        .ofEpochMilli(start)
        .atZone(TimeZone
            .getTimeZone(ETC_UTC_TIMEZONE)
            .toZoneId())
        .toLocalDateTime();            

    LocalDateTime endLocalDateTime = Instant
        .ofEpochMilli(end)
        .atZone(TimeZone
            .getTimeZone(ETC_UTC_TIMEZONE)
            .toZoneId())
        .toLocalDateTime();
    
    LOGGER.info("retrieveMetricValuesFromKafka(): metricIds: {} start: {}, end: {}", metricIds, startLocalDateTime, endLocalDateTime);

    Map<String, Map<Long, Double>> allMetricValues = loadKafkaMetricValuesFromRepository(customerId);
    
    Map<String, Map<Long, Double>> allMatchingMetricValues = new HashMap<>();
    for (String metricId: metricIds) {
      
      Map<Long, Double> metricValues = allMetricValues.get(metricId);
      if (metricValues != null) {
      
        Map<Long, Double> filteredMetricValues = new TreeMap<>();
        for (Map.Entry<Long, Double> entry: metricValues.entrySet()) {
          
          Long millis = entry.getKey();
          if (millis >= start && millis <= end) {

            filteredMetricValues.put(millis, entry.getValue());
          }
        }
        allMatchingMetricValues.put(metricId, filteredMetricValues);
      }
    }
    
    return allMatchingMetricValues;
  }
  
  @Override
  public boolean submitMetricValuesToKafka(
      Integer customerId,
      String jobId,
      String metricId,
      Map<String, String> tags,
      Map<Long, Double> metricValues)
  throws 
      TimeSeriesClientException {
        
    LOGGER.info("submitMetricValuesToKafka(): metricId: {} metricValues size: {}", metricId, metricValues.size());
    Map<String, Map<Long, Double>> incomingMetricValues = new TreeMap<>();
    incomingMetricValues.put(metricId, metricValues);
    return storeKafkaMetricValues(customerId, tags, incomingMetricValues);
  }  
  
  private boolean storeTsdbMetricValues(
      Integer customerId,
      Map<String, String> tags,
      Map<String, Map<Long, Double>> incomingMetricValues)
  throws 
      TimeSeriesClientException {
        
    Map<String, Map<Long, Double>> repoMetricValues = loadTsdbMetricValuesFromRepository(customerId);
    
    for (Map.Entry<String, Map<Long, Double>> entry: incomingMetricValues.entrySet()) {
      
      String incomingMetricId = entry.getKey();
      Map<Long, Double> incomingValues = entry.getValue();
      
      Map<Long, Double> repoValues = repoMetricValues.get(incomingMetricId);
      if (repoValues == null) {
        
        repoValues = new TreeMap<>();
        repoMetricValues.put(incomingMetricId, repoValues);
      }
      repoValues.putAll(incomingValues);
    }
    
    storeTsdbMetricValuesToRepository(customerId, repoMetricValues);
    
    return true;
  }

  private Map<String, Map<Long, Double>> loadTsdbMetricValuesFromRepository(int customerId) {
    
    File file = new File(basePath + "/Customer_" + Integer.toString(customerId) + "_TsdbMetricValues.json");
    if (file.exists()) {
      try (InputStream in = new BufferedInputStream(new FileInputStream(file));
          
          Scanner s = new Scanner(in).useDelimiter("\\A")) {
          String fullJson = s.hasNext() ? s.next() : "";
  
          JsonNode jsonNode = AbstractEntity.OBJECT_MAPPER.get().readValue(fullJson, JsonNode.class);
          String compactJson = jsonNode.toString();
          
          Map<String, Map<Long, Double>> metricValues = AbstractEntity.OBJECT_MAPPER.get().readValue(compactJson, new TypeReference<Map<String, Map<Long, Double>>>() {});
          return metricValues;
  
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return new TreeMap<>();
  }
  
  private void storeTsdbMetricValuesToRepository(int customerId, Map<String, Map<Long, Double>> metricValues) {
    
    File file = new File(basePath + "/Customer_" + Integer.toString(customerId) + "_TsdbMetricValues.json");
    OutputStream out = null;
    try {
      
      out = new BufferedOutputStream(new FileOutputStream(file));
      AbstractEntity.OBJECT_WRITER.get().writeValue(out, metricValues);

    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (out != null) {
        try {
          out.flush();
          out.close();
        } catch (IOException ioe) {
        }
      }
    }
  }
  
  private boolean storeKafkaMetricValues(
      Integer customerId,
      Map<String, String> tags,
      Map<String, Map<Long, Double>> incomingMetricValues)
  throws 
      TimeSeriesClientException {
        
    Map<String, Map<Long, Double>> repoMetricValues = loadKafkaMetricValuesFromRepository(customerId);
    
    for (Map.Entry<String, Map<Long, Double>> entry: incomingMetricValues.entrySet()) {
      
      String incomingMetricId = entry.getKey();
      Map<Long, Double> incomingValues = entry.getValue();
      
      Map<Long, Double> repoValues = repoMetricValues.get(incomingMetricId);
      if (repoValues == null) {
        
        repoValues = new TreeMap<>();
        repoMetricValues.put(incomingMetricId, repoValues);
      }
      repoValues.putAll(incomingValues);
    }
    
    storeKafkaMetricValuesToRepository(customerId, repoMetricValues);
    
    return true;
  }

  public Map<String, Map<Long, Double>> getAllTsdbMetricValues(Integer customerId) {
    return loadTsdbMetricValuesFromRepository(customerId);
  }
  
  public Map<String, Map<Long, Double>> getAllKafkaMetricValues(Integer customerId) {
    return loadKafkaMetricValuesFromRepository(customerId);
  }
  
  private Map<String, Map<Long, Double>> loadKafkaMetricValuesFromRepository(int customerId) {
    
    File file = new File(basePath + "/Customer_" + Integer.toString(customerId) + "_KafkaMetricValues.json");
    if (file.exists()) {
      try (InputStream in = new BufferedInputStream(new FileInputStream(file));
          
          Scanner s = new Scanner(in).useDelimiter("\\A")) {
          String fullJson = s.hasNext() ? s.next() : "";
  
          JsonNode jsonNode = AbstractEntity.OBJECT_MAPPER.get().readValue(fullJson, JsonNode.class);
          String compactJson = jsonNode.toString();
          
          Map<String, Map<Long, Double>> metricValues = AbstractEntity.OBJECT_MAPPER.get().readValue(compactJson, new TypeReference<Map<String, Map<Long, Double>>>() {});
          return metricValues;
  
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return new TreeMap<>();
  }
  
  private void storeKafkaMetricValuesToRepository(int customerId, Map<String, Map<Long, Double>> metricValues) {
    
    File file = new File(basePath + "/Customer_" + Integer.toString(customerId) + "_KafkaMetricValues.json");
    OutputStream out = null;
    try {
      
      out = new BufferedOutputStream(new FileOutputStream(file));
      AbstractEntity.OBJECT_WRITER.get().writeValue(out, metricValues);

    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (out != null) {
        try {
          out.flush();
          out.close();
        } catch (IOException ioe) {
        }
      }
    }
  }    
  
}
//@formatter:on