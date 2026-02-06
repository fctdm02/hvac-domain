//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.point.computed.async.custom;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.AbstractPersistentEntity;
import com.djt.hvac.domain.model.common.dsl.computedpoint.v2.ComputedPointExpression;
import com.djt.hvac.domain.model.common.dsl.computedpoint.v2.Result;
import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;

public class TemporalAsyncComputedPointConfigEntity extends AbstractPersistentEntity {
  
  private static final long serialVersionUID = 1L;
  private static final Logger LOGGER = LoggerFactory.getLogger(TemporalAsyncComputedPointConfigEntity.class);
  
  private static final Map<String, ComputedPointExpression> COMPUTED_POINT_EXPRESSION_CACHE = new HashMap<>();
  
  public static ComputedPointExpression getFromComputedPointExpressionCache(String formula) {
    
    synchronized (COMPUTED_POINT_EXPRESSION_CACHE) {
    
      ComputedPointExpression expr = COMPUTED_POINT_EXPRESSION_CACHE.get(formula);
      if (expr == null) {
       
        expr = ComputedPointExpression.parse(formula);
        COMPUTED_POINT_EXPRESSION_CACHE.put(formula, expr);
      }
      return expr;
    }
  }
  
  public static void resetComputedPointExpressionCache() {
    
    COMPUTED_POINT_EXPRESSION_CACHE.clear();
  }
  
  private CustomAsyncComputedPointEntity parentCustomComputedPoint;
  private LocalDate effectiveDate;
  private String formula;
  private String description;
  private Set<FormulaVariableEntity> childVariables = new TreeSet<>();
  
  public TemporalAsyncComputedPointConfigEntity() {}
  
  public TemporalAsyncComputedPointConfigEntity(
      CustomAsyncComputedPointEntity parentCustomComputedPoint,
      LocalDate effectiveDate,
      String formula,
      String description) {
    this(
        null,
        parentCustomComputedPoint,
        effectiveDate,
        formula,
        description);
  }
  
  public TemporalAsyncComputedPointConfigEntity(
      Integer persistentIdentity,
      CustomAsyncComputedPointEntity parentCustomComputedPoint,
      LocalDate effectiveDate,
      String formula,
      String description) {
    super(persistentIdentity);
    requireNonNull(parentCustomComputedPoint, "parentCustomComputedPoint cannot be null");
    requireNonNull(effectiveDate, "effectiveDate cannot be null");
    requireNonNull(formula, "formula cannot be null");
    this.parentCustomComputedPoint = parentCustomComputedPoint;
    this.effectiveDate = effectiveDate;
    this.formula = formula;
    this.description = description;
  }

  public CustomAsyncComputedPointEntity getParentCustomComputedPoint() {
    return parentCustomComputedPoint;
  }

  public LocalDate getEffectiveDate() {
    return effectiveDate;
  }
  
  public void setEffectiveDate(LocalDate effectiveDate) {
    
    if (effectiveDate != null && !effectiveDate.equals(this.effectiveDate)) {
      
      this.effectiveDate = effectiveDate;
      this.setIsModified("effectiveDate");
    }
  }

  public String getFormula() {
    return formula;
  }

  public void setFormula(String formula) {
    
    if (formula != null && !formula.equals(this.formula)) {
      
      this.formula = formula;
      this.setIsModified("formula");
    }
  }
  
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    
    if (description != null && !description.equals(this.description)) {
      
      this.description = description;
      this.setIsModified("description");
    }
  }
  
  public Set<FormulaVariableEntity> getChildVariables() {
    return childVariables;
  }

  public boolean addChildVariable(FormulaVariableEntity childVariable) throws EntityAlreadyExistsException {
    return addChild(childVariables, childVariable, this);
  }

  public FormulaVariableEntity getChildVariableByMappablePointId(Integer parentMappablePointId) throws EntityDoesNotExistException {
    for (FormulaVariableEntity childVariable:  childVariables) {
      if (childVariable.getParentPoint().getPersistentIdentity().equals(parentMappablePointId)) {
        return childVariable;
      }
    }
    throw new EntityDoesNotExistException("Custom point temporal config: ["
        + getNaturalIdentity()
        + "] does not have a child variable whose point id is: ["
        + parentMappablePointId
        + "]");
  }

  public FormulaVariableEntity getChildVariableByMappablePointIdNullIfNotExists(Integer parentMappablePointId) {
    for (FormulaVariableEntity childVariable:  childVariables) {
      if (childVariable.getParentPoint().getPersistentIdentity().equals(parentMappablePointId)) {
        return childVariable;
      }
    }
    return null;
  }
  
  public FormulaVariableEntity removeChildVariable(Integer parentMappablePointId) throws EntityDoesNotExistException {
    
    FormulaVariableEntity childVariable = getChildVariableByMappablePointId(parentMappablePointId);
    childVariable.setIsDeleted();
    return childVariable;
  }
  
  public List<String> validateFormula() {
    
    List<String> errors = new ArrayList<>();
    try {

      Set<String> childVariableNames = new HashSet<>();
      for (FormulaVariableEntity childVariable: childVariables) {
        
        if (childVariable.getParentPoint() == null) {
         
          errors.add("Temporal config has a variable: ["
              + childVariable.getName() 
              + "] whose underlying point no longer exists");
          
        } else if (!childVariable.getParentPoint().getIsDeleted()) {
          
          childVariableNames.add(childVariable.getName().toLowerCase());
          
        }
      }
      
      ComputedPointExpression expr = getFromComputedPointExpressionCache(formula);
      
      Set<String> formulaVariables = expr.getVariables();
      for (String formulaVariable: formulaVariables) {
        
        if (!childVariableNames.contains(formulaVariable.toLowerCase())) {
          
          errors.add("Formula variable: ["
              + formulaVariable 
              + "] is not mapped to a point");
        }
      }
    } catch (Throwable t) {
      errors.add("Formula: [" + formula + "] has an error: ["+ t.getMessage() + "]");
    }
    if (!errors.isEmpty()) {
      
      LOGGER.warn("Custom point temporal config: [{}] has an invalid formula: [{}], errors: {}",
          getNaturalIdentity(),
          formula,
          errors);
    }
    return errors;
  }
  
  public Result evaluateFormula(
      long timeMillis,
      Map<String, Double> variableValues,
      Map<String, String> functionState) {
    
    ComputedPointExpression expr = getFromComputedPointExpressionCache(formula);
    
    return expr.eval(
        timeMillis, 
        variableValues, 
        functionState);
  }
  
  @Override
  public String getNaturalIdentity() {
    
    return new StringBuilder()
        .append(parentCustomComputedPoint.getNaturalIdentity())
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
  
  public int compareTo(AbstractEntity obj) {
    
    int compareTo = this.getNaturalIdentity().compareTo(obj.getNaturalIdentity());
    if (compareTo == 0) {
      
      if (obj instanceof TemporalAsyncComputedPointConfigEntity) {
        compareTo = this.effectiveDate.compareTo(((TemporalAsyncComputedPointConfigEntity)obj).effectiveDate);
      } else {
        throw new IllegalStateException(obj 
            + " is not an instance of TemporalAsyncComputedPointConfigEntity, rather, it is a: " 
            + obj.getClass().getSimpleName());
      }
    }
    return compareTo;
  }
}
//@formatter:on