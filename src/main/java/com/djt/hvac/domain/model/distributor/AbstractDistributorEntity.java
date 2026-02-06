//@formatter:off
package com.djt.hvac.domain.model.distributor;

import static java.util.Objects.requireNonNull;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
import com.djt.hvac.domain.model.customer.AbstractCustomerEntity;
import com.djt.hvac.domain.model.customer.DemoCustomerEntity;
import com.djt.hvac.domain.model.customer.OnlineCustomerEntity;
import com.djt.hvac.domain.model.customer.enums.CustomerPaymentStatus;
import com.djt.hvac.domain.model.customer.enums.CustomerStatus;
import com.djt.hvac.domain.model.dictionary.enums.UnitSystem;
import com.djt.hvac.domain.model.distributor.dto.DistributorDto;
import com.djt.hvac.domain.model.distributor.enums.DistributorPaymentStatus;
import com.djt.hvac.domain.model.distributor.enums.DistributorStatus;
import com.djt.hvac.domain.model.distributor.enums.PaymentMethodType;
import com.djt.hvac.domain.model.distributor.paymentmethod.AbstractPaymentMethodEntity;
import com.djt.hvac.domain.model.distributor.paymentmethod.AchPaymentMethodEntity;
import com.djt.hvac.domain.model.distributor.paymentmethod.CreditCardPaymentMethodEntity;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BuildingEntity;
import com.djt.hvac.domain.model.referralagent.AbstractReferralAgentEntity;
import com.djt.hvac.domain.model.referralagent.InternalOrganizationalReferralAgentEntity;
import com.djt.hvac.domain.model.user.AbstractUserEntity;
import com.djt.hvac.domain.model.user.DistributorUserEntity;

public abstract class AbstractDistributorEntity extends AbstractPersistentEntity {
  private static final long serialVersionUID = 1L;
  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDistributorEntity.class);

  /**
   * The Root distributor is a special out-of-band distributor that is 
   * the parent/ancestor distributor of all other distributors.  It is the
   * only distributor that does not have a parent distributor. 
   */
  public static final Integer ROOT_DISTRIBUTOR_ID = Integer.valueOf(1);
  
  private AbstractDistributorEntity parentDistributor;
  private String name;
  private Timestamp createdAt = AbstractEntity.getTimeKeeper().getCurrentTimestamp();
  private Timestamp updatedAt = AbstractEntity.getTimeKeeper().getCurrentTimestamp();
  private String uuid = UUID.randomUUID().toString();
  private UnitSystem unitSystem = UnitSystem.IP;
  private Set<DistributorLevelPointTemplateUnitMappingOverrideEntity> pointTemplateUnitMappingOverrides = new HashSet<>();
  private AbstractReferralAgentEntity referralAgent = InternalOrganizationalReferralAgentEntity.buildResoluteReferralAgentStub();
  private DistributorStatus distributorStatus = DistributorStatus.CREATED;
  private Timestamp distributorStatusUpdatedAt = AbstractEntity.getTimeKeeper().getCurrentTimestamp();
  private DistributorPaymentStatus distributorPaymentStatus = DistributorPaymentStatus.UP_TO_DATE;
  private Timestamp distributorPaymentStatusUpdatedAt = AbstractEntity.getTimeKeeper().getCurrentTimestamp();
  private Set<AbstractDistributorEntity> childDistributors = new TreeSet<>();
  private Set<AbstractCustomerEntity> childCustomers = new TreeSet<>();
  private Set<DistributorUserEntity> childDistributorUsers = new TreeSet<>();

  // Used to specify the state of the distributor, as it was
  // loaded from the repository
  public boolean loadDistributorPaymentMethods;
  public boolean loadDistributorUsers;
  
  // For new instances (i.e. have not been persisted yet)
  public AbstractDistributorEntity(
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
  }
  
  public AbstractDistributorEntity(
      Integer persistentIdentity,
      AbstractDistributorEntity parentDistributor,
      String name,
      Timestamp createdAt,
      Timestamp updatedAt,
      String uuid,
      UnitSystem unitSystem,
      AbstractReferralAgentEntity referralAgent,
      DistributorStatus distributorStatus,
      Timestamp distributorStatusUpdatedAt,
      DistributorPaymentStatus distributorPaymentStatus,
      Timestamp distributorPaymentStatusUpdatedAt) {
    super(persistentIdentity);
    requireNonNull(name, "name cannot be null");
    requireNonNull(createdAt, "createdAt cannot be null");
    requireNonNull(updatedAt, "updatedAt cannot be null");
    requireNonNull(uuid, "uuid cannot be null");
    requireNonNull(unitSystem, "unitSystem cannot be null");
    requireNonNull(referralAgent, "referralAgent cannot be null");
    requireNonNull(distributorStatus, "distributorStatus cannot be null");
    requireNonNull(distributorStatusUpdatedAt, "distributorStatusUpdatedAt cannot be null");
    requireNonNull(distributorPaymentStatus, "distributorPaymentStatus cannot be null");
    requireNonNull(distributorPaymentStatus, "distributorPaymentStatusUpdatedAt cannot be null");
    this.parentDistributor = parentDistributor;
    this.name = name;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.uuid = uuid;
    this.unitSystem = unitSystem;
    this.referralAgent = referralAgent;
    this.distributorStatus = distributorStatus;
    this.distributorStatusUpdatedAt = distributorStatusUpdatedAt;
    this.distributorPaymentStatus = distributorPaymentStatus;
    this.distributorPaymentStatusUpdatedAt = distributorPaymentStatusUpdatedAt;
  }
  
  public Set<DistributorLevelPointTemplateUnitMappingOverrideEntity> getPointTemplateUnitMappingOverrides() {
    return pointTemplateUnitMappingOverrides;
  }

  public DistributorLevelPointTemplateUnitMappingOverrideEntity getPointTemplateUnitMappingOverride(Integer pointTemplateUnitMappingOverrideId) throws EntityDoesNotExistException{
    return getChild(DistributorLevelPointTemplateUnitMappingOverrideEntity.class, pointTemplateUnitMappingOverrides, pointTemplateUnitMappingOverrideId, this);
  }

  public boolean addPointTemplateUnitMappingOverride(DistributorLevelPointTemplateUnitMappingOverrideEntity pointTemplateUnitMappingOverride)
      throws EntityAlreadyExistsException {
    
    boolean result = addChild(pointTemplateUnitMappingOverrides, pointTemplateUnitMappingOverride, this);
    setIsModified("add:pointTemplateUnitMappingOverride");
    return result;
  }

  public void removePointTemplateUnitMappingOverride(Integer pointTemplateUnitMappingOverrideId)
      throws EntityDoesNotExistException {
    
    DistributorLevelPointTemplateUnitMappingOverrideEntity pointTemplateUnitMappingOverride = getPointTemplateUnitMappingOverride(pointTemplateUnitMappingOverrideId);
    pointTemplateUnitMappingOverride.setIsDeleted();
    setIsModified("remove:pointTemplateUnitMappingOverride");
  }
  
  public void removeAllPointTemplateUnitMappingOverrides() {
    
    for (DistributorLevelPointTemplateUnitMappingOverrideEntity override: pointTemplateUnitMappingOverrides) {

      override.setIsDeleted();
      setIsModified("remove:pointTemplateUnitMappingOverride");
    }
  }
  
  public Set<AbstractDistributorEntity> getChildDistributors() {
    return childDistributors;
  }
  
  public AbstractDistributorEntity getDescendantDistributorNullIfNotExists(Integer distributorId) {
    
    if (this.getPersistentIdentity().equals(distributorId)) {
      
      return this;
    }
    
    for (AbstractDistributorEntity d: childDistributors) {
      
      AbstractDistributorEntity descendantDistributor = d.getDescendantDistributorNullIfNotExists(distributorId);
      if (descendantDistributor != null) {

        return descendantDistributor;
      }
    }
    
    return null;
  }

  public List<AbstractDistributorEntity> getAllDescendantDistributors() {
    
    List<AbstractDistributorEntity> list = new ArrayList<>();
    
    list.add(this);
    
    for (AbstractDistributorEntity d: childDistributors) {
      
      list.addAll(d.getAllDescendantDistributors());
    }
    
    return list;
  }
  
  public boolean addChildDistributor(AbstractDistributorEntity distributor)
      throws EntityAlreadyExistsException {
    return addChild(childDistributors, distributor, this);
  }

  public AbstractDistributorEntity getDescendantDistributor(Integer persistentIdentity)
      throws EntityDoesNotExistException {
    
    AbstractDistributorEntity descendantDistributor = getDescendantDistributorNullIfNotExists(persistentIdentity);
    if (descendantDistributor == null) {
      throw new EntityDoesNotExistException("Descendant distributor with id: [" + persistentIdentity + "] not found.");
    }
    return descendantDistributor;
  }

  public AbstractDistributorEntity getChildDistributor(Integer persistentIdentity) throws EntityDoesNotExistException {
    return getChild(AbstractDistributorEntity.class, childDistributors, persistentIdentity, this);
  }
  
  public AbstractDistributorEntity removeChildDistributor(Integer persistentIdentity)
      throws EntityDoesNotExistException {
    
    AbstractDistributorEntity childDistributor = getChildDistributor(persistentIdentity);
    childDistributor.setIsDeleted();
    return childDistributor;
  }

  public void removeChildDistributor(AbstractDistributorEntity childDistributor) {
    
    childDistributor.setIsDeleted();
  }
  
  @Override
  public void setIsModified(String modifiedAttributeName) {
    
    if (!modifiedAttributeName.endsWith("DistributorEntity")) {
      super.setIsModified(modifiedAttributeName);
    }
  }  
  
  public void resetAllIsModified() {
  
    for (AbstractCustomerEntity c: childCustomers) {
      c.setNotModified();
    }

    for (AbstractDistributorEntity d: childDistributors) {
      d.setNotModified();
    }
    setNotModified();
  }
  
  public Set<AbstractCustomerEntity> getChildCustomers() {
    return childCustomers;
  }
  
  public AbstractCustomerEntity getDescendantCustomer(Integer customerId) throws EntityDoesNotExistException {
    
    AbstractCustomerEntity c = getDescendantCustomerNullIfNotExists(customerId);
    if (c != null) {
      return c;
    }
    throw new EntityDoesNotExistException("Descendant customer with id: [" + customerId + "] not found.");
  }
  
  public AbstractCustomerEntity getDescendantCustomerNullIfNotExists(Integer customerId) {
    
    for (AbstractCustomerEntity c: childCustomers) {
      
      if (c.getPersistentIdentity().equals(customerId)) {
        
        return c;
      }
    }

    for (AbstractDistributorEntity d: childDistributors) {
      
      AbstractCustomerEntity c = d.getDescendantCustomerNullIfNotExists(customerId);
      if (c != null) {
        return c;
      }
    }
    
    return null;
  }
  
  
  
  
  public BuildingEntity getDescendantBuildingNullIfNotExists(Integer buildingId) {
    
    BuildingEntity building = null;
    for (AbstractCustomerEntity c: childCustomers) {
      
      building = c.getChildPortfolio().getBuildingNullIfNotExists(buildingId);
      if (buildingId != null) {
        return building;
      }
    }

    for (AbstractDistributorEntity d: childDistributors) {
      for (AbstractCustomerEntity c: d.getChildCustomers()) {
        
        building = c.getChildPortfolio().getBuildingNullIfNotExists(buildingId);
        if (buildingId != null) {
          return building;
        }
      }
    }
    
    return null;
  }
  
  public BuildingEntity getDescendantBuilding(Integer buildingId) throws EntityDoesNotExistException {
    
    BuildingEntity b = getDescendantBuildingNullIfNotExists(buildingId);
    if (b != null) {
      return b;
    }

    throw new EntityDoesNotExistException("Descendant building with id: [" 
        + buildingId 
        + "] not found in distributor: " 
        + this 
        + "].");
  }  
  
  public Set<AbstractUserEntity> getAllDescendantUsers() {
    
    Set<AbstractUserEntity> users = new TreeSet<>();
    
    users.addAll(getChildDistributorUsers());
    
    for (AbstractCustomerEntity c: childCustomers) {
      users.addAll(c.getChildCustomerUsers());
    }

    for (AbstractDistributorEntity d: childDistributors) {
      users.addAll(d.getAllDescendantUsers());
    }    
    
    return users;
  }
  
  public boolean addChildCustomer(AbstractCustomerEntity customer)
      throws EntityAlreadyExistsException {
    return addChild(childCustomers, customer, this);
  }

  public AbstractCustomerEntity getChildCustomer(Integer persistentIdentity)
      throws EntityDoesNotExistException {
    return getChild(AbstractCustomerEntity.class, childCustomers, persistentIdentity, this);
  }

  public AbstractCustomerEntity removeChildCustomer(Integer persistentIdentity)
      throws EntityDoesNotExistException {

    AbstractCustomerEntity childCustomer = getChildCustomer(persistentIdentity);
    childCustomer.setIsDeleted();
    return childCustomer;
  }

  public void removeChildCustomer(AbstractCustomerEntity childCustomer) {
    
    childCustomer.setIsDeleted();
  }
  
  public Set<DistributorUserEntity> getChildDistributorUsers() {
    return childDistributorUsers;
  }

  public boolean addChildDistributorUser(DistributorUserEntity distributorUser)
      throws EntityAlreadyExistsException {
    return addChild(childDistributorUsers, distributorUser, this);
  }

  public DistributorUserEntity getChildDistributorUser(Integer persistentIdentity)
      throws EntityDoesNotExistException {
    return getChild(DistributorUserEntity.class, childDistributorUsers, persistentIdentity, this);
  }

  public DistributorUserEntity getChildDistributorUser(String email)
      throws EntityDoesNotExistException {
    
    DistributorUserEntity user = getChildDistributorUserNullIfNotExists(email);
    if (user != null) {
      return user;
    }
    throw new EntityDoesNotExistException("Distributor: ["
        + this
        + "] does not have user with email: ["
        + email
        + "].");
  }
  
  public DistributorUserEntity getChildDistributorUserNullIfNotExists(String email) {
    
    for (DistributorUserEntity distributorUser: childDistributorUsers) {
      
      if (distributorUser.getEmail().equals(email)) {
        return distributorUser;
      }
    }
    return null;
  }
  
  public DistributorUserEntity removeChildDistributorUser(Integer persistentIdentity)
      throws EntityDoesNotExistException {
    
    DistributorUserEntity childUser = getChildDistributorUser(persistentIdentity);
    childUser.setIsDeleted();
    return childUser;
  }  
  
  public DistributorUserEntity getAccountManagerDistributorUser() throws EntityDoesNotExistException {
    
    DistributorUserEntity distributorUser = getAccountManagerDistributorUserNullIfNotExists();
    if (distributorUser == null) {
      
      throw new EntityDoesNotExistException("Distributor: ["
          + this
          + "] does not have an account manager.");
    }
    return distributorUser;
  }  

  public DistributorUserEntity getAccountManagerDistributorUserNullIfNotExists() {
    
    for (DistributorUserEntity distributorUser: childDistributorUsers) {
      
      if (distributorUser.isAccountManager().booleanValue()) {
        return distributorUser;
      }
    }
    return null;
  }  
  
  public AbstractDistributorEntity getParentDistributor() {
    return parentDistributor;
  }

  public String getName() {
    return name;
  }

  public Timestamp getCreatedAt() {
    return createdAt;
  }

  public Timestamp getUpdatedAt() {
    return updatedAt;
  }

  public String getUuid() {
    return uuid;
  }
  
  public UnitSystem getUnitSystem() {
    return unitSystem;
  }

  public void setUnitSystem(UnitSystem unitSystem) {
    
    if ((this.unitSystem == null && unitSystem != null) || (this.unitSystem != null && unitSystem == null)) {
      
      this.unitSystem = unitSystem;
      setIsModified("unitSystem");
      
    } else if (this.unitSystem != null && unitSystem != null) {
      
      if (!this.unitSystem.equals(unitSystem)) {

        this.unitSystem = unitSystem;
        setIsModified("unitSystem");
        
      }
    }
    
    if (this.unitSystem != null && this.unitSystem.equals(UnitSystem.IP)) {
      
      removeAllPointTemplateUnitMappingOverrides();
    }
  }    

  public AbstractReferralAgentEntity getReferralAgent() {
    return referralAgent;
  }

  public DistributorStatus getDistributorStatus() {
    return distributorStatus;
  }
  
  public Timestamp getDistributorStatusUpdatedAt() {
    return distributorStatusUpdatedAt;
  }

  public void setDistributorStatus(DistributorStatus distributorStatus) {
    
    if (distributorStatus.equals(DistributorStatus.DELETED)) {
      
      LOGGER.info("Distributor: {} has been in the CREATED state for more than 90 days, so is automatically being soft deleted as of: {}",
          this,
          AbstractEntity.getTimeKeeper().getCurrentLocalDate());
      
    } else if (distributorStatus.equals(DistributorStatus.CREATED)) {
      
      LOGGER.info("Distributor: {} has been moved into the CREATED state as of: {} and will be soft deleted in 90 days",
          this,
          AbstractEntity.getTimeKeeper().getCurrentLocalDate());
    }
    
    this.distributorStatus = distributorStatus;
    this.distributorStatusUpdatedAt = AbstractEntity.getTimeKeeper().getCurrentTimestamp();
    this.setIsModified("distributorStatus");
  }
  
  public DistributorPaymentStatus getDistributorPaymentStatus() {
    return distributorPaymentStatus;
  }

  public Timestamp getDistributorPaymentStatusUpdatedAt() {
    return distributorPaymentStatusUpdatedAt;
  }
  
  public void setDistributorPaymentStatus(DistributorPaymentStatus newDistributorPaymentStatus) {
    
    if (newDistributorPaymentStatus.equals(this.distributorPaymentStatus)) {
      return;
    }
    
    this.distributorPaymentStatus = newDistributorPaymentStatus;
    this.distributorPaymentStatusUpdatedAt = AbstractEntity.getTimeKeeper().getCurrentTimestamp();
    this.setIsModified("distributorPaymentStatus");
  }
  
  @Override
  public String getNaturalIdentity() {

    if (this.parentDistributor == null) {
      return name;
    }
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
      boolean remediate) {

    for (AbstractDistributorEntity distributor : childDistributors) {
      distributor.validate(issueTypes, validationMessages, remediate);
    }

    for (AbstractCustomerEntity customer : childCustomers) {
      if (customer.isActive()) {
        customer.validate(issueTypes, validationMessages, remediate);
      }
    }
  }

  // This is convenience method relating to the concrete sub-class type.
  public abstract String getDistributorTypeDescription();

  @Override
  public void evaluateState() {
    
    // NOTE:
    // Online and OutOfBand distributors are evaluated differently, 
    // see the subclass implementations.    

    // Evaluates all descendant billable customers for their config status, 
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

    for (AbstractDistributorEntity childDistributor: getChildDistributors()) {
      
      childDistributor.evaluateConfigState();
      
      if (childDistributor instanceof OnlineDistributorEntity) {

        OnlineDistributorEntity onlineDistributor = (OnlineDistributorEntity)childDistributor;
        
        // If the distributor has been in the CREATED state for 90 days, then 
        // we automatically change to be in the DELETED state (i.e. "soft deleted").
        if (onlineDistributor.shouldBeSoftDeleted()) {

          onlineDistributor.setDistributorStatus(DistributorStatus.DELETED);

          LOGGER.info("{}: {}: child distributor state is being changed CREATED --> DELETED (i.e. soft deleted) as it has been CREATED for 90 days as of: [{}].",
              AbstractEntity.getTimeKeeper().getCurrentLocalDate(),
              onlineDistributor.getName(),
              onlineDistributor.getCreatePeriodEndTimestamp());
        }
      }
    }    
    
    int numBillableChildCustomers = 0;
    for (AbstractCustomerEntity childCustomer: getChildCustomers()) {
      
      childCustomer.evaluateConfigState();
      
      CustomerStatus customerStatus = childCustomer.getCustomerStatus();
      if (customerStatus.equals(CustomerStatus.BILLABLE)) {
        
        numBillableChildCustomers++;
      }
    }
    
    // See if we need to make any changes to the customer based upon the state of the child customers, which
    // for the config state, is solely based upon the number of ACTIVE billable buildings.
    if (numBillableChildCustomers == 0 && distributorStatus.equals(DistributorStatus.BILLABLE)) {
      
      setDistributorStatus(DistributorStatus.CREATED);
      LOGGER.info(
          AbstractEntity.getTimeKeeper().getCurrentLocalDate()
          + ": "
          + name 
          + ": distributor config status: BILLABLE --> CREATED: numBillableChildCustomers: ["
          + numBillableChildCustomers
          + "]");
      
    } else if (numBillableChildCustomers > 0 && distributorStatus.equals(DistributorStatus.CREATED)) {
      
      setDistributorStatus(DistributorStatus.BILLABLE);
      LOGGER.info(
          AbstractEntity.getTimeKeeper().getCurrentLocalDate()
          + ": "
          + name
          + ": distributor config status: CREATED --> BILLABLE: numBillableChildCustomers: ["
          + numBillableChildCustomers
          + "]");
    } else if (distributorStatus.equals(DistributorStatus.CREATED) 
        && this instanceof OnlineDistributorEntity
        && ((OnlineDistributorEntity)this).shouldBeSoftDeleted()) {
      
      setDistributorStatus(DistributorStatus.DELETED);
      
      LOGGER.info(
          AbstractEntity.getTimeKeeper().getCurrentLocalDate()
          + ": "
          + name
          + ": this state is being changed to CREATED --> DELETED (i.e. soft deleted) as it has been CREATED for 90 days as of: ["
          + ((OnlineDistributorEntity)this).getCreatePeriodEndTimestamp()
          + "].");
    }
  }    
  
  public void evaluatePaymentState() {

    int numDelinquentChildDistributors = 0;
    for (AbstractDistributorEntity childDistributor: getChildDistributors()) {
      
      if (childDistributor instanceof OnlineDistributorEntity) {
        
        childDistributor.evaluatePaymentState();
        
        if (childDistributor.getDistributorPaymentStatus().equals(DistributorPaymentStatus.DELINQUENT)) {
          
          numDelinquentChildDistributors++;
        }
      }
    }
    
    int numDelinquentChildCustomers = 0;
    for (AbstractCustomerEntity childCustomer: getChildCustomers()) {
      
      if (childCustomer instanceof OnlineCustomerEntity) {
        
        childCustomer.evaluatePaymentState();
        
        if (childCustomer.getCustomerPaymentStatus().equals(CustomerPaymentStatus.DELINQUENT)) {
          
          numDelinquentChildCustomers++;
        }
      }
    }

    // If a distributor has at least one DELINQUENT child distributor or customer, 
    // then the parent distributor will be DELINQUENT as well.
    if ((numDelinquentChildDistributors > 0 || numDelinquentChildCustomers > 0)
        && distributorPaymentStatus.equals(DistributorPaymentStatus.UP_TO_DATE)) {
      
      setDistributorPaymentStatus(DistributorPaymentStatus.DELINQUENT);
      
      LOGGER.info(
          AbstractEntity.getTimeKeeper().getCurrentLocalDate()
          + ": "
          + name
          + ": distributor payment status: UP_TO_DATE --> DELINQUENT: numDelinquentChildDistributors: ["
          + numDelinquentChildDistributors
          + "], numDelinquentChildCustomers: ["
          + numDelinquentChildCustomers
          + "], current time: ["
          + AbstractEntity.getTimeKeeper().getCurrentTimestamp()
          + "].");
      
    } else if (numDelinquentChildDistributors == 0 
        && numDelinquentChildCustomers == 0
        && distributorPaymentStatus.equals(DistributorPaymentStatus.DELINQUENT)) {
      
      setDistributorPaymentStatus(DistributorPaymentStatus.UP_TO_DATE);
      
      LOGGER.info(
          AbstractEntity.getTimeKeeper().getCurrentLocalDate()
          + ": "
          + name
          + ": distributor payment status: DELINQUENT --> UP_TO_DATE: numDelinquentChildDistributors: ["
          + numDelinquentChildDistributors
          + "], numDelinquentChildCustomers: ["
          + numDelinquentChildCustomers
          + "].");
      
    }
    
    if (distributorPaymentStatus.equals(DistributorPaymentStatus.DELINQUENT)) {
      
      // NOTE: The DELINQUENT to PAST_DUE state transition can occur in one of two ways:
      // 1: out of band distributor: the CFO instructs the developers to mark it as PAST_DUE
      // 
      // 2: online distributor: If it has been DELINQUENT for 90 days or more, then it is automatically
      // moved into the PAST_DUE state.
      long numberDaysInDelinquentState = ((OnlineDistributorEntity)this).getNumberDaysInDelinquentState();
      if (this instanceof OnlineDistributorEntity && numberDaysInDelinquentState >= 90) {
        
        setDistributorPaymentStatus(DistributorPaymentStatus.PAST_DUE);
        LOGGER.info(
            AbstractEntity.getTimeKeeper().getCurrentLocalDate()
            + ": "
            + this
            + ": distributor payment status: DELINQUENT --> PAST_DUE: last payment status updated at: ["
            + distributorPaymentStatusUpdatedAt
            + "] current time: ["
            + AbstractEntity.getTimeKeeper().getCurrentTimestamp()
            + "], number days being DELINQUENT: ["
            + numberDaysInDelinquentState
            + "]");
      }
      
    }
  }
  
  public void evaluatePendingDeletionState() {
    
    for (AbstractDistributorEntity childDistributor: getChildDistributors()) {
      
      childDistributor.evaluatePendingDeletionState();
      
      if (childDistributor instanceof OnlineDistributorEntity) {

        OnlineDistributorEntity onlineDistributor = (OnlineDistributorEntity)childDistributor;
        
        // See if we need to hard delete the distributor (if DELETED state for 30 days)
        if (!onlineDistributor.getIsDeleted() && onlineDistributor.shouldBeHardDeleted()) {

          LOGGER.info(
              AbstractEntity.getTimeKeeper().getCurrentLocalDate()
              + ": "
              + onlineDistributor.getName()
              + ": is being hard deleted as it has been: ["
              + onlineDistributor.getNumberDaysSinceSoftDeletePeriodEnd()
              + "] days as of: ["
              + onlineDistributor.getSoftDeletePeriodEnd()
              + "] since it was soft deleted at: ["
              + onlineDistributor.getDistributorStatusUpdatedAt()
              + "].");
          
          onlineDistributor.setIsDeleted();
          
        }
      }
    }
    
    for (AbstractCustomerEntity childCustomer: getChildCustomers()) {
      
      childCustomer.evaluatePendingDeletionState();
      
      if (childCustomer instanceof OnlineCustomerEntity) {

        OnlineCustomerEntity onlineCustomer = (OnlineCustomerEntity)childCustomer;
        
        // See if we need to hard delete the customer (if DELETED state for 30 days)
        if (!onlineCustomer.getIsDeleted() && onlineCustomer.shouldBeHardDeleted()) {

          LOGGER.info(
              AbstractEntity.getTimeKeeper().getCurrentLocalDate()
              + ": "
              + onlineCustomer.getName()
              + ": is being hard deleted as it has been: ["
              + onlineCustomer.getNumberDaysSinceSoftDeletePeriodEnd()
              + "] days as of: ["
              + onlineCustomer.getSoftDeletePeriodEnd()
              + "] since it was soft deleted at: ["
              + onlineCustomer.getCustomerStatusUpdatedAt()
              + "].");
          
          onlineCustomer.setIsDeleted();
          
        }
      }
    }

    // Evaluate ourselves
    if (this instanceof OnlineDistributorEntity) {
      
      OnlineDistributorEntity thisDistributor = (OnlineDistributorEntity)this;

      if (!thisDistributor.getIsDeleted() && thisDistributor.shouldBeHardDeleted()) {

        LOGGER.info(
            AbstractEntity.getTimeKeeper().getCurrentLocalDate()
            + ": "
            + thisDistributor.getName()
            + ": is being hard deleted as it has been: ["
            + thisDistributor.getNumberDaysSinceSoftDeletePeriodEnd()
            + "] days as of: ["
            + thisDistributor.getSoftDeletePeriodEnd()
            + "] since it was soft deleted at: ["
            + thisDistributor.getDistributorStatusUpdatedAt()
            + "].");
        
        thisDistributor.setIsDeleted();
      }
    }
  }

  public Set<PortfolioEntity> getModifiedChildPortfolios() {
    
    Set<PortfolioEntity> modifiedChildPortfolios = new TreeSet<>();
    for (AbstractCustomerEntity childCustomer: childCustomers) {
      
      PortfolioEntity childPortfolio = childCustomer.getChildPortfolio();
      if (childPortfolio != null && childPortfolio.getIsModified()) {
        modifiedChildPortfolios.add(childPortfolio);
      }
    }
    return modifiedChildPortfolios;
  }  
  
  public Set<AbstractCustomerEntity> getModifiedChildCustomers() {
    
    Set<AbstractCustomerEntity> modifiedChildCustomers = new TreeSet<>();
    for (AbstractCustomerEntity childCustomer: childCustomers) {
      
      if (!childCustomer.getIsDeleted() && childCustomer.getIsModified()) {
        modifiedChildCustomers.add(childCustomer);
      }
    }
    return modifiedChildCustomers;
  }

  public Set<AbstractCustomerEntity> getHardDeletedChildCustomers() {
    
    Set<AbstractCustomerEntity> hardDeletedChildCustomers = new TreeSet<>();
    for (AbstractCustomerEntity childCustomer: childCustomers) {
      
      if (childCustomer.getIsDeleted()) {
        hardDeletedChildCustomers.add(childCustomer);
      }
    }
    return hardDeletedChildCustomers;
  }
  
  public Set<AbstractDistributorEntity> getModifiedChildDistributors() {
    
    Set<AbstractDistributorEntity> modifiedChildDistributors = new TreeSet<>();
    for (AbstractDistributorEntity childDistributor: childDistributors) {
      
      if (!childDistributor.getIsDeleted() && childDistributor.getIsModified()) {
        modifiedChildDistributors.add(childDistributor);
      }
    }
    return modifiedChildDistributors;
  } 

  public Set<AbstractDistributorEntity> getHardDeletedChildDistributors() {
    
    Set<AbstractDistributorEntity> hardDeletedChildDistributors = new TreeSet<>();
    for (AbstractDistributorEntity childDistributor: childDistributors) {
      
      if (childDistributor.getIsDeleted()) {
        hardDeletedChildDistributors.add(childDistributor);
      }
    }
    return hardDeletedChildDistributors;
  }
  
  public AbstractDistributorEntity getNonResoluteRootDistributor() {
    
    if (this.getParentDistributor() == null) {
      throw new IllegalStateException("Cannot get non Resolute root distributor on the Resolute root distributor itself.");
    }
    
    if (getParentDistributor().getPersistentIdentity().equals(ROOT_DISTRIBUTOR_ID)) {
      return this;
    }
    
    AbstractDistributorEntity distributor = this.parentDistributor;
    while (!distributor.getParentDistributor().getPersistentIdentity().equals(ROOT_DISTRIBUTOR_ID)) {
      
      distributor = distributor.getParentDistributor();
    }
    return distributor;
  }
  
  public AbstractDistributorEntity getRootDistributor() {
    
    if (this.parentDistributor == null) {
      return this;
    }
    
    AbstractDistributorEntity distributor = this.parentDistributor;
    while (distributor.getParentDistributor() != null) {
      distributor = distributor.getParentDistributor();
    }
    return distributor;
  }
  
  public DistributorUserEntity changeDistributorAccountManager(Integer newAccountManagerId) throws EntityDoesNotExistException {

    DistributorUserEntity currentAccountManager = this.getAccountManagerDistributorUserNullIfNotExists();
    if (currentAccountManager != null) {
      
      currentAccountManager.setAccountManager(Boolean.FALSE);  
    }
    
    DistributorUserEntity newAccountManager = getChildDistributorUser(newAccountManagerId);
    newAccountManager.setAccountManager(Boolean.TRUE);
    
    return currentAccountManager;
  }
  
  private boolean hasCreatePeriodExpired() {
    
    long currentTimeMillis = getTimeKeeper().getCurrentTimeInMillis();
    long distributorStatusUpdatedAtMillis = getDistributorStatusUpdatedAt().getTime();
    
    long durationMillis = currentTimeMillis - distributorStatusUpdatedAtMillis;
    
    long durationDays = TimeUnit.MILLISECONDS.toDays(durationMillis);
    if (durationDays >= 90) {
      
      return true;
    }    
    return false;
  }
  
  private boolean hasDelinquentOrPastDueDescendantDistributors() {
    
    boolean hasDelinquentOrPastDueDescendantDistributors = false;
   
    for (AbstractDistributorEntity d: getAllDescendantDistributors()) {
      if (!d.getDistributorPaymentStatus().equals(DistributorPaymentStatus.UP_TO_DATE)) {
        
        return true;
      }
    }
    
    return hasDelinquentOrPastDueDescendantDistributors;
  }
  
  private boolean hasNonSoftDeletedDescendantDistributors() {
    
    boolean hasNonSoftDeletedDescendantDistributors = false;

    for (AbstractDistributorEntity d: getAllDescendantDistributors()) {
      if (!d.equals(this) && !d.getDistributorStatus().equals(DistributorStatus.DELETED)) {
        
        return true;
      }
    }
    
    return hasNonSoftDeletedDescendantDistributors;
  }
  
  private boolean hasNonSoftDeletedDescendantCustomers() {
   
    boolean hasNonSoftDeletedDescendantCustomers = false;
    
    for (AbstractDistributorEntity d: getAllDescendantDistributors()) {
      for (AbstractCustomerEntity c: d.getChildCustomers()) {
        if (!c.getCustomerStatus().equals(CustomerStatus.DELETED)) {
          
          return true;
        }
      }
    }
    
    for (AbstractCustomerEntity c: getChildCustomers()) {
      if (!c.getCustomerStatus().equals(CustomerStatus.DELETED)) {
        
        return true;
      }
    }    
    
    return hasNonSoftDeletedDescendantCustomers;    
  }
  
  private boolean hasNonExpiringDescendantDemoCustomers() {
    
    boolean hasNonExpiringDescendantCustomers = false;
    
    for (AbstractDistributorEntity d: getAllDescendantDistributors()) {
      for (AbstractCustomerEntity c: d.getChildCustomers()) {
        if (c instanceof DemoCustomerEntity && ((DemoCustomerEntity)c).isExpires() == false) {
          
          return true;
        }
      }
    }
    
    for (AbstractCustomerEntity c: getChildCustomers()) {
      if (c instanceof DemoCustomerEntity && ((DemoCustomerEntity)c).isExpires() == false) {
        
        return true;
      }
    }    
    
    return hasNonExpiringDescendantCustomers;
  }
  
  private int getNumberDaysSinceSoftDeletePeriodEnd() {

    LocalDate currentLocalDate = AbstractEntity.getTimeKeeper().getCurrentLocalDate();
    
    LocalDate statusUpdatedAt = getDistributorStatusUpdatedAt()
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
  
  /**
   * 
   * @return <code>true</code> if the distributor has been in the created state for greater than 90 days
   *  and it has allow out of band buildings = false
   */
  public boolean shouldBeSoftDeleted() {
    
    /* 
     * ONLINE DISTRIBUTORS/CUSTOMERS ONLY:
     * 
     * We automatically move the online distributor to the DELETED state if it has been in the
     * CREATED state for 90 days AND allow out of band buildings is false.  
     * 
     * EXCEPTIONS:
     * 1: Don’t soft delete if a distributor has a descendant building that is in the 
     *    PENDING_ACTIVATION state.
     *       
     * 2: Don't soft delete if a distributor has a payment_status of DELINQUENT or PAST_DUE. 
     *       
     * 3: Don't soft delete a distributor if it has a descendant distributor that is not in
     *    the soft deleted state.
     *    
     * 4. Don't soft delete a distributor if it has a demo customer that has demo_expires=false. 
     *    
     * NOTES:
     * With regard to hard deletion:
     *   1: If none of the above exceptions apply, then only demo customers that are expired
     *      are first soft deleted after 90 days and then hard deleted 30 days after that. 
     *      
     *   2: Non-demo distributors/customers are NEVER automatically hard deleted, rather, they
     *      are only hard deleted after a quarterly review by management/sales in which it is 
     *      agreed upon that they should be hard deleted. When this is done, the archival process
     *      will run and then an API endpoint will be called to hard delete in the PROD DB.
     */
    if (this instanceof OnlineDistributorEntity) {

      DistributorStatus distributorStatus = getDistributorStatus();
      if (distributorStatus.equals(DistributorStatus.CREATED) 
          && hasCreatePeriodExpired()
          && !((OnlineDistributorEntity)this).getAllowOutOfBandBuildings() 
          && !hasDelinquentOrPastDueDescendantDistributors()
          && !hasNonSoftDeletedDescendantDistributors()
          && !hasNonSoftDeletedDescendantCustomers()
          && !hasNonExpiringDescendantDemoCustomers()) {

        return true;
      }
    }
    return false;
  }
  
  /**
   * 
   * @return <code>true</code> if the online distributor has been in a soft deleted state for more than 30 days
   */
  public boolean shouldBeHardDeleted() {

    if (!getIsDeleted()
        && getDistributorStatus().equals(DistributorStatus.DELETED) 
        && getNumberDaysSinceSoftDeletePeriodEnd() > 30
        && !hasNonSoftDeletedDescendantDistributors()
        && !hasNonSoftDeletedDescendantCustomers()
        && !hasNonExpiringDescendantDemoCustomers()) {
      
      return true;
    }
    return false;
  }  
  
  public static class Mapper
      implements DtoMapper<AbstractDistributorEntity, AbstractDistributorEntity, DistributorDto> {

    private static final Mapper INSTANCE = new Mapper();

    private Mapper() {}

    public static Mapper getInstance() {
      return INSTANCE;
    }

    public List<DistributorDto> mapEntitiesToDtos(List<AbstractDistributorEntity> entities) {

      List<DistributorDto> list = new ArrayList<>();
      Iterator<AbstractDistributorEntity> iterator = entities.iterator();
      while (iterator.hasNext()) {

        AbstractDistributorEntity entity = iterator.next();
        if (!entity.getIsDeleted()) {
          list.add(mapEntityToDto(entity));
        }
      }
      return list;
    }

    @Override
    public DistributorDto mapEntityToDto(AbstractDistributorEntity e) {

      DistributorDto dto = new DistributorDto();

      dto.setId(e.getPersistentIdentity());
      AbstractDistributorEntity parentDistributor = e.getParentDistributor();
      if (parentDistributor != null) {
        dto.setParentId(parentDistributor.getPersistentIdentity());
      }
      dto.setName(e.getName());
      dto.setCreatedAt(AbstractEntity.formatTimestamp(e.getCreatedAt()));
      dto.setUpdatedAt(AbstractEntity.formatTimestamp(e.getUpdatedAt()));
      dto.setUuid(e.getUuid());
      dto.setUnitSystem(e.getUnitSystem().toString());
      dto.setReferralAgentId(e.getReferralAgent().getPersistentIdentity());
      dto.setStatus(e.getDistributorStatus().name());
      dto.setStatusUpdatedAt(AbstractEntity.formatTimestamp(e.getDistributorStatusUpdatedAt()));
      dto.setPaymentStatus(e.getDistributorPaymentStatus().toString());
      dto.setPaymentStatusUpdatedAt(AbstractEntity.formatTimestamp(e.getDistributorPaymentStatusUpdatedAt()));
      
      dto.setType(e.getDistributorTypeDescription());

      if (e instanceof OnlineDistributorEntity) {

        OnlineDistributorEntity ode = (OnlineDistributorEntity) e;

        dto.setStripeCustomerId(ode.getStripeCustomerId());
        dto.setAllowOutOfBandBuildings(ode.getAllowOutOfBandBuildings());

        StringBuilder paymentMethodIdSb = new StringBuilder("{");
        StringBuilder paymentMethodTypeSb = new StringBuilder("{");
        StringBuilder paymentMethodNameSb = new StringBuilder("{");
        StringBuilder paymentMethodStripeSourceIdSb = new StringBuilder("{");
        StringBuilder paymentMethodAccountHolderNameSb = new StringBuilder("{");
        StringBuilder paymentMethodAddressSb = new StringBuilder("{");
        StringBuilder paymentMethodCitySb = new StringBuilder("{");
        StringBuilder paymentMethodStateSb = new StringBuilder("{");
        StringBuilder paymentMethodZipCodeSb = new StringBuilder("{");
        StringBuilder paymentMethodPhoneNumberSb = new StringBuilder("{");
        StringBuilder paymentMethodCardBrandSb = new StringBuilder("{");
        StringBuilder paymentMethodCardExpirySb = new StringBuilder("{");
        StringBuilder paymentMethodCardLastFourSb = new StringBuilder("{");

        Iterator<AbstractPaymentMethodEntity> paymentMethodIterator =
            ode.getChildPaymentMethods().iterator();
        while (paymentMethodIterator.hasNext()) {

          AbstractPaymentMethodEntity paymentMethod = paymentMethodIterator.next();

          paymentMethodIdSb.append(paymentMethod.getPersistentIdentity());
          paymentMethodTypeSb.append(paymentMethod.getPaymentMethodType().getName());
          paymentMethodNameSb.append(paymentMethod.getName());
          paymentMethodStripeSourceIdSb.append(paymentMethod.getStripeSourceId());
          paymentMethodAccountHolderNameSb.append(paymentMethod.getAccountHolderName());
          paymentMethodAddressSb.append(paymentMethod.getAddress());
          paymentMethodCitySb.append(paymentMethod.getCity());
          paymentMethodStateSb.append(paymentMethod.getState());
          paymentMethodZipCodeSb.append(paymentMethod.getZipCode());
          paymentMethodPhoneNumberSb.append(paymentMethod.getPhoneNumber());

          if (paymentMethod instanceof CreditCardPaymentMethodEntity) {

            CreditCardPaymentMethodEntity creditCardPaymentMethod =
                (CreditCardPaymentMethodEntity) paymentMethod;

            paymentMethodCardBrandSb.append(creditCardPaymentMethod.getBrand());
            paymentMethodCardExpirySb.append(creditCardPaymentMethod.getExpiry());
            paymentMethodCardLastFourSb.append(creditCardPaymentMethod.getLastFour());
          }

          if (paymentMethodIterator.hasNext()) {

            paymentMethodIdSb.append(",");
            paymentMethodTypeSb.append(",");
            paymentMethodNameSb.append(",");
            paymentMethodStripeSourceIdSb.append(",");
            paymentMethodAccountHolderNameSb.append(",");
            paymentMethodAddressSb.append(",");
            paymentMethodCitySb.append(",");
            paymentMethodStateSb.append(",");
            paymentMethodZipCodeSb.append(",");
            paymentMethodPhoneNumberSb.append(",");
            paymentMethodCardBrandSb.append(",");
            paymentMethodCardExpirySb.append(",");
            paymentMethodCardLastFourSb.append(",");
          }
        }

        paymentMethodIdSb.append("}");
        paymentMethodTypeSb.append("}");
        paymentMethodNameSb.append("}");
        paymentMethodStripeSourceIdSb.append("}");
        paymentMethodAccountHolderNameSb.append("}");
        paymentMethodAddressSb.append("}");
        paymentMethodCitySb.append("}");
        paymentMethodStateSb.append("}");
        paymentMethodZipCodeSb.append("}");
        paymentMethodPhoneNumberSb.append("}");
        paymentMethodCardBrandSb.append("}");
        paymentMethodCardExpirySb.append("}");
        paymentMethodCardLastFourSb.append("}");

        dto.setPaymentMethodId(paymentMethodIdSb.toString());
        dto.setPaymentMethodType(paymentMethodTypeSb.toString());
        dto.setPaymentMethodName(paymentMethodNameSb.toString());
        dto.setPaymentMethodStripeSourceId(paymentMethodStripeSourceIdSb.toString());
        dto.setPaymentMethodAccountHolderName(paymentMethodAccountHolderNameSb.toString());
        dto.setPaymentMethodAddress(paymentMethodAddressSb.toString());
        dto.setPaymentMethodCity(paymentMethodCitySb.toString());
        dto.setPaymentMethodState(paymentMethodStateSb.toString());
        dto.setPaymentMethodZipCode(paymentMethodZipCodeSb.toString());
        dto.setPaymentMethodPhoneNumber(paymentMethodPhoneNumberSb.toString());
        dto.setPaymentMethodCardBrand(paymentMethodCardBrandSb.toString());
        dto.setPaymentMethodCardExpiry(paymentMethodCardExpirySb.toString());
        dto.setPaymentMethodCardLastFour(paymentMethodCardLastFourSb.toString());
        
        dto.setAllowOutOfBandBuildings(ode.getAllowOutOfBandBuildings());

      } else if (e instanceof OutOfBandDistributorEntity) {

        OutOfBandDistributorEntity oobde = (OutOfBandDistributorEntity) e;

        dto.setBillingStartDate(AbstractEntity.formatTimestamp(oobde.getBillingStartDate()));
        dto.setBillingRenewalDate(AbstractEntity.formatTimestamp(oobde.getBillingRenewalDate()));

      } else {
        throw new IllegalStateException("Unsupported distributor type: ["
            + e.getClassAndNaturalIdentity()
            + "] with id: ["
            + e.getPersistentIdentity()
            + "]");
      }
      return dto;
    }

    @Override
    public AbstractDistributorEntity mapDtoToEntity(
        AbstractDistributorEntity rootDistributor,
        DistributorDto dto) {

      AbstractDistributorEntity childDistributor = null;

      try {

        AbstractDistributorEntity parentDistributor = null;
        Integer parentDistributorId = dto.getParentId();
        if (rootDistributor!= null && parentDistributorId != null) {
          
          parentDistributor = rootDistributor.getDescendantDistributor(parentDistributorId);
        }

        String type = dto.getType();
        if (type == null) {
          LOGGER.error("Distributor is missing type information, defaulting to out of band: {}", dto);
          type = OutOfBandDistributorEntity.DISTRIBUTOR_TYPE_OUT_OF_BAND;
        }
        if (type.equalsIgnoreCase(OnlineDistributorEntity.DISTRIBUTOR_TYPE_ONLINE)) {

          childDistributor = new OnlineDistributorEntity(
              dto.getId(),
              parentDistributor,
              dto.getName(),
              AbstractEntity.parseTimestamp(dto.getCreatedAt()),
              AbstractEntity.parseTimestamp(dto.getUpdatedAt()),
              dto.getUuid(),
              UnitSystem.get(dto.getUnitSystem()),
              InternalOrganizationalReferralAgentEntity.buildResoluteReferralAgentStub(), // Change (if the need ever arises)
              DistributorStatus.valueOf(dto.getStatus()),
              AbstractEntity.parseTimestamp(dto.getStatusUpdatedAt()),
              DistributorPaymentStatus.valueOf(dto.getPaymentStatus()),
              AbstractEntity.parseTimestamp(dto.getPaymentStatusUpdatedAt()),
              dto.getAllowOutOfBandBuildings());
          
          if (dto.getStripeCustomerId() != null) {
            ((OnlineDistributorEntity) childDistributor).setStripeCustomerId(dto.getStripeCustomerId());
          }
          
          if (dto.getPaymentMethodId() != null) {
            
            String[] paymentMethodIdArr = dto.getPaymentMethodId().replace("{", "").replace("}", "")
                .replace("\"", "").split(",");
            String[] paymentMethodTypeArr = dto.getPaymentMethodType().replace("{", "")
                .replace("}", "").replace("\"", "").split(",");
            String[] paymentMethodNameArr = dto.getPaymentMethodName().replace("{", "")
                .replace("}", "").replace("\"", "").split(",");
            String[] paymentMethodStripeSourceIdArr = dto.getPaymentMethodStripeSourceId()
                .replace("{", "").replace("}", "").replace("\"", "").split(",");
            String[] paymentMethodAccountHolderNameArr = dto.getPaymentMethodAccountHolderName()
                .replace("{", "").replace("}", "").replace("\"", "").split(",");
            String[] paymentMethodAddressArr = dto.getPaymentMethodAddress().replace("{", "")
                .replace("}", "").replace("\"", "").split(",");;
            String[] paymentMethodCityArr = dto.getPaymentMethodCity().replace("{", "")
                .replace("}", "").replace("\"", "").split(",");
            String[] paymentMethodStateArr = dto.getPaymentMethodState().replace("{", "")
                .replace("}", "").replace("\"", "").split(",");
            String[] paymentMethodZipCodeArr = dto.getPaymentMethodZipCode().replace("{", "")
                .replace("}", "").replace("\"", "").split(",");
            String[] paymentMethodPhoneNumberArr = dto.getPaymentMethodPhoneNumber().replace("{", "")
                .replace("}", "").replace("\"", "").split(",");
            String[] paymentMethodCardBrandArr = dto.getPaymentMethodCardBrand().replace("{", "")
                .replace("}", "").replace("\"", "").split(",");
            String[] paymentMethodCardExpiryArr = dto.getPaymentMethodCardExpiry().replace("{", "")
                .replace("}", "").replace("\"", "").split(",");
            String[] paymentMethodCardLastFourArr = dto.getPaymentMethodCardLastFour()
                .replace("{", "").replace("}", "").replace("\"", "").split(",");

            for (int i = 0; i < paymentMethodIdArr.length; i++) {

              AbstractPaymentMethodEntity paymentMethod = null;

              String paymentMethodId = paymentMethodIdArr[i];
              if (!paymentMethodId.equals("") && !paymentMethodId.equals("NULL")) {

                String paymentMethodType = paymentMethodTypeArr[i];
                String paymentMethodName = paymentMethodNameArr[i];
                String paymentMethodStripeSourceId = paymentMethodStripeSourceIdArr[i];
                String paymentMethodAccountHolderName = paymentMethodAccountHolderNameArr[i];
                String paymentMethodAddress = paymentMethodAddressArr[i];
                String paymentMethodCity = paymentMethodCityArr[i];
                String paymentMethodState = paymentMethodStateArr[i];
                String paymentMethodZipCode = paymentMethodZipCodeArr[i];
                String paymentMethodPhoneNumber = paymentMethodPhoneNumberArr[i];
                String paymentMethodCardBrand = paymentMethodCardBrandArr[i];
                String paymentMethodCardExpiry = paymentMethodCardExpiryArr[i];
                String paymentMethodCardLastFour = paymentMethodCardLastFourArr[i];

                if (paymentMethodType.equals(PaymentMethodType.CREDIT_CARD.getName())) {

                  paymentMethod = new CreditCardPaymentMethodEntity(
                      Integer.parseInt(paymentMethodId),
                      (OnlineDistributorEntity)childDistributor,
                      PaymentMethodType.valueOf(paymentMethodType),
                      paymentMethodName,
                      paymentMethodStripeSourceId,
                      paymentMethodAccountHolderName,
                      paymentMethodAddress,
                      paymentMethodCity,
                      paymentMethodState,
                      paymentMethodZipCode,
                      paymentMethodPhoneNumber,
                      paymentMethodCardBrand,
                      paymentMethodCardExpiry,
                      paymentMethodCardLastFour);

                } else if (paymentMethodType.equals(PaymentMethodType.ACH.getName())) {

                  paymentMethod = new AchPaymentMethodEntity(
                      Integer.parseInt(paymentMethodId),
                      (OnlineDistributorEntity)childDistributor,
                      PaymentMethodType.valueOf(paymentMethodType),
                      paymentMethodName,
                      paymentMethodStripeSourceId,
                      paymentMethodAccountHolderName,
                      paymentMethodAddress,
                      paymentMethodCity,
                      paymentMethodState,
                      paymentMethodZipCode,
                      paymentMethodPhoneNumber);

                } else {
                  throw new IllegalStateException("Unsupported payment method type: ["
                      + paymentMethodType
                      + "]");
                }

                ((OnlineDistributorEntity) childDistributor).addChildPaymentMethod(paymentMethod);
              }
            }
          }

        } else if (type.equalsIgnoreCase(OutOfBandDistributorEntity.DISTRIBUTOR_TYPE_OUT_OF_BAND)) {
          
          childDistributor = new OutOfBandDistributorEntity(
              dto.getId(),
              parentDistributor,
              dto.getName(),
              AbstractEntity.parseTimestamp(dto.getCreatedAt()),
              AbstractEntity.parseTimestamp(dto.getUpdatedAt()),
              dto.getUuid(),
              UnitSystem.get(dto.getUnitSystem()),
              InternalOrganizationalReferralAgentEntity.buildResoluteReferralAgentStub(), // Change (if the need ever arises)
              DistributorStatus.valueOf(dto.getStatus()),
              AbstractEntity.parseTimestamp(dto.getStatusUpdatedAt()),
              DistributorPaymentStatus.valueOf(dto.getPaymentStatus()),
              AbstractEntity.parseTimestamp(dto.getPaymentStatusUpdatedAt()),
              AbstractEntity.parseTimestamp(dto.getBillingStartDate()),
              AbstractEntity.parseTimestamp(dto.getBillingRenewalDate()));

        } else {
          throw new IllegalStateException("Unsupported distributor type: ["
              + type
              + "] for dto: ["
              + dto
              + "]");
        }
        if (parentDistributor != null) {
          parentDistributor.addChildDistributor(childDistributor);
        }

      } catch (Exception e) {
        throw new IllegalStateException("Unable to map distributor: "
            + dto
            + "\n error: "
            + e.getMessage(), e);
      }
      return childDistributor;
    }
  }
}
//@formatter:on