package com.djt.hvac.domain.model.dictionary.weather;

import static java.util.Objects.requireNonNull;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

import com.djt.hvac.domain.model.common.AbstractPersistentEntity;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.dictionary.UnitEntity;
import com.djt.hvac.domain.model.dictionary.enums.DataType;

public class GlobalComputedPointEntity extends AbstractPersistentEntity {
  private static final long serialVersionUID = 1L;
  
  private final String name;
  private final String displayName;
  private final String description;
  private final UnitEntity unit;
  private final String metricId;
  private final Timestamp lastProcessedAt;
  private final Timestamp earliestProcessedAt;
  
  public GlobalComputedPointEntity(
      String name,
      String displayName,
      String description,
      UnitEntity unit,
      String metricId) {
    this(
        null,
        name,
        displayName,
        description,
        unit,
        metricId,
        null,
        null);
  }
  
  public GlobalComputedPointEntity(
      Integer persistentIdentity,
      String name,
      String displayName,
      String description,
      UnitEntity unit,
      String metricId,
      Timestamp lastProcessedAt,
      Timestamp earliestProcessedAt) {
    super(persistentIdentity);
    requireNonNull(name, "name cannot be null");
    requireNonNull(displayName, "displayName cannot be null");
    requireNonNull(description, "description cannot be null");
    requireNonNull(unit, "unit cannot be null");
    requireNonNull(metricId, "metricId cannot be null");
    this.name = name;
    this.displayName = displayName;
    this.description = description;
    this.unit = unit;
    this.metricId = metricId;
    this.lastProcessedAt = lastProcessedAt;
    this.earliestProcessedAt = earliestProcessedAt;
  }
    
  public String getName() {
    return name;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getDescription() {
    return description;
  }

  public UnitEntity getUnit() {
    return unit;
  }

  public String getMetricId() {
    return metricId;
  }

  public Timestamp getLastProcessedAt() {
    return lastProcessedAt;
  }

  public Timestamp getEarliestProcessedAt() {
    return earliestProcessedAt;
  }
  
  public Integer getPointTemplateId() {
    
    if (name.endsWith("OaTemp") || name.endsWith("OaTemp SI")) {
      return Integer.valueOf(27);
    } else if (name.endsWith("OaHumidity")) {
      return Integer.valueOf(10);
    } else {
      throw new IllegalStateException("Only OaTemp, OaTemp SI and OaHumidity are allowed, but encountered unsupported global computed point: " + this.name);
    }
  }
  
  @Override
  public String getNaturalIdentity() {
    return getMetricId();
  }  
  
  public DataType getDataType() {
    return DataType.NUMERIC;
  }
  
  public String getRange() {
    return null;
  }
  
  @Override
  public void validate(
      Set<IssueType> issueTypes,
      List<ValidationMessage> validationMessages, 
      boolean remediate) {
  }
}
