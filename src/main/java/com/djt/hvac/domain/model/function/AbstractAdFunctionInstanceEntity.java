package com.djt.hvac.domain.model.function;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.djt.hvac.domain.model.common.AbstractPersistentEntity;
import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.common.mapper.DtoMapper;
import com.djt.hvac.domain.model.dictionary.TagEntity;
import com.djt.hvac.domain.model.dictionary.UnitEntity;
import com.djt.hvac.domain.model.dictionary.container.DictionaryContext;
import com.djt.hvac.domain.model.dictionary.container.NodeTagTemplatesContainer;
import com.djt.hvac.domain.model.dictionary.enums.FunctionType;
import com.djt.hvac.domain.model.dictionary.enums.UnitSystem;
import com.djt.hvac.domain.model.dictionary.template.function.AbstractAdFunctionTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.function.AdFunctionTemplateInputConstantEntity;
import com.djt.hvac.domain.model.dictionary.template.function.AdFunctionTemplateInputPointEntity;
import com.djt.hvac.domain.model.dictionary.template.function.AdFunctionTemplateOutputPointEntity;
import com.djt.hvac.domain.model.dictionary.template.function.computedpoint.AdComputedPointFunctionTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.function.rule.AdRuleFunctionTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.nodetag.AbstractPointTemplateUnitMappingOverrideEntity;
import com.djt.hvac.domain.model.dictionary.template.nodetag.PointTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.nodetag.PointTemplateUnitMappingEntity;
import com.djt.hvac.domain.model.dictionary.template.nodetag.UnitMappingEntity;
import com.djt.hvac.domain.model.function.computedpoint.AdComputedPointFunctionInstanceEntity;
import com.djt.hvac.domain.model.function.dto.AdFunctionInstanceCandidateBoundPointsDto;
import com.djt.hvac.domain.model.function.dto.AdFunctionInstanceDto;
import com.djt.hvac.domain.model.function.rule.AdRuleFunctionInstanceEntity;
import com.djt.hvac.domain.model.nodehierarchy.AbstractNodeEntity;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BuildingEntity;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.EnergyExchangeEntity;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.EquipmentEntity;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.LoopEntity;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.PlantEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.AdFunctionAsyncComputedPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.AsyncComputedPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.utils.OpenTsdbStringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;

public abstract class AbstractAdFunctionInstanceEntity extends AbstractPersistentEntity {
  private static final long serialVersionUID = 1L;
  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAdFunctionInstanceEntity.class);

  private final EnergyExchangeEntity equipment;
  private final AbstractAdFunctionTemplateEntity adFunctionTemplate;
  private boolean isCandidate = false;
  
  // If ignored, then can't be enabled, nor will be evaluated.
  private boolean isIgnored = false;
  
  // Comes from the version of the AD function template at the time of initial creation.  
  // If there is a mis-match, then re-evaluation and copy on write occurs.
  // That is, a new "instance" will be created, assuming that it is still valid for the
  // updated AD function template definition.
  private Integer templateVersion = Integer.valueOf(1);
  
  /*
   * This is incremented whenever there is a change in the state of the instance, which could be:
   *   1. A change in the template version
   *   2. A change in the input constant values
   *   3. A change in the input points
   *   4. A change in the output points (unlikely)
   *   5. A change in "state".  The possible states are:
   *      a. Ignored (i.e. The user doesn't care about this at all, regardless of validity)
   *      b. Invalid (i.e. wrong equipment type or node filter expression doesn't match)
   *      c. Valid/Disabled (a.k.a. "candidate" state)
   *      d. Valid/Enabled (a.k.a. "instance" state)  NOTE: This is the only state that has meaning for the rule engine,
   *         so any state transitions to from this state will be communicated as either "add" (i.e. Valid/Disabled to Valid/Enabled)
   *         or "remove" (i.e. from Valid/Enabled to any of the other states)
   */
  private Integer instanceVersion = Integer.valueOf(1);
  
  private Set<AdFunctionInstanceInputConstantEntity> inputConstants = new TreeSet<>();
  private Set<AdFunctionInstanceInputPointEntity> inputPoints = new TreeSet<>();
  private Set<AdFunctionInstanceOutputPointEntity> outputPoints = new TreeSet<>();

  public AbstractAdFunctionInstanceEntity(
      Integer persistentIdentity,
      EnergyExchangeEntity equipment,
      AbstractAdFunctionTemplateEntity adFunctionTemplate,
      boolean isCandidate,
      boolean isIgnored,
      Integer templateVersion,
      Integer instanceVersion) {
    super(persistentIdentity);
    requireNonNull(adFunctionTemplate, "adFunctionTemplate cannot be null");
    requireNonNull(isCandidate, "isCandidate cannot be null");
    requireNonNull(templateVersion, "templateVersion cannot be null");
    requireNonNull(instanceVersion, "instanceVersion cannot be null");
    this.equipment = equipment; // If equipment is null, remediation will delete.
    this.adFunctionTemplate = adFunctionTemplate;
    this.isCandidate = isCandidate;
    this.isIgnored = isIgnored;
    this.templateVersion = templateVersion;
    this.instanceVersion = instanceVersion;
  }
  
  public void setIsIgnored() {
    setIsIgnored(true);
  }
  
  public boolean isIgnored() {
    return isIgnored;
  }

  public void setIsIgnored(boolean isIgnored) {
    this.isIgnored = isIgnored;
    setIsModified("isIgnored");
  }
  
  public Integer getTemplateVersion() {
    return templateVersion;
  }
  
  public boolean isTemplateVersionOutOfDate() {
    
    return !this.adFunctionTemplate.getVersion().equals(this.templateVersion);
  }
  
  public void setTemplateVersion() {
    
    Integer adFunctionTemplateVersion = this.adFunctionTemplate.getVersion();
    if (!adFunctionTemplateVersion.equals(this.templateVersion)) {
      
      this.templateVersion = adFunctionTemplateVersion;
      setIsModified("templateVersion");
    }
  }
  
  public Integer getInstanceVersion() {
    return instanceVersion;
  }
  
  public void incrementInstanceVersion() {
    
    this.instanceVersion = Integer.valueOf(this.instanceVersion.intValue() + 1);
    setIsModified("instanceVersion");
  }  
  
  @Override
  public void setIsModified(String modifiedAttributeName) {
    
    if (!equipment.getRootPortfolioNode().isBeingMapped) {
      
      super.setIsModified(modifiedAttributeName);
      equipment.setIsModified("adFunctionInstance:" + modifiedAttributeName);      
    }
  }
  
  @Override
  public void setIsDeleted() {
    
    super.setIsDeleted();
    
    // RP-11915: Mark the associated async computed point as deleted (so it won't be orphaned).
    List<AdFunctionInstanceOutputPointEntity> list = Lists.newArrayList();
    list.addAll(outputPoints);
    for (AdFunctionInstanceOutputPointEntity op: list) {

      AsyncComputedPointEntity acp = op.getPoint();
      acp.setIsDeleted();
    }

    // In order to handle candidates whose equipment no longer exists, we need to do a null check.
    if (this.equipment != null && !equipment.getRootPortfolioNode().isBeingMapped) {

      if (this.isCandidate) {
        this.equipment.addDeletedAdFunctionInstanceCandidate(this);
      } else {
        this.equipment.getRootPortfolioNode().addNewlyDisabledAdFunctionInstanceId(this.getPersistentIdentity());
        this.equipment.addDeletedAdFunctionInstance(this);
      }
      this.equipment.setIsModified("adFunctionInstance:deleted");
    }
  }
  
  public EnergyExchangeEntity getEquipment() {
    return this.equipment;
  }

  public AbstractAdFunctionTemplateEntity getAdFunctionTemplate() {
    return this.adFunctionTemplate;
  }
  
  public boolean getIsCandidate() {
    return isCandidate;
  }
    
  public boolean addInputConstant(AdFunctionInstanceInputConstantEntity inputConstant) throws EntityAlreadyExistsException {
    return addChild(inputConstants, inputConstant, this);
  }
  
  public Set<AdFunctionInstanceInputConstantEntity> getInputConstants() {
    return inputConstants;
  }

  public AdFunctionInstanceInputConstantEntity getInputConstantByTemplateConstantId(Integer templateConstantId) {
    
    Iterator<AdFunctionInstanceInputConstantEntity> iterator = inputConstants.iterator();
    while (iterator.hasNext()) {
      
      AdFunctionInstanceInputConstantEntity inputConstant = iterator.next();
      if (inputConstant.getAdFunctionTemplateInputConstant().getPersistentIdentity().equals(templateConstantId)) {
        
        return inputConstant;
      }
    }
    return null;
  }
  
  public AdFunctionInstanceInputConstantEntity getInputConstant(
      String constantName)
  throws 
      EntityDoesNotExistException {

    for (AdFunctionInstanceInputConstantEntity inputConstant: inputConstants) {
      if (inputConstant.getAdFunctionTemplateInputConstant().getName().equals(constantName) || inputConstant.getAdFunctionTemplateInputConstant().getName().toUpperCase().equals(constantName)) {
        return inputConstant;
      }
    }
    throw new EntityDoesNotExistException(
        getClassAndPersistentIdentity() 
        + "] does not have an input constant named: ["
        + constantName
        + "] in its child input constants collection: "
        + inputConstants);
  }
  
  public boolean addInputPoint(AdFunctionInstanceInputPointEntity inputPoint) throws EntityAlreadyExistsException {
    
    // RP-13301: The unique index is on instance_id+template_point_id+subscript, NOT the expected instance_id+template_point_id+_point_id
    Set<String> uniqueIdentities = new HashSet<>();
    for (AdFunctionInstanceInputPointEntity ip: inputPoints) {
      
      uniqueIdentities.add(getPersistentIdentity() 
          + "_" 
          + ip.getAdFunctionTemplateInputPoint().getPersistentIdentity() 
          + "_" 
          + ip.getSubscript());
    }
    
    String key = getPersistentIdentity() 
        + "_" 
        + inputPoint.getAdFunctionTemplateInputPoint().getPersistentIdentity() 
        + "_" 
        + inputPoint.getSubscript();
    
    if (uniqueIdentities.contains(key)) {

      boolean foundUnique = false;
      int count = 0;
      int maxCount = 1000; 
      do {
        
        count = count + 1;
        inputPoint.incrementSubscript();
        
        key = getPersistentIdentity() 
            + "_" 
            + inputPoint.getAdFunctionTemplateInputPoint().getPersistentIdentity() 
            + "_" 
            + inputPoint.getSubscript();
        
        if (!uniqueIdentities.contains(key)) {
          foundUnique = true;
        }
        
      } while (count <= maxCount && !foundUnique);
    }
    
    return addChild(inputPoints, inputPoint, this);
  }
  
  public Set<AdFunctionInstanceInputPointEntity> getInputPoints() {
    return inputPoints;
  }

  public AdFunctionInstanceInputPointEntity getInputPoint(Map<String, Integer> parentIdentities) throws EntityDoesNotExistException {
    return getChild(AdFunctionInstanceInputPointEntity.class, inputPoints, parentIdentities, this);
  }

  public AdFunctionInstanceInputPointEntity getInputPoint(
      String inputPointName)
  throws 
      EntityDoesNotExistException {

    for (AdFunctionInstanceInputPointEntity inputPoint: inputPoints) {
      if (inputPoint.getAdFunctionTemplateInputPoint().getName().equals(inputPointName)) {
        return inputPoint;
      }
    }
    throw new EntityDoesNotExistException(
        getClassAndPersistentIdentity() 
        + "] does not have an input point named: ["
        + inputPointName
        + "] in its child input point collection: "
        + inputPoints);
  }
  
  public int getNumNonDeletedInputPoints() {
    
    int count = 0;
    for (AdFunctionInstanceInputPointEntity inputPoint: inputPoints) {
      
      if (!inputPoint.getIsDeleted()) {
        count++;
      }
    }
    return count;
  }
  
  public boolean removeInputPoint(AdFunctionInstanceInputPointEntity inputPoint) {
    return inputPoints.remove(inputPoint);
  }
  
  public boolean addOutputPoint(AdFunctionInstanceOutputPointEntity outputPoint) throws EntityAlreadyExistsException {
    return addChild(outputPoints, outputPoint, this);
  }
  
  public Set<AdFunctionInstanceOutputPointEntity> getOutputPoints() {
    return outputPoints;
  }

  public AdFunctionInstanceOutputPointEntity getOutputPoint(
      AdFunctionTemplateOutputPointEntity templateOutputPoint) 
  throws 
      EntityDoesNotExistException {
    
    for (AdFunctionInstanceOutputPointEntity outputPoint: outputPoints) {
      if (outputPoint.getAdFunctionTemplateOutputPoint().equals(templateOutputPoint)) {
        return outputPoint;
      }
    }
    throw new EntityDoesNotExistException(
        getClassAndPersistentIdentity() 
        + "] does not have an output point associated with: ["
        + templateOutputPoint.getClassAndPersistentIdentity()
        + "] in its child output points collection: "
        + outputPoints);
  }
  
  public AdFunctionInstanceOutputPointEntity getOutputPoint() 
  throws 
      EntityDoesNotExistException {
    
    if (outputPoints.isEmpty()) {
      throw new EntityDoesNotExistException(
          getClassAndPersistentIdentity() 
          + "] does not have an output point in its child output points collection: "
          + outputPoints);
    }
    AdFunctionInstanceOutputPointEntity op = null;
    for (AdFunctionInstanceOutputPointEntity outputPoint: outputPoints) {
      op = outputPoint;
    }
    return op;
  }
  
  public Map<AdFunctionTemplateInputPointEntity, Set<AdFunctionInstanceEligiblePoint>> getAllBoundPoints() {
    
    Map<AdFunctionTemplateInputPointEntity, Set<AdFunctionInstanceEligiblePoint>> allBoundPoints = new LinkedHashMap<>();
    Iterator<AdFunctionInstanceInputPointEntity> iterator = inputPoints.iterator();
    while (iterator.hasNext()) {
      
      AdFunctionInstanceInputPointEntity inputPoint = iterator.next();
      
      if (!inputPoint.getIsDeleted()) {

        AdFunctionTemplateInputPointEntity templateInputPoint = adFunctionTemplate.getInputPoint(inputPoint.getAdFunctionTemplateInputPoint().getPersistentIdentity());
        Set<AdFunctionInstanceEligiblePoint> boundPoints = allBoundPoints.get(templateInputPoint);
        if (boundPoints == null) {
          
          boundPoints = new LinkedHashSet<>();
          allBoundPoints.put(templateInputPoint, boundPoints);
        }
        boundPoints.add(inputPoint.getPoint());
      }
    }
    return allBoundPoints;
  }
  
  public int getNodePointCountForArrayTemplateInputPoint(Integer templateInputPointId) {
    
    int nodePointCount = 0;
    Iterator<AdFunctionInstanceInputPointEntity> iterator = inputPoints.iterator();
    while (iterator.hasNext()) {
      
      AdFunctionInstanceInputPointEntity rip = iterator.next();
      if (rip.getAdFunctionTemplateInputPoint().getPersistentIdentity().equals(templateInputPointId)) {
        nodePointCount++;
      }
    }
    return nodePointCount;
  }

  /**
   * 
   * @param nodeTagTemplatesContainer The point templates container used to set point template/tags for the
   *        async computed point(s) that is created against the instance output point(s)
   *        
   * @param portfolio The owning portfolio
   *        
   * @param source The "source" instance, which can either be a candidate or an active AD function instance
   *        that is being "updated" (i.e. copy on write)
   * 
   * @return The "created" AD Function Instance, using the "source" as a template (which can either be a 
   *         candidate or an active instance.  For the latter, the source is then deactivated)
   */
  public static AbstractAdFunctionInstanceEntity createAdFunctionInstance(
      NodeTagTemplatesContainer nodeTagTemplatesContainer,
      PortfolioEntity portfolio,
      AbstractAdFunctionInstanceEntity source) {
    
    AbstractAdFunctionInstanceEntity createdInstance = null;
    try {

      FunctionType functionType = source.getAdFunctionTemplate().getAdFunction().getFunctionType();
      AbstractAdFunctionTemplateEntity adFunctionTemplate = source.getAdFunctionTemplate();
      EnergyExchangeEntity equipment = source.getEquipment();
      
      if (functionType.equals(FunctionType.RULE)) {
        
        createdInstance = new AdRuleFunctionInstanceEntity(
            null,
            equipment,
            (AdRuleFunctionTemplateEntity)adFunctionTemplate,
            false, // isCandidate
            false, // isIgnored
            adFunctionTemplate.getVersion(),
            Integer.valueOf(source.getInstanceVersion().intValue() + 1));
                    
      } else if (functionType.equals(FunctionType.COMPUTED_POINT)) {

        createdInstance = new AdComputedPointFunctionInstanceEntity(
            null,
            equipment,
            (AdComputedPointFunctionTemplateEntity)adFunctionTemplate,
            false, // isCandidate
            false, // isIgnored
            adFunctionTemplate.getVersion(),
            Integer.valueOf(source.getInstanceVersion().intValue() + 1));
                    
      } else {
        throw new IllegalStateException("Unsupported function type: ["
            + functionType.getName() 
            + "]");
      }
      
      // INPUT POINTS
      for (AdFunctionInstanceInputPointEntity inputPoint: source.getInputPoints()) {
        
        if (!inputPoint.getIsDeleted()) {

          createdInstance.addInputPoint(new AdFunctionInstanceInputPointEntity(
              createdInstance, 
              inputPoint.getAdFunctionTemplateInputPoint(),
              inputPoint.getPoint(),
              inputPoint.getSubscript()));
        }
      }
      
      // CONSTANTS
      if (source.getIsCandidate()) {
        for (AdFunctionTemplateInputConstantEntity templateInputConstant: adFunctionTemplate.getInputConstants()) {
          
          createdInstance.addInputConstant(new AdFunctionInstanceInputConstantEntity(
              createdInstance, 
              templateInputConstant,
              templateInputConstant.getDefaultValue()));
        }
      } else {
        for (AdFunctionInstanceInputConstantEntity inputConstant: source.getInputConstants()) {
          
          if (!inputConstant.getIsDeleted()) {

            createdInstance.addInputConstant(new AdFunctionInstanceInputConstantEntity(
                createdInstance, 
                inputConstant.getAdFunctionTemplateInputConstant(),
                inputConstant.getValue()));
          }
        }
      }
      
      // If the source is a candidate, then we need to deal with the unit system.
      if (source.isCandidate) {

        BuildingEntity building = equipment.getAncestorBuilding();
        if (building.getUnitSystem().equals(UnitSystem.SI)) {
        
          createdInstance.setUnitSystem(building, UnitSystem.SI, null);
        }
      }
      
      // OUTPUT POINTS
      for (AdFunctionTemplateOutputPointEntity templateOutputPoint: adFunctionTemplate.getOutputPoints()) {

        // AD function instance output points have a one to one to an async computed point with the following metricId:
        String adFunctionTemplateName = adFunctionTemplate.getName();
        
        // Create a unique metric id that is based on natural identities.  We add a UUID 
        // to the end in order to distinguish this new point from any "deactivated" points.
        String functionTypeName = functionType.getName().replace(" ", "_");
        String metricId = OpenTsdbStringUtils.toValidMetricId(
            "/Async/"
            + functionTypeName
            + "/"
            + equipment.getNodePath() 
            + "/" 
            + adFunctionTemplateName
            + "/"
            + templateOutputPoint.getSequenceNumber()
            + "/"
            + UUID.randomUUID().toString());
        
        // AD computed point output points will have both point template and tag associations.
        Set<TagEntity> tags = templateOutputPoint.getTags();
        PointTemplateEntity pointTemplate = null;
        if (tags != null && !tags.isEmpty()) {
          
          pointTemplate = nodeTagTemplatesContainer.getPointTemplateByTags(tags);
        }
        
        // When "updating" an active AD function instance, we need to find the "old" async
        // computed point and delete it (otherwise, it would be orphaned from the disabled
        // AD function instance, as they are hard deleted now.
        // 
        // For either "creating" or "upating", we create an async computed point for each
        // AD function instance output point.
        if (!source.getIsCandidate()) {

          AdFunctionInstanceOutputPointEntity instanceOutputPoint = source.getOutputPoint(templateOutputPoint);
          
          // Get the existing async computed point associated with the current instance.
          AsyncComputedPointEntity existingAsyncComputedOutputPoint = instanceOutputPoint.getPoint();
          
          // Ensure that its metric id follows the pattern above, which is based solely on 
          // natural identities (including uniqueness when multiple output points exist)
          String displayName = existingAsyncComputedOutputPoint.getDisplayName();
          displayName = displayName + "_inactive";
          existingAsyncComputedOutputPoint.setDisplayName(displayName);
          existingAsyncComputedOutputPoint.setIsDeleted();
        }
        
        AsyncComputedPointEntity asyncComputedOutputPoint = new AdFunctionAsyncComputedPointEntity(
            (AbstractNodeEntity)equipment,
            adFunctionTemplateName + "_" + UUID.randomUUID().toString(),
            adFunctionTemplateName,
            tags,
            templateOutputPoint.getDataType(),
            templateOutputPoint.getUnit(),
            templateOutputPoint.getRange(),
            pointTemplate,
            metricId);
        
        if (equipment instanceof EquipmentEntity) {
          portfolio.addNodeToParentAndIndex((EquipmentEntity)equipment, asyncComputedOutputPoint);
          
        } else if (equipment instanceof PlantEntity) {
          portfolio.addNodeToParentAndIndex((PlantEntity)equipment, asyncComputedOutputPoint);
          
        } else if (equipment instanceof LoopEntity) {
          portfolio.addNodeToParentAndIndex((LoopEntity)equipment, asyncComputedOutputPoint);
          
        } else {
          throw new IllegalStateException("Unsupported node type: ["
              + equipment.getClass().getSimpleName()
              + "].  Supported types are: [EQUIPMENT, PLANT, LOOP].");
        }
        
        createdInstance.addOutputPoint(new AdFunctionInstanceOutputPointEntity(
            createdInstance, 
            templateOutputPoint,
            asyncComputedOutputPoint));
      }
      
      // There are two scenarios here for the instance we created the new instance from:
      // 1: It's a "candidate". We mark it as deleted so that it gets deleted from the repository.
      // 2: It's an active "instance".  We mark it as active=false and set the effective end date.
      if (source.getIsCandidate()) {
        equipment.addDeletedAdFunctionInstanceCandidate(source);  
      } else {
        equipment.addDeletedAdFunctionInstance(source);
      }
      
      // Either way, we add the created/updated instance to the equipment (as well as the response)
      equipment.addAdFunctionInstance(createdInstance);
      
      return createdInstance;

    } catch (Exception e) {
      throw new IllegalStateException("Unable to create/copy AD Function Instance from: ["
          + source
          + "], error: "
          + e.getMessage(), e);
    }
  }
  
  public boolean setUnitSystem(BuildingEntity building, UnitSystem pUnitSystem, PointTemplateEntity overridePointTemplate) {
    
    UnitSystem unitSystem = pUnitSystem;
    boolean modified = false;

    // Get all the levels of overrides, with building level having precedence over customer level and customer level over distributor level.
    Map<PointTemplateEntity, AbstractPointTemplateUnitMappingOverrideEntity> overrides = building.getAllPointTemplateUnitMappingOverrides();
    
    List<AdFunctionInstanceInputConstantEntity> constantList = new ArrayList<>();
    constantList.addAll(this.inputConstants);
    for (int j=0; j < constantList.size(); j++) {
      
      AdFunctionInstanceInputConstantEntity instanceInputConstant = constantList.get(j);
      
      // See if there is a point template associated with the template input constant.  
      // If so, we need to convert the constant value to the new unit system.
      Integer pointTemplateId = DictionaryContext
          .getAdFunctionTemplatesContainer()
          .getAdFunctionTemplateInputConstantPointTemplateMapping(
              instanceInputConstant
              .getAdFunctionTemplateInputConstant()
              .getPersistentIdentity());
      
      if (pointTemplateId != null) {
        
        PointTemplateEntity pointTemplate = DictionaryContext
            .getNodeTagTemplatesContainer()
            .getPointTemplateNullIfNotExists(pointTemplateId);
        
        if (overridePointTemplate != null && !overridePointTemplate.equals(pointTemplate)) {
          return false;
        }
        
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
              .getDefaultPointTemplateUnitMapping(pointTemplateId);
          
          if (pointTemplateUnitMapping != null) {
            unitMapping = pointTemplateUnitMapping.getUnitMapping();  
          }
        }
        
        // There should exist at least the default mapping, but be defensive here anyway.
        if (unitMapping != null) {
          
          // Use either the built in conversion factor or the hard coded temperature conversion formulas
          // for specific temperature conversions (as we need to account for the zero point difference):
          // °F to °C: (X°F - 32) * 5/9
          // °C to °F: (X°C * 9/5) + 32
          Double value = Double.parseDouble(instanceInputConstant.getValue());
          
          Double conversionFactor = null;
          Double convertedValue = null;
          
          UnitEntity ipUnit = unitMapping.getIpUnit();
          UnitEntity siUnit = unitMapping.getSiUnit();

          if (unitSystem.equals(UnitSystem.SI)) {

            if (ipUnit.getName().equals("°F")) {
              convertedValue = Double.valueOf((value.doubleValue() - 32.0) * (5.0/9.0));
            } else {
              conversionFactor = unitMapping.getIpToSiConversionFactorAsDouble();
              convertedValue = value.doubleValue() * conversionFactor.doubleValue();
            }
          } else {

            if (siUnit.getName().equals("°C")) {
              convertedValue = Double.valueOf((value.doubleValue() * (9.0/5.0)) + 32.0);
            } else {
              conversionFactor = unitMapping.getSiToIpConversionFactorAsDouble();
              convertedValue = value.doubleValue() * conversionFactor.doubleValue();
            }
          }
          
          if (!convertedValue.equals(value)) {
            
            modified = true;
            instanceInputConstant.setValue(convertedValue.toString());
          }
          
        } else {
          throw new IllegalStateException("There exists an AD function template input constant: ["
              + instanceInputConstant.getAdFunctionTemplateInputConstant().getNaturalIdentity()
              + "] that is associated with point template: ["
              + pointTemplate.getNaturalIdentity()
              + "], yet the point template does not have any unit mappings.  Either remove the association from the template or add a unit mapping.");
        }
      }
    }
    
    return modified;
  }
  
  public static String getNaturalIdentity(AbstractAdFunctionTemplateEntity adFunctionTemplate, EnergyExchangeEntity equipment) {
    return new StringBuilder()
        .append(equipment.getNaturalIdentity())
        .append("  -  ")
        .append(adFunctionTemplate.getNaturalIdentity())
        .toString();
  }  
  
  public static class Mapper implements DtoMapper<PortfolioEntity, AbstractAdFunctionInstanceEntity, AdFunctionInstanceDto> {
    
    private static final Mapper INSTANCE = new Mapper();
    private Mapper() {
    }
    
    public static Mapper getInstance() {
      return INSTANCE;
    }
    
    public List<AdFunctionInstanceDto> mapEntitiesToDtos(List<AbstractAdFunctionInstanceEntity> entities) {
      
      List<AdFunctionInstanceDto> list = new ArrayList<>();
      Iterator<AbstractAdFunctionInstanceEntity> iterator = entities.iterator();
      while (iterator.hasNext()) {
        AbstractAdFunctionInstanceEntity entity = iterator.next();
        if (!entity.getIsDeleted()) {
          list.add(mapEntityToDto(entity));  
        }
      }
      return list;
    }
    
    @Override
    public AdFunctionInstanceDto mapEntityToDto(AbstractAdFunctionInstanceEntity entity) {
      
      StringBuilder sb1 = new StringBuilder();
      StringBuilder sb2 = new StringBuilder();
      StringBuilder sb3 = new StringBuilder();
      
      String templateInputPointIds = null;
      String inputPointIds = null;
      String inputPointSubscript = null;
      String templateOutputPointIds = null;
      String outputPointIds = null;
      String templateInputConstIds = null;
      String inputConstValues = null;
      String candidateJson = null;
      
      if (entity.getIsCandidate()) {
       
        Map<Integer, List<Integer>> boundCandidatePoints = new LinkedHashMap<>();
        Iterator<AdFunctionInstanceInputPointEntity> inputPointIterator = entity.getInputPoints().iterator();
        while (inputPointIterator.hasNext()) {

          AdFunctionInstanceInputPointEntity inputPoint = inputPointIterator.next();
          Integer adFunctionTemplateInputPointId = inputPoint.getAdFunctionTemplateInputPoint().getPersistentIdentity();
          List<Integer> boundPoints = boundCandidatePoints.get(adFunctionTemplateInputPointId);
          if (boundPoints == null) {
            boundPoints = new ArrayList<>();
            boundCandidatePoints.put(adFunctionTemplateInputPointId, boundPoints);
          }
          boundPoints.add(inputPoint.getPoint().getPersistentIdentity());
        }

        AdFunctionInstanceCandidateBoundPointsDto candidateBoundPoints =
            AdFunctionInstanceCandidateBoundPointsDto
                .builder()
                .withBoundCandidatePoints(boundCandidatePoints)
                .build();

        try {
          candidateJson = OBJECT_MAPPER.get().writeValueAsString(candidateBoundPoints); 
        } catch (JsonProcessingException e) {
          throw new IllegalStateException("Unable to marshall bound points: ["
              + candidateBoundPoints
              + "] for candidate: "
              + entity.getNaturalIdentity());
        }      
      } else {

        Iterator<AdFunctionInstanceInputPointEntity> inputPointIterator = entity.getInputPoints().iterator();
        sb1.setLength(0);
        sb1.append("{");
        sb2.setLength(0);
        sb2.append("{");
        sb3.setLength(0);
        sb3.append("{");
        while (inputPointIterator.hasNext()) {

          AdFunctionInstanceInputPointEntity inputPoint = inputPointIterator.next();
          
          if (!inputPoint.getIsDeleted()) {

            sb1.append(inputPoint.getAdFunctionTemplateInputPoint().getPersistentIdentity());
            sb2.append(inputPoint.getPoint().getPersistentIdentity());
            sb3.append(inputPoint.getSubscript());
            if (inputPointIterator.hasNext()) {
              
              sb1.append(",");
              sb2.append(",");
              sb3.append(",");
            }
          }
        }
        sb1.append("}");
        sb2.append("}");
        sb3.append("}");
        templateInputPointIds = sb1.toString();
        inputPointIds = sb2.toString();
        inputPointSubscript = sb3.toString();
        
        Iterator<AdFunctionInstanceInputConstantEntity> inputConstantIterator = entity.getInputConstants().iterator();
        sb1.setLength(0);
        sb1.append("{");
        sb2.setLength(0);
        sb2.append("{");
        while (inputConstantIterator.hasNext()) {

          AdFunctionInstanceInputConstantEntity inputConstant = inputConstantIterator.next();
          
          sb1.append(inputConstant.getAdFunctionTemplateInputConstant().getPersistentIdentity());
          sb2.append(inputConstant.getValue());
          if (inputConstantIterator.hasNext()) {
            
            sb1.append(",");
            sb2.append(",");
          }
        }
        sb1.append("}");
        sb2.append("}");
        templateInputConstIds = sb1.toString();
        inputConstValues = sb2.toString();
        
        Iterator<AdFunctionInstanceOutputPointEntity> outputPointIterator = entity.getOutputPoints().iterator();
        sb1.setLength(0);
        sb1.append("{");
        sb2.setLength(0);
        sb2.append("{");
        while (outputPointIterator.hasNext()) {

          AdFunctionInstanceOutputPointEntity outputPoint = outputPointIterator.next();
          
          sb1.append(outputPoint.getAdFunctionTemplateOutputPoint().getPersistentIdentity());
          sb2.append(outputPoint.getPoint().getPersistentIdentity());
          if (outputPointIterator.hasNext()) {
            
            sb1.append(",");
            sb2.append(",");
          }
        }
        sb1.append("}");
        sb2.append("}");
        templateOutputPointIds = sb1.toString();
        outputPointIds = sb2.toString();
      }
      
      AdFunctionInstanceDto dto = new AdFunctionInstanceDto();
      dto.setId(entity.getPersistentIdentity());
      dto.setEquipmentId(entity.getEquipment().getPersistentIdentity());
      dto.setTemplateId(entity.getAdFunctionTemplate().getPersistentIdentity());
      dto.setTemplateInputPointId(templateInputPointIds);
      dto.setInputPointId(inputPointIds);
      dto.setInputPointSubscript(inputPointSubscript);
      dto.setTemplateOutputPointId(templateOutputPointIds);
      dto.setOutputPointId(outputPointIds);
      dto.setTemplateInputConstId(templateInputConstIds);
      dto.setInputConstValue(inputConstValues);
      dto.setCandidateJson(candidateJson);
      dto.setIgnored(entity.isIgnored());
      dto.setTemplateVersion(entity.getTemplateVersion());
      dto.setInstanceVersion(entity.getInstanceVersion());

      return dto;    
    }

    @Override
    public AbstractAdFunctionInstanceEntity mapDtoToEntity(PortfolioEntity portfolio, AdFunctionInstanceDto dto) {
      
      AbstractAdFunctionInstanceEntity entity = null;
      try {
        
        Integer id = dto.getId();
        AbstractNodeEntity node = portfolio.getChildNodeNullIfNotExists(dto.getEquipmentId());
        EnergyExchangeEntity equipment = null;
        if (node instanceof EnergyExchangeEntity) {
          equipment = (EnergyExchangeEntity)node;
        }

        AbstractAdFunctionTemplateEntity adFunctionTemplate = DictionaryContext.getAdFunctionTemplatesContainer().getAdFunctionTemplate(dto.getTemplateId());
        FunctionType functionType = adFunctionTemplate.getAdFunction().getFunctionType();
        String candidateJson = dto.getCandidateJson();
        Boolean isIgnored = dto.getIgnored();
        Integer templateVersion = dto.getTemplateVersion();
        Integer instanceVersion = dto.getInstanceVersion();
        
        if (isIgnored != null && isIgnored.booleanValue()) {
          
          if (functionType.equals(FunctionType.RULE)) {
            
            entity = new AdRuleFunctionInstanceEntity(
                id,
                equipment,
                (AdRuleFunctionTemplateEntity)adFunctionTemplate,
                true, // isCandidate
                true, // isIgnored
                templateVersion,
                instanceVersion);
                        
          } else if (functionType.equals(FunctionType.COMPUTED_POINT)) {

            entity = new AdComputedPointFunctionInstanceEntity(
                id,
                equipment,
                (AdComputedPointFunctionTemplateEntity)adFunctionTemplate,
                true, // isCandidate
                true, // isIgnored
                templateVersion,
                instanceVersion);
                        
          } else {
            throw new IllegalStateException("Unsupported function type: ["
                + functionType.getName() 
                + "]");
          }          
          
        } else if (candidateJson != null) {
          
          /*
          INSTANCE CANDIDATE:
          {
              "id" : 56704,
              "equipment_id" : 454,
              "template_id" : 47,
              "template_input_point_id" : null,
              "input_point_id" : null,
              "template_output_point_id" : null,
              "output_point_id" : null,
              "template_input_const_id" : null,
              "input_const_value" : null,
              "candidate_json" : "{\"boundCandidatePoints\":{\"114\":[18190],\"115\":[11053],\"151\":[16021]}}"
          }
          */      
          if (functionType.equals(FunctionType.RULE)) {
            
            entity = new AdRuleFunctionInstanceEntity(
                id,
                equipment,
                (AdRuleFunctionTemplateEntity)adFunctionTemplate,
                true, // isCandidate
                false, // isIgnored
                templateVersion,
                instanceVersion);
                        
          } else if (functionType.equals(FunctionType.COMPUTED_POINT)) {

            entity = new AdComputedPointFunctionInstanceEntity(
                id,
                equipment,
                (AdComputedPointFunctionTemplateEntity)adFunctionTemplate,
                true, // isCandidate
                false, // isIgnored
                templateVersion,
                instanceVersion);
                        
          } else {
            throw new IllegalStateException("Unsupported function type: ["
                + functionType.getName() 
                + "]");
          }
          
          // INPUT POINTS
          AdFunctionInstanceCandidateBoundPointsDto candidateBoundPoints = OBJECT_MAPPER.get().readValue(
                candidateJson, AdFunctionInstanceCandidateBoundPointsDto.class);
          
          Map<Integer, List<Integer>> boundCandidatePoints = candidateBoundPoints.getBoundCandidatePoints();

          Iterator<Entry<Integer, List<Integer>>> boundCandidatePointsIterator =
              boundCandidatePoints.entrySet().iterator();
          while (boundCandidatePointsIterator.hasNext()) {

            Entry<Integer, List<Integer>> entry = boundCandidatePointsIterator.next();
            Integer templateInputPointId = entry.getKey();
            List<Integer> boundPointList = entry.getValue();

            int subscript = 0;
            Iterator<Integer> boundPointListIterator = boundPointList.iterator();
            while (boundPointListIterator.hasNext()) {

              Integer inputPointId = boundPointListIterator.next();
              
              // RP-12899: If the template input point no longer exists, then mark the candidate as deleted.
              AdFunctionTemplateInputPointEntity templateInputPoint = adFunctionTemplate.getInputPointNullIfNotExists(templateInputPointId);
              if (templateInputPoint != null) {

                AdFunctionInstanceEligiblePoint inputPoint = (AdFunctionInstanceEligiblePoint) portfolio.getChildNodeNullIfNotExists(inputPointId);
                
                // If the point could not be found, then it was deleted. In this case, we do not instantiate the candidate input point. 
                // We mark the candidate as deleted, as the evaluation process would not know about the non-existence of this input point.
                if (inputPoint != null) {
                  
                  entity.addInputPoint(new AdFunctionInstanceInputPointEntity(
                      entity, 
                      templateInputPoint,
                      inputPoint,
                      Integer.valueOf(subscript++)));
                  
                } else {
                  LOGGER.warn(
                      "Marking AD Function Instance Candidate: [{}] as deleted, because input point: [{}] no longer exists",
                      entity.getPersistentIdentity(),
                      inputPointId);
                  portfolio.addInvalidAdFunctionInstanceCandidate(entity);
                  portfolio.setIsModified("invalidAdFunctionInstanceCandidates");
                }
                
              } else {
                LOGGER.warn(
                    "Marking AD Function Instance Candidate: [{}] as deleted, because template input point: [{}] no longer exists",
                    entity.getPersistentIdentity(),
                    templateInputPointId);
                portfolio.addInvalidAdFunctionInstanceCandidate(entity);
                portfolio.setIsModified("invalidAdFunctionInstanceCandidates");
              }
            }
          }          
        } else {

          /*
          INSTANCE:
          {
              "id" : 52865,
              "equipment_id" : 115320,
              "template_id" : 79,
              
              "template_input_point_id" : "{181,182}",
              "input_point_id" : "{115965,115980}",
              "input_point_subscript" : "{0,0}",
              
              "template_output_point_id" : "{1122754}",
              "output_point_id" : "{1122754}",
              
              "template_input_const_id" : "{230,228,229}",
              "input_const_value" : "{55,60,2.0}",
              
              "candidate_json" : null
          }
          */      
          if (functionType.equals(FunctionType.RULE)) {
            
            entity = new AdRuleFunctionInstanceEntity(
                id,
                equipment,
                (AdRuleFunctionTemplateEntity)adFunctionTemplate,
                false, // isCandidate
                false, // isIgnored
                templateVersion,
                instanceVersion);
                        
          } else if (functionType.equals(FunctionType.COMPUTED_POINT)) {

            entity = new AdComputedPointFunctionInstanceEntity(
                id,
                equipment,
                (AdComputedPointFunctionTemplateEntity)adFunctionTemplate,
                false, // isCandidate
                false, // isIgnored
                templateVersion,
                instanceVersion);
                        
          } else {
            throw new IllegalStateException("Unsupported function type: ["
                + functionType.getName() 
                + "]");
          }
          
          if (equipment != null) {
            
            String s = dto.getTemplateInputPointId();
            if (s != null && !s.trim().equalsIgnoreCase("{NULL}") && !s.trim().equalsIgnoreCase("{}")) {
             
              String[] templateInputPointIds = dto.getTemplateInputPointId().replace("{", "").replace("}", "").replace("\"", "").split(",");
              String[] inputPointIds = dto.getInputPointId().replace("{", "").replace("}", "").replace("\"", "").split(",");
              String[] inputPointSubscripts = dto.getInputPointSubscript().replace("{", "").replace("}", "").replace("\"", "").split(",");
              for (int i=0; i < templateInputPointIds.length; i++) {
                
                Integer templateInputPointId = Integer.parseInt(templateInputPointIds[i]);
                Integer inputPointId = Integer.parseInt(inputPointIds[i]);
                Integer inputPointSubscript = Integer.parseInt(inputPointSubscripts[i]);
                
                AdFunctionTemplateInputPointEntity templateInputPoint = adFunctionTemplate.getInputPoint(templateInputPointId); 
                AdFunctionInstanceEligiblePoint inputPoint = (AdFunctionInstanceEligiblePoint) portfolio.getChildNodeNullIfNotExists(inputPointId);
                
                // If the point could not be found, then it was deleted. In this case, we do not instantiate the instance input point. 
                // We mark the instance as deleted, as the evaluation process would not know about the non-existence of this input point.
                if (inputPoint != null) {

                  entity.addInputPoint(new AdFunctionInstanceInputPointEntity(
                      entity, 
                      templateInputPoint,
                      inputPoint,
                      inputPointSubscript));
                  
                } else {
                  entity.setIsDeleted();
                  LOGGER.warn(
                      "Marking AD Function Instance: [{}] as deleted, because input point: [{}] no longer exists",
                      entity.getPersistentIdentity(),
                      inputPointId);
                }
              }
            }
            
            s = dto.getTemplateInputConstId();
            if (s != null && !s.trim().equalsIgnoreCase("{NULL}") && !s.trim().equalsIgnoreCase("{}")) {

              String[] templateInputConstantIds = dto.getTemplateInputConstId().replace("{", "").replace("}", "").replace("\"", "").split(",");
              String[] inputConstantValues = dto.getInputConstValue().replace("{", "").replace("}", "").replace("\"", "").split(",");
              for (int i=0; i < templateInputConstantIds.length; i++) {
                
                Integer templateInputConstantId = Integer.parseInt(templateInputConstantIds[i]);
                String inputConstantValue = inputConstantValues[i];
                
                AdFunctionTemplateInputConstantEntity templateInputConstant = adFunctionTemplate.getInputConstant(templateInputConstantId);
                
                entity.addInputConstant(new AdFunctionInstanceInputConstantEntity(
                    entity, 
                    templateInputConstant,
                    inputConstantValue));
              }          
            }            

            s = dto.getTemplateOutputPointId();
            if (s != null && !s.trim().equalsIgnoreCase("{NULL}") && !s.trim().equalsIgnoreCase("{}")) {

              String[] templateOutputPointIds = dto.getTemplateOutputPointId().replace("{", "").replace("}", "").replace("\"", "").split(",");
              String[] outputPointIds = dto.getOutputPointId().replace("{", "").replace("}", "").replace("\"", "").split(",");
              for (int i=0; i < templateOutputPointIds.length; i++) {
                
                Integer templateOutputPointId = Integer.parseInt(templateOutputPointIds[i]);
                Integer outputPointId = Integer.parseInt(outputPointIds[i]);
                
                AdFunctionTemplateOutputPointEntity templateOutputPoint = adFunctionTemplate.getOutputPoint(templateOutputPointId);

                AsyncComputedPointEntity outputPoint = (AsyncComputedPointEntity) portfolio.getChildNodeNullIfNotExists(outputPointId);
                
                if (outputPoint != null) {

                  entity.addOutputPoint(new AdFunctionInstanceOutputPointEntity(
                      entity, 
                      templateOutputPoint,
                      outputPoint));
                  
                } else {
                  LOGGER.error("{}: Cannot find node output point with id: [{}]",
                      entity.getClassAndPersistentIdentity(),
                      outputPointId);
                }
              }           
            }
          } else {
            LOGGER.warn(
                "Marking AD Function Instance: [{}] as deleted, because equipment: [{}] no longer exists",
                entity.getPersistentIdentity(),
                dto.getEquipmentId());
            portfolio.addInvalidAdFunctionInstanceCandidate(entity);
          }
        }          
      } catch (Exception e) {

        throw new IllegalStateException("Unable to map: "
            + dto 
            + "\n error: "
            + e.getMessage(), e);
      }
      return entity;
    }
  }  
}