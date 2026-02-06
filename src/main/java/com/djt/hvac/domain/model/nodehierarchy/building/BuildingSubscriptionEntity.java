//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.building;

import static java.util.Objects.requireNonNull;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.AbstractPersistentEntity;
import com.djt.hvac.domain.model.common.mapper.DtoMapper;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.dictionary.PaymentPlanEntity;
import com.djt.hvac.domain.model.dictionary.container.DictionaryContext;
import com.djt.hvac.domain.model.dictionary.container.PaymentPlansContainer;
import com.djt.hvac.domain.model.dictionary.enums.PaymentInterval;
import com.djt.hvac.domain.model.distributor.OnlineDistributorEntity;
import com.djt.hvac.domain.model.distributor.paymentmethod.AbstractPaymentMethodEntity;
import com.djt.hvac.domain.model.nodehierarchy.dto.BuildingSubscriptionDto;
import com.djt.hvac.domain.model.nodehierarchy.utils.BuildingSubscriptionTemporalAdjuster;

public class BuildingSubscriptionEntity extends AbstractPersistentEntity {
  private static final long serialVersionUID = 1L;
  private static final Logger LOGGER = LoggerFactory.getLogger(BuildingSubscriptionEntity.class);
  
  private final BillableBuildingEntity parentBuilding;
  private PaymentPlanEntity parentPaymentPlan;
  private AbstractPaymentMethodEntity parentPaymentMethod;
  private String stripeSubscriptionId;
  private Timestamp startedAt = AbstractEntity.getTimeKeeper().getCurrentTimestamp();
  private Timestamp currentIntervalStartedAt = AbstractEntity.getTimeKeeper().getCurrentTimestamp();
  private PaymentPlanEntity pendingPaymentPlan;
  private Timestamp pendingPaymentPlanUpdatedAt;

  public BuildingSubscriptionEntity(
      BillableBuildingEntity parentBuilding,
      PaymentPlanEntity parentPaymentPlan,
      AbstractPaymentMethodEntity parentPaymentMethod,
      String stripeSubscriptionId) {
    this(
        null,
        parentBuilding,
        parentPaymentPlan,
        parentPaymentMethod,
        stripeSubscriptionId,
        AbstractEntity.getTimeKeeper().getCurrentTimestamp(),
        AbstractEntity.getTimeKeeper().getCurrentTimestamp(),
        null,
        null);
  }
  
  public BuildingSubscriptionEntity(
      Integer persistentIdentity,
      BillableBuildingEntity parentBuilding,
      PaymentPlanEntity parentPaymentPlan,
      AbstractPaymentMethodEntity parentPaymentMethod,
      String stripeSubscriptionId,
      Timestamp startedAt,
      Timestamp currentIntervalStartedAt,
      PaymentPlanEntity pendingPaymentPlan,
      Timestamp pendingPaymentPlanUpdatedAt) {
    super(persistentIdentity);
    requireNonNull(parentBuilding, "parentBuilding cannot be null");
    requireNonNull(parentPaymentPlan, "parentPaymentPlan cannot be null");
    requireNonNull(parentPaymentMethod, "parentPaymentMethod cannot be null");
    requireNonNull(startedAt, "startedAt cannot be null");
    requireNonNull(currentIntervalStartedAt, "currentIntervalStartedAt cannot be null");
    this.parentBuilding = parentBuilding;
    this.parentPaymentPlan = parentPaymentPlan;
    this.parentPaymentMethod = parentPaymentMethod;
    this.stripeSubscriptionId = stripeSubscriptionId;
    this.startedAt = startedAt;
    this.currentIntervalStartedAt = currentIntervalStartedAt;
    this.pendingPaymentPlan = pendingPaymentPlan;
    this.pendingPaymentPlanUpdatedAt = pendingPaymentPlanUpdatedAt;
  }

  public BillableBuildingEntity getParentBuilding() {
    return parentBuilding;
  }
  
  public PaymentPlanEntity getParentPaymentPlan() {
    return parentPaymentPlan;
  }

  public void setParentPaymentPlan(PaymentPlanEntity parentPaymentPlan) {
    this.parentPaymentPlan = parentPaymentPlan;
    this.setIsModified();
  }

  public AbstractPaymentMethodEntity getParentPaymentMethod() {
    return parentPaymentMethod;
  }

  public void setParentPaymentMethod(AbstractPaymentMethodEntity parentPaymentMethod) {
    this.parentPaymentMethod = parentPaymentMethod;
    this.setIsModified();
  }

  public String getStripeSubscriptionId() {
    return stripeSubscriptionId;
  }

  public void setStripeSubscriptionId(String stripeSubscriptionId) {
    this.stripeSubscriptionId = stripeSubscriptionId;
    this.setIsModified();
  }
  
  public Timestamp getStartedAt() {
    return startedAt;
  }
  
  public Timestamp getCurrentIntervalStartedAt() {
    return currentIntervalStartedAt;
  }
  
  public void setIsModified() {
    this.parentBuilding.setIsModified("buildingSubscription");    
  }
  
  public void setCurrentIntervalStartedAt(Timestamp currentIntervalStartedAt) {
    this.currentIntervalStartedAt = currentIntervalStartedAt;
    this.setIsModified();
  }

  public PaymentPlanEntity getPendingPaymentPlan() {
    return pendingPaymentPlan;
  }

  public void setPendingPaymentPlan(PaymentPlanEntity pendingPaymentPlan) {
    this.pendingPaymentPlan = pendingPaymentPlan;
    this.pendingPaymentPlanUpdatedAt = AbstractEntity.getTimeKeeper().getCurrentTimestamp();
    this.setIsModified();
  }
  
  public boolean isCanceled() {
    return parentBuilding.isSubscriptionCanceled();
  }

  public Timestamp getPendingPaymentPlanUpdatedAt() {
    return pendingPaymentPlanUpdatedAt;
  }
  
  /**
   * 
   * @return the new payment plan, null otherwise
   */
  public PaymentPlanEntity transitionToNewPaymentInterval() {
    
    PaymentPlanEntity newPaymentPlan = this.pendingPaymentPlan;

    LocalDate currentIntervalStartedAtLocalDate = this.currentIntervalStartedAt
        .toLocalDateTime()
        .toLocalDate();

    LocalDate newCurrentIntervalStartedAtLocalDate = null;
    LocalDate currentIntervalEndsAtLocalDate = null;
    if (parentPaymentPlan.getPaymentInterval().equals(PaymentInterval.MONTHLY) && this.pendingPaymentPlan == null) {

      currentIntervalEndsAtLocalDate = BuildingSubscriptionTemporalAdjuster.nextDayOfMonthForCurrentLocalDate(
          currentIntervalStartedAtLocalDate, 
          currentIntervalStartedAtLocalDate);
      
      newCurrentIntervalStartedAtLocalDate = currentIntervalEndsAtLocalDate;
      
    } else if (parentPaymentPlan.getPaymentInterval().equals(PaymentInterval.MONTHLY) && this.pendingPaymentPlan != null) {

      currentIntervalEndsAtLocalDate = BuildingSubscriptionTemporalAdjuster.nextDayOfMonthForCurrentLocalDate(
          currentIntervalStartedAtLocalDate, 
          currentIntervalStartedAtLocalDate);
      
      newCurrentIntervalStartedAtLocalDate = currentIntervalEndsAtLocalDate;
      
      currentIntervalEndsAtLocalDate = BuildingSubscriptionTemporalAdjuster.nextDayOfYearForCurrentLocalDate(
          currentIntervalStartedAtLocalDate.plusMonths(1), 
          currentIntervalStartedAtLocalDate);
      
    } else if (parentPaymentPlan.getPaymentInterval().equals(PaymentInterval.YEARLY) && this.pendingPaymentPlan == null) {
      
      currentIntervalEndsAtLocalDate = BuildingSubscriptionTemporalAdjuster.nextDayOfYearForCurrentLocalDate(
          currentIntervalStartedAtLocalDate, 
          currentIntervalStartedAtLocalDate);
      
      newCurrentIntervalStartedAtLocalDate = currentIntervalEndsAtLocalDate;
      
    } else if (parentPaymentPlan.getPaymentInterval().equals(PaymentInterval.YEARLY) && this.pendingPaymentPlan != null) {
      
      currentIntervalEndsAtLocalDate = BuildingSubscriptionTemporalAdjuster.nextDayOfMonthForCurrentLocalDate(
          currentIntervalStartedAtLocalDate, 
          currentIntervalStartedAtLocalDate.plusYears(1));
      
      newCurrentIntervalStartedAtLocalDate = currentIntervalEndsAtLocalDate;
      
    }

    if (this.pendingPaymentPlan != null) {

      LOGGER.info(
          AbstractEntity.getTimeKeeper().getCurrentLocalDate()
          + ": PAYMENT PLAN WITH DIFFERENT INTERVAL TRANSITION: Setting new payment plan from: ["
          + parentPaymentPlan
          + "] to: ["
          + pendingPaymentPlan
          + "] currentIntervalStartedAt: ["
          + currentIntervalStartedAt
          + "] currentIntervalEndsAtLocalDate: ["
          + currentIntervalEndsAtLocalDate
          + "] for subscription: ["
          + this
          + "].");
      
      this.parentPaymentPlan = this.pendingPaymentPlan;
      this.pendingPaymentPlan = null;
      this.pendingPaymentPlanUpdatedAt = null;
      this.setIsModified();
    }
    
    if (newCurrentIntervalStartedAtLocalDate == null) {
      throw new IllegalStateException("newCurrentIntervalStartedAtLocalDate is null and should not be for building subscription: ["
          + this);
    }
    
    Timestamp newCurrentIntervalStartedAt = Timestamp.valueOf(newCurrentIntervalStartedAtLocalDate.atStartOfDay());
    
    LOGGER.info(
        AbstractEntity.getTimeKeeper().getCurrentLocalDate()
        + ": PAYMENT INTERVAL TRANSITION: Setting current payment interval started at from: ["
        + currentIntervalStartedAt
        + "] to: ["
        + newCurrentIntervalStartedAt
        + "] currentIntervalEndsAtLocalDate: ["
        + currentIntervalEndsAtLocalDate
        + "] for subscription: ["
        + this
        + "].");
    
    this.setIsModified();
    
    setCurrentIntervalStartedAt(newCurrentIntervalStartedAt);
    
    return newPaymentPlan;
  }
  
  /*
   * MONTHLY:
   * start: 06-15-2018
   * current: 09-25-2020
   * interval start: 09-15-2020
   * interval end: 10-15-2020
   * 
   * MONTHLY:
   * start: 06-15-2018
   * current: 10-01-2020
   * interval start: 09-15-2020
   * interval end: 10-15-2020
   * 
   * 1. get start date day of month/year
   * 2. get current date day of month/year
   * 3. if current date day of month/year is equal to, or greater than, than the 
   *    start date day of month/year, 
   *    then the interval start is the current month start date day of month/year
   *    otherwise, the interval start is the next month/year day of month/year
   * 
   */
  public Timestamp getCurrentIntervalEndsAt() {
    
    LocalDate currentIntervalEndsAtLocalDate = getCurrentIntervalEndsAt(
        currentIntervalStartedAt
        .toLocalDateTime()
        .toLocalDate(),
        parentPaymentPlan);
    
    Timestamp paymentIntervalEndTimestamp = Timestamp.valueOf(currentIntervalEndsAtLocalDate.atStartOfDay());
    return paymentIntervalEndTimestamp;
  }

  public static LocalDate getCurrentIntervalEndsAt(
      LocalDate currentIntervalStartedAtLocalDate,
      PaymentPlanEntity parentPaymentPlan) {
    
    LocalDate nextIntervalStartsAt = getNextIntervalStartsAt(
        currentIntervalStartedAtLocalDate, 
        parentPaymentPlan)
        .minusDays(1);
    
    return nextIntervalStartsAt;
  }
  
  public LocalDate getNextIntervalStartsAt() {
    
    LocalDate currentIntervalStartedAtLocalDate = currentIntervalStartedAt
        .toLocalDateTime()
        .toLocalDate();
    
    return getNextIntervalStartsAt(
        currentIntervalStartedAtLocalDate,
        parentPaymentPlan); 
  }
  
  public static LocalDate getNextIntervalStartsAt(
      LocalDate currentIntervalStartedAtLocalDate,
      PaymentPlanEntity parentPaymentPlan) {

    LocalDate currentIntervalEndsAtLocalDate = null;
    if (parentPaymentPlan.getPaymentInterval().equals(PaymentInterval.MONTHLY)) {

      currentIntervalEndsAtLocalDate = BuildingSubscriptionTemporalAdjuster.nextDayOfMonthForCurrentLocalDate(
          currentIntervalStartedAtLocalDate, 
          currentIntervalStartedAtLocalDate);
      
    } else {
      
      currentIntervalEndsAtLocalDate = BuildingSubscriptionTemporalAdjuster.nextDayOfYearForCurrentLocalDate(
          currentIntervalStartedAtLocalDate, 
          currentIntervalStartedAtLocalDate);
      
    }
    return currentIntervalEndsAtLocalDate;
  }
  
  public boolean hasCurrentPaymentIntervalExpired() {

    LocalDate currentIntervalEndsAtLocalDate = getCurrentIntervalEndsAt()
        .toLocalDateTime()
        .toLocalDate();
    
    return hasCurrentPaymentIntervalExpired(currentIntervalEndsAtLocalDate);
  }
  
  public static boolean hasCurrentPaymentIntervalExpired(
      LocalDate currentIntervalEndsAtLocalDate) {
    
    LocalDate currentLocalDate = AbstractEntity
        .getTimeKeeper()
        .getCurrentTimestamp()
        .toLocalDateTime()
        .toLocalDate();
    
    boolean isAfter = currentLocalDate.isAfter(currentIntervalEndsAtLocalDate);
    
    if (isAfter) {
      return true;
    }
    return false;
  }
  
  public String getStatus() {
    
    return new StringBuilder()
        .append(this.getNaturalIdentity())
        .append(" is cancelled: [")
        .append(parentBuilding.isSubscriptionCanceled())
        .append("] startedAt: [")
        .append(startedAt)
        .append("] current interval started: [")
        .append(getCurrentIntervalStartedAt())
        .append("] current interval end: [")
        .append(getCurrentIntervalEndsAt())
        .append("] is expired: [")
        .append(parentBuilding.isSubscriptionCanceled()?hasCurrentPaymentIntervalExpired():"false")
        .append("]")
        .toString();
  }
  
  @Override
  public String getNaturalIdentity() {
    
    return new StringBuilder()
        .append(parentBuilding.getNaturalIdentity())
        .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
        .append(parentPaymentPlan.getNaturalIdentity())
        .append(" is cancelled: [")
        .append(parentBuilding.isSubscriptionCanceled())
        .append("] startedAt: [")
        .append(startedAt)
        .append("] current interval started: [")
        .append(getCurrentIntervalStartedAt())
        .append("] current interval end: [")
        .append(getCurrentIntervalEndsAt())
        .append("] is expired: [")
        .append(parentBuilding.isSubscriptionCanceled()?hasCurrentPaymentIntervalExpired():"false")
        .toString();
  }  
  
  @Override
  public void validate(
      Set<IssueType> issueTypes,
      List<ValidationMessage> validationMessages, 
      boolean remediate) {
  }  
  
  @Override
  public void evaluateState() {
  }
  
  public static class Mapper implements DtoMapper<BillableBuildingEntity, BuildingSubscriptionEntity, BuildingSubscriptionDto> {
    
    private static final Mapper INSTANCE = new Mapper();
    private Mapper() {
    }
    
    public static Mapper getInstance() {
      return INSTANCE;
    }
    
    public List<BuildingSubscriptionDto> mapEntitiesToDtos(List<BuildingSubscriptionEntity> entities) {
      
      List<BuildingSubscriptionDto> list = new ArrayList<>();
      Iterator<BuildingSubscriptionEntity> iterator = entities.iterator();
      while (iterator.hasNext()) {
        
        BuildingSubscriptionEntity entity = iterator.next();
        list.add(mapEntityToDto(entity));  
      }
      return list;
    }
    
    @Override
    public BuildingSubscriptionDto mapEntityToDto(BuildingSubscriptionEntity e) {
      
      BuildingSubscriptionDto dto = new BuildingSubscriptionDto();
      
      dto.setBuildingId(e.getPersistentIdentity());
      dto.setPaymentPlanId(e.getParentPaymentPlan().getPersistentIdentity());
      dto.setPaymentMethodId(e.getParentPaymentMethod().getPersistentIdentity());
      dto.setStripeSubscriptionId(e.getStripeSubscriptionId());
      dto.setStartedAt(e.getStartedAt() != null?AbstractEntity.formatTimestamp(e.getStartedAt()):null);
      dto.setCurrentIntervalStartedAt(e.getCurrentIntervalStartedAt() != null?AbstractEntity.formatTimestamp(e.getCurrentIntervalStartedAt()):null);
      dto.setPendingPaymentPlanId(e.getPendingPaymentPlan() != null?e.getPendingPaymentPlan().getPersistentIdentity():null);
      dto.setPendingPaymentPlanUpdatedAt(e.getPendingPaymentPlanUpdatedAt() != null?AbstractEntity.formatTimestamp(e.getPendingPaymentPlanUpdatedAt()):null);
      
      return dto;
    }

    @Override
    public BuildingSubscriptionEntity mapDtoToEntity(BillableBuildingEntity parentBuilding, BuildingSubscriptionDto d) {
      
      BuildingSubscriptionEntity buildingSubscription = null;
      try {
        
        PaymentPlansContainer paymentPlansContainer = DictionaryContext.getPaymentPlansContainer();
        OnlineDistributorEntity distributor = (OnlineDistributorEntity)parentBuilding.getRootPortfolioNode().getParentCustomer().getParentDistributor();
        
        Timestamp startedAt = null;
        if (d.getStartedAt() != null) {
          startedAt = AbstractEntity.parseTimestamp(d.getStartedAt());
        }

        Timestamp currentIntervalStartedAt = null;
        if (d.getCurrentIntervalStartedAt() != null) {
          currentIntervalStartedAt = AbstractEntity.parseTimestamp(d.getCurrentIntervalStartedAt());
        }
        
        PaymentPlanEntity pendingPaymentPlan = null;
        if (d.getPendingPaymentPlanId() != null) {
          pendingPaymentPlan = paymentPlansContainer.getPaymentPlan(d.getPendingPaymentPlanId());
        }
        
        Timestamp pendingPaymentPlanUpdatedAt = null;
        if (d.getPendingPaymentPlanUpdatedAt() != null) {
          pendingPaymentPlanUpdatedAt = AbstractEntity.parseTimestamp(d.getPendingPaymentPlanUpdatedAt());
        }
        
        buildingSubscription = new BuildingSubscriptionEntity(
            d.getBuildingId(),
            parentBuilding,
            paymentPlansContainer.getPaymentPlan(d.getPaymentPlanId()),
            distributor.getChildPaymentMethod(d.getPaymentMethodId()),
            d.getStripeSubscriptionId(),
            startedAt,
            currentIntervalStartedAt,
            pendingPaymentPlan,
            pendingPaymentPlanUpdatedAt);
        
        parentBuilding.setChildBuildingSubscription(buildingSubscription);
        
      } catch (Exception e) {
        LOGGER.error("Error: ["
            + e.getMessage()
            + "], unable to map building subscription: ["
            + d
            + "]");
      }
      return buildingSubscription;
    }
  }
}
//@formatter:on