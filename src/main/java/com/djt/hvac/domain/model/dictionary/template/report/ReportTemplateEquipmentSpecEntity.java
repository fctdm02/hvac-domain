//@formatter:off
package com.djt.hvac.domain.model.dictionary.template.report;

import static java.util.Objects.requireNonNull;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.AbstractPersistentEntity;
import com.djt.hvac.domain.model.common.dsl.tagquery.TagQueryExpression;
import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.SimpleValidationMessage;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.common.validation.SimpleValidationMessage.MessageType;
import com.djt.hvac.domain.model.dictionary.TagEntity;
import com.djt.hvac.domain.model.dictionary.container.DictionaryContext;
import com.djt.hvac.domain.model.dictionary.container.NodeTagTemplatesContainer;
import com.djt.hvac.domain.model.dictionary.energyexchange.AbstractEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.enums.TagGroupType;
import com.djt.hvac.domain.model.dictionary.template.nodetag.PointTemplateEntity;

public class ReportTemplateEquipmentSpecEntity extends AbstractPersistentEntity {
  private static final long serialVersionUID = 1L;
  
  private final ReportTemplateEntity parentReportTemplate;
  private final AbstractEnergyExchangeTypeEntity energyExchangeType;
  private final String nodeFilterExpression;
  private final String nodeFilterErrorMessage;
  private final String tupleConstraintExpression;
  private final String tupleConstraintErrorMessage;
  private final Set<AbstractReportTemplatePointSpecEntity> pointSpecs = new TreeSet<>();
  
  public ReportTemplateEquipmentSpecEntity(
      Integer persistentIdentity,
      ReportTemplateEntity parentReportTemplate,
      AbstractEnergyExchangeTypeEntity energyExchangeType,
      String nodeFilterExpression,
      String nodeFilterErrorMessage,
      String tupleConstraintExpression,
      String tupleConstraintErrorMessage) {
    super(persistentIdentity);
    requireNonNull(parentReportTemplate, "parentReportTemplate cannot be null");
    requireNonNull(energyExchangeType, "energyExchangeType cannot be null");
    this.parentReportTemplate = parentReportTemplate;
    this.energyExchangeType = energyExchangeType;
    this.nodeFilterExpression = nodeFilterExpression;
    this.nodeFilterErrorMessage = nodeFilterErrorMessage;
    this.tupleConstraintExpression = tupleConstraintExpression;
    this.tupleConstraintErrorMessage = tupleConstraintErrorMessage;
  }
  
  public ReportTemplateEntity getParentReportTemplate() {
    return parentReportTemplate;
  }
  
  public AbstractEnergyExchangeTypeEntity getEnergyExchangeType() {
    return energyExchangeType;
  }

  public String getNodeFilterExpression() {
    return nodeFilterExpression;
  }

  public String getNodeFilterErrorMessage() {
    return nodeFilterErrorMessage;
  }

  public String getTupleConstraintExpression() {
    return tupleConstraintExpression;
  }

  public String getTupleConstraintErrorMessage() {
    return tupleConstraintErrorMessage;
  }
  
  public boolean addPointSpec(AbstractReportTemplatePointSpecEntity pointSpec) throws EntityAlreadyExistsException {
    return addChild(pointSpecs, pointSpec, this);
  }
  
  public Set<AbstractReportTemplatePointSpecEntity> getPointSpecs() {
    return pointSpecs;
  }
  
  public AbstractReportTemplatePointSpecEntity getPointSpec(Integer persistentIdentity) throws EntityDoesNotExistException {
    
    Iterator<AbstractReportTemplatePointSpecEntity> iterator = pointSpecs.iterator();
    while (iterator.hasNext()) {
      
      AbstractReportTemplatePointSpecEntity pointSpec = iterator.next();
      if (pointSpec.getPersistentIdentity().equals(persistentIdentity)) {
        
        return pointSpec;
      }
    }
    throw new EntityDoesNotExistException("Point spec with id: [" + persistentIdentity + "] does not exist.");
  }
  
  public AbstractReportTemplatePointSpecEntity getPointSpecByNameNullIfNotExists(String pointSpecName) {
    
    Iterator<AbstractReportTemplatePointSpecEntity> iterator = pointSpecs.iterator();
    while (iterator.hasNext()) {
      
      AbstractReportTemplatePointSpecEntity pointSpec = iterator.next();
      if (pointSpec.getName().equals(pointSpecName)) {
        
        return pointSpec;
      }
    }
    return null;
  }

  @Override
  public String getNaturalIdentity() {

    if (_naturalIdentity == null) {

      _naturalIdentity = new StringBuilder()
          .append(parentReportTemplate.getNaturalIdentity())
          .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
          .append(energyExchangeType.getNaturalIdentity())
          .toString();
    }
    return _naturalIdentity;
  }
  
  @Override
  public void validate(
      Set<IssueType> issueTypes,
      List<ValidationMessage> validationMessages, 
      boolean remediate) {
    
    // DO NOTHING
  }  
  
  @Override
  public void validateSimple(List<SimpleValidationMessage> simpleValidationMessages) {

    // Validate that all tags in the node filter expression actually exist.
    try {
      if (nodeFilterExpression != null && !nodeFilterExpression.trim().isEmpty()) {
        
        TagQueryExpression tqe = TagQueryExpression.parse(nodeFilterExpression);
        Set<String> tags = tqe.getTags();
        for (String tag: tags) {
          TagEntity t = DictionaryContext.getTagsContainer().getTagByNameNullIfNotExists(tag, TagGroupType.EQUIPMENT_METADATA);
          if (t == null) {
            
            simpleValidationMessages.add(new SimpleValidationMessage(
                MessageType.ERROR,
                getNaturalIdentity(),
                "nodeFilterExpression",
                getParentReportTemplate().getName()
                    + ": "
                    + getEnergyExchangeType()
                    + ": invalid nodeFilterExpression: ["
                    + nodeFilterExpression
                    + "] specifies equipment metadata tag name: ["
                    + tag 
                    + "] that does not exist"));
          }
        }
      }
    } catch (Exception e) {
      
      simpleValidationMessages.add(new SimpleValidationMessage(
          MessageType.ERROR,
          getNaturalIdentity(),
          "nodeFilterExpression",
          getParentReportTemplate().getName()
              + ": "
              + getEnergyExchangeType()
              + ": invalid nodeFilterExpression: ["
              + nodeFilterExpression
              + "] error: ["
              + e.getMessage() 
              + "]."));
    }
    
    // Validate that all points in the tuple constraint expression are present.
    try {
      if (tupleConstraintExpression != null && !tupleConstraintExpression.trim().isEmpty()) {
        
        TagQueryExpression tqe = TagQueryExpression.parse(tupleConstraintExpression);
        Set<String> pointNames = tqe.getTags();
        for (String pointName: pointNames) {

          AbstractReportTemplatePointSpecEntity ps = this.getPointSpecByNameNullIfNotExists(pointName);
          if (ps == null) {
            
            simpleValidationMessages.add(new SimpleValidationMessage(
                MessageType.ERROR,
                getNaturalIdentity(),
                "tupleConstraintExpression",
                getParentReportTemplate().getName()
                    + ": "
                    + getEnergyExchangeType()
                    + ": tupleConstraintExpression: ["
                    + tupleConstraintExpression
                    + "] specifies point spec name: ["
                    + pointName 
                    + "] that does not exist"));
          }
        }
      }
    } catch (Exception e) {
      
      simpleValidationMessages.add(new SimpleValidationMessage(
          MessageType.ERROR,
          getNaturalIdentity(),
          "tupleConstraintExpression",
          getParentReportTemplate().getName()
              + ": "
              + getEnergyExchangeType()
              + ": invalid tupleConstraintExpression: ["
              + tupleConstraintExpression
              + "] error: ["
              + e.getMessage() 
              + "]."));
    }
    
    if (energyExchangeType == null) {

      simpleValidationMessages.add(new SimpleValidationMessage(
          MessageType.ERROR,
          this.getNaturalIdentity(),
          "energyExchangeType",
          "Must be specified")); 
      
    } else {
      
      NodeTagTemplatesContainer nodeTagTemplatesContainer = DictionaryContext.getNodeTagTemplatesContainer();
      Set<PointTemplateEntity> pointTemplates = nodeTagTemplatesContainer.getEnergyExchangeLevelPointTemplatesForEnergyExchangeType(energyExchangeType);
      if (pointTemplates == null) {

        simpleValidationMessages.add(new SimpleValidationMessage(
            MessageType.ERROR,
            this.getNaturalIdentity(),
            "equipmentType",
            "Does not have any point templates defined")); 
      }
    }
    
    boolean hasAtLeastOneRequiredPointSpec = false;
    if (pointSpecs.isEmpty()) {

      simpleValidationMessages.add(new SimpleValidationMessage(
          MessageType.ERROR,
          this.getNaturalIdentity(),
          "pointSpecs",
          "Cannot be empty")); 
      
    } else {

      Iterator<AbstractReportTemplatePointSpecEntity> iterator = pointSpecs.iterator();
      while (iterator.hasNext()) {
        
        AbstractReportTemplatePointSpecEntity pointSpec = iterator.next();
        
        if (pointSpec.isRequired() && !hasAtLeastOneRequiredPointSpec) {
          
          hasAtLeastOneRequiredPointSpec = true; 
        }
        
        pointSpec.validateSimple(simpleValidationMessages);
      }
    }
    
    if (!hasAtLeastOneRequiredPointSpec && (tupleConstraintExpression == null || tupleConstraintExpression.isEmpty())) {
     
      simpleValidationMessages.add(new SimpleValidationMessage(
          MessageType.ERROR,
          this.getNaturalIdentity(),
          "tupleConstraintExpression",
          "Must be specified if no required points exist")); 
    }
  }
}
//@formatter:on