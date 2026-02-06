//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy;

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
import com.djt.hvac.domain.model.dictionary.enums.NodeSubType;
import com.djt.hvac.domain.model.dictionary.enums.NodeType;
import com.djt.hvac.domain.model.nodehierarchy.building.BuildingEntity;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.EquipmentEntity;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.PlantEntity;
import com.djt.hvac.domain.model.nodehierarchy.mapper.PortfolioDtoMapper;
import com.djt.hvac.domain.model.nodehierarchy.point.AbstractPointEntity;

public class SubBuildingEntity extends AbstractNodeEntity {
  private static final long serialVersionUID = 1L;
  
  private Set<PlantEntity> childPlants = new TreeSet<>();
  private Set<FloorEntity> childFloors = new TreeSet<>();
  private Set<EquipmentEntity> childEquipment = new TreeSet<>();
  
  @SuppressWarnings("unchecked")
  @Override
  protected <T extends AbstractNodeEntity> Set<T> getChildSet(AbstractNodeEntity t) {
    
    if (t instanceof AbstractPointEntity) {
      return (Set<T>)this.getChildPoints();

    } else if (t instanceof PlantEntity) {
      return (Set<T>)this.childPlants;
      
    } else if (t instanceof FloorEntity) {
      return (Set<T>)this.childFloors;
      
    } else if (t instanceof EquipmentEntity) {
      return (Set<T>)this.childEquipment;
      
    }
    throw new UnsupportedOperationException("Sub building: ["
        + getNodePath()
        + "] does not support having child: ["
        + t.getClassAndNaturalIdentity()
        + "]");
  }
  
  // For new instances (i.e. have not been persisted yet)
  public SubBuildingEntity(
      AbstractNodeEntity parentNode,
      String name,
      String displayName) {
    super(
        parentNode,
        name,
        displayName);        
  }
  
  public SubBuildingEntity(
      BuildingEntity parentNode,
      String name,
      String displayName) {
    super(
        parentNode,
        name,
        displayName);        
  }
  
  public SubBuildingEntity(
      Integer persistentIdentity,
      BuildingEntity parentNode,
      String name,
      String displayName,
      String uuid,
      String createdAt,
      String updatedAt,
      Set <TagEntity> nodeTags) {
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

  public NodeType getNodeType() {
    return NodeType.SUB_BUILDING;
  }
  
  public NodeSubType getNodeSubType() {
    return NodeSubType.NONE;
  }

  public boolean addChildPlant(PlantEntity energyExchangePlant) throws EntityAlreadyExistsException {
    return addChild(childPlants, energyExchangePlant, this);
  }

  public Set<PlantEntity> getChildPlants() {
    return childPlants;
  }

  public PlantEntity getChildPlant(Integer persistentIdentity) throws EntityDoesNotExistException {
    return (PlantEntity)getRootPortfolioNode().getChildNode(persistentIdentity);
  }
  
  public PlantEntity getChildPlantByNameNullIfNotExists(String name) {
    
    for (PlantEntity energyExchangePlant: childPlants) {
      if (energyExchangePlant.getName().equals(name)) {
        return energyExchangePlant;
      }
    } 
    return null;
  }  
  
  public PlantEntity getChildPlantNullIfINotExists(Integer persistentIdentity) {
    
    AbstractNodeEntity node = getRootPortfolioNode().getChildNodeNullIfNotExists(persistentIdentity);
    if (node != null) {
      return (PlantEntity)node;
    }
    return null;
  }
  
  public Optional<PlantEntity> getChildPlantEmptyIfINotExists(Integer persistentIdentity) {
    
    AbstractNodeEntity node = getRootPortfolioNode().getChildNodeNullIfNotExists(persistentIdentity);
    if (node != null) {
      return Optional.of((PlantEntity)node);
    }
    return Optional.empty();
  }
  
  public boolean addChildFloorIfNotExists(FloorEntity floor) {
    return addChildIfNotExists(childFloors, floor, this);
  }

  public boolean addChildFloor(FloorEntity floor) throws EntityAlreadyExistsException {
    return addChild(childFloors, floor, this);
  }
  
  public Set<FloorEntity> getChildFloors() {
    return childFloors;
  }
  
  public FloorEntity getChildFloor(Integer persistentIdentity) throws EntityDoesNotExistException {
    return (FloorEntity)getRootPortfolioNode().getChildNode(persistentIdentity);
  }
  
  public FloorEntity getChildFloorByNameNullIfNotExists(String name) {
    
    for (FloorEntity floor: childFloors) {
      if (floor.getName().equals(name)) {
        return floor;
      }
    } 
    return null;
  }    
  
  public EquipmentEntity getChildEquipmentByNameNullIfNotExists(String name) {
    
    for (EquipmentEntity equipment: childEquipment) {
      if (equipment.getName().equals(name)) {
        return equipment;
      }
    } 
    return null;
  }    
  
  public boolean addChildEquipmentIfNotExists(EquipmentEntity equipment) {
    return addChildIfNotExists(childEquipment, equipment, this);
  }
  
  public boolean addChildEquipment(EquipmentEntity equipment) throws EntityAlreadyExistsException {
    return addChild(childEquipment, equipment, this);
  }

  public Set<EquipmentEntity> getChildEquipment() {
    return childEquipment;
  }
  
  public EquipmentEntity getChildEquipment(Integer persistentIdentity) throws EntityDoesNotExistException {
    return (EquipmentEntity)getRootPortfolioNode().getChildNode(persistentIdentity);
  }
  
  public Optional<FloorEntity> getChildFloorEmptyIfINotExists(Integer persistentIdentity) {
    
    AbstractNodeEntity node = getRootPortfolioNode().getChildNodeNullIfNotExists(persistentIdentity);
    if (node != null) {
      return Optional.of((FloorEntity)node);
    }
    return Optional.empty();
  }  
  
  public Optional<EquipmentEntity> getChildEquipmentEmptyIfINotExists(Integer persistentIdentity) {
    
    AbstractNodeEntity node = getRootPortfolioNode().getChildNodeNullIfNotExists(persistentIdentity);
    if (node != null) {
      return Optional.of((EquipmentEntity)node);
    }
    return Optional.empty();
  }
  
  @Override
  public int calculateTotalMappedPointCount() {
    
    int childPointCount = getDirectChildMappedPointCount();

    for (PlantEntity childPlant : childPlants) {
      childPointCount = childPointCount + childPlant.getTotalMappedPointCount();  
    }    
    
    for (FloorEntity floor : childFloors) {
      childPointCount = childPointCount + floor.getTotalMappedPointCount();  
    }    
    
    for (EquipmentEntity equipment : childEquipment) {
      childPointCount = childPointCount + equipment.getTotalMappedPointCount();  
    }    
    
    return childPointCount;
  }
  
  @Override
  public Set<AbstractNodeEntity> getAllChildNodes() {
    
    Set<AbstractNodeEntity> set = new HashSet<>();
    set.addAll(this.getChildPoints());
    set.addAll(this.childPlants);
    set.addAll(this.childFloors);
    set.addAll(this.childEquipment);
    return set;
  }
  
  @Override
  public AbstractNodeEntity duplicateNode(PortfolioEntity portfolio, AbstractNodeEntity parentNode, int duplicationIndex) {
    
    try {

      SubBuildingEntity subBuilding = new SubBuildingEntity(
          null,
          (BuildingEntity)parentNode,
          Integer.toString(duplicationIndex) + "_" + getName(),
          Integer.toString(duplicationIndex) + "_" + getDisplayName(),
          UUID.randomUUID().toString(),
          AbstractEntity.formatTimestamp(getCreatedAt()),
          AbstractEntity.formatTimestamp(getUpdatedAt()),
          getNodeTags());
      
      portfolio.addNodeToParentAndIndex(parentNode, subBuilding);
      
      duplicatePointNodes(portfolio, subBuilding, duplicationIndex);
      
      for (PlantEntity plant: childPlants) {
        plant.duplicateNode(portfolio, subBuilding, duplicationIndex);
      }    

      for (FloorEntity floor : childFloors) {
        floor.duplicateNode(portfolio, subBuilding, duplicationIndex);
      }    
      
      for (EquipmentEntity equipment : childEquipment) {
        equipment.duplicateNode(portfolio, subBuilding, duplicationIndex);
      }
      
      return subBuilding;
      
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
 
    if (ValidationMessage.hasPhaseOneIssueTypes(issueTypes)) {
      for (AbstractPointEntity point : getChildPoints()) {
        if (!point.getIsDeleted()) {
          point.validate(issueTypes, validationMessages, remediate);  
        }
      }
    }

    for (PlantEntity plant : childPlants) {
      plant.validate(issueTypes, validationMessages, remediate);  
    }    
    
    for (FloorEntity floor : childFloors) {
      floor.validate(issueTypes, validationMessages, remediate);  
    }    
    
    for (EquipmentEntity equipment : childEquipment) {
      equipment.validate(issueTypes, validationMessages, remediate);  
    }    
  }
  
  public AbstractNodeEntity getChildNodeByNameNullIfNotExists(String name) {
    
    AbstractNodeEntity childNode = getChildPlantByNameNullIfNotExists(name);
    if (childNode == null) {

      childNode = getChildFloorByNameNullIfNotExists(name);
    }

    if (childNode == null) {

      childNode = getChildEquipmentByNameNullIfNotExists(name);
    }

    if (childNode == null) {

      childNode = getChildPointByNameNullIfNotExists(name);
    }
    
    return childNode;
  }
  
  public void mapToDtos(Map<String, Object> dtos) {
    
    PortfolioDtoMapper.mapNonPointNodeDto(this, dtos);

    for (PlantEntity childPlant : childPlants) {
      if (!childPlant.getIsDeleted()) {
        childPlant.mapToDtos(dtos);  
      }
    }
    
    Iterator<FloorEntity> floorIterator = childFloors.iterator();
    while (floorIterator.hasNext()) {
      FloorEntity floor = floorIterator.next();
      if (!floor.getIsDeleted()) {
        floor.mapToDtos(dtos);  
      }
    }
    
    Iterator<EquipmentEntity> equipmentIterator = childEquipment.iterator();
    while (equipmentIterator.hasNext()) {
      EquipmentEntity equipment = equipmentIterator.next();
      if (!equipment.getIsDeleted()) {
        equipment.mapToDtos(dtos);  
      }
    }

    Iterator<AbstractPointEntity> pointIterator = getChildPoints().iterator();
    while (pointIterator.hasNext()) {
      AbstractPointEntity point = pointIterator.next();
      if (!point.getIsDeleted()) {
        point.mapToDtos(dtos);  
      }
    }
  }
}
//@formatter:on