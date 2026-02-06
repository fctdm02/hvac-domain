package com.djt.hvac.domain.model.nodehierarchy.point;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.RemediationStrategy;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.dictionary.TagEntity;
import com.djt.hvac.domain.model.dictionary.UnitEntity;
import com.djt.hvac.domain.model.dictionary.container.DictionaryContext;
import com.djt.hvac.domain.model.dictionary.container.NodeTagTemplatesContainer;
import com.djt.hvac.domain.model.dictionary.energyexchange.EquipmentEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.enums.DataType;
import com.djt.hvac.domain.model.dictionary.enums.NodeType;
import com.djt.hvac.domain.model.dictionary.enums.TagGroupType;
import com.djt.hvac.domain.model.dictionary.enums.UnitSystem;
import com.djt.hvac.domain.model.dictionary.template.nodetag.AbstractPointTemplateUnitMappingOverrideEntity;
import com.djt.hvac.domain.model.dictionary.template.nodetag.PointTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.nodetag.PointTemplateUnitMappingEntity;
import com.djt.hvac.domain.model.dictionary.template.nodetag.UnitMappingEntity;
import com.djt.hvac.domain.model.nodehierarchy.AbstractNodeEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BuildingEntity;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.EquipmentEntity;
import com.djt.hvac.domain.model.nodehierarchy.validation.DeleteAndReAddTagsFromTemplateStrategyImpl;
import com.djt.hvac.domain.model.nodehierarchy.validation.DeleteTagsStrategyImpl;
import com.djt.hvac.domain.model.nodehierarchy.validation.RemovePointTemplateAndTagsStrategyImpl;
import com.djt.hvac.domain.model.timeseries.client.TimeSeriesServiceClient;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

public abstract class AbstractPointEntity extends AbstractNodeEntity {
  private static final long serialVersionUID = 1L;
  
  private DataType dataType;
  private UnitEntity unit;
  private String range;
  private PointTemplateEntity pointTemplate;
  private String lastValue;
  private Long lastValueTimestamp;
  private Map<Long, String> values;
  
  private transient Set<String> _haystackTags; // Tag Group 7

  @Override
  protected void resetTransientAttributes() {
    
    super.resetTransientAttributes();
    _haystackTags = null;
  }
  
  @Override
  protected <T extends AbstractNodeEntity> Set<T> getChildSet(AbstractNodeEntity t) {
    throw new UnsupportedOperationException("Points do not have any child nodes, so this method invocation has no meaning");
  }
  
  public AbstractPointEntity() {}

  // For new instances (i.e. have not been persisted yet)
  public AbstractPointEntity(
      AbstractNodeEntity parentNode,
      String name,
      String displayName,
      DataType dataType,
      UnitEntity unit,
      String range) {
    super(
        parentNode, 
        name, 
        displayName);
    this.dataType = dataType;
    this.unit = unit;
    this.range = range;
  }
  
  public AbstractPointEntity(
      Integer persistentIdentity,
      AbstractNodeEntity parentNode,
      String name,
      String displayName,
      Set<TagEntity> nodeTags,
      DataType dataType,
      String range,
      PointTemplateEntity pointTemplate) {
    this(
        persistentIdentity, 
        parentNode, 
        name, 
        displayName,
        null,
        null,
        nodeTags,
        dataType,
        null,
        range,
        pointTemplate,
        null,
        null);
    }
  
  public AbstractPointEntity(
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
      Long lastValueTimestamp) {
    super(
        persistentIdentity, 
        parentNode, 
        name, 
        displayName,
        null,
        createdAt,
        updatedAt,
        nodeTags);
    requireNonNull(dataType, "dataType cannot be null");
    this.dataType = dataType;
    this.unit = unit;
    this.range = range;
    this.pointTemplate = pointTemplate;
    this.lastValue = lastValue;
    this.lastValueTimestamp = lastValueTimestamp;
    
    if (parentNode instanceof AbstractPointEntity) {
      
      throw new IllegalArgumentException("Parent of point with id: [" 
          + persistentIdentity 
          + "] cannot be another point: [" 
          + parentNode.getClassAndPersistentIdentity()
          + "].");
    }
  }
  
  public NodeType getNodeType() {
    return NodeType.POINT;
  }
  
  public abstract String getMetricId();
  
  public DataType getDataType() {
    return dataType;
  }
  
  public String getMetricIdForTsdb() {
    String metricId = getMetricId();
    return getRootPortfolioNode().getParentCustomer().getUuid() + TimeSeriesServiceClient.METRIC_ID_DELIMITER + metricId;
  }
  
  public void resetValues() {

    if (values != null) {
      values.clear();
    }
  }

  public void addValues(Map<Long, Double> metricValues) {
    
    if (values == null) {
      values = new TreeMap<>();
    }
    for (Map.Entry<Long, Double> entry: metricValues.entrySet()) {
      
      Long epochSeconds = entry.getKey();
      if (Long.toString(epochSeconds).length() != 10) {
        epochSeconds = epochSeconds / 1000;
      }
      Double value = entry.getValue();
      values.put(epochSeconds, value.toString());  
    }
  }
  
  public String getValue(Long epochSeconds) throws EntityDoesNotExistException {
    
    if (values != null) {
      if (Long.toString(epochSeconds).length() != 10) {
        epochSeconds = epochSeconds / 1000;
      }
      String value = values.get(epochSeconds);
      return value;
    }
    return null;
  }
  
  public void addValue(Long epochSeconds, String value) {
    
    if (values == null) {
      values = new TreeMap<>();
    }
    if (Long.toString(epochSeconds).length() != 10) {
      epochSeconds = epochSeconds / 1000;
    }
    values.put(epochSeconds, value);
  }
  
  public Map<Long, String> getValues() {
    
    return values;
  }

  public void setUnit(UnitEntity unit) {
    
    if (this.unit == null && unit == null) {
      
      // BOTH NULL: DO NOTHING
      
    } else if (this.unit == null && unit != null) {
      
      this.unit = unit;
      setIsModified("unit");
      
    } else if (this.unit != null && unit == null) {
      
      this.unit = unit;
      setIsModified("unit");
      
    } else if (this.unit != null && unit != null) {
      
      if (!this.unit.equals(unit)) {

        this.unit = unit;
        setIsModified("unit");
        
      } else {
        
        // BOTH NOT NULL AND EQUAL TO EACH OTHER: DO NOTHING
        
      }
    } 
  }
  
  public Optional<UnitEntity> getUnit() {
    return Optional.ofNullable(unit);
  }

  public UnitEntity getUnitNullIfNotExists() {
    return unit;
  }
  
  public void setRange(String range) {
    
    if (this.range == null && range == null) {
      
      // BOTH NULL: DO NOTHING
      
    } else if (this.range == null && range != null) {
      
      this.range = range;
      setIsModified("range");
      
    } else if (this.range != null && range == null) {
      
      this.range = range;
      setIsModified("range");
      
    } else if (this.range != null && range != null) {
      
      if (!this.range.equals(range)) {

        this.range = range;
        setIsModified("range");
        
      } else {
        
        // BOTH NOT NULL AND EQUAL TO EACH OTHER: DO NOTHING
        
      }
    } 
  }
  
  public Optional<String> getRange() {
    return Optional.ofNullable(range);
  }

  public String getRangeNullIfEmpty() {
    return range;
  }
  
  public Optional<PointTemplateEntity> getPointTemplate() {
    return Optional.ofNullable(pointTemplate);
  }

  public PointTemplateEntity getPointTemplateNullIfEmpty() {
    return pointTemplate;
  }
  
  public void setPointTemplate(PointTemplateEntity pointTemplate) {
    
    if (this.pointTemplate == null && pointTemplate == null) {
      
      // BOTH NULL: DO NOTHING
      
    } else if (this.pointTemplate == null && pointTemplate != null) {
      
      this.pointTemplate = pointTemplate;
      this.removeHaystackTags();
      this.addNodeTags(pointTemplate.getTags());
      setIsModified("pointTemplate: added");
      
    } else if (this.pointTemplate != null && pointTemplate == null) {
      
      this.pointTemplate = pointTemplate;
      this.removeHaystackTags();
      setIsModified("pointTemplate:removed");
      
    } else if (this.pointTemplate != null && pointTemplate != null) {
      
      if (!this.pointTemplate.equals(pointTemplate)) {

        this.pointTemplate = pointTemplate;
        this.removeHaystackTags();
        this.addNodeTags(pointTemplate.getTags());
        setIsModified("pointTemplate:changed");
        
      } else {
        
        // BOTH NOT NULL AND EQUAL TO EACH OTHER: DO NOTHING
        
      }
    }    
  }
  
  public String getLastValue() {
    return lastValue;
  }
  
  public Long getLastValueTimestamp() {
    return lastValueTimestamp;
  }
  
  public Set<String> getHaystackTags() {
    
    if (_haystackTags == null) {
      _haystackTags = new TreeSet<>();
      Iterator<TagEntity> iterator = getNodeTags().iterator();
      while (iterator.hasNext()) {
         
         TagEntity tag = iterator.next();
         if (tag.getTagGroupType().equals(TagGroupType.POINT_HAYSTACK_TAG)) {
           _haystackTags.add(tag.getName());
         }
      }
    }
    return _haystackTags;
  }

  public Set<TagEntity> getHaystackTagsAsEntities() {
    
    Set<TagEntity> set = new TreeSet<>();
    Iterator<TagEntity> iterator = getNodeTags().iterator();
    while (iterator.hasNext()) {
       
       TagEntity tag = iterator.next();
       if (tag.getTagGroupType().equals(TagGroupType.POINT_HAYSTACK_TAG)) {
         set.add(tag);
       }
    }
    return set;
  }
  
  public boolean removeHaystackTags() {
    
    _haystackTags = null;
    Set<TagEntity> victimTags = new TreeSet<>();
    Iterator<TagEntity> iterator = getNodeTags().iterator();
    while (iterator.hasNext()) {
       
       TagEntity tag = iterator.next();
       if (tag.getTagGroupType().equals(TagGroupType.POINT_HAYSTACK_TAG)) {
         victimTags.add(tag);
       }
    }
    return removeNodeTags(victimTags);
  }

  public Set<TagEntity> getMetadataTags() {

    Set<TagEntity> tags = new TreeSet<>();
    Iterator<TagEntity> iterator = getNodeTags().iterator();
    while (iterator.hasNext()) {

      TagEntity tag = iterator.next();
      if (tag.getTagGroupType().equals(TagGroupType.POINT_TAG)) {
        tags.add(tag);
      }
    }
    return tags;
  }

  public boolean removeMetadataTags() {
    
    return removeNodeTags(getMetadataTags());
  }
  
  public void setMetadataTags(Set<TagEntity> tags) {
    
    Set<TagEntity> existingTags = getMetadataTags();
    if (!existingTags.equals(tags)) {
      
      removeMetadataTags();
      addNodeTags(tags);
    }
    remediateScopedTagConflicts();
  }
  
  @Override
  public int calculateTotalMappedPointCount() {
    return 0;
  }
  
  @Override
  public Set<AbstractNodeEntity> getAllChildNodes() {
    
    return new HashSet<>();
  }
  
  @Override
  public void validate(
      Set<IssueType> issueTypes,
      List<ValidationMessage> validationMessages, 
      boolean remediate) {
    
    NodeTagTemplatesContainer nodeTagTemplatesContainer = DictionaryContext.getNodeTagTemplatesContainer();
    PointTemplateEntity pointTemplate = this.getPointTemplateNullIfEmpty();
    
    Map<String, Object> entities = new LinkedHashMap<>();
    entities.put("point", this);
    
    if (pointTemplate == null) {
      
      Set<String> haystackTags = getHaystackTags();
      if (!haystackTags.isEmpty()) {

        RemediationStrategy remediationStrategy = DeleteTagsStrategyImpl.get();

        if (issueTypes.contains(IssueType.POINT_HAS_TAGS_BUT_NO_TEMPLATE)) {
          validationMessages.add(ValidationMessage.builder()
              .withIssueType(IssueType.POINT_HAS_TAGS_BUT_NO_TEMPLATE)
              .withDetails("Point has haystack tags: "
                  + haystackTags
                  + " but does not have an associated point template")
              .withEntityType(getClass().getSimpleName())
              .withNaturalIdentity(getNaturalIdentity())
              .withRemediationDescription("Delete haystack tags for point")
              .withRemediationStrategy(remediationStrategy)
              .build());

          if (remediate) {
            remediationStrategy.remediate(entities);
          }
        }
        return;
      }
    }
    
    AbstractNodeEntity parentNode = getParentNode();
    NodeType parentNodeType = parentNode.getNodeType();
    if (pointTemplate != null) {
      
      // See if the point template is deprecated.
      Boolean isDeprecated = pointTemplate.getIsDeprecated();
      if (isDeprecated.booleanValue() && remediate) {
        
        Integer replacementPointTemplateId = pointTemplate.getReplacementPointTemplateId();
        if (replacementPointTemplateId == null) {

          // If deprecated, but no replacement point template, then simply remove the point haystack tags and null out the point template association.
          RemediationStrategy remediationStrategy = RemovePointTemplateAndTagsStrategyImpl.get();
          remediationStrategy.remediate(entities);
          
        } else {

          // If deprecated, but there is a replacement point template, then change to the replacement point template.
          try {
            PointTemplateEntity replacementPointTemplate = DictionaryContext
                .getNodeTagTemplatesContainer()
                .getPointTemplate(replacementPointTemplateId);
            
            setPointTemplate(replacementPointTemplate);
            RemediationStrategy remediationStrategy = DeleteAndReAddTagsFromTemplateStrategyImpl.get();
            remediationStrategy.remediate(entities);
            
          } catch (EntityDoesNotExistException e) {
            
            throw new IllegalStateException("Unable to migrate point template to replacement: "
                + replacementPointTemplateId, e);
          }
        }
      }
      
      Set<NodeType> parentNodeTypes = pointTemplate.getParentNodeTypes();
      if (!parentNodeTypes.contains(parentNodeType)) {

        RemediationStrategy remediationStrategy = RemovePointTemplateAndTagsStrategyImpl.get();

        if (issueTypes.contains(IssueType.POINT_HAS_TEMPLATE_YET_NON_EQUIPMENT_PARENT)) {
          validationMessages.add(ValidationMessage.builder()
              .withIssueType(IssueType.POINT_HAS_TEMPLATE_YET_NON_EQUIPMENT_PARENT)
              .withDetails("Point is associated with point template: ["
                  + pointTemplate.getName()
                  + "], but whose direct parent is of type: ["
                  + getParentNode().getClass().getSimpleName()
                  + "]")
              .withEntityType(getClass().getSimpleName())
              .withNaturalIdentity(getNaturalIdentity())
              .withRemediationDescription("Disassociate point template and delete haystack tags")
              .withRemediationStrategy(remediationStrategy)
              .build());

          if (remediate) {
            remediationStrategy.remediate(entities);
            return;
          }
        }        
      }
      
      if (parentNode instanceof EquipmentEntity) {
      
        EquipmentEntity parentEquipment = (EquipmentEntity)parentNode;
        Optional<EquipmentEnergyExchangeTypeEntity> parentEquipmentTypeOptional = parentEquipment.getEquipmentType();

        if (!parentEquipmentTypeOptional.isPresent()) {

          RemediationStrategy remediationStrategy = RemovePointTemplateAndTagsStrategyImpl.get();

          if (issueTypes.contains(IssueType.POINT_HAS_TEMPLATE_YET_PARENT_EQUIPMENT_NO_TYPE)) {
            validationMessages.add(ValidationMessage.builder()
                .withIssueType(IssueType.POINT_HAS_TEMPLATE_YET_PARENT_EQUIPMENT_NO_TYPE)
                .withDetails("Point template: ["
                    + pointTemplate.getName()
                    + "] is not applicable because parent equipment does not have an equipment type")
                .withEntityType(getClass().getSimpleName())
                .withNaturalIdentity(getNaturalIdentity())
                .withRemediationDescription("Disassociate point template and delete haystack tags")
                .withRemediationStrategy(remediationStrategy)
                .build());

            if (remediate) {
              remediationStrategy.remediate(entities);
            }
          }
          return;
        }

        EquipmentEnergyExchangeTypeEntity parentEquipmentType = parentEquipmentTypeOptional.get();
        Set<PointTemplateEntity> pointTemplates = nodeTagTemplatesContainer.getEnergyExchangeLevelPointTemplatesForEnergyExchangeType(parentEquipmentType);
        if (!pointTemplates.contains(pointTemplate)) {

          RemediationStrategy remediationStrategy = RemovePointTemplateAndTagsStrategyImpl.get();

          if (issueTypes
              .contains(IssueType.POINT_HAS_TEMPLATE_THAT_IS_INVALID_FOR_PARENT_EQUIPMENT_TYPE)) {
            validationMessages.add(ValidationMessage.builder()
                .withIssueType(IssueType.POINT_HAS_TEMPLATE_THAT_IS_INVALID_FOR_PARENT_EQUIPMENT_TYPE)
                .withDetails("Point template: ["
                    + pointTemplate.getName()
                    + "] is not compatible for parent equipment type: ["
                    + parentEquipmentType
                    + "]")
                .withEntityType(getClass().getSimpleName())
                .withNaturalIdentity(getNaturalIdentity())
                .withRemediationDescription("Disassociate point template and delete haystack tags")
                .withRemediationStrategy(remediationStrategy)
                .build());

            if (remediate) {
              remediationStrategy.remediate(entities);
            }
          }
          return;
        }

        Set<String> pointHaystackTags = getHaystackTags();
        Set<String> pointTemplateHaystackTags = pointTemplate.getNormalizedTagsAsSet(); 
        if (!pointTemplateHaystackTags.equals(pointHaystackTags)) {

          RemediationStrategy remediationStrategy = DeleteAndReAddTagsFromTemplateStrategyImpl.get();

          if (issueTypes.contains(IssueType.POINT_HAS_MISMATCH_BETWEEN_TAGS_AND_TEMPLATE_TAGS)) {
            validationMessages.add(ValidationMessage.builder()
                .withIssueType(IssueType.POINT_HAS_MISMATCH_BETWEEN_TAGS_AND_TEMPLATE_TAGS)
                .withDetails("Point haystack tags: "
                    + pointHaystackTags
                    + " do not match point template: ["
                    + pointTemplate.getName()
                    + "] haystack tags: "
                    + pointTemplateHaystackTags)
                .withEntityType(getClass().getSimpleName())
                .withNaturalIdentity(getNaturalIdentity())
                .withRemediationDescription(
                    "Delete and re-add haystack tags for point from point template")
                .withRemediationStrategy(remediationStrategy)
                .build());

            if (remediate) {
              remediationStrategy.remediate(entities);
            }
          }
          return;
        }
      }
    }
  }
  
  public boolean setUnitSystem(UnitSystem pUnitSystem) {
    
    UnitSystem unitSystem = pUnitSystem;
    boolean converted = false;
    if (pointTemplate != null) {
      
      // Verify that there is a mis-match between the unit system of the point and that of the building.
      BuildingEntity building = getAncestorBuilding();
      
      // Get all the levels of overrides, with building level having precedence over customer level and customer level over distributor level.
      Map<PointTemplateEntity, AbstractPointTemplateUnitMappingOverrideEntity> overrides = building.getAllPointTemplateUnitMappingOverrides();
      
      // Either use an override or the default unit mapping.
      UnitMappingEntity unitMapping = null;
      
      // See if there is a building/customer/distributor level point template unit mapping override.
      AbstractPointTemplateUnitMappingOverrideEntity override = overrides.get(pointTemplate);
      if (override != null) {
        // If there is an overide, there are two scenarios:
        // 1: Keep using the IP unit system
        // 2: Use the alternate unit mapping specified by the override
        if (override.getKeepIpUnitSystem()) {
          unitSystem = UnitSystem.IP;
          PointTemplateUnitMappingEntity pointTemplateUnitMapping = DictionaryContext
              .getNodeTagTemplatesContainer()
              .getDefaultPointTemplateUnitMapping(pointTemplate.getPersistentIdentity());
          if (pointTemplateUnitMapping != null) {
            unitMapping = pointTemplateUnitMapping.getUnitMapping();  
          }
        } else {
          unitMapping = override.getUnitMapping();  
        }
      } else {
        PointTemplateUnitMappingEntity pointTemplateUnitMapping = DictionaryContext
            .getNodeTagTemplatesContainer()
            .getDefaultPointTemplateUnitMapping(pointTemplate.getPersistentIdentity());
        
        if (pointTemplateUnitMapping != null) {
          unitMapping = pointTemplateUnitMapping.getUnitMapping();  
        }
      }
      
      // There should exist at least the default mapping, but be defensive here anyway.
      if (unitMapping != null) {
        
        if (unitSystem.equals(UnitSystem.SI)) {
          this.setUnit(unitMapping.getSiUnit());  
        } else {
          this.setUnit(unitMapping.getIpUnit());
        }
        converted = true;
      }
    }
    return converted;
  }  
  
  public AbstractNodeEntity getChildNodeByNameNullIfNotExists(String name) {
    throw new IllegalStateException("This method should only be used in the point mapping process");
  }
  
  static synchronized boolean isValidBooleanRange(String range) {
    try {
      OBJECT_MAPPER.get().readValue(range, TrueTextFalseTextBooleanRange.class);
      return true;
    } catch (IOException e1) {
      try {
        OBJECT_MAPPER.get().readValue(range, ZeroOneBooleanRange.class);
        return true;
      } catch (IOException e2) {
        return false;
      }
    }
  }
  
  static synchronized boolean isValidEnumRange(String range) {
    try {
      OBJECT_MAPPER.get().readValue(range, EnumRange.class);
      return true;
    } catch (IOException e1) {
      return false;
    }
  }  
  
  static final class TrueTextFalseTextBooleanRange {
    
    private String trueText;
    private String falseText;
    
    public String getTrueText() {
      return trueText;
    }
    public void setTrueText(String trueText) {
      this.trueText = trueText;
    }
    public String getFalseText() {
      return falseText;
    }
    public void setFalseText(String falseText) {
      this.falseText = falseText;
    }
  }
  
  @JsonPropertyOrder({
    "0",
    "1"
    })
  static final class ZeroOneBooleanRange {
    
    @JsonProperty("0")
    private String zeroText;
    @JsonProperty("1")
    private String oneText;
    
    @JsonProperty("0")
    public String getZeroText() {
      return zeroText;
    }
    @JsonProperty("0")
    public void setZeroText(String zeroText) {
      this.zeroText = zeroText;
    }
    @JsonProperty("1")
    public String setOneText() {
      return oneText;
    }
    @JsonProperty("1")
    public void setOneText(String oneText) {
      this.oneText = oneText;
    }
  }
  
  static final class EnumRange {
    
    private String key;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    
    public String key() {
      return key;
    }
    public void key(String key) {
      this.key = key;
    }
    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
    return this.additionalProperties;
    }
    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
    }    
  }
}