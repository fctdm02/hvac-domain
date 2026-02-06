//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.point.computed.async;

import java.util.Set;

import com.djt.hvac.domain.model.dictionary.TagEntity;
import com.djt.hvac.domain.model.dictionary.UnitEntity;
import com.djt.hvac.domain.model.dictionary.enums.DataType;
import com.djt.hvac.domain.model.dictionary.enums.NodeSubType;
import com.djt.hvac.domain.model.dictionary.template.nodetag.PointTemplateEntity;
import com.djt.hvac.domain.model.nodehierarchy.AbstractNodeEntity;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;

public class AdFunctionAsyncComputedPointEntity extends AsyncComputedPointEntity {
  private static final long serialVersionUID = 1L;
  public AdFunctionAsyncComputedPointEntity(
      AbstractNodeEntity parentNode,
      String name,
      String displayName,
      Set<TagEntity> nodeTags,
      DataType dataType,
      UnitEntity unit,
      String range,
      PointTemplateEntity pointTemplate,
      String metricId) {
    this(
        null,
        parentNode,
        name,
        displayName,
        "",
        "",
        nodeTags,
        dataType,
        unit,
        range,
        pointTemplate,
        null,
        null,      
        metricId,
        Boolean.FALSE,
        Boolean.FALSE,
        null);
  }
  
  public AdFunctionAsyncComputedPointEntity(
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
  }  

  @Override
  public AbstractNodeEntity duplicateNode(PortfolioEntity portfolio, AbstractNodeEntity parentNode, int duplicationIndex) {
    throw new IllegalStateException("duplicateNode() cannot be called on a leaf point node: [" + this + "].");
  }
  
  @Override
  public NodeSubType getNodeSubType() {
    return NodeSubType.AD_FUNCTION_ASYNC_COMPUTED_POINT;
  }
}
//@formatter:on