//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.building;

import static java.util.Objects.requireNonNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.djt.hvac.domain.model.common.AbstractAssociativeEntity;
import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.BuildingUtilityType;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.UtilityComputationInterval;

public class BuildingTemporalUtilityEntity extends AbstractAssociativeEntity {
  private static final long serialVersionUID = 1L;
  private final BuildingTemporalConfigEntity parentTemporalConfig;
  private final BuildingUtilityType buildingUtilityType;
  private final UtilityComputationInterval computationInterval;
  private final String formula;
  private final Double rate;
  private final String baselineDescription;
  private final String userNotes;

  public BuildingTemporalUtilityEntity(
      BuildingTemporalConfigEntity parentTemporalConfig,
      BuildingUtilityType buildingUtilityType,
      UtilityComputationInterval computationInterval,
      String formula,
      Double rate,
      String baselineDescription,
      String userNotes) {
    requireNonNull(parentTemporalConfig, "parentTemporalConfig cannot be null");
    requireNonNull(buildingUtilityType, "buildingUtilityType cannot be null");
    requireNonNull(formula, "formula cannot be null");
    this.parentTemporalConfig = parentTemporalConfig;
    this.buildingUtilityType = buildingUtilityType;
    if (computationInterval == null) {
      this.computationInterval = UtilityComputationInterval.DAILY;
    } else {
      this.computationInterval = computationInterval;
    }
    this.formula = formula;
    this.rate = rate;
    if (baselineDescription != null) {
      this.baselineDescription = baselineDescription.replace(",", "");
    } else {
      this.baselineDescription = null;
    }
    this.userNotes = userNotes;
  }

  public BuildingTemporalConfigEntity getParentTemporalConfig() {
    return parentTemporalConfig;
  }

  public BuildingUtilityType getBuildingUtilityType() {
    return buildingUtilityType;
  }
  
  public UtilityComputationInterval getComputationInterval() {
    return computationInterval;
  }

  public String getFormula() {
    return formula;
  }

  public Double getRate() {
    return rate;
  }

  public String getBaselineDescription() {
    return baselineDescription;
  }

  public String getUserNotes() {
    return userNotes;
  }

  @Override
  public Map<String, Integer> getParentIdentities() {
    
    Map<String, Integer> parentIdentities = new LinkedHashMap<>();
    parentIdentities.put("parentTemporalConfig", parentTemporalConfig.getPersistentIdentity());
    parentIdentities.put("buildingUtilityType", Integer.valueOf(buildingUtilityType.getId()));
    return parentIdentities;
  }
  
  @Override
  public String getNaturalIdentity() {
    
    return new StringBuilder()
        .append(parentTemporalConfig.getNaturalIdentity())
        .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
        .append(buildingUtilityType.getName())
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