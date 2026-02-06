//@formatter:off
package com.djt.hvac.domain.model.report;

import static java.util.Objects.requireNonNull;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.djt.hvac.domain.model.common.AbstractAssociativeEntity;
import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.dictionary.template.report.AbstractReportTemplatePointSpecEntity;
import com.djt.hvac.domain.model.dictionary.template.report.ReportTemplateRulePointSpecEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.AbstractPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.AsyncComputedPointEntity;

public class ReportInstancePointEntity extends AbstractAssociativeEntity {
  private static final long serialVersionUID = 1L;
  private final ReportInstanceEquipmentEntity parentReportInstanceEquipment;
  private final AbstractReportTemplatePointSpecEntity reportTemplatePointSpec;
  private final AbstractPointEntity point;
  
  public ReportInstancePointEntity(
      ReportInstanceEquipmentEntity parentReportInstanceEquipment,
      AbstractReportTemplatePointSpecEntity reportTemplatePointSpec,
      AbstractPointEntity point) {
    requireNonNull(parentReportInstanceEquipment, "parentReportInstanceEquipment cannot be null");
    requireNonNull(reportTemplatePointSpec, "reportTemplatePointSpec cannot be null");
    requireNonNull(point, "point cannot be null");
    this.parentReportInstanceEquipment = parentReportInstanceEquipment;
    this.reportTemplatePointSpec = reportTemplatePointSpec;
    
    if (reportTemplatePointSpec instanceof ReportTemplateRulePointSpecEntity) {
      
      if (!(point instanceof AsyncComputedPointEntity)) {
        throw new RuntimeException("Expected point to be an async computed point, but instead was: ["
            + point.getClassAndNaturalIdentity() 
            + "].");
      }
    }
    this.point = point;
  }
  
  public ReportInstanceEquipmentEntity getParentReportInstanceEquipment() {
    return parentReportInstanceEquipment;
  }

  public AbstractReportTemplatePointSpecEntity getReportTemplatePointSpec() {
    return reportTemplatePointSpec;
  }

  public AbstractPointEntity getPoint() {
    return point;
  }
  
  public String getPointMetricId() {
    return point.getMetricId();
  }

  public Map<String, Integer> getParentIdentities() {
    
    Map<String, Integer> parentIdentities = new LinkedHashMap<>();
    
    Iterator<Map.Entry<String, Integer>> iterator = parentReportInstanceEquipment.getParentIdentities().entrySet().iterator();
    while (iterator.hasNext()) {
      
      Map.Entry<String, Integer> entry = iterator.next();
      parentIdentities.put(entry.getKey(), entry.getValue());
    }
    
    parentIdentities.put("reportTemplatePointSpecId", reportTemplatePointSpec.getPersistentIdentity());
    parentIdentities.put("pointId", point.getPersistentIdentity());
    return parentIdentities;
  }
  
  @Override
  public String getNaturalIdentity() {

    if (_naturalIdentity == null) {

      _naturalIdentity = new StringBuilder()
          .append(parentReportInstanceEquipment.getNaturalIdentity())
          .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
          .append(reportTemplatePointSpec.getNaturalIdentity())
          .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
          .append(point.getNaturalIdentity())
          .toString();
    }
    return _naturalIdentity;
  }
}
//@formatter:on