package com.djt.hvac.domain.model.dictionary.template.v3.function.instance;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;

import com.djt.hvac.domain.model.dictionary.template.v3.function.instance.AdEngineAdFunctionInstanceEntity;
import com.djt.hvac.domain.model.dictionary.template.v3.function.instance.AdEngineAdFunctionInstanceInputConstantEntity;
import com.djt.hvac.domain.model.dictionary.template.v3.function.instance.AdEngineAdFunctionInstanceInputPointEntity;

public class AdEngineAdFunctionInstanceTestDataReader {
  
  private AdEngineAdFunctionInstanceTestDataReader() {
  }

  public static final TimeZone UTC_TIME_ZONE = TimeZone.getTimeZone("UTC");
  public static final ZoneId UTC_ZONE_ID = UTC_TIME_ZONE.toZoneId();
  
  public static final String DATE_TIME_FORMAT1 = "M/d/yy H:mm";
  public static final ThreadLocal<DateTimeFormatter> DATE_TIME_FORMATTER1 =
      new ThreadLocal<DateTimeFormatter>() {
        @Override
        protected DateTimeFormatter initialValue() {
          return DateTimeFormatter
              .ofPattern(DATE_TIME_FORMAT1)
              .withZone(ZoneId.of("UTC"));
        }
      };

  public static final String DATE_TIME_FORMAT2 = "M/d/yy HH:mm";
  public static final ThreadLocal<DateTimeFormatter> DATE_TIME_FORMATTER2 =
      new ThreadLocal<DateTimeFormatter>() {
        @Override
        protected DateTimeFormatter initialValue() {
          return DateTimeFormatter
              .ofPattern(DATE_TIME_FORMAT2)
              .withZone(ZoneId.of("UTC"));
        }
      };
  
  public static final String DATE_TIME_FORMAT3 = "M/d/yyyy H:mm";
  public static final ThreadLocal<DateTimeFormatter> DATE_TIME_FORMATTER3 =
      new ThreadLocal<DateTimeFormatter>() {
        @Override
        protected DateTimeFormatter initialValue() {
          return DateTimeFormatter
              .ofPattern(DATE_TIME_FORMAT3)
              .withZone(ZoneId.of("UTC"));
        }
      };

  public static final String DATE_TIME_FORMAT4 = "M/d/yyyy HH:mm";
  public static final ThreadLocal<DateTimeFormatter> DATE_TIME_FORMATTER4 =
      new ThreadLocal<DateTimeFormatter>() {
        @Override
        protected DateTimeFormatter initialValue() {
          return DateTimeFormatter
              .ofPattern(DATE_TIME_FORMAT4)
              .withZone(ZoneId.of("UTC"));
        }
      };
      
  private static final List<ThreadLocal<DateTimeFormatter>> DATE_TIME_FORMATTERS = new ArrayList<>();
  static {
    DATE_TIME_FORMATTERS.add(DATE_TIME_FORMATTER1);
    DATE_TIME_FORMATTERS.add(DATE_TIME_FORMATTER2);
    DATE_TIME_FORMATTERS.add(DATE_TIME_FORMATTER3);
    DATE_TIME_FORMATTERS.add(DATE_TIME_FORMATTER4);
  }
  
  public static Long parseDateTimeIntoTimestamp(String dateTime) {
    
    LocalDateTime ldt = null;
    for (ThreadLocal<DateTimeFormatter> dateTimeFormatter: DATE_TIME_FORMATTERS) {
      try {
        ldt = LocalDateTime.parse(dateTime, dateTimeFormatter.get());
      } catch (DateTimeParseException e) {
        // Eat exception
      }
    }
    
    if (ldt == null) {
      throw new IllegalStateException("Unable to parse dateTime: ["
          + dateTime 
          + "].  Expected formats are: ["
          + DATE_TIME_FORMAT1
          + ", "
          + DATE_TIME_FORMAT2
          + ", "
          + DATE_TIME_FORMAT3
          + ", "
          + DATE_TIME_FORMAT4
          + "].");
    }
    
    ZoneOffset zo = UTC_ZONE_ID.getRules().getOffset(ldt);        
    long epochMillis = ldt.toInstant(zo).toEpochMilli();
    return epochMillis;
  }
  
  public static SortedMap<Long, Boolean> parseTestDataFile(AdEngineAdFunctionInstanceEntity adEngineAdFunctionInstance) throws IOException {
    
    String adFunctionName = adEngineAdFunctionInstance.getAdFunctionTemplate().getAdFunction().getName();
    String inputGroupName = adEngineAdFunctionInstance.getInputPointGroupName();
    
    String filename = adFunctionName + "__" + inputGroupName + ".csv";
    File file = new File("src/test/resources/com/resolute/services/domain/model/dictionary/template/v3/function/instance/" + filename);
    List<String> sourceLines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
    boolean parsedInputConstantsHeader = false;
    boolean parsedInputConstantsValues = false;
    boolean parsedInputPointsHeader = false;
    
    List<String> inputConstantsHeaders = new ArrayList<>();
    List<String> inputConstantsValues = new ArrayList<>();
    
    List<String> inputPointsHeaders = new ArrayList<>();
    LinkedHashMap<String, List<String>> inputPointsValues = new LinkedHashMap<>();
    SortedMap<Long, Boolean> expectedFaults = new TreeMap<>();
    
    for (String line: sourceLines) {
      
      line = line.trim();
      if (line.length() > 0) {
        
        String[] elements = line.split(",");
        if (elements.length > 0) {

          for (int i=0; i < elements.length; i++) {
            
            String element = elements[i];
            if (!element.trim().isEmpty()) {

              if (!parsedInputConstantsHeader) {
                inputConstantsHeaders.add(element);
              } else if (!parsedInputConstantsValues) {
                inputConstantsValues.add(element);
              } else if (!parsedInputPointsHeader) {
                inputPointsHeaders.add(element);
              } else {
                
                String timestamp = elements[0];
                
                if (i == 0) {
                  timestamp = element;
                  
                } else if (i == elements.length -1 ) {
                  Integer expectedFault = Integer.parseInt(element);
                  Long ts = AdEngineAdFunctionInstanceTestDataReader.parseDateTimeIntoTimestamp(timestamp);
                  if (expectedFault.intValue() == 0) {
                    expectedFaults.put(ts, Boolean.FALSE);  
                  } else if (expectedFault.intValue() == 1) {
                    expectedFaults.put(ts, Boolean.TRUE);
                  } else {
                    throw new IllegalStateException("Expected 0 or 1, but got: ["
                        + expectedFault 
                        + "] for expected fault value for line: [" 
                        + line
                        + "]");
                  }
                } else {
                  
                  List<String> list = null;
                  if (i == 1) {
                    list = new ArrayList<>();
                    inputPointsValues.put(timestamp, list);
                  } else {
                    list = inputPointsValues.get(timestamp);
                  }
                  list.add(element);
                }
              }
            }
          }

          if (!parsedInputConstantsHeader) {
            parsedInputConstantsHeader = true;
          } else if (!parsedInputConstantsValues) {
            parsedInputConstantsValues = true;
          } else if (!parsedInputPointsHeader) {
            parsedInputPointsHeader = true;
          }
        }
      }
    }
    
    // Now that we have loaded the test data from the CSV, load the data into the instance.
    if (inputConstantsHeaders.size() != inputConstantsValues.size()) {
      throw new IllegalStateException("Size mismatch between inputConstantsHeaders: ["
          + inputConstantsHeaders.size()
          + "] and inputConstantsValues: ["
          + inputConstantsValues.size()
          + "]");
    }
    
    // INPUT CONSTANTS
    for (int i=0; i < inputConstantsHeaders.size(); i++) {
      
      String inputConstantName = inputConstantsHeaders.get(i);
      String inputConstantValue = inputConstantsValues.get(i);
      
      AdEngineAdFunctionInstanceInputConstantEntity ic = adEngineAdFunctionInstance.getInputConstant(inputConstantName);
      
      ic.setValue(inputConstantValue);
    }
    
    // INPUT POINTS
    for (Map.Entry<String, List<String>> entry: inputPointsValues.entrySet()) {
      
      String timestamp = entry.getKey();
      List<String> inputPointValuesForTimestamp = entry.getValue();
      
      if (inputPointsHeaders.size() != (inputPointValuesForTimestamp.size()+2)) {
        throw new IllegalStateException("Size mismatch between inputPointsHeaders: ["
            + inputPointsHeaders.size()
            + "] and inputPointValuesForTimestamp: ["
            + inputPointValuesForTimestamp.size()
            + "] for timestamp: ["
            + timestamp
            + "]");
      }
      
      for (int i=1; i < inputPointsHeaders.size()-1; i++) {
        
        String inputPointName = inputPointsHeaders.get(i);
        String inputPointValue = inputPointValuesForTimestamp.get(i-1);

        // Most of the test files have all points regardless of input point groups.
        AdEngineAdFunctionInstanceInputPointEntity ip = adEngineAdFunctionInstance.getInputPointNullIfNotExists(inputPointName);
        if (ip != null) {

          Long ts = AdEngineAdFunctionInstanceTestDataReader.parseDateTimeIntoTimestamp(timestamp);
          
          ip.addValue(ts, inputPointValue);
        }
      }
    }

    return expectedFaults;
  }
}