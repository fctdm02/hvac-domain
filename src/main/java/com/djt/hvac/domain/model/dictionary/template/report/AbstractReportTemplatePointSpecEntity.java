//@formatter:off
package com.djt.hvac.domain.model.dictionary.template.report;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Set;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.AbstractPersistentEntity;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;

public abstract class AbstractReportTemplatePointSpecEntity extends AbstractPersistentEntity {
  private static final long serialVersionUID = 1L;
  
  private final ReportTemplateEquipmentSpecEntity parentEquipmentSpec;
  private final String name;
  private final boolean isRequired;
  private final boolean isArray;
  private final String currentObjectExpression;
  private final String errorMessage;
  private final Integer requiredDataTypeId;

  public AbstractReportTemplatePointSpecEntity(
      Integer persistentIdentity,
      ReportTemplateEquipmentSpecEntity parentEquipmentSpec,
      String name,
      boolean isRequired,
      boolean isArray,
      String currentObjectExpression,
      String errorMessage,
      Integer requiredDataTypeId) {
    super(persistentIdentity);
    requireNonNull(parentEquipmentSpec, "parentEquipmentSpec cannot be null");
    requireNonNull(name, "name cannot be null");
    requireNonNull(isRequired, "isRequired cannot be null");
    requireNonNull(isArray, "isArray cannot be null");
    requireNonNull(errorMessage, "errorMessage cannot be null");
    this.parentEquipmentSpec = parentEquipmentSpec;
    this.name = name;
    this.isRequired = isRequired;
    this.isArray = isArray;
    this.currentObjectExpression = currentObjectExpression;
    this.errorMessage = errorMessage;
    this.requiredDataTypeId = requiredDataTypeId;
  }

  public ReportTemplateEquipmentSpecEntity getParentEquipmentSpec() {
    return parentEquipmentSpec;
  }

  public String getName() {
    return name;
  }

  public boolean isRequired() {
    return isRequired;
  }

  public boolean isArray() {
    return isArray;
  }

  public String getCurrentObjectExpression() {
    return currentObjectExpression;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public Integer getRequiredDataTypeId() {
    return requiredDataTypeId;
  }

  @Override
  public String getNaturalIdentity() {

    if (_naturalIdentity == null) {

      _naturalIdentity = new StringBuilder()
          .append(parentEquipmentSpec.getNaturalIdentity())
          .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
          .append(name)
          .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
          .append(this.currentObjectExpression)
          .toString();
    }
    return _naturalIdentity;
  }

  @Override
  public void validate(
      Set<IssueType> issueTypes,
      List<ValidationMessage> validationMessages,
      boolean remediate) {
  }

  public int compareTo(AbstractEntity obj) {
    
    int compareTo = 0;
    if (obj instanceof AbstractReportTemplatePointSpecEntity) {
      
      AbstractReportTemplatePointSpecEntity that = (AbstractReportTemplatePointSpecEntity)obj;
      
      if (!this.isRequired && !that.isRequired) {
        compareTo = 0;
      } else if (!this.isRequired && that.isRequired) {
        compareTo = -1;
      } else if (this.isRequired && !that.isRequired) {
        compareTo = 1;
      } else {
        compareTo = 0;
      }
    } else {
      throw new IllegalStateException(obj 
          + " is not an instance of AbstractReportTemplatePointSpecEntity, rather, it is a: " 
          + obj.getClass().getSimpleName());
    }

    if (compareTo == 0) {
      compareTo = this.getNaturalIdentity().compareTo(obj.getNaturalIdentity());
    }
    
    return compareTo;
  }
}
//@formatter:on