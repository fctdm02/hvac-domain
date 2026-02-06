//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.energyexchange;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.dictionary.TagEntity;
import com.djt.hvac.domain.model.dictionary.container.DictionaryContext;
import com.djt.hvac.domain.model.dictionary.energyexchange.AbstractEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.energyexchange.LoopEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.enums.NodeSubType;
import com.djt.hvac.domain.model.dictionary.enums.NodeType;
import com.djt.hvac.domain.model.dictionary.enums.TagGroupType;
import com.djt.hvac.domain.model.nodehierarchy.AbstractNodeEntity;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.AbstractPointEntity;

public class LoopEntity extends AbstractEnergyExchangeEntity {
  private static final long serialVersionUID = 1L;
  private LoopEnergyExchangeTypeEntity loopType; // Tag Group 10

  // For new instances (i.e. have not been persisted yet)
  public LoopEntity(
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
  
  public LoopEntity(
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
    
    if (this.loopType == null && nodeTags != null) {
      for (TagEntity tag: nodeTags) {
        if (tag.getTagGroupType().equals(TagGroupType.LOOP_TYPE)) {
          
          this.loopType = DictionaryContext.getTagsContainer().getLoopTypeById(tag.getPersistentIdentity());
        }
      }
    }
    
    if (!(parentNode instanceof PlantEntity)) {

      throw new IllegalStateException("Expected parent of loop with id: ["
          + persistentIdentity
          + "] to be a plant, but instead was: ["
          + parentNode.getClassAndPersistentIdentity()
          + "].");
    }    
  }

  @Override
  public AbstractNodeEntity duplicateNode(PortfolioEntity portfolio, AbstractNodeEntity parentNode, int duplicationIndex) {
    
    try {

      LoopEntity loop = new LoopEntity(
          null,
          parentNode,
          Integer.toString(duplicationIndex) + "_" + getName(),
          Integer.toString(duplicationIndex) + "_" + getDisplayName(),
          UUID.randomUUID().toString(),
          AbstractEntity.formatTimestamp(getCreatedAt()),
          AbstractEntity.formatTimestamp(getUpdatedAt()),
          getNodeTags());
      
      portfolio.addNodeToParentAndIndex(parentNode, loop);
      
      duplicatePointNodes(portfolio, loop, duplicationIndex);
            
      return loop;
      
    } catch (EntityAlreadyExistsException eaee) {
      throw new IllegalStateException("Unable to duplicate child nodes for: ["
          + getClassAndNaturalIdentity()
          + "] and duplicationIndex: ["
          + duplicationIndex
          + "].", eaee);
    }
  }
  
  public NodeType getNodeType() {
    return NodeType.LOOP;
  }
  
  public NodeSubType getNodeSubType() {
    return NodeSubType.NONE;
  }
  
  public TagGroupType getTagGroupType() {
    return TagGroupType.LOOP_TYPE;
  }
  
  @Override
  public Optional<AbstractEnergyExchangeTypeEntity> getEnergyExchangeType() {
    
    return Optional.ofNullable(getEnergyExchangeTypeNullIfNotExists());
  }
  
  @Override
  public AbstractEnergyExchangeTypeEntity getEnergyExchangeTypeNullIfNotExists() {
   
    return getLoopTypeNullIfNotExists();
  }
  
  public Optional<LoopEnergyExchangeTypeEntity> getLoopType() {
    
    return Optional.ofNullable(loopType);
  }

  public LoopEnergyExchangeTypeEntity getLoopTypeNullIfNotExists() {
    
    return loopType;
  }
  
  public void setLoopType(LoopEnergyExchangeTypeEntity loopType) throws EntityAlreadyExistsException {
    
    try {
      if (this.loopType == null && loopType == null) {
        
        // DO NOTHING
        
      } else if (this.loopType == null && loopType != null) {
        
        this.loopType = loopType;
        this.addNodeTag(this.loopType.getTag());
        setIsModified("loopType:added");
        
      } else if (this.loopType != null && loopType == null) {
        
        this.loopType = loopType;
        this.removeAllNodeTagsByType(getTagGroupType());
        setIsModified("loopType:removed");
        
      } else if (this.loopType != null && loopType != null) {
        
        if (loopType != null && !this.loopType.equals(loopType)) {
          
          this.loopType = loopType;
          this.removeAllNodeTagsByType(getTagGroupType());
          this.addNodeTag(this.loopType.getTag());
          setIsModified("loopType:changed");
          
        } else {
          // DO NOTHING
        }
      }
    } catch (EntityAlreadyExistsException eaee) {
      throw new IllegalStateException("Unable to set loop type: ["
          + loopType
          + "] for loop: ["
          + getNodePath()
          + "]", eaee);
    }
  }
  
  @Override
  public int calculateTotalMappedPointCount() {
    
    return getDirectChildMappedPointCount();
  }
  
  @Override
  public void validate(
      Set<IssueType> issueTypes, 
      List<ValidationMessage> validationMessages, 
      boolean remediate) {
    
    super.validate(issueTypes, validationMessages, remediate);
  }
  
  @SuppressWarnings("unchecked")
  @Override
  protected <T extends AbstractNodeEntity> Set<T> getChildSet(AbstractNodeEntity t) {
    
    if (t instanceof AbstractPointEntity) {
      return (Set<T>)getChildPoints();
      
    }
    throw new UnsupportedOperationException("Energy Exchange Loop: ["
        + getNodePath()
        + "] does not support having child: ["
        + t.getClassAndNaturalIdentity()
        + "]");
  }
}
//@formatter:on