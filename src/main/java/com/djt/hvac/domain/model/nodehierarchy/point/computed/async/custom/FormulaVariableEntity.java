//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.point.computed.async.custom;

import static java.util.Objects.requireNonNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.djt.hvac.domain.model.common.AbstractAssociativeEntity;
import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.custom.enums.FillPolicy;

public class FormulaVariableEntity extends AbstractAssociativeEntity {
  private static final long serialVersionUID = 1L;
  private TemporalAsyncComputedPointConfigEntity parentTemporalConfig;
  private CustomPointFormulaVariableEligiblePoint parentPoint;
  private String name;
  private FillPolicy fillPolicy;

  public FormulaVariableEntity() {}
  
  public FormulaVariableEntity(
      TemporalAsyncComputedPointConfigEntity parentTemporalConfig,
      CustomPointFormulaVariableEligiblePoint parentPoint,
      String name,
      FillPolicy fillPolicy) {
    requireNonNull(parentTemporalConfig, "parentTemporalConfig cannot be null");
    requireNonNull(name, "name cannot be null");
    requireNonNull(fillPolicy, "fillPolicy cannot be null");
    this.parentTemporalConfig = parentTemporalConfig;
    this.parentPoint = parentPoint;
    this.name = name;
    this.fillPolicy = fillPolicy;
  }

  public TemporalAsyncComputedPointConfigEntity getParentTemporalConfig() {
    return parentTemporalConfig;
  }

  public CustomPointFormulaVariableEligiblePoint getParentPoint() {
    return parentPoint;
  }

  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    
    if (name != null && !name.equals(this.name)) {
      this.name = name;
      this.setIsModified("name");
    }
  }

  public FillPolicy getFillPolicy() {
    return fillPolicy;
  }
  
  public void setFillPolicy(FillPolicy fillPolicy) {
    
    if (fillPolicy != null && !fillPolicy.equals(this.fillPolicy)) {
      this.fillPolicy = fillPolicy;
      this.setIsModified("fillPolicy");
    }
  }
  
  @Override
  public Map<String, Integer> getParentIdentities() {
    
    Map<String, Integer> parentIdentities = new LinkedHashMap<>();
    parentIdentities.put("parentTemporalConfig", parentTemporalConfig.getPersistentIdentity());
    if (parentPoint != null) {
      parentIdentities.put("parentPointId", parentPoint.getPersistentIdentity());  
    } else {
      parentIdentities.put("parentPointId", Integer.valueOf(name.hashCode()));
    }
    return parentIdentities;
  }
  
  @Override
  public String getNaturalIdentity() {
    
    return new StringBuilder()
        .append(parentTemporalConfig.getNaturalIdentity())
        .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
        .append(name)
        .toString();
  }  
  
  @Override
  public void validate(
      Set<IssueType> issueTypes,
      List<ValidationMessage> validationMessages, 
      boolean remediate) {
  }
}
//@formatter:on