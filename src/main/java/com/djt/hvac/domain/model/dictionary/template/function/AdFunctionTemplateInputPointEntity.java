package com.djt.hvac.domain.model.dictionary.template.function;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.AbstractPersistentEntity;
import com.djt.hvac.domain.model.common.dsl.currentobject.CurrentObjectExpressionParser;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.SimpleValidationMessage;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.common.validation.SimpleValidationMessage.MessageType;
import com.djt.hvac.domain.model.dictionary.TagEntity;

public class AdFunctionTemplateInputPointEntity extends AbstractPersistentEntity {
  private static final long serialVersionUID = 1L;
  
  private final AbstractAdFunctionTemplateEntity parentRuleTemplate;
  private final Integer sequenceNumber;
  private final String name;
  private final String description;
  private final String currentObjectExpression;
  private final Boolean isRequired;
  private final Boolean isArray;
  private final Set<TagEntity> tags;
  private transient Set<String> _normalizedTagsAsSet;
  private transient String _normalizedTags;
  
  public AdFunctionTemplateInputPointEntity(
      Integer persistentIdentity,
      AbstractAdFunctionTemplateEntity parentRuleTemplate,
      String name,
      String description,
      String currentObjectExpression,
      Boolean isRequired,
      Boolean isArray,
      Integer sequenceNumber,
      Set<TagEntity> tags) {
    super(persistentIdentity);
    requireNonNull(parentRuleTemplate, "parentRuleTemplate cannot be null");
    requireNonNull(sequenceNumber, "sequenceNumber cannot be null");
    requireNonNull(name, "name cannot be null");
    requireNonNull(description, "description cannot be null");
    requireNonNull(isRequired, "isRequired cannot be null");
    requireNonNull(isArray, "isArray cannot be null");
    requireNonNull(tags, "tags cannot be null");
    this.parentRuleTemplate = parentRuleTemplate;
    this.sequenceNumber = sequenceNumber;
    this.name = name;
    this.description = description;
    this.isRequired = isRequired;
    this.isArray = isArray;
    this.tags = tags;

    // Validate the current object expression.
    try {
      if (currentObjectExpression != null && !currentObjectExpression.trim().isEmpty()) {
        
        if (currentObjectExpression.equals("childEquipment(VAV)")) {
          currentObjectExpression = "childEquipment(tags=vav)"; 
        } else if (currentObjectExpression.equals("childEquipment")) {
          currentObjectExpression = "childEquipment(tags=vav)";
        }
        
        CurrentObjectExpressionParser.parse(currentObjectExpression);  
      }
    } catch (Exception e) {
      throw new IllegalArgumentException(getClassAndNaturalIdentity()
          + ": Invalid currentObjectExpression: ["
          + currentObjectExpression
          + "], error: ["
          + e.getMessage()
          + "]", e);
    }
    this.currentObjectExpression = currentObjectExpression;
  }
  
  public AbstractAdFunctionTemplateEntity getParentRuleTemplate() {
    return parentRuleTemplate;
  }

  public Integer getSequenceNumber() {
    return sequenceNumber;
  }  
  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getCurrentObjectExpression() {
    return currentObjectExpression;
  }
  
  public String getCurrentObjectExpressionNullIfNotExists() {
    if (currentObjectExpression == null || currentObjectExpression.trim().equals("")) {
      return "NULL";
    }
    return currentObjectExpression;
  }

  public Boolean getIsRequired() {
    return isRequired;
  }

  public Boolean getIsArray() {
    return isArray;
  }

  public Set<TagEntity> getTags() {
    return tags;
  }
  
  public String getNaturalIdentity() {
    
    return new StringBuilder()
        .append(parentRuleTemplate.getNaturalIdentity())
        .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
        .append(sequenceNumber)
        
        // TODO: TDM: The following is because there isn't a unique constraint on the table for 
        // rule template id FK + sequence number (and there are some input points with duplicate
        // sequence numbers, so we need to give uniqueness by adding the input point's name.
        .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
        .append(this.name)
        
        .toString();
  }
  
  public int compareTo(AbstractEntity that) {

    return this.sequenceNumber.compareTo(((AdFunctionTemplateInputPointEntity) that).sequenceNumber);
  } 
  
  @Override
  public void validate(
      Set<IssueType> issueTypes,
      List<ValidationMessage> validationMessages, 
      boolean remediate) {
  }

  public Set<String> getNormalizedTagsAsSet() {
    
    if (_normalizedTagsAsSet == null) {
      _normalizedTagsAsSet = new TreeSet<>();
      if (tags != null) {
        Iterator<TagEntity> iterator = tags.iterator();
        while (iterator.hasNext()) {
          _normalizedTagsAsSet.add(iterator.next().getName());
        }
      }
    }
    return _normalizedTagsAsSet;
  }
  
  public String getNormalizedTags() {
    
    if (_normalizedTags == null) {
      if (tags == null) {
        _normalizedTags = "";
      } else {
        Set<String> set = getNormalizedTagsAsSet();
        _normalizedTags = set.toString().replaceAll(", ",  ",").replace("[", "").replace("]", "").replace("\"", "");  
      }
    }
    return _normalizedTags;
  }
  
  public List<String> getNormalizedTagsAsList() {
    
    List<String> list = new ArrayList<>();
    list.addAll(getNormalizedTagsAsSet());
    return list;
  }
  
  @Override
  public void validateSimple(List<SimpleValidationMessage> simpleValidationMessages) {
    
    // Validate that if an energy exchange type is given, that the wildcard (*) tag is
    // also present, which is needed in case there exists equipment metadata tags.
    if (currentObjectExpression != null 
        && !currentObjectExpression.trim().isEmpty() 
        && currentObjectExpression.contains("tags=") 
        && !currentObjectExpression.contains("|*")) {

      simpleValidationMessages.add(new SimpleValidationMessage(
          MessageType.ERROR,
          getNaturalIdentity(),
          "currentObjectExpression",
          getParentRuleTemplate().getFaultOrReferenceNumber()
              + ": currentObjectExpression: ["
              + currentObjectExpression
              + "] specifies equipment type 'tags', but does not contain the '|*' wildcard"));
    }    
  }
  
  public String getSignature() {
    
    return new StringBuilder()
        .append("sequenceNumber=")
        .append(sequenceNumber)
        .append(", name=")
        .append(name)
        .append(", description=")
        .append(description)
        .append(", currentObjectExpression=")
        .append(getCurrentObjectExpressionNullIfNotExists())
        .append(", isRequired=")
        .append(isRequired)
        .append(", isArray=")
        .append(isArray)
        .append(", tags=")
        .append(tags)
        .toString();
  }  
}
