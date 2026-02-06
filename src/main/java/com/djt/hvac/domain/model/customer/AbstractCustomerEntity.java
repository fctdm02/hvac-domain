//@formatter:off
package com.djt.hvac.domain.model.customer;

import static java.util.Objects.requireNonNull;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.AbstractPersistentEntity;
import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.common.mapper.DtoMapper;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.customer.dto.CustomerDto;
import com.djt.hvac.domain.model.customer.enums.CustomerPaymentStatus;
import com.djt.hvac.domain.model.customer.enums.CustomerStatus;
import com.djt.hvac.domain.model.dictionary.enums.NodeType;
import com.djt.hvac.domain.model.dictionary.enums.UnitSystem;
import com.djt.hvac.domain.model.distributor.AbstractDistributorEntity;
import com.djt.hvac.domain.model.distributor.OnlineDistributorEntity;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BuildingEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.BuildingPaymentStatus;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.BuildingStatus;
import com.djt.hvac.domain.model.rawpoint.RawPointEntity;
import com.djt.hvac.domain.model.user.CustomerUserEntity;

public abstract class AbstractCustomerEntity extends AbstractPersistentEntity {
  private static final long serialVersionUID = 1L;
  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCustomerEntity.class);


  private final AbstractDistributorEntity parentDistributor;
  private final String name;
  private Timestamp createdAt = AbstractEntity.getTimeKeeper().getCurrentTimestamp();
  private Timestamp updatedAt = AbstractEntity.getTimeKeeper().getCurrentTimestamp();
  private String uuid = UUID.randomUUID().toString();
  private UnitSystem unitSystem = UnitSystem.IP;
  private Set<CustomerLevelPointTemplateUnitMappingOverrideEntity> pointTemplateUnitMappingOverrides = new HashSet<>();
  private Timestamp startDate;
  private CustomerStatus customerStatus = CustomerStatus.CREATED;
  private Timestamp customerStatusUpdatedAt = AbstractEntity.getTimeKeeper().getCurrentTimestamp();
  private CustomerPaymentStatus customerPaymentStatus = CustomerPaymentStatus.UP_TO_DATE;
  private Timestamp customerPaymentStatusUpdatedAt = AbstractEntity.getTimeKeeper().getCurrentTimestamp();
  
  private PortfolioEntity childPortfolio;
  
  private Set<RawPointEntity> childRawPoints = new LinkedHashSet<>();
  private Set<CustomerUserEntity> childCustomerUsers = new TreeSet<>();
  
  private boolean hasUnpersistedRawPoints = false;
  public boolean hasUnpersistedRawPoints() {
    return hasUnpersistedRawPoints;
  }
  public void setHasUnpersistedRawPoints(boolean hasUnpersistedRawPoints) {
    this.hasUnpersistedRawPoints = hasUnpersistedRawPoints;
  }

  // Used to specify the state of the customer portfolio, as it was
  // loaded from the repository
  public NodeType filterNodeType;
  public List<Integer> filterNodePersistentIdentities;
  public boolean loadAdFunctionInstances;
  public boolean loadReportInstances;
  public boolean loadPointLastValues;
  public boolean loadBuildingTemporalData;
  public boolean loadCustomPointTemporalData;
  public NodeType depthNodeType;
  
  public abstract boolean allowAutomaticConfiguration();
  
  // For new instances (i.e. have not been persisted yet)
  public AbstractCustomerEntity(
      AbstractDistributorEntity parentDistributor,
      String name,
      UnitSystem unitSystem) {
    super(null);
    requireNonNull(parentDistributor, "parentDistributor cannot be null");
    requireNonNull(name, "name cannot be null");
    requireNonNull(unitSystem, "unitSystem cannot be null");
    this.parentDistributor = parentDistributor;
    this.name = name;
    this.unitSystem = unitSystem;
    this.startDate = Timestamp.valueOf(AbstractEntity.adjustCurrentTimeIntoMonthlyFloor());
  }
  
  public AbstractCustomerEntity(
      Integer persistentIdentity,
      AbstractDistributorEntity parentDistributor,
      String name,
      String uuid,
      UnitSystem unitSystem,
      Timestamp createdAt,
      Timestamp updatedAt,
      Timestamp startDate,
      CustomerStatus customerStatus,
      Timestamp customerStatusUpdatedAt,
      CustomerPaymentStatus customerPaymentStatus,
      Timestamp customerPaymentStatusUpdatedAt,
      PortfolioEntity childPortfolio) {
    super(persistentIdentity);
    requireNonNull(parentDistributor, "parentDistributor cannot be null");
    requireNonNull(name, "name cannot be null");
    requireNonNull(uuid, "uuid cannot be null");
    requireNonNull(unitSystem, "unitSystem cannot be null");
    requireNonNull(createdAt, "createdAt cannot be null");
    requireNonNull(updatedAt, "updatedAt cannot be null");
    requireNonNull(startDate, "startDate cannot be null");
    requireNonNull(customerStatus, "customerStatus cannot be null");
    requireNonNull(customerStatusUpdatedAt, "customerStatusUpdatedAt cannot be null");
    requireNonNull(customerPaymentStatus, "customerStatus cannot be null");
    requireNonNull(customerPaymentStatusUpdatedAt, "customerPaymentStatusUpdatedAt cannot be null");
    this.parentDistributor = parentDistributor;
    this.name = name;
    this.uuid = uuid;
    this.unitSystem = unitSystem;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.startDate = startDate;
    this.customerStatus = customerStatus;
    this.customerStatusUpdatedAt = customerStatusUpdatedAt;
    this.customerPaymentStatus = customerPaymentStatus;
    this.customerPaymentStatusUpdatedAt = customerPaymentStatusUpdatedAt;
    this.childPortfolio = childPortfolio;
  }

  public Set<CustomerLevelPointTemplateUnitMappingOverrideEntity> getPointTemplateUnitMappingOverrides() {
    return pointTemplateUnitMappingOverrides;
  }
  
  public CustomerLevelPointTemplateUnitMappingOverrideEntity getPointTemplateUnitMappingOverride(Integer pointTemplateUnitMappingOverrideId) throws EntityDoesNotExistException{
    return getChild(CustomerLevelPointTemplateUnitMappingOverrideEntity.class, pointTemplateUnitMappingOverrides, pointTemplateUnitMappingOverrideId, this);
  }

  public boolean addPointTemplateUnitMappingOverride(CustomerLevelPointTemplateUnitMappingOverrideEntity pointTemplateUnitMappingOverride)
      throws EntityAlreadyExistsException {
    
    boolean result = addChild(pointTemplateUnitMappingOverrides, pointTemplateUnitMappingOverride, this);
    setIsModified("add:pointTemplateUnitMappingOverride");
    return result;
  }
  
  public void removePointTemplateUnitMappingOverride(Integer pointTemplateUnitMappingOverrideId)
      throws EntityDoesNotExistException {
    
    CustomerLevelPointTemplateUnitMappingOverrideEntity pointTemplateUnitMappingOverride = getPointTemplateUnitMappingOverride(pointTemplateUnitMappingOverrideId);
    pointTemplateUnitMappingOverride.setIsDeleted();
    setIsModified("remove:pointTemplateUnitMappingOverride");
  }
  
  public void removeAllPointTemplateUnitMappingOverrides() {
    
    for (CustomerLevelPointTemplateUnitMappingOverrideEntity override: pointTemplateUnitMappingOverrides) {

      override.setIsDeleted();
      setIsModified("remove:pointTemplateUnitMappingOverride");
    }
  }
  
  public Set<RawPointEntity> getRawPoints() {
    return childRawPoints;
  }

  public Set<RawPointEntity> getIgnoredRawPoints() {
    
    Set<RawPointEntity> ignoredRawPoints = new TreeSet<>();
    for (RawPointEntity rawPoint: childRawPoints) {
      if (rawPoint.getIgnored()) {
        ignoredRawPoints.add(rawPoint);
      }
    }
    return ignoredRawPoints;
  }
  
  public void addRawPoints(Collection<RawPointEntity> rawPoints) {
    
    boolean changed = childRawPoints.addAll(rawPoints);
    if (changed) {
      setIsModified("childRawPoints:added");
    }
    for (RawPointEntity rawPoint: rawPoints) {
      if (rawPoint.getPersistentIdentity() == null) {
        hasUnpersistedRawPoints = true;
        break;
      }
    }
  }

  public boolean addRawPoint(RawPointEntity rawPoint) throws EntityAlreadyExistsException {
    if (rawPoint.getPersistentIdentity() == null) {
      hasUnpersistedRawPoints = true;
    }
    return addChild(childRawPoints, rawPoint, this);
  }

  public RawPointEntity getRawPoint(Integer persistentIdentity) throws EntityDoesNotExistException {
    return getChild(RawPointEntity.class, childRawPoints, persistentIdentity, this);
  }
  
  private transient Map<String, RawPointEntity> _rawPointsByMetricIdMap = null;
  public RawPointEntity getRawPointByMetricId(String metricId) throws EntityDoesNotExistException {
    
    if (_rawPointsByMetricIdMap == null) {
      _rawPointsByMetricIdMap = new HashMap<>();
      for (RawPointEntity rawPoint: childRawPoints) {
        _rawPointsByMetricIdMap.put(rawPoint.getMetricId(), rawPoint);
      }
    }
    
    RawPointEntity rawPoint = _rawPointsByMetricIdMap.get(metricId);
    
    if (rawPoint == null) {

      throw new IllegalStateException("Raw point with metricId: ["
          + metricId
          + "] does not exist for customer: ["
          + this
          + "]");
    }
    
    return rawPoint;
  }
  
  public void resetRawPointsByMetricIdMap() {
    _rawPointsByMetricIdMap = null;
  }

  public PortfolioEntity getChildPortfolio() {
    return childPortfolio;
  }
  
  public Set<CustomerUserEntity> getChildCustomerUsers() {
    return childCustomerUsers;
  }

  public boolean addChildCustomerUser(CustomerUserEntity customerUser) throws EntityAlreadyExistsException {
    return addChild(childCustomerUsers, customerUser, this);
  }

  public CustomerUserEntity getChildCustomerUser(Integer persistentIdentity) throws EntityDoesNotExistException {
    return getChild(CustomerUserEntity.class, childCustomerUsers, persistentIdentity, this);
  }

  public CustomerUserEntity getChildCustomerUser(String email)
      throws EntityDoesNotExistException {
    
    for (CustomerUserEntity customerUser: childCustomerUsers) {
      
      if (customerUser.getEmail().equals(email)) {
        return customerUser;
      }
    }
    throw new EntityDoesNotExistException("Customer: ["
        + this
        + "] does not have user with email: ["
        + email
        + "].");
  }
  
  public CustomerUserEntity removeChildCustomerUser(Integer persistentIdentity)
      throws EntityDoesNotExistException {
    
    CustomerUserEntity childUser = getChildCustomerUser(persistentIdentity);
    childUser.setIsDeleted();
    return childUser;
  }  
  
  /**
   * Associating the child portfolio to the parent customer does not constitute a change
   * that needs to be stored to the repository.
   * 
   * @param childPortfolio
   */
  public void setChildPortfolio(PortfolioEntity childPortfolio) {
    this.childPortfolio = childPortfolio;
  }

  public AbstractDistributorEntity getParentDistributor() {
    return parentDistributor;
  }

  public Set<AbstractDistributorEntity> getAncestorDistributors() {
    Set<AbstractDistributorEntity> set = new LinkedHashSet<>();
    set.add(parentDistributor);
    AbstractDistributorEntity ancestorDistributor = parentDistributor.getParentDistributor();
    while (ancestorDistributor != null) {
      set.add(ancestorDistributor);
      ancestorDistributor = ancestorDistributor.getParentDistributor();
    }
    return set;
  }
  
  public String getName() {
    return name;
  }

  public Timestamp getUpdatedAt() {
    return updatedAt;
  }

  public CustomerStatus getCustomerStatus() {
    return customerStatus;
  }

  public Timestamp getCustomerStatusUpdatedAt() {
    return customerStatusUpdatedAt;
  }
  
  public void setCustomerStatus(CustomerStatus customerStatus) {
    
    this.customerStatus = customerStatus;
    this.customerStatusUpdatedAt = AbstractEntity.getTimeKeeper().getCurrentTimestamp();
    this.setIsModified("customerStatus");
  }
  
  public CustomerPaymentStatus getCustomerPaymentStatus() {
    return customerPaymentStatus;
  }
  
  public Timestamp getCustomerPaymentStatusUpdatedAt() {
    return customerPaymentStatusUpdatedAt;
  }
  
  public void setCustomerPaymentStatus(CustomerPaymentStatus newCustomerPaymentStatus) {
    
    if (newCustomerPaymentStatus.equals(customerPaymentStatus)) {
      return;
    }
    
    this.customerPaymentStatus = newCustomerPaymentStatus;
    this.customerPaymentStatusUpdatedAt = AbstractEntity.getTimeKeeper().getCurrentTimestamp();
    this.setIsModified("customerPaymentStatus");
  }
  
  public String getUuid() {
    return uuid;
  }
  
  // ONLY USED FOR TESTING
  public void setUuid(String uuid) {

    if (uuid == null) {
      throw new IllegalArgumentException("'uuid' cannot be null.");
    }
    if (!this.uuid.equals(uuid)) {

      this.uuid = uuid;
      setIsModified("uuid");
    }  
  }

  public UnitSystem getUnitSystem() {
    return unitSystem;
  }

  public void setUnitSystem(UnitSystem unitSystem) {
    
    if (this.unitSystem == null && unitSystem == null) {
      
      // BOTH NULL: DO NOTHING
      
    } else if ((this.unitSystem == null && unitSystem != null) 
        || (this.unitSystem != null && unitSystem == null)) {
      
      this.unitSystem = unitSystem;
      setIsModified("unitSystem");
      
    } else if (this.unitSystem != null && unitSystem != null) {
      
      if (!this.unitSystem.equals(unitSystem)) {

        this.unitSystem = unitSystem;
        setIsModified("unitSystem");
        
        if (this.unitSystem.equals(UnitSystem.IP)) {
          
          removeAllPointTemplateUnitMappingOverrides();
        }
        
      } else {
        
        // BOTH NOT NULL AND EQUAL TO EACH OTHER: DO NOTHING
        
      }
    }
  }    
  
  public Timestamp getCreatedAt() {
    return createdAt;
  }

  public Timestamp getStartDate() {
    return startDate;
  }
  
  public void setStartDate(Timestamp startDate) {
    
    if (startDate == null) {
      throw new IllegalArgumentException("'startDate' cannot be null.");
    }
    if (!this.startDate.equals(startDate)) {
      
      this.startDate = Timestamp.valueOf(AbstractEntity.adjustTimeIntoMonthlyFloor(AbstractEntity.convertTimestampToLocalDateTime(startDate)));
      LOGGER.info("Changing customer: [" + this.getName() + "] start date to: [" + this.startDate + "].");
      setIsModified("startDate");
    }    
  }
  
  @Override
  public String getNaturalIdentity() {

    return new StringBuilder()
        .append(parentDistributor.getNaturalIdentity())
        .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
        .append(name)
        .toString();
  }

  @Override
  public void validate(
      Set<IssueType> issueTypes,
      List<ValidationMessage> validationMessages,
      boolean remediate) {}

  @Override
  public void setIsModified(String modifiedAttributeName) {
    
    if (!modifiedAttributeName.equals(getClass().getSimpleName())) {
      super.setIsModified(modifiedAttributeName);
    }
  }
  
  public boolean isActive() {

    if (this.customerStatus.equals(CustomerStatus.DELETED)) {
      return false;
    }
    return true;
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

    if (childPortfolio != null && this instanceof OnlineCustomerEntity) {
      childPortfolio.evaluateConfigState();  
    }
  }    
  
  public void evaluatePaymentState() {

    if (childPortfolio != null && this instanceof OnlineCustomerEntity) {
      childPortfolio.evaluatePaymentState();
    }
  }
    
  public void evaluatePendingDeletionState() {
    
    if (childPortfolio != null && this instanceof OnlineCustomerEntity) {
      childPortfolio.evaluatePendingDeletionState();  
    }
  }
  
  public int getNumberDaysSinceSoftDeleted() {

    LocalDate currentLocalDate = AbstractEntity.getTimeKeeper().getCurrentLocalDate();
    
    LocalDate statusUpdatedAt = getCustomerStatusUpdatedAt()
        .toLocalDateTime()
        .toLocalDate();
    
    int numDays = 0;
    LocalDate ld = statusUpdatedAt;
    while (ld.isBefore(currentLocalDate)) {
      
      numDays++;
      ld = ld.plusDays(1);
    }
    
    return numDays;
  }
  
  private boolean hasAllBuildingsInCreatedState() {
    
    boolean hasAllBuildingsInCreatedState = true;
    
    for (BuildingEntity b: getChildPortfolio().getAllBuildings()) {
      if (!b.getBuildingStatus().equals(BuildingStatus.CREATED)) {
        return false;
      }
    }
    
    return hasAllBuildingsInCreatedState;
  }
  
  private boolean hasUpToDatePaymentStatus() {
    
    boolean hasUpToDatePaymentStatus = true;
    
    if (!customerPaymentStatus.equals(CustomerPaymentStatus.UP_TO_DATE)) {
      return false;
    }
    
    for (BuildingEntity b: getChildPortfolio().getAllBuildings()) {
      if (!b.getBuildingPaymentStatus().equals(BuildingPaymentStatus.UP_TO_DATE)) {
        return false;
      }
    }
    
    return hasUpToDatePaymentStatus;
  }
  
  private boolean hasCreatePeriodExpired() {
    
    if (!this.customerStatus.equals(CustomerStatus.CREATED)) {
      return false;
    }
    
    long currentTimeMillis = getTimeKeeper().getCurrentTimeInMillis();
    long customerStatusUpdatedAtMillis = getCustomerStatusUpdatedAt().getTime();
    
    long durationMillis = currentTimeMillis - customerStatusUpdatedAtMillis;
    
    long durationDays = TimeUnit.MILLISECONDS.toDays(durationMillis);
    if (durationDays >= 90) {
      return true;
    }
    
    return false;
  }
  
  /**
   * 
   * @return <code>true</code> if the eligible demo customer has been in a CREATED state for more than 90 days (i.e. expired)
   */
  public boolean shouldBeSoftDeleted() {
    
    if (this instanceof DemoCustomerEntity
        && ((DemoCustomerEntity)this).isExpires()
        && getCustomerStatus().equals(CustomerStatus.CREATED)
        && hasCreatePeriodExpired()
        && hasAllBuildingsInCreatedState()
        && hasUpToDatePaymentStatus()) {
      
      return true;
    }
    return false;
  }
  
  /**
   * 
   * @return <code>true</code> if the eligible demo customer has been in a soft deleted state for more than 30 days
   */
  public boolean shouldBeHardDeleted() {
    
    if (this instanceof DemoCustomerEntity
        && ((DemoCustomerEntity)this).isExpires()
        && getCustomerStatus().equals(CustomerStatus.DELETED) 
        && getNumberDaysSinceSoftDeleted() > 30
        && hasAllBuildingsInCreatedState()
        && hasUpToDatePaymentStatus()) {
      
      return true;
    }
    return false;
  }  
  
  public static class Mapper implements DtoMapper<AbstractDistributorEntity, AbstractCustomerEntity, CustomerDto> {

    private static final Mapper INSTANCE = new Mapper();

    private Mapper() {}

    public static Mapper getInstance() {
      return INSTANCE;
    }

    public List<CustomerDto> mapEntitiesToDtos(List<AbstractCustomerEntity> entities) {

      List<CustomerDto> list = new ArrayList<>();
      Iterator<AbstractCustomerEntity> iterator = entities.iterator();
      while (iterator.hasNext()) {

        AbstractCustomerEntity entity = iterator.next();
        if (!entity.getIsDeleted()) {
          list.add(mapEntityToDto(entity));
        }
      }
      return list;
    }

    @Override
    public CustomerDto mapEntityToDto(AbstractCustomerEntity entity) {

      CustomerDto dto = new CustomerDto();

      dto.setId(entity.getPersistentIdentity());
      dto.setDistributorId(entity.getParentDistributor().getPersistentIdentity());
      dto.setName(entity.getName());
      dto.setUuid(entity.getUuid());
      dto.setUnitSystem(entity.getUnitSystem().toString());
      dto.setCreatedAt(AbstractEntity.formatTimestamp(entity.getCreatedAt()));
      dto.setUpdatedAt(AbstractEntity.formatTimestamp(entity.getUpdatedAt()));
      dto.setStartDate(AbstractEntity.formatTimestamp(entity.getStartDate()));
      dto.setStatus(entity.getCustomerStatus().toString());
      dto.setStatusUpdatedAt(AbstractEntity.formatTimestamp(entity.getCustomerStatusUpdatedAt()));
      dto.setPaymentStatus(entity.getCustomerPaymentStatus().toString());
      dto.setPaymentStatusUpdatedAt(AbstractEntity.formatTimestamp(entity.getCustomerPaymentStatusUpdatedAt()));

      if (entity instanceof DemoCustomerEntity) {

        dto.setDemo(Boolean.TRUE);
        dto.setDemoExpires(((DemoCustomerEntity) entity).isExpires());
        dto.setInternal(((DemoCustomerEntity) entity).isInternal());

      } else if (entity instanceof OnlineCustomerEntity) {

        dto.setDemo(Boolean.FALSE);

      } else if (entity instanceof OutOfBandCustomerEntity) {

        dto.setDemo(Boolean.FALSE);

      } else {
        throw new RuntimeException("Unsupported customer type: ["
            + entity.getClassAndNaturalIdentity()
            + "] with id: ["
            + entity.getPersistentIdentity()
            + "]");
      }
      
      return dto;
    }

    @Override
    public AbstractCustomerEntity mapDtoToEntity(
        AbstractDistributorEntity rootDistributor,
        CustomerDto dto) {

      AbstractCustomerEntity customer = null;
      try {

        Integer parentDistributorId = dto.getDistributorId();
        AbstractDistributorEntity parentDistributor = rootDistributor.getDescendantDistributor(parentDistributorId);
        if (parentDistributor == null) {
          throw new RuntimeException("Could not find parent distributor with id: ["
              + parentDistributorId
              + "] for customer with id: ["
              + dto.getId()
              + "]");
        }        

        Boolean demo = dto.getDemo();
        if (demo != null && demo.equals(Boolean.TRUE)) {

          customer = new DemoCustomerEntity(
              dto.getId(),
              parentDistributor,
              dto.getName(),
              dto.getUuid(),
              UnitSystem.get(dto.getUnitSystem()),
              AbstractEntity.parseTimestamp(dto.getCreatedAt()),
              AbstractEntity.parseTimestamp(dto.getUpdatedAt()),
              AbstractEntity.parseTimestamp(dto.getStartDate()),
              CustomerStatus.valueOf(dto.getStatus()),
              AbstractEntity.parseTimestamp(dto.getStatusUpdatedAt()),
              CustomerPaymentStatus.valueOf(dto.getPaymentStatus()),
              AbstractEntity.parseTimestamp(dto.getPaymentStatusUpdatedAt()),
              dto.getDemoExpires(),
              dto.getInternal());

        } else if (parentDistributor instanceof OnlineDistributorEntity) {

          customer = new OnlineCustomerEntity(
              dto.getId(),
              parentDistributor,
              dto.getName(),
              dto.getUuid(),
              UnitSystem.get(dto.getUnitSystem()),
              AbstractEntity.parseTimestamp(dto.getCreatedAt()),
              AbstractEntity.parseTimestamp(dto.getUpdatedAt()),
              AbstractEntity.parseTimestamp(dto.getStartDate()),
              CustomerStatus.valueOf(dto.getStatus()),
              AbstractEntity.parseTimestamp(dto.getStatusUpdatedAt()),
              CustomerPaymentStatus.valueOf(dto.getPaymentStatus()),
              AbstractEntity.parseTimestamp(dto.getPaymentStatusUpdatedAt()));

        } else {

          customer = new OutOfBandCustomerEntity(
              dto.getId(),
              parentDistributor,
              dto.getName(),
              dto.getUuid(),
              UnitSystem.get(dto.getUnitSystem()),
              AbstractEntity.parseTimestamp(dto.getCreatedAt()),
              AbstractEntity.parseTimestamp(dto.getUpdatedAt()),
              AbstractEntity.parseTimestamp(dto.getStartDate()),
              CustomerStatus.valueOf(dto.getStatus()),
              AbstractEntity.parseTimestamp(dto.getStatusUpdatedAt()),
              CustomerPaymentStatus.valueOf(dto.getPaymentStatus()),
              AbstractEntity.parseTimestamp(dto.getPaymentStatusUpdatedAt()));
        }
        
        if (!parentDistributor.getChildCustomers().contains(customer)) {
          parentDistributor.addChildCustomer(customer);  
        }

      } catch (Exception e) {
        LOGGER.error("Error: ["
            + e.getMessage()
            + "], unable to map customer: ["
            + dto
            + "]", e);
      }
      return customer;
    }
  }
}
//@formatter:on