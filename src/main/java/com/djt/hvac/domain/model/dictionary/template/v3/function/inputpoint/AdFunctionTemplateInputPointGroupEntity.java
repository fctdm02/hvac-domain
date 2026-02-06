//@formatter:off
package com.djt.hvac.domain.model.dictionary.template.v3.function.inputpoint;

import static java.util.Objects.requireNonNull;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.AbstractPersistentEntity;
import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.dictionary.template.v3.function.AdFunctionModuleEntity;
import com.djt.hvac.domain.model.dictionary.template.v3.function.AdFunctionTemplateInputConstantEntity;
import com.udojava.evalex.Expression;

/**
 * @author tommyers
 *
 */
public class AdFunctionTemplateInputPointGroupEntity extends AbstractPersistentEntity {
  private static final long serialVersionUID = 1L;
  
  private final AdFunctionModuleEntity parentAdFunction;
  private final String name;
  private final Integer priority;
  
  // e.g. (EffCoolSp - EffHeatSp) < DEADBAND
  // The expression has point template/constant names that have meaning for the AD function template.
  private final String faultExpressionTemplate;
  
  // e.g. (P1 - P2) < C1
  // The expression been "normalized" such that point templates have been replaced by Pn, where n is the 
  // sequence number of the input point, and where constants have been replaced by Cm, where m is the 
  // sequence number of the input constant.  
  //
  // This is done in order to minimize the number of actual expressions needed.  The default implementation
  // will be a DSL, but for a given normalized fault expression template, there could exist a "hardcoded"
  // java function module, accessed by a O(1) lookup.
  //
  // NOTE: Ideally, the act of normalizing an expression should be done programmatically.
  private String normalizedFaultExpressionTemplate = null;
  
  private final Set<AbstractAdFunctionTemplateInputPointEntity> inputPoints = new TreeSet<>();
  
  public AdFunctionTemplateInputPointGroupEntity(
      Integer persistentIdentity,
      AdFunctionModuleEntity parentAdFunction,
      String name,
      Integer priority,
      String faultExpressionTemplate) {
    super(persistentIdentity);
    requireNonNull(parentAdFunction, "parentAdFunction cannot be null");
    requireNonNull(name, "name cannot be null");
    requireNonNull(priority, "priority cannot be null");
    requireNonNull(faultExpressionTemplate, "faultExpressionTemplate cannot be null");
    this.parentAdFunction = parentAdFunction;
    this.name = name;
    this.priority = priority;
    this.faultExpressionTemplate = faultExpressionTemplate;
  }

  public AdFunctionModuleEntity getParentAdFunction() {
    return parentAdFunction;
  }
  
  public String getName() {
    return name;
  }
  
  public Integer getPriority() {
    return priority;
  }
  
  public String getFaultExpressionTemplate() {
    return faultExpressionTemplate;
  }

  public boolean addInputPoint(AbstractAdFunctionTemplateInputPointEntity inputPoint) throws EntityAlreadyExistsException {
    return addChild(inputPoints, inputPoint, this);
  }

  public Set<AbstractAdFunctionTemplateInputPointEntity> getInputPoints() {
    return inputPoints;
  }

  public AbstractAdFunctionTemplateInputPointEntity getInputPoint(String inputPointName) {
    
    Iterator<AbstractAdFunctionTemplateInputPointEntity> iterator = inputPoints.iterator();
    while (iterator.hasNext()) {
      AbstractAdFunctionTemplateInputPointEntity inputPoint = iterator.next();
      if (inputPoint.getName().equals(inputPointName)) {
        return inputPoint;
      }
    }
    throw new IllegalStateException("AD function template input point with name: "
        + inputPointName
        + " not found.");
  }
  
  public AbstractAdFunctionTemplateInputPointEntity getInputPoint(Integer inputPointId) {
    
    Iterator<AbstractAdFunctionTemplateInputPointEntity> iterator = inputPoints.iterator();
    while (iterator.hasNext()) {
      AbstractAdFunctionTemplateInputPointEntity inputPoint = iterator.next();
      if (inputPoint.getPersistentIdentity().equals(inputPointId)) {
        return inputPoint;
      }
    }
    throw new IllegalStateException("AD function template input point with id: "
        + inputPointId
        + " not found.");
  }
  
  public AbstractAdFunctionTemplateInputPointEntity getInputPoint(
      Set<String> haystackTags, 
      String thatCurrentObjectExpression) {
    
    if (thatCurrentObjectExpression == null) {
      thatCurrentObjectExpression = "";
    }
    
    Iterator<AbstractAdFunctionTemplateInputPointEntity> iterator = inputPoints.iterator();
    while (iterator.hasNext()) {
      
      AbstractAdFunctionTemplateInputPointEntity inputPoint = iterator.next();
      
      String thisCurrentObjectExpression = inputPoint.getCurrentObjectExpression();
      if (thisCurrentObjectExpression == null) {
        thisCurrentObjectExpression = "";
      }
      
      if (inputPoint.getNormalizedTagsAsSet().equals(haystackTags) 
          && thisCurrentObjectExpression.equals(thatCurrentObjectExpression)) {
        
          return inputPoint;
      }
    }
    return null;
  }
  
  /**
   * 
   * @param instanceInputConstants A map containing input constant values keyed by name
   * @param instanceInputPoints A map containing input point values keyed by name
   * 
   * @return <code>true</code> if the expression evaluates to true, regardless of the delay constant
   */
  public boolean evaluateExpression(
      Map<String, String> instanceInputConstants,
      Map<String, String> instanceInputPoints) {
    
    String faultExpression = getNormalizedFaultExpressionTemplate(); 
    
    // Do replacement for instance input constants.
    for (Map.Entry<String, String> entry: instanceInputConstants.entrySet()) {
      
      AdFunctionTemplateInputConstantEntity ic = getParentAdFunction().getInputConstant(entry.getKey());
      
      faultExpression = faultExpression.replace("C" + ic.getSequenceNumber(), entry.getValue());
    }

    // Do replacement for instance input points.
    for (Map.Entry<String, String> entry: instanceInputPoints.entrySet()) {
      
      AbstractAdFunctionTemplateInputPointEntity ip = getInputPoint(entry.getKey());
      
      faultExpression = faultExpression.replace("P" + ip.getSequenceNumber(), entry.getValue());
    }
    
    Expression expressionEvaluator = new Expression(faultExpression);
    
    BigDecimal result = expressionEvaluator.eval();
    if (result.equals(BigDecimal.ZERO)) {
      return false;
    }
    return true;
  }
  
  @Override
  public String getNaturalIdentity() {
    
    return new StringBuilder()
        .append(parentAdFunction.getNaturalIdentity())
        .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
        .append(name)
        .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
        .append(priority)
        .toString();
  }
  
  @Override
  public void validate(
      Set<IssueType> issueTypes,
      List<ValidationMessage> validationMessages, 
      boolean remediate) {
  }
  
  public void normalizeFaultExpressionTemplate() {

    String faultExpression = faultExpressionTemplate;
    
    // Do replacement for template input constants.
    for (AdFunctionTemplateInputConstantEntity ic: getParentAdFunction().getInputConstants()) {
      
      faultExpression = faultExpression.replace(ic.getName(), "C" + ic.getSequenceNumber());
    }

    // Do replacement for template input points.
    for (AbstractAdFunctionTemplateInputPointEntity ip: getInputPoints()) {
      
      faultExpression = faultExpression.replace(ip.getName(), "P" + ip.getSequenceNumber());
    }
    
    normalizedFaultExpressionTemplate = faultExpression;    
  }
  
  public String getNormalizedFaultExpressionTemplate() {
    
    if (normalizedFaultExpressionTemplate == null) {
      
      normalizeFaultExpressionTemplate();
    }
    
    return normalizedFaultExpressionTemplate;
  }
}
//@formatter:on