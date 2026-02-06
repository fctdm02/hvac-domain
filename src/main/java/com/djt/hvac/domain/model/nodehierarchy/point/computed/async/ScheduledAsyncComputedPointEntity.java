//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.point.computed.async;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.dictionary.ScheduledEventTypeEntity;
import com.djt.hvac.domain.model.dictionary.TagEntity;
import com.djt.hvac.domain.model.dictionary.UnitEntity;
import com.djt.hvac.domain.model.dictionary.enums.DataType;
import com.djt.hvac.domain.model.dictionary.enums.NodeSubType;
import com.djt.hvac.domain.model.dictionary.template.nodetag.PointTemplateEntity;
import com.djt.hvac.domain.model.function.AdFunctionInstanceEligiblePoint;
import com.djt.hvac.domain.model.nodehierarchy.AbstractNodeEntity;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BuildingEntity;
import com.djt.hvac.domain.model.nodehierarchy.mapper.PortfolioDtoMapper;

public class ScheduledAsyncComputedPointEntity extends AsyncComputedPointEntity implements AdFunctionInstanceEligiblePoint {
  private static final long serialVersionUID = 1L;
  private final ScheduledEventTypeEntity scheduledEventType;

  public ScheduledAsyncComputedPointEntity(
      BuildingEntity parentNode,
      String name,
      String displayName,
      Set<TagEntity> nodeTags,
      UnitEntity unit,
      String range,
      PointTemplateEntity pointTemplate,
      String metricId,
      ScheduledEventTypeEntity scheduledEventType) {
    this(
        null,
        parentNode,
        name,
        displayName,
        AbstractEntity.toFormattedZonedTime(AbstractEntity.getTimeKeeper().getCurrentTimeInMillis(), "UTC"),
        AbstractEntity.toFormattedZonedTime(AbstractEntity.getTimeKeeper().getCurrentTimeInMillis(), "UTC"),
        nodeTags,
        DataType.BOOLEAN,
        unit,
        range,
        pointTemplate,
        null,
        null,
        metricId,
        Boolean.FALSE,
        Boolean.FALSE,
        null,
        scheduledEventType);
  }
  
  public ScheduledAsyncComputedPointEntity(
      Integer persistentIdentity,
      BuildingEntity parentNode,
      String name,
      String displayName,
      String createdAt,
      String updatedAt,
      Set<TagEntity> nodeTags,
      DataType dataType,
      UnitEntity unit,
      String range,
      PointTemplateEntity pointTemplate,
      String lastValue,
      Long lastValueTimestamp,      
      String metricId,
      Boolean configurable,
      Boolean timezoneBasedRollups,
      Integer globalComputedPointId,
      ScheduledEventTypeEntity scheduledEventType) {
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
        range,
        pointTemplate,
        lastValue,
        lastValueTimestamp,
        metricId,
        Boolean.FALSE,
        Boolean.FALSE,
        null);
    requireNonNull(scheduledEventType, "scheduledEventType cannot be null");
    this.scheduledEventType = scheduledEventType;
  }

  @Override
  public AbstractNodeEntity duplicateNode(PortfolioEntity portfolio, AbstractNodeEntity parentNode, int duplicationIndex) {
    throw new IllegalStateException("duplicateNode() cannot be called on a leaf point node: [" + this + "].");
  }
  
  @Override
  public NodeSubType getNodeSubType() {
    return NodeSubType.SCHEDULED_ASYNC_COMPUTED_POINT;
  }

  public ScheduledEventTypeEntity getScheduledEventType() {
    return scheduledEventType;
  }

  @Override
  public void validate(
      Set<IssueType> issueTypes,
      List<ValidationMessage> validationMessages,
      boolean remediate) {

    // RP-8595: Report Evaluation: Do not remediate scheduled points until refactoring
    // is done to assign the appropriate point template when they are created.
    // super.validate(issueTypes, validationMessages, remediate);
  }
  
  @Override
  public void mapToDtos(Map<String, Object> dtos) {

    PortfolioDtoMapper.mapScheduledAsyncComputedPointNodeDto(this, dtos);
  }
}
//@formatter:on