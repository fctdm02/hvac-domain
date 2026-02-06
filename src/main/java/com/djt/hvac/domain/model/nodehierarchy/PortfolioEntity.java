//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy;

import static java.util.Objects.requireNonNull;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.RemediationStrategy;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.customer.AbstractCustomerEntity;
import com.djt.hvac.domain.model.customer.OnlineCustomerEntity;
import com.djt.hvac.domain.model.customer.enums.CustomerPaymentStatus;
import com.djt.hvac.domain.model.customer.enums.CustomerStatus;
import com.djt.hvac.domain.model.dictionary.TagEntity;
import com.djt.hvac.domain.model.dictionary.UnitEntity;
import com.djt.hvac.domain.model.dictionary.container.DictionaryContext;
import com.djt.hvac.domain.model.dictionary.energyexchange.AbstractEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.enums.DataType;
import com.djt.hvac.domain.model.dictionary.enums.FunctionType;
import com.djt.hvac.domain.model.dictionary.enums.NodeSubType;
import com.djt.hvac.domain.model.dictionary.enums.NodeType;
import com.djt.hvac.domain.model.dictionary.enums.UnitSystem;
import com.djt.hvac.domain.model.dictionary.template.nodetag.AbstractNodeTagTemplateEntity;
import com.djt.hvac.domain.model.function.AbstractAdFunctionInstanceEntity;
import com.djt.hvac.domain.model.function.AdFunctionErrorMessagesEntity;
import com.djt.hvac.domain.model.function.AdFunctionRemediationStrategyFinder;
import com.djt.hvac.domain.model.function.dto.AdFunctionErrorMessagesDto;
import com.djt.hvac.domain.model.function.dto.AdFunctionInstanceDto;
import com.djt.hvac.domain.model.nodehierarchy.building.BillableBuildingEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BuildingEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BuildingSubscriptionEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.BuildingPaymentStatus;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.BuildingPaymentType;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.BuildingStatus;
import com.djt.hvac.domain.model.nodehierarchy.dto.AsyncComputedPointNodeDto;
import com.djt.hvac.domain.model.nodehierarchy.dto.CustomAsyncComputedPointNodeDto;
import com.djt.hvac.domain.model.nodehierarchy.dto.EnergyExchangeSystemEdgeDto;
import com.djt.hvac.domain.model.nodehierarchy.dto.MappablePointNodeDto;
import com.djt.hvac.domain.model.nodehierarchy.dto.NodeTagDto;
import com.djt.hvac.domain.model.nodehierarchy.dto.NonPointNodeDto;
import com.djt.hvac.domain.model.nodehierarchy.dto.ScheduledAsyncComputedPointNodeDto;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.EnergyExchangeEntity;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.EquipmentEntity;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.LoopEntity;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.PlantEntity;
import com.djt.hvac.domain.model.nodehierarchy.mapper.PortfolioDtoMapper;
import com.djt.hvac.domain.model.nodehierarchy.point.AbstractPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.AsyncComputedPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.ScheduledAsyncComputedPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.WeatherAsyncComputedPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.custom.CustomAsyncComputedPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.custom.CustomPointFormulaVariableEligiblePoint;
import com.djt.hvac.domain.model.nodehierarchy.point.mapped.MappablePointEntity;
import com.djt.hvac.domain.model.rawpoint.RawPointEntity;
import com.djt.hvac.domain.model.rawpoint.dto.RawPointDto;
import com.djt.hvac.domain.model.report.ReportInstanceEntity;
import com.djt.hvac.domain.model.report.dto.ReportInstanceDto;
import com.djt.hvac.domain.model.report.dto.ReportInstanceStatusDto;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

public class PortfolioEntity extends AbstractNodeEntity {
  private static final long serialVersionUID = 1L;
  
  private static final Logger LOGGER = LoggerFactory.getLogger(PortfolioEntity.class);

  /**
   * The value used to represent NULL in the update request DTOs (i.e. -1)
   */
  private static final int NULL = -1;

  /**
   * The value used to represent ANY in the update request DTOs (i.e. -2)
   */
  private static final int ANY = -2;
  
  // The equivalent of a database index, facilitates O(1) retrieval of 
  // any child node by its persistent identity.
  private Map<Integer, AbstractNodeEntity> nodeMap;

  
  
  // These are used to facilitate point mapping.  We need a way
  // to efficiently retrieve newly created nodes that cannot be 
  // added to the 'nodeMap' index until AFTER they have been persisted,
  // as the nodeMap stores nodes by their persistent identity.
  private List<AbstractNodeEntity> createdNodes = new ArrayList<>();
  
  private AbstractCustomerEntity parentCustomer;
  private Set<BuildingEntity> childBuildings = new TreeSet<>();

  private transient Map<String, MappablePointEntity> _mappablePointsByRawPointMetricIdMap = new HashMap<>();

  private transient List<ReportInstanceEntity> _newlyCreatedReportInstances = new ArrayList<>();
  private transient List<AbstractAdFunctionInstanceEntity> _newlyCreatedAdFunctionInstances = new ArrayList<>();

  private transient Set<Integer> _newlyDisabledReportInstanceIds = new HashSet<>();
  private transient Set<Integer> _newlyDisabledAdFunctionInstanceIds = new HashSet<>();

  private transient Map<String, EnergyExchangeEntity> _energyExchangeSystemNodesByNaturalIdentityMap = null;
  
  
  
  private final Set<AbstractAdFunctionInstanceEntity> invalidAdFunctionInstanceCandidates = new HashSet<>();
  
  public void addInvalidAdFunctionInstanceCandidate(AbstractAdFunctionInstanceEntity entity) {

    entity.setIsDeleted();
    invalidAdFunctionInstanceCandidates.add(entity);
  }
  
  public Set<AbstractAdFunctionInstanceEntity> getInvalidAdFunctionInstanceCandidates() {
    
    return invalidAdFunctionInstanceCandidates;
  }
  
  public void resetInvalidAdFunctionInstanceCandidates() {
   
    invalidAdFunctionInstanceCandidates.clear();
  }
  
  int numBuildingsProcessed;
  int numSubBuildingsProcessed;
  int numPlantsProcessed;
  int numLoopsProcessed;
  int numFloorsProcessed;
  int numEquipmentProcessed;
  int numMappablePointsProcessed;
  int numCustomAsyncComputedPointsProcessed;
  int numScheduledAsyncComputedPointsProcessed;
  int numAsyncComputedPointsProcessed;
  
  int numAdFunctionInstanceCandidatesProcessed;
  public void incrementNumAdFunctionInstanceCandidatesProcessed() {
    numAdFunctionInstanceCandidatesProcessed++;
  }
  
  int numAdFunctionInstancesProcessed;
  public void incrementNumAdFunctionInstancesProcessed() {
    numAdFunctionInstancesProcessed++;
  }

  int numAdFunctionErrorMessagesProcessed;
  public void incrementNumAdFunctionErrorMessagesProcessed() {
    numAdFunctionErrorMessagesProcessed++;
  }
  
  int numReportInstancesProcessed;
  public void incrementNumReportInstancesProcessed() {
    numReportInstancesProcessed++;
  }
  
  public void setNumReportInstancesProcessed(int size) {
    numReportInstancesProcessed = size; 
  }
  
  // Used to disable node modification tracking during the load portfolio process
  public boolean isBeingMapped = false;
  
  public int numEnergyExchangeSystemParents;
  
  public PortfolioEntity(
      AbstractCustomerEntity parentCustomer,
      String name,
      String displayName) {
    super(
        null,
        name,
        displayName);
    requireNonNull(parentCustomer, "parentCustomer cannot be null");
    this.parentCustomer = parentCustomer;
    this.nodeMap = new HashMap<>();
  }
  
  public PortfolioEntity() {}

  public PortfolioEntity(
      Integer persistentIdentity,
      AbstractCustomerEntity parentCustomer,
      String name,
      String displayName,
      String uuid,
      String createdAt,
      String updatedAt,
      int nodeHierarchySize) {
    super(
        persistentIdentity,
        null,
        name,
        displayName,
        uuid,
        createdAt,
        updatedAt,
        null);
    requireNonNull(parentCustomer, "parentCustomer cannot be null");
    this.parentCustomer = parentCustomer;
    this.nodeMap = Maps.newHashMapWithExpectedSize(nodeHierarchySize);
  }

  @Override
  public Timestamp setUpdatedAt() {
    return super.setUpdatedAt();
  }
  
  @Override
  public NodeType getNodeType() {
    return NodeType.PORTFOLIO;
  }

  @Override
  public NodeSubType getNodeSubType() {
    return NodeSubType.NONE;
  }

  // Used for O(1) retrieval of any node in the node hierarchy.
  private void addChildNodeToIndex(AbstractNodeEntity node) {
    
    // Set the modified flag on every parent up to the root portfolio
    // node to be modified for the 'updatedAt' field. (This is to
    // support stale data checking for mutation operations)
    // Also, reset any transient attributes for all affected nodes.
    AbstractNodeEntity parentNode = node.getParentNode();
    while (parentNode != null) {
      parentNode.resetTransientAttributes();
      parentNode.setIsModified("added child");
      parentNode = parentNode.getParentNode();
    }

    if (node.getPersistentIdentity() != null) {
      nodeMap.put(node.getPersistentIdentity(), node);
    } else {
      // When the portfolio is stored, these nodes
      // will have their persistent identities set, and
      // then added to the index above, which is keyed 
      // by persistent identity.
      createdNodes.add(node);
    }
  }

  // It is assumed that the nodes have been persisted and thus, have had their persistent identities created.
  public void addCreatedNodesToNodeIndex(Collection<AbstractNodeEntity> nodes) {
    
    List<AbstractNodeEntity> list = new ArrayList<>();
    list.addAll(nodes);
    int size = list.size();
    for (int i=0; i < size; i++) {
      
      AbstractNodeEntity node = list.get(i);
      addChildNodeToIndex(node);
    }
  }

  public void addNodeToParentAndIndex(
      BuildingEntity child) 
  throws EntityAlreadyExistsException {
    
    addChildNodeToIndex(child);
    numBuildingsProcessed++;
    addChildBuilding(child);
  }
  
  public void addNodeToParentAndIndex(
      AbstractNodeEntity parent,
      AbstractNodeEntity child) 
  throws EntityAlreadyExistsException {
    
    if (parent instanceof BuildingEntity && child instanceof SubBuildingEntity) {
      
      addNodeToParentAndIndex((BuildingEntity)parent, (SubBuildingEntity)child);
      
    } else if (parent instanceof BuildingEntity && child instanceof PlantEntity) {
      
      addNodeToParentAndIndex((BuildingEntity)parent, (PlantEntity)child);
      
    } else if (parent instanceof BuildingEntity && child instanceof FloorEntity) {
      
      addNodeToParentAndIndex((BuildingEntity)parent, (FloorEntity)child);
      
    } else if (parent instanceof BuildingEntity && child instanceof EquipmentEntity) {
      
      addNodeToParentAndIndex((BuildingEntity)parent, (EquipmentEntity)child);
      
    } else if (parent instanceof SubBuildingEntity && child instanceof PlantEntity) {
      
      addNodeToParentAndIndex((SubBuildingEntity)parent, (PlantEntity)child);
      
    } else if (parent instanceof SubBuildingEntity && child instanceof FloorEntity) {
      
      addNodeToParentAndIndex((SubBuildingEntity)parent, (FloorEntity)child);
      
    } else if (parent instanceof SubBuildingEntity && child instanceof EquipmentEntity) {
      
      addNodeToParentAndIndex((SubBuildingEntity)parent, (EquipmentEntity)child);
      
    } else if (parent instanceof PlantEntity && child instanceof LoopEntity) {
      
      addNodeToParentAndIndex((PlantEntity)parent, (LoopEntity)child);
      
    } else if (parent instanceof PlantEntity && child instanceof FloorEntity) {
      
      addNodeToParentAndIndex((PlantEntity)parent, (FloorEntity)child);
      
    } else if (parent instanceof PlantEntity && child instanceof EquipmentEntity) {
      
      addNodeToParentAndIndex((PlantEntity)parent, (EquipmentEntity)child);
      
    } else if (parent instanceof FloorEntity && child instanceof EquipmentEntity) {
      
      addNodeToParentAndIndex((FloorEntity)parent, (EquipmentEntity)child);
      
    } else if (parent instanceof EquipmentEntity && child instanceof EquipmentEntity) {
      
      addNodeToParentAndIndex((EquipmentEntity)parent, (EquipmentEntity)child);
      
    } else {

      throw new IllegalStateException("Illegal combination of parent: ["
          + parent.getClassAndNaturalIdentity()
          + "] and child: "
          + child.getClassAndNaturalIdentity()
          + "]");
    }
  }  
  
  public void addNodeToParentAndIndex(
      BuildingEntity parent, 
      SubBuildingEntity child) 
  throws EntityAlreadyExistsException {
    
    addChildNodeToIndex(child);
    numSubBuildingsProcessed++;
    parent.addChildSubBuilding(child);
  }

  public void addNodeToParentAndIndex(
      BuildingEntity parent, 
      FloorEntity child) 
  throws EntityAlreadyExistsException {
    
    addChildNodeToIndex(child);
    numFloorsProcessed++;
    parent.addChildFloor(child);
  }

  public void addNodeToParentAndIndex(
      SubBuildingEntity parent, 
      FloorEntity child) 
  throws EntityAlreadyExistsException {
    
    addChildNodeToIndex(child);
    numFloorsProcessed++;
    parent.addChildFloor(child);
  }
  
  public void addNodeToParentAndIndex(
      BuildingEntity parent, 
      PlantEntity child) 
  throws EntityAlreadyExistsException {
    
    addChildNodeToIndex(child);
    numPlantsProcessed++;
    parent.addChildPlant(child);
  }

  public void addNodeToParentAndIndex(
      SubBuildingEntity parent, 
      PlantEntity child) 
  throws EntityAlreadyExistsException {
    
    addChildNodeToIndex(child);
    numPlantsProcessed++;
    parent.addChildPlant(child);
  }  
  
  public void addNodeToParentAndIndex(
      PlantEntity parent, 
      LoopEntity child) 
  throws EntityAlreadyExistsException {
    
    addChildNodeToIndex(child);
    numLoopsProcessed++;
    parent.addChildLoop(child);
  }

  public void addNodeToParentAndIndex(
      BuildingEntity parent, 
      EquipmentEntity child) 
  throws EntityAlreadyExistsException {
    
    addChildNodeToIndex(child);
    numEquipmentProcessed++;
    parent.addChildEquipment(child);
  }

  public void addNodeToParentAndIndex(
      SubBuildingEntity parent, 
      EquipmentEntity child) 
  throws EntityAlreadyExistsException {
    
    addChildNodeToIndex(child);
    numEquipmentProcessed++;
    parent.addChildEquipment(child);
  }

  public void addNodeToParentAndIndex(
      FloorEntity parent, 
      EquipmentEntity child) 
  throws EntityAlreadyExistsException {
    
    addChildNodeToIndex(child);
    numEquipmentProcessed++;
    parent.addChildEquipment(child);
  }

  public void addNodeToParentAndIndex(
      EquipmentEntity parent, 
      EquipmentEntity child) 
  throws EntityAlreadyExistsException {
    
    addChildNodeToIndex(child);
    numEquipmentProcessed++;
    parent.addNodeHierarchyChildEquipment(child);
  }

  public void addNodeToParentAndIndex(
      AbstractNodeEntity parent,
      MappablePointEntity child) 
  throws EntityAlreadyExistsException {
    
    if (parent instanceof AbstractPointEntity) {
      throw new IllegalStateException("Cannot add point: ["
          + child.getName()
          + "] to a parent that is also a point: "
          + parent.getNodePath()
          + "]");
    }
    
    addChildNodeToIndex(child);
    numMappablePointsProcessed++;
    parent.addChildPoint(child);
  }

  public void addNodeToParentAndIndex(
      AbstractNodeEntity parent, 
      AsyncComputedPointEntity child) 
  throws EntityAlreadyExistsException {
    
    if (parent instanceof AbstractPointEntity) {
      throw new IllegalStateException("Cannot add point: ["
          + child.getName()
          + "] to a parent that is also a point: "
          + parent.getNodePath()
          + "]");
    }
    
    addChildNodeToIndex(child);
    numAsyncComputedPointsProcessed++;
    parent.addChildPoint(child);
  }

  public void addNodeToParentAndIndex(
      AbstractNodeEntity parent, 
      CustomAsyncComputedPointEntity child) 
  throws EntityAlreadyExistsException {
    
    if (parent instanceof AbstractPointEntity) {
      throw new IllegalStateException("Cannot add point: ["
          + child.getName()
          + "] to a parent that is also a point: "
          + parent.getNodePath()
          + "]");
    }
    
    addChildNodeToIndex(child);
    numCustomAsyncComputedPointsProcessed++;
    parent.addChildPoint(child);
  }
  
  public void addNodeToParentAndIndex(
      BuildingEntity parent, 
      ScheduledAsyncComputedPointEntity child) 
  throws EntityAlreadyExistsException {
    
    addChildNodeToIndex(child);
    numScheduledAsyncComputedPointsProcessed++;
    parent.addChildPoint(child);
  }
  
  public AbstractNodeEntity getChildNodeNullIfNotExists(Integer persistentIdentity) {
    return nodeMap.get(persistentIdentity);
  }

  public Optional<AbstractNodeEntity> getChildNodeEmptyIfINotExists(Integer persistentIdentity) {
    return Optional.ofNullable(nodeMap.get(persistentIdentity));
  }

  public AbstractNodeEntity getChildNode(Integer persistentIdentity)
      throws EntityDoesNotExistException {

    AbstractNodeEntity childNode = getChildNodeNullIfNotExists(persistentIdentity);
    if (childNode == null) {
      if (this.getPersistentIdentity().equals(persistentIdentity)) {
        return this;
      }
      throw new EntityDoesNotExistException(
          " child node with persistent identity: ["
              + persistentIdentity
              + "] not found in "
              + this.getClassAndNaturalIdentity());
    }
    return childNode;
  }
  
  public AbstractCustomerEntity getParentCustomer() {
    return parentCustomer;
  }

  @Override
  public Integer getCustomerId() {
    return parentCustomer.getPersistentIdentity();
  }

  public String getCustomerUuid() {
    return parentCustomer.getUuid();
  }
  
  public BuildingEntity addChildBuilding(
      String name, 
      String displayName) 
  throws 
      EntityAlreadyExistsException {
    
    UnitSystem unitSystem = parentCustomer.getUnitSystem();
    BuildingEntity building = null;
    if (parentCustomer instanceof OnlineCustomerEntity) {
      building = new BillableBuildingEntity(
          this,
          name,
          displayName,
          unitSystem);
    } else {
      building = new BuildingEntity(
          this,
          name,
          displayName,
          BuildingPaymentType.OUT_OF_BAND,
          unitSystem);
    }
    addNodeToParentAndIndex(building);
    return building;
  }
  
  public MappablePointEntity addChildMappablePoint(
      AbstractNodeEntity parentNode,
      String name,
      String displayName,
      RawPointEntity rawPoint) 
  throws 
      EntityAlreadyExistsException {
    
    // Ensure that we have a unique name
    String uniqueName = name;
    AbstractPointEntity check = parentNode.getChildPointByNameNullIfNotExists(name);
    if (check != null) {
      
      int sequenceNumber = 0;
      while (check != null && sequenceNumber < 10000) {

        sequenceNumber++;
        uniqueName = name + "_" + Integer.toString(sequenceNumber);
        check = parentNode.getChildPointByNameNullIfNotExists(uniqueName);
      }
      if (check != null) {
        throw new IllegalStateException("Unable to find unique sequence number: ["
            + sequenceNumber 
            + "] for point with name: [" 
            + name 
            + "] under parent node: ["
            + parentNode.getNodePath()
            + "]");
      }
    }
    
    DataType dataType = DataType.getDataTypeFromNiagaraPointType(rawPoint.getPointType());
    
    UnitEntity unit = DictionaryContext.getUnitsContainer().getUnitFromNiagaraUnitType(rawPoint.getUnitType());
    
    String range = MappablePointEntity.getDefaultRange(rawPoint, dataType);
    
    MappablePointEntity mappablePoint = new MappablePointEntity(
        parentNode,
        uniqueName,
        displayName,
        dataType,
        unit,
        range,
        rawPoint);
    addNodeToParentAndIndex(parentNode, mappablePoint);    
    return mappablePoint;
  }  

  public boolean addChildBuilding(BuildingEntity building) throws EntityAlreadyExistsException {
    return addChild(childBuildings, building, this);
  }

  public Set<BuildingEntity> getChildBuildings() {
    return childBuildings;
  }

  public BuildingEntity getChildBuilding(Integer persistentIdentity)
      throws EntityDoesNotExistException {
    return (BuildingEntity) getRootPortfolioNode().getChildNode(persistentIdentity);
  }

  public BuildingEntity getChildBuildingByName(String name) 
      throws EntityDoesNotExistException {
    
    for (BuildingEntity building: childBuildings) {
      if (building.getName().equals(name)) {
        return building;
      }
    } 
    throw new EntityDoesNotExistException(
        "Building with name: ["
            + name
            + "] not found in ["
            + this.getNaturalIdentity()
            + "].");    
  }
  
  public BuildingEntity getChildBuildingByNameNullIfNotExists(String name) {
    
    for (BuildingEntity building: childBuildings) {
      if (building.getName().equals(name)) {
        return building;
      }
    } 
    return null;
  }
  
  public SubBuildingEntity getChildSubBuildingByNameNullIfNotExists(String name) {
    
    for (AbstractNodeEntity node: getDescendantNodes()) {
      if (node.getName().equals(name) && node instanceof SubBuildingEntity) {
        return (SubBuildingEntity)node;
      }
    }
    return null;    
  }  
  
  public PlantEntity getChildPlantByNameNullIfNotExists(String name) {
    
    for (AbstractNodeEntity node: getDescendantNodes()) {
      if (node.getName().equals(name) && node instanceof PlantEntity) {
        return (PlantEntity)node;
      }
    }
    return null;    
  } 
  
  public FloorEntity getChildFloorByNameNullIfNotExists(String name) {
    
    for (AbstractNodeEntity node: getDescendantNodes()) {
      if (node.getName().equals(name) && node instanceof FloorEntity) {
        return (FloorEntity)node;
      }
    }
    return null;    
  } 

  public EquipmentEntity getChildEquipmentByNameNullIfNotExists(String name) {
    
    for (AbstractNodeEntity node: getDescendantNodes()) {
      if (node.getName().equals(name) && node instanceof EquipmentEntity) {
        return (EquipmentEntity)node;
      }
    }
    return null;    
  }
  
  // a.k.a. "hard delete"
  public BuildingEntity removeChildBuilding(Integer persistentIdentity) throws EntityDoesNotExistException {
    
    BuildingEntity building = getChildBuilding(persistentIdentity);
    
    // This method will throw exceptions if the building is not deletable (for whatever reason).
    building.validateDeletable();
    
    BillableBuildingEntity billableBuilding = (BillableBuildingEntity)building;
    if (building instanceof BillableBuildingEntity) {
      
      BuildingStatus buildingStatus = billableBuilding.getBuildingStatus();
      if (buildingStatus.equals(BuildingStatus.CREATED) 
          || buildingStatus.equals(BuildingStatus.PENDING_ACTIVATION)) {

        LOGGER.info(
            building.getPersistentIdentity() 
            + ": non-active billable building in the: ["
            + buildingStatus
            + "] state is being hard deleted.");
        
      } else {

        // The subscription will not be empty, as we just validated its existence above.
        BuildingSubscriptionEntity buildingSubscription = billableBuilding.getChildBuildingSubscriptionNullIfNotExists();
        LOGGER.info(
            building.getPersistentIdentity() 
            + ": billable building has a canceled subscription whose payment interval expired on: ["
            + buildingSubscription.getCurrentIntervalEndsAt()
            + "] where current tine is ["
            + AbstractEntity.getTimeKeeper().getCurrentTimestamp()
            + "], performing hard delete.");
        
      }
    } else {
      
      LOGGER.info(
          building.getPersistentIdentity() 
          + ": non-billable building is being hard deleted.");
      
    }
    
    building.setIsDeleted();
    return building;
  }
  
  public Optional<BuildingEntity> getChildBuildingEmptyIfINotExists(Integer persistentIdentity)
      throws EntityDoesNotExistException {
    Optional<AbstractNodeEntity> nodeOptional = getChildNodeEmptyIfINotExists(persistentIdentity);
    if (nodeOptional.isPresent()) {
      return Optional.of((BuildingEntity) nodeOptional.get());
    }
    return Optional.empty();
  }
  
  public List<BuildingSubscriptionEntity> getAllDescendantBuildingSubscriptions() {
    
    List<BuildingSubscriptionEntity> buildingSubscriptions = new ArrayList<>();
    for (BuildingEntity childBuilding: childBuildings) {
      if (childBuilding instanceof BillableBuildingEntity) {
        
        BillableBuildingEntity bb = (BillableBuildingEntity)childBuilding;
        if (bb.getBuildingStatus().equals(BuildingStatus.ACTIVE)) {
          
          BuildingSubscriptionEntity buildingSubscription = bb.getChildBuildingSubscriptionNullIfNotExists();
          
          if (buildingSubscription != null) {
            buildingSubscriptions.add(buildingSubscription);  
          }
        }
      } else {
        throw new IllegalStateException("Online customer: ["
            + this
            + "] must have child billable buildings only, but a non billable building: ["
            + childBuilding
            + "] was found.");
      }
    }
    return buildingSubscriptions;
  }  
  
  public Set<AbstractNodeEntity> getAllDescendantNodesWithTag(AbstractNodeEntity ancestorNode, TagEntity tag) {
    
    Set<AbstractNodeEntity> nodes = new HashSet<>();
    Iterator<AbstractNodeEntity> nodeIterator = nodeMap.values().iterator();
    while (nodeIterator.hasNext()) {

      AbstractNodeEntity node = nodeIterator.next();
      if (ancestorNode.isAncestor(node) && node.getNodeTags().contains(tag)) {
        
        nodes.add(node);
      }
    }
    return nodes;
  }

  @SuppressWarnings("unchecked")
  @Override
  protected <T extends AbstractNodeEntity> Set<T> getChildSet(AbstractNodeEntity t) {
    
    if (t instanceof AbstractPointEntity) {
      return (Set<T>)this.getChildPoints();
      
    } else if (t instanceof BuildingEntity) {
      return (Set<T>)this.childBuildings;
      
    }
    throw new UnsupportedOperationException("Portfolio: ["
        + getNodePath()
        + "] does not support having child: ["
        + t.getClassAndNaturalIdentity()
        + "]");
  }
  
  public int getNumBuildingsProcessed() {
    return numBuildingsProcessed;
  }

  public int getNumSubBuildingsProcessed() {
    return numSubBuildingsProcessed;
  }

  public int getNumPlantsProcessed() {
    return numPlantsProcessed;
  }

  public int getNumLoopsProcessed() {
    return numLoopsProcessed;
  }
  
  public int getNumFloorsProcessed() {
    return numFloorsProcessed;
  }

  public int getNumEquipmentProcessed() {
    return numEquipmentProcessed;
  }

  public int getNumMappablePointsProcessed() {
    return numMappablePointsProcessed;
  }

  public int getNumCustomAsyncComputedPointsProcessed() {
    return numCustomAsyncComputedPointsProcessed;
  }
  
  public int getNumScheduledAsyncComputedPointsProcessed() {
    return numScheduledAsyncComputedPointsProcessed;
  }

  public int getNumAsyncComputedPointsProcessed() {
    return numAsyncComputedPointsProcessed;
  }

  public int getNumReportInstancesProcessed() {
    return numReportInstancesProcessed;
  }

  public int getNumAdFunctionInstanceCandidatesProcessed() {
    return numAdFunctionInstanceCandidatesProcessed;
  }

  public int getNumAdFunctionInstancesProcessed() {
    return numAdFunctionInstancesProcessed;
  }

  public int getNumAdFunctionErrorMessagesProcessed() {
    return numAdFunctionErrorMessagesProcessed;
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public void mapToDtos(Map<String, Object> dtos) {

    PortfolioDtoMapper.mapNonPointNodeDto(this, dtos);
    
    if (getParentCustomer().loadAdFunctionInstances) {
      
      List<AdFunctionErrorMessagesDto> adFunctionErrorMessagesDtoList = (List<AdFunctionErrorMessagesDto>) dtos.get(PortfolioDtoMapper.AD_FUNCTION_ERROR_MESSAGES_DTO_LIST);
      for (AdFunctionErrorMessagesEntity entity: getAdFunctionErrorMessages()) {

        adFunctionErrorMessagesDtoList.add(AdFunctionErrorMessagesEntity
            .Mapper
            .getInstance()
            .mapEntityToDto(entity));
      }
    }

    Iterator<AbstractPointEntity> pointIterator = getChildPoints().iterator();
    while (pointIterator.hasNext()) {
      AbstractPointEntity point = pointIterator.next();
      if (!point.getIsDeleted()) {
        point.mapToDtos(dtos);  
      }
    }

    Iterator<BuildingEntity> buildingIterator = childBuildings.iterator();
    while (buildingIterator.hasNext()) {
      BuildingEntity building = buildingIterator.next();
      if (!building.getIsDeleted()) {
        building.mapToDtos(dtos);  
      }
    }
  }

  public List<ReportInstanceEntity> getAllReportInstances() {

    List<ReportInstanceEntity> list = new ArrayList<>();
    for (AbstractNodeEntity node: nodeMap.values()) {

      if (node instanceof BuildingEntity) {

        BuildingEntity building = (BuildingEntity) node;
        list.addAll(building.getReportInstances());
      }
    }
    return list;
  }

  public List<ReportInstanceEntity> getAllEnabledReportInstances() {

    List<ReportInstanceEntity> list = new ArrayList<>();
    for (AbstractNodeEntity node: nodeMap.values()) {

      if (node instanceof BuildingEntity) {

        BuildingEntity building = (BuildingEntity) node;
        list.addAll(building.getEnabledReportInstances());
      }
    }
    return list;
  }
  
  public List<ReportInstanceEntity> getAllModifiedReportInstances() {

    List<ReportInstanceEntity> list = new ArrayList<>();
    for (AbstractNodeEntity node: nodeMap.values()) {

      if (node instanceof BuildingEntity) {

        BuildingEntity building = (BuildingEntity) node;
        for (ReportInstanceEntity reportInstance: building.getReportInstances()) {
          
          if (reportInstance.getIsModified()) {
            list.add(reportInstance); 
          }
        }
      }
    }
    return list;
  }
  
  public ReportInstanceEntity getReportInstance(Integer persistentIdentity) throws EntityDoesNotExistException {
    
    ReportInstanceEntity ri = this.getReportInstanceNullIfNotExists(persistentIdentity);
    if (ri == null) {

      throw new EntityDoesNotExistException("Report instance with persistentIdentity: ["
          + persistentIdentity
          + "] does not exist in buildings: " 
          + childBuildings
          + "");
    }
    return ri;
  }
  
  public ReportInstanceEntity getReportInstanceNullIfNotExists(Integer persistentIdentity) {

    if (persistentIdentity == null) {
      throw new IllegalArgumentException("persistentIdentity must have a value that corresponds to some non-null/non-empty surrogate key value in the repository");
    }
    
    for (BuildingEntity building: childBuildings) {
      
      ReportInstanceEntity ri = building.getReportInstanceNullIfNotExists(persistentIdentity);
      if (ri != null) {
        
        return ri;
      }
    }
    return null;
  }   
  
  public void removeAllReportInstances() {

    for (BuildingEntity building: childBuildings) {
      
      building.removeAllReportInstances();
    }
  }
  
  public void removeAllAdFunctionInstanceCandidates() {

    Iterator<AbstractNodeEntity> nodeIterator = nodeMap.values().iterator();
    while (nodeIterator.hasNext()) {

      AbstractNodeEntity node = nodeIterator.next();
      if (node instanceof EnergyExchangeEntity) {

        EnergyExchangeEntity equipment = (EnergyExchangeEntity) node;
        equipment.removeAllAdFunctionInstanceCandidates();
      }
    }
  }

  public void removeAllAdFunctionInstances() {

    Iterator<AbstractNodeEntity> nodeIterator = nodeMap.values().iterator();
    while (nodeIterator.hasNext()) {

      AbstractNodeEntity node = nodeIterator.next();
      if (node instanceof EnergyExchangeEntity) {

        EnergyExchangeEntity equipment = (EnergyExchangeEntity) node;
        equipment.removeAllAdFunctionInstances();
      }
    }
  }

  public void removeAllAdFunctions() {

    Iterator<AbstractNodeEntity> nodeIterator = nodeMap.values().iterator();
    while (nodeIterator.hasNext()) {

      AbstractNodeEntity node = nodeIterator.next();
      if (node instanceof EnergyExchangeEntity) {

        EnergyExchangeEntity equipment = (EnergyExchangeEntity) node;
        equipment.removeAllAdFunctionInstanceCandidates();
        equipment.removeAllAdFunctionInstances();
      }
    }
  }

  public List<AbstractAdFunctionInstanceEntity> getAllAdFunctionInstanceCandidates() {

    List<AbstractAdFunctionInstanceEntity> set = new ArrayList<>();
    Iterator<AbstractNodeEntity> nodeIterator = nodeMap.values().iterator();
    while (nodeIterator.hasNext()) {

      AbstractNodeEntity node = nodeIterator.next();
      if (node instanceof EnergyExchangeEntity) {

        EnergyExchangeEntity equipment = (EnergyExchangeEntity) node;
        set.addAll(equipment.getAdFunctionInstanceCandidates());
      }
    }
    return set;
  }

  public List<AbstractAdFunctionInstanceEntity> getAllAdFunctionInstanceCandidates(FunctionType functionType) {

    List<AbstractAdFunctionInstanceEntity> set = new ArrayList<>();
    Iterator<AbstractNodeEntity> nodeIterator = nodeMap.values().iterator();
    while (nodeIterator.hasNext()) {

      AbstractNodeEntity node = nodeIterator.next();
      if (node instanceof EnergyExchangeEntity) {

        EnergyExchangeEntity equipment = (EnergyExchangeEntity) node;
        for (AbstractAdFunctionInstanceEntity adFunction: equipment.getAdFunctionInstanceCandidates()) {
          
          if (adFunction.getAdFunctionTemplate().getAdFunction().getFunctionType().equals(functionType)) {
            
            set.add(adFunction);
          }
        }
      }
    }
    return set;
  }
  
  public List<AbstractAdFunctionInstanceEntity> getAllAdFunctionInstances() {

    List<AbstractAdFunctionInstanceEntity> set = new ArrayList<>();
    Iterator<AbstractNodeEntity> nodeIterator = nodeMap.values().iterator();
    while (nodeIterator.hasNext()) {

      AbstractNodeEntity node = nodeIterator.next();
      if (node instanceof EnergyExchangeEntity) {

        EnergyExchangeEntity equipment = (EnergyExchangeEntity) node;
        set.addAll(equipment.getAdFunctionInstances());
      }
    }
    return set;
  }

  public AbstractAdFunctionInstanceEntity getAdFunctionInstanceNullIfNotExists(Integer persistentIdentity) {

    Iterator<AbstractNodeEntity> nodeIterator = nodeMap.values().iterator();
    while (nodeIterator.hasNext()) {

      AbstractNodeEntity node = nodeIterator.next();
      if (node instanceof EnergyExchangeEntity) {

        EnergyExchangeEntity equipment = (EnergyExchangeEntity) node;
        AbstractAdFunctionInstanceEntity instance = equipment.getAdFunctionInstanceByTemplateIdNullIfNotExists(persistentIdentity);
        if (instance != null) {
          return instance;
        }
      }
    }
    return null;
  }  
  public List<AbstractAdFunctionInstanceEntity> getAllAdFunctionInstances(FunctionType functionType) {

    List<AbstractAdFunctionInstanceEntity> set = new ArrayList<>();
    Iterator<AbstractNodeEntity> nodeIterator = nodeMap.values().iterator();
    while (nodeIterator.hasNext()) {

      AbstractNodeEntity node = nodeIterator.next();
      if (node instanceof EnergyExchangeEntity) {

        EnergyExchangeEntity equipment = (EnergyExchangeEntity) node;
        for (AbstractAdFunctionInstanceEntity adFunction: equipment.getAdFunctionInstances()) {
          
          if (adFunction.getAdFunctionTemplate().getAdFunction().getFunctionType().equals(functionType)) {
            
            set.add(adFunction);
          }
        }
      }
    }
    return set;
  }

  @Override
  protected void resetTransientAttributes() {
    
    super.resetTransientAttributes();
    
    /*
    private transient Map<String, MappablePointEntity> _mappablePointsByRawPointMetricIdMap = new HashMap<>();

    private transient List<ReportInstanceEntity> _newlyCreatedReportInstances = new ArrayList<>();
    private transient List<AbstractAdFunctionInstanceEntity> _newlyCreatedAdFunctionInstances = new ArrayList<>();

    private transient Set<Integer> _newlyDisabledReportInstanceIds = new HashSet<>();
    private transient Set<Integer> _newlyDisabledAdFunctionInstanceIds = new HashSet<>();
    */

    
    if (_mappablePointsByRawPointMetricIdMap == null) {
      _mappablePointsByRawPointMetricIdMap = new HashMap<>();
    } else {
      _mappablePointsByRawPointMetricIdMap.clear();
    }
    
    if (_newlyCreatedReportInstances == null) {
      _newlyCreatedReportInstances = new ArrayList<>();
    } else {
      _newlyCreatedReportInstances.clear(); 
    }
    
    if (_newlyCreatedAdFunctionInstances == null) {
      _newlyCreatedAdFunctionInstances = new ArrayList<>();
    } else {
      _newlyCreatedAdFunctionInstances.clear();
    }
    
    if (_newlyDisabledReportInstanceIds == null) {
      _newlyDisabledReportInstanceIds = new HashSet<>();
    } else {
      _newlyDisabledReportInstanceIds.clear();
    }

    if (_newlyDisabledAdFunctionInstanceIds == null) {
      _newlyDisabledAdFunctionInstanceIds = new HashSet<>();
    } else {
      _newlyDisabledAdFunctionInstanceIds.clear();
    }
    
    _energyExchangeSystemNodesByNaturalIdentityMap = null;
    
    parentCustomer.resetRawPointsByMetricIdMap();
  }

  public List<AbstractAdFunctionInstanceEntity> getNewlyCreatedAdFunctionInstances() {
    if (_newlyCreatedAdFunctionInstances == null) {
      _newlyCreatedAdFunctionInstances = new ArrayList<>();
    }
    return ImmutableList.copyOf(_newlyCreatedAdFunctionInstances);
  }
  public void addNewlyCreatedAdFunctionInstance(AbstractAdFunctionInstanceEntity adFunctionInstance) {
    if (!isBeingMapped) {
      if (_newlyCreatedAdFunctionInstances == null) {
        _newlyCreatedAdFunctionInstances = new ArrayList<>();
      }
      _newlyCreatedAdFunctionInstances.add(adFunctionInstance);  
    }
  }
  public void addNewlyCreatedAdFunctionInstances(Collection<AbstractAdFunctionInstanceEntity> adFunctionInstances) {
    if (!isBeingMapped) {
      if (_newlyCreatedAdFunctionInstances == null) {
        _newlyCreatedAdFunctionInstances = new ArrayList<>();
      }
      _newlyCreatedAdFunctionInstances.addAll(adFunctionInstances);  
    }
  }
  public void resetNewlyCreatedAdFunctionInstances() {
    if (!isBeingMapped) {
      if (_newlyCreatedAdFunctionInstances == null) {
        _newlyCreatedAdFunctionInstances = new ArrayList<>();
      } else {
        _newlyCreatedAdFunctionInstances.clear();  
      }
    }
  }
  
  public List<ReportInstanceEntity> getNewlyCreatedReportInstances() {
    if (_newlyCreatedReportInstances == null) {
      _newlyCreatedReportInstances = new ArrayList<>();
    }
    return ImmutableList.copyOf(_newlyCreatedReportInstances);
  }
  public void addNewlyCreatedReportInstance(ReportInstanceEntity reportInstance) {
    if (!isBeingMapped) {
      if (_newlyCreatedReportInstances == null) {
        _newlyCreatedReportInstances = new ArrayList<>();
      }
      _newlyCreatedReportInstances.add(reportInstance);  
    }
  }
  public void resetNewlyCreatedReportInstances() {
    if (!isBeingMapped) {
      if (_newlyCreatedReportInstances == null) {
        _newlyCreatedReportInstances = new ArrayList<>();
      } else {
        _newlyCreatedReportInstances.clear();  
      }
    }
  }

  public List<Integer> getNewlyDisabledAdFunctionInstanceIds() {
    if (_newlyDisabledAdFunctionInstanceIds == null) {
      _newlyDisabledAdFunctionInstanceIds = new HashSet<>();
    }
    List<Integer> list = ImmutableList.copyOf(_newlyDisabledAdFunctionInstanceIds);
    return list;
  }
  public void addNewlyDisabledAdFunctionInstanceId(Integer adFunctionInstanceId) {
    if (!isBeingMapped) {
      if (_newlyDisabledAdFunctionInstanceIds == null) {
        _newlyDisabledAdFunctionInstanceIds = new HashSet<>();
      }
      _newlyDisabledAdFunctionInstanceIds.add(adFunctionInstanceId);  
    }
  }
  public void addNewlyDisabledAdFunctionInstanceIds(Collection<Integer> adFunctionInstanceIds) {
    if (!isBeingMapped) {
      if (_newlyDisabledAdFunctionInstanceIds == null) {
        _newlyDisabledAdFunctionInstanceIds = new HashSet<>();
      }
      _newlyDisabledAdFunctionInstanceIds.addAll(adFunctionInstanceIds);  
    }
  }
  public void resetNewlyDisabledAdFunctionInstanceIds() {
    if (!isBeingMapped) {
      if (_newlyDisabledAdFunctionInstanceIds == null) {
        _newlyDisabledAdFunctionInstanceIds = new HashSet<>();
      } else {
        _newlyDisabledAdFunctionInstanceIds.clear();  
      }
    }
  }
  
  public List<Integer> getNewlyDisabledReportInstanceIds() {
    if (_newlyDisabledReportInstanceIds == null) {
      _newlyDisabledReportInstanceIds = new HashSet<>();
    }
    return ImmutableList.copyOf(_newlyDisabledReportInstanceIds);
  }
  public void addNewlyDisabledReportInstanceId(Integer reportInstanceId) {
    if (!isBeingMapped) {
      if (_newlyDisabledReportInstanceIds == null) {
        _newlyDisabledReportInstanceIds = new HashSet<>();
      }
      _newlyDisabledReportInstanceIds.add(reportInstanceId);  
    }
  }
  public void resetNewlyDisabledReportInstanceIds() {
    if (!isBeingMapped) {
      if (_newlyDisabledReportInstanceIds == null) {
        _newlyDisabledReportInstanceIds = new HashSet<>();
      } else {
        _newlyDisabledReportInstanceIds.clear();  
      }
    }
  }
  
  public Collection<AbstractNodeEntity> getAllNodes() {

    return nodeMap.values();
  }

  public List<AbstractNodeEntity> getAllModifiedNodes() {
    
    // See if any reports need to be rebuilt.
    for (BuildingEntity building: childBuildings) {
      for (ReportInstanceEntity reportInstance: building.getReportInstances()) {
        if (reportInstance.getNeedsRebuilding()) {
          
          reportInstance.setIsModified("needsRebuilding");
          reportInstance.setNeedsRebuilding(false);
        }
      }
    }
    
    // See if any AD function instances/candidates need to be processed.
    for (EquipmentEntity equipment: getAllEquipment()) {
      if (!equipment.getDeletedAdFunctionInstanceCandidates().isEmpty()) {
        equipment.setIsModified("deleted AD function instance candidates");
      } else if (!equipment.getDeletedAdFunctionInstances().isEmpty()) {
        equipment.setIsModified("deleted AD function instances");
        
      }
    }

    List<AbstractNodeEntity> modifiedNodes = new ArrayList<>();
    for (AbstractNodeEntity node: nodeMap.values()) {
      
      if (node.getIsModified()) {
        
        modifiedNodes.add(node);
      }
    }
    
    this.setUpdatedAt();    
    modifiedNodes.add(this);
    
    return modifiedNodes;
  }  
  
  public void resetAllIsModified() {
    
    for (AbstractNodeEntity node: nodeMap.values()) {
      if (node.getIsModified()) {
        
        node.setNotModified();
      }
    }
    setNotModified();
  }
  
  public Collection<AdFunctionErrorMessagesEntity> getAdFunctionErrorMessages() {
    
    List<AdFunctionErrorMessagesEntity> list = new ArrayList<>();
    for (EnergyExchangeEntity energyExchangeEntity: getAllEnergyExchangeSystemNodes()) {
      energyExchangeEntity.getAdFunctionErrorMessages(list);
    }
    return list;
  }
  
  public List<AbstractNodeEntity> getAllCreatedNodes() {
    
    return createdNodes;
  } 

  public void resetCreatedLists() {
    
    createdNodes.clear();
  }
    
  public List<AbstractNodeEntity> getAllDeletedNodes() {
    
    List<AbstractNodeEntity> deletedNodes = new ArrayList<>();
    for (AbstractNodeEntity node: nodeMap.values()) {
      
      if (node.getIsDeleted()) {
        
        deletedNodes.add(node);
      }
    }
    return deletedNodes;
  }   

  public MappablePointEntity getChildMappablePointByRawPointMetricIdNullIfNotExists(String metricId) {

    if (_mappablePointsByRawPointMetricIdMap.isEmpty()) {
      for (AbstractNodeEntity node: nodeMap.values()) {
        if (node instanceof MappablePointEntity) {
          
          MappablePointEntity mappablePoint = (MappablePointEntity)node;
          _mappablePointsByRawPointMetricIdMap.put(mappablePoint.getMetricId(), mappablePoint);
        }
      }
    }
    return _mappablePointsByRawPointMetricIdMap.get(metricId);
  }
  
  public void resetMappablePointsByRawPointMetricIdMap() {
    _mappablePointsByRawPointMetricIdMap.clear();
  }
  
  public List<BuildingEntity> getAllBuildings() {

    List<BuildingEntity> list = new ArrayList<>();
    list.addAll(childBuildings);
    return list;
  }

  public List<BuildingEntity> getAllCreatedBuildings() {

    List<BuildingEntity> list = new ArrayList<>();
    for (AbstractNodeEntity node: createdNodes) {
      
      if (node instanceof BuildingEntity) {
        
        list.add((BuildingEntity)node);
      }
    }
    return list;    
  }
  
  public List<SubBuildingEntity> getAllSubBuildings() {

    List<SubBuildingEntity> list = new ArrayList<>();
    Iterator<AbstractNodeEntity> nodeIterator = nodeMap.values().iterator();
    while (nodeIterator.hasNext()) {

      AbstractNodeEntity node = nodeIterator.next();
      if (node instanceof SubBuildingEntity) {

        list.add((SubBuildingEntity) node);
      }
    }
    return list;
  }

  public List<SubBuildingEntity> getAllCreatedSubBuildings() {

    List<SubBuildingEntity> list = new ArrayList<>();
    for (AbstractNodeEntity node: createdNodes) {
      
      if (node instanceof SubBuildingEntity) {
        
        list.add((SubBuildingEntity)node);
      }
    }
    return list;    
  }

  public EnergyExchangeEntity getEnergyExchangeSystemNode(Integer persistentIdentity) throws EntityDoesNotExistException {

    AbstractNodeEntity node = nodeMap.get(persistentIdentity);
    if (node instanceof EnergyExchangeEntity) {
      return (EnergyExchangeEntity)node;
    }
    throw new EntityDoesNotExistException("EnergyExchangeEntity with persistentIdentity: ["
        + persistentIdentity
        + "] does not exist in portfolio: [" 
        + getNaturalIdentity()
        + "]");
  }

  public EnergyExchangeEntity getEnergyExchangeSystemNodeNullIfNotExists(Integer persistentIdentity) {

    AbstractNodeEntity node = nodeMap.get(persistentIdentity);
    if (node instanceof EnergyExchangeEntity) {
      return (EnergyExchangeEntity)node;
    }
    return null;
  }
  
  
  public EnergyExchangeEntity getEnergyExchangeEntityByNaturalIdentity(String naturalIdentity) throws EntityDoesNotExistException {
    
    if (_energyExchangeSystemNodesByNaturalIdentityMap == null) {
      _energyExchangeSystemNodesByNaturalIdentityMap = new HashMap<>();
      for (AbstractNodeEntity node: nodeMap.values()) {
        if (node instanceof EnergyExchangeEntity) {
          _energyExchangeSystemNodesByNaturalIdentityMap.put(node.getNaturalIdentity(), (EnergyExchangeEntity)node);
        }
      }      
    }
    
    EnergyExchangeEntity energyExchangeEntity = _energyExchangeSystemNodesByNaturalIdentityMap.get(naturalIdentity);
    
    if (energyExchangeEntity == null) {

      throw new IllegalStateException("EnergyExchangeEntity with naturalIdentity: ["
          + naturalIdentity
          + "] does not exist for customer: ["
          + this
          + "]");
    }
    
    return energyExchangeEntity;
  }
  
  public List<EnergyExchangeEntity> getAllEnergyExchangeSystemNodes() {

    List<EnergyExchangeEntity> list = new ArrayList<>();
    Iterator<AbstractNodeEntity> nodeIterator = nodeMap.values().iterator();
    while (nodeIterator.hasNext()) {

      AbstractNodeEntity node = nodeIterator.next();
      if (node instanceof EnergyExchangeEntity) {

        list.add((EnergyExchangeEntity) node);
      }
    }
    return list;
  }
  
  public PlantEntity getPlant(Integer persistentIdentity) throws EntityDoesNotExistException {

    AbstractNodeEntity node = nodeMap.get(persistentIdentity);
    if (node instanceof PlantEntity) {
      return (PlantEntity)node;
    }
    throw new EntityDoesNotExistException("Plant with persistentIdentity: ["
        + persistentIdentity
        + "] does not exist in portfolio: [" 
        + getNaturalIdentity()
        + "]");
  }
  
  public List<PlantEntity> getAllPlants() {

    List<PlantEntity> list = new ArrayList<>();
    Iterator<AbstractNodeEntity> nodeIterator = nodeMap.values().iterator();
    while (nodeIterator.hasNext()) {

      AbstractNodeEntity node = nodeIterator.next();
      if (node instanceof PlantEntity) {

        list.add((PlantEntity) node);
      }
    }
    return list;
  }

  public List<PlantEntity> getAllCreatedPlants() {

    List<PlantEntity> list = new ArrayList<>();
    for (AbstractNodeEntity node: createdNodes) {
      
      if (node instanceof PlantEntity) {
        
        list.add((PlantEntity)node);
      }
    }
    return list;    
  }  
  
  public LoopEntity getLoop(Integer persistentIdentity) throws EntityDoesNotExistException {

    AbstractNodeEntity node = nodeMap.get(persistentIdentity);
    if (node instanceof LoopEntity) {
      return (LoopEntity)node;
    }
    throw new EntityDoesNotExistException("Loop with persistentIdentity: ["
        + persistentIdentity
        + "] does not exist in portfolio: [" 
        + getNaturalIdentity()
        + "]");
  }
  
  public List<LoopEntity> getAllLoops() {

    List<LoopEntity> list = new ArrayList<>();
    Iterator<AbstractNodeEntity> nodeIterator = nodeMap.values().iterator();
    while (nodeIterator.hasNext()) {

      AbstractNodeEntity node = nodeIterator.next();
      if (node instanceof LoopEntity) {

        list.add((LoopEntity) node);
      }
    }
    return list;
  }

  public List<LoopEntity> getAllCreatedLoops() {

    List<LoopEntity> list = new ArrayList<>();
    for (AbstractNodeEntity node: createdNodes) {
      
      if (node instanceof LoopEntity) {
        
        list.add((LoopEntity)node);
      }
    }
    return list;    
  }  
  
  public List<FloorEntity> getAllFloors() {

    List<FloorEntity> list = new ArrayList<>();
    Iterator<AbstractNodeEntity> nodeIterator = nodeMap.values().iterator();
    while (nodeIterator.hasNext()) {

      AbstractNodeEntity node = nodeIterator.next();
      if (node instanceof FloorEntity) {

        list.add((FloorEntity) node);
      }
    }
    return list;
  }
  
  public List<FloorEntity> getAllCreatedFloors() {

    List<FloorEntity> list = new ArrayList<>();
    for (AbstractNodeEntity node: createdNodes) {
      
      if (node instanceof FloorEntity) {
        
        list.add((FloorEntity)node);
      }
    }
    return list;    
  }
  
  public List<EquipmentEntity> getAllEquipment() {

    List<EquipmentEntity> list = new ArrayList<>();
    Iterator<AbstractNodeEntity> nodeIterator = nodeMap.values().iterator();
    while (nodeIterator.hasNext()) {

      AbstractNodeEntity node = nodeIterator.next();
      if (node instanceof EquipmentEntity) {

        list.add((EquipmentEntity) node);
      }
    }
    return list;
  }

  public EquipmentEntity getEquipment(Integer persistentIdentity) throws EntityDoesNotExistException {

    AbstractNodeEntity node = nodeMap.get(persistentIdentity);
    if (node instanceof EquipmentEntity) {
      return (EquipmentEntity)node;
    }
    throw new EntityDoesNotExistException("Equipment with persistentIdentity: ["
        + persistentIdentity
        + "] does not exist in portfolio: [" 
        + getNaturalIdentity()
        + "]");
  }
  
  public List<EquipmentEntity> getAllCreatedEquipment() {

    List<EquipmentEntity> list = new ArrayList<>();
    for (AbstractNodeEntity node: createdNodes) {
      
      if (node instanceof EquipmentEntity) {
        
        list.add((EquipmentEntity)node);
      }
    }
    return list; 
  }
  
  public List<CustomAsyncComputedPointEntity> getAllCustomAsyncComputedPoints() {

    List<CustomAsyncComputedPointEntity> list = new ArrayList<>();
    Iterator<AbstractNodeEntity> nodeIterator = nodeMap.values().iterator();
    while (nodeIterator.hasNext()) {

      AbstractNodeEntity node = nodeIterator.next();
      if (node instanceof CustomAsyncComputedPointEntity) {

        list.add((CustomAsyncComputedPointEntity) node);
      }
    }
    return list;
  }

  public CustomAsyncComputedPointEntity getCustomAsyncComputedPoint(Integer persistentIdentity) throws EntityDoesNotExistException {
    
    AbstractNodeEntity node = nodeMap.get(persistentIdentity);
    if (node instanceof CustomAsyncComputedPointEntity) {
      return (CustomAsyncComputedPointEntity)node;
    }
    throw new EntityDoesNotExistException("Custom Async Computed Point with persistentIdentity: ["
        + persistentIdentity
        + "] does not exist in portfolio: [" 
        + getNaturalIdentity()
        + "]");    
  }
  
  public CustomAsyncComputedPointEntity getCustomAsyncComputedPointNullIfNotExists(Integer persistentIdentity) {
    
    AbstractNodeEntity node = nodeMap.get(persistentIdentity);
    if (node instanceof CustomAsyncComputedPointEntity) {
      return (CustomAsyncComputedPointEntity)node;
    }
    return null;
  }
  
  public List<ScheduledAsyncComputedPointEntity> getAllScheduledAsyncComputedPoints() {

    List<ScheduledAsyncComputedPointEntity> list = new ArrayList<>();
    Iterator<AbstractNodeEntity> nodeIterator = nodeMap.values().iterator();
    while (nodeIterator.hasNext()) {

      AbstractNodeEntity node = nodeIterator.next();
      if (node instanceof ScheduledAsyncComputedPointEntity) {

        list.add((ScheduledAsyncComputedPointEntity) node);
      }
    }
    return list;
  }
  
  public List<WeatherAsyncComputedPointEntity> getAllWeatherAsyncComputedPoints() {

    List<WeatherAsyncComputedPointEntity> list = new ArrayList<>();
    Iterator<AbstractNodeEntity> nodeIterator = nodeMap.values().iterator();
    while (nodeIterator.hasNext()) {

      AbstractNodeEntity node = nodeIterator.next();
      if (node instanceof WeatherAsyncComputedPointEntity) {

        list.add((WeatherAsyncComputedPointEntity) node);
      }
    }
    return list;
  }
  
  public List<MappablePointEntity> getAllMappablePoints() {

    List<MappablePointEntity> list = new ArrayList<>();
    Iterator<AbstractNodeEntity> nodeIterator = nodeMap.values().iterator();
    while (nodeIterator.hasNext()) {

      AbstractNodeEntity node = nodeIterator.next();
      if (node instanceof MappablePointEntity) {

        list.add((MappablePointEntity) node);
      }
    }
    return list;
  }

  /**
   * 
   * DB grouping:
   * <pre>
      GROUP BY 
        p.name,
        nt.tag_id, -- parent equipment type
        p.data_type_id, 
        p.display_name, 
        ppt.node_template_id, -- point template 
        p.unit_id, 
   * </pre>
   * 
   * @param name point group name
   * @param parentEnergyExchangeType point group parent energy exchange type
   * @param dataType point group data type
   * @param displayName point group display name
   * @param pointTemplateId -1 represents NULL, -2 represents ANY/WILDCARD
   * @param unitId  -1 represents NULL, -2 represents ANY/WILDCARD
   * 
   * @return The set of mappable points that have the same characteristics,
   *         as specified by the given input parameters.  NOTE: -1 means NULL
   */
  public Set<MappablePointEntity> getMappablePointGroup(
      String name,
      AbstractEnergyExchangeTypeEntity parentEnergyExchangeType,
      DataType dataType,
      String displayName,
      int pointTemplateId,
      int unitId) {

    String nameRegex = null;
    if (name.contains("*")) {
      nameRegex = wildcardToRegex(name);
    }
    
    String displayNameRegex = null;
    if (displayName.contains("*")) {
      if (displayName.equals(name)) {
        displayNameRegex = nameRegex;
      } else {
        displayNameRegex = wildcardToRegex(displayName);  
      }
    }
    
    Set<MappablePointEntity> set = new HashSet<>();
    Collection<AbstractNodeEntity> nodes = nodeMap.values();
    for (AbstractNodeEntity node: nodes) {

      if (node instanceof MappablePointEntity) {

        MappablePointEntity mappablePoint = (MappablePointEntity)node;
        
        DataType dt = mappablePoint.getDataType();
        if (dt.equals(dataType) 
            && like(name, nameRegex, mappablePoint.getName())
            && like(displayName, displayNameRegex, mappablePoint.getDisplayName())) {
          
          AbstractNodeEntity parentNode = mappablePoint.getParentNode();
          if (parentNode instanceof EnergyExchangeEntity) {
            
            AbstractEnergyExchangeTypeEntity pet = ((EnergyExchangeEntity)parentNode).getEnergyExchangeTypeNullIfNotExists();
            if ((pet == null && parentEnergyExchangeType == null) 
                || (pet != null && parentEnergyExchangeType != null && pet.equals(parentEnergyExchangeType))) {

              int ptid = NULL;
              AbstractNodeTagTemplateEntity pt = mappablePoint.getPointTemplateNullIfEmpty();
              if (pt != null) {
                ptid = pt.getPersistentIdentity();
              }
              
              if ((pointTemplateId == ANY)
                  || (pointTemplateId == NULL && ptid == NULL)
                  || (pointTemplateId == ptid)) {              
              
                int uid = NULL;
                UnitEntity u = mappablePoint.getUnitNullIfNotExists();
                if (u != null) {
                  uid = u.getPersistentIdentity();
                }
                
                if ((unitId == ANY)
                    || (unitId == NULL && uid == NULL)
                    || (unitId == uid)) {
                  
                  set.add(mappablePoint);
                }
              }
            }
          }
        }
      }
    }
    return set;
  }
  
  public static boolean like(
      String searchTerm,
      String regex,
      String string) {
    
    if (regex != null) {
      return string.matches(regex);
    } else {
      return string.equals(searchTerm); 
    }
  }
  
  public static String wildcardToRegex(String wildcardString) {
  
    // https://stackoverflow.com/questions/14134558/list-of-all-special-characters-that-need-to-be-escaped-in-a-regex/26228852#26228852
    // The 12 is arbitrary, you may adjust it to fit your needs depending
    // on how many special characters you expect in a single pattern.
    StringBuilder sb = new StringBuilder(wildcardString.length() + 12);
    sb.append('^');
    for (int i = 0; i < wildcardString.length(); ++i) {
      
      char c = wildcardString.charAt(i);
      if (c == '*') {
        sb.append(".*");
      } else if (c == '?') {
        sb.append('.');
      } else if ("\\.[]{}()+-^$|".indexOf(c) >= 0) {
        sb.append('\\');
          sb.append(c);
      } else {
        sb.append(c);
      }
    }
    sb.append('$');
    return sb.toString();
  }
  
  public BuildingEntity getBuilding(Integer persistentIdentity) throws EntityDoesNotExistException {
    
    AbstractNodeEntity node = nodeMap.get(persistentIdentity);
    if (node instanceof BuildingEntity) {
      return (BuildingEntity)node;
    }
    throw new EntityDoesNotExistException("building with persistentIdentity: ["
        + persistentIdentity
        + "] does not exist in portfolio: [" 
        + getNaturalIdentity()
        + "]");
  }  
  
  public BuildingEntity getBuildingNullIfNotExists(Integer persistentIdentity) {
    
    AbstractNodeEntity node = nodeMap.get(persistentIdentity);
    if (node instanceof BuildingEntity) {
      return (BuildingEntity)node;
    }
    return null;
  }
  
  public Set<BuildingEntity> getChildBuildingsByNamePattern(String name) {
    
    Set<BuildingEntity> set = new TreeSet<>();
    
    String nameRegex = null;
    if (name != null && name.contains("*")) {
      nameRegex = wildcardToRegex(name);
    }
    
    for (BuildingEntity building: this.childBuildings) {
      
      if (name == null || name.equals("*") || like(name, nameRegex, building.getName())) {
        
        set.add(building);
      }
    }
    
    return set;
  }
  
  public CustomPointFormulaVariableEligiblePoint getCustomPointFormulaVariableEligiblePointNullIfNotExists(Integer persistentIdentity) {
    
    AbstractNodeEntity node = nodeMap.get(persistentIdentity);
    if (node instanceof CustomPointFormulaVariableEligiblePoint) {
      return (CustomPointFormulaVariableEligiblePoint)node;
    }
    return null;
  }

  public AbstractPointEntity getPoint(Integer persistentIdentity) throws EntityDoesNotExistException {
    
    AbstractNodeEntity node = nodeMap.get(persistentIdentity);
    if (node instanceof AbstractPointEntity) {
      return (AbstractPointEntity)node;
    }
    throw new EntityDoesNotExistException("Point with persistentIdentity: ["
        + persistentIdentity
        + "] does not exist in portfolio: [" 
        + getNaturalIdentity()
        + "]");
  }  
  
  public MappablePointEntity getMappablePoint(Integer persistentIdentity) throws EntityDoesNotExistException {
    
    AbstractNodeEntity node = nodeMap.get(persistentIdentity);
    if (node instanceof MappablePointEntity) {
      return (MappablePointEntity)node;
    }
    throw new EntityDoesNotExistException("Mappable point with persistentIdentity: ["
        + persistentIdentity
        + "] does not exist in portfolio: [" 
        + getNaturalIdentity()
        + "]");
  }  
  
  public MappablePointEntity getMappablePointNullIfNotExists(Integer persistentIdentity) {
    
    AbstractNodeEntity node = nodeMap.get(persistentIdentity);
    if (node instanceof MappablePointEntity) {
      return (MappablePointEntity)node;
    }
    return null;
  }
  
  public List<MappablePointEntity> getAllCreatedMappablePoints() {

    List<MappablePointEntity> list = new ArrayList<>();
    for (AbstractNodeEntity node: createdNodes) {
      
      if (node instanceof MappablePointEntity) {
        
        list.add((MappablePointEntity)node);
      }
    }
    return list; 
  }

  public List<CustomAsyncComputedPointEntity> getAllCreatedCustomAsyncComputedPoints() {

    List<CustomAsyncComputedPointEntity> list = new ArrayList<>();
    for (AbstractNodeEntity node: createdNodes) {
      
      if (node instanceof CustomAsyncComputedPointEntity) {
        
        list.add((CustomAsyncComputedPointEntity)node);
      }
    }
    return list; 
  }
  
  public List<ScheduledAsyncComputedPointEntity> getAllCreatedScheduledAsyncComputedPoints() {

    List<ScheduledAsyncComputedPointEntity> list = new ArrayList<>();
    for (AbstractNodeEntity node: createdNodes) {
      
      if (node instanceof ScheduledAsyncComputedPointEntity) {
        
        list.add((ScheduledAsyncComputedPointEntity)node);
      }
    }
    return list; 
  }

  public List<AsyncComputedPointEntity> getAllCreatedAsyncComputedPoints() {

    List<AsyncComputedPointEntity> list = new ArrayList<>();
    for (AbstractNodeEntity node: createdNodes) {
      
      if (node instanceof AsyncComputedPointEntity 
          && !(node instanceof ScheduledAsyncComputedPointEntity)
          && !(node instanceof CustomAsyncComputedPointEntity)) {
        
        list.add((AsyncComputedPointEntity)node);
      }
    }
    return list; 
  }
  
  @Override
  public int calculateTotalMappedPointCount() {
    
    int childPointCount = getDirectChildMappedPointCount();
    
    for (BuildingEntity building : childBuildings) {
      childPointCount = childPointCount + building.getTotalMappedPointCount();
    }
    
    return childPointCount;
  }

  @Override
  public Set<AbstractNodeEntity> getAllChildNodes() {
    
    Set<AbstractNodeEntity> set = new HashSet<>();
    set.addAll(this.getChildPoints());
    set.addAll(this.childBuildings);
    return set;
  }
  
  @Override
  public AbstractNodeEntity duplicateNode(PortfolioEntity portfolio, AbstractNodeEntity parentNode, int duplicationIndex) {
    throw new IllegalStateException("duplicateNode() cannot be called on the root portfolio node: [" + this + "].");
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

    for (BuildingEntity building : childBuildings) {
      building.validate(issueTypes, validationMessages, remediate);
    }
    
    // RP-6335: Deal with AD function instance candidates whose equipment has been deleted.
    IssueType issueType = IssueType.AD_FUNCTION_EQUIPMENT_DOES_NOT_HAVE_TYPE;
    if (issueTypes.contains(issueType) && !invalidAdFunctionInstanceCandidates.isEmpty()) {
      
      setIsModified("invalidAdFunctionInstanceCandidates");

      for (AbstractAdFunctionInstanceEntity adFunctionInstance: invalidAdFunctionInstanceCandidates) {

        Map<String, Object> entities = new HashMap<>();
        if (adFunctionInstance.getIsCandidate()) {
          entities.put("function_candidate", adFunctionInstance);  
        } else {
          entities.put("function_instance", adFunctionInstance);  
        }
        
        RemediationStrategy remediationStrategy = AdFunctionRemediationStrategyFinder.find(
            issueType, 
            adFunctionInstance);

        validationMessages.add(ValidationMessage.builder()
            .withIssueType(issueType)
            .withDetails("AD function instance candidate has equipment that no longer exists")
            .withEntityType(adFunctionInstance.getClass().getSimpleName())
            .withNaturalIdentity("ID=" + adFunctionInstance.getPersistentIdentity())
            .withRemediationDescription(remediationStrategy.getRemediationDescription())
            .withRemediationStrategy(remediationStrategy)
            .build());
        
        if (remediate) {
          remediationStrategy.remediate(entities);
        }
      }
    }    
  }
  
  public int remediate() {

    if (!getParentCustomer().loadAdFunctionInstances) {
      
      throw new RuntimeException("Cannot remediate a portfolio that hasn't had AD function instances loaded");
    }
    
    try {

      // Phase One: Points (Point Template Associations and Haystack Tags)
      LOGGER.debug("REMEDIATE PHASE ONE: {}", this);
      Set<IssueType> phaseOneIssueTypes = ValidationMessage.extractPhaseOneIssueTypes();
      List<ValidationMessage> phaseOneValidationMessages = remediate(phaseOneIssueTypes);

      // Phase Two: Rule Candidates and Rule Instances
      LOGGER.debug("REMEDIATE PHASE TWO: {}", this);
      Set<IssueType> phaseTwoIssueTypes = ValidationMessage.extractPhaseTwoIssueTypes();
      List<ValidationMessage> phaseTwoValidationMessages = remediate(phaseTwoIssueTypes);

      // Return the number of issues resolved.
      int numIssuesResolved = phaseOneValidationMessages.size() + phaseTwoValidationMessages.size();
      LOGGER.debug("NUM ISSUES RESOLVED FOR {}: {}", this, numIssuesResolved);
      return numIssuesResolved;

    } catch (Exception e) {
      throw new RuntimeException("Unable to remediate portfolio: ["
          + this
          + "], error: "
          + e.getMessage(), e);
    }
  }

  @Override
  public List<ValidationMessage> remediate(Set<IssueType> issueTypes) {

    if (!getParentCustomer().loadAdFunctionInstances) {
      
      throw new RuntimeException("Cannot remediate a portfolio that hasn't had AD function instances loaded");
    }
    
    return super.remediate(issueTypes);
  }  
   
  @Override
  public void evaluateState() {
    
    // NOTE:
    // Online and OutOfBand customers are evaluated differently, 
    // see the subclass implementations.    

    // Evaluates all descendant billable buildings for their config status, 
    // which is the number of mapped points that the building has.  All parent customer
    // and distributor config states are derived from their descendant building 
    // config states.
    evaluateConfigState();
    
    // Evaluates all descendant billable buildings for their payment status,
    // which is the status of the building subscription, if any.  All parent customer
    // and distributor payment states are derived from their descendant building 
    // payment states.
    evaluatePaymentState();
    
    // Evaluates to see whether any buildings that have had their pending deletion 
    // flag set for long enough are eligible for deletion (it is assumed that 
    // everything has been archived in the time between the pending deletion flag
    // set and the transition to being hard deleted.
    evaluatePendingDeletionState();
  }  
  
  public void evaluateConfigState() {
    
    // Iterate through the child buildings.  We should not have to make any state transitions, as these
    // are normally done when the underlying attributes (i.e. mapped point count) are changed.  However,
    // we cannot assume that all changes are done through this domain model and domain services framework.
    if (parentCustomer instanceof OnlineCustomerEntity) {
      
      int numActiveBuildings = 0;
      for (BuildingEntity childBuilding: childBuildings) {
        
        if (childBuilding instanceof BillableBuildingEntity) {

          BillableBuildingEntity bb = (BillableBuildingEntity)childBuilding;
          
          bb.evaluateConfigState();
          
          BuildingStatus buildingStatus = bb.getBuildingStatus();
          if (buildingStatus.equals(BuildingStatus.ACTIVE)) {

            BuildingSubscriptionEntity bs = bb.getChildBuildingSubscriptionNullIfNotExists();
            if (bs != null) {
              
              boolean isSubscriptionCanceled = bb.isSubscriptionCanceled();
              boolean hasCurrentPaymentIntervalExpired = false;
              if (isSubscriptionCanceled) {
                
                hasCurrentPaymentIntervalExpired = bs.hasCurrentPaymentIntervalExpired();
              }
              
              // If the subscription has not been cancelled OR the subscription has been cancelled
              // AND the current payment interval has not expired yet, then we consider the building
              // to still be ACTIVE (from a billable perspective)
              if (!isSubscriptionCanceled 
                  || (isSubscriptionCanceled && !hasCurrentPaymentIntervalExpired)) {
                
                numActiveBuildings++;
              }
            }
          }
        }
      }
      
      // See if we need to make any changes to the customer based upon the state of the child buildings, which
      // for the config state, is solely based upon the number of ACTIVE billable buildings.
      OnlineCustomerEntity onlineCustomer = (OnlineCustomerEntity)parentCustomer;
      CustomerStatus customerStatus = onlineCustomer.getCustomerStatus();
      
      if (numActiveBuildings == 0 && customerStatus.equals(CustomerStatus.BILLABLE)) {
        
        onlineCustomer.setCustomerStatus(CustomerStatus.CREATED);
        LOGGER.info(
            AbstractEntity.getTimeKeeper().getCurrentLocalDate()
            + ": "
            + onlineCustomer.getName() 
            + ": customer config status: BILLABLE --> CREATED: numActiveBuildings: ["
            + numActiveBuildings
            + "]");
        
      } else if (numActiveBuildings > 0 && customerStatus.equals(CustomerStatus.CREATED)) {

        onlineCustomer.setCustomerStatus(CustomerStatus.BILLABLE);
        LOGGER.info(
            AbstractEntity.getTimeKeeper().getCurrentLocalDate()
            + ": "
            + onlineCustomer.getName() 
            + ": customer config status: CREATED --> BILLABLE: numActiveBuildings: ["
            + numActiveBuildings
            + "]");
      }
    }
  }   
  
  public void evaluatePaymentState() {

    int numDelinquentChildBuildings = 0;
    for (BuildingEntity childBuilding: childBuildings) {
      
      if (childBuilding instanceof BillableBuildingEntity) {

        BillableBuildingEntity bb = (BillableBuildingEntity)childBuilding;
        
        bb.evaluatePaymentState();
        
        if (bb.getBuildingPaymentStatus().equals(BuildingPaymentStatus.DELINQUENT)) {
          
          numDelinquentChildBuildings++;
        }
      }
    }
    
    // If an online customer has at least one DELINQUENT child building, then 
    // it will be DELINQUENT as well.  If all child buildings are UP_TO_DATE, then it
    // is UP_TO_DATE as well.
    if (parentCustomer instanceof OnlineCustomerEntity) {
      
      OnlineCustomerEntity oc = (OnlineCustomerEntity)parentCustomer;
      
      CustomerPaymentStatus customerPaymentStatus = oc.getCustomerPaymentStatus();
      
      if (numDelinquentChildBuildings > 0 && customerPaymentStatus.equals(CustomerPaymentStatus.UP_TO_DATE)) {
        
        oc.setCustomerPaymentStatus(CustomerPaymentStatus.DELINQUENT);
        
        LOGGER.info(
            AbstractEntity.getTimeKeeper().getCurrentLocalDate()
            + ": "
            + oc.getName()
            + ": customer payment status: UP_TO_DATE --> DELINQUENT: numDelinquentChildBuildings: ["
            + numDelinquentChildBuildings
            + "].");
        
      } else if (numDelinquentChildBuildings == 0 && customerPaymentStatus.equals(CustomerPaymentStatus.DELINQUENT)) {
        
        oc.setCustomerPaymentStatus(CustomerPaymentStatus.UP_TO_DATE);
        
        LOGGER.info(
            AbstractEntity.getTimeKeeper().getCurrentLocalDate()
            + ": "
            + oc.getName()
            + ": customer payment status: DELINQUENT --> UP_TO_DATE: numDelinquentChildBuildings: ["
            + numDelinquentChildBuildings
            + "].");
        
      }     
    }
  }
  
  public void evaluatePendingDeletionState() {

    for (BuildingEntity childBuilding: childBuildings) {
      
      if (childBuilding instanceof BillableBuildingEntity) {

        BillableBuildingEntity bb = (BillableBuildingEntity)childBuilding;
        
        // See if we need to hard delete the building.
        //
        // The portfolio will be updated/persisted by the higher level service that 
        // is invoking this method, so here, all we do is update the object tree.
        if (bb.shouldBeHardDeleted()) {
          
          bb.setIsDeleted();
          
          BuildingSubscriptionEntity bs = bb.getChildBuildingSubscriptionNullIfNotExists();
          if (bs == null) {
            LOGGER.info(
                AbstractEntity.getTimeKeeper().getCurrentLocalDate()
                + ": "
                + bb.getPersistentIdentity() 
                + ": is being hard deleted as pending deletion is: ["
                + bb.getPendingDeletion()
                + "] and config status: ["
                + bb.getBuildingStatus()
                + "] as of: ["
                + bb.getBuildingStatusUpdatedAt()
                + "].");
          } else {
            LOGGER.info(
                AbstractEntity.getTimeKeeper().getCurrentLocalDate()
                + ": "
                + bb.getPersistentIdentity() 
                + ": is being hard deleted as pending deletion is: ["
                + bb.getPendingDeletion()
                + "] and config status: ["
                + bb.getBuildingStatus()
                + "] as of: ["
                + bb.getBuildingStatusUpdatedAt()
                + "] and subscription interval ends at: ["
                + bs.getCurrentIntervalEndsAt()
                + "]");
          }
        }
      }
    }
  }

  @Override
  public boolean getIsModified() {
    
    boolean isModified = false;
    for (AbstractNodeEntity node: nodeMap.values()) {
      
      if (node.getIsModified() || node.getIsDeleted()) {
        isModified = true;
        break;
      }
    }
    
    if (!isModified && !invalidAdFunctionInstanceCandidates.isEmpty()) {
      isModified = true;
    }
    return isModified;
  }
  
  public AbstractNodeEntity getChildNodeByNameNullIfNotExists(String name) {
    
    AbstractNodeEntity childNode = getChildBuildingByNameNullIfNotExists(name);
    if (childNode == null) {

      childNode = getChildPointByNameNullIfNotExists(name);
    }
    return childNode;
  }

  public AbstractNodeEntity getChildNodeByNodePath(String nodePath) throws EntityDoesNotExistException {
    
    for (AbstractNodeEntity node: nodeMap.values()) {
      if (node.getNodePath().equals(nodePath)) {
        return node;
      }
    }
    String np = "/" + nodePath;
    for (AbstractNodeEntity node: nodeMap.values()) {
      if (node.getNodePath().equals(np)) {
        LOGGER.warn("Please remove leading forward slash when retrieving by node path: {}", nodePath);
        return node;
      }
    }
    throw new EntityDoesNotExistException("node with nodePath: ["
            + nodePath
            + "] not found in portfolio: ["
            + getNodePath()
            + "]");
  }  
  
  @Override
  public String getNodePath() {
    return getDisplayName();
  }
  
  // This is the entry point for mapping a portfolio to DTOs.
  public static Map<String, Object> mapToDtos(PortfolioEntity portfolio) {

    PortfolioDtoMapper portfolioDtoMapper = new PortfolioDtoMapper(portfolio.getParentCustomer());
    return portfolioDtoMapper.mapEntityToDto(portfolio);
  }
  
  public static PortfolioEntity mapFromDtos(
      AbstractCustomerEntity parentCustomer,
      List<RawPointDto> rawPointDtoList,
      List<NonPointNodeDto> nonPointNodeDtoList,
      List<MappablePointNodeDto> mappablePointNodeDtoList,
      List<CustomAsyncComputedPointNodeDto> customAsyncComputedPointNodeDtoList,
      List<ScheduledAsyncComputedPointNodeDto> scheduledAsyncComputedPointNodeDtoList,
      List<AsyncComputedPointNodeDto> asyncComputedPointNodeDtoList,
      List<NodeTagDto> nodeTagDtoList,
      List<EnergyExchangeSystemEdgeDto> energyExchangeSystemEdgeDtoList,
      List<ReportInstanceDto> reportInstanceDtoList,
      List<ReportInstanceStatusDto> reportInstanceStatusDtoList,
      List<AdFunctionInstanceDto> adFunctionInstanceCandidateDtoList,
      List<AdFunctionInstanceDto> adFunctionInstanceDtoList,
      List<AdFunctionErrorMessagesDto> adFunctionErrorMessages) throws EntityAlreadyExistsException {

    // If there aren't any non-point nodes, then there is nothing to map.
    if (nonPointNodeDtoList == null || nonPointNodeDtoList.isEmpty()) {
      return null;
    }
    if (customAsyncComputedPointNodeDtoList == null) {
      throw new IllegalStateException("customAsyncComputedPointNodeDtoList cannot be null");
    }
    if (mappablePointNodeDtoList == null) {
      throw new IllegalStateException("mappablePointNodeDtoList cannot be null");
    }
    if (scheduledAsyncComputedPointNodeDtoList == null) {
      throw new IllegalStateException("scheduledAsyncComputedPointNodeDtoList cannot be null");
    }
    if (asyncComputedPointNodeDtoList == null) {
      throw new IllegalStateException("asyncComputedPointNodeDtoList cannot be null");
    }
    if (nodeTagDtoList == null) {
      throw new IllegalStateException("nodeTagDtoList cannot be null");
    }
    if (energyExchangeSystemEdgeDtoList == null) {
      throw new IllegalStateException("energyExchangeSystemEdgeDtoList cannot be null");
    }
    if (reportInstanceDtoList == null) {
      throw new IllegalStateException("reportInstanceDtoList cannot be null");
    }
    if (reportInstanceStatusDtoList == null) {
      throw new IllegalStateException("reportInstanceStatusDtoList cannot be null");
    }
    if (adFunctionInstanceCandidateDtoList == null) {
      throw new IllegalStateException("adFunctionInstanceCandidateDtoList cannot be null");
    }
    if (adFunctionInstanceDtoList == null) {
      throw new IllegalStateException("adFunctionInstanceDtoList cannot be null");
    }
    if (adFunctionErrorMessages == null) {
      throw new IllegalStateException("adFunctionErrorMessages cannot be null");
    }
    
    PortfolioDtoMapper portfolioDtoMapper = new PortfolioDtoMapper(parentCustomer);
    Map<String, Object> dtos = PortfolioDtoMapper.buildDtosMap(
        rawPointDtoList,
        nonPointNodeDtoList,
        mappablePointNodeDtoList,
        customAsyncComputedPointNodeDtoList,
        scheduledAsyncComputedPointNodeDtoList,
        asyncComputedPointNodeDtoList,
        nodeTagDtoList,
        energyExchangeSystemEdgeDtoList,
        reportInstanceDtoList,
        reportInstanceStatusDtoList,
        adFunctionInstanceCandidateDtoList,
        adFunctionInstanceDtoList,
        adFunctionErrorMessages);

    // Since the portfolio is the aggregate root, we pass in null.
    return portfolioDtoMapper.mapDtoToEntity(null, dtos);
  }
}
//@formatter:on