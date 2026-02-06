package com.djt.hvac.domain.model.dictionary.template.function;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.AbstractPersistentEntity;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.dictionary.TagEntity;
import com.djt.hvac.domain.model.dictionary.UnitEntity;
import com.djt.hvac.domain.model.dictionary.enums.DataType;
import com.djt.hvac.domain.model.dictionary.template.function.rule.AdRuleFunctionTemplateEntity;

public class AdFunctionTemplateOutputPointEntity extends AbstractPersistentEntity {
  private static final long serialVersionUID = 1L;
  
  public static final DataType DEFAULT_DATA_TYPE = DataType.BOOLEAN;
  public static final UnitEntity DEFAULT_UNIT_ENTITY = UnitEntity.EMPTY_UNIT;
  public static final String DEFAULT_RANGE = "{\"trueText\":\"On\",\"falseText\":\"Off\"}";
  public static final String DEFAULT_DESCRIPTION = "Anomaly Detected";
  
  private final AbstractAdFunctionTemplateEntity parentRuleTemplate;
  private final Integer sequenceNumber;
  private final String description;
  private final DataType dataType;
  private final UnitEntity unit;
  private final String range;
  private final Set<TagEntity> tags;
  private transient Set<String> _normalizedTagsAsSet;
  private transient String _normalizedTags;
  
  public AdFunctionTemplateOutputPointEntity(
      Integer persistentIdentity,
      AbstractAdFunctionTemplateEntity parentRuleTemplate,
      Integer sequenceNumber,
      String description,
      DataType dataType,
      UnitEntity unit,
      String range,
      Set<TagEntity> tags) {
    super(persistentIdentity);
    requireNonNull(parentRuleTemplate, "parentRuleTemplate cannot be null");
    requireNonNull(sequenceNumber, "sequenceNumber cannot be null");
    requireNonNull(description, "description cannot be null");
    requireNonNull(dataType, "dataType cannot be null");
    requireNonNull(unit, "unit cannot be null");
    if (parentRuleTemplate instanceof AdRuleFunctionTemplateEntity) {
      requireNonNull(range, "range cannot be null for rule function templates");  
    }
    this.parentRuleTemplate = parentRuleTemplate;
    this.sequenceNumber = sequenceNumber;
    this.description = description;
    this.dataType = dataType;
    this.unit = unit;
    this.range = range;
    this.tags = tags;
  }
  
  public AbstractAdFunctionTemplateEntity getParentRuleTemplate() {
    return parentRuleTemplate;
  }

  public Integer getSequenceNumber() {
    return sequenceNumber;
  }

  public String getDescription() {
    return description;
  }

  public DataType getDataType() {
    return dataType;
  }

  public UnitEntity getUnit() {
    return unit;
  }

  public String getRange() {
    return range;
  }
  
  public Set<TagEntity> getTags() {
    return tags;
  }
  
  public String getNaturalIdentity() {
    
    return new StringBuilder()
        .append(parentRuleTemplate.getNaturalIdentity())
        .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
        .append(sequenceNumber)
        .toString();
  }
  
  public int compareTo(AbstractEntity that) {

    return this.sequenceNumber.compareTo(((AdFunctionTemplateOutputPointEntity) that).sequenceNumber);
  }
  
  @Override
  public void validate(
      Set<IssueType> issueTypes,
      List<ValidationMessage> validationMessages, 
      boolean remediate) {
  }

  public Set<String> getNormalizedTagsAsSet() {
    
    if (_normalizedTagsAsSet == null) {
      _normalizedTagsAsSet = TagEntity.getTagNamesAsSet(tags);
    }
    return _normalizedTagsAsSet;
  }
  
  public String getNormalizedTags() {
    
    if (_normalizedTags == null) {
      _normalizedTags = TagEntity.getTagNamesAsString(tags);
    }
    return _normalizedTags;
  }
  
  public List<String> getNormalizedTagsAsList() {
    
    List<String> list = new ArrayList<>();
    list.addAll(getNormalizedTagsAsSet());
    return list;
  }  
}
