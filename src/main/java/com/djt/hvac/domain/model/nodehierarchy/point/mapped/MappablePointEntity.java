//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.point.mapped;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.RemediationStrategy;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.dictionary.TagEntity;
import com.djt.hvac.domain.model.dictionary.UnitEntity;
import com.djt.hvac.domain.model.dictionary.container.DictionaryContext;
import com.djt.hvac.domain.model.dictionary.energyexchange.EquipmentEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.enums.DataType;
import com.djt.hvac.domain.model.dictionary.enums.NodeSubType;
import com.djt.hvac.domain.model.dictionary.template.nodetag.AbstractNodeTagTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.nodetag.PointTemplateEntity;
import com.djt.hvac.domain.model.function.AdFunctionInstanceEligiblePoint;
import com.djt.hvac.domain.model.nodehierarchy.AbstractNodeEntity;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.EquipmentEntity;
import com.djt.hvac.domain.model.nodehierarchy.mapper.PortfolioDtoMapper;
import com.djt.hvac.domain.model.nodehierarchy.point.AbstractPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.custom.CustomPointFormulaVariableEligiblePoint;
import com.djt.hvac.domain.model.nodehierarchy.validation.HardDeleteNodeStrategyImpl;
import com.djt.hvac.domain.model.rawpoint.RawPointEntity;

public class MappablePointEntity extends AbstractPointEntity implements AdFunctionInstanceEligiblePoint, CustomPointFormulaVariableEligiblePoint {
  private static final long serialVersionUID = 1L;
  
  private RawPointEntity rawPoint;
  private Boolean isChangeOfValue;
  
  public MappablePointEntity() {}

  // For new instances (i.e. have not been persisted yet)
  public MappablePointEntity(
      AbstractNodeEntity parentNode,
      String name,
      String displayName,
      DataType dataType,
      UnitEntity unit,
      String range,
      RawPointEntity rawPoint) {
    super(
        parentNode,
        name,
        displayName,
        dataType,
        unit,
        range);
    
    if (rawPoint == null) {
      throw new IllegalArgumentException("rawPoint cannot be null.  parentNode: ["
          + parentNode.getNaturalIdentity()
          + "], child mappable point name: ["
          + name
          + "].");
    }
    this.rawPoint = rawPoint;
    
    if (dataType.equals(DataType.BOOLEAN) || dataType.equals(DataType.ENUM)) {
      this.isChangeOfValue = Boolean.TRUE;
    } else {
      this.isChangeOfValue = Boolean.FALSE;
    }
  }
  
  public MappablePointEntity(
      Integer persistentIdentity,
      AbstractNodeEntity parentNode,
      String name,
      String displayName,
      String createdAt,
      String updatedAt,
      Set<TagEntity> nodeTags,
      DataType dataType,
      UnitEntity unit,
      String range,
      PointTemplateEntity pointTemplate,
      String lastValue,
      Long lastValueTimestamp,
      RawPointEntity rawPoint,
      Boolean isChangeOfValue) {
    super(
        persistentIdentity,
        parentNode,
        name,
        displayName,
        createdAt,
        updatedAt,
        nodeTags,
        dataType,
        unit,
        range,
        pointTemplate,
        lastValue,
        lastValueTimestamp);
    
    if (rawPoint == null) {
      throw new IllegalArgumentException("rawPoint cannot be null.  parentNode: ["
          + parentNode.getNaturalIdentity()
          + "], child mappable point name: ["
          + name
          + "].");
    }
    this.rawPoint = rawPoint;
    
    if (isChangeOfValue != null) {
      this.isChangeOfValue = isChangeOfValue;
    } else if (dataType.equals(DataType.BOOLEAN) || dataType.equals(DataType.ENUM)) {
      this.isChangeOfValue = Boolean.TRUE;
    } else {
      this.isChangeOfValue = Boolean.FALSE;
    }
  }

  @Override
  public int calculateTotalMappedPointCount() {
    return 1;
  }
  
  @Override
  public AbstractNodeEntity duplicateNode(PortfolioEntity portfolio, AbstractNodeEntity parentNode, int duplicationIndex) {
    throw new IllegalStateException("duplicateNode() cannot be called on a leaf point node: [" + this + "].");
  }
  
  @Override
  public NodeSubType getNodeSubType() {
    return NodeSubType.MAPPABLE_POINT;
  }

  @Override
  public String getMetricId() {
    return this.rawPoint.getMetricId();
  }

  public RawPointEntity getRawPoint() {
    return this.rawPoint;
  }
  
  public Boolean getIsChangeOfValue() {
    return this.isChangeOfValue;
  }

  @Override
  public void validate(
      Set<IssueType> issueTypes,
      List<ValidationMessage> validationMessages,
      boolean remediate) {

    super.validate(issueTypes, validationMessages, remediate);
    
    Map<String, Object> entities = new LinkedHashMap<>();
    entities.put("point", this);

    if (rawPoint == null || rawPoint.getDeleted()) {

      RemediationStrategy remediationStrategy = HardDeleteNodeStrategyImpl.get();

      if (issueTypes.contains(IssueType.POINT_HAS_DELETED_RAW_POINT)) {
        validationMessages.add(ValidationMessage.builder()
            .withIssueType(IssueType.POINT_HAS_DELETED_RAW_POINT)
            .withDetails("Mappable Point: ["
                + getPersistentIdentity()
                + "] is associated with a raw point that has been deleted or non-existent.")
            .withEntityType(getClass().getSimpleName())
            .withNaturalIdentity(getNaturalIdentity())
            .withRemediationDescription("Hard delete mappable point")
            .withRemediationStrategy(remediationStrategy)
            .build());

        if (remediate) {
          remediationStrategy.remediate(entities);
        }
      }
      return;
    }

    if (rawPoint.getIgnored()) {

      RemediationStrategy remediationStrategy = HardDeleteNodeStrategyImpl.get();

      if (issueTypes.contains(IssueType.POINT_HAS_IGNORED_RAW_POINT)) {
        validationMessages.add(ValidationMessage.builder()
            .withIssueType(IssueType.POINT_HAS_IGNORED_RAW_POINT)
            .withDetails("Mappable Point: ["
                + getPersistentIdentity()
                + "] is associated with raw point: ["
                + rawPoint.getNaturalIdentity()
                + "] with id: ["
                + rawPoint.getPersistentIdentity()
                + "] that has been marked as ignored.")
            .withEntityType(getClass().getSimpleName())
            .withNaturalIdentity(getNaturalIdentity())
            .withRemediationDescription("Hard delete mappable point")
            .withRemediationStrategy(remediationStrategy)
            .build());

        if (remediate) {
          remediationStrategy.remediate(entities);
        }
      }
      return;
    }
  }

  public void remediatePointTemplateConflicts() {

    /*
     * RP-6489: 
     * If point is being moved to equipment whose equipment type is not compatible
     * (either not in list for new equipment type or null), then remove the point template
     * association and delete the point's haystack tags.
     */
    boolean remediatePointTemplateConflicts = false;
    AbstractNodeTagTemplateEntity pointTemplate = getPointTemplateNullIfEmpty();
    if (pointTemplate != null) {
      
      if (getParentNode() instanceof EquipmentEntity) {
        
        EquipmentEntity parentEquipment = (EquipmentEntity)getParentNode();
        
        EquipmentEnergyExchangeTypeEntity parentEquipmentType = parentEquipment.getEquipmentTypeNullIfNotExists();
        if (parentEquipmentType == null) {

          // The new parent is a piece of equipment, but it doesn't have an equipment type.
          remediatePointTemplateConflicts = true;
        } else {
          
          Set<PointTemplateEntity> pointTemplates = DictionaryContext
              .getNodeTagTemplatesContainer()
              .getEquipmentPointTemplatesForEquipmentType(parentEquipmentType);
          
          if (!pointTemplates.contains(pointTemplate)) {

            // The new parent is a piece of equipment and has an equipment type, 
            // but it is not compatible with the point template.
            remediatePointTemplateConflicts = true;
          }
        }
      } else {
        
        // The new parent is not a piece of equipment. 
        remediatePointTemplateConflicts = true;
      }
      
      if (remediatePointTemplateConflicts) {
        
        removeHaystackTags();
        setPointTemplate(null);
      }
    }
  }  
  
  @Override
  public void mapToDtos(Map<String, Object> dtos) {

    PortfolioDtoMapper.mapMappablePointNodeDto(this, dtos);
  }
  
  public static String getDefaultRange(
      RawPointEntity rawPoint,
      DataType dataType) {
    
    String range = null;
    int dataTypeId = dataType.getId();
    if (dataTypeId == 2 || dataTypeId == 3) {
      
      range = rawPoint.getRange();
      if (range == null || range.trim().equals("")) {
        range = "";
      }
    }
    return range;
  }
}
//@formatter:on