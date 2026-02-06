//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.energyexchange;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.dictionary.TagEntity;
import com.djt.hvac.domain.model.dictionary.container.DictionaryContext;
import com.djt.hvac.domain.model.dictionary.energyexchange.AbstractEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.energyexchange.EquipmentEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.energyexchange.enums.EnergyExchangeSystemType;
import com.djt.hvac.domain.model.dictionary.enums.NodeSubType;
import com.djt.hvac.domain.model.dictionary.enums.NodeType;
import com.djt.hvac.domain.model.dictionary.enums.TagGroupType;
import com.djt.hvac.domain.model.nodehierarchy.AbstractNodeEntity;
import com.djt.hvac.domain.model.nodehierarchy.FloorEntity;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;
import com.djt.hvac.domain.model.nodehierarchy.SubBuildingEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BuildingEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.AbstractPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.WeatherAsyncComputedPointEntity;

public class EquipmentEntity extends AbstractEnergyExchangeEntity {
  private static final long serialVersionUID = 1L;
  private EquipmentEnergyExchangeTypeEntity equipmentType; // Tag Group 4

  private Set<EquipmentEntity> nodeHierarchyChildEquipment = new TreeSet<>();
  
  public EquipmentEntity() {}

  // For new instances (i.e. have not been persisted yet)
  public EquipmentEntity(
      AbstractNodeEntity parentNode,
      String name,
      String displayName) {
    this(
        null,
        parentNode,
        name,
        displayName,
        null,
        null,
        null,
        null);        
  }

  public EquipmentEntity(
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
    
    if (this.equipmentType == null && nodeTags != null) {
      for (TagEntity tag: nodeTags) {
        if (tag.getTagGroupType().equals(TagGroupType.EQUIPMENT_TYPE)) {
          
          this.equipmentType = DictionaryContext.getTagsContainer().getEquipmentTypeById(tag.getPersistentIdentity());
        }
      }
    }

    if (!(parentNode instanceof BuildingEntity)
        && !(parentNode instanceof SubBuildingEntity)
        && !(parentNode instanceof FloorEntity)
        && !(parentNode instanceof EquipmentEntity)
        && !parentNode.getPersistentIdentity().equals(persistentIdentity)) {

      throw new IllegalStateException("Expected parent of equipment with id: ["
          + persistentIdentity
          + "] to be either a building, sub-building, floor or another piece of equipment, but instead was: ["
          + parentNode.getClassAndPersistentIdentity()
          + "].");
    }
  }
  
  @Override
  public NodeType getNodeType() {
    return NodeType.EQUIPMENT;
  }

  @Override
  public NodeSubType getNodeSubType() {
    return NodeSubType.NONE;
  }

  public TagGroupType getTagGroupType() {
    return TagGroupType.EQUIPMENT_TYPE;
  }
  
  @Override
  public Optional<AbstractEnergyExchangeTypeEntity> getEnergyExchangeType() {
    
    return Optional.ofNullable(getEnergyExchangeTypeNullIfNotExists());
  }
  
  @Override
  public AbstractEnergyExchangeTypeEntity getEnergyExchangeTypeNullIfNotExists() {
   
    return getEquipmentTypeNullIfNotExists();
  }
  
  public Optional<EquipmentEnergyExchangeTypeEntity> getEquipmentType() {
    
    return Optional.ofNullable(equipmentType);
  }

  public EquipmentEnergyExchangeTypeEntity getEquipmentTypeNullIfNotExists() {
    
    return equipmentType;
  }
  
  public void setEquipmentType(EquipmentEnergyExchangeTypeEntity equipmentType) {
    
    try {
      if (this.equipmentType == null && equipmentType == null) {
        
        // DO NOTHING
        
      } else if (this.equipmentType == null && equipmentType != null) {

        // Remove any equipment metadata tags (if they exist)
        setMetadataTags(new HashSet<>());
	
        this.equipmentType = equipmentType;
        this.addNodeTag(this.equipmentType.getTag());
        setIsModified("equipmentType:added");
        
      } else if (this.equipmentType != null && equipmentType == null) {

        // Remove any equipment metadata tags (if they exist)
        setMetadataTags(new HashSet<>());
	
        this.equipmentType = equipmentType;
        this.removeAllNodeTagsByType(getTagGroupType());
        setIsModified("equipmentType:removed");
        
      } else if (this.equipmentType != null && equipmentType != null) {
        
        if (equipmentType != null && !this.equipmentType.equals(equipmentType)) {

          // Remove any equipment metadata tags (if they exist)
          setMetadataTags(new HashSet<>());
          
          this.equipmentType = equipmentType;
          this.removeAllNodeTagsByType(getTagGroupType());
          this.addNodeTag(this.equipmentType.getTag());
          setIsModified("equipmentType:changed");
          
        } else {
          // DO NOTHING
        }
      }      
    } catch (EntityAlreadyExistsException eaee) {
      throw new IllegalStateException("Unable to set equipment type: ["
          + equipmentType
          + "] for equipment: ["
          + getNodePath()
          + "]", eaee);
    }
    
    remediateEnergyExchangeRelationshipsForEquipmentType();
  }
  
  private void remediateEnergyExchangeRelationshipsForEquipmentType() {

    // RP-12475: If the equipment type is either null or NOT an ahu, then 
    // remove any children that may exist for the air supply system type.
    EquipmentEnergyExchangeTypeEntity ahuEquipmentType = DictionaryContext.getTagsContainer().getEquipmentTypeById(42);
    if (this.equipmentType == null || (this.equipmentType != null && !this.equipmentType.equals(ahuEquipmentType))) {
      
      try {
        setChildEnergyExchangeSystemNodes(
            EnergyExchangeSystemType.AIR_SUPPLY, 
            new ArrayList<>());
      } catch (EntityAlreadyExistsException | EntityDoesNotExistException e) {
        throw new RuntimeException("Unable to remove child energy exchange nodes for equipment: " + this);
      }
    }
    
    // RP-12475: If the equipment type is either null or NOT an fcu, heatPump, uv or vav,
    // then remove any children that may exist for the air supply system type.
    /*
     * 43  vav
     * 44  fcu
     * 149 heatPump
     * 170 uv     
     */
    EquipmentEnergyExchangeTypeEntity vavEquipmentType = DictionaryContext.getTagsContainer().getEquipmentTypeById(43);
    EquipmentEnergyExchangeTypeEntity fcuEquipmentType = DictionaryContext.getTagsContainer().getEquipmentTypeById(44);
    EquipmentEnergyExchangeTypeEntity heatPumpEquipmentType = DictionaryContext.getTagsContainer().getEquipmentTypeById(149);
    EquipmentEnergyExchangeTypeEntity uvEquipmentType = DictionaryContext.getTagsContainer().getEquipmentTypeById(170);
    if (this.equipmentType == null || (this.equipmentType != null 
        && !this.equipmentType.equals(vavEquipmentType)
        && !this.equipmentType.equals(fcuEquipmentType)
        && !this.equipmentType.equals(heatPumpEquipmentType)
        && !this.equipmentType.equals(uvEquipmentType))) {
      
      try {
        setParentEnergyExchangeSystemNodes(
            EnergyExchangeSystemType.AIR_SUPPLY, 
            new ArrayList<>());
      } catch (EntityAlreadyExistsException | EntityDoesNotExistException e) {
        throw new RuntimeException("Unable to remove parent energy exchange nodes for equipment: " + this);
      }
    }    
  }
  
  // Recursive method
  public boolean isAncestorEquipment(EquipmentEntity equipment) {

    if (equipment == this) {
      return true;
    }

    EquipmentEntity pe = equipment.getParentEquipmentNullIfNotExists();
    if (pe != null) {

      return isAncestor(pe);
    }

    return false;
  }
  
  public boolean addNodeHierarchyChildEquipment(EquipmentEntity equipment) throws EntityAlreadyExistsException {
    return addChild(nodeHierarchyChildEquipment, equipment, this);
  }

  public Set<EquipmentEntity> getNodeHierarchyChildEquipment() {
    return nodeHierarchyChildEquipment;
  }

  @SuppressWarnings("unchecked")
  @Override
  protected <T extends AbstractNodeEntity> Set<T> getChildSet(AbstractNodeEntity t) {
    
    if (t instanceof AbstractPointEntity) {
      return (Set<T>)this.getChildPoints();
      
    } else if (t instanceof EquipmentEntity) {
      return (Set<T>)this.nodeHierarchyChildEquipment;
      
    }
    throw new UnsupportedOperationException("Equipment: ["
        + getNodePath()
        + "] does not support having child: ["
        + t.getClassAndNaturalIdentity()
        + "]");
  }
    
  
  @Override
  public BuildingEntity getAncestorBuilding() {

    AbstractNodeEntity targetAncestorNode = null;
    AbstractNodeEntity parentNode = getParentNode();
    while (targetAncestorNode == null && parentNode != null) {

      if (parentNode.getNodeType().equals(NodeType.BUILDING)) {

        targetAncestorNode = parentNode;
        break;
      }
      parentNode = parentNode.getParentNode();
    }
    if (targetAncestorNode == null) {

      throw new IllegalStateException("Could not find parent building for equipment: ["
          + getNodePath()
          + "] with id: ["
          + getPersistentIdentity()
          + "].");
    }
    return (BuildingEntity) targetAncestorNode;
  }

  @Override
  public int calculateTotalMappedPointCount() {
    
    int childPointCount = getDirectChildMappedPointCount();

    for (EquipmentEntity equipment : nodeHierarchyChildEquipment) {
      childPointCount = childPointCount + equipment.getTotalMappedPointCount();  
    }    
    
    return childPointCount;
  }
  
  @Override
  public Set<AbstractNodeEntity> getAllChildNodes() {
    
    Set<AbstractNodeEntity> set = super.getAllChildNodes();
    set.addAll(this.nodeHierarchyChildEquipment);
    return set;
  }
  
  @Override
  public AbstractNodeEntity duplicateNode(PortfolioEntity portfolio, AbstractNodeEntity parentNode, int duplicationIndex) {
    
    try {

      EquipmentEntity equipment = new EquipmentEntity(
          null,
          parentNode,
          Integer.toString(duplicationIndex) + "_" + getName(),
          Integer.toString(duplicationIndex) + "_" + getDisplayName(),
          UUID.randomUUID().toString(),
          AbstractEntity.formatTimestamp(getCreatedAt()),
          AbstractEntity.formatTimestamp(getUpdatedAt()),
          getNodeTags());
      
      portfolio.addNodeToParentAndIndex(parentNode, equipment);
      
      duplicatePointNodes(portfolio, equipment, duplicationIndex);
      
      for (EquipmentEntity childEquipment: nodeHierarchyChildEquipment) {
        childEquipment.duplicateNode(portfolio, equipment, duplicationIndex);
      }
      
      return equipment;
      
    } catch (EntityAlreadyExistsException eaee) {
      throw new IllegalStateException("Unable to duplicate child nodes for: ["
          + getClassAndNaturalIdentity()
          + "] and duplicationIndex: ["
          + duplicationIndex
          + "].", eaee);
    }
  }  
  
  @Override
  public void validate(
      Set<IssueType> issueTypes,
      List<ValidationMessage> validationMessages,
      boolean remediate) {
    
    super.validate(issueTypes, validationMessages, remediate);
    
    for (EquipmentEntity equipment : nodeHierarchyChildEquipment) {
      equipment.validate(issueTypes, validationMessages, remediate);
    }

    if (remediate) {
      
      remediateEnergyExchangeRelationshipsForEquipmentType();
      
      if (getName().equals(BuildingEntity.OFF_PREM_WEATH_STATION_EQUIP_NAME)) {
        
        // Ensure that off_prem_weather_station is tagged with off_prem_weather_station equipment type.
        if (equipmentType != null && !equipmentType.getPersistentIdentity().equals(BuildingEntity.OFF_PREM_WEATH_STATION_EQUIP_TYPE_ID)) {
          setEquipmentType(DictionaryContext.getTagsContainer().getEquipmentTypeById(BuildingEntity.OFF_PREM_WEATH_STATION_EQUIP_TYPE_ID));
        }
        
        // Ensure that only the two weather station points exist as children.  All others will be moved to the ancestor building.
        List<AbstractPointEntity> victims = new ArrayList<>();
        if (getChildPoints() != null) {
          for (AbstractPointEntity point: getChildPoints()) {
            if (point instanceof WeatherAsyncComputedPointEntity == false) {
              victims.add(point); 
            }
          }
        }
        if (!victims.isEmpty()) {
          for (int i=0; i < victims.size(); i++) {
            victims.get(i).setNewParentNode(getAncestorBuilding());
          }
        }
      }
    }
  }

  public EquipmentEntity getNodeChildHierarchyEquipmentByNameNullIfNotExists(String name) {
    
    for (EquipmentEntity equipment: nodeHierarchyChildEquipment) {
      if (equipment.getName().equals(name)) {
        return equipment;
      }
    } 
    return null;
  } 
  
  public AbstractNodeEntity getChildNodeByNameNullIfNotExists(String name) {
    
    AbstractNodeEntity childNode = getNodeChildHierarchyEquipmentByNameNullIfNotExists(name);
    if (childNode == null) {

      childNode = getChildPointByNameNullIfNotExists(name);
    }
    
    return childNode;
  }
  
  @Override
  public void mapToDtos(Map<String, Object> dtos) {
    
    super.mapToDtos(dtos);

    Iterator<EquipmentEntity> nodeHierarchyEquipmentIterator = this.nodeHierarchyChildEquipment.iterator();
    while (nodeHierarchyEquipmentIterator.hasNext()) {
      EquipmentEntity equipment = nodeHierarchyEquipmentIterator.next();
      if (!equipment.getIsDeleted()) {
        equipment.mapToDtos(dtos);
      }
    }
  }
}
//@formatter:on