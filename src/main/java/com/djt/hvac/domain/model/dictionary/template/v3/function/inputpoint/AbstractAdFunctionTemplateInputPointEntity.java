//@formatter:off
package com.djt.hvac.domain.model.dictionary.template.v3.function.inputpoint;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.AbstractPersistentEntity;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.dictionary.TagEntity;
import com.djt.hvac.domain.model.dictionary.UnitEntity;

public abstract class AbstractAdFunctionTemplateInputPointEntity extends AbstractPersistentEntity {
  private static final long serialVersionUID = 1L;
  
  private final AdFunctionTemplateInputPointGroupEntity parentInputPointGroup;
  private final Integer sequenceNumber;
  private final String name;
  private final String description;
  private final UnitEntity unit;
  private final String currentObjectExpression;
  private final Boolean isArray;
  private final Set<TagEntity> tags;
  private transient Set<String> _normalizedTagsAsSet;
  private transient String _normalizedTags;
  
  public AbstractAdFunctionTemplateInputPointEntity(
      Integer persistentIdentity,
      AdFunctionTemplateInputPointGroupEntity parentInputPointGroup,
      String name,
      String description,
      UnitEntity unit,
      String currentObjectExpression,
      Boolean isArray,
      Integer sequenceNumber,
      Set<TagEntity> tags) {
    super(persistentIdentity);
    requireNonNull(parentInputPointGroup, "parentInputPointGroup cannot be null");
    requireNonNull(sequenceNumber, "sequenceNumber cannot be null");
    requireNonNull(name, "name cannot be null");
    requireNonNull(description, "description cannot be null");
    requireNonNull(unit, "unit cannot be null");
    requireNonNull(isArray, "isArray cannot be null");
    requireNonNull(tags, "tags cannot be null");
    this.parentInputPointGroup = parentInputPointGroup;
    this.sequenceNumber = sequenceNumber;
    this.name = name;
    this.description = description;
    this.unit = unit;
    this.currentObjectExpression = currentObjectExpression;
    this.isArray = isArray;
    this.tags = tags;
  }

  public AdFunctionTemplateInputPointGroupEntity getParentInputPointGroup() {
    return parentInputPointGroup;
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

  public UnitEntity getUnit() {
    return unit;
  }
  
  public String getCurrentObjectExpression() {
    return currentObjectExpression;
  }

  public Boolean getIsArray() {
    return isArray;
  }

  public Set<TagEntity> getTags() {
    return tags;
  }
  
  public String getNaturalIdentity() {
    
    return new StringBuilder()
        .append(this.parentInputPointGroup.getNaturalIdentity())
        .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
        .append(this.name)
        .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
        .append(this.currentObjectExpression)
        .toString();
  }
  
  public int compareTo(AbstractEntity that) {

    return this.sequenceNumber.compareTo(((AbstractAdFunctionTemplateInputPointEntity)that).sequenceNumber);
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
}
//@formatter:on