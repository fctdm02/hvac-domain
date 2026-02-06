//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy;

import java.util.ArrayList;
import java.util.Collections;
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
import com.djt.hvac.domain.model.nodehierarchy.mapper.PortfolioDtoMapper;
import com.djt.hvac.domain.model.nodehierarchy.point.AbstractPointEntity;

public class FloorEntity extends AbstractNodeEntity {
  private static final long serialVersionUID = 1L;
  
  private Integer floorOrdinal; 
  private Set<EquipmentEntity> childEquipment = new TreeSet<>();
  
  @SuppressWarnings("unchecked")
  @Override
  protected <T extends AbstractNodeEntity> Set<T> getChildSet(AbstractNodeEntity t) {
    
    if (t instanceof AbstractPointEntity) {
      return (Set<T>)this.getChildPoints();
      
    } else if (t instanceof EquipmentEntity) {
      return (Set<T>)this.childEquipment;
      
    }
    throw new UnsupportedOperationException("Floor: ["
        + getNodePath()
        + "] does not support having child: ["
        + t.getClassAndNaturalIdentity()
        + "]");
  }
  
  // For new instances (i.e. have not been persisted yet)
  public FloorEntity(
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
  
  public FloorEntity(
      Integer persistentIdentity,
      AbstractNodeEntity parentNode,
      String name,
      String displayName,
      String uuid,
      String createdAt,
      String updatedAt,
      Set<TagEntity> nodeTags) {
    this(
        persistentIdentity, 
        parentNode, 
        name, 
        displayName,
        uuid,
        createdAt,
        updatedAt,
        nodeTags,
        Integer.valueOf(1));
  }
  
  public FloorEntity(
      Integer persistentIdentity,
      AbstractNodeEntity parentNode,
      String name,
      String displayName,
      String uuid,
      String createdAt,
      String updatedAt,
      Set <TagEntity> nodeTags,
      Integer floorOrdinal) {
    super(
        persistentIdentity, 
        parentNode, 
        name, 
        displayName,
        uuid,
        createdAt,
        updatedAt,
        nodeTags);
    
    if (floorOrdinal != null) {
      this.floorOrdinal = floorOrdinal;   
    } else {
      this.floorOrdinal = Integer.valueOf(1);
    }
    
    if (!(parentNode instanceof BuildingEntity) 
        && !(parentNode instanceof SubBuildingEntity)) {
      
      throw new RuntimeException("Expected parent of floor with id: [" 
          + persistentIdentity 
          + "] to be either a building or sub-building, but instead was: [" 
          + parentNode.getClassAndPersistentIdentity()
          + "].");
    }
  }
    
  public NodeType getNodeType() {
    return NodeType.FLOOR;
  }
  
  public NodeSubType getNodeSubType() {
    return NodeSubType.NONE;
  }
  
  public Integer getFloorOrdinal() {
     return floorOrdinal;
  }

  public void setFloorOrdinal(Integer floorOrdinal) {
    this.floorOrdinal = floorOrdinal;
    this.setIsModified("floorOrdinal");
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
  
  public Optional<EquipmentEntity> getChildEquipmentEmptyIfINotExists(Integer persistentIdentity) {
    
    AbstractNodeEntity node = getRootPortfolioNode().getChildNodeNullIfNotExists(persistentIdentity);
    if (node != null) {
      return Optional.of((EquipmentEntity)node);
    }
    return Optional.empty();
  }  
  
  public EquipmentEntity getChildEquipmentByNameNullIfNotExists(String name) {
    
    for (EquipmentEntity equipment: childEquipment) {
      if (equipment.getName().equals(name)) {
        return equipment;
      }
    } 
    return null;
  }  
  
  @Override
  public int calculateTotalMappedPointCount() {
    
    int childPointCount = getDirectChildMappedPointCount();

    for (EquipmentEntity equipment : childEquipment) {
      childPointCount = childPointCount + equipment.getTotalMappedPointCount();  
    }    
    
    return childPointCount;
  }
  
  @Override
  public Set<AbstractNodeEntity> getAllChildNodes() {
    
    Set<AbstractNodeEntity> set = new HashSet<>();
    set.addAll(this.getChildPoints());
    set.addAll(this.childEquipment);
    return set;
  }
  
  @Override
  public AbstractNodeEntity duplicateNode(PortfolioEntity portfolio, AbstractNodeEntity parentNode, int duplicationIndex) {
    
    try {
      
      FloorEntity floor = new FloorEntity(
          null,
          parentNode,
          Integer.toString(duplicationIndex) + "_" + getName(),
          Integer.toString(duplicationIndex) + "_" + getDisplayName(),
          UUID.randomUUID().toString(),
          AbstractEntity.formatTimestamp(getCreatedAt()),
          AbstractEntity.formatTimestamp(getUpdatedAt()),
          getNodeTags(),
          floorOrdinal);
      
      portfolio.addNodeToParentAndIndex(parentNode, floor);
      
      duplicatePointNodes(portfolio, floor, duplicationIndex);
      
      for (EquipmentEntity equipment : childEquipment) {
        equipment.duplicateNode(portfolio, floor, duplicationIndex);
      }
      
      return floor;
      
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
    
    for (EquipmentEntity equipment : childEquipment) {
      equipment.validate(issueTypes, validationMessages, remediate);  
    }    
  }

  public AbstractNodeEntity getChildNodeByNameNullIfNotExists(String name) {
    
    AbstractNodeEntity childNode = getChildEquipmentByNameNullIfNotExists(name);
    if (childNode == null) {

      childNode = getChildPointByNameNullIfNotExists(name);
    }
    
    return childNode;
  }  
  
  public void remediateFloorOrdinalConflicts() {
    
    List<FloorEntity> childFloors = new ArrayList<>();
    if (getParentNode() instanceof BuildingEntity) {
      childFloors.addAll(((BuildingEntity)getParentNode()).getChildFloors());
    } else if (getParentNode() instanceof SubBuildingEntity) {
      childFloors.addAll(((SubBuildingEntity)getParentNode()).getChildFloors());
    }
    
    int size = childFloors.size();
    if (size > 0) {
      
      if (size == 1 && getFloorOrdinal().intValue() != 1) {

        setFloorOrdinal(Integer.valueOf(1));
        
      } else {

        Collections.sort(childFloors);
        for (int i=0; i < size; i++) {
          
          FloorEntity childFloor = childFloors.get(i);
          int floorOrdinal = childFloor.getFloorOrdinal().intValue();
          if (floorOrdinal != (i+1)) {
        
            childFloor.setFloorOrdinal(Integer.valueOf(i+1));
            
          }
        }
      }
    } 
  }
    
  public void mapToDtos(Map<String, Object> dtos) {
    
    PortfolioDtoMapper.mapNonPointNodeDto(this, dtos);
    
    Iterator<EquipmentEntity> equipmentIterator = this.childEquipment.iterator();
    while (equipmentIterator.hasNext()) {
      EquipmentEntity equipment = equipmentIterator.next();
      if (!equipment.getIsDeleted()) {
        equipment.mapToDtos(dtos);
      }
    }

    Iterator<AbstractPointEntity> pointIterator = this.getChildPoints().iterator();
    while (pointIterator.hasNext()) {
      AbstractPointEntity point = pointIterator.next();
      if (!point.getIsDeleted()) {
        point.mapToDtos(dtos);  
      }
    }
  }   
}
//@formatter:on