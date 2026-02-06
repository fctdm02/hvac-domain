//@formatter:off
package com.djt.hvac.domain.model.report;

import static java.util.Objects.requireNonNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.djt.hvac.domain.model.common.AbstractAssociativeEntity;
import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.EnergyExchangeEntity;

/**
 * 
 * Represents an equipment that is in RED status, at least one issue, 
 * which are contained in the error messages collection. 
 * 
 * @author tmyers
 *
 */
public class ReportInstanceEquipmentErrorMessagesEntity extends AbstractAssociativeEntity {
  private static final long serialVersionUID = 1L;
  private final ReportInstanceEntity parentReportInstance;
  private final EnergyExchangeEntity equipment;
  private final List<Integer> errorMessages;

  public ReportInstanceEquipmentErrorMessagesEntity(
      ReportInstanceEntity parentReportInstance,
      EnergyExchangeEntity equipment,
      List<Integer> errorMessages) {
    requireNonNull(parentReportInstance, "parentReportInstance cannot be null");
    requireNonNull(equipment, "equipment cannot be null");
    requireNonNull(errorMessages, "errorMessages cannot be null");
    this.parentReportInstance = parentReportInstance;
    this.equipment = equipment;
    this.errorMessages = errorMessages;
  }
  
  public ReportInstanceEntity getParentReportInstance() {
    return parentReportInstance;
  }

  public EnergyExchangeEntity getEquipment() {
    return equipment;
  }

  public List<Integer> getErrorMessages() {
    return errorMessages;
  }
  
  public Map<String, Integer> getParentIdentities() {
    
    Map<String, Integer> parentIdentities = new LinkedHashMap<>();
    parentIdentities.put("reportInstanceId", parentReportInstance.getPersistentIdentity());
    parentIdentities.put("equipmentId", equipment.getPersistentIdentity());
    return parentIdentities;
  }  
  
  @Override
  public String getNaturalIdentity() {

    if (_naturalIdentity == null) {

      _naturalIdentity = new StringBuilder()
          .append(parentReportInstance.getNaturalIdentity())
          .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
          .append(equipment.getNaturalIdentity())
          .toString();
    }
    return _naturalIdentity;
  }
}
//@formatter:on