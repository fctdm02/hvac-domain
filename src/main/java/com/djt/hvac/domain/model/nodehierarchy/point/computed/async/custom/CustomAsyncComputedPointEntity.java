//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.point.computed.async.custom;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.dsl.computedpoint.v2.Result;
import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.RemediationStrategy;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.dictionary.TagEntity;
import com.djt.hvac.domain.model.dictionary.UnitEntity;
import com.djt.hvac.domain.model.dictionary.enums.DataType;
import com.djt.hvac.domain.model.dictionary.enums.NodeSubType;
import com.djt.hvac.domain.model.dictionary.template.nodetag.PointTemplateEntity;
import com.djt.hvac.domain.model.function.AdFunctionInstanceEligiblePoint;
import com.djt.hvac.domain.model.nodehierarchy.AbstractNodeEntity;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;
import com.djt.hvac.domain.model.nodehierarchy.mapper.PortfolioDtoMapper;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.AsyncComputedPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.custom.enums.ComputationInterval;
import com.djt.hvac.domain.model.nodehierarchy.validation.HardDeleteNodeStrategyImpl;

public class CustomAsyncComputedPointEntity extends AsyncComputedPointEntity implements AdFunctionInstanceEligiblePoint {
  
  private static final long serialVersionUID = 1L;
    
  private ComputationInterval computationInterval;
  private Set<TemporalAsyncComputedPointConfigEntity> childTemporalConfigs = new TreeSet<>();
  
  public CustomAsyncComputedPointEntity() {}
  
  public CustomAsyncComputedPointEntity(
      AbstractNodeEntity parentNode,
      String name,
      String displayName,
      Set<TagEntity> nodeTags,
      UnitEntity unit,
      PointTemplateEntity pointTemplate,
      String metricId,
      ComputationInterval computationInterval) {
    this(
        null,
        parentNode,
        name,
        displayName,
        AbstractEntity.toFormattedZonedTime(AbstractEntity.getTimeKeeper().getCurrentTimeInMillis(), "UTC"),
        AbstractEntity.toFormattedZonedTime(AbstractEntity.getTimeKeeper().getCurrentTimeInMillis(), "UTC"),
        nodeTags,
        DataType.NUMERIC,
        unit,
        pointTemplate,
        null,
        null,      
        metricId,
        Boolean.TRUE,
        Boolean.FALSE,
        computationInterval);
  }
  
  public CustomAsyncComputedPointEntity(
      Integer persistentIdentity,
      AbstractNodeEntity parentNode,
      String name,
      String displayName,
      String createdAt,
      String updatedAt,
      Set<TagEntity> nodeTags,
      DataType dataType,
      UnitEntity unit,
      PointTemplateEntity pointTemplate,
      String lastValue,
      Long lastValueTimestamp,      
      String metricId,
      Boolean configurable,
      Boolean timezoneBasedRollups,
      ComputationInterval computationInterval) {
    super(
        persistentIdentity,
        parentNode,
        name,
        displayName,
        createdAt,
        updatedAt,
        nodeTags,
        dataType,
        unit,
        null,
        pointTemplate,
        lastValue,
        lastValueTimestamp,      
        metricId,
        configurable,
        timezoneBasedRollups,
        null);
    this.computationInterval = computationInterval;
  }
  
  public void setComputationInterval(ComputationInterval computationInterval) {
    
    if (computationInterval != null && this.computationInterval.getId() != computationInterval.getId()) {
    
      this.computationInterval = computationInterval;
      this.setIsModified("computationInterval");
    }
  }
  
  public ComputationInterval getComputationInterval() {
    return computationInterval;
  }

  public Set<TemporalAsyncComputedPointConfigEntity> getChildTemporalConfigs() {
    return childTemporalConfigs;
  }

  public List<TemporalAsyncComputedPointConfigEntity> getChildTemporalConfigsAsList() {
    List<TemporalAsyncComputedPointConfigEntity> list = new ArrayList<>();
    list.addAll(childTemporalConfigs);
    return list;
  }
  
  public boolean addChildTemporalConfig(TemporalAsyncComputedPointConfigEntity childTemporalConfig) throws EntityAlreadyExistsException {
    return addChild(childTemporalConfigs, childTemporalConfig, this);
  }
  
  public boolean removeAllChildTemporalConfigs() {
    
    boolean changed = false;
    if (!childTemporalConfigs.isEmpty()) {
      childTemporalConfigs.clear();
      setIsModified("childTemporalConfigs:removed");
    }
    return changed;
  }

  public TemporalAsyncComputedPointConfigEntity getChildTemporalConfig(Integer persistentIdentity) throws EntityDoesNotExistException {
    for (TemporalAsyncComputedPointConfigEntity childTemporalConfig: childTemporalConfigs) {
      if (childTemporalConfig.getPersistentIdentity().equals(persistentIdentity)) {
        return childTemporalConfig;
      }
    }
    throw new EntityDoesNotExistException("Custom point: ["
        + getNodePath()
        + "] does not have a child temporal config with id: ["
        + persistentIdentity
        + "]");
  }

  public TemporalAsyncComputedPointConfigEntity getChildTemporalConfigByEffectiveDate(LocalDate effectiveDate) throws EntityDoesNotExistException {
    for (TemporalAsyncComputedPointConfigEntity childTemporalConfig: childTemporalConfigs) {
      if (childTemporalConfig.getEffectiveDate().equals(effectiveDate)) {
        return childTemporalConfig;
      }
    }
    throw new EntityDoesNotExistException("Custom point: ["
        + getNodePath()
        + "] does not have a child temporal config with effective date: ["
        + effectiveDate
        + "]");
  }
  
  private List<String> validateChildTemporalConfigs() {
    
    List<String> errors = new ArrayList<>();
    for (TemporalAsyncComputedPointConfigEntity childTemporalConfig: childTemporalConfigs) {
      
      errors.addAll(childTemporalConfig.validateFormula());
    }
    return errors;
  }
  
  @Override
  public void validate(
      Set<IssueType> issueTypes,
      List<ValidationMessage> validationMessages,
      boolean remediate) {

    super.validate(issueTypes, validationMessages, remediate);
    
    Map<String, Object> entities = new LinkedHashMap<>();
    entities.put("point", this);

    List<String> errors = validateChildTemporalConfigs();
    if (!errors.isEmpty()) {

      RemediationStrategy remediationStrategy = HardDeleteNodeStrategyImpl.get();

      if (issueTypes.contains(IssueType.CUSTOM_POINT_HAS_INVALID_FORMULA)) {
        validationMessages.add(ValidationMessage.builder()
            .withIssueType(IssueType.CUSTOM_POINT_HAS_INVALID_FORMULA)
            .withDetails("Custom Async Computed Point: ["
                + getNodePath()
                + "] has invalid temporal config formula(s): ["
                + errors)
            .withEntityType(getClass().getSimpleName())
            .withNaturalIdentity(getNaturalIdentity())
            .withRemediationDescription("Hard delete custom async computed point")
            .withRemediationStrategy(remediationStrategy)
            .build());

        if (remediate) {
          remediationStrategy.remediate(entities);
        }
      }
    }    
  }
    
  @Override
  public AbstractNodeEntity duplicateNode(PortfolioEntity portfolio, AbstractNodeEntity parentNode, int duplicationIndex) {
    throw new IllegalStateException("duplicateNode() cannot be called on a leaf point node: [" + this + "].");
  }
  
  @Override
  public NodeSubType getNodeSubType() {
    return NodeSubType.CUSTOM_ASYNC_COMPUTED_POINT;
  }
  
  @Override
  public void mapToDtos(Map<String, Object> dtos) {

    PortfolioDtoMapper.mapCustomAsyncComputedPointNodeDto(this, dtos);
  }
  
  public List<TemporalAsyncComputedPointConfigEntity> getChildTemporalConfigForTimeInterval(
      Timestamp startTimestamp,
      Timestamp endTimestamp) {
    
    List<TemporalAsyncComputedPointConfigEntity> list = new ArrayList<>();

    LocalDateTime startLocalDateTime = Instant
        .ofEpochMilli(startTimestamp.getTime())
        .atZone(TimeZone
            .getTimeZone(ETC_UTC_TIMEZONE)
            .toZoneId())
        .toLocalDateTime();

    LocalDateTime endLocalDateTime = Instant
        .ofEpochMilli(endTimestamp.getTime())
        .atZone(TimeZone
            .getTimeZone(ETC_UTC_TIMEZONE)
            .toZoneId())
        .toLocalDateTime();
    
    for (TemporalAsyncComputedPointConfigEntity childTemporalConfig: childTemporalConfigs) {
      
      LocalDate effectiveDate = childTemporalConfig.getEffectiveDate();
      if ((startLocalDateTime.isAfter(effectiveDate.atStartOfDay()) || startLocalDateTime.isEqual(effectiveDate.atStartOfDay()))
          && (endLocalDateTime.isBefore(effectiveDate.atTime(LocalTime.MAX)) || endLocalDateTime.isEqual(effectiveDate.atTime(LocalTime.MAX)))) {
        
        list.add(childTemporalConfig);
      }
    }
    
    return list;
  }
  
  public TemporalAsyncComputedPointConfigEntity getChildTemporalConfigForTimeMillis(long timeMillis) {
   
    LocalDate localDateToEvaluate = Instant
        .ofEpochMilli(timeMillis)
        .atZone(TimeZone
            .getTimeZone(ETC_UTC_TIMEZONE)
            .toZoneId())
        .toLocalDateTime()
        .toLocalDate();
    
    for (TemporalAsyncComputedPointConfigEntity childTemporalConfig: childTemporalConfigs) {
      
      LocalDate effectiveDate = childTemporalConfig.getEffectiveDate();
      if (localDateToEvaluate.isAfter(effectiveDate) || localDateToEvaluate.isEqual(effectiveDate)) {
        
        return childTemporalConfig;
      }
    }
    
    throw new IllegalArgumentException("timestamp: ["
        + localDateToEvaluate 
        + "] is out of range for custom point: ["
        + getNodePath()
        + "], configs: "
        + childTemporalConfigs);
  }
  
  public Result evaluateFormula(
      long timeMillis,
      Map<String, Double> variableValues,
      Map<String, String> functionState) {
    
    TemporalAsyncComputedPointConfigEntity childTemporalConfig = getChildTemporalConfigForTimeMillis(timeMillis);
    
    return childTemporalConfig.evaluateFormula(
        timeMillis, 
        variableValues, 
        functionState);
  }
}
//@formatter:on