//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.building;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.AbstractPersistentEntity;
import com.djt.hvac.domain.model.common.dsl.baseline.BaselineExpression;
import com.djt.hvac.domain.model.common.dsl.baseline.ParseException;
import com.djt.hvac.domain.model.common.dsl.baseline.Variable;
import com.djt.hvac.domain.model.common.dsl.baseline.VariableId;
import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.BuildingUtilityType;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.UtilityComputationInterval;

public class BuildingTemporalConfigEntity extends AbstractPersistentEntity {
  private static final long serialVersionUID = 1L;
  private static final Logger LOGGER = LoggerFactory.getLogger(BuildingTemporalConfigEntity.class);

  private final BuildingEntity parentBuilding;
  private LocalDate effectiveDate;
  private Integer squareFeet;
  private Set<BuildingTemporalUtilityEntity> childUtilities = new TreeSet<>();
  
  public BuildingTemporalConfigEntity(
      BuildingEntity parentBuilding,
      LocalDate effectiveDate,
      Integer squareFeet) {
    this(
        null,
        parentBuilding,
        effectiveDate,
        squareFeet);
  }
  
  public BuildingTemporalConfigEntity(
      Integer persistentIdentity,
      BuildingEntity parentBuilding,
      LocalDate effectiveDate,
      Integer squareFeet) {
    super(persistentIdentity);
    requireNonNull(parentBuilding, "parentBuilding cannot be null");
    requireNonNull(effectiveDate, "effectiveDate cannot be null");
    requireNonNull(squareFeet, "squareFeet cannot be null");
    this.parentBuilding = parentBuilding;
    this.effectiveDate = effectiveDate;
    this.squareFeet = squareFeet;
  }

  public BuildingEntity getParentBuilding() {
    return parentBuilding;
  }

  public LocalDate getEffectiveDate() {
    return effectiveDate;
  }

  public void setEffectiveDate(LocalDate effectiveDate) {
    
    if (this.effectiveDate == null) {
      if (effectiveDate == null) {
        
        // BOTH NULL: DO NOTHING
        
      } else {

        // THIS NULL AND INCOMING NOT NULL
        this.effectiveDate = effectiveDate;
        setIsModified("effectiveDate");
        
      }
    } else {
      if (effectiveDate == null) {

        // THIS NOT NULL AND INCOMING NULL
        this.effectiveDate = effectiveDate;
        setIsModified("effectiveDate");
        
      } else {

        if (!this.effectiveDate.equals(effectiveDate)) {

          // BOTH NOT NULL AND EQUAL TO NOT EQUAL TO EACH OTHER
          this.effectiveDate = effectiveDate;
          setIsModified("effectiveDate");
          
        } else {
          
          // BOTH NOT NULL AND EQUAL TO EACH OTHER: DO NOTHING 
          
        }
      }
    }    
  }

  public void setSquareFeet(Integer squareFeet) {
    
    if (this.squareFeet == null) {
      if (squareFeet == null) {
        
        // BOTH NULL: DO NOTHING
        
      } else {

        // THIS NULL AND INCOMING NOT NULL
        this.squareFeet = squareFeet;
        setIsModified("squareFeet");
        
      }
    } else {
      if (squareFeet == null) {

        // THIS NOT NULL AND INCOMING NULL
        this.squareFeet = squareFeet;
        setIsModified("squareFeet");
        
      } else {

        if (!this.squareFeet.equals(squareFeet)) {

          // BOTH NOT NULL AND EQUAL TO NOT EQUAL TO EACH OTHER
          this.squareFeet = squareFeet;
          setIsModified("squareFeet");
          
        } else {
          
          // BOTH NOT NULL AND EQUAL TO EACH OTHER: DO NOTHING 
          
        }
      }
    }    
  }

  public Integer getSquareFeet() {
    return squareFeet;
  }

  public Set<BuildingTemporalUtilityEntity> getChildUtilities() {
    return childUtilities;
  }

  public boolean addChildUtility(BuildingTemporalUtilityEntity childUtility) throws EntityAlreadyExistsException {
    return addChild(childUtilities, childUtility, this);
  }

  public BuildingTemporalUtilityEntity getChildUtilityById(Integer utilityId) throws EntityDoesNotExistException {
    
    for (BuildingTemporalUtilityEntity childUtility: childUtilities) {
      if (childUtility.getBuildingUtilityType().getId() == utilityId.intValue()) {
        return childUtility;
      }
    }
    throw new EntityDoesNotExistException("Building temporal config: ["
        + getNaturalIdentity()
        + "] does not have a child utility whose id is: ["
        + utilityId
        + "]");
  }

  public BuildingTemporalUtilityEntity removeChildUtility(Integer utilityId) throws EntityDoesNotExistException {
    
    BuildingTemporalUtilityEntity childUtility = getChildUtilityById(utilityId);
    childUtilities.remove(childUtility);
    return childUtility;
  }

  public void removeAllChildUtilities()  {
    
    childUtilities.clear();
  }
  
  public List<String> validateFormula() {
    
    List<String> errors = new ArrayList<>();
    
    // Verify that a weather station has been associated to the building.
    if (getParentBuilding().getWeatherStation() == null) {
      errors.add("A weather station must be associated with the building first.");
    }
    
    // Verify the child utilities.
    for (BuildingTemporalUtilityEntity utility: childUtilities) {
      
      BuildingUtilityType utilityType = utility.getBuildingUtilityType();
      String formula = utility.getFormula();
      if (formula != null && !formula.trim().isEmpty()) {
        try {
          
          BaselineExpression expr = BaselineExpression.parse(utility.getFormula());
          UtilityComputationInterval interval = utility.getComputationInterval();
          Set<VariableId<?>> variableIds = expr.getVariables().stream().map(Variable::getId).collect(Collectors.toSet());
          for (VariableId<?> variableId : variableIds) {
            if (!variableId
                .getComputationIntervals()
                .contains(interval.toBaselineExpressionComputationInterval())) {
              
              errors.add("Invalid meter baseline for "
                  + utilityType.getName()
                  + ": The formula references the ''"
                  + variableId
                  + "'' variable, which is not supported for the "
                  + interval.getName().toLowerCase()
                  + " computation interval.");
            }
          }
        } catch (ParseException pe) {
          errors.add("Invalid meter baseline for "
              + utilityType.getName()
              + ": "
              + pe.getMessage());
        }
      }
    }
    if (!errors.isEmpty()) {
      
      LOGGER.warn("Building temporal data: [{}] has errors: {}",
          getNaturalIdentity(),
          errors);
    }
    return errors;
  }
  
  @Override
  public String getNaturalIdentity() {
    
    return new StringBuilder()
        .append(parentBuilding.getNaturalIdentity())
        .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
        .append(effectiveDate)
        .toString();
  }  
  
  @Override
  public void validate(
      Set<IssueType> issueTypes,
      List<ValidationMessage> validationMessages, 
      boolean remediate) {
  }
  
  public int compareTo(AbstractEntity that) {
    
    int compareTo = this.getNaturalIdentity().compareTo(that.getNaturalIdentity());
    if (compareTo == 0) {
      
      compareTo = this.effectiveDate.compareTo(((BuildingTemporalConfigEntity)that).effectiveDate);
    }
    return compareTo;
  }
}
//@formatter:on