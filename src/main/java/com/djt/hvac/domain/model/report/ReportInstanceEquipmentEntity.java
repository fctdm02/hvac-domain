//@formatter:off
package com.djt.hvac.domain.model.report;

import static java.util.Objects.requireNonNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.djt.hvac.domain.model.common.AbstractAssociativeEntity;
import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.dictionary.template.report.ReportTemplateEquipmentSpecEntity;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.EnergyExchangeEntity;

/**
 * 
 * Represents an equipment that is in GREEN status (all required points found)
 * 
 * @author tmyers
 *
 */
public class ReportInstanceEquipmentEntity extends AbstractAssociativeEntity {
  private static final long serialVersionUID = 1L;
  private static final Logger LOGGER = LoggerFactory.getLogger(ReportInstanceEquipmentEntity.class);

  public static final String STATUS_GREEN = "GREEN";
  
  private final ReportInstanceEntity parentReportInstance;
  private final ReportTemplateEquipmentSpecEntity reportTemplateEquipmentSpec;
  private final EnergyExchangeEntity equipment;
  
  private final Set<ReportInstancePointEntity> reportInstancePoints = new TreeSet<>();
  
  public ReportInstanceEquipmentEntity(
      ReportInstanceEntity parentReportInstance,
      ReportTemplateEquipmentSpecEntity reportTemplateEquipmentSpec,
      EnergyExchangeEntity equipment) {
    requireNonNull(parentReportInstance, "parentReportInstance cannot be null");
    requireNonNull(reportTemplateEquipmentSpec, "reportTemplateEquipmentSpec cannot be null");
    requireNonNull(equipment, "equipment cannot be null");
    this.parentReportInstance = parentReportInstance;
    this.reportTemplateEquipmentSpec = reportTemplateEquipmentSpec;
    this.equipment = equipment;
  }
  
  @Override
  public void setNotModified() {
    
    super.setNotModified();
    for (ReportInstancePointEntity entity: reportInstancePoints) {
      
      entity.setNotModified();
    }
  }
  
  public ReportInstanceEntity getParentReportInstance() {
    return parentReportInstance;
  }
  
  public ReportTemplateEquipmentSpecEntity getReportTemplateEquipmentSpec() {
    return reportTemplateEquipmentSpec;
  }

  public EnergyExchangeEntity getEquipment() {
    return equipment;
  }

  public boolean addReportInstancePoint(ReportInstancePointEntity reportInstancePoint) throws EntityAlreadyExistsException {
    
    if (reportInstancePoints.contains(reportInstancePoint)) {
      
      LOGGER.warn("{} already has: [{}] with parentIdentities: {} in collection: {}",
          getClassAndNaturalIdentity(),
          reportInstancePoint.getClassAndNaturalIdentity(),
          reportInstancePoint.getParentIdentities(),
          reportInstancePoints);
    }
    return reportInstancePoints.add(reportInstancePoint);
    
    // TODO: TDM: Investigate duplicates
    //return addChild(reportInstancePoints, reportInstancePoint, this);
  }
  
  public Set<ReportInstancePointEntity> getReportInstancePoints() {
    return reportInstancePoints;
  }
  
  public Map<String, Integer> getParentIdentities() {
    
    Map<String, Integer> parentIdentities = new LinkedHashMap<>();
    parentIdentities.put("reportInstanceId", parentReportInstance.getPersistentIdentity());
    parentIdentities.put("reportTemplateEquipmentSpecId", reportTemplateEquipmentSpec.getPersistentIdentity());
    parentIdentities.put("equipmentId", equipment.getPersistentIdentity());
    return parentIdentities;
  }  
  
  @Override
  public String getNaturalIdentity() {

    if (_naturalIdentity == null) {

      _naturalIdentity = new StringBuilder()
          .append(parentReportInstance.getNaturalIdentity())
          .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
          .append(reportTemplateEquipmentSpec.getNaturalIdentity())
          .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
          .append(equipment.getNaturalIdentity())
          .toString();
    }
    return _naturalIdentity;
  }
  
  /*
   * EQUIPMENT STATUS: (derived/evaluated on-demand)
   * ===============================================
   * GREEN: All required point specs have been matched
   * RED: At least 1 required point spec has not been matched
   */
  public String getStatus() {
    return STATUS_GREEN;
  }
}
//@formatter:on