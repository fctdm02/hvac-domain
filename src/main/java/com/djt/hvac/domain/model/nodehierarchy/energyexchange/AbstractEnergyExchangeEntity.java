//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.energyexchange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.RemediationStrategy;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.dictionary.TagEntity;
import com.djt.hvac.domain.model.dictionary.container.DictionaryContext;
import com.djt.hvac.domain.model.dictionary.container.NodeTagTemplatesContainer;
import com.djt.hvac.domain.model.dictionary.energyexchange.EquipmentEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.energyexchange.enums.EnergyExchangeSystemType;
import com.djt.hvac.domain.model.dictionary.enums.FunctionType;
import com.djt.hvac.domain.model.dictionary.enums.TagGroupType;
import com.djt.hvac.domain.model.dictionary.enums.UnitSystem;
import com.djt.hvac.domain.model.dictionary.template.function.AbstractAdFunctionTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.function.AdFunctionTemplateOutputPointEntity;
import com.djt.hvac.domain.model.dictionary.template.nodetag.AbstractNodeTagTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.nodetag.PointTemplateEntity;
import com.djt.hvac.domain.model.function.AbstractAdFunctionInstanceEntity;
import com.djt.hvac.domain.model.function.AdFunctionErrorMessagesEntity;
import com.djt.hvac.domain.model.function.AdFunctionEvaluator;
import com.djt.hvac.domain.model.function.AdFunctionInstanceOutputPointEntity;
import com.djt.hvac.domain.model.function.AdFunctionRemediationStrategyFinder;
import com.djt.hvac.domain.model.function.dto.RedGreenDto;
import com.djt.hvac.domain.model.nodehierarchy.AbstractNodeEntity;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BuildingEntity;
import com.djt.hvac.domain.model.nodehierarchy.dto.EnergyExchangeSystemEdgeDto;
import com.djt.hvac.domain.model.nodehierarchy.mapper.PortfolioDtoMapper;
import com.djt.hvac.domain.model.nodehierarchy.point.AbstractPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.AsyncComputedPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.custom.CustomAsyncComputedPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.mapped.MappablePointEntity;
import com.djt.hvac.domain.model.nodehierarchy.validation.DeleteAndReAddTagsFromTemplateStrategyImpl;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public abstract class AbstractEnergyExchangeEntity extends AbstractNodeEntity implements EnergyExchangeEntity {
  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractEnergyExchangeEntity.class);
  
  private static final Set<EnergyExchangeEntity> EMPTY_SET = ImmutableSet.of();
  private static final List<EnergyExchangeSystemEdgeDto> EMPTY_LIST = ImmutableList.of();
  
  private static final Set<AbstractAdFunctionInstanceEntity> EMPTY_AD_FUNCTION_SET = ImmutableSet.of();
  private static final Set<Integer> EMPTY_ID_SET = ImmutableSet.of();
  
  private final Map<EnergyExchangeSystemType, Set<EnergyExchangeEntity>> parentEnergyExchangeSystemNodes = new HashMap<>();
  private final Map<EnergyExchangeSystemType, Set<EnergyExchangeEntity>> childEnergyExchangeSystemNodes = new HashMap<>();

  private Set<AbstractAdFunctionInstanceEntity> adFunctionInstanceCandidates = new TreeSet<>();
  private Set<AbstractAdFunctionInstanceEntity> deletedAdFunctionInstanceCandidates;
  
  private Set<AbstractAdFunctionInstanceEntity> adFunctionInstances = new TreeSet<>();
  private Set<AbstractAdFunctionInstanceEntity> deletedAdFunctionInstances;
  
  private List<EnergyExchangeSystemEdgeDto> addedEnergyExchangeSystemEdges;
  private List<EnergyExchangeSystemEdgeDto> removedEnergyExchangeSystemEdges;

  // key is AD function template id
  private final Map<Integer, AdFunctionErrorMessagesEntity> adFunctionErrorMessages = new HashMap<>();
  private Set<AdFunctionErrorMessagesEntity> addedAdFunctionErrorMessages = new HashSet<>();
  private Set<AdFunctionErrorMessagesEntity> removedAdFunctionErrorMessages = new HashSet<>();  
  
  private transient Set<String> _metadataTags; // Tag Group 8
  
  @Override
  protected void resetTransientAttributes() {
    
    super.resetTransientAttributes();
    _metadataTags = null;
  }
  
  public AbstractEnergyExchangeEntity() {}
  
  public AbstractEnergyExchangeEntity(
      Integer persistentIdentity,
      AbstractNodeEntity parentNode,
      String name,
      String displayName,
      String uuid,
      String createdAt,
      String updatedAt,
      Set<TagEntity> nodeTags) {
    super(
        persistentIdentity, 
        parentNode, 
        name, 
        displayName,
        uuid,
        createdAt,
        updatedAt,
        nodeTags);
  }
  
  // RECURSIVE
  public Set<EnergyExchangeEntity> getAncestorEnergyExchangeSystemNodes(EnergyExchangeSystemType energyExchangeSystemType) {

    initEnergyExchangeSystemCollections();
    
    Set<EnergyExchangeEntity> nodes = parentEnergyExchangeSystemNodes.get(energyExchangeSystemType);
    if (nodes == null) {
      return new HashSet<>();
    } else if (!nodes.isEmpty()) {
      
      Set<EnergyExchangeEntity> newNodes = new HashSet<>();
      
      for (EnergyExchangeEntity node: nodes) {
        if (node != null) {
          newNodes.addAll(node.getAncestorEnergyExchangeSystemNodes(energyExchangeSystemType));  
        } else {
          LOGGER.error("{} has a null ancestor node for {}",
              this,
              energyExchangeSystemType);
        }
      }
      
      nodes.addAll(newNodes);
    }
    
    return nodes;
  }

  // RECURSIVE
  public Set<EnergyExchangeEntity> getDescendantEnergyExchangeSystemNodes(EnergyExchangeSystemType energyExchangeSystemType) {

    initEnergyExchangeSystemCollections();
    
    Set<EnergyExchangeEntity> nodes = childEnergyExchangeSystemNodes.get(energyExchangeSystemType);
    if (nodes == null) {
      return new HashSet<>();
    } else if (!nodes.isEmpty()) {
      
      Set<EnergyExchangeEntity> newNodes = new HashSet<>();
      
      for (EnergyExchangeEntity node: nodes) {
        if (node != null) {
          newNodes.addAll(node.getDescendantEnergyExchangeSystemNodes(energyExchangeSystemType));  
        } else {
          LOGGER.error("{} has a null descendant node for {}",
              this,
              energyExchangeSystemType);
        }
      }
      
      nodes.addAll(newNodes);
    }
    
    return nodes;
  }
  
  public Map<EnergyExchangeSystemType, Set<EnergyExchangeEntity>> getAllParentEnergyExchangeSystemNodes() {
    
    return parentEnergyExchangeSystemNodes;
  }
  
  public Set<EnergyExchangeEntity> getParentEnergyExchangeSystemNodes(EnergyExchangeSystemType energyExchangeSystemType) {
    
    initEnergyExchangeSystemCollections();
    
    Set<EnergyExchangeEntity> nodes = parentEnergyExchangeSystemNodes.get(energyExchangeSystemType);
    if (nodes == null) {
      nodes = EMPTY_SET;
    }
    return nodes;
  }
  
  public EquipmentEntity getParentEquipmentNullIfNotExists() {
    
    if (this instanceof EquipmentEntity) {

      Set<EnergyExchangeEntity> parentNodes = getParentEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
      if (parentNodes.size() > 1) {
        throw new IllegalStateException("There can only be one air supply parent for equipment: ["
            + this
            + "], yet encountered: ["
            + parentNodes
            + "]");
      }
      for (EnergyExchangeEntity parentNode: parentNodes) {
        
        if (parentNode instanceof EquipmentEntity) {
          
          EquipmentEntity parentEquipment = (EquipmentEntity)parentNode;
          EquipmentEnergyExchangeTypeEntity parentEquipmentType = parentEquipment.getEquipmentTypeNullIfNotExists();
          if (parentEquipmentType != null && parentEquipmentType.getName().equalsIgnoreCase("ahu")) {
            
            return parentEquipment;
          }
        }
      }
      return null;
    }
    throw new IllegalStateException("getParentEquipmentNullIfNotExists() can only be invoked on equipment, but was attempted with: "
        + this.getClassAndNaturalIdentity());
  }
  
  public Set<EquipmentEntity> getEquipmentHierarchyChildEquipment() {
    
    if (this instanceof EquipmentEntity) {

      Set<EquipmentEntity> equipmentHierarchyChildEquipment = new TreeSet<>();
      for (EnergyExchangeEntity childNode: getChildEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY)) {
        
        if (childNode instanceof EquipmentEntity) {
          
          equipmentHierarchyChildEquipment.add((EquipmentEntity)childNode);
        }
      }
      return equipmentHierarchyChildEquipment;
    }
    throw new IllegalStateException("getParentEquipmentNullIfNotExists() can only be invoked on equipment, but was attempted with: "
        + this.getClassAndNaturalIdentity());
  }

  public boolean isAncestorEnergyExchangeSystemNode(
      EnergyExchangeSystemType energyExchangeSystemType,
      EnergyExchangeEntity energyExchangeSystemNode) {
    
    initEnergyExchangeSystemCollections();
    
    boolean isAncestorEnergyExchangeSystemNode = false;
    for (EnergyExchangeEntity parentEnergyExchangeEntity: getParentEnergyExchangeSystemNodes(energyExchangeSystemType)) {
      
      if (energyExchangeSystemNode.equals(parentEnergyExchangeEntity)) {
        
        isAncestorEnergyExchangeSystemNode = true;
        break;
      }
    }
    return isAncestorEnergyExchangeSystemNode;
  }
  
  public boolean isDescendantEnergyExchangeSystemNode(
      EnergyExchangeSystemType energyExchangeSystemType,
      EnergyExchangeEntity energyExchangeSystemNode) {
    
    initEnergyExchangeSystemCollections();
    
    boolean isDescendantEnergyExchangeSystemNode = false;
    for (EnergyExchangeEntity childEnergyExchangeEntity: getChildEnergyExchangeSystemNodes(energyExchangeSystemType)) {
      
      if (energyExchangeSystemNode.equals(childEnergyExchangeEntity)) {
        
        isDescendantEnergyExchangeSystemNode = true;
        break;
      }
    }
    return isDescendantEnergyExchangeSystemNode;
  }
  
  public void addChildEnergyExchangeSystemNode(
      EnergyExchangeSystemType energyExchangeSystemType, 
      EnergyExchangeEntity childEnergyExchangeEntity,
      boolean addInverseChildRelationship)
  throws 
      EntityAlreadyExistsException {
    
    initEnergyExchangeSystemCollections();
    
    if (childEnergyExchangeEntity != null) {
      
      if (childEnergyExchangeEntity.equals(this)) {
        throw new IllegalStateException("Energy exchange system node: ["
            + getNodePath()
            + "] cannot be set as a child to itself, as it would create a cycle.");
        
      } else if (isAncestorEnergyExchangeSystemNode(energyExchangeSystemType, childEnergyExchangeEntity)) {
        
        throw new IllegalStateException("Energy exchange system node: ["
            + getNodePath()
            + "] cannot have energy exchange system node: ["
            + childEnergyExchangeEntity.getNodePath()
            + "] as a child because it already is an ancestor, causing a cycle.");
      }
      
      boolean result = childEnergyExchangeSystemNodes.get(energyExchangeSystemType).add(childEnergyExchangeEntity);
      if (result) {

        setIsModified(energyExchangeSystemType + "childEnergyExchangeSystemNodes:added");
        
        if (addInverseChildRelationship) {
          childEnergyExchangeEntity.addParentEnergyExchangeSystemNode(energyExchangeSystemType, this, false);  
        }
        
      } else {
        throw new EntityAlreadyExistsException("There already exists a child energy exchange system node: ["
            + childEnergyExchangeEntity
            + "] for energy exchange system type: ["
            + energyExchangeSystemType
            + "] for energy exchange node: ["
            + getNodePath()
            + "]");
      }
    }    
  }
  
  public void removeChildEnergyExchangeSystemNode(
      EnergyExchangeSystemType energyExchangeSystemType, 
      EnergyExchangeEntity childEnergyExchangeEntity,
      boolean addInverseChildRelationship)
  throws 
      EntityDoesNotExistException {
    
    initEnergyExchangeSystemCollections();
    
    boolean result = childEnergyExchangeSystemNodes.get(energyExchangeSystemType).remove(childEnergyExchangeEntity);
    if (result) {

      setIsModified(energyExchangeSystemType + "childEnergyExchangeSystemNodes:removed");
      
      if (addInverseChildRelationship) {
        childEnergyExchangeEntity.removeParentEnergyExchangeSystemNode(energyExchangeSystemType, this, false);
      }
      
    } else {
      throw new EntityDoesNotExistException("There does not exist a child energy exchange system node: ["
          + childEnergyExchangeEntity
          + "] for energy exchange system type: ["
          + energyExchangeSystemType
          + "].");
    }
  }  
  
  @Override
  public void addParentEnergyExchangeSystemNode(
      EnergyExchangeSystemType energyExchangeSystemType, 
      EnergyExchangeEntity parentEnergyExchangeEntity,
      boolean addInverseChildRelationship) 
  throws 
      EntityAlreadyExistsException {

    initEnergyExchangeSystemCollections();
    
    if (parentEnergyExchangeEntity != null) {
      
      if (parentEnergyExchangeEntity.equals(this)) {
        throw new IllegalStateException("Energy exchange system node: ["
            + getNodePath()
            + "] cannot be set as a parent to itself, as it would create a cycle.");
        
      } else if (isDescendantEnergyExchangeSystemNode(energyExchangeSystemType, parentEnergyExchangeEntity)) {
        
        throw new IllegalStateException("Energy exchange system node: ["
            + getNodePath()
            + "] cannot have energy exchange system node: ["
            + parentEnergyExchangeEntity.getNodePath()
            + "] as a parent because it already is a descendant, causing a cycle.");
      }
      
      boolean result = parentEnergyExchangeSystemNodes.get(energyExchangeSystemType).add(parentEnergyExchangeEntity);
      if (result) {
        
        setIsModified(energyExchangeSystemType + "parentEnergyExchangeSystemNodes:added");
        
        if (addInverseChildRelationship) {
          parentEnergyExchangeEntity.addChildEnergyExchangeSystemNode(energyExchangeSystemType, this, false);  
        }
        
      } else {
        throw new EntityAlreadyExistsException("There already exists a parent energy exchange system node: ["
            + parentEnergyExchangeEntity
            + "] for energy exchange system type: ["
            + energyExchangeSystemType
            + "].");
      }
    }
  }
  
  @Override
  public void removeParentEnergyExchangeSystemNode(
      EnergyExchangeSystemType energyExchangeSystemType, 
      EnergyExchangeEntity parentEnergyExchangeEntity,
      boolean addInverseChildRelationship) 
  throws 
      EntityDoesNotExistException {
    
    initEnergyExchangeSystemCollections();

    boolean result = getParentEnergyExchangeSystemNodes(energyExchangeSystemType).remove(parentEnergyExchangeEntity);
    if (result) {

      setIsModified(energyExchangeSystemType + "parentEnergyExchangeSystemNodes:removed");
      
      if (addInverseChildRelationship) {
        parentEnergyExchangeEntity.removeChildEnergyExchangeSystemNode(energyExchangeSystemType, this, false);
      }
      
    } else {
      throw new EntityDoesNotExistException("There does not exist a parent energy exchange system node: ["
          + parentEnergyExchangeEntity
          + "] for energy exchange system type: ["
          + energyExchangeSystemType
          + "].");
    }
  }
  
  public void setParentEnergyExchangeSystemNodes(
      EnergyExchangeSystemType energyExchangeSystemType, 
      List<EnergyExchangeEntity> incomingParents) 
  throws 
      EntityAlreadyExistsException, 
      EntityDoesNotExistException {
    
    initEnergyExchangeSystemCollections();
    
    // There exists 3 possibilities:
    // 1. An incoming node is NOT in the existing parent set: Mark it as "needs adding"
    // 2. An incoming node is ALREADY in the existing parent set: Do nothing
    // 3. An existing node is NOT in the incoming parent set: Mark it as "needs removing"
    List<EnergyExchangeEntity> existingParents = new ArrayList<>();
    existingParents.addAll(getParentEnergyExchangeSystemNodes(energyExchangeSystemType));
    
    for (int i=0; i < incomingParents.size(); i++) {
      
      EnergyExchangeEntity incomingParent = incomingParents.get(i);
      
      if (!existingParents.contains(incomingParent)) {
        
        if (addedEnergyExchangeSystemEdges == null) {
          
          addedEnergyExchangeSystemEdges = new ArrayList<>();
        }
        
        if (incomingParent.getAncestorBuilding().equals(getAncestorBuilding())) {

          EnergyExchangeSystemEdgeDto dto = new EnergyExchangeSystemEdgeDto();
          dto.setSystemTypeId(energyExchangeSystemType.getId());
          dto.setParentId(incomingParent.getPersistentIdentity());
          
          Integer childId = this.getPersistentIdentity();
          if (childId != null) {
            dto.setChildId(childId);          
          } else {
            dto.setChildNode(this);
          }
          addedEnergyExchangeSystemEdges.add(dto);
          
          addParentEnergyExchangeSystemNode(energyExchangeSystemType, incomingParent, true);
          
        } else {
          throw new IllegalArgumentException("Cannot assign ["
              + getNaturalIdentity()
              + "] to be a child of ["
              + incomingParent.getNaturalIdentity()
              + "], because the ancestor building is different");
        }
      }
    }

    for (int i=0; i < existingParents.size(); i++) {
      
      EnergyExchangeEntity existingParent = existingParents.get(i);
      
      if (!incomingParents.contains(existingParent)) {
        
        if (removedEnergyExchangeSystemEdges == null) {
          
          removedEnergyExchangeSystemEdges = new ArrayList<>();
        }
        
        EnergyExchangeSystemEdgeDto dto = new EnergyExchangeSystemEdgeDto();
        dto.setSystemTypeId(energyExchangeSystemType.getId());
        dto.setParentId(existingParent.getPersistentIdentity());
        dto.setChildId(this.getPersistentIdentity());
        removedEnergyExchangeSystemEdges.add(dto);
        
        removeParentEnergyExchangeSystemNode(energyExchangeSystemType, existingParent, true);
      }
    }
  }

  public void setChildEnergyExchangeSystemNodes(
      EnergyExchangeSystemType energyExchangeSystemType, 
      List<EnergyExchangeEntity> incomingChildren) 
  throws 
      EntityAlreadyExistsException, 
      EntityDoesNotExistException {
    
    initEnergyExchangeSystemCollections();
    
    // There exists 3 possibilities:
    // 1. An incoming node is NOT in the existing child set: Mark it as "needs adding"
    // 2. An incoming node is ALREADY in the existing child set: Do nothing
    // 3. An existing node is NOT in the incoming child set: Mark it as "needs removing"
    List<EnergyExchangeEntity> existingChildren = new ArrayList<>();
    existingChildren.addAll(getChildEnergyExchangeSystemNodes(energyExchangeSystemType));
    
    for (int i=0; i < incomingChildren.size(); i++) {
      
      EnergyExchangeEntity incomingChild = incomingChildren.get(i);
      
      if (!existingChildren.contains(incomingChild)) {
        
        if (addedEnergyExchangeSystemEdges == null) {
          
          addedEnergyExchangeSystemEdges = new ArrayList<>();
        }
        
        Set<EnergyExchangeEntity> existingAirSupplyParents = incomingChild.getParentEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
        if (!existingAirSupplyParents.isEmpty()) {
          throw new IllegalArgumentException("Cannot assign ["
              + getNodePath()
              + "] to be a parent of ["
              + incomingChild.getNodePath()
              + "], because it already has an air supply system parent of: "
              + existingAirSupplyParents);
        }

        if (incomingChild.getAncestorBuilding().equals(getAncestorBuilding())) {

          EnergyExchangeSystemEdgeDto dto = new EnergyExchangeSystemEdgeDto();
          dto.setSystemTypeId(energyExchangeSystemType.getId());
          dto.setParentId(this.getPersistentIdentity());
          dto.setChildId(incomingChild.getPersistentIdentity());
          addedEnergyExchangeSystemEdges.add(dto);
          
          addChildEnergyExchangeSystemNode(energyExchangeSystemType, incomingChild, true);
          
        } else {
          throw new IllegalArgumentException("Cannot assign ["
              + getNodePath()
              + "] to be a parent of ["
              + incomingChild.getNodePath()
              + "], because the ancestor building is different");
        }
      }
    }

    for (int i=0; i < existingChildren.size(); i++) {
      
      EnergyExchangeEntity existingChild = existingChildren.get(i);
      
      if (!incomingChildren.contains(existingChild)) {
        
        if (removedEnergyExchangeSystemEdges == null) {
          
          removedEnergyExchangeSystemEdges = new ArrayList<>();
        }
        
        EnergyExchangeSystemEdgeDto dto = new EnergyExchangeSystemEdgeDto();
        dto.setSystemTypeId(energyExchangeSystemType.getId());
        dto.setParentId(this.getPersistentIdentity());
        dto.setChildId(existingChild.getPersistentIdentity());
        removedEnergyExchangeSystemEdges.add(dto);
        
        removeChildEnergyExchangeSystemNode(energyExchangeSystemType, existingChild, true);
      }
    }
  }
    
  public List<EnergyExchangeSystemEdgeDto> getAddedEnergyExchangeSystemEdges() {
    
    if (addedEnergyExchangeSystemEdges != null) {
      return addedEnergyExchangeSystemEdges;
    }
    return EMPTY_LIST;
  }
  
  public List<EnergyExchangeSystemEdgeDto> getRemovedEnergyExchangeSystemEdges() {

    if (removedEnergyExchangeSystemEdges != null) {
      return removedEnergyExchangeSystemEdges;
    }
    return EMPTY_LIST;
  }
  
  public Set<EnergyExchangeEntity> getChildEnergyExchangeSystemNodes(EnergyExchangeSystemType energyExchangeSystemType) {
    
    initEnergyExchangeSystemCollections();
    
    Set<EnergyExchangeEntity> nodes = childEnergyExchangeSystemNodes.get(energyExchangeSystemType);
    if (nodes == null) {
      nodes = EMPTY_SET;
    }
    return nodes;
  }
  
  public Map<EnergyExchangeSystemType, Set<EnergyExchangeEntity>> getAllChildEnergyExchangeSystemNodes() {
    
    return childEnergyExchangeSystemNodes;
  }
  
  private void initEnergyExchangeSystemCollections() {
    
    initEnergyExchangeSystemCollection(parentEnergyExchangeSystemNodes);
    initEnergyExchangeSystemCollection(childEnergyExchangeSystemNodes);
  }
  
  private void initEnergyExchangeSystemCollection(
      Map<EnergyExchangeSystemType, 
      Set<EnergyExchangeEntity>> energyExchangeSystemNodes) {
    
    initEnergyExchangeSystemCollection(EnergyExchangeSystemType.CHILLED_WATER, energyExchangeSystemNodes);
    initEnergyExchangeSystemCollection(EnergyExchangeSystemType.HOT_WATER, energyExchangeSystemNodes);
    initEnergyExchangeSystemCollection(EnergyExchangeSystemType.STEAM, energyExchangeSystemNodes);
    initEnergyExchangeSystemCollection(EnergyExchangeSystemType.AIR_SUPPLY, energyExchangeSystemNodes);
  }
  
  private void initEnergyExchangeSystemCollection(
      EnergyExchangeSystemType energyExchangeSystemType, 
      Map<EnergyExchangeSystemType, Set<EnergyExchangeEntity>> energyExchangeSystemNodes) {
    
    Set<EnergyExchangeEntity> set = energyExchangeSystemNodes.get(energyExchangeSystemType);
    if (set == null) {
      
      energyExchangeSystemNodes.put(energyExchangeSystemType, new HashSet<>());
    }
  }
  
  public Set<String> getMetadataTags() {

    if (_metadataTags == null) {
      _metadataTags = new TreeSet<>();
      Iterator<TagEntity> iterator = getNodeTags().iterator();
      while (iterator.hasNext()) {

        TagEntity tag = iterator.next();
        if (tag.getTagGroupType().equals(TagGroupType.EQUIPMENT_METADATA)) {
          _metadataTags.add(tag.getName());
        }
      }
    }
    return _metadataTags;
  }
  
  public Set<TagEntity> getMetadataTagsAsTags() {

    Set<TagEntity> tags = new TreeSet<>();
    Iterator<TagEntity> iterator = getNodeTags().iterator();
    while (iterator.hasNext()) {

      TagEntity tag = iterator.next();
      if (tag.getTagGroupType().equals(TagGroupType.EQUIPMENT_METADATA)) {
        tags.add(tag);
      }
    }
    return tags;
  }

  public boolean removeMetadataTags() {
    
    return removeNodeTags(getMetadataTagsAsTags());
  }
  
  public void setMetadataTags(Set<TagEntity> tags) {
    
    Set<TagEntity> existingTags = getMetadataTagsAsTags();
    if (!existingTags.equals(tags)) {
      
      removeMetadataTags();
      addNodeTags(tags);
    }
  }
  
  @Override
  public Map<String, AbstractPointEntity> getAssignedPointTemplateHaystackTags() {
    
    Map<String, AbstractPointEntity> assignedPointTemplateHaystackTags = new HashMap<>();
    for (AbstractPointEntity point: getChildPoints()) {
      
      if (point instanceof MappablePointEntity || point instanceof CustomAsyncComputedPointEntity) {

        AbstractNodeTagTemplateEntity pointTemplate = point.getPointTemplateNullIfEmpty();
        if (pointTemplate != null) {
          
          assignedPointTemplateHaystackTags.put(pointTemplate.getNormalizedTags(), point);
        }
      }
    }
    return assignedPointTemplateHaystackTags;
  }
  
  @Override
  public void setNotModified() {
    
    super.setNotModified();
    
    this.addedEnergyExchangeSystemEdges = null;
    this.removedEnergyExchangeSystemEdges = null;
    
    this.addedAdFunctionErrorMessages.clear();
    this.removedAdFunctionErrorMessages.clear();
    
    this.deletedAdFunctionInstanceCandidates = null;
    for (AbstractAdFunctionInstanceEntity entity: adFunctionInstanceCandidates) {
      
      entity.setNotModified();
    }
    
    this.deletedAdFunctionInstances = null;
    for (AbstractAdFunctionInstanceEntity entity: adFunctionInstances) {
      
      entity.setNotModified();
    }
  }
  
  public void addDeletedAdFunctionInstanceCandidate(AbstractAdFunctionInstanceEntity entity) {
    
    Integer id = entity.getPersistentIdentity();
    if (id != null) {
      if (deletedAdFunctionInstanceCandidates == null) {
        deletedAdFunctionInstanceCandidates = new HashSet<>();
      }
      deletedAdFunctionInstanceCandidates.add(entity);
      adFunctionInstanceCandidates.remove(entity);
    }
    setIsModified("adFunctionInstanceCandidate:deleted");
  }
  
  public Set<AbstractAdFunctionInstanceEntity> getDeletedAdFunctionInstanceCandidates() {
   
    if (deletedAdFunctionInstanceCandidates != null) {
      return deletedAdFunctionInstanceCandidates;
    }
    return EMPTY_AD_FUNCTION_SET;
  }
  
  public Set<Integer> getDeletedAdFunctionInstanceCandidateIds() {
    
    if (deletedAdFunctionInstanceCandidates != null) {
      
      Set<Integer> ids = new HashSet<>();
      for (AbstractAdFunctionInstanceEntity entity: deletedAdFunctionInstanceCandidates) {
        ids.add(entity.getPersistentIdentity());
      }
      return ids;
    }
    return EMPTY_ID_SET;
  }  
  
  public void resetDeletedAdFunctionInstanceCandidates() {
    
    deletedAdFunctionInstanceCandidates = null;
  }
  
  public void addDeletedAdFunctionInstance(AbstractAdFunctionInstanceEntity entity) {

    Integer id = entity.getPersistentIdentity();
    if (id != null) {
      if (deletedAdFunctionInstances == null) {
        deletedAdFunctionInstances = new HashSet<>();
      }
      deletedAdFunctionInstances.add(entity);
    }
    adFunctionInstances.remove(entity);
    getRootPortfolioNode().addNewlyDisabledAdFunctionInstanceId(entity.getPersistentIdentity());
    setIsModified("adFunctionInstance:deleted");
  }
  
  public Set<AbstractAdFunctionInstanceEntity> getDeletedAdFunctionInstances() {
    
    if (deletedAdFunctionInstances != null) {
      return deletedAdFunctionInstances;
    }
    return EMPTY_AD_FUNCTION_SET;
  }

  public Set<Integer> getDeletedAdFunctionInstanceIds() {
    
    if (deletedAdFunctionInstances != null) {
      
      Set<Integer> ids = new HashSet<>();
      for (AbstractAdFunctionInstanceEntity entity: deletedAdFunctionInstances) {
        ids.add(entity.getPersistentIdentity());
      }
      return ids;
    }
    return EMPTY_ID_SET;
  }
  
  public void resetDeletedAdFunctionInstance() {
    
    deletedAdFunctionInstances = null;
  }  
  
  public Set<Integer> getUpdatedAdFunctionInstances() {

    Set<Integer> updatedIds = new HashSet<>();
    for (AbstractAdFunctionInstanceEntity entity: adFunctionInstances) {
      if (entity.getIsModified()) {
        updatedIds.add(entity.getPersistentIdentity());
      }
    }
    return updatedIds;
  }

  public Set<Integer> getCreatedAdFunctionInstances() {

    Set<Integer> createdIds = new HashSet<>();
    for (AbstractAdFunctionInstanceEntity entity: adFunctionInstances) {
      if (entity.getIsModified()) {
        createdIds.add(entity.getPersistentIdentity());
      }
    }
    return createdIds;
  }

  public boolean addAdFunctionInstanceCandidate(AbstractAdFunctionInstanceEntity adFunction) throws EntityAlreadyExistsException {
    
    AbstractAdFunctionInstanceEntity check = getAdFunctionInstanceByTemplateIdNullIfNotExists(
        adFunction.getAdFunctionTemplate().getPersistentIdentity());
    if (check != null) {
      
      LOGGER.warn("Marking AD Function Instance Candidate as deleted, as an active instance already exists: [{}]",
          check.getNaturalIdentity() + "(" + adFunction.getPersistentIdentity() + ")");
      setIsModified("adFunctionInstanceCandidates: removed");
      adFunction.setIsDeleted();
      return false;
      
    } else {
      
      setIsModified("adFunctionInstanceCandidates: added");
      return addChild(adFunctionInstanceCandidates, adFunction, this);  
    }
  }

  public Set<AbstractAdFunctionInstanceEntity> getAdFunctionInstanceCandidates() {
    
    return adFunctionInstanceCandidates;
  }

  public AbstractAdFunctionInstanceEntity getAdFunctionInstanceCandidateByTemplateIdNullIfNotExists(
      Integer adFunctionTemplateId) {
    
    Iterator<AbstractAdFunctionInstanceEntity> iterator = adFunctionInstanceCandidates.iterator();
    while (iterator.hasNext()) {
      
      AbstractAdFunctionInstanceEntity e = iterator.next();
      
      AbstractAdFunctionTemplateEntity adFunctionTemplate = e.getAdFunctionTemplate();
      
      if (adFunctionTemplate.getPersistentIdentity().equals(adFunctionTemplateId)) {
        
        return e;
      }
    }
    return null;
  }
  
  public void removeAdFunctionInstanceCandidate(AbstractAdFunctionInstanceEntity child) {

    adFunctionInstanceCandidates.remove(child);
    child.setIsDeleted();
  }

  public void removeAllAdFunctionInstanceCandidates() {

    adFunctionInstanceCandidates.clear();
  }
  
  public boolean addAdFunctionInstance(AbstractAdFunctionInstanceEntity adFunctionInstance) throws EntityAlreadyExistsException {
    
    // See if there is a candidate for the given AD function.
    AbstractAdFunctionInstanceEntity check1 = getAdFunctionInstanceCandidateByTemplateIdNullIfNotExists(
        adFunctionInstance.getAdFunctionTemplate().getPersistentIdentity());
    
    if (check1 != null && !check1.getIsDeleted()) {
      
      LOGGER.warn("Marking existing AD Function Instance Candidate as deleted: [{}]",
          check1.getNaturalIdentity() + "(" + check1.getPersistentIdentity() + ")");
      
      setIsModified("adFunctionInstanceCandidate: removed");
      check1.setIsDeleted();
    }

    // See if this is a duplicate.
    AbstractAdFunctionInstanceEntity check2 = getAdFunctionInstanceByTemplateIdNullIfNotExists(
        adFunctionInstance.getAdFunctionTemplate().getPersistentIdentity());
    
    if (check2 != null && !check2.getIsDeleted()) {
      
      // Keep the more recent instance.
      Integer check2Id = check2.getPersistentIdentity();
      if (check2Id != null && check2Id.intValue() < 0) {
        check2Id = check2Id * -1; // For the file system repository implementation.
      } else {
        check2Id = Integer.valueOf(0); // Even though the check hasn't been persisted yet either, we assume the incoming is newer.
      }
      
      // RP-10712: Computed Point - Error with ChWTempDelta points. Incoming instance has not been persisted yet.
      int incomingInstanceId = Integer.MAX_VALUE;
      if (adFunctionInstance.getPersistentIdentity() != null) {

        incomingInstanceId = adFunctionInstance.getPersistentIdentity().intValue();

        if (incomingInstanceId < 0) {
          incomingInstanceId = incomingInstanceId * -1; // For the file system repository implementation.
        }
      }
      
      if (incomingInstanceId > check2Id.intValue()) {

        LOGGER.warn("Marking duplicate AD Function Instance as deleted: [{}]",
            check2.getNaturalIdentity() + "(" + check2.getPersistentIdentity() + ")");
        
        addDeletedAdFunctionInstance(check2);
        adFunctionInstances.remove(check2);
        getRootPortfolioNode().addNewlyDisabledAdFunctionInstanceId(check2.getPersistentIdentity());

        setIsModified("adFunctionInstance: added");
        getRootPortfolioNode().addNewlyCreatedAdFunctionInstance(adFunctionInstance);
        return addChild(adFunctionInstances, adFunctionInstance, this);
        
      } else {
        
        LOGGER.warn("Marking duplicate AD Function Instance as deleted: [{}]",
            adFunctionInstance.getNaturalIdentity() + "(" + adFunctionInstance.getPersistentIdentity() + ")");
        
        addDeletedAdFunctionInstance(adFunctionInstance);
        return true;
        
      }
      
    } else {

      setIsModified("adFunctionInstance: added");
      getRootPortfolioNode().addNewlyCreatedAdFunctionInstance(adFunctionInstance);
      return addChild(adFunctionInstances, adFunctionInstance, this);
      
    }
  }

  public Set<AbstractAdFunctionInstanceEntity> getAdFunctionInstances() {
    
    return adFunctionInstances;
  }

  @Override
  public AbstractAdFunctionInstanceEntity getAdFunctionInstanceByTemplateIdNullIfNotExists(
      Integer adFunctionTemplateId) {
    
    Iterator<AbstractAdFunctionInstanceEntity> iterator = adFunctionInstances.iterator();
    while (iterator.hasNext()) {
      
      AbstractAdFunctionInstanceEntity e = iterator.next();
      
      AbstractAdFunctionTemplateEntity adFunctionTemplate = e.getAdFunctionTemplate();
      
      if (adFunctionTemplate.getPersistentIdentity().equals(adFunctionTemplateId)) {
        
        return e;
      }
    }
    return null;
  }
  
  public void removeAllAdFunctionInstances() {

    adFunctionInstances.clear();
  }  
  
  public Set<Integer> getBoundAdFunctionTemplateIds() {

    Set<Integer> boundAdFunctionTemplateIds = new HashSet<>();
    Iterator<AbstractAdFunctionInstanceEntity> candidateIterator = adFunctionInstanceCandidates.iterator();
    while (candidateIterator.hasNext()) {
      boundAdFunctionTemplateIds.add(candidateIterator.next().getAdFunctionTemplate().getPersistentIdentity());
    }

    Iterator<AbstractAdFunctionInstanceEntity> instanceIterator = adFunctionInstances.iterator();
    while (instanceIterator.hasNext()) {
      boundAdFunctionTemplateIds.add(instanceIterator.next().getAdFunctionTemplate().getPersistentIdentity());
    }
    return boundAdFunctionTemplateIds;
  }

  @Override
  public Set<AbstractNodeEntity> getAllChildNodes() {
    
    Set<AbstractNodeEntity> set = new HashSet<>();
    set.addAll(this.getChildPoints());
    return set;
  }
  
  @Override
  public void validate(
      Set<IssueType> issueTypes,
      List<ValidationMessage> validationMessages,
      boolean remediate) {

    Map<String, Object> entities = new LinkedHashMap<>();
    entities.put("equipment", this);
    
    if (ValidationMessage.hasPhaseOneIssueTypes(issueTypes)) {

      for (AbstractPointEntity point : getChildPoints()) {
        if (!point.getIsDeleted()) {
          point.validate(issueTypes, validationMessages, remediate);
        }
      }
      
      // RP-8658 Make sure that any async computed points associated with AD computed point
      // function output points have the correct point template and haystack tag associations.
      Set<AbstractAdFunctionInstanceEntity> instancesAndCandidates = new HashSet<>();
      instancesAndCandidates.addAll(getAdFunctionInstances());
      instancesAndCandidates.addAll(getAdFunctionInstanceCandidates());
      for (AbstractAdFunctionInstanceEntity adFunctionInstance : instancesAndCandidates) {
        
        if (adFunctionInstance
            .getAdFunctionTemplate()
            .getAdFunction()
            .getFunctionType()
            .equals(FunctionType.COMPUTED_POINT)) {
          
          for (AdFunctionInstanceOutputPointEntity instanceOutputPoint: adFunctionInstance.getOutputPoints()) {
            AsyncComputedPointEntity point = instanceOutputPoint.getPoint();
            if (point.getPointTemplateNullIfEmpty() == null || point.getNodeTags().isEmpty()) {
              
              AdFunctionTemplateOutputPointEntity templateOutputPoint = instanceOutputPoint.getAdFunctionTemplateOutputPoint();
              
              Set<TagEntity> tags = templateOutputPoint.getTags();
              if (tags != null && !tags.isEmpty()) {

                // AD computed point output points will have both point template and tag associations.
                NodeTagTemplatesContainer nodeTagTemplatesContainer = DictionaryContext.getNodeTagTemplatesContainer();
                PointTemplateEntity pointTemplate = nodeTagTemplatesContainer.getPointTemplateByTags(tags);
                
                StringBuilder tagIds = new StringBuilder();
                Set<String> pointTemplateHaystackTags = pointTemplate.getNormalizedTagsAsSet();
                Iterator<String> tagNameIterator = pointTemplateHaystackTags.iterator();
                while (tagNameIterator.hasNext()) {

                  String tagName = tagNameIterator.next();
                  Integer tagId = DictionaryContext.getTagsContainer().getHaystackTagId(tagName);
                  tagIds.append(tagId.toString());
                  if (tagNameIterator.hasNext()) {
                    tagIds.append(",");
                  }
                }
                
                Map<String, Object> pointEntities = new LinkedHashMap<>();
                pointEntities.put("point", point);
                
                point.setPointTemplate(pointTemplate);
                
                RemediationStrategy remediationStrategy = DeleteAndReAddTagsFromTemplateStrategyImpl.get();

                if (issueTypes.contains(IssueType.POINT_HAS_MISMATCH_BETWEEN_TAGS_AND_TEMPLATE_TAGS)) {
                  validationMessages.add(ValidationMessage.builder()
                      .withIssueType(IssueType.POINT_HAS_MISMATCH_BETWEEN_TAGS_AND_TEMPLATE_TAGS)
                      .withDetails("Async Computed Point: ["
                          + point
                          + "] is missing point template and haystack tags for AD computed point function template output point: ["
                          + templateOutputPoint
                          + "]")
                      .withEntityType(point.getClass().getSimpleName())
                      .withNaturalIdentity(point.getNaturalIdentity())
                      .withRemediationDescription("Delete and re-add haystack tags for point from AD computed point function template point template")
                      .withRemediationStrategy(remediationStrategy)
                      .build());

                  if (remediate) {
                    remediationStrategy.remediate(pointEntities);
                  }
                }                
              }
            }
          }
        }
      }
    }

    if (ValidationMessage.hasPhaseTwoIssueTypes(issueTypes)) {

      List<AbstractAdFunctionInstanceEntity> list = new ArrayList<>();
      list.addAll(adFunctionInstanceCandidates);
      for (int i=0; i < list.size(); i++) {
        
        AbstractAdFunctionInstanceEntity adFunctionInstance = list.get(i);
        
        boolean adFunctionInstanceNoLongerValid = false;
        RedGreenDto redGreenDto = AdFunctionEvaluator.validate(adFunctionInstance, issueTypes, validationMessages, remediate);
        if (adFunctionInstance.getIsDeleted() || redGreenDto.getGreen() == null) {
          
          adFunctionInstanceNoLongerValid = true;
          
        } else if (redGreenDto.getRed() != null) {
          
          EnergyExchangeEntity equipment = adFunctionInstance.getEquipment();
          equipment.addAdFunctionErrorMessages(AdFunctionErrorMessagesEntity
              .Mapper
              .getInstance()
              .mapDtoToEntity(
                  adFunctionInstance.getEquipment(),
                  redGreenDto.getRed()));
        }        
        
        if (adFunctionInstanceNoLongerValid) {
          
          IssueType issueType = IssueType.AD_FUNCTION_EQUIPMENT_HAS_ZERO_BOUND_POINTS;
          if (issueTypes.contains(issueType)) {
            RemediationStrategy remediationStrategy = AdFunctionRemediationStrategyFinder.find(
                issueType, 
                adFunctionInstance);
            if (remediationStrategy != null) {
              
              if (remediate) {
                entities = new HashMap<>();
                if (adFunctionInstance.getIsCandidate()) {
                  entities.put("function_candidate", adFunctionInstance);  
                } else {
                  entities.put("function_instance", adFunctionInstance);  
                }
              }
              String details = "Instance candidate has been marked as deleted";
              
              validationMessages.add(ValidationMessage.builder()
                  .withIssueType(issueType)
                  .withDetails(details)
                  .withEntityType(adFunctionInstance.getClass().getSimpleName())
                  .withNaturalIdentity(adFunctionInstance.getNaturalIdentity())
                  .withRemediationDescription(remediationStrategy.getRemediationDescription())
                  .withRemediationStrategy(remediationStrategy)
                  .build());
              
              if (remediate) {
                remediationStrategy.remediate(entities);
              }
            }
          }
        }
      }

      list.clear();
      list.addAll(adFunctionInstances);
      for (int i=0; i < list.size(); i++) {
        
        AbstractAdFunctionInstanceEntity adFunctionInstance = list.get(i);
        
        boolean adFunctionInstanceNoLongerValid = false;
        RedGreenDto redGreenDto = AdFunctionEvaluator.validate(adFunctionInstance, issueTypes, validationMessages, remediate);
        if (adFunctionInstance.getIsDeleted() || redGreenDto.getGreen() == null) {
          
          adFunctionInstanceNoLongerValid = true;
          
        } else if (redGreenDto.getRed() != null) {
         
          EnergyExchangeEntity equipment = adFunctionInstance.getEquipment();
          equipment.addAdFunctionErrorMessages(AdFunctionErrorMessagesEntity
              .Mapper
              .getInstance()
              .mapDtoToEntity(
                  adFunctionInstance.getEquipment(),
                  redGreenDto.getRed()));
        }
        
        if (adFunctionInstanceNoLongerValid) {
          
          IssueType issueType = IssueType.AD_FUNCTION_EQUIPMENT_HAS_ZERO_BOUND_POINTS;
          if (issueTypes.contains(issueType)) {
            RemediationStrategy remediationStrategy = AdFunctionRemediationStrategyFinder.find(
                issueType, 
                adFunctionInstance);
            if (remediationStrategy != null) {
              
              if (remediate) {
                entities = new HashMap<>();
                if (adFunctionInstance.getIsCandidate()) {
                  entities.put("function_candidate", adFunctionInstance);  
                } else {
                  entities.put("function_instance", adFunctionInstance);  
                }
              }
              String details = "Instance has been marked as deleted";
              
              validationMessages.add(ValidationMessage.builder()
                  .withIssueType(issueType)
                  .withDetails(details)
                  .withEntityType(adFunctionInstance.getClass().getSimpleName())
                  .withNaturalIdentity(adFunctionInstance.getNaturalIdentity())
                  .withRemediationDescription(remediationStrategy.getRemediationDescription())
                  .withRemediationStrategy(remediationStrategy)
                  .build());
              
              if (remediate) {
                remediationStrategy.remediate(entities);
              }
            }
          }
        }
      }
    }
  }
  
  public AbstractNodeEntity getChildNodeByNameNullIfNotExists(String name) {
    
    return getChildPointByNameNullIfNotExists(name);
  }    
  
  @Override
  public void mapToDtos(Map<String, Object> dtos) {

    PortfolioDtoMapper.mapNonPointNodeDto(this, dtos);

    Iterator<AbstractPointEntity> pointIterator = this.getChildPoints().iterator();
    while (pointIterator.hasNext()) {
      AbstractPointEntity point = pointIterator.next();
      if (!point.getIsDeleted()) {
        point.mapToDtos(dtos);  
      }
    }

    Iterator<AbstractAdFunctionInstanceEntity> functionCandidateIterator = this.adFunctionInstanceCandidates.iterator();
    while (functionCandidateIterator.hasNext()) {
      AbstractAdFunctionInstanceEntity candidate = functionCandidateIterator.next();
      if (!candidate.getIsDeleted()) {
        PortfolioDtoMapper.mapAdFunctionInstanceCandidateDto(candidate, dtos);  
      }
    }      
    
    Iterator<AbstractAdFunctionInstanceEntity> functionInstanceIterator = this.adFunctionInstances.iterator();
    while (functionInstanceIterator.hasNext()) {
      AbstractAdFunctionInstanceEntity instance = functionInstanceIterator.next();
      if (!instance.getIsDeleted()) {
        PortfolioDtoMapper.mapAdFunctionInstanceDto(instance, dtos);  
      }
    }      
  }
  
  public void addAdFunctionErrorMessages(AdFunctionErrorMessagesEntity thatAdFunctionErrorMessages) {

    Integer key = thatAdFunctionErrorMessages.getAdFunctionTemplate().getPersistentIdentity();
    
    boolean isBeingMapped = getRootPortfolioNode().isBeingMapped;
    if (!isBeingMapped) {

      AdFunctionErrorMessagesEntity thisAdFunctionErrorMessages = adFunctionErrorMessages.get(key);
      if (thisAdFunctionErrorMessages == null) {
        
        addedAdFunctionErrorMessages.add(thatAdFunctionErrorMessages);
        setIsModified("ad_function_error_messages_added");
        
      } else {
        
        int thatHashCode = thatAdFunctionErrorMessages.getErrorMessages().toString().hashCode();
        int thisHashCode = thisAdFunctionErrorMessages.getErrorMessages().toString().hashCode();
        if (thatHashCode != thisHashCode) {

          addedAdFunctionErrorMessages.add(thatAdFunctionErrorMessages);
          setIsModified("ad_function_error_messages_added");
          
          removedAdFunctionErrorMessages.add(thisAdFunctionErrorMessages);
          setIsModified("ad_function_error_messages_removed");
        }
      }
    }
    
    adFunctionErrorMessages.put(key, thatAdFunctionErrorMessages);
  }

  public void removeAdFunctionErrorMessages(AbstractAdFunctionTemplateEntity adFunctionTemplate) {
    
    if (!adFunctionErrorMessages.isEmpty()) {

      Integer key = adFunctionTemplate.getPersistentIdentity();
      
      AdFunctionErrorMessagesEntity victim = adFunctionErrorMessages.remove(key);
      
      if (victim != null) {

        removedAdFunctionErrorMessages.add(victim);
        setIsModified("ad_function_error_messages_removed");
      }
    }
  }

  public void getAddedAdFunctionErrorMessages(List<AdFunctionErrorMessagesEntity> list) {
    list.addAll(this.addedAdFunctionErrorMessages);
  }

  public void getRemovedAdFunctionErrorMessages(List<AdFunctionErrorMessagesEntity> list) {
    list.addAll(this.removedAdFunctionErrorMessages);
  }
  
  public void getAdFunctionErrorMessages(List<AdFunctionErrorMessagesEntity> list) {
    list.addAll(this.adFunctionErrorMessages.values());
  }
  
  public void setUnitSystem(UnitSystem unitSystem, PointTemplateEntity overridePointTemplate) {
    
    BuildingEntity building = getAncestorBuilding();
    List<AbstractAdFunctionInstanceEntity> instanceList = new ArrayList<>();
    instanceList.addAll(adFunctionInstances);
    for (int i=0; i < instanceList.size(); i++) {
      
      AbstractAdFunctionInstanceEntity adFunctionInstance = instanceList.get(i);
      
      // Keep track of any converted values.  Only do copy on write if constant(s) were converted.
      boolean modified = adFunctionInstance.setUnitSystem(building, unitSystem, overridePointTemplate);
      
      // Copy on write (using the modified instance, which will be deleted).
      if (modified) {

        AbstractAdFunctionInstanceEntity.createAdFunctionInstance(
            DictionaryContext.getNodeTagTemplatesContainer(),
            building.getRootPortfolioNode(),
            adFunctionInstance);
      }
    }
  }
  
  public static class Mapper {
    
    private static final Mapper INSTANCE = new Mapper();
    private Mapper() {
    }
    
    public static Mapper getInstance() {
      return INSTANCE;
    }

    public EnergyExchangeSystemEdgeDto mapEntityToDto(
        EnergyExchangeSystemType energyExchangeSystemType, 
        EnergyExchangeEntity parentEnergyExchangeSystemEntity,
        EnergyExchangeEntity childEnergyExchangeSystemEntity) {

      EnergyExchangeSystemEdgeDto dto = new EnergyExchangeSystemEdgeDto();
      dto.setSystemTypeId(energyExchangeSystemType.getId());
      dto.setParentId(parentEnergyExchangeSystemEntity.getPersistentIdentity());
      dto.setChildId(childEnergyExchangeSystemEntity.getPersistentIdentity());
      return dto;
    }

    public AbstractEnergyExchangeEntity mapDtoToEntity(PortfolioEntity portfolio, EnergyExchangeSystemEdgeDto dto) throws EntityAlreadyExistsException, EntityDoesNotExistException {
      
      EnergyExchangeSystemType energyExchangeSystemType = EnergyExchangeSystemType.get(dto.getSystemTypeId());
      
      AbstractEnergyExchangeEntity parentEnergyExchangeEntity = (AbstractEnergyExchangeEntity)portfolio.getChildNode(dto.getParentId());
      AbstractEnergyExchangeEntity childEnergyExchangeEntity = (AbstractEnergyExchangeEntity)portfolio.getChildNode(dto.getChildId());
      
      childEnergyExchangeEntity.addParentEnergyExchangeSystemNode(energyExchangeSystemType, parentEnergyExchangeEntity, true);
      
      portfolio.numEnergyExchangeSystemParents++;
      
      return childEnergyExchangeEntity;
    }
  }
}
//@formatter:on
