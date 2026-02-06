//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.point.computed.async;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.dictionary.TagEntity;
import com.djt.hvac.domain.model.dictionary.UnitEntity;
import com.djt.hvac.domain.model.dictionary.enums.DataType;
import com.djt.hvac.domain.model.dictionary.template.nodetag.PointTemplateEntity;
import com.djt.hvac.domain.model.nodehierarchy.AbstractNodeEntity;
import com.djt.hvac.domain.model.nodehierarchy.mapper.PortfolioDtoMapper;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.AbstractComputedPointEntity;

public abstract class AsyncComputedPointEntity extends AbstractComputedPointEntity {
  private static final long serialVersionUID = 1L;
  // Used for AD function instance output points
  public static final DataType DEFAULT_DATA_TYPE = DataType.BOOLEAN;
  public static final UnitEntity DEFAULT_UNIT_ENTITY = UnitEntity.EMPTY_UNIT;
  public static final String DEFAULT_RANGE = "{\"trueText\":\"On\",\"falseText\":\"Off\"}";
  public static final String DEFAULT_DESCRIPTION = "Anomaly Detected";
  
  private Boolean configurable;
  private Boolean timezoneBasedRollups;
  private Integer globalComputedPointId;
  
  public AsyncComputedPointEntity() {}
  
  public AsyncComputedPointEntity(
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
        metricId);
    if (configurable != null) {
      this.configurable = configurable;  
    } else {
      this.configurable = Boolean.FALSE;
    }
    if (timezoneBasedRollups != null) {
      this.timezoneBasedRollups = timezoneBasedRollups;  
    } else {
      this.timezoneBasedRollups = Boolean.FALSE;
    }
    this.globalComputedPointId = globalComputedPointId;
  }
  
  public Boolean getConfigurable() {
    return configurable;
  }
  
  public Boolean getTimezoneBasedRollups() {
    return timezoneBasedRollups;
  }

  public Integer getGlobalComputedPointId() {
    return globalComputedPointId;
  }
  
  @Override
  public void validate(
      Set<IssueType> issueTypes,
      List<ValidationMessage> validationMessages,
      boolean remediate) {

    super.validate(issueTypes, validationMessages, remediate);
  }
  
  public void mapToDtos(Map<String, Object> dtos) {
    
    PortfolioDtoMapper.mapAsyncComputedPointNodeDto(this, dtos);
  }
}
//@formatter:on