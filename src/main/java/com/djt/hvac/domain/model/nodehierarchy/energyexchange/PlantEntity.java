//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.energyexchange;

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
import com.djt.hvac.domain.model.dictionary.energyexchange.PlantEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.enums.NodeSubType;
import com.djt.hvac.domain.model.dictionary.enums.NodeType;
import com.djt.hvac.domain.model.dictionary.enums.TagGroupType;
import com.djt.hvac.domain.model.nodehierarchy.AbstractNodeEntity;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;
import com.djt.hvac.domain.model.nodehierarchy.SubBuildingEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BuildingEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.AbstractPointEntity;

public class PlantEntity extends AbstractEnergyExchangeEntity {
  private static final long serialVersionUID = 1L;
  private Set<LoopEntity> childLoops = new TreeSet<>();
  
  private PlantEnergyExchangeTypeEntity plantType; // Tag Group 9

  // For new instances (i.e. have not been persisted yet)
  public PlantEntity(
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
  
  public PlantEntity(
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
    
    if (this.plantType == null && nodeTags != null) {
      for (TagEntity tag: nodeTags) {
        if (tag.getTagGroupType().equals(TagGroupType.PLANT_TYPE)) {
          
          this.plantType = DictionaryContext.getTagsContainer().getPlantTypeById(tag.getPersistentIdentity());
        }
      }
    }
    
    if (!(parentNode instanceof BuildingEntity)
        && !(parentNode instanceof SubBuildingEntity)) {

      throw new IllegalStateException("Expected parent of plant with id: ["
          + persistentIdentity
          + "] to be either a building or sub-building, but instead was: ["
          + parentNode.getClassAndPersistentIdentity()
          + "].");
    }    
  }

  @Override
  public Set<AbstractNodeEntity> getAllChildNodes() {
    
    Set<AbstractNodeEntity> set = super.getAllChildNodes();
    set.addAll(this.childLoops);
    return set;
  }
  
  @Override
  public AbstractNodeEntity duplicateNode(PortfolioEntity portfolio, AbstractNodeEntity parentNode, int duplicationIndex) {
    
    try {

      PlantEntity plant = new PlantEntity(
          null,
          parentNode,
          Integer.toString(duplicationIndex) + "_" + getName(),
          Integer.toString(duplicationIndex) + "_" + getDisplayName(),
          UUID.randomUUID().toString(),
          AbstractEntity.formatTimestamp(getCreatedAt()),
          AbstractEntity.formatTimestamp(getUpdatedAt()),
          getNodeTags());
      
      portfolio.addNodeToParentAndIndex(parentNode, plant);
      
      duplicatePointNodes(portfolio, plant, duplicationIndex);
      
      for (LoopEntity loop: childLoops) {
        loop.duplicateNode(portfolio, plant, duplicationIndex);
      }
      
      return plant;
      
    } catch (EntityAlreadyExistsException eaee) {
      throw new IllegalStateException("Unable to duplicate child nodes for: ["
          + getClassAndNaturalIdentity()
          + "] and duplicationIndex: ["
          + duplicationIndex
          + "].", eaee);
    }
  }
  
  public NodeType getNodeType() {
    return NodeType.PLANT;
  }
  
  public NodeSubType getNodeSubType() {
    return NodeSubType.NONE;
  }
  
  public TagGroupType getTagGroupType() {
    return TagGroupType.PLANT_TYPE;
  }

  public boolean addChildLoop(LoopEntity energyExchangeLoop) throws EntityAlreadyExistsException {
    return addChild(childLoops, energyExchangeLoop, this);
  }

  public Set<LoopEntity> getChildLoops() {
    return childLoops;
  }

  public LoopEntity getChildLoop(Integer persistentIdentity) throws EntityDoesNotExistException {
    return (LoopEntity)getRootPortfolioNode().getChildNode(persistentIdentity);
  }
  
  public LoopEntity getChildLoopByNameNullIfNotExists(String name) {
    
    for (LoopEntity energyExchangeLoop: childLoops) {
      if (energyExchangeLoop.getName().equals(name)) {
        return energyExchangeLoop;
      }
    } 
    return null;
  }  
  
  public LoopEntity getChildLoopNullIfINotExists(Integer persistentIdentity) {
    
    AbstractNodeEntity node = getRootPortfolioNode().getChildNodeNullIfNotExists(persistentIdentity);
    if (node != null) {
      return (LoopEntity)node;
    }
    return null;
  }
  
  public Optional<LoopEntity> getChildLoopEmptyIfINotExists(Integer persistentIdentity) {
    
    AbstractNodeEntity node = getRootPortfolioNode().getChildNodeNullIfNotExists(persistentIdentity);
    if (node != null) {
      return Optional.of((LoopEntity)node);
    }
    return Optional.empty();
  } 
  
  @Override
  public Optional<AbstractEnergyExchangeTypeEntity> getEnergyExchangeType() {
    
    return Optional.ofNullable(getEnergyExchangeTypeNullIfNotExists());
  }
  
  @Override
  public AbstractEnergyExchangeTypeEntity getEnergyExchangeTypeNullIfNotExists() {
   
    return getPlantTypeNullIfNotExists();
  }
  
  public Optional<PlantEnergyExchangeTypeEntity> getPlantType() {
    
    return Optional.ofNullable(plantType);
  }

  public PlantEnergyExchangeTypeEntity getPlantTypeNullIfNotExists() {
    
    return plantType;
  }
  
  public boolean setPlantType(PlantEnergyExchangeTypeEntity plantType) {
    
    try {
      
      // RP-12902: If the plant type is changed/removed, then remove any energy exchange
      // system relationships that may exist. Additionally, delete any child loops that
      // may exist under the plant (using the "deleteChildLoop()" method that was done 
      // for RP-12872.
      boolean plantTypeChanged = false;
      
      if (this.plantType == null && plantType == null) {
        
        // DO NOTHING
        
      } else if (this.plantType == null && plantType != null) {
        
        this.plantType = plantType;
        this.addNodeTag(this.plantType.getTag());
        setIsModified("plantType:added");
        
      } else if (this.plantType != null && plantType == null) {
        
        this.plantType = plantType;
        this.removeAllNodeTagsByType(getTagGroupType());
        setIsModified("plantType:removed");
        plantTypeChanged = true;
        
      } else if (this.plantType != null && plantType != null) {
        
        if (plantType != null && !this.plantType.equals(plantType)) {
          
          this.plantType = plantType;
          this.removeAllNodeTagsByType(getTagGroupType());
          this.addNodeTag(this.plantType.getTag());
          setIsModified("plantType:changed");
          plantTypeChanged = true;
          
        } else {
          // DO NOTHING
        }
      }
      
      return plantTypeChanged;
      
    } catch (EntityAlreadyExistsException eaee) {
      throw new IllegalStateException("Unable to set plant type: ["
          + plantType
          + "] for plant: ["
          + getNodePath()
          + "]", eaee);
    }
  }
  
  @Override
  public int calculateTotalMappedPointCount() {
    
    int childPointCount = getDirectChildMappedPointCount();

    for (LoopEntity energyExchangeLoop : childLoops) {
      childPointCount = childPointCount + energyExchangeLoop.getTotalMappedPointCount();  
    }    
    
    return childPointCount;
  }
  
  @Override
  public void validate(
      Set<IssueType> issueTypes, 
      List<ValidationMessage> validationMessages, 
      boolean remediate) {
    
    super.validate(issueTypes, validationMessages, remediate);
    
    for (LoopEntity energyExchangeLoop : childLoops) {
      energyExchangeLoop.validate(issueTypes, validationMessages, remediate);  
    }    
  }  

  public AbstractNodeEntity getChildNodeByNameNullIfNotExists(String name) {
    
    AbstractNodeEntity childNode = getChildLoopByNameNullIfNotExists(name);
    if (childNode == null) {

      childNode = getChildPointByNameNullIfNotExists(name);
    }
    
    return childNode;
  }
  
  @SuppressWarnings("unchecked")
  @Override
  protected <T extends AbstractNodeEntity> Set<T> getChildSet(AbstractNodeEntity t) {
    
    if (t instanceof AbstractPointEntity) {
      
      return (Set<T>)getChildPoints();
      
    } else if (t instanceof LoopEntity) {
      
      return (Set<T>)this.childLoops;
      
    } 
    throw new UnsupportedOperationException("Energy Exchange Plant: ["
        + getNodePath()
        + "] does not support having child: ["
        + t.getClassAndNaturalIdentity()
        + "]");
  }
  
  @Override
  public void mapToDtos(Map<String, Object> dtos) {
    
    super.mapToDtos(dtos);

    for (LoopEntity energyExchangeLoop : childLoops) {
      if (!energyExchangeLoop.getIsDeleted()) {
        energyExchangeLoop.mapToDtos(dtos);  
      }
    }
  }  
}
//@formatter:on