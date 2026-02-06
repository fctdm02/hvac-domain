//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.energyexchange;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.dictionary.TagEntity;
import com.djt.hvac.domain.model.dictionary.energyexchange.AbstractEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.energyexchange.enums.EnergyExchangeSystemType;
import com.djt.hvac.domain.model.dictionary.enums.NodeType;
import com.djt.hvac.domain.model.dictionary.enums.UnitSystem;
import com.djt.hvac.domain.model.dictionary.template.function.AbstractAdFunctionTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.nodetag.PointTemplateEntity;
import com.djt.hvac.domain.model.function.AbstractAdFunctionInstanceEntity;
import com.djt.hvac.domain.model.function.AdFunctionErrorMessagesEntity;
import com.djt.hvac.domain.model.function.AdFunctionInstanceEligiblePoint;
import com.djt.hvac.domain.model.nodehierarchy.AbstractNodeEntity;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BuildingEntity;
import com.djt.hvac.domain.model.nodehierarchy.dto.EnergyExchangeSystemEdgeDto;
import com.djt.hvac.domain.model.nodehierarchy.point.AbstractPointEntity;

/**
 * 
 * Marker interface for energy exchange system nodes, of which, there are three implementations:
 * <ol>
 *   <li>Equipment</li>
 *   <li>Plant</li>
 *   <li>Loop</li>
 * </ol> 
 * </p>
 * The node hierarchy relationships can be thought of as structural components 
 * (e.g. Buildings, sub buildings and floors can contain Equipment, which then has Points)
 * </p>
 * Independent of this node hierarchy, are the notion of "Energy Exchange Systems" that have a
 * many to many relationship between its nodes.  There are four types of energy exchange systems
 * and are as follows:
 * <ol>
 *   <li><b>Chilled Water</b></li>
 *   <li><b>Hot Water</b></li>
 *   <li><b>Steam</b></li>
 *   <li><b>Air Supply</b></li>
 * </ol>
 * 
 * For example, for the "Air Supply" system, which is a replacement for the old "equipment hierarchy",
 * a set of parent/child relationships can be set between equipment.
 * 
 * The type of nodes that implement this interface (via extending AbstractEnergyExchangeEntity) are:
 * <ol>
 *   <li><b>Plants</b>: Can have Buildings/Sub-Buildings as parents and can have Points as node hierarchy children</li>
 *   <li><b>Loops</b>: Can have Buildings/Sub-Buildings as parents and can have Points as node hierarchy children</li>
 *   <li><b>Equipment</b>: Can have Buildings/Sub-Buildings/Floors/Equipment as node hierarchy parents and can have Equipment/Points as node hierarchy children</li>
 * </ol>
 * 
 * NOTE: The "Energy Exchange Type", is a tag, and should not be confused with the "Energy Exchange System Type".
 * <ol>
 *   <li><b>Plants</b>: Can have a Plant Type tag assigned, such as "HotWater" or "ColdWater"</li>
 *   <li><b>Loops</b>: Can have a Loop Type tag assigned, such as "Primary", "Secondary" or "Tertiary" </li>
 *   <li><b>Equipment</b>: Can have an Equipment Type tag assigned, such as "pump", "heatExchanger", or any existing type </li>
 * </ol>
 * 
 * @author tommyers
 *
 */
public interface EnergyExchangeEntity {
  
  /**
   * 
   * @return The persistent identity of the node
   */
  Integer getPersistentIdentity();
  
  /**
   * 
   * @return The natural identity of the node
   */
  String getNaturalIdentity();
  
  /**
   * 
   * @return The class and natural identity of the node
   */
  String getClassAndNaturalIdentity();
  
  /**
   * 
   * @return The node type
   */
  NodeType getNodeType();
  
  /**
   * 
   * @return The node tags as a sorted list
   */
  List<String> getNodeTagNamesAsSortedList();
  
  /**
   * 
   * @return The node tags as a set of strings
   */
  Set<String> getNodeTagNames();
  
  /**
   * 
   * @param tagsToRemove The tags to remove
   * 
   * @return <code>true</code> If tags were removed
   */
  boolean removeNodeTags(Set<TagEntity> tagsToRemove);
  
  /**
   * 
   * @return The node path of the node
   */
  String getNodePath();
  
  /**
   * 
   * @return The customer id
   */
  Integer getCustomerId();
  
  /**
   * 
   * @return The UUID of the node
   */
  String getUuid();
  
  /**
   * 
   * @return The name of the node
   */
  String getName();

  /**
   * 
   * @return The display name of the node
   */
  String getDisplayName();
  
  /**
   * 
   * @return When the node was created
   */
  Timestamp getCreatedAt();
  
  /**
   * 
   * @return When the node was last updated
   */
  Timestamp getUpdatedAt();
  
  /**
   * 
   * @return The tags associated with the node
   */
  Set<TagEntity> getNodeTags();
  
  /**
   * 
   * @return The child nodes associated with the node
   */
  Set<AbstractNodeEntity> getAllChildNodes();
  
  /**
   * 
   * @param displayName
   */
  void setDisplayName(String displayName);
  
  /**
   * 
   * @return Whether the node has been modified or not
   */
  boolean getIsModified();
  
  /**
   * 
   * @param modifiedAttributeName The name of the modified attribute
   */
  void setIsModified(String modifiedAttributeName);
  
  /**
   *  Sets the node to be marked as deleted, so that it will be removed from the 
   *  repository when the root portfolio is persisted.
   */
  void setIsDeleted();
  
  /**
   * 
   * @return The parent node hierarchy node
   */
  AbstractNodeEntity getParentNode();
  
  /**
   * 
   * @return The first equipment in the air supply energy exchange system
   */
  EquipmentEntity getParentEquipmentNullIfNotExists();
  
  /**
   * 
   * @return The set of AD function instance eligible points
   */
  Set<AdFunctionInstanceEligiblePoint> getAdFunctionInstanceEligiblePoints();
  
  /**
   * 
   * @param adFunctionTemplateId The AD template id of the AD function instance candidate to retrieve
   * @return The requested AD function instance candidate
   */
  AbstractAdFunctionInstanceEntity getAdFunctionInstanceCandidateByTemplateIdNullIfNotExists(Integer adFunctionTemplateId);
  
  /**
   * 
   * @param adFunctionTemplateId The AD template id of the AD function instance to retrieve
   * @return The requested AD function instance
   */
  AbstractAdFunctionInstanceEntity getAdFunctionInstanceByTemplateIdNullIfNotExists(Integer adFunctionTemplateId);
  
  /**
   * 
   * @param adFunctionInstanceCandidate The AD function instance candidate to add
   * 
   * @return <code>true</code> If added successfully, <code>false</code> otherwise
   * 
   * @throws EntityAlreadyExistsException If the given AD function instance candidate already exists
   */
  boolean addAdFunctionInstanceCandidate(AbstractAdFunctionInstanceEntity adFunctionInstanceCandidate) throws EntityAlreadyExistsException;
  
  /**
   * 
   * @param adFunctionInstance The AD function instance to add
   * 
   * @return <code>true</code> If added successfully, <code>false</code> otherwise
   * 
   * @throws EntityAlreadyExistsException If the given AD function instance already exists
   */
  boolean addAdFunctionInstance(AbstractAdFunctionInstanceEntity adFunctionInstance) throws EntityAlreadyExistsException;
  
  /**
   * 
   * @param adFunctionInstanceCandidate The AD function instance candidate to delete 
   */
  void addDeletedAdFunctionInstanceCandidate(AbstractAdFunctionInstanceEntity adFunctionInstanceCandidate);

  /**
   * 
   * @param adFunctionInstance The AD function instance to delete 
   */
  void addDeletedAdFunctionInstance(AbstractAdFunctionInstanceEntity adFunctionInstance);
  
  /**
   * 
   * @return A set containing the persistent identities of all the AD function templates that are associated with this energy exchange entity
   */
  Set<Integer> getBoundAdFunctionTemplateIds();
  
  /**
   * 
   * @return The set of child points (any type)
   */
  Set<AbstractPointEntity> getChildPoints();
  
  /**
   * 
   * @return The set of metadata tags associated with the energy exchange entity
   *         i.e. equipment/plant/loop
   */
  Set<String> getMetadataTags();

  /**
   * 
   * @return The set of <code>AbstractAdFunctionInstanceEntity</code>, in the candidate state, associated with this energy exchange system node instance
   */
  Set<AbstractAdFunctionInstanceEntity> getAdFunctionInstanceCandidates();
  
  /**
   * 
   * @return The set of <code>AbstractAdFunctionInstanceEntity</code> associated with this energy exchange system node instance
   */
  Set<AbstractAdFunctionInstanceEntity> getAdFunctionInstances();

  /**
   * 
   * @param adFunctionInstanceCandidate The AD function instance candidate to remove
   */
  void removeAdFunctionInstanceCandidate(AbstractAdFunctionInstanceEntity adFunctionInstanceCandidate);

  /**
   * 
   */
  void removeAllAdFunctionInstanceCandidates();
  
  /**
   * 
   */
  void removeAllAdFunctionInstances();
  
  /**
   * 
   * @return The ancestor building
   */
  BuildingEntity getAncestorBuilding();

  /**
   * 
   * @return The root portfolio
   */
  PortfolioEntity getRootPortfolioNode();
  
  /**
   * 
   * @return A map whose keys are the comma delimited set of tags and values are the set of mappable and custom computed points that
   *         have point templates that match.  This is to prevent AD computed point function instances from being created that have
   *         the same "tag signature".
   */
  Map<String, AbstractPointEntity> getAssignedPointTemplateHaystackTags();
  
  /**
   * 
   * @param issueTypes
   * @param validationMessages
   * @param remediate
   */
  void validate(
      Set<IssueType> issueTypes, 
      List<ValidationMessage> validationMessages, 
      boolean remediate);

  /**
   * 
   * @return The generic energy exchange type
   */
  Optional<AbstractEnergyExchangeTypeEntity> getEnergyExchangeType();
  
  /**
   * 
   * @return The generic energy exchange type
   */
  AbstractEnergyExchangeTypeEntity getEnergyExchangeTypeNullIfNotExists();

  /**
   * 
   * @param energyExchangeSystemType The type of energy exchange system to retrieve ancestor nodes for
   * <ol>
   *   <li>Chilled Water</li>
   *   <li>Hot Water</li>
   *   <li>Steam</li>
   *   <li>Air Supply</li>
   * </ol>
   * 
   * @return The set of ancestor energy exchange system nodes for the given system type
   */
  Set<EnergyExchangeEntity> getAncestorEnergyExchangeSystemNodes(EnergyExchangeSystemType energyExchangeSystemType);
  
  /**
   * 
   * @param energyExchangeSystemType The type of energy exchange system to retrieve descendant nodes for
   * <ol>
   *   <li>Chilled Water</li>
   *   <li>Hot Water</li>
   *   <li>Steam</li>
   *   <li>Air Supply</li>
   * </ol>
   * 
   * @return The set of descendant energy exchange system nodes for the given system type
   */
  Set<EnergyExchangeEntity> getDescendantEnergyExchangeSystemNodes(EnergyExchangeSystemType energyExchangeSystemType);
  
  /**
   * 
   * @param energyExchangeSystemType The type of energy exchange system to retrieve parent nodes for
   * <ol>
   *   <li>Chilled Water</li>
   *   <li>Hot Water</li>
   *   <li>Steam</li>
   *   <li>Air Supply</li>
   * </ol>
   * 
   * @return The set of parent energy exchange system nodes for the given system type
   */
  Set<EnergyExchangeEntity> getParentEnergyExchangeSystemNodes(EnergyExchangeSystemType energyExchangeSystemType);
  
  /**
   * 
   * @return A sorted map keyed by the following energy exchange system types:
   * <ol>
   *   <li>Chilled Water</li>
   *   <li>Hot Water</li>
   *   <li>Steam</li>
   *   <li>Air Supply</li>
   * </ol>
   * whose values are the set of all parent energy exchange system nodes for the given system time.
   */
  Map<EnergyExchangeSystemType, Set<EnergyExchangeEntity>> getAllParentEnergyExchangeSystemNodes();
  
  /**
   * 
   * @param energyExchangeSystemType The type of energy exchange system to retrieve child nodes for
   * <ol>
   *   <li>Chilled Water</li>
   *   <li>Hot Water</li>
   *   <li>Steam</li>
   *   <li>Air Supply</li>
   * </ol>
   * 
   * @return The set of child energy exchange system nodes for the given system type
   */
  Set<EnergyExchangeEntity> getChildEnergyExchangeSystemNodes(EnergyExchangeSystemType energyExchangeSystemType);

  /**
   * 
   * @return A sorted map keyed by the following energy exchange system types:
   * <ol>
   *   <li>Chilled Water</li>
   *   <li>Hot Water</li>
   *   <li>Steam</li>
   *   <li>Air Supply</li>
   * </ol>
   * whose values are the set of all child energy exchange system nodes for the given system time.
   */
  Map<EnergyExchangeSystemType, Set<EnergyExchangeEntity>> getAllChildEnergyExchangeSystemNodes();

  /**
   * 
   * @param energyExchangeSystemType An energy exchange system type, which is one of:
   * <ol>
   *   <li>Chilled Water</li>
   *   <li>Hot Water</li>
   *   <li>Steam</li>
   *   <li>Air Supply</li>
   * </ol>
   * 
   * @param energyExchangeSystemNode The energy exchange system node to see if it is a descendant
   * 
   * @return <code>true</code> If the given energy exchange system node is an ancestor of the current
   * energy exchange system node being evaluated
   */
  boolean isAncestorEnergyExchangeSystemNode(
      EnergyExchangeSystemType energyExchangeSystemType,
      EnergyExchangeEntity energyExchangeSystemNode);
  
  /**
   * 
   * @param energyExchangeSystemType An energy exchange system type, which is one of:
   * <ol>
   *   <li>Chilled Water</li>
   *   <li>Hot Water</li>
   *   <li>Steam</li>
   *   <li>Air Supply</li>
   * </ol>
   * 
   * @param energyExchangeSystemNode The energy exchange system node to see if it is a descendant
   * 
   * @return <code>true</code> If the given energy exchange system node is a descendant of the current
   * energy exchange system node being evaluated
   */
  boolean isDescendantEnergyExchangeSystemNode(
      EnergyExchangeSystemType energyExchangeSystemType,
      EnergyExchangeEntity energyExchangeSystemNode);
  
  /**
   * 
   * @param energyExchangeSystemType 
   * @param childEnergyExchangeSystemNode
   * @param addInverseChildRelationship
   * 
   * @throws EntityAlreadyExistsException If there already exists a parent-child relationship for the given system type
   */
  void addChildEnergyExchangeSystemNode(
      EnergyExchangeSystemType energyExchangeSystemType,
      EnergyExchangeEntity childEnergyExchangeSystemNode,
      boolean addInverseChildRelationship)
  throws 
      EntityAlreadyExistsException;
  
  /**
   * 
   * @param energyExchangeSystemType
   * @param childEnergyExchangeEntity
   * @param addInverseChildRelationship
   * 
   * @throws EntityDoesNotExistException If there does not exist a parent-child relationship for the given system type
   */
  void removeChildEnergyExchangeSystemNode(
      EnergyExchangeSystemType energyExchangeSystemType, 
      EnergyExchangeEntity childEnergyExchangeEntity,
      boolean addInverseChildRelationship)
  throws 
      EntityDoesNotExistException;

  /**
   * 
   * @param energyExchangeSystemType 
   * @param parentEnergyExchangeSystemNode
   * @param addInverseChildRelationship
   * 
   * @throws EntityAlreadyExistsException If there already exists a parent-child relationship for the given system type
   */
  void addParentEnergyExchangeSystemNode(
      EnergyExchangeSystemType energyExchangeSystemType,
      EnergyExchangeEntity parentEnergyExchangeSystemNode,
      boolean addInverseChildRelationship)
  throws 
      EntityAlreadyExistsException;

  /**
   * 
   * @param energyExchangeSystemType
   * @param parentEnergyExchangeEntity
   * @param addInverseChildRelationship
   * 
   * @throws EntityDoesNotExistException If there does not exist a parent-child relationship for the given system type
   */
  void removeParentEnergyExchangeSystemNode(
      EnergyExchangeSystemType energyExchangeSystemType, 
      EnergyExchangeEntity parentEnergyExchangeEntity,
      boolean addInverseChildRelationship)
  throws 
      EntityDoesNotExistException;
  
  /**
   * 
   * @param energyExchangeSystemType
   * @param parentEnergyExchangeSystemNodes
   * 
   * @throws EntityAlreadyExistsException If a parent already exists
   * @throws EntityDoesNotExistException If a parent does not exist
   */
  void setParentEnergyExchangeSystemNodes(
      EnergyExchangeSystemType energyExchangeSystemType, 
      List<EnergyExchangeEntity> parentEnergyExchangeSystemNodes)
  throws
      EntityAlreadyExistsException,
      EntityDoesNotExistException;
  
  /**
   * 
   * @param energyExchangeSystemType
   * @param childEnergyExchangeSystemNodes
   * 
   * @throws EntityAlreadyExistsException If a child already exists
   * @throws EntityDoesNotExistException If a child does not exist
   */
  void setChildEnergyExchangeSystemNodes(
      EnergyExchangeSystemType energyExchangeSystemType, 
      List<EnergyExchangeEntity> childEnergyExchangeSystemNodes)
  throws
      EntityAlreadyExistsException,
      EntityDoesNotExistException;
  
  /**
   * 
   * @return The list of added energy exchange system edge DTOs
   */
  List<EnergyExchangeSystemEdgeDto> getAddedEnergyExchangeSystemEdges();
  
  /**
   * 
   * @return The list of removed energy exchange system edge DTOs
   */
  List<EnergyExchangeSystemEdgeDto> getRemovedEnergyExchangeSystemEdges();
  
  /**
   * 
   * @param adFunctionErrorMessages The AD function error messages to add
   */
  void addAdFunctionErrorMessages(AdFunctionErrorMessagesEntity adFunctionErrorMessages);
  
  /**
   * 
   * @param adFunctionTemplate The AD function template to remove the error messages for
   */
  void removeAdFunctionErrorMessages(AbstractAdFunctionTemplateEntity adFunctionTemplate);

  /**
   * 
   * @param list The list to add removed AD function error messages to (less instantiation this way)
   */
  void getRemovedAdFunctionErrorMessages(List<AdFunctionErrorMessagesEntity> list);
  
  /**
   * 
   * @param list The list to add AD function error messages to (less instantiation this way)
   */
  void getAdFunctionErrorMessages(List<AdFunctionErrorMessagesEntity> list);
  
  /**
   * 
   * @param list The list to add added AD function error messages to (less instantiation this way)
   */
  void getAddedAdFunctionErrorMessages(List<AdFunctionErrorMessagesEntity> list);
  
  /**
   * 
   * @param unitSystem The unit system to change to.  Will migrate any constants for all child
   * AD function instances.
   * @param overridePointTemplate  Optional point template to match on (should only be set in the context of adding an override)
   */
  void setUnitSystem(UnitSystem unitSystem, PointTemplateEntity overridePointTemplate);
}
//@formatter:on