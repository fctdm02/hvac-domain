//@formatter:off
package com.djt.hvac.domain.model.dictionary.template.report;

import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.djt.hvac.domain.model.common.AbstractPersistentEntity;
import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.SimpleValidationMessage;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.common.validation.SimpleValidationMessage.MessageType;
import com.djt.hvac.domain.model.dictionary.template.report.enums.ReportPriority;

public class ReportTemplateEntity extends AbstractPersistentEntity {
  private static final long serialVersionUID = 1L;
  
  private final String name;
  private final String description;
  private final Boolean isInternal;
  private final Boolean isBeta;
  private final ReportPriority defaultPriority;
  private Set<ReportTemplateEquipmentSpecEntity> equipmentSpecs = new TreeSet<>();
  
  private transient ReportTemplateEquipmentSpecEntity _firstEquipmentSpec;
  
  public ReportTemplateEntity(
      Integer persistentIdentity,
      String name,
      String description,
      Boolean isInternal,
      Boolean isBeta) {
    super(persistentIdentity);
    requireNonNull(name, "name cannot be null");
    requireNonNull(description, "description cannot be null");
    requireNonNull(isInternal, "isInternal cannot be null");
    requireNonNull(isBeta, "isBeta cannot be null");
    this.name = name;
    this.description = description;
    this.isInternal = isInternal;
    this.isBeta = isBeta;
    this.defaultPriority = deriveDefaultPriority(persistentIdentity);
  }
  
  public ReportPriority getDefaultPriority() {
    return this.defaultPriority;
  }
  
  /**
   * <pre>
    REQUIREMENTS:
    =============
    Report default priorities:
      High priority:
        - AHU Operations (2)
        - Air Terminal Unit Operations (1)
      Medium priority:
        - AHU Economizer Performance (12)
        - AHU Controller Performance (10)
        - VAV Controller Performance (7)
      Low Priority:
        - Everything else
        
    REPORT TEMPLATES: (as of 1/28/2022)
    ===================================
    ID  Name
    --  -------------------------------
    10  AHU Controller Performance
    12  AHU Economizer Performance
    2   AHU Operations
    3   AHU Performance
    8   AHU Schedule
    1   Air Terminal Unit Operations
    4   High Demand Zones
    13  IAQ Scorecard
    6   OR Performance
    5   OR Scorecard
    9   RTU DX Staging - Short Cycling
    7   VAV Controller Performance
    11  VAV Overrides    
   * </pre>
   * 
   * @param persistentIdentity The report template persistent identity
   */
  public static final ReportPriority deriveDefaultPriority(Integer persistentIdentity) {
    
    if (persistentIdentity == null) {
      throw new IllegalArgumentException("persistentIdentity cannot be null");
    }
    
    ReportPriority defaultPriority = DEFAULT_REPORT_PRIORITIES.get(persistentIdentity);
    if (defaultPriority == null) {
      defaultPriority = ReportPriority.LOW;
    }
    
    return defaultPriority;
  }
  
  private static final Map<Integer, ReportPriority> DEFAULT_REPORT_PRIORITIES = new HashMap<>();
  
  static {
    DEFAULT_REPORT_PRIORITIES.put(Integer.valueOf(2), ReportPriority.HIGH);
    DEFAULT_REPORT_PRIORITIES.put(Integer.valueOf(1), ReportPriority.HIGH);
    
    DEFAULT_REPORT_PRIORITIES.put(Integer.valueOf(12), ReportPriority.MEDIUM);
    DEFAULT_REPORT_PRIORITIES.put(Integer.valueOf(10), ReportPriority.MEDIUM);
    DEFAULT_REPORT_PRIORITIES.put(Integer.valueOf(7), ReportPriority.MEDIUM);
  }
    
  public boolean addEquipmentSpec(ReportTemplateEquipmentSpecEntity equipmentSpec) throws EntityAlreadyExistsException {
    return addChild(equipmentSpecs, equipmentSpec, this);
  }
  
  public Set<ReportTemplateEquipmentSpecEntity> getEquipmentSpecs() {
    return equipmentSpecs;
  }
  
  public ReportTemplateEquipmentSpecEntity getEquipmentSpec(Integer persistentIdentity) throws EntityDoesNotExistException {
   
    Iterator<ReportTemplateEquipmentSpecEntity> iterator = equipmentSpecs.iterator();
    while (iterator.hasNext()) {
      
      ReportTemplateEquipmentSpecEntity equipmentSpec = iterator.next();
      if (equipmentSpec.getPersistentIdentity().equals(persistentIdentity)) {
        
        return equipmentSpec;
      }
    }
    throw new EntityDoesNotExistException("Equipment spec with id: [" + persistentIdentity + "] does not exist.");
  }
  
  
  public ReportTemplateEquipmentSpecEntity getFirstEquipmentSpec() {
    
    if (_firstEquipmentSpec == null) {

      if (equipmentSpecs.isEmpty()) {
        throw new IllegalStateException("Report template: ["
            + getNaturalIdentity() 
            + "] does not have any equipment specs.");
      }
      
      if (equipmentSpecs.size() > 1) {
        throw new IllegalStateException("Report template: ["
            + getNaturalIdentity() 
            + "] has more than one equipment spec: "
            + equipmentSpecs);
      }
      
      Iterator<ReportTemplateEquipmentSpecEntity> iterator = equipmentSpecs.iterator();
      while (iterator.hasNext()) {
        
        _firstEquipmentSpec = iterator.next();
        break;
      }
    }
    return _firstEquipmentSpec;
  }
  
  public String getName() {
    return name;
  }
  
  public String getDescription() {
    return description;
  }
  
  public Boolean getIsInternal() {
    return isInternal;
  }

  public Boolean getIsBeta() {
    return isBeta;
  }
  
  public String getNaturalIdentity() {
    return getName();
  }  
  
  @Override
  public void validate(
      Set<IssueType> issueTypes,
      List<ValidationMessage> validationMessages, 
      boolean remediate) {
    
    // This type of validation does not apply for dictionary data.
  }
  
  @Override
  public void validateSimple(List<SimpleValidationMessage> simpleValidationMessages) {
    
    if (equipmentSpecs.isEmpty()) {

      simpleValidationMessages.add(new SimpleValidationMessage(
          MessageType.ERROR,
          this.getNaturalIdentity(),
          "equipmentSpecs",
          "Cannot be empty")); 
      
    } else if (equipmentSpecs.size() > 1) {
        
      simpleValidationMessages.add(new SimpleValidationMessage(
          MessageType.ERROR,
          this.getNaturalIdentity(),
          "equipmentSpecs",
          "Currently, cannot be greater than one")); 
        
    } else {

      Iterator<ReportTemplateEquipmentSpecEntity> iterator = equipmentSpecs.iterator();
      while (iterator.hasNext()) {
        
        iterator.next().validateSimple(simpleValidationMessages);
      }
    }
  }
}
//@formatter:on