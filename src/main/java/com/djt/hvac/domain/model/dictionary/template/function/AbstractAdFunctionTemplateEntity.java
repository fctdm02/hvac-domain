//@formatter:off
package com.djt.hvac.domain.model.dictionary.template.function;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.djt.hvac.domain.model.common.AbstractPersistentEntity;
import com.djt.hvac.domain.model.common.dsl.tagquery.TagQueryExpression;
import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.common.mapper.DtoMapper;
import com.djt.hvac.domain.model.common.validation.SimpleValidationMessage;
import com.djt.hvac.domain.model.common.validation.SimpleValidationMessage.MessageType;
import com.djt.hvac.domain.model.dictionary.TagEntity;
import com.djt.hvac.domain.model.dictionary.container.AdFunctionTemplatesContainer;
import com.djt.hvac.domain.model.dictionary.container.DictionaryContext;
import com.djt.hvac.domain.model.dictionary.dto.function.AdFunctionTemplateDto;
import com.djt.hvac.domain.model.dictionary.energyexchange.AbstractEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.enums.TagGroupType;
import com.djt.hvac.domain.model.dictionary.template.function.rule.AdRuleFunctionTemplateEntity;

/**
 * @author tommyers
 *
 */
public abstract class AbstractAdFunctionTemplateEntity extends AbstractPersistentEntity {
  private static final long serialVersionUID = 1L;
  
  private final AdFunctionEntity adFunction;
  private String name;
  private final String displayName;
  private final String description;
  private final AbstractEnergyExchangeTypeEntity energyExchangeType;
  private final String nodeFilterExpression;
  private final String tupleConstraintExpression;
  private final Boolean isBeta;
  private Integer version;
  private final Set<AdFunctionTemplateInputConstantEntity> inputConstants = new TreeSet<>();
  private final Set<AdFunctionTemplateInputPointEntity> inputPoints = new TreeSet<>();
  private final Set<AdFunctionTemplateOutputPointEntity> outputPoints = new TreeSet<>();
  
  public AbstractAdFunctionTemplateEntity(
      Integer persistentIdentity,
      AdFunctionEntity adFunction,
      String name,
      String displayName,
      String description,
      AbstractEnergyExchangeTypeEntity energyExchangeType,
      String nodeFilterExpression,
      String tupleConstraintExpression,
      Boolean isBeta,
      Integer version) {
    super(persistentIdentity);
    requireNonNull(adFunction, "adFunction cannot be null");
    requireNonNull(name, "name cannot be null");
    requireNonNull(displayName, "displayName cannot be null");
    requireNonNull(description, "description cannot be null");
    requireNonNull(energyExchangeType, "energyExchangeType cannot be null");
    requireNonNull(version, "version cannot be null");
    this.adFunction = adFunction;
    this.name = name;
    this.displayName = displayName;
    this.description = description;
    this.energyExchangeType = energyExchangeType;
    this.isBeta = isBeta;
    this.version = version;

    // Validate the node filter expression.
    try {
      if (nodeFilterExpression != null && !nodeFilterExpression.trim().isEmpty()) {
        
        if (nodeFilterExpression.equals("multiZone, dualDuct, and tripleDuct")) {
          nodeFilterExpression = "multiZone && dualDuct && tripleDuct"; 
        }
        
        TagQueryExpression.parse(nodeFilterExpression);  
      }
    } catch (Exception e) {
      throw new IllegalArgumentException(getDisplayName()
          + ": Invalid nodeFilterExpression: ["
          + nodeFilterExpression
          + "], error: ["
          + e.getMessage()
          + "]", e);
    }
    this.nodeFilterExpression = nodeFilterExpression;

    // Validate the tuple constraint expression.
    try {
      if (tupleConstraintExpression != null && !tupleConstraintExpression.trim().isEmpty()) {
        TagQueryExpression.parse(tupleConstraintExpression);  
      }
    } catch (Exception e) {
      throw new IllegalArgumentException(getDisplayName()
          + ": Invalid tupleConstraintExpression: ["
          + tupleConstraintExpression
          + "], error: ["
          + e.getMessage()
          + "]", e);
    }
    this.tupleConstraintExpression = tupleConstraintExpression;
  }

  public AdFunctionEntity getAdFunction() {
    return adFunction;
  }
  
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
  
  public String getDisplayName() {
    return displayName;
  }
  
  public String getDescription() {
    return description;
  }

  public AbstractEnergyExchangeTypeEntity getEnergyExchangeType() {
    return energyExchangeType;
  }

  public String getNodeFilterExpression() {
    return nodeFilterExpression;
  }

  public String getNodeFilterExpressionNullIfNotExists() {
    if (nodeFilterExpression == null || nodeFilterExpression.trim().equals("")) {
      return "NULL";
    }
    return nodeFilterExpression;
  }

  public String getTupleConstraintExpression() {
    return tupleConstraintExpression;
  }
  
  public String getTupleConstraintExpressionNullIfNotExists() {
    if (tupleConstraintExpression == null || tupleConstraintExpression.trim().equals("")) {
      return "NULL";
    }
    return tupleConstraintExpression;
  }

  public Boolean getIsBeta() {
    return isBeta;
  }
  
  public Integer getVersion() {
    return version;
  }
  
  public void setVersion(Integer version) {
    
    if (!version.equals(this.version)) {
      
      this.version = version;
      setIsModified("version");
    }
  }  
  
  public boolean addInputConstant(AdFunctionTemplateInputConstantEntity inputConstant) throws EntityAlreadyExistsException {
    return addChild(inputConstants, inputConstant, this);
  }
  
  public Set<AdFunctionTemplateInputConstantEntity> getInputConstants() {
    return inputConstants;
  }

  public boolean addInputPoint(AdFunctionTemplateInputPointEntity inputPoint) throws EntityAlreadyExistsException {
    return addChild(inputPoints, inputPoint, this);
  }

  public Set<AdFunctionTemplateInputPointEntity> getInputPoints() {
    return inputPoints;
  }

  public AdFunctionTemplateInputPointEntity getInputPoint(Integer inputPointId) {
    
    AdFunctionTemplateInputPointEntity ip = getInputPointNullIfNotExists(inputPointId);
    if (ip != null) {
      return ip;
    }
    throw new IllegalStateException("AD function template input point with id: "
        + inputPointId
        + " not found.");
  }
  
  public AdFunctionTemplateInputPointEntity getInputPointNullIfNotExists(Integer inputPointId) {
    
    if (inputPointId == null) {
      throw new IllegalStateException("Input point cannot be null");
    }
    
    Iterator<AdFunctionTemplateInputPointEntity> iterator = inputPoints.iterator();
    while (iterator.hasNext()) {
      AdFunctionTemplateInputPointEntity inputPoint = iterator.next();
      if (inputPoint.getPersistentIdentity().equals(inputPointId)) {
        return inputPoint;
      }
    }
    return null;
  }

  public AdFunctionTemplateInputPointEntity getInputPointByName(String name) {
    
    AdFunctionTemplateInputPointEntity ip = getInputPointByNameNullIfNotExists(name);
    if (ip != null) {
      return ip;
    }
    throw new IllegalStateException("AD function template input point with name: "
        + name
        + " not found.");
  }
  
  public AdFunctionTemplateInputPointEntity getInputPointByNameNullIfNotExists(String name) {
    
    if (name == null) {
      throw new IllegalStateException("Name cannot be null");
    }
    
    Iterator<AdFunctionTemplateInputPointEntity> iterator = inputPoints.iterator();
    while (iterator.hasNext()) {
      AdFunctionTemplateInputPointEntity inputPoint = iterator.next();
      if (inputPoint.getName().equals(name)) {
        return inputPoint;
      }
    }
    return null;
  }
  
  public AdFunctionTemplateInputPointEntity getInputPointBySequenceNumber(Integer sequenceNumber) {
    
    AdFunctionTemplateInputPointEntity ip = getInputPointNullBySequenceNumberIfNotExists(sequenceNumber);
    if (ip != null) {
      return ip;
    }
    throw new IllegalStateException("AD function template input point with sequence number: "
        + sequenceNumber
        + " not found.");
  }
  
  public AdFunctionTemplateInputPointEntity getInputPointNullBySequenceNumberIfNotExists(Integer sequenceNumber) {
    
    if (sequenceNumber == null) {
      throw new IllegalStateException("Sequence number cannot be null");
    }
    
    Iterator<AdFunctionTemplateInputPointEntity> iterator = inputPoints.iterator();
    while (iterator.hasNext()) {
      AdFunctionTemplateInputPointEntity inputPoint = iterator.next();
      if (inputPoint.getSequenceNumber().equals(sequenceNumber)) {
        return inputPoint;
      }
    }
    return null;
  }  
  
  public AdFunctionTemplateInputConstantEntity getInputConstant(Integer inputConstantId) {
    
    Iterator<AdFunctionTemplateInputConstantEntity> iterator = inputConstants.iterator();
    while (iterator.hasNext()) {
      AdFunctionTemplateInputConstantEntity inputConstant = iterator.next();
      if (inputConstant.getPersistentIdentity().equals(inputConstantId)) {
        return inputConstant;
      }
    }
    throw new IllegalStateException("AD function template input constant with id: "
        + inputConstantId
        + " not found.");
  }  

  public AdFunctionTemplateInputConstantEntity getInputConstantNullIfNotExists(Integer inputConstantId) {
    
    if (inputConstantId == null) {
      throw new IllegalStateException("Input constant cannot be null");
    }
    
    Iterator<AdFunctionTemplateInputConstantEntity> iterator = inputConstants.iterator();
    while (iterator.hasNext()) {
      AdFunctionTemplateInputConstantEntity inputConstant = iterator.next();
      if (inputConstant.getPersistentIdentity().equals(inputConstant)) {
        return inputConstant;
      }
    }
    return null;
  }  
  
  public AdFunctionTemplateInputConstantEntity getInputConstant(String inputConstantName) {
    
    Iterator<AdFunctionTemplateInputConstantEntity> iterator = inputConstants.iterator();
    while (iterator.hasNext()) {
      AdFunctionTemplateInputConstantEntity inputConstant = iterator.next();
      if (inputConstant.getName().equals(inputConstantName) || inputConstant.getName().toUpperCase().equals(inputConstantName)) {
        return inputConstant;
      }
    }
    return null;
  }
  
  public AdFunctionTemplateOutputPointEntity getOutputPoint(Integer outputPointId) {
    
    Iterator<AdFunctionTemplateOutputPointEntity> iterator = outputPoints.iterator();
    while (iterator.hasNext()) {
      AdFunctionTemplateOutputPointEntity outputPoint = iterator.next();
      if (outputPoint.getPersistentIdentity().equals(outputPointId)) {
        return outputPoint;
      }
    }
    throw new IllegalStateException("AD function template output point with id: "
        + outputPointId
        + " not found.");
  }

  public AdFunctionTemplateOutputPointEntity getOutputPointBySequenceNumber(Integer sequenceNumber) {
    
    Iterator<AdFunctionTemplateOutputPointEntity> iterator = outputPoints.iterator();
    while (iterator.hasNext()) {
      AdFunctionTemplateOutputPointEntity outputPoint = iterator.next();
      if (outputPoint.getSequenceNumber().equals(sequenceNumber)) {
        return outputPoint;
      }
    }
    throw new IllegalStateException("AD function template output point with sequence number: "
        + sequenceNumber
        + " not found.");
  }
  
  public boolean addOutputPoint(AdFunctionTemplateOutputPointEntity outputPoint) throws EntityAlreadyExistsException {
    return addChild(outputPoints, outputPoint, this);
  }
  
  public Set<AdFunctionTemplateOutputPointEntity> getOutputPoints() {
    return outputPoints;
  }
  
  public abstract String getFaultOrReferenceNumber();
  public abstract String getFullDisplayName();
  
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
                getFaultOrReferenceNumber()
                    + ": nodeFilterExpression: ["
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
          getFaultOrReferenceNumber()
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

          AdFunctionTemplateInputPointEntity ip = getInputPointByNameNullIfNotExists(pointName);
          if (ip == null) {
            
            simpleValidationMessages.add(new SimpleValidationMessage(
                MessageType.ERROR,
                getNaturalIdentity(),
                "tupleConstraintExpression",
                getFaultOrReferenceNumber()
                    + ": tupleConstraintExpression: ["
                    + tupleConstraintExpression
                    + "] specifies point name: ["
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
            getFaultOrReferenceNumber()
                + ": invalid tupleConstraintExpression: ["
                + this.tupleConstraintExpression
                + "] error: ["
                + e.getMessage() 
                + "]."));
    }      
    
    if (inputPoints.isEmpty()) {

      simpleValidationMessages.add(new SimpleValidationMessage(
          MessageType.ERROR,
          this.getNaturalIdentity(),
          "inputPoints",
          "AD function template does not have any input points")); 
      
    } else {
      
      //Map<Integer, AdFunctionTemplateInputPointEntity> inputPointSequenceNumbers = new TreeMap<>();
      Map<String, AdFunctionTemplateInputPointEntity> inputPointNames = new TreeMap<>();
      Iterator<AdFunctionTemplateInputPointEntity> iterator = inputPoints.iterator();
      while (iterator.hasNext()) {
        
        AdFunctionTemplateInputPointEntity inputPoint = iterator.next();

        /*
        if (inputPoint.getCurrentObjectExpression() == null || inputPoint.getCurrentObjectExpression().trim().isEmpty()) {

          // Verify that the input point tags, thus, the point template, is compatible with the given energy exchange type.
          AbstractEnergyExchangeTypeEntity energyExchangeType = getEnergyExchangeType(); 
          Set<EnergyExchangeLevelPointTemplateEntity> pts = DictionaryContext
              .getNodeTagTemplatesContainer()
              .getEnergyExchangeLevelPointTemplatesForEnergyExchangeType(energyExchangeType);
          
          Set<AbstractNodeTagTemplateEntity> energyExchangePointTemplates = new HashSet<>();
          for (EnergyExchangeLevelPointTemplateEntity pt: pts) {
            energyExchangePointTemplates.add(pt);
          }
          
          Set<AbstractNodeTagTemplateEntity> matchingPointTemplates = DictionaryContext
              .getNodeTagTemplatesContainer()
              .getAllMatchingPointTemplateByTags(inputPoint.getTags());
          
          Set<AbstractNodeTagTemplateEntity> intersection = new HashSet<>();
          intersection.addAll(matchingPointTemplates);
          
          intersection.retainAll(energyExchangePointTemplates);
          
          if (intersection.isEmpty()) {
            
            //if (pointTemplate instanceof EnergyExchangeLevelPointTemplateEntity) {
            //
            //  simpleValidationMessages.add(new SimpleValidationMessage(
            //      MessageType.ERROR,
            //      this.getNaturalIdentity(),
            //      "input point: [" + inputPoint.getName() + "]",
            //      "point template: [" 
            //          + pointTemplate 
            //          + "] is incompatible with energy exchange type: ["
            //          + energyExchangeType
            //          + "], valid energy exchange types are: "
            //          + ((EnergyExchangeLevelPointTemplateEntity)pointTemplate).getParentEnergyExchangeTypes()));
            //          
            //} else {
              simpleValidationMessages.add(new SimpleValidationMessage(
                  MessageType.INFO,
                  this.getNaturalIdentity(),
                  "input point: [" + inputPoint.getName() + "]",
                  "point template: [" 
                      + matchingPointTemplates 
                      + "] is incompatible with energy exchange type: ["
                      + energyExchangeType
                      + "]"));            
            //}
          }
        }
        */
        
        inputPoint.validateSimple(simpleValidationMessages);
        
        //Integer inputPointSequenceNumber = inputPoint.getSequenceNumber();
        String inputPointName = inputPoint.getName();
        
        if (inputPointNames.keySet().contains(inputPointName)) {
          
          simpleValidationMessages.add(new SimpleValidationMessage(
              MessageType.ERROR,
              this.getNaturalIdentity(),
              "input point: [" + inputPoint.getName() + "]",
              "AD function template input point: ["
                  + inputPoint
                  + "] has a duplicate name: [" 
                  + inputPointName 
                  + "] with: ["
                  + inputPointNames.get(inputPointName)
                  + "]"));           
        }
        inputPointNames.put(inputPointName, inputPoint);        
        /*
        if (inputPointSequenceNumbers.keySet().contains(inputPointSequenceNumber)) {
         
          simpleValidationMessages.add(new SimpleValidationMessage(
              MessageType.ERROR,
              this.getNaturalIdentity(),
              "inputPoints",
              "AD function template input point: ["
                  + inputPoint
                  + "] has a duplicate sequence number: [" 
                  + inputPointSequenceNumber 
                  + "] with: ["
                  + inputPointSequenceNumbers.get(inputPointSequenceNumber)
                  + "]"));           
        }
        inputPointSequenceNumbers.put(inputPointSequenceNumber, inputPoint);
        */
      }
    }

    if (!inputConstants.isEmpty()) {

      Iterator<AdFunctionTemplateInputConstantEntity> iterator = inputConstants.iterator();
      while (iterator.hasNext()) {
        
        iterator.next().validateSimple(simpleValidationMessages);
      }
    }

    if (outputPoints.isEmpty()) {

      simpleValidationMessages.add(new SimpleValidationMessage(
          MessageType.ERROR,
          this.getNaturalIdentity(),
          "outputPoints",
          "AD function template does not have any output points")); 
      
    } else {
      
      if (this instanceof AdRuleFunctionTemplateEntity && outputPoints.size() > 1) {
        
        simpleValidationMessages.add(new SimpleValidationMessage(
            MessageType.ERROR,
            ((AdRuleFunctionTemplateEntity)this).getFaultNumber(),
            "inputConstants",
            "AD rule function templates can only have one output point")); 
        
      } else {

        Iterator<AdFunctionTemplateOutputPointEntity> iterator = outputPoints.iterator();
        while (iterator.hasNext()) {
          
          iterator.next().validateSimple(simpleValidationMessages);
        }
      }
    }
  }
  
  public String getSignature() {
    
    StringBuilder sb = new StringBuilder()
        .append("adFunction=")
        .append(adFunction.getSignature())
        .append(" name=")
        .append(name)
        .append(" displayName=")
        .append(name)
        .append(" description=")
        .append(description)
        .append(" energyExchangeType=")
        .append(energyExchangeType)
        .append(" nodeFilterExpression=")
        .append(getNodeFilterExpressionNullIfNotExists())
        .append(" tupleConstraintExpression=")
        .append(getTupleConstraintExpressionNullIfNotExists())
        .append(" isBeta=")
        .append(isBeta)
        .append(" inputConstants=");
    
    for (AdFunctionTemplateInputConstantEntity ic: inputConstants) {
      sb.append(ic.getSignature());
    }
    sb.append(" inputPoints=");
    for (AdFunctionTemplateInputPointEntity ip: inputPoints) {
      sb.append(ip.getSignature());
    }
    
    return sb.toString();
  }
  
  public static class Mapper implements DtoMapper<AdFunctionTemplatesContainer, AbstractAdFunctionTemplateEntity, AdFunctionTemplateDto> {

    private static final Mapper INSTANCE = new Mapper();

    private Mapper() {}

    public static Mapper getInstance() {
      return INSTANCE;
    }

    public List<AdFunctionTemplateDto> mapEntitiesToDtos(List<AbstractAdFunctionTemplateEntity> entities) {

      List<AdFunctionTemplateDto> list = new ArrayList<>();
      for (AbstractAdFunctionTemplateEntity entity: entities) {
        list.add(mapEntityToDto(entity));
      }
      return list;
    }

    @Override
    public AdFunctionTemplateDto mapEntityToDto(AbstractAdFunctionTemplateEntity entity) {
      
      return AdFunctionTemplatesContainer.mapToDto(entity);
    }

    public List<AbstractAdFunctionTemplateEntity> mapDtosToEntities(
        AdFunctionTemplatesContainer adFunctionTemplatesContainer,
        List<AdFunctionTemplateDto> dtos) {

      List<AbstractAdFunctionTemplateEntity> list = new ArrayList<>();
      for (AdFunctionTemplateDto dto: dtos) {
        list.add(mapDtoToEntity(adFunctionTemplatesContainer, dto));
      }
      return list;
    }
    
    @Override
    public AbstractAdFunctionTemplateEntity mapDtoToEntity(
        AdFunctionTemplatesContainer adFunctionTemplatesContainer,
        AdFunctionTemplateDto dto) {
      
      try {
        
        return AdFunctionTemplatesContainer.mapDtoToEntity(dto);
        
      } catch (Exception e) {
        throw new IllegalStateException("Unable to map AD function template: ["
            + dto.getReferenceNumber()
            + "], error: "
            + e.getMessage() , e);
      }      
    }
  }
}
//@formatter:on