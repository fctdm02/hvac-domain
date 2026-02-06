package com.djt.hvac.domain.model.nodehierarchy.validation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
  "customerId",
  "portfolioName",
  "isSummary",
  "numProcessedBuildings",
  "numProcessedSubBuildings",
  "numProcessedPlants",
  "numProcessedLoops",
  "numProcessedFloors",
  "numProcessedEquipment",
  "numProcessedMappablePoints",
  "numProcessedCustomAsyncComputedPoints",
  "numProcessedScheduledAsyncComputedPoints",
  "numProcessedAsyncComputedPoints",
  "numProcessedAdFunctionInstanceCandidates",
  "numProcessedAdFunctionInstances",
  "numProcessedReportInstances",
  "totalNumberOfValidationMessages",
  "validationMessageCountByIssueType",
  "validationMessages"  
})
public class PortfolioValidationResult {
  
  private boolean isSummary;
  private Integer customerId;
  private String portfolioName;
  private int numProcessedBuildings;
  private int numProcessedSubBuildings;
  private int numProcessedPlants;
  private int numProcessedLoops;
  private int numProcessedFloors;
  private int numProcessedEquipment;
  private int numProcessedMappablePoints;
  private int numProcessedCustomAsyncComputedPoints;
  private int numProcessedScheduledAsyncComputedPoints;
  private int numProcessedAsyncComputedPoints;
  private int numProcessedAdFunctionInstanceCandidates;
  private int numProcessedAdFunctionInstances;
  private int numProcessedReportInstances;
  
  private int totalNumberOfValidationMessages;
  private Map<String, Integer> validationMessageCountByIssueType;
  private Map<String, List<ValidationMessage>> validationMessages;
  
  public PortfolioValidationResult() {
  }

  public PortfolioValidationResult(
      PortfolioEntity portfolio, 
      List<ValidationMessage> allValidationMessages) {
    this(
        false,
        portfolio.getCustomerId(),
        portfolio.getDisplayName(),
        portfolio.getNumBuildingsProcessed(),
        portfolio.getNumSubBuildingsProcessed(),
        portfolio.getNumPlantsProcessed(),
        portfolio.getNumLoopsProcessed(),
        portfolio.getNumFloorsProcessed(),
        portfolio.getNumEquipmentProcessed(),
        portfolio.getNumMappablePointsProcessed(),
        portfolio.getNumCustomAsyncComputedPointsProcessed(),
        portfolio.getNumScheduledAsyncComputedPointsProcessed(),
        portfolio.getNumAsyncComputedPointsProcessed(),
        portfolio.getNumAdFunctionInstanceCandidatesProcessed(),
        portfolio.getNumAdFunctionInstancesProcessed(),
        portfolio.getNumReportInstancesProcessed(),
        allValidationMessages.size(),
        allValidationMessages);
  }
  
  public PortfolioValidationResult(
      boolean isSummary,
      int customerId,
      String portfolioName,
      int numProcessedBuildings,
      int numProcessedSubBuildings,
      int numProcessedPlants,
      int numProcessedLoops,
      int numProcessedFloors,
      int numProcessedEquipment,
      int numProcessedMappablePoints,
      int numProcessedCustomAsyncComputedPoints,
      int numProcessedScheduledAsyncComputedPoints,
      int numProcessedAsyncComputedPoints,
      int numProcessedAdFunctionInstanceCandidates,
      int numProcessedAdFunctionInstances,
      int numProcessedReportInstances,
      int totalNumberOfValidationMessages,
      List<ValidationMessage> allValidationMessages) {
    
    this.isSummary = isSummary;
    this.customerId = customerId;
    this.portfolioName = portfolioName;
    this.numProcessedBuildings = numProcessedBuildings;
    this.numProcessedSubBuildings = numProcessedSubBuildings;
    this.numProcessedPlants = numProcessedPlants;
    this.numProcessedLoops = numProcessedLoops;
    this.numProcessedFloors = numProcessedFloors;
    this.numProcessedEquipment = numProcessedEquipment;
    this.numProcessedMappablePoints = numProcessedMappablePoints;
    this.numProcessedCustomAsyncComputedPoints = numProcessedCustomAsyncComputedPoints;
    this.numProcessedScheduledAsyncComputedPoints = numProcessedScheduledAsyncComputedPoints;
    this.numProcessedAsyncComputedPoints = numProcessedAsyncComputedPoints;
    this.numProcessedAdFunctionInstanceCandidates = numProcessedAdFunctionInstanceCandidates;
    this.numProcessedAdFunctionInstances = numProcessedAdFunctionInstances;
    this.numProcessedReportInstances = numProcessedReportInstances;
    this.totalNumberOfValidationMessages = totalNumberOfValidationMessages;

    validationMessageCountByIssueType = new TreeMap<>();
    validationMessages = new TreeMap<>();
    
    Iterator<ValidationMessage> iterator = allValidationMessages.iterator();
    while (iterator.hasNext()) {
      
      ValidationMessage validationMessage = iterator.next();
      String key = new StringBuilder()
          .append("issueId ")
          .append(validationMessage.getIssueId())
          .append(": ")
          .append(validationMessage.getIssue())
          .toString();
      
      List<ValidationMessage> list = validationMessages.get(key);
      if (list == null) {
        
        list = new ArrayList<>();
        validationMessages.put(key, list);
      }
      list.add(validationMessage);
    }
    
    Iterator<Entry<String, List<ValidationMessage>>> mapIterator = validationMessages.entrySet().iterator();
    while (mapIterator.hasNext()) {
      
      Entry<String, List<ValidationMessage>> entry = mapIterator.next();
      String issue = entry.getKey();
      List<ValidationMessage> list = entry.getValue();
      validationMessageCountByIssueType.put(issue, Integer.valueOf(list.size()));
    }
  }
  
  public boolean getIsSummary() {
    return isSummary;
  }
  public int getCustomerId() {
    return customerId;
  }
  public String getPortfolioName() {
    return portfolioName;
  }
  public int getNumProcessedBuildings() {
    return numProcessedBuildings;
  }
  public int getNumProcessedSubBuildings() {
    return numProcessedSubBuildings;
  }
  public int getNumProcessedPlants() {
    return numProcessedPlants;
  }
  public int getNumProcessedLoops() {
    return numProcessedLoops;
  }
  public int getNumProcessedFloors() {
    return numProcessedFloors;
  }
  public int getNumProcessedEquipment() {
    return numProcessedEquipment;
  }
  public int getNumProcessedMappablePoints() {
    return numProcessedMappablePoints;
  }
  public int getNumProcessedCustomAsyncComputedPoints() {
    return numProcessedCustomAsyncComputedPoints;
  }
  public int getNumProcessedScheduledAsyncComputedPoints() {
    return numProcessedScheduledAsyncComputedPoints;
  }
  public int getNumProcessedAsyncComputedPoints() {
    return numProcessedAsyncComputedPoints;
  }
  public int getNumProcessedAdFunctionInstanceCandidates() {
    return numProcessedAdFunctionInstanceCandidates;
  }
  public int getNumProcessedAdFunctionInstances() {
    return numProcessedAdFunctionInstances;
  }
  public int getNumProcessedReportInstances() {
    return numProcessedReportInstances;
  }
  public int getTotalNumberOfValidationMessages() {
    return totalNumberOfValidationMessages;
  }
  public Map<String, Integer> getValidationMessageCountByIssueType() {
    return validationMessageCountByIssueType;
  }
  public Map<String, List<ValidationMessage>> getValidationMessages() {
    return validationMessages;
  }
  
  @Override
  public String toString() {
    
    return new StringBuilder()
        .append("PortfolioValidationResult [customerId=")
        .append(customerId)
        .append(", portfolioName=")
        .append(portfolioName)
        .append(", numProcessedBuildings=")
        .append(numProcessedBuildings)
        .append(", numProcessedSubBuildings=")
        .append(numProcessedSubBuildings)
        .append(", numProcessedPlants=")
        .append(numProcessedPlants)
        .append(", numProcessedLoops=")
        .append(numProcessedLoops)
        .append(", numProcessedFloors=")
        .append(numProcessedFloors)
        .append(", numProcessedEquipment=")
        .append(numProcessedEquipment)
        .append(", numProcessedMappablePoints=")
        .append(numProcessedMappablePoints)
        .append(", numProcessedCustomAsyncComputedPoints=")
        .append(numProcessedCustomAsyncComputedPoints)
        .append(", numProcessedScheduledAsyncComputedPoints=")
        .append(numProcessedScheduledAsyncComputedPoints)
        .append(", numProcessedAsyncComputedPoints=")
        .append(numProcessedAsyncComputedPoints)
        .append(", numProcessedAdFunctionInstanceCandidates=")
        .append(numProcessedAdFunctionInstanceCandidates)
        .append(", numProcessedAdFunctionInstances=")
        .append(numProcessedAdFunctionInstances)
        .append(", numProcessedReportInstances=")
        .append(numProcessedReportInstances)
        .append(", totalNumberOfValidationMessages=")
        .append(totalNumberOfValidationMessages)
        .append(", validationMessageCountByIssueType=")
        .append(validationMessageCountByIssueType)
        .append("]")
        .toString();
  }

  public void setSummary(boolean isSummary) {
    this.isSummary = isSummary;
  }

  public void setCustomerId(Integer customerId) {
    this.customerId = customerId;
  }

  public void setPortfolioName(String portfolioName) {
    this.portfolioName = portfolioName;
  }

  public void setNumProcessedBuildings(int numProcessedBuildings) {
    this.numProcessedBuildings = numProcessedBuildings;
  }

  public void setNumProcessedSubBuildings(int numProcessedSubBuildings) {
    this.numProcessedSubBuildings = numProcessedSubBuildings;
  }

  public void setNumProcessedPlants(int numProcessedPlants) {
    this.numProcessedPlants = numProcessedPlants;
  }

  public void setNumProcessedLoops(int numProcessedLoops) {
    this.numProcessedLoops = numProcessedLoops;
  }
  
  public void setNumProcessedFloors(int numProcessedFloors) {
    this.numProcessedFloors = numProcessedFloors;
  }

  public void setNumProcessedEquipment(int numProcessedEquipment) {
    this.numProcessedEquipment = numProcessedEquipment;
  }

  public void setNumProcessedMappablePoints(int numProcessedMappablePoints) {
    this.numProcessedMappablePoints = numProcessedMappablePoints;
  }

  public void setNumProcessedCustomAsyncComputedPoints(int numProcessedCustomAsyncComputedPoints) {
    this.numProcessedCustomAsyncComputedPoints = numProcessedCustomAsyncComputedPoints;
  }
  
  public void setNumProcessedScheduledAsyncComputedPoints(int numProcessedScheduledAsyncComputedPoints) {
    this.numProcessedScheduledAsyncComputedPoints = numProcessedScheduledAsyncComputedPoints;
  }
  
  public void setNumProcessedAsyncComputedPoints(int numProcessedAsyncComputedPoints) {
    this.numProcessedAsyncComputedPoints = numProcessedAsyncComputedPoints;
  }
  
  public void setNumProcessedAdFunctionInstanceCandidates(int numProcessedAdFunctionInstanceCandidates) {
    this.numProcessedAdFunctionInstanceCandidates = numProcessedAdFunctionInstanceCandidates;
  }

  public void setNumProcessedAdFunctionInstances(int numProcessedAdFunctionInstances) {
    this.numProcessedAdFunctionInstances = numProcessedAdFunctionInstances;
  }

  public void setNumProcessedReportInstances(int numProcessedReportInstances) {
    this.numProcessedReportInstances = numProcessedReportInstances;
  }
  
  public void setTotalNumberOfValidationMessages(int totalNumberOfValidationMessages) {
    this.totalNumberOfValidationMessages = totalNumberOfValidationMessages;
  }

  public void setValidationMessageCountByIssueType(
      Map<String, Integer> validationMessageCountByIssueType) {
    this.validationMessageCountByIssueType = validationMessageCountByIssueType;
  }

  public void setValidationMessages(Map<String, List<ValidationMessage>> validationMessages) {
    this.validationMessages = validationMessages;
  }
}