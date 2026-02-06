//@formatter:off
package com.djt.hvac.domain.model.dictionary.template.v3.function.instance;

import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.dictionary.template.v3.function.AdFunctionTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.v3.function.inputpoint.AbstractAdFunctionTemplateInputPointEntity;
import com.djt.hvac.domain.model.dictionary.template.v3.function.inputpoint.AdFunctionTemplateInputPointGroupEntity;

public class AdEngineAdFunctionInstanceEntity extends AbstractEntity {
  private static final long serialVersionUID = 1L;
  
  private static final Logger LOGGER = LoggerFactory.getLogger(AdEngineAdFunctionInstanceEntity.class);
  
  public static final String DELAY = "DELAY";
  
  // Is associated with a template AND one of its input point groups.  All input points in the group need to exist for the instance.
  private final AdFunctionTemplateEntity adFunctionTemplate;
  private final String inputPointGroupName;
  private final String energyExchangeNodePath;
  
  // When true, then the expression in the function evaluates to true, regardless of what the delay is.
  private boolean isInFault;
  
  // When the expression in the function evaluates to true, regardless of what the delay is.
  private long isInFaultEpochMillis;
  
  // Will be true when isInFault is true AND currentTimeMillis - isInFaultEpochMillis >= DELAY (millis).
  // that is when isInFaultWithDelay is true, then the output time-series will have a value of true. 
  private boolean isInFaultWithDelay;
  
  // The constant DELAY will always be present and non-zero.  We convert it to a long for performance reasons.
  private long delayMillis = -1;
  
  private final Set<AdEngineAdFunctionInstanceInputConstantEntity> inputConstants = new TreeSet<>();
  private final Set<AdEngineAdFunctionInstanceInputPointEntity> inputPoints = new TreeSet<>();
  private final Set<AdEngineAdFunctionInstanceOutputPointEntity> outputPoints = new TreeSet<>();
  
  // Used to actually evaluate the fault expression, given the set of input points associated with this group.
  private final AdFunctionTemplateInputPointGroupEntity inputPointGroup;
  
  private Map<String, String> instanceInputConstants = null;
  
  public AdEngineAdFunctionInstanceEntity(
      AdFunctionTemplateEntity adFunctionTemplate,
      String inputPointGroupName,
      String energyExchangeNodePath) {
    super();
    requireNonNull(adFunctionTemplate, "adFunctionTemplate cannot be null");
    requireNonNull(inputPointGroupName, "inputPointGroupName cannot be null");
    requireNonNull(energyExchangeNodePath, "energyExchangeNodePath cannot be null");
    this.adFunctionTemplate = adFunctionTemplate;
    this.inputPointGroupName = inputPointGroupName;
    this.energyExchangeNodePath = energyExchangeNodePath;
    
    // Verify that the given input point group exists for the given function template.
    inputPointGroup = adFunctionTemplate.getAdFunction().getInputPointGroup(inputPointGroupName);
  }
  
  public AdFunctionTemplateEntity getAdFunctionTemplate() {
    return adFunctionTemplate;
  }

  public String getEnergyExchangeNodePath() {
    return energyExchangeNodePath;
  }
  
  public String getInputPointGroupName() {
    return inputPointGroupName;
  }

  public AdFunctionTemplateInputPointGroupEntity getInputPointGroup() {
    return inputPointGroup;
  }  
  
  public Set<AdEngineAdFunctionInstanceInputConstantEntity> getInputConstants() {
    return inputConstants;
  }

  public AdEngineAdFunctionInstanceInputConstantEntity getInputConstant(String name) {
    
    AdEngineAdFunctionInstanceInputConstantEntity ic = getInputConstantNullIfNotExists(name);
    if (ic == null) {
      throw new IllegalStateException("AD Engine AD function instance input constant with name: "
          + name
          + " not found in: "
          + inputConstants);
    }
    return ic;
  }

  public AdEngineAdFunctionInstanceInputConstantEntity getInputConstantNullIfNotExists(String name) {
    
    for (AdEngineAdFunctionInstanceInputConstantEntity ic: inputConstants) {
      if (ic.getAdFunctionTemplateInputConstant().getName().equals(name)) {
        return ic;
      }
    }
    return null;
  }
  
  public boolean addInputConstant(AdEngineAdFunctionInstanceInputConstantEntity inputConstant) throws EntityAlreadyExistsException {
    return addChild(inputConstants, inputConstant, this);
  }

  public Set<AdEngineAdFunctionInstanceInputPointEntity> getInputPoints() {
    return inputPoints;
  }

  public AdEngineAdFunctionInstanceInputPointEntity getInputPoint(String name) {
    
    AdEngineAdFunctionInstanceInputPointEntity ip = getInputPointNullIfNotExists(name);
    if (ip == null) {
      throw new IllegalStateException("AD Engine AD function instance input point with name: "
          + name
          + " not found in: "
          + inputPoints);
    }
    return ip;
  }

  public AdEngineAdFunctionInstanceInputPointEntity getInputPointNullIfNotExists(String name) {
    
    for (AdEngineAdFunctionInstanceInputPointEntity ip: inputPoints) {
      if (ip.getAdFunctionTemplateInputPoint().getName().equals(name)) {
        return ip;
      }
    }
    return null;
  }
  
  public boolean addInputPoint(AdEngineAdFunctionInstanceInputPointEntity inputPoint) throws EntityAlreadyExistsException {
    return addChild(inputPoints, inputPoint, this);
  }

  public Set<AdEngineAdFunctionInstanceOutputPointEntity> getOutputPoints() {
    return outputPoints;
  }

  public boolean addOutputPoint(AdEngineAdFunctionInstanceOutputPointEntity outputPoint) throws EntityAlreadyExistsException {
    return addChild(outputPoints, outputPoint, this);
  }
  
  public boolean isInFault() {
    return isInFault;
  }

  public long getIsInFaultEpochMillis() {
    return isInFaultEpochMillis;
  }

  public boolean isInFaultWithDelay() {
    return isInFaultWithDelay;
  }

  public long getDelayMillis() {
    return delayMillis;
  }

  private void initializeDelayMillis() {
    
    // The delay constant is stored in minutes, so convert it to milliseconds.
    String delay = getInputConstant(DELAY).getValue();
    delayMillis = Long.parseLong(delay) * 60 * 1000;
  }
  
  private void initializeInstanceInputConstants() {
    
    instanceInputConstants = new HashMap<>();
    for (AdEngineAdFunctionInstanceInputConstantEntity ic: inputConstants) {
      
      instanceInputConstants.put(ic.getAdFunctionTemplateInputConstant().getName(), ic.getValue());
    }
  }
  
  private boolean isDelayThresholdMet(long timestamp) {
    
    if (isInFaultEpochMillis <= 0) {
      throw new IllegalStateException("Cannot invoke isDelayThresholdMet when not in fault state.");
    }
    
    if (timestamp - isInFaultEpochMillis >= delayMillis) {
      return true;
    }
    return false;
  }
  
  /**
   * Evaluates the input data, which are directly associated with the input points (along with their timestamps)
   * 
   * @return A result for each *complete* timestamp (meaning that ALL points have data for each given timestamp)
   */
  public SortedMap<Long, Boolean> evaluate() {
    
    SortedMap<Long, Boolean> results = new TreeMap<>();
   
    if (instanceInputConstants == null) {
      initializeInstanceInputConstants();
    }
    
    if (delayMillis == -1) {
      initializeDelayMillis();
    }
    
    // First parse all input data associated directly with each point and segregate by timestamp.
    SortedMap<Long, Map<String, String>> instanceInputPointsByTimestamp = new TreeMap<>();
    for (AdEngineAdFunctionInstanceInputPointEntity ip: inputPoints) {
      
      String inputPointName = ip.getAdFunctionTemplateInputPoint().getName();
      Map<Long, String> inputPointValues = ip.getValues();
      for (Map.Entry<Long, String> entry: inputPointValues.entrySet()) {
        
        Long timestamp = entry.getKey();
        String value = entry.getValue();
        
        Map<String, String> map = instanceInputPointsByTimestamp.get(timestamp);
        if (map == null) {
          
          map = new HashMap<>();
          instanceInputPointsByTimestamp.put(timestamp, map);
        }
        
        map.put(inputPointName, value);
      }
    }
    
    // We will only evaluate those timestamps which have the correct number of points.  NOTE: Here,
    // all points are required, because groups of points are now part of "input point groups".
    int numPoints = inputPointGroup.getInputPoints().size();
    for (Map.Entry<Long, Map<String, String>> entry: instanceInputPointsByTimestamp.entrySet()) {
      
      Long timestamp = entry.getKey();
      Map<String, String> instanceInputPoints = entry.getValue();
      
      if (instanceInputPoints.size() == numPoints) {
        
        boolean result = evaluate(timestamp, instanceInputPoints);
        results.put(timestamp, Boolean.valueOf(result));
        
      } else if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Incomplete input point data for timestamp: [{}] for: [{}]",
            timestamp,
            this);
      }
    }
    
    return results;
  }

  /**
   * 
   * @param timestamp The timestamp (epoch millis) for whose input constants/points are also given
   * @param instanceInputPoints A map containing input point values keyed by name
   * 
   * @return <code>true</code> if the expression evaluates to true, *including* the delay constant
   */
  public boolean evaluate(
      long timestamp,
      Map<String, String> instanceInputPoints) {
    
    if (instanceInputConstants == null) {
      initializeInstanceInputConstants();
    }

    if (delayMillis == -1) {
      initializeDelayMillis();
    }
    
    boolean result = inputPointGroup.evaluateExpression(
        instanceInputConstants,
        instanceInputPoints);
    
    boolean outputPointValue = false;
    
    if (!isInFault && !result) { // AD function not in fault and expression result is false. (output value is false)
      
      outputPointValue = false;

    } else if (!isInFault && result) { // If the AD function not in fault, but the expression result is true, then start (output point won't be true unless delay has been met)

      isInFault = true;
      isInFaultEpochMillis = timestamp;
      
      if (isDelayThresholdMet(timestamp)) {
        
        isInFaultWithDelay = true;
        outputPointValue = true; 
      }
      
    } else if (isInFault && !result) { // If the AD function is in fault, but the expression result is false, then reset. (output value is false)
      
      isInFault = false;
      isInFaultEpochMillis = -1;
      
      
    } else if (isInFault && result) { // If both the AD function and the function result was in fault, then start (output point won't be true unless delay has been met)

      isInFault = true;
      
      if (isDelayThresholdMet(timestamp)) {
        
        isInFaultWithDelay = true;
        outputPointValue = true; 
      }
      
    }
    
    for (AdEngineAdFunctionInstanceOutputPointEntity outputPoint: outputPoints) {
      
      outputPoint.addValue(Long.valueOf(timestamp), Boolean.toString(outputPointValue));
    }
    
    return outputPointValue;
  }
  
  @Override
  public String getNaturalIdentity() {
    
    return new StringBuilder()
        .append(adFunctionTemplate.getNaturalIdentity())
        .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
        .append(inputPointGroupName)
        .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
        .append(energyExchangeNodePath)
        .toString();
  }
  
  @Override
  public void validate(
      Set<IssueType> issueTypes,
      List<ValidationMessage> validationMessages, 
      boolean remediate) {
    
    if (this.outputPoints.isEmpty()) {
      
      throw new IllegalStateException("There must exist at least 1 output point for: ["
          + this
          + "].");
    }
    
    if (getInputConstantNullIfNotExists(DELAY) == null) {
      
      throw new IllegalStateException("DELAY constant must exist for: ["
          + this
          + "].");
    }

    // Verify that there exists an input point for each input point in the input point group from the template.
    Set<String> inputPointGroupNames = new TreeSet<>();
    for (AbstractAdFunctionTemplateInputPointEntity ip: inputPointGroup.getInputPoints()) {
      inputPointGroupNames.add(ip.getName());
    }
    
    Set<String> inputPointNames = new TreeSet<>();
    for (AdEngineAdFunctionInstanceInputPointEntity ip: inputPoints) {
      inputPointNames.add(ip.getAdFunctionTemplateInputPoint().getName());
    }
    
    if (!inputPointGroupNames.equals(inputPointNames)) {
      
      throw new IllegalStateException("Mismatch between input point group names: ["
          + inputPointGroupNames
          + "] and input point names: ["
          + inputPointNames
          + "] for: ["
          + this
          + "].");
    }
    
  }
}
//@formatter:on