//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy;

import static java.util.Objects.requireNonNull;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.AbstractPersistentEntity;
import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.customer.AbstractCustomerEntity;
import com.djt.hvac.domain.model.dictionary.TagEntity;
import com.djt.hvac.domain.model.dictionary.enums.NodeSubType;
import com.djt.hvac.domain.model.dictionary.enums.NodeType;
import com.djt.hvac.domain.model.dictionary.enums.TagGroupType;
import com.djt.hvac.domain.model.function.AdFunctionInstanceEligiblePoint;
import com.djt.hvac.domain.model.nodehierarchy.building.BuildingEntity;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.EnergyExchangeEntity;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.EquipmentEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.AbstractPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.AdFunctionAsyncComputedPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.ScheduledAsyncComputedPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.WeatherAsyncComputedPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.custom.CustomAsyncComputedPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.mapped.MappablePointEntity;
import com.djt.hvac.domain.model.rawpoint.RawPointEntity;
import com.google.common.collect.ImmutableList;

public abstract class AbstractNodeEntity extends AbstractPersistentEntity {
  private static final long serialVersionUID = 1L;
  
  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractNodeEntity.class);

  public static final List<Integer> EMPTY_INTEGER_LIST = ImmutableList.of();
  public static final List<String> EMPTY_STRING_LIST = ImmutableList.of();

  private AbstractNodeEntity parentNode;
  private String name;
  private String displayName;
  private String uuid;
  private Timestamp createdAt;
  private Timestamp updatedAt;
  private Set<TagEntity> nodeTags = new HashSet<>();
  private Set<AbstractPointEntity> childPoints = new TreeSet<>();
  private Set<Integer> deletedChildNodes;

  private transient Integer _totalMappedPointCount;
  private transient Map<String, AbstractPointEntity> _childPointIndex;
  private transient Set<AdFunctionInstanceEligiblePoint> _adFunctionInstanceEligiblePoints;
  private transient Set<AbstractNodeEntity> _descendantNodes;
  
  @Override
  protected void resetTransientAttributes() {
    super.resetTransientAttributes();
    _totalMappedPointCount = null;
    _childPointIndex = null;
    _adFunctionInstanceEligiblePoints = null;
    _descendantNodes = null;
  }
  
  public AbstractNodeEntity() {}

  public AbstractNodeEntity(
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
        AbstractEntity.parseTimestamp(createdAt),
        AbstractEntity.parseTimestamp(updatedAt),
        nodeTags);
  }
  
  // For new instances (i.e. have not been persisted yet)
  public AbstractNodeEntity(
      AbstractNodeEntity parentNode,
      String name,
      String displayName) {
    this(
        null,
        parentNode,
        name,
        displayName,
        UUID.randomUUID().toString(),
        AbstractEntity.getTimeKeeper().getCurrentTimestamp(),
        AbstractEntity.getTimeKeeper().getCurrentTimestamp(),
        null);
  }  
  
  public AbstractNodeEntity(
      Integer persistentIdentity,
      AbstractNodeEntity parentNode,
      String name,
      String displayName,
      String uuid,
      Timestamp createdAt,
      Timestamp updatedAt,
      Set<TagEntity> nodeTags) {
    super(persistentIdentity);
    requireNonNull(name, "name cannot be null");
    requireNonNull(displayName, "displayName cannot be null");
    this.parentNode = parentNode;
    this.name = name;
    this.displayName = displayName;
    if (uuid != null) {
      this.uuid = uuid;
    } else {
      this.uuid = UUID.randomUUID().toString();
    }
    if (createdAt != null) {
      this.createdAt = createdAt;  
    }
    if (updatedAt != null) {
      this.updatedAt = updatedAt;  
    }
    if (nodeTags != null) {
      this.nodeTags.addAll(nodeTags);
      validateNodeTags();
    }
  }
  
  // For the moveChildNodesToNewParentNode() service method
  public AbstractNodeEntity setNewParentNode(AbstractNodeEntity newParentNode) {
    
    /*
     * Need to perform remediation for names, scoped tags, etc.
     * 
     * RP-6562: Bulk Node API: Inline changes for move/delete subtree.
     *
     * 2. Floor ordinal conflict: The floor being moved will take the ordinal that is in
     * conflict and the existing floor with the ordinal that is in conflict will have its
     * ordinal incremented, as well as affected floors above it (so there may be a cascade
     * of ordinal changes).
     * 
     * 3. Conflict if node includes child with scoped tag: Remove the scoped tag from the
     * node being moved.
     * 
     * 4. If point is being moved to equipment whose equipment type is not compatible
     * (either not in list for new equipment type or null), then remove the point template
     * association and delete the point's haystack tags.
     */    
    try {

      // Get the current parent for the given child node.
      AbstractNodeEntity oldParentNode = this.parentNode;
      
      
      // Remove the child node from the old parent node's child collection
      // and then add to the new parent node's child collection.
      oldParentNode.removeChild(oldParentNode.getChildSet(this), this);
      
      String oldName = this.getName();
      String oldDisplayName = this.getDisplayName();
      
      String newName = oldName;
      String newDisplayName = oldDisplayName;
      
      AbstractNodeEntity node = newParentNode.getChildNodeByNameNullIfNotExists(
          getClass(),
          newParentNode.getChildSet(this), 
          oldName);
      
      if (node != null) {
      
        newName = getUniqueNameForNewChildNode(
            getClass(), 
            newParentNode.getChildSet(this), 
            oldName, 
            newParentNode);
        
        if (oldName.equals(oldDisplayName)) {
          newDisplayName = newName; 
        }
      }
      
      if (!newName.equals(oldName)) {
        this.setName(newName);
      }

      if (!newDisplayName.equals(oldDisplayName)) {
        this.setDisplayName(newDisplayName);
      }
      newParentNode.addChild(newParentNode.getChildSet(this), this, newParentNode);

      
      // Change the child node's association to the parent node to be that of the new parent.
      this.parentNode = newParentNode;
      
      // If the 'this' node is a floor, then ensure there is no floor ordinal conflict.
      if (this instanceof FloorEntity) {
        
        ((FloorEntity)this).remediateFloorOrdinalConflicts();
        
      } else if (this instanceof MappablePointEntity) {
        
        ((MappablePointEntity)this).remediatePointTemplateConflicts();
        
      }
      
      // Ensure there aren't any scoped tag conflicts.
      remediateScopedTagConflicts();
      
      // Reset all transient attributes, as things have changed.
      newParentNode.resetTransientAttributes();
      oldParentNode.resetTransientAttributes();
      this.resetTransientAttributes();
      
      newParentNode.setIsModified("child moved in");
      oldParentNode.setIsModified("child moved out");
      this.setIsModified("new parent");
      
      return oldParentNode;
      
    } catch (Exception e) {
      throw new IllegalStateException("Unable to move node with id: ["
          + this.getPersistentIdentity()
          + "] to new parent node with id: ["
          + newParentNode.getPersistentIdentity()
          + "], error: "
          + e.getMessage(), e);
    }
  }
  
  public void addDeletedChildNode(AbstractNodeEntity entity) {
    
    Integer id = entity.getPersistentIdentity();
    if (id != null) {
      if (deletedChildNodes == null) {
        deletedChildNodes = new HashSet<>();
      }
      deletedChildNodes.add(id);
    }
  }
  
  public Set<Integer> getDeletedChildNodes() {

    if (deletedChildNodes != null) {
      return deletedChildNodes;
    }
    return new HashSet<>();
  }
  
  public void resetDeletedChildNodes() {
    
    deletedChildNodes = null;
  }
  
  public final int getTotalMappedPointCount() {
    
    if (_totalMappedPointCount == null) {
      
      _totalMappedPointCount = calculateTotalMappedPointCount();
    }
    return _totalMappedPointCount;
  }
  
  // This is to facilitate use cases where a depth filter is 
  // applied, but the point count is needed.  For the file system 
  // repository implementation, this will never be used, but for
  // the JDBC implementation, this value will be retrieved by the 
  // get_node_point_count() stored procedure.
  public final void setTotalMappedPointCount(Integer count) {
    
    _totalMappedPointCount = count;
  }
  
  protected final int getDirectChildMappedPointCount() {
    
    int count = 0;
    for (AbstractPointEntity point: childPoints) {
      if (point instanceof MappablePointEntity) {
        count++;
      }
    }
    return count;
  }
  
  public abstract AbstractNodeEntity getChildNodeByNameNullIfNotExists(String name);

  public abstract int calculateTotalMappedPointCount();
  
  public abstract Set<AbstractNodeEntity> getAllChildNodes();
  
  /**
   * 
   * NOTE: To be used only for testing purposes!!!
   * 
   * @param portfolio The owning portfolio
   * @param parentNode The (newly copied) parent node
   * @param duplicationIndex A sequence number to use for creating a unique node name
   * 
   * @return the duplicated node
   */
  public abstract AbstractNodeEntity duplicateNode(
      PortfolioEntity portfolio,
      AbstractNodeEntity parentNode,
      int duplicationIndex);

  /**
   * 
   * NOTE: To be used only for testing purposes!!!
   * 
   * @param portfolio The owning portfolio
   * @param parentNode The (newly copied) parent node
   * @param duplicationIndex A sequence number to use for creating a unique node name
   */
  protected void duplicatePointNodes(
      PortfolioEntity portfolio,
      AbstractNodeEntity parentNode,
      int duplicationIndex) {
    
    try {
      
      String metricIdPrefix = "__.Bldg#.__".replace("#", Integer.toString(duplicationIndex));
      
      AbstractCustomerEntity customer = portfolio.getParentCustomer();
      
      for (AbstractPointEntity point : getChildPoints()) {
        
        if (point instanceof MappablePointEntity) {
          
          MappablePointEntity mappablePoint = (MappablePointEntity)point;
          RawPointEntity rawPoint = mappablePoint.getRawPoint();
          
          RawPointEntity rp = new RawPointEntity(
              null,
              rawPoint.getCustomerId(),
              rawPoint.getComponentId(),
              metricIdPrefix + rawPoint.getMetricId(),
              rawPoint.getPointType(),
              rawPoint.getRange(),
              rawPoint.getUnitType(),
              rawPoint.getIgnored(),
              rawPoint.getDeleted(),
              rawPoint.getCreatedAtAsString());
          
          customer.addRawPoint(rp);
          
          MappablePointEntity mp = new MappablePointEntity(
              null,
              parentNode,
              Integer.toString(duplicationIndex) + "_" + mappablePoint.getName(),
              Integer.toString(duplicationIndex) + "_" + mappablePoint.getDisplayName(),
              AbstractEntity.formatTimestamp(getCreatedAt()),
              AbstractEntity.formatTimestamp(getUpdatedAt()),
              mappablePoint.getNodeTags(),
              mappablePoint.getDataType(),
              mappablePoint.getUnitNullIfNotExists(),
              mappablePoint.getRangeNullIfEmpty(),
              mappablePoint.getPointTemplateNullIfEmpty(),
              mappablePoint.getLastValue(),
              mappablePoint.getLastValueTimestamp(),
              rp,
              mappablePoint.getIsChangeOfValue());
          
          portfolio.addNodeToParentAndIndex(parentNode, mp);
          
        } else if (point instanceof CustomAsyncComputedPointEntity) {
          
          CustomAsyncComputedPointEntity customPoint = (CustomAsyncComputedPointEntity)point;
          
          CustomAsyncComputedPointEntity cp = new CustomAsyncComputedPointEntity(
              null,
              parentNode,
              Integer.toString(duplicationIndex) + "_" + customPoint.getName(),
              Integer.toString(duplicationIndex) + "_" + customPoint.getDisplayName(),
              AbstractEntity.formatTimestamp(getCreatedAt()),
              AbstractEntity.formatTimestamp(getUpdatedAt()),
              customPoint.getNodeTags(),
              customPoint.getDataType(),
              customPoint.getUnitNullIfNotExists(),
              customPoint.getPointTemplateNullIfEmpty(),
              customPoint.getLastValue(),
              customPoint.getLastValueTimestamp(),
              metricIdPrefix + customPoint.getMetricId(),
              customPoint.getConfigurable(),
              customPoint.getTimezoneBasedRollups(),
              customPoint.getComputationInterval());
          
          portfolio.addNodeToParentAndIndex(parentNode, cp);
          
        } else if (point instanceof AdFunctionAsyncComputedPointEntity) {

          AdFunctionAsyncComputedPointEntity adFunctionInstancePoint = (AdFunctionAsyncComputedPointEntity)point;
          
          AdFunctionAsyncComputedPointEntity ap = new AdFunctionAsyncComputedPointEntity(
              null,
              parentNode,
              Integer.toString(duplicationIndex) + "_" + adFunctionInstancePoint.getName(),
              Integer.toString(duplicationIndex) + "_" + adFunctionInstancePoint.getDisplayName(),
              AbstractEntity.formatTimestamp(getCreatedAt()),
              AbstractEntity.formatTimestamp(getUpdatedAt()),
              adFunctionInstancePoint.getNodeTags(),
              adFunctionInstancePoint.getDataType(),
              adFunctionInstancePoint.getUnitNullIfNotExists(),
              adFunctionInstancePoint.getRangeNullIfEmpty(),
              adFunctionInstancePoint.getPointTemplateNullIfEmpty(),
              adFunctionInstancePoint.getLastValue(),
              adFunctionInstancePoint.getLastValueTimestamp(),
              metricIdPrefix + adFunctionInstancePoint.getMetricId(),
              adFunctionInstancePoint.getConfigurable(),
              adFunctionInstancePoint.getTimezoneBasedRollups(),
              adFunctionInstancePoint.getGlobalComputedPointId());
          
          portfolio.addNodeToParentAndIndex(parentNode, ap);
          
        } else if (point instanceof ScheduledAsyncComputedPointEntity) {
          
          ScheduledAsyncComputedPointEntity scheduledPoint = (ScheduledAsyncComputedPointEntity)point;
          
          ScheduledAsyncComputedPointEntity sp = new ScheduledAsyncComputedPointEntity(
              null,
              (BuildingEntity)parentNode,
              Integer.toString(duplicationIndex) + "_" + scheduledPoint.getName(),
              Integer.toString(duplicationIndex) + "_" + scheduledPoint.getDisplayName(),
              AbstractEntity.formatTimestamp(getCreatedAt()),
              AbstractEntity.formatTimestamp(getUpdatedAt()),
              scheduledPoint.getNodeTags(),
              scheduledPoint.getDataType(),
              scheduledPoint.getUnitNullIfNotExists(),
              scheduledPoint.getRangeNullIfEmpty(),
              scheduledPoint.getPointTemplateNullIfEmpty(),
              scheduledPoint.getLastValue(),
              scheduledPoint.getLastValueTimestamp(),
              metricIdPrefix + scheduledPoint.getMetricId(),
              scheduledPoint.getConfigurable(),
              scheduledPoint.getTimezoneBasedRollups(),
              scheduledPoint.getGlobalComputedPointId(),
              scheduledPoint.getScheduledEventType());
          
          portfolio.addNodeToParentAndIndex(parentNode, sp);
          
        } else if (point instanceof WeatherAsyncComputedPointEntity) {
          
          WeatherAsyncComputedPointEntity weatherPoint = (WeatherAsyncComputedPointEntity)point;
          
          WeatherAsyncComputedPointEntity wp = new WeatherAsyncComputedPointEntity(
              null,
              parentNode,
              Integer.toString(duplicationIndex) + "_" + weatherPoint.getName(),
              Integer.toString(duplicationIndex) + "_" + weatherPoint.getDisplayName(),
              AbstractEntity.formatTimestamp(getCreatedAt()),
              AbstractEntity.formatTimestamp(getUpdatedAt()),
              weatherPoint.getNodeTags(),
              weatherPoint.getDataType(),
              weatherPoint.getUnitNullIfNotExists(),
              weatherPoint.getRangeNullIfEmpty(),
              weatherPoint.getPointTemplateNullIfEmpty(),
              weatherPoint.getLastValue(),
              weatherPoint.getLastValueTimestamp(),
              metricIdPrefix + weatherPoint.getMetricId(),
              weatherPoint.getConfigurable(),
              weatherPoint.getTimezoneBasedRollups(),
              weatherPoint.getGlobalComputedPointId());
          
          portfolio.addNodeToParentAndIndex(parentNode, wp);
          
        } else {
          throw new RuntimeException("Unsupported point type: "
              + point.getClassAndNaturalIdentity());
        }
      } 
      
    } catch (EntityAlreadyExistsException eaee) {
      throw new IllegalStateException("Unable to duplicate child points for: ["
          + getClassAndNaturalIdentity()
          + "] and duplicationIndex: ["
          + duplicationIndex
          + "].", eaee);
    }
  }
  
  public abstract NodeType getNodeType();

  public abstract NodeSubType getNodeSubType();

  public abstract void mapToDtos(Map<String, Object> dtos);

  public AbstractNodeEntity getParentNode() {
    return parentNode;
  }

  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    
    this.name = name;
    this.setIsModified("name");
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    
    if (displayName != null && !this.displayName.equals(displayName)) {
      
      if (displayName.trim().isEmpty()) {
        
        throw new IllegalArgumentException("displayName cannot be null for "
            + this.getNodePath());
      }
      
      this.displayName = displayName;
      this.setIsModified("displayName");
    }
  }
  
  public String getUuid() {
    return uuid;
  }

  public String getCreatedAtAsString() {
    return AbstractEntity.formatTimestamp(createdAt);
  }

  public Timestamp getCreatedAt() {
    return createdAt;
  }

  public String getUpdatedAtAsString() {
    return AbstractEntity.formatTimestamp(updatedAt);
  }

  public Timestamp getUpdatedAt() {
    return updatedAt;
  }
  
  public Set<AbstractPointEntity> getChildPoints() {
    return childPoints;
  }

  public AbstractPointEntity getChildPointByNameNullIfNotExists(String name) {
    
    if (_childPointIndex == null) {
      _childPointIndex = new HashMap<>();
      for (AbstractPointEntity point: childPoints) {
        _childPointIndex.put(point.getName().toUpperCase(), point);
      }
    }
    return _childPointIndex.get(name.toUpperCase());
  }
    
  public boolean addChildPoint(AbstractPointEntity point) throws EntityAlreadyExistsException {
    
    if (this instanceof AbstractPointEntity) {
      
      throw new IllegalArgumentException("Point: ["
          + point.getName()
          + "] of type: ["
          + point.getClass().getSimpleName()
          + "] cannot be added to node: ["
          + getClassAndNaturalIdentity()
          + "]");
    }
    
    return addChild(childPoints, point, this);
  }

  public boolean removeChildPoint(AbstractPointEntity point) {

    _adFunctionInstanceEligiblePoints = null;
    return childPoints.remove(point);
  }

  public AbstractPointEntity getChildPoint(Integer persistentIdentity)
      throws EntityDoesNotExistException {
    return (AbstractPointEntity) getRootPortfolioNode().getChildNode(persistentIdentity);
  }

  public Set<TagEntity> getNodeTags() {
    return nodeTags;
  }

  public boolean removeAllNodeTagsByType(TagGroupType tagGroup) {
    
    if (tagGroup == null) {
      throw new IllegalStateException("tagGroup cannot be null");
    }

    Set<TagEntity> tagsToRemove = new HashSet<>();
    Iterator<TagEntity> iterator = nodeTags.iterator();
    while (iterator.hasNext()) {

      TagEntity tag = iterator.next();
      if (tag.getTagGroupType().equals(tagGroup)) {

        tagsToRemove.add(tag);
      }
    }
    return removeNodeTags(tagsToRemove);
  }
  
  public boolean removeNodeTag(TagEntity tagToRemove) {
    
    boolean changed = nodeTags.remove(tagToRemove);
    if (changed) {
      setIsModified("nodeTags:remove");  
    }
    return changed;
  }

  public boolean removeNodeTags(Set<TagEntity> tagsToRemove) {
    
    boolean changed = nodeTags.removeAll(tagsToRemove);
    if (changed) {
      setIsModified("nodeTags:removed");  
    }
    return changed;
  }
  
  public boolean removeAllNodeTags() {

    boolean changed = false;
    if (!nodeTags.isEmpty()) {
      nodeTags.clear();
      setIsModified("nodeTags:removed");
    }
    return changed;
  }  

  public boolean addNodeTag(TagEntity tag) throws EntityAlreadyExistsException {
    
    boolean changed = addChild(nodeTags, tag, this);
    if (changed) {
      setIsModified("nodeTags:add");  
    }
    validateNodeTags();
    return changed;
  }
  
  public boolean addNodeTags(Set<TagEntity> tagsToAdd) {
    
    boolean changed = nodeTags.addAll(tagsToAdd);
    if (changed) {
      setIsModified("nodeTags:added");  
    }
    validateNodeTags();
    return changed;
  }
  
  public void validateNodeTags() {
    
    NodeType nodeType = getNodeType();
    for (TagEntity tag: nodeTags) {
      if (!tag.getTagGroupType().getNodeTypes().contains(nodeType)) {

        throw new IllegalStateException("tag: ["
            + tag
            + "] is not valid for nodeType: ["
            + nodeType
            + "] for node: ["
            + getClassAndNaturalIdentity()
            + "]");
      }
    }
  }

  public Set<String> getNodeTagNames() {
    return this.getNodeTagNames(null);
  }

  public Set<String> getNodeTagNames(TagGroupType tagGroup) {

    Set<String> tagNames = new TreeSet<>();
    Iterator<TagEntity> iterator = nodeTags.iterator();
    while (iterator.hasNext()) {

      TagEntity tag = iterator.next();
      if (tagGroup == null || tag.getTagGroupType().equals(tagGroup)) {

        tagNames.add(tag.getName());
      }
    }
    return tagNames;
  }

  public List<Integer> getNodeTagIdsAsList() {
    
    if (nodeTags.isEmpty()) {
      return EMPTY_INTEGER_LIST;
    }
    List<Integer> tagIds = new ArrayList<>();
    for (TagEntity tag: nodeTags) {
      tagIds.add(tag.getPersistentIdentity());
    }
    return tagIds;
  }

  public List<String> getNodeTagNamesAsSortedList() {
    
    if (nodeTags.isEmpty()) {
      return EMPTY_STRING_LIST;
    }
    List<String> tagNames = new ArrayList<>();
    tagNames.addAll(getNodeTagNames());
    Collections.sort(tagNames);
    return ImmutableList.copyOf(tagNames);
  }
  
  // RECURSIVE
  @Override
  public String getNaturalIdentity() {

    if (_naturalIdentity == null) {

      _naturalIdentity = new StringBuilder()
          .append((parentNode != null) ? parentNode.getNaturalIdentity() : "")
          .append(NATURAL_IDENTITY_DELIMITER)
          .append(getName())
          .toString();
    }
    return _naturalIdentity;
  }
  
  // RECURSIVE - Used only for testing purposes (i.e. duplicating buildings)
  public static String deriveDuplicatedNaturalIdentity(AbstractNodeEntity node, int duplicationIndex) {

    StringBuilder sb = new StringBuilder();
    
    AbstractNodeEntity parentNode = node.getParentNode();
    if (parentNode != null) {
      if (node instanceof PortfolioEntity) {
        sb.append(node.getNaturalIdentity());
      } else {
        sb.append(AbstractNodeEntity.deriveDuplicatedNaturalIdentity(parentNode, duplicationIndex));  
      }
    }
    
    sb.append(NATURAL_IDENTITY_DELIMITER);
    
    if (node instanceof PortfolioEntity) {
      sb.append(node.getName());
      String nodeName = node.getName();
      nodeName = Integer.toString(duplicationIndex) + "_" + nodeName;
    } else {
      sb.append(Integer.toString(duplicationIndex));
      sb.append("_");
      sb.append(node.getName());
    }
    
    return sb.toString();
  }
  
  public Set<AdFunctionInstanceEligiblePoint> getAdFunctionInstanceEligiblePoints() {

    if (_adFunctionInstanceEligiblePoints == null) {

      _adFunctionInstanceEligiblePoints = new HashSet<>();
      Iterator<AbstractPointEntity> pointIterator = childPoints.iterator();
      while (pointIterator.hasNext()) {

        AbstractPointEntity point = pointIterator.next();
        if (point instanceof AdFunctionInstanceEligiblePoint && !point.getIsDeleted()) {

          _adFunctionInstanceEligiblePoints.add((AdFunctionInstanceEligiblePoint) point);
        }
      }
    }
    return _adFunctionInstanceEligiblePoints;
  }

  public String getNodePath() {
    return new StringBuilder()
        .append((parentNode != null) ? parentNode.getNodePath() : "")
        .append(NATURAL_IDENTITY_DELIMITER)
        .append(getDisplayName())
        .toString();
  }

  // Recursive method
  public Integer getCustomerId() {

    if (parentNode != null) {
      return parentNode.getCustomerId();
    }
    if (this instanceof PortfolioEntity) {
      return ((PortfolioEntity) this).getCustomerId();
    }
    throw new IllegalStateException(
        "Expected this to be an instance of PortfolioEntity, but instead was: ["
            + this.getClassAndNaturalIdentity()
            + "], with persistentIdentity: ["
            + this.getPersistentIdentity()
            + "] as parent node is null");
  }

  // Recursive method
  public PortfolioEntity getRootPortfolioNode() {

    if (parentNode != null) {
      return parentNode.getRootPortfolioNode();
    }
    if (this instanceof PortfolioEntity) {
      return (PortfolioEntity) this;
    }
    throw new IllegalStateException(
        "Expected this to be an instance of PortfolioEntity, but instead was: ["
            + this.getClassAndNaturalIdentity()
            + "], with persistentIdentity: ["
            + this.getPersistentIdentity()
            + "] as parent node is null");
  }

  // Recursive method
  public boolean isAncestor(AbstractNodeEntity node) {

    if (node == this) {
      return true;
    }

    AbstractNodeEntity parentNode = node.getParentNode();
    if (parentNode != null) {

      return isAncestor(parentNode);
    }

    return false;
  }
  
  // Recursive method
  public boolean isAncestorOfEnergyExchangeSystemNode(EnergyExchangeEntity node) {

    if (node == this) {
      return true;
    }

    AbstractNodeEntity parentNode = node.getParentNode();
    if (parentNode != null) {

      return isAncestor(parentNode);
    }

    return false;
  }
  
  // Recursive method
  public BuildingEntity getAncestorBuilding() {
    
    if (this instanceof PortfolioEntity) {
      
      throw new IllegalStateException(getNodePath()
          + " ("
          + getClass().getSimpleName()
          + ") has no ancestor building");
    }
    
    if (this instanceof BuildingEntity) {
      return (BuildingEntity)this;
    }

    AbstractNodeEntity parentNode = getParentNode();
    while (parentNode != null) {

      if (parentNode instanceof BuildingEntity) {
        return (BuildingEntity)parentNode;
      }
      parentNode = parentNode.getParentNode();
    }
    throw new IllegalStateException(getNodePath()
        + " ("
        + getClass().getSimpleName()
        + ") has no ancestor building");
  }
  
  // Recursive method
  public AbstractNodeEntity getAncestorNodeNullIfNotExists(NodeType nodeType) {
    
    if (getNodeType().equals(nodeType)) {
      return this;
    }
    AbstractNodeEntity parentNode = getParentNode();
    while (parentNode != null) {

      NodeType parentNodeType = parentNode.getNodeType();
      if (parentNodeType.equals(nodeType)) {
        return parentNode;
      }
      parentNode = parentNode.getParentNode();
    }
    return null;
  }
  
  // Recursive method
  public Set<AbstractNodeEntity> getDescendantNodes() {
    
    if (_descendantNodes == null) {

      _descendantNodes = new HashSet<>();
      for (AbstractNodeEntity node: getAllChildNodes()) {
        
        _descendantNodes.add(node);
        _descendantNodes.addAll(node.getDescendantNodes());
      }
    }
    return _descendantNodes;
  }

  // Recursive method
  public Set<SubBuildingEntity> getDescendantSubBuildings() {
    
    Set<SubBuildingEntity> set = new HashSet<>();
    for (AbstractNodeEntity node: getAllChildNodes()) {
      
      if (node instanceof SubBuildingEntity) {
        set.add((SubBuildingEntity)node);  
      } else {
        set.addAll(node.getDescendantSubBuildings());  
      }
    }
    return set;
  }  
  
  // Recursive method
  public Set<FloorEntity> getDescendantFloors() {
    
    Set<FloorEntity> set = new HashSet<>();
    for (AbstractNodeEntity node: getAllChildNodes()) {
      
      if (node instanceof FloorEntity) {
        set.add((FloorEntity)node);  
      } else {
        set.addAll(node.getDescendantFloors());  
      }
    }
    return set;
  }  
  
  // Recursive method
  public Set<EnergyExchangeEntity> getDescendantEnergyExchangeSystemNodes() {
    
    Set<EnergyExchangeEntity> set = new HashSet<>();
    for (AbstractNodeEntity node: getAllChildNodes()) {
      
      if (node instanceof EnergyExchangeEntity) {
        set.add((EnergyExchangeEntity)node);  
      } else {
        set.addAll(node.getDescendantEnergyExchangeSystemNodes());  
      }
    }
    return set;
  } 
  
  // Recursive method
  public Set<EquipmentEntity> getDescendantEquipment() {
    
    Set<EquipmentEntity> set = new HashSet<>();
    for (AbstractNodeEntity node: getAllChildNodes()) {
      
      if (node instanceof EquipmentEntity) {
        set.add((EquipmentEntity)node);  
      } else {
        set.addAll(node.getDescendantEquipment());  
      }
    }
    return set;
  }  

  // Recursive method
  public Set<AbstractPointEntity> getDescendantPoints() {
    
    Set<AbstractPointEntity> set = new HashSet<>();
    for (AbstractNodeEntity node: getAllChildNodes()) {
      
      if (node instanceof AbstractPointEntity) {
        set.add((AbstractPointEntity)node);  
      } else {
        set.addAll(node.getDescendantPoints());  
      }
    }
    return set;
  }  
  
  // Recursive method
  public Set<AbstractNodeEntity> getAncestorNodes() {
    
    Set<AbstractNodeEntity> set = new HashSet<>();
    if (parentNode != null && !(parentNode instanceof PortfolioEntity)) {
      
      set.add(parentNode);
      set.addAll(parentNode.getAncestorNodes());
    }
    return set;
  }  
  
  public Timestamp setUpdatedAt() {
    return this.updatedAt = AbstractEntity.getTimeKeeper().getCurrentTimestamp();
  }
  
  // Recursive method
  @Override
  public void setIsModified(String modifiedAttributeName) {
    
    if (!getRootPortfolioNode().isBeingMapped) {

      // Set the modified flag on this node for the specified attribute.
      super.setIsModified(modifiedAttributeName);
      
      // We update the timestamp of every node except for the root portfolio node, as 
      // we perform stale data checking against the repository with the timestamp from
      // when the portfolio was loaded.  When we store to the repository, the timestamp
      // will be updated to the current timestamp.
      if (this instanceof PortfolioEntity == false) {
      
        updatedAt = AbstractEntity.getTimeKeeper().getCurrentTimestamp();
      }
      
      // Set the modified flag on every parent up to the root portfolio
      // node to be modified for the 'updatedAt' field. (This is to
      // support stale data checking for mutation operations)
      AbstractNodeEntity parentNode = getParentNode();
      while (parentNode != null) {

        parentNode.setIsModified("child updated");
        
        if (parentNode.getPersistentIdentity() != null) {
          parentNode = parentNode.getParentNode();
        } else {
          return;
        }
      }
      
      // TEMPORARY FOR node_graph_edges migration
    } else if (modifiedAttributeName.equals("migrateToEnergyExchangeNodes:parentEquipment")) {
      
      // Set the modified flag on this node for the specified attribute.
      super.setIsModified(modifiedAttributeName);
      
      // Set the modified flag on every parent up to the root portfolio
      // node to be modified for the 'updatedAt' field. (This is to
      // support stale data checking for mutation operations)
      AbstractNodeEntity parentNode = getParentNode();
      while (parentNode != null) {

        parentNode.setIsModified("child updated");
        
        if (parentNode.getPersistentIdentity() != null) {
          parentNode = parentNode.getParentNode();
        } else {
          return;
        }
      }
      
    }
  }
  
  // Recursive method
  @Override
  public void setIsDeleted() {
    
    super.setIsDeleted();
    _adFunctionInstanceEligiblePoints = null;
    parentNode.addDeletedChildNode(this);
    
    // Set the modified flag on every parent up to the root portfolio
    // node to be modified for the 'updatedAt' field. (This is to
    // support stale data checking for mutation operations)
    AbstractNodeEntity parentNode = getParentNode();
    while (parentNode != null) {

      if (parentNode.getPersistentIdentity() != null) {

        parentNode.setIsModified("child deleted");
        parentNode = parentNode.getParentNode();
        
      } else {
        return;
      }
    }
  }

  @Override
  public String toString() {
    return getNodePath();
  }
  
  protected <T extends AbstractPersistentEntity> T getChild(
      Class<T> childClass,
      Set<T> set,
      Integer persistentIdentity,
      AbstractPersistentEntity parent) throws EntityDoesNotExistException {

    return set
        .stream()
        .filter(t -> t.getPersistentIdentity().equals(persistentIdentity))
        .findAny()
        .orElseThrow(() -> new EntityDoesNotExistException(
            childClass.getSimpleName()
            + " with id: [" 
            + persistentIdentity 
            + "] not found in "
            + parent.getClassAndPersistentIdentity()
            ));
  }
  
  protected abstract <T extends AbstractNodeEntity> Set<T> getChildSet(AbstractNodeEntity t);
  
  /*
   * Conflict if node includes child with scoped tag: Remove the scoped tag from the
   * node being moved
   */
  public void remediateScopedTagConflicts() {
    
    Set<TagEntity> victimTags = new HashSet<>();
    for (TagEntity tag: nodeTags) {

      NodeType scopedConstraintNodeType = tag.getScopedToConstraint();
      if (scopedConstraintNodeType != null) {
        
        boolean removeTag = false;
        AbstractNodeEntity ancestorNode = getAncestorNodeNullIfNotExists(scopedConstraintNodeType);
        
        // Scoped tag conflict: Missing ancestor
        if (ancestorNode == null) {
          
          removeTag = true;
          LOGGER.info("Removing scoped constraint tag: ["
              + tag
              + "] from node: ["
              + getNaturalIdentity()
              + "] because it is missing the scoped ancestor node type: ["
              + scopedConstraintNodeType
              + "]");
        } else {
          
          // Scoped tag conflict: Duplicate
          Set<AbstractNodeEntity> nodes = getRootPortfolioNode().getAllDescendantNodesWithTag(ancestorNode, tag);
          for (AbstractNodeEntity node: nodes) {
            if (!node.equals(this)) {

              node.removeNodeTag(tag);
              
              LOGGER.info("Removing scoped constraint tag: ["
                  + tag
                  + "] for: ["
                  + ancestorNode
                  + "] from outgoing node: ["
                  + node
                  + "] because the incoming node: ["
                  + getNaturalIdentity()
                  + "] is replacing it.");
            }
          }
        }
        if (removeTag) {
          victimTags.add(tag);
        }
      }
    }
    if (!victimTags.isEmpty()) {
      
      removeNodeTags(victimTags);
    }
  }
  
  protected <T extends AbstractNodeEntity> String getUniqueNameForNewChildNode(
      Class<T> childClass,
      Set<T> set,
      String name,
      AbstractNodeEntity newParentNode) {
    
    T check = set
        .stream()
        .filter(t -> t.getName().equals(name))
        .findAny()
        .orElse(null);
    
    String uniqueName = name;
    if (check != null) {
      
      int sequenceNumber = 0;
      uniqueName = name + "_" + Integer.toString(sequenceNumber);
      while (check != null && sequenceNumber < 1000) {

        sequenceNumber++;
        uniqueName = name + "_" + Integer.toString(sequenceNumber);
        
        final String newName = uniqueName;
        check = set
            .stream()
            .filter(t -> t.getName().equals(newName))
            .findAny()
            .orElse(null);
      }
      if (check != null) {
        throw new IllegalStateException("Unable to find unique sequence number: ["
            + sequenceNumber 
            + "] for child node of type: ["
            + childClass.getSimpleName()
            + "] with name: [" 
            + name 
            + "] for new parent node: ["
            + newParentNode.getNodePath()
            + "]");
      }
    }
    return uniqueName;
  }
  
  protected <T extends AbstractNodeEntity> AbstractNodeEntity getChildNodeByNameNullIfNotExists(
      Class<T> childClass,
      Set<T> set,
      String name) {
    
    T check = set
        .stream()
        .filter(t -> t.getName().equals(name))
        .findAny()
        .orElse(null);
    
    return check;
  }
}
//@formatter:on
