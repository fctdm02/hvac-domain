package com.djt.hvac.domain.model.common.validation;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;
import com.djt.hvac.domain.model.nodehierarchy.validation.PortfolioValidationResult;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = ValidationMessage.Builder.class)
@JsonIgnoreProperties(ignoreUnknown=true)
public final class ValidationMessage implements Comparable<ValidationMessage> {
  
  public static final String NONE = "NONE";
  
  private final IssueType issueType;
  private final String details;
  private final String entityType;
  private final String naturalIdentity;
  private final String remediationDescription;
  private final RemediationStrategy remediationStrategy;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (ValidationMessage validationMessage) {
    return new Builder(validationMessage);
  }

  private ValidationMessage (Builder builder) {
    this.issueType = builder.issueType;  
    this.details = builder.details;
    this.entityType = builder.entityType;
    this.naturalIdentity = builder.naturalIdentity;
    this.remediationDescription = builder.remediationDescription;
    this.remediationStrategy = builder.remediationStrategy;
  }

  public Integer getIssueId() {
    return issueType.getId();
  }

  public String getIssue() {
    return issueType.getName();
  }

  public String getDetails() {
    return details;
  }

  public String getEntityType() {
    return entityType;
  }

  public String getNaturalIdentity() {
    return naturalIdentity;
  }

  public String getRemediationDescription() {
    return remediationDescription;
  }

  @JsonIgnore
  public RemediationStrategy getRemediationStrategy() {
    return remediationStrategy;
  }

  @Override
  public int hashCode() {
    return toString().hashCode();
  }

  @Override
  public boolean equals(Object that) {
    
    if (that == null) {
      return false;
    }

    if (that == this) {
      return true;
    }

    if (!this.getClass().equals(that.getClass())) {
      return false;
    }
    
    return this.toString().equals(that.toString());
  }  
  
  public int compareTo(ValidationMessage that) {
    
    int compareTo = this.issueType.compareTo(that.issueType);
    if (compareTo == 0) {
      compareTo = this.naturalIdentity.compareTo(that.naturalIdentity);
    }
    return compareTo;
  }

  @Override
  public String toString() {
    
    return new StringBuilder()
        .append("ValidationMessage [issueType=")
        .append(issueType)
        .append(", details=")
        .append(details)
        .append(", entityType=")
        .append(entityType)
        .append(", naturalIdentity=")
        .append(naturalIdentity)
        .append(", remediationDescription=")
        .append(remediationDescription)
        .append(", remediationStrategy=")
        .append(remediationStrategy)
        .append("]")
        .toString();
  }

  public static List<ValidationMessage> filterByIssueType(
      List<ValidationMessage> validationMessages,
      Set<IssueType> issueTypes) {
    
    if (issueTypes != null && !issueTypes.isEmpty()) {
      
      List<ValidationMessage> filteredValidationMessages = new ArrayList<>();
      Iterator<ValidationMessage> validationMessageIterator = validationMessages.iterator();
      while (validationMessageIterator.hasNext()) {
        
        ValidationMessage validationMessage = validationMessageIterator.next();
        if (issueTypes.contains(IssueType.get(validationMessage.getIssueId()))) {
          
          filteredValidationMessages.add(validationMessage);
        }
      }
      return filteredValidationMessages;
    }
    return validationMessages;
  }

  public static PortfolioValidationResult buildPortfolioValidationResult(
      PortfolioEntity portfolio,
      List<ValidationMessage> validationMessages) {
   
    return ValidationMessage.buildPortfolioValidationResult(portfolio, validationMessages, null);
  }
  
  public static PortfolioValidationResult buildPortfolioValidationResult(
      PortfolioEntity portfolio,
      List<ValidationMessage> validationMessages,
      Set<IssueType> issueTypes) {
    
    return new PortfolioValidationResult(portfolio, filterByIssueType(validationMessages, issueTypes));
  }
  
  public static PortfolioValidationResult buildSummaryResult(PortfolioValidationResult result) {
    
    Iterator<List<ValidationMessage>> iterator = result
        .getValidationMessages()
        .values()
        .iterator();
    
    boolean isSummary = false;
    List<ValidationMessage> summaryList = new ArrayList<>();
    while (iterator.hasNext()) {
      
      List<ValidationMessage> list = iterator.next();
      if (list.size() >= 10) {
        
        isSummary = true;
        for (int i=0; i < 10 && i < list.size(); i++) {
          summaryList.add(list.get(i));
        }
      } else {
        summaryList.addAll(list);
      }
    }
    
    return new PortfolioValidationResult(
        isSummary,
        result.getCustomerId(),
        result.getPortfolioName(),
        result.getNumProcessedBuildings(),
        result.getNumProcessedSubBuildings(),
        result.getNumProcessedPlants(),
        result.getNumProcessedLoops(),
        result.getNumProcessedFloors(),
        result.getNumProcessedEquipment(),
        result.getNumProcessedMappablePoints(),
        result.getNumProcessedCustomAsyncComputedPoints(),
        result.getNumProcessedScheduledAsyncComputedPoints(),
        result.getNumProcessedAsyncComputedPoints(),
        result.getNumProcessedAdFunctionInstanceCandidates(),
        result.getNumProcessedAdFunctionInstances(),
        result.getNumProcessedReportInstances(),
        result.getTotalNumberOfValidationMessages(),
        summaryList);
  }

  public static Set<IssueType> extractPhaseOneIssueTypes() {
    return extractPhaseOneIssueTypes(null);
  }
  
  public static Set<IssueType> extractPhaseOneIssueTypes(Set<IssueType> issueTypesFilter) {
    
    // Phase One: Points/Equipment
    Set<IssueType> issueTypes = new HashSet<>();
    if (issueTypesFilter == null || issueTypesFilter.isEmpty() || issueTypesFilter.contains(IssueType.POINT_HAS_TEMPLATE_YET_NON_EQUIPMENT_PARENT)) {
      issueTypes.add(IssueType.POINT_HAS_TEMPLATE_YET_NON_EQUIPMENT_PARENT);  
    }
    if (issueTypesFilter == null || issueTypesFilter.isEmpty() || issueTypesFilter.contains(IssueType.POINT_HAS_TEMPLATE_YET_PARENT_EQUIPMENT_NO_TYPE)) {
      issueTypes.add(IssueType.POINT_HAS_TEMPLATE_YET_PARENT_EQUIPMENT_NO_TYPE);  
    }
    if (issueTypesFilter == null || issueTypesFilter.isEmpty() || issueTypesFilter.contains(IssueType.POINT_HAS_TEMPLATE_THAT_IS_INVALID_FOR_PARENT_EQUIPMENT_TYPE)) {
      issueTypes.add(IssueType.POINT_HAS_TEMPLATE_THAT_IS_INVALID_FOR_PARENT_EQUIPMENT_TYPE);  
    }
    if (issueTypesFilter == null || issueTypesFilter.isEmpty() || issueTypesFilter.contains(IssueType.POINT_HAS_MISMATCH_BETWEEN_TAGS_AND_TEMPLATE_TAGS)) {
      issueTypes.add(IssueType.POINT_HAS_MISMATCH_BETWEEN_TAGS_AND_TEMPLATE_TAGS);  
    }
    if (issueTypesFilter == null || issueTypesFilter.isEmpty() || issueTypesFilter.contains(IssueType.POINT_HAS_TAGS_BUT_NO_TEMPLATE)) {
      issueTypes.add(IssueType.POINT_HAS_TAGS_BUT_NO_TEMPLATE);  
    }
    if (issueTypesFilter == null || issueTypesFilter.isEmpty() || issueTypesFilter.contains(IssueType.POINT_HAS_DELETED_RAW_POINT)) {
      issueTypes.add(IssueType.POINT_HAS_DELETED_RAW_POINT);  
    }
    if (issueTypesFilter == null || issueTypesFilter.isEmpty() || issueTypesFilter.contains(IssueType.POINT_HAS_IGNORED_RAW_POINT)) {
      issueTypes.add(IssueType.POINT_HAS_IGNORED_RAW_POINT);  
    }
    if (issueTypesFilter == null || issueTypesFilter.isEmpty() || issueTypesFilter.contains(IssueType.EQUIPMENT_HAS_PARENT_EQUIPMENT_FROM_ANOTHER_BUILDING)) {
      issueTypes.add(IssueType.EQUIPMENT_HAS_PARENT_EQUIPMENT_FROM_ANOTHER_BUILDING);  
    }
    if (issueTypesFilter == null || issueTypesFilter.isEmpty() || issueTypesFilter.contains(IssueType.CUSTOM_POINT_HAS_INVALID_FORMULA)) {
      issueTypes.add(IssueType.CUSTOM_POINT_HAS_INVALID_FORMULA);  
    }
    if (issueTypesFilter == null || issueTypesFilter.isEmpty() || issueTypesFilter.contains(IssueType.POINT_HAS_DEPRECATED_TEMPLATE)) {
      issueTypes.add(IssueType.POINT_HAS_DEPRECATED_TEMPLATE);  
    }
    
    return issueTypes;
  }

  public static boolean hasPhaseOneIssueTypes(Set<IssueType> issueTypesFilter) {
    
    // Phase One: Points/Equipment
    boolean hasPhaseOneIssueTypes = false;
    if (issueTypesFilter != null 
        && !issueTypesFilter.isEmpty()
        && (issueTypesFilter.contains(IssueType.POINT_HAS_TEMPLATE_YET_NON_EQUIPMENT_PARENT) // 1
          || issueTypesFilter.contains(IssueType.POINT_HAS_TEMPLATE_YET_PARENT_EQUIPMENT_NO_TYPE) // 2
          || issueTypesFilter.contains(IssueType.POINT_HAS_TEMPLATE_THAT_IS_INVALID_FOR_PARENT_EQUIPMENT_TYPE) // 3
          || issueTypesFilter.contains(IssueType.POINT_HAS_MISMATCH_BETWEEN_TAGS_AND_TEMPLATE_TAGS) // 4
          || issueTypesFilter.contains(IssueType.POINT_HAS_TAGS_BUT_NO_TEMPLATE) // 5
          || issueTypesFilter.contains(IssueType.POINT_HAS_DELETED_RAW_POINT) // 23
          || issueTypesFilter.contains(IssueType.POINT_HAS_IGNORED_RAW_POINT) // 24
          || issueTypesFilter.contains(IssueType.EQUIPMENT_HAS_PARENT_EQUIPMENT_FROM_ANOTHER_BUILDING) // 25
          || issueTypesFilter.contains(IssueType.CUSTOM_POINT_HAS_INVALID_FORMULA) // 28
          || issueTypesFilter.contains(IssueType.POINT_HAS_DEPRECATED_TEMPLATE))) { // 30
        
      hasPhaseOneIssueTypes = true;
    }
    return hasPhaseOneIssueTypes;
  } 
  
  public static Set<IssueType> extractPhaseTwoIssueTypes() {
    return extractPhaseTwoIssueTypes(null);
  }

  public static Set<IssueType> extractPhaseTwoIssueTypes(Set<IssueType> issueTypesFilter) {
    
    // Phase Two: AD Function Instance/Instance Candidates
    Set<IssueType> issueTypes = new HashSet<>();
    if (issueTypesFilter == null || issueTypesFilter.isEmpty() || issueTypesFilter.contains(IssueType.AD_FUNCTION_EQUIPMENT_DOES_NOT_HAVE_TYPE)) {
      issueTypes.add(IssueType.AD_FUNCTION_EQUIPMENT_DOES_NOT_HAVE_TYPE);  
    }
    if (issueTypesFilter == null || issueTypesFilter.isEmpty() || issueTypesFilter.contains(IssueType.AD_FUNCTION_EQUIPMENT_HAS_INVALID_EQUIPMENT_TYPE)) {
      issueTypes.add(IssueType.AD_FUNCTION_EQUIPMENT_HAS_INVALID_EQUIPMENT_TYPE);  
    }
    if (issueTypesFilter == null || issueTypesFilter.isEmpty() || issueTypesFilter.contains(IssueType.AD_FUNCTION_EQUIPMENT_HAS_TOO_MANY_BOUND_POINTS_FOR_SCALAR_INPUT)) {
      issueTypes.add(IssueType.AD_FUNCTION_EQUIPMENT_HAS_TOO_MANY_BOUND_POINTS_FOR_SCALAR_INPUT);  
    }
    if (issueTypesFilter == null || issueTypesFilter.isEmpty() || issueTypesFilter.contains(IssueType.AD_FUNCTION_EQUIPMENT_DOESNT_HAVE_ANY_BOUND_POINTS_FOR_REQUIRED_INPUT)) {
      issueTypes.add(IssueType.AD_FUNCTION_EQUIPMENT_DOESNT_HAVE_ANY_BOUND_POINTS_FOR_REQUIRED_INPUT);  
    }
    if (issueTypesFilter == null || issueTypesFilter.isEmpty() || issueTypesFilter.contains(IssueType.AD_FUNCTION_INPUT_POINT_HAS_INVALID_TAGS_FOR_OPTIONAL_SCALAR_INPUT)) {
      issueTypes.add(IssueType.AD_FUNCTION_INPUT_POINT_HAS_INVALID_TAGS_FOR_OPTIONAL_SCALAR_INPUT);  
    }
    if (issueTypesFilter == null || issueTypesFilter.isEmpty() || issueTypesFilter.contains(IssueType.AD_FUNCTION_INPUT_POINT_HAS_INVALID_TAGS_FOR_REQUIRED_SCALAR_INPUT)) {
      issueTypes.add(IssueType.AD_FUNCTION_INPUT_POINT_HAS_INVALID_TAGS_FOR_REQUIRED_SCALAR_INPUT);  
    }
    if (issueTypesFilter == null || issueTypesFilter.isEmpty() || issueTypesFilter.contains(IssueType.AD_FUNCTION_INPUT_POINT_HAS_INVALID_TAGS_FOR_OPTIONAL_ARRAY_INPUT)) {
      issueTypes.add(IssueType.AD_FUNCTION_INPUT_POINT_HAS_INVALID_TAGS_FOR_OPTIONAL_ARRAY_INPUT);  
    }
    if (issueTypesFilter == null || issueTypesFilter.isEmpty() || issueTypesFilter.contains(IssueType.AD_FUNCTION_INPUT_POINT_HAS_INVALID_TAGS_FOR_REQUIRED_ARRAY_INPUT_COUNT_EQ_1)) {
      issueTypes.add(IssueType.AD_FUNCTION_INPUT_POINT_HAS_INVALID_TAGS_FOR_REQUIRED_ARRAY_INPUT_COUNT_EQ_1);  
    }
    if (issueTypesFilter == null || issueTypesFilter.isEmpty() || issueTypesFilter.contains(IssueType.AD_FUNCTION_INPUT_POINT_HAS_INVALID_TAGS_FOR_REQUIRED_ARRAY_INPUT_COUNT_GT_1)) {
      issueTypes.add(IssueType.AD_FUNCTION_INPUT_POINT_HAS_INVALID_TAGS_FOR_REQUIRED_ARRAY_INPUT_COUNT_GT_1);  
    }
    if (issueTypesFilter == null || issueTypesFilter.isEmpty() || issueTypesFilter.contains(IssueType.NEW_QUALIFYING_INPUT_POINT_FOR_AD_FUNCTION_AVAILABLE)) {
      issueTypes.add(IssueType.NEW_QUALIFYING_INPUT_POINT_FOR_AD_FUNCTION_AVAILABLE);  
    }
    if (issueTypesFilter == null || issueTypesFilter.isEmpty() || issueTypesFilter.contains(IssueType.AD_FUNCTION_EQUIPMENT_HAS_ZERO_BOUND_POINTS)) {
      issueTypes.add(IssueType.AD_FUNCTION_EQUIPMENT_HAS_ZERO_BOUND_POINTS);  
    }
    if (issueTypesFilter == null || issueTypesFilter.isEmpty() || issueTypesFilter.contains(IssueType.AD_FUNCTION_EQUIPMENT_HAS_INVALID_EQUIPMENT_TYPE)) {
      issueTypes.add(IssueType.AD_FUNCTION_EQUIPMENT_HAS_INVALID_EQUIPMENT_TYPE);  
    }
    if (issueTypesFilter == null || issueTypesFilter.isEmpty() || issueTypesFilter.contains(IssueType.AD_FUNCTION_EQUIPMENT_DOESNT_MATCH_NODE_FILTER_EXPRESSION)) {
      issueTypes.add(IssueType.AD_FUNCTION_EQUIPMENT_DOESNT_MATCH_NODE_FILTER_EXPRESSION);  
    }
    if (issueTypesFilter == null || issueTypesFilter.isEmpty() || issueTypesFilter.contains(IssueType.AD_FUNCTION_INPUT_POINT_IS_NO_LONGER_ELIGIBLE)) {
      issueTypes.add(IssueType.AD_FUNCTION_INPUT_POINT_IS_NO_LONGER_ELIGIBLE);  
    }
    if (issueTypesFilter == null || issueTypesFilter.isEmpty() || issueTypesFilter.contains(IssueType.AD_FUNCTION_POINT_TUPLE_CONSTRAINT_EXPRESSION_FALSE)) {
      issueTypes.add(IssueType.AD_FUNCTION_POINT_TUPLE_CONSTRAINT_EXPRESSION_FALSE);  
    }
    if (issueTypesFilter == null || issueTypesFilter.isEmpty() || issueTypesFilter.contains(IssueType.AD_FUNCTION_MISSING_INPUT_CONSTANT)) {
      issueTypes.add(IssueType.AD_FUNCTION_MISSING_INPUT_CONSTANT);  
    }
    if (issueTypesFilter == null || issueTypesFilter.isEmpty() || issueTypesFilter.contains(IssueType.VERSION_MISMATCH_MIGRATE_AD_FUNCTION_INSTANCE)) {
      issueTypes.add(IssueType.VERSION_MISMATCH_MIGRATE_AD_FUNCTION_INSTANCE);  
    }
    return issueTypes;
  }
  
  public static boolean hasPhaseTwoIssueTypes(Set<IssueType> issueTypesFilter) {
    
    // Phase Two: AD Function Instance/Instance Candidates
    boolean hasPhaseTwoIssueTypes = false;
    if (issueTypesFilter != null 
        && !issueTypesFilter.isEmpty()
        && (issueTypesFilter.contains(IssueType.AD_FUNCTION_EQUIPMENT_DOES_NOT_HAVE_TYPE) // 9  
            || issueTypesFilter.contains(IssueType.AD_FUNCTION_EQUIPMENT_HAS_INVALID_EQUIPMENT_TYPE) // 10  
            || issueTypesFilter.contains(IssueType.AD_FUNCTION_EQUIPMENT_HAS_TOO_MANY_BOUND_POINTS_FOR_SCALAR_INPUT) // 11  
            || issueTypesFilter.contains(IssueType.AD_FUNCTION_EQUIPMENT_DOESNT_HAVE_ANY_BOUND_POINTS_FOR_REQUIRED_INPUT) // 12  
            || issueTypesFilter.contains(IssueType.AD_FUNCTION_INPUT_POINT_HAS_INVALID_TAGS_FOR_OPTIONAL_SCALAR_INPUT) // 13  
            || issueTypesFilter.contains(IssueType.AD_FUNCTION_INPUT_POINT_HAS_INVALID_TAGS_FOR_REQUIRED_SCALAR_INPUT) // 14  
            || issueTypesFilter.contains(IssueType.AD_FUNCTION_INPUT_POINT_HAS_INVALID_TAGS_FOR_OPTIONAL_ARRAY_INPUT) // 15  
            || issueTypesFilter.contains(IssueType.AD_FUNCTION_INPUT_POINT_HAS_INVALID_TAGS_FOR_REQUIRED_ARRAY_INPUT_COUNT_EQ_1) // 16  
            || issueTypesFilter.contains(IssueType.AD_FUNCTION_INPUT_POINT_HAS_INVALID_TAGS_FOR_REQUIRED_ARRAY_INPUT_COUNT_GT_1) // 17  
            || issueTypesFilter.contains(IssueType.AD_FUNCTION_EQUIPMENT_HAS_ZERO_BOUND_POINTS) // 20
            || issueTypesFilter.contains(IssueType.NEW_QUALIFYING_INPUT_POINT_FOR_AD_FUNCTION_AVAILABLE) // 18
            || issueTypesFilter.contains(IssueType.AD_FUNCTION_EQUIPMENT_DOESNT_MATCH_NODE_FILTER_EXPRESSION) // 21  
            || issueTypesFilter.contains(IssueType.AD_FUNCTION_INPUT_POINT_IS_NO_LONGER_ELIGIBLE) // 22  
            || issueTypesFilter.contains(IssueType.AD_FUNCTION_POINT_TUPLE_CONSTRAINT_EXPRESSION_FALSE) // 26  
            || issueTypesFilter.contains(IssueType.AD_FUNCTION_MISSING_INPUT_CONSTANT) // 27
            || issueTypesFilter.contains(IssueType.VERSION_MISMATCH_MIGRATE_AD_FUNCTION_INSTANCE))) { // 29
        
      hasPhaseTwoIssueTypes = true;
    }
    return hasPhaseTwoIssueTypes;
  } 
  
  @JsonPOJOBuilder
  @JsonIgnoreProperties(value = {"issue"})
  public static class Builder {
    private IssueType issueType;
    private String details;
    private String entityType;
    private String naturalIdentity;
    private String remediationDescription;
    private RemediationStrategy remediationStrategy;

    private Builder() {}

    private Builder(ValidationMessage validationMessage) {
      requireNonNull(validationMessage, "validationMessage cannot be null");
      this.issueType = validationMessage.issueType;
      this.details = validationMessage.details;
      this.entityType = validationMessage.entityType;
      this.naturalIdentity = validationMessage.naturalIdentity;
      this.remediationDescription = validationMessage.remediationDescription;
      this.remediationStrategy = validationMessage.remediationStrategy;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withIssueId(Integer issueId) {
      requireNonNull(issueId, "issueId cannot be null");
      this.issueType = IssueType.get(issueId);
      return this;
    }

    @JsonIgnore
    public Builder withIssueType(IssueType issueType) {
      requireNonNull(issueType, "issueType cannot be null");
      this.issueType = issueType;
      return this;
    }

    public Builder withDetails(String details) {
      requireNonNull(details, "details cannot be null");
      this.details = details;
      return this;
    }

    public Builder withEntityType(String entityType) {
      requireNonNull(entityType, "entityType cannot be null");
      this.entityType = entityType
          .replace("Entity", "")
          .replace("Abstract", "");
      return this;
    }

    public Builder withNaturalIdentity(String naturalIdentity) {
      requireNonNull(naturalIdentity, "naturalIdentity cannot be null");
      this.naturalIdentity = naturalIdentity;
      return this;
    }

    public Builder withRemediationDescription(String remediationDescription) {
      requireNonNull(remediationDescription, "remediationDescription cannot be null");
      this.remediationDescription = remediationDescription;
      return this;
    }

    public Builder withRemediationStrategy(RemediationStrategy remediationStrategy) {
      this.remediationStrategy = remediationStrategy;
      return this;
    }
    
    public ValidationMessage build() {
      requireNonNull(issueType, "issueType cannot be null");
      requireNonNull(details, "details cannot be null");
      requireNonNull(entityType, "entityType cannot be null");
      requireNonNull(naturalIdentity, "naturalIdentity cannot be null");
      requireNonNull(remediationDescription, "remediationDescription cannot be null");
      return new ValidationMessage(this);
    }
  }
}
