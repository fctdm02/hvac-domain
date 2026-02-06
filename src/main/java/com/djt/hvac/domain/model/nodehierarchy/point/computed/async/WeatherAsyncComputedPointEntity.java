//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.point.computed.async;

import java.util.Set;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.dictionary.TagEntity;
import com.djt.hvac.domain.model.dictionary.UnitEntity;
import com.djt.hvac.domain.model.dictionary.container.DictionaryContext;
import com.djt.hvac.domain.model.dictionary.enums.DataType;
import com.djt.hvac.domain.model.dictionary.enums.NodeSubType;
import com.djt.hvac.domain.model.dictionary.template.nodetag.PointTemplateEntity;
import com.djt.hvac.domain.model.dictionary.weather.GlobalComputedPointEntity;
import com.djt.hvac.domain.model.function.AdFunctionInstanceEligiblePoint;
import com.djt.hvac.domain.model.nodehierarchy.AbstractNodeEntity;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;

public class WeatherAsyncComputedPointEntity extends AsyncComputedPointEntity implements AdFunctionInstanceEligiblePoint {
  private static final long serialVersionUID = 1L;
  private final GlobalComputedPointEntity globalComputedPoint;

  public WeatherAsyncComputedPointEntity(
      AbstractNodeEntity parentNode,
      String name,
      String displayName,
      Set<TagEntity> nodeTags,
      UnitEntity unit,
      PointTemplateEntity pointTemplate,
      String metricId,
      Integer globalComputedPointId) {
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
        null,
        pointTemplate,
        null,
        null,      
        metricId,
        Boolean.TRUE,
        Boolean.FALSE,
        globalComputedPointId);
  }
  
  public WeatherAsyncComputedPointEntity(
      Integer persistentIdentity,
      AbstractNodeEntity parentNode,
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
      Integer globalComputedPointId) {
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
        configurable,
        timezoneBasedRollups,
        globalComputedPointId);
    
    if (globalComputedPointId != null) {
      globalComputedPoint = DictionaryContext
          .getWeatherStationsContainer()
          .getGlobalComputedPointById(globalComputedPointId);
    } else {
      globalComputedPoint = null;
    }
  }
  
  public GlobalComputedPointEntity getGlobalComputedPoint() {
    return globalComputedPoint;
  }
  
  @Override
  public AbstractNodeEntity duplicateNode(PortfolioEntity portfolio, AbstractNodeEntity parentNode, int duplicationIndex) {
    throw new IllegalStateException("duplicateNode() cannot be called on a leaf point node: [" + this + "].");
  }
  
  @Override
  public NodeSubType getNodeSubType() {
    return NodeSubType.WEATHER_ASYNC_COMPUTED_POINT;
  }
}
//@formatter:on