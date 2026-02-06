//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.point.computed;

import java.util.Set;

import com.djt.hvac.domain.model.dictionary.TagEntity;
import com.djt.hvac.domain.model.dictionary.UnitEntity;
import com.djt.hvac.domain.model.dictionary.enums.DataType;
import com.djt.hvac.domain.model.dictionary.template.nodetag.PointTemplateEntity;
import com.djt.hvac.domain.model.nodehierarchy.AbstractNodeEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.AbstractPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.utils.OpenTsdbStringUtils;

public abstract class AbstractComputedPointEntity extends AbstractPointEntity {
  private static final long serialVersionUID = 1L;
  
  private String metricId;

  public AbstractComputedPointEntity() {}
  
  public AbstractComputedPointEntity(
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
      String metricId) {
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
        lastValueTimestamp);
    this.metricId = metricId;
  }
  
  public String getMetricId() {
    return metricId;
  }
  
  public void setMetricId(String metricId) {
    
    if (metricId == null || metricId.trim().isEmpty()) {
      throw new IllegalArgumentException("Cannot set a null/empty metric id for async computed point: ["
          + getNodePath()
          + "]");
    }
    metricId = OpenTsdbStringUtils.toValidMetricId(metricId);
    
    if (!this.metricId.equals(metricId)) {

      this.metricId = metricId;
      setIsModified("metricId");
    }    
  }
}
//@formatter:on