//@formatter:off
package com.djt.hvac.domain.model.report;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.AbstractPersistentEntity;
import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.common.mapper.DtoMapper;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.dictionary.container.DictionaryContext;
import com.djt.hvac.domain.model.dictionary.dto.report.ReportTemplatePointSpecDto;
import com.djt.hvac.domain.model.dictionary.template.function.rule.AdRuleFunctionTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.report.AbstractReportTemplatePointSpecEntity;
import com.djt.hvac.domain.model.dictionary.template.report.ReportTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.report.ReportTemplateEquipmentSpecEntity;
import com.djt.hvac.domain.model.dictionary.template.report.ReportTemplateRulePointSpecEntity;
import com.djt.hvac.domain.model.dictionary.template.report.ReportTemplateStandardPointSpecEntity;
import com.djt.hvac.domain.model.dictionary.template.report.enums.ReportPriority;
import com.djt.hvac.domain.model.function.AdFunctionInstanceOutputPointEntity;
import com.djt.hvac.domain.model.function.rule.AdRuleFunctionInstanceEntity;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BuildingEntity;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.EquipmentEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.AbstractPointEntity;
import com.djt.hvac.domain.model.report.dto.ReportInstanceDto;
import com.djt.hvac.domain.model.report.dto.ReportInstanceEquipmentDto;
import com.djt.hvac.domain.model.report.dto.ReportInstancePointDto;
import com.djt.hvac.domain.model.report.dto.ReportInstanceStatusDto;
import com.djt.hvac.domain.model.report.dto.ReportInstanceStatusErrorMessageDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableList;

public class ReportInstanceEntity extends AbstractPersistentEntity {
  private static final long serialVersionUID = 1L;
  private static final Logger LOGGER = LoggerFactory.getLogger(ReportInstanceEntity.class);
  
  private static final List<Integer> IMMUTABLE_EMPTY_ERROR_SET = ImmutableList.of();
  
  public static final String STATUS_GREEN = "GREEN";
  public static final String STATUS_YELLOW = "YELLOW";
  public static final String STATUS_RED = "RED";
  
  public static final String GREEN_EQUIPMENT_UPDATED = "greenEquipmentStateHasChanged";
  public static final String RED_EQUIPMENT_UPDATED = "redEquipmentStateHasChanged";
  
  private final ReportTemplateEntity reportTemplate;
  private final BuildingEntity building;
  
  private ReportPriority priority;
  
  private boolean isIgnored = false; // If ignored, then can't be enabled, nor will be evaluated.
  
  private long createdAt;
  private long updatedAt; // This is used to specify the time when the entity was stored, which may
  // be different than the last evaluation time (depending upon what the service layer is doing)
  
  // Reqardless of whther a report has been enabled/instantiated, this list contains the set of
  // GREEN/VALID equipment and their matched points.
  private final Set<ReportInstanceEquipmentEntity> reportInstanceEquipment = new TreeSet<>();
  private int numGreenEquipment = -1; // When load portfolio depth is BUILDING level, GREEN equipment count is set instead
  public void setNumGreenEquipment(int numGreenEquipment) {
    this.numGreenEquipment = numGreenEquipment;
  }
  private Set<ReportInstanceEquipmentEntity> addedGreenEquipment = new HashSet<>();
  private Set<ReportInstanceEquipmentEntity> removedGreenEquipment = new HashSet<>();

  // If true, then the report instance needs to be re-evaluated and stored to the DB
  private boolean needsRebuilding = false;
  
  // If true, then the report instance, if "enabled", meaning that the instance tables have been populated,
  // will be deleted, as the report is no longer valid OR the user has explicitly disabled the report.
  private boolean needsDisabling = false;
  
  // If true, then this report instance has transitioned from disabled to enabled, and thus, needs
  // persisting to the "instance" tables.  Note, this only occurs from an explicit request from the user.
  private boolean needsEnabling = false;
  
  // A piece of equipment is in one of two states: GREEN or RED.
  // ------------------------------------------------------------
  // GREEN STATUS: the equipment matched against the report template equipment spec for:
  // 1. equipment type
  // 2. node filter expression
  // 3. all required child point specs of the equipment spec are bound to points
  //
  // RED STATUS: the equipment matched for the equipment type, yet one of the following
  // occured:
  // 1. The node filter expression evaluated to FALSE for the equipment.
  // 2. One or more required points were not bound.
  // Error messages are created and added to the parent report instance in a map
  // keyed by equipment.
  // 
  // In short, when GREEN, the equipment fully matched and report instance equipment
  // children are created.  Otherwise, the equipment is RED, there are one or more 
  // error messages explaining why the equipment didn't match for the report template.
  private final Set<ReportInstanceEquipmentErrorMessagesEntity> reportInstanceEquipmentErrorMessages = new TreeSet<>();
  private int numRedEquipment = -1; // When load portfolio depth is BUILDING level, RED equipment count is set instead
  public void setNumRedEquipment(int numRedEquipment) {
    this.numRedEquipment = numRedEquipment;
  }
  private Set<ReportInstanceEquipmentErrorMessagesEntity> addedRedEquipment = new HashSet<>();
  private Set<ReportInstanceEquipmentErrorMessagesEntity> removedRedEquipment = new HashSet<>();
 
  // Additional info from the DTO to entity mapper:
  // ----------------------------------------------
  // The trick here is to ensure that there exists one report instance for each combination of 
  // building/report template.  This is because the database persistence only has "enabled" 
  // report instances occupying the report instance tables.  If a combination is "disabled", we
  // return a report instance that is both "disabled" and has an empty list of "bound" 
  // equipment/points (i.e. GREEN equipment status).  We need this entity for every 
  // building/report template combination for *evaluation* purposes, as well as to hold the map
  // of qualifying equipment/points so that when "enabled", the report instance tables can easily
  // be created.  In addition, this base report instance will hold the *results* of periodic 
  // evaluation, which means we have a list of equipment that didn't match, along with a list of
  // error messages explaining why an equipment is RED.

  public ReportInstanceEntity(
      ReportTemplateEntity reportTemplate,
      BuildingEntity building,
      long createdAt,
      long updatedAt) {
    this(
        null,
        reportTemplate,
        building,
        createdAt,
        updatedAt,
        reportTemplate.getDefaultPriority());
  }  
  
  public ReportInstanceEntity(
      Integer persistentIdentity,
      ReportTemplateEntity reportTemplate,
      BuildingEntity building,
      long createdAt,
      long updatedAt,
      ReportPriority priority) {
    super(persistentIdentity);
    requireNonNull(reportTemplate, "reportTemplate cannot be null");
    requireNonNull(building, "building cannot be null");
    requireNonNull(priority, "priority cannot be null");
    this.reportTemplate = reportTemplate;
    this.building = building;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.priority = priority;
  }
  
  public ReportTemplateEntity getReportTemplate() {
    return reportTemplate;
  }
  
  public BuildingEntity getBuilding() {
    return building;
  }
  
  public ReportPriority getPriority() {
    return priority;
  }

  public void setPriority(ReportPriority priority) {
    
    if (priority == null) {
      throw new IllegalArgumentException("Incoming report priority must be specified and one of: [Low, Medium or High].");
    }

    if (!this.priority.equals(priority)) {

      this.priority = priority;
      setIsModified("priority");
      
    }    
  }  
  
  public boolean isIgnored() {
    return isIgnored;
  }

  public void setIsIgnored() {
    setIsIgnored(true);
  }
  
  public void setIsIgnored(boolean isIgnored) {
    this.isIgnored = isIgnored;
    setIsModified("isIgnored");
  }
  
  public long getCreatedAt() {
    return createdAt;
  }
  
  public long getUpdatedAt() {
    return updatedAt;
  }
  
  public Set<ReportInstanceEquipmentEntity> getReportInstanceEquipment() {
    return reportInstanceEquipment;
  }

  public void setGreenEquipment(Set<ReportInstanceEquipmentEntity> thatReportInstanceEquipment) {
    
    if (thatReportInstanceEquipment == null) {
      throw new IllegalStateException("thatReportInstanceEquipment cannot be null");
    }
    
    boolean areEqual = false;
    boolean isBeingMapped = building.getRootPortfolioNode().isBeingMapped;
    if (!isBeingMapped) {
      
      Map<String, ReportInstanceEquipmentEntity> thatHashes = new HashMap<>();
      for (ReportInstanceEquipmentEntity thatEquipment: thatReportInstanceEquipment) {
        
        thatHashes.put(thatEquipment.getNaturalIdentity(), thatEquipment);
      }
      
      Map<String, ReportInstanceEquipmentEntity> thisHashes = new HashMap<>();
      for (ReportInstanceEquipmentEntity thisEquipment: this.reportInstanceEquipment) {
        
        thisHashes.put(thisEquipment.getNaturalIdentity(), thisEquipment);
      }
      
      for (ReportInstanceEquipmentEntity thatEq: thatReportInstanceEquipment) {
        
        ReportInstanceEquipmentEntity thisEq = thisHashes.get(thatEq.getNaturalIdentity());
        
        if (!this.reportInstanceEquipment.contains(thatEq)) {
          
          addedGreenEquipment.add(thatEq);
          
        } else {
          
          int thatHashCode = thatEq.getReportInstancePoints().toString().hashCode();
          int thisHashCode = thisEq.getReportInstancePoints().toString().hashCode();
          if (thatHashCode != thisHashCode) {

            addedGreenEquipment.add(thatEq);
            removedGreenEquipment.add(thisEq);
            
          }
        }
      }
      
      for (ReportInstanceEquipmentEntity thisEq: this.reportInstanceEquipment) {
        
        ReportInstanceEquipmentEntity thatEq = thatHashes.get(thisEq.getNaturalIdentity());

        if (!thatReportInstanceEquipment.contains(thisEq)) {
          
          removedGreenEquipment.add(thisEq);
          
        } else {

          
          int thisHashCode = thisEq.getReportInstancePoints().toString().hashCode();
          int thatHashCode = thatEq.getReportInstancePoints().toString().hashCode();
          if (thisHashCode != thatHashCode) {

            removedGreenEquipment.add(thisEq);
            addedGreenEquipment.add(thatEq);
            
          }
        }
      }

      if (addedGreenEquipment.isEmpty() && removedGreenEquipment.isEmpty()) {
        areEqual = true;
      } else {
        updatedAt = AbstractEntity.getTimeKeeper().getCurrentTimeInMillis();
        setIsModified(GREEN_EQUIPMENT_UPDATED);
      }
    }
    
    if (!areEqual) {
      
      reportInstanceEquipment.clear();
      reportInstanceEquipment.addAll(thatReportInstanceEquipment);
    }
  }
  
  public Set<ReportInstanceEquipmentEntity> getAddedGreenEquipment() {
    return this.addedGreenEquipment;
  }

  public Set<ReportInstanceEquipmentEntity> getRemovedGreenEquipment() {
    return this.removedGreenEquipment;
  }

  public static String buildGreenEquipmentHierarchy(
      ReportInstanceEntity reportInstance,
      Set<ReportInstanceEquipmentEntity> entities) {
    
    StringBuilder sb = new StringBuilder();
    for (ReportInstanceEquipmentEntity e: entities) {
      for (ReportInstancePointEntity p: e.getReportInstancePoints()) {
        sb.append(p.getPoint().getNodePath()).append("\n");
      }
    }
    return sb.toString();
  }
  
  public Set<ReportInstanceEquipmentErrorMessagesEntity> getReportInstanceEquipmentErrorMessages() {
    return reportInstanceEquipmentErrorMessages;
  }
  
  private transient Map<Integer, ReportInstanceEquipmentErrorMessagesEntity>_reportInstanceEquipmentErrorMessages = null;
  public ReportInstanceEquipmentErrorMessagesEntity getReportInstanceEquipmentErrorMessagesNullIfNotExists(Integer equipmentId) {
    
    if (_reportInstanceEquipmentErrorMessages == null) {
      
      _reportInstanceEquipmentErrorMessages = new HashMap<>();
      for (ReportInstanceEquipmentErrorMessagesEntity entity: reportInstanceEquipmentErrorMessages) {
        
        _reportInstanceEquipmentErrorMessages.put(entity.getEquipment().getPersistentIdentity(), entity);
      }
    }
    return _reportInstanceEquipmentErrorMessages.get(equipmentId);
  }
  public void resetReportInstanceEquipmentErrorMessages() {
    _reportInstanceEquipmentErrorMessages = null;
  }
  
  public boolean addReportInstanceEquipmentErrorMessages(ReportInstanceEquipmentErrorMessagesEntity entity) {
    
    _reportInstanceEquipmentErrorMessages = null;
    return reportInstanceEquipmentErrorMessages.add(entity);
  }
  
  public void setRedEquipment(Set<ReportInstanceEquipmentErrorMessagesEntity> thatReportInstanceEquipment) {
    
    if (thatReportInstanceEquipment == null) {
      throw new IllegalStateException("thatReportInstanceEquipment cannot be null");
    }
    
    boolean areEqual = false;
    boolean isBeingMapped = building.getRootPortfolioNode().isBeingMapped;
    if (!isBeingMapped) {
      
      Map<String, ReportInstanceEquipmentErrorMessagesEntity> thatHashes = new HashMap<>();
      for (ReportInstanceEquipmentErrorMessagesEntity thatEquipment: thatReportInstanceEquipment) {
        
        thatHashes.put(thatEquipment.getNaturalIdentity(), thatEquipment);
      }
      
      Map<String, ReportInstanceEquipmentErrorMessagesEntity> thisHashes = new HashMap<>();
      for (ReportInstanceEquipmentErrorMessagesEntity thisEquipment: this.reportInstanceEquipmentErrorMessages) {
        
        thisHashes.put(thisEquipment.getNaturalIdentity(), thisEquipment);
      }
      
      for (ReportInstanceEquipmentErrorMessagesEntity thatEq: thatReportInstanceEquipment) {
        
        ReportInstanceEquipmentErrorMessagesEntity thisEq = thisHashes.get(thatEq.getNaturalIdentity());
        
        if (!this.reportInstanceEquipmentErrorMessages.contains(thatEq)) {
          
          addedRedEquipment.add(thatEq);
          
        } else {
          
          int thatHashCode = thatEq.getErrorMessages().toString().hashCode();
          int thisHashCode = thisEq.getErrorMessages().toString().hashCode();
          if (thatHashCode != thisHashCode) {

            addedRedEquipment.add(thatEq);
            removedRedEquipment.add(thisEq);
            
          }
        }
      }
      
      for (ReportInstanceEquipmentErrorMessagesEntity thisEq: this.reportInstanceEquipmentErrorMessages) {
        
        ReportInstanceEquipmentErrorMessagesEntity thatEq = thatHashes.get(thisEq.getNaturalIdentity());

        if (!thatReportInstanceEquipment.contains(thisEq)) {
          
          removedRedEquipment.add(thisEq);
          
        } else {

          
          int thisHashCode = thisEq.getErrorMessages().toString().hashCode();
          int thatHashCode = thatEq.getErrorMessages().toString().hashCode();
          if (thisHashCode != thatHashCode) {

            removedRedEquipment.add(thisEq);
            addedRedEquipment.add(thatEq);
            
          }
        }
      }

      if (addedRedEquipment.isEmpty() && removedRedEquipment.isEmpty()) {
        areEqual = true;
      } else {
        updatedAt = AbstractEntity.getTimeKeeper().getCurrentTimeInMillis();
        setIsModified(RED_EQUIPMENT_UPDATED);
      }
    }
    
    if (!areEqual) {
      
      _reportInstanceEquipmentErrorMessages = null;
      reportInstanceEquipmentErrorMessages.clear();
      reportInstanceEquipmentErrorMessages.addAll(thatReportInstanceEquipment);
    }    
  }
  
  public Set<ReportInstanceEquipmentErrorMessagesEntity> getAddedRedEquipment() {
    return this.addedRedEquipment;
  }

  public Set<ReportInstanceEquipmentErrorMessagesEntity> getRemovedRedEquipment() {
    return this.removedRedEquipment;
  }

  public static String buildRedEquipmentHierarchy(
      ReportInstanceEntity reportInstance,
      Set<ReportInstanceEquipmentErrorMessagesEntity> entities) {
    
    StringBuilder sb = new StringBuilder();
    for (ReportInstanceEquipmentErrorMessagesEntity e: entities) {
      sb.append(e.getEquipment().getNodePath()).append(e.getErrorMessages()).append("\n");
    }    
    return sb.toString();
  }
  
  public Set<ReportInstanceEquipmentErrorMessagesEntity> getCreatedReportInstanceEquipmentErrorMessages() {
  
    Set<ReportInstanceEquipmentErrorMessagesEntity> set = new HashSet<>();
    for (ReportInstanceEquipmentErrorMessagesEntity entity: reportInstanceEquipmentErrorMessages) {
      
      if (entity.getNeedsPersisting()) {
        set.add(entity);
      }
    }
    return set;
  }
  
  public Set<ReportInstanceEquipmentErrorMessagesEntity> getUpdatedReportInstanceEquipmentErrorMessages() {
    
    Set<ReportInstanceEquipmentErrorMessagesEntity> set = new HashSet<>();
    for (ReportInstanceEquipmentErrorMessagesEntity entity: reportInstanceEquipmentErrorMessages) {
      
      if (entity.getIsModified()) {
        set.add(entity);
      }
    }
    return set;
  }
  
  @Override
  public String getNaturalIdentity() {

    if (_naturalIdentity == null) {
      _naturalIdentity = getNaturalIdentity(building, reportTemplate);
    }
    return _naturalIdentity;
  }
  
  public boolean isValid() {
    
    String status = getStatus();
    if (status.equals(STATUS_GREEN) || status.equals(STATUS_YELLOW)) {
      return true;
    }
    return false;
  }

  public boolean isEnabled() {
    
    // When true, then we expect that the "instance" tables have been populated for GREEN/VALID equipment
    // (corresponding to the reportInstanceEquipment entities contained below).  Otherwise, the information
    // needed to enable/instantiate the report will be contained in the corresponding "candidateJson", which
    // are a set of corresponding report instance equipment DTO objects for all of GREEN/VALID equipment 
    // (and their points).  Either way, for any RED/INVALID equipment, the error messages for them are kept in
    // the "errorMessagesJson" field
    if (getPersistentIdentity() != null && getPersistentIdentity() != 0) {
      return true;
    }
    return false;
  }
  
  public void setIsModified(String modifiedAttributeName) {
    
    if (!building.getRootPortfolioNode().isBeingMapped) {
      
      super.setIsModified(modifiedAttributeName);
      building.setIsModified("childReportInstance:" + modifiedAttributeName);
    }
  }
  
  public void setDisabled() {
    
    if (isEnabled()) {
      
      setNeedsDisabling();
      setIsModified("enabled_to_disabled");
      
    } else {
      LOGGER.warn("Ignoring request to disable report instance: ["
          + this
          + "], as it is already disabled");
    }
  }

  public void setEnabled() {
    
    boolean isEnabled = isEnabled();
    boolean isValid = isValid();
    
    if (!isEnabled && !isValid) { // 00

      LOGGER.warn("Ignoring request to enable report instance: ["
          + this
          + "], as it in an invalid state");
      
    } else if (!isEnabled && isValid) { // 01

      setIsModified("disabled_to_enabled");
      setNeedsEnabling();
      
    } else if (isEnabled && !isValid) { // 10

      LOGGER.warn("Detected enabled report instance: ["
          + this
          + "], that is in an invalid state, automatically disabling");

      setNeedsDisabling();
      setIsModified("automatic_enabled_to_disabled");
      
    } else if (isEnabled && isValid) { // 11
      
      // NOTHING TO DO - ALREADY ENABLED AND IS VALID
      
    }
  }
  
  public String getStatus() {
    
    int numGreenEquipment = getNumEquipmentInGreenStatus();
    int numEquipmentTotal = getNumEquipmentTotal();
    
    return ReportInstanceEntity.getStatus(numGreenEquipment, numEquipmentTotal);
  }
  
  public static String getStatus(int numGreenEquipment, int numEquipmentTotal) {
    
    String status = null;
    if (numEquipmentTotal == 0 || numGreenEquipment == 0) {
      status = STATUS_RED;
    } else {
      if (numEquipmentTotal == numGreenEquipment) {
        status = STATUS_GREEN;
      } else {
        double percentageGreenEquipment = ((double)numGreenEquipment / (double)numEquipmentTotal) * 100;
        if (percentageGreenEquipment >= 70.0) {
          status = STATUS_GREEN;  
        } else {
          status = STATUS_YELLOW;
        }
      }
    }
    return status;
  } 
  
  /*
   *  REPORT STATUS: (derived/evaluated on-demand ) 
   *  =============================================
   *  GREEN: 1 or more pieces of equipment where GREEN status is between 70-100% (inclusive)
   *  YELLOW: 1 or more pieces of equipment where GREEN status is between 0-70% (exclusive)
   *  RED: 0 or more pieces of equipment where either 0 pieces of equipment or NONE with GREEN status
   */
  public void update(
      Set<ReportInstanceEquipmentEntity> greenEquipment,
      Set<ReportInstanceEquipmentErrorMessagesEntity> redEquipment) {

    // BEFORE STATE
    boolean isEnabled = isEnabled();
    
    // UPDATE STATE
    setGreenEquipment(greenEquipment);
    setRedEquipment(redEquipment);  

    // AFTER STATE
    boolean afterValid = isValid();
    boolean isModified = getIsModified();
    
    // SEE IF THERE WAS A CHANGE IN STATE
    if (isEnabled && !afterValid) {

      if (isEnabled && !afterValid) {
        
        setNeedsDisabling();
        if (!isModified) {
          setIsModified("automatic_enabled_to_disabled");  
        }
      }
    }
  }

  @Override
  public void setNotModified() {
    
    super.setNotModified();
    
    for (ReportInstanceEquipmentEntity entity: reportInstanceEquipment) {
      entity.setNotModified();
      entity.setNeedsPersisting(false);
    }
    
    for (ReportInstanceEquipmentErrorMessagesEntity entity: reportInstanceEquipmentErrorMessages) {
      entity.setNotModified();
      entity.setNeedsPersisting(false);
    }
    
    this.addedGreenEquipment.clear();
    this.removedGreenEquipment.clear();
    
    this.addedRedEquipment.clear();
    this.removedRedEquipment.clear();
  }
  
  @Override
  public void setIsDeleted() {
    
    // A report is never "deleted", it's either enabled/disabled, but 
    // disabling will result in the instance tables being deleted.
    this.setNeedsDisabling();
  }
  
  public Map<Integer, List<Integer>> getAllEquipmentErrorMessages() {
    
    Map<Integer, List<Integer>> allEquipmentErrorMessages = new HashMap<>();
    for (ReportInstanceEquipmentErrorMessagesEntity entity: reportInstanceEquipmentErrorMessages) {
      
      allEquipmentErrorMessages.put(entity.getEquipment().getPersistentIdentity(), entity.getErrorMessages());
    }
    return allEquipmentErrorMessages;
  }
  
  public List<Integer> getEquipmentErrorMessages(EquipmentEntity equipment) {
    
    for (ReportInstanceEquipmentErrorMessagesEntity entity: reportInstanceEquipmentErrorMessages) {
      
      if (entity.getEquipment().equals(equipment)) {
        return entity.getErrorMessages();
      }
    }
    return IMMUTABLE_EMPTY_ERROR_SET;
  }
  
  public long getLastEvaluationTime() {
    return this.updatedAt;
  }
  
  public boolean getNeedsRebuilding() {
    return this.needsRebuilding;
  }
  
  public void setNeedsRebuilding(boolean needsRebuilding) {
    this.needsRebuilding = needsRebuilding;
  }
  
  public boolean getNeedsDisabling() {
    return needsDisabling;
  }
  
  public void setNeedsDisabling() {
    
    if (getPersistentIdentity() != null) {
      getBuilding()
          .getRootPortfolioNode()
          .addNewlyDisabledReportInstanceId(getPersistentIdentity());  
    }
    
    setPersistentIdentity(null);
    needsDisabling = true;
    needsEnabling = false;
    setIsModified("needsDisabling");
 }

  public void resetNeedsDisabling() {
    needsDisabling = false;
  }
  
  public boolean getNeedsEnabling() {
    return needsEnabling;
  }  
  
  public void setNeedsEnabling() {
    
    if (!getModifiedAttributes().toString().contains("needsDisabling")) {

      getBuilding()
          .getRootPortfolioNode()
          .addNewlyCreatedReportInstance(this);  
      
      needsEnabling = true;
      needsDisabling = false;
      setIsModified("needsEnabling");
    }
  }
  
  public void resetNeedsEnabling() {
    needsEnabling = false;
  }
  
  public Integer getNumEquipmentInGreenStatus() {
    
    if (numGreenEquipment != -1) {
      return numGreenEquipment;
    }
    return Integer.valueOf(reportInstanceEquipment.size());
  }

  public Integer getNumEquipmentInRedStatus() {
    
    if (numRedEquipment != -1) {
      return numRedEquipment;
    }
    return Integer.valueOf(reportInstanceEquipmentErrorMessages.size());
  }
  
  public Integer getNumEquipmentTotal() {
    
    return Integer.valueOf(
        getNumEquipmentInGreenStatus() 
        + getNumEquipmentInRedStatus());
  }

  @Override
  public void validate(
      Set<IssueType> issueTypes, 
      List<ValidationMessage> validationMessages, 
      boolean remediate) {
  }
  
  public static String getNaturalIdentity(BuildingEntity building, ReportTemplateEntity reportTemplate) {
    return new StringBuilder()
        .append(reportTemplate.getNaturalIdentity())
        .append(" - ")
        .append(building.getNaturalIdentity())
        .toString();
  }   
  
  public static class Mapper implements DtoMapper<PortfolioEntity, ReportInstanceEntity, ReportInstanceDto> {
    
    private static final Mapper INSTANCE = new Mapper();
    private Mapper() {
    }
    
    public static Mapper getInstance() {
      return INSTANCE;
    }
    
    public List<ReportInstanceDto> mapEntitiesToDtos(List<ReportInstanceEntity> entities) {
      
      List<ReportInstanceDto> list = new ArrayList<>();
      Iterator<ReportInstanceEntity> iterator = entities.iterator();
      while (iterator.hasNext()) {
        list.add(mapEntityToDto(iterator.next()));
      }
      return list;
    }
    
    @Override
    public ReportInstanceDto mapEntityToDto(ReportInstanceEntity reportInstance) {

      List<ReportInstanceEquipmentDto> reportInstanceEquipmentList = new ArrayList<>();
      Iterator<ReportInstanceEquipmentEntity> reportInstanceEquipmentIterator = reportInstance.getReportInstanceEquipment().iterator();
      while (reportInstanceEquipmentIterator.hasNext()) {
        
        ReportInstanceEquipmentEntity reportInstanceEquipment = reportInstanceEquipmentIterator.next();
        
        Integer reportTemplateEquipmentSpecId = reportInstanceEquipment.getReportTemplateEquipmentSpec().getPersistentIdentity();
        
        List<ReportInstancePointDto> reportInstancePoints = new ArrayList<>();
        Iterator<ReportInstancePointEntity> reportInstancePointIterator = reportInstanceEquipment.getReportInstancePoints().iterator();
        while (reportInstancePointIterator.hasNext()) {
          
          ReportInstancePointEntity reportInstancePoint = reportInstancePointIterator.next();
          
          AbstractReportTemplatePointSpecEntity reportTemplatePointSpec = reportInstancePoint.getReportTemplatePointSpec();
          if (reportTemplatePointSpec instanceof ReportTemplateStandardPointSpecEntity) {

            reportInstancePoints.add(ReportInstancePointDto
                .builder()
                .withReportTemplateEquipmentSpecId(reportTemplateEquipmentSpecId)
                .withReportTemplatePointSpecId(reportTemplatePointSpec.getPersistentIdentity())
                .withEquipmentId(reportInstanceEquipment.getEquipment().getPersistentIdentity())
                .withPointId(reportInstancePoint.getPoint().getPersistentIdentity())
                .withType(ReportTemplatePointSpecDto.TYPE_STANDARD)
                .build());        
            
          } else if (reportTemplatePointSpec instanceof ReportTemplateRulePointSpecEntity) {

            reportInstancePoints.add(ReportInstancePointDto
                .builder()
                .withReportTemplateEquipmentSpecId(reportTemplateEquipmentSpecId)
                .withReportTemplatePointSpecId(reportTemplatePointSpec.getPersistentIdentity())
                .withEquipmentId(reportInstanceEquipment.getEquipment().getPersistentIdentity())
                .withPointId(reportInstancePoint.getPoint().getPersistentIdentity())
                .withType(ReportTemplatePointSpecDto.TYPE_RULE)
                .build());  
            
          } else {
            throw new RuntimeException("Unsupported point template spec type: ["
                + reportTemplatePointSpec.getClassAndNaturalIdentity()
                + "].");
          }
        }
        
        reportInstanceEquipmentList.add(ReportInstanceEquipmentDto
            .builder()
            .withReportTemplateEquipmentSpecId(reportInstanceEquipment.getReportTemplateEquipmentSpec().getPersistentIdentity())
            .withEquipmentId(reportInstanceEquipment.getEquipment().getPersistentIdentity())
            .withReportInstancePoints(reportInstancePoints)
            .build());      
      }
      
      ReportInstanceDto reportInstanceDto = ReportInstanceDto
          .builder()
          .withId(reportInstance.getPersistentIdentity())
          .withReportTemplateId(reportInstance.getReportTemplate().getPersistentIdentity())
          .withBuildingId(reportInstance.getBuilding().getPersistentIdentity())
          .withPriority(reportInstance.getPriority().toString())
          .withCreatedAt(reportInstance.getCreatedAt())
          .withUpdatedAt(reportInstance.getUpdatedAt())
          .withReportInstanceEquipment(reportInstanceEquipmentList)
          .withStateHasChanged(reportInstance.getIsModified())
          .withNeedsEnabling(reportInstance.getNeedsEnabling())
          .withNeedsDisabling(reportInstance.getNeedsDisabling())
          .build();    
      
      return reportInstanceDto;    
    }
    
    public List<ReportInstanceStatusDto> mapEntitiesToStatusDtos(List<ReportInstanceEntity> entities) {
      
      List<ReportInstanceStatusDto> list = new ArrayList<>();
      Iterator<ReportInstanceEntity> iterator = entities.iterator();
      while (iterator.hasNext()) {
        list.add(mapEntityToStatusDto(iterator.next()));
      }
      return list;
    }
    
    public ReportInstanceStatusDto mapEntityToStatusDto(ReportInstanceEntity reportInstance) {

      try {
        
        Integer reportInstanceId = reportInstance.getPersistentIdentity();
        Integer reportTemplateId = reportInstance.getReportTemplate().getPersistentIdentity();
        Integer buildingId = reportInstance.getBuilding().getPersistentIdentity();
        List<ReportInstanceStatusErrorMessageDto> errorMessageDtoList = new ArrayList<>(); 

        List<ReportInstanceEquipmentDto> reportInstanceEquipmentDtoList = new ArrayList<>();
        
        for (ReportInstanceEquipmentErrorMessagesEntity reportInstanceEquipmentErrorMessages: reportInstance.getReportInstanceEquipmentErrorMessages()) {
          
          Integer equipmentId = reportInstanceEquipmentErrorMessages.getEquipment().getPersistentIdentity();
          
          errorMessageDtoList.add(ReportInstanceStatusErrorMessageDto
              .builder()
              .withReportTemplateId(reportTemplateId)
              .withBuildingId(buildingId)
              .withEquipmentId(equipmentId)
              .withErrorMessages(reportInstanceEquipmentErrorMessages.getErrorMessages())
              .build());
        }
        
        Iterator<ReportInstanceEquipmentEntity> reportInstanceEquipmentIterator = reportInstance.getReportInstanceEquipment().iterator();
        while (reportInstanceEquipmentIterator.hasNext()) {
          
          ReportInstanceEquipmentEntity reportInstanceEquipment = reportInstanceEquipmentIterator.next();
          
          Integer equipmentId = reportInstanceEquipment.getEquipment().getPersistentIdentity();
          Integer equipmentSpecId = reportInstanceEquipment.getReportTemplateEquipmentSpec().getPersistentIdentity();
          
          List<ReportInstancePointDto> reportInstancePointDtoList = new ArrayList<>();
          Iterator<ReportInstancePointEntity> reportInstancePointIterator = reportInstanceEquipment.getReportInstancePoints().iterator();
          while (reportInstancePointIterator.hasNext()) {
            
            ReportInstancePointEntity reportInstancePoint = reportInstancePointIterator.next();
            
            AbstractReportTemplatePointSpecEntity reportTemplatePointSpec = reportInstancePoint.getReportTemplatePointSpec();
            Integer pointSpecId = reportTemplatePointSpec.getPersistentIdentity();
            Integer pointId = reportInstancePoint.getPoint().getPersistentIdentity();
            
            if (reportTemplatePointSpec instanceof ReportTemplateStandardPointSpecEntity) {
              
              reportInstancePointDtoList.add(ReportInstancePointDto
                  .builder()
                  .withReportInstanceId(reportInstanceId)
                  .withReportTemplateEquipmentSpecId(equipmentSpecId)
                  .withReportTemplatePointSpecId(pointSpecId)
                  .withEquipmentId(equipmentId)
                  .withPointId(pointId)
                  .withType(ReportTemplatePointSpecDto.TYPE_STANDARD)
                  .build());
              
            } else {
              
              reportInstancePointDtoList.add(ReportInstancePointDto
                  .builder()
                  .withReportInstanceId(reportInstanceId)
                  .withReportTemplateEquipmentSpecId(equipmentSpecId)
                  .withReportTemplatePointSpecId(pointSpecId)
                  .withEquipmentId(equipmentId)
                  .withPointId(pointId)
                  .withType(ReportTemplatePointSpecDto.TYPE_RULE)
                  .build());            
            }
          }
          
          reportInstanceEquipmentDtoList.add(ReportInstanceEquipmentDto
              .builder()
              .withReportTemplateEquipmentSpecId(equipmentSpecId)
              .withEquipmentId(equipmentId)
              .withReportInstancePoints(reportInstancePointDtoList)
              .build());
        }
        
        ReportInstanceStatusDto reportInstanceStatusDto = ReportInstanceStatusDto
            .builder()
            .withId(reportInstanceId)
            .withReportTemplateId(reportTemplateId)
            .withBuildingId(buildingId)
            .withPriority(reportInstance.getPriority().toString())
            .withCreatedAt(reportInstance.getCreatedAt())
            .withUpdatedAt(reportInstance.getUpdatedAt())
            .withCandidateJson(OBJECT_MAPPER.get().writeValueAsString(reportInstanceEquipmentDtoList))
            .withErrorMessages(errorMessageDtoList)
            .withStateHasChanged(reportInstance.getIsModified())
            .withNeedsEnabling(reportInstance.getNeedsEnabling())
            .withNeedsDisabling(reportInstance.getNeedsDisabling())
            .withIgnored(reportInstance.isIgnored())
            .build();
        
        return reportInstanceStatusDto;
        
      } catch (JsonProcessingException e) {
        throw new RuntimeException("Unable to marshall to JSON", e);
      }        
    }    
    
    @Override
    public ReportInstanceEntity mapDtoToEntity(PortfolioEntity portfolio, ReportInstanceDto dto) {
      
      Integer reportTemplateId = dto.getReportTemplateId();
      ReportTemplateEntity reportTemplate = null;
      reportTemplate = DictionaryContext.getReportTemplatesContainer().getReportTemplate(reportTemplateId);
      if (reportTemplate == null) {
        throw new IllegalStateException("Cannot find report template with id: ["
            + reportTemplateId 
            + "] in "
            + DictionaryContext.getReportTemplatesContainer());
      }

      Integer buildingId = dto.getBuildingId();
      BuildingEntity building = null;
      try {
        building = portfolio.getChildBuilding(buildingId);
      } catch (EntityDoesNotExistException e) {
        LOGGER.error("Unable to find building with id: ["
            + buildingId
            + "] in portfolio: ["
            + this
            + "]");
      }

      Integer reportInstanceId = dto.getId();
      ReportInstanceEntity reportInstance = null;
      if (building != null) {
        try {
          reportInstance = building.getReportInstanceByReportTemplateId(reportTemplateId);
          reportInstance.setPersistentIdentity(reportInstanceId);
          reportInstance.setPriority(ReportPriority.get(dto.getPriority()));
        } catch (EntityDoesNotExistException e) {
          LOGGER.error("Unable to find report instance with report template id: ["
              + reportTemplateId
              + "] in building: ["
              + building
              + "]");
        }
      }
      
      if (building != null && reportInstance != null && dto.getReportInstanceEquipment() != null) {
        
        Set<ReportInstanceEquipmentEntity> reportInstanceEquipmentSet = new HashSet<>();
        for (ReportInstanceEquipmentDto reportInstanceEquipmentDto: dto.getReportInstanceEquipment()) {
          try {
            
            ReportTemplateEquipmentSpecEntity reportTemplateEquipmentSpec = reportTemplate.getEquipmentSpec(reportInstanceEquipmentDto.getReportTemplateEquipmentSpecId());
            EquipmentEntity equipment = (EquipmentEntity)portfolio.getChildNodeNullIfNotExists(reportInstanceEquipmentDto.getEquipmentId());
            if (equipment != null) {

              ReportInstanceEquipmentEntity reportInstanceEquipment = new ReportInstanceEquipmentEntity(
                  reportInstance,
                  reportTemplateEquipmentSpec,
                  equipment);
              
              Iterator<ReportInstancePointDto> reportInstancePointDtoIterator = reportInstanceEquipmentDto.getReportInstancePoints().iterator();
              while (reportInstancePointDtoIterator.hasNext()) {
                
                ReportInstancePointDto reportInstancePointDto = reportInstancePointDtoIterator.next();
                
                AbstractReportTemplatePointSpecEntity reportTemplatePointSpec = reportTemplateEquipmentSpec.getPointSpec(reportInstancePointDto.getReportTemplatePointSpecId());
                AbstractPointEntity point = (AbstractPointEntity)portfolio.getChildNodeNullIfNotExists(reportInstancePointDto.getPointId());
                
                // See if there exists another rule for the given rule template.
                if (point == null && reportTemplatePointSpec instanceof ReportTemplateRulePointSpecEntity) {
                
                  ReportTemplateRulePointSpecEntity rulePointSpec = (ReportTemplateRulePointSpecEntity)reportTemplatePointSpec;
                  AdRuleFunctionTemplateEntity ruleTemplate = rulePointSpec.getRuleTemplate();
                  AdRuleFunctionInstanceEntity adRuleFunctionInstance = (AdRuleFunctionInstanceEntity)equipment.getAdFunctionInstanceByTemplateIdNullIfNotExists(ruleTemplate.getPersistentIdentity());
                  if (adRuleFunctionInstance != null) {

                    AdFunctionInstanceOutputPointEntity outputPoint = adRuleFunctionInstance.getOutputPoint();
                    point = outputPoint.getPoint();
                  }
                }
                
                if (point != null) {

                  ReportInstancePointEntity reportInstancePoint = new ReportInstancePointEntity(
                      reportInstanceEquipment,
                      reportTemplatePointSpec,
                      point);
                  
                  reportInstanceEquipment.addReportInstancePoint(reportInstancePoint);
                } else {
                  if (reportInstance.isEnabled()) {
                    
                    reportInstance.setDisabled();
                    reportInstance.setIsModified("removed_non_existent_point");
                  }
                }
              }
              reportInstanceEquipmentSet.add(reportInstanceEquipment);
              
            } else {
              if (reportInstance.isEnabled()) {
                
                reportInstance.setDisabled();
                reportInstance.setIsModified("removed_non_existent_equipment");
              }
            }
            
          } catch (Exception e) {
            if (reportInstance != null) {
              if (reportInstance.isEnabled()) {
                
                reportInstance.setDisabled();
                reportInstance.setIsModified("removed_non_existent_equipment");
              }
              LOGGER.error("Unable to map: " + reportInstanceEquipmentDto + "\n error: "
                + e.getMessage(), e);
            }
          }
        }
        reportInstance.setGreenEquipment(reportInstanceEquipmentSet);
      }
      return reportInstance;
    }
    
    public void mapStatusDtosToEntities(PortfolioEntity portfolio, List<ReportInstanceStatusDto> reportInstanceStatusDtoList) {

      for (ReportInstanceStatusDto reportInstanceStatusDto: reportInstanceStatusDtoList) {
        
        mapStatusDtoToEntity(portfolio, reportInstanceStatusDto);
      }
    }
    
    public void mapStatusDtoToEntity(PortfolioEntity portfolio, ReportInstanceStatusDto dto) {

      Integer reportTemplateId = dto.getReportTemplateId();
      
      Integer buildingId = dto.getBuildingId();
      BuildingEntity building = null;
      try {
        building = portfolio.getChildBuilding(buildingId);
      } catch (EntityDoesNotExistException e) {
        LOGGER.error("Unable to find building with id: ["
            + buildingId
            + "] in portfolio: ["
            + this
            + "]");
      }

      ReportInstanceEntity reportInstance = null;
      if (building != null) {
        try {
          reportInstance = building.getReportInstanceByReportTemplateId(reportTemplateId);
          reportInstance.setPriority(ReportPriority.get(dto.getPriority()));
        } catch (EntityDoesNotExistException e) {
          LOGGER.error("Unable to find report instance with report template id: ["
              + reportTemplateId
              + "] in building: ["
              + building
              + "]");
        }
      }
      
      if (building != null && reportInstance != null) {
        
        if (dto.getIgnored()) {
          reportInstance.setIsIgnored();  
        }
        
        // ADD RED EQUIPMENT ERROR MESSAGES
        if (dto.getErrorMessages() != null) {
          
          for (ReportInstanceStatusErrorMessageDto errorMessageDto: dto.getErrorMessages()) {

            try {

              reportInstance.addReportInstanceEquipmentErrorMessages(new ReportInstanceEquipmentErrorMessagesEntity(
                  reportInstance,
                  portfolio.getEquipment(errorMessageDto.getEquipmentId()),
                  errorMessageDto.getErrorMessages()));                
              
            } catch (Exception e) {
              LOGGER.error("Unable to map/add: ["
                  + errorMessageDto
                  + "] to: ["
                  + reportInstance
                  + "]", e);
            }
          }
        }
        
        // IF "DISABLED", ADD GREEN EQUIPMENT FROM CANDIDATE JSON
        if (!reportInstance.isEnabled()) {

          Set<ReportInstanceEquipmentEntity> reportInstanceEquipmentSet = new TreeSet<>();
          ReportTemplateEntity reportTemplate = reportInstance.getReportTemplate();
          List<ReportInstanceEquipmentDto> reportInstanceEquipmentDtoList = null;
          String candidateJson = dto.getCandidateJson();

          if (candidateJson != null) {
            try {
            reportInstanceEquipmentDtoList = AbstractEntity.OBJECT_MAPPER.get().readValue(
                candidateJson, 
                new TypeReference<List<ReportInstanceEquipmentDto>>() {});
            } catch (Exception e) {
              reportInstance.setNeedsRebuilding(true);
              LOGGER.error("Unable to deserialize report instance candidateJson: "
                  + candidateJson);
            }
          } else {
            reportInstanceEquipmentDtoList =  new ArrayList<>();
          }
          
          for (ReportInstanceEquipmentDto reportInstanceEquipmentDto: reportInstanceEquipmentDtoList) {

            ReportTemplateEquipmentSpecEntity equipmentSpec = null;
            try {
              equipmentSpec = reportTemplate.getEquipmentSpec(reportInstanceEquipmentDto.getReportTemplateEquipmentSpecId());
            } catch (EntityDoesNotExistException e) {
              reportInstance.setNeedsRebuilding(true);
              LOGGER.error("Unable to find equipment spec with id: ["
                  + reportInstanceEquipmentDto.getReportTemplateEquipmentSpecId()
                  + "] in reportTemplate with id: ["
                  + reportTemplate.getPersistentIdentity()
                  + "]");
            }
            EquipmentEntity equipment = null;
            try {
              equipment =  building.getChildEquipment(reportInstanceEquipmentDto.getEquipmentId());
            } catch (EntityDoesNotExistException e) {
              reportInstance.setNeedsRebuilding(true);
              LOGGER.error("Unable to find equipment with id: ["
                  + reportInstanceEquipmentDto.getEquipmentId()
                  + "] in building with id: ["
                  + building.getPersistentIdentity()
                  + "] for report template with id: ["
                  + reportTemplate.getPersistentIdentity()
                  + "]");
            }
            if (equipmentSpec != null && equipment != null) {
              
              ReportInstanceEquipmentEntity reportInstanceEquipment = new ReportInstanceEquipmentEntity(
                  reportInstance,
                  equipmentSpec,
                  equipment);
              reportInstanceEquipmentSet.add(reportInstanceEquipment);
              
              Iterator<ReportInstancePointDto> pointIterator = reportInstanceEquipmentDto.getReportInstancePoints().iterator();
              while (pointIterator.hasNext()) {
                
                ReportInstancePointDto reportInstancePointDto = pointIterator.next();
                AbstractReportTemplatePointSpecEntity pointSpec = null;
                try {
                  pointSpec = equipmentSpec.getPointSpec(reportInstancePointDto.getReportTemplatePointSpecId());  
                } catch (EntityDoesNotExistException e) {
                  reportInstance.setNeedsRebuilding(true);
                  LOGGER.error("Unable to find point spec with id: ["
                      + reportInstancePointDto.getReportTemplatePointSpecId()
                      + "] in equipment spec with id: ["
                      + equipmentSpec.getPersistentIdentity()
                      + "]");
                }
                AbstractPointEntity point = null;
                try {
                  point = (AbstractPointEntity)portfolio.getChildNode(reportInstancePointDto.getPointId());
                } catch (EntityDoesNotExistException e) {
                  reportInstance.setNeedsRebuilding(true);
                  LOGGER.error("Unable to find point with id: ["
                      + reportInstancePointDto.getPointId()
                      + "] in building with id: ["
                      + building.getPersistentIdentity()
                      + "] for report template with id: ["
                      + reportTemplate.getPersistentIdentity()
                      + "], DTO: ["
                      + reportInstancePointDto
                      + "], reportInstance: ["
                      + reportInstance);
                }
                if (pointSpec != null && point != null) {
                  try {
                    reportInstanceEquipment.addReportInstancePoint(new ReportInstancePointEntity(
                        reportInstanceEquipment,
                        pointSpec,
                        point));
                  } catch (EntityAlreadyExistsException e) {
                    reportInstance.setNeedsRebuilding(true);
                    LOGGER.error("Unable to add report instance point with id: ["
                        + point.getPersistentIdentity()
                        + "] with point spec with id: ["
                        + pointSpec.getPersistentIdentity()
                        + "] in building with id: ["
                        + building.getPersistentIdentity()
                        + "] for report template with id: ["
                        + reportTemplate.getPersistentIdentity()
                        + "] because it already exists");
                  }
                } else {
                  reportInstance.setNeedsRebuilding(true);
                  LOGGER.error("Unable to add report instance point with id: ["
                      + "] in building with id: ["
                      + building.getPersistentIdentity()
                      + "] for report template with id: ["
                      + reportTemplate.getPersistentIdentity()
                      + "] because either the pointSpec or point is null, invalidating.");
                }
              }
            }
          }
          reportInstance.setGreenEquipment(reportInstanceEquipmentSet);
        }
      }
    }  
  }
}
//@formatter:off