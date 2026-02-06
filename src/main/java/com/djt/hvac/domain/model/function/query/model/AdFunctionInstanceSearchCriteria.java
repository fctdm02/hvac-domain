//@formatter:off
package com.djt.hvac.domain.model.function.query.model;

import com.djt.hvac.domain.model.common.query.model.SearchCriteria;

/**
 * 
 * @author tmyers
 *
 */
public class AdFunctionInstanceSearchCriteria extends SearchCriteria {

  public static final String AD_FUNCTION_TYPE_RULE = "RULE";
  public static final String AD_FUNCTION_TYPE_COMPUTED_POINT = "COMPUTED_POINT";
  
  public static final String ENTITY_STATE_IGNORED = "IGNORED";
  public static final String ENTITY_STATE_DISABLED = "DISABLED";
  public static final String ENTITY_STATE_ENABLED = "ENABLED";
  
  public static final String SORT_KEY_NODE_PATH = "nodePath";
  public static final String SORT_KEY_ENERGY_EXCHANGE_TYPE_NAME = "energyExchangeTypeName";
  public static final String SORT_KEY_AD_FUNCTION_TEMPLATE_NAME = "adFunctionTemplateName";
  public static final String SORT_KEY_AD_FUNCTION_FAULT_NUMBER = "faultNumber";
  public static final String DEFAULT_SORT = SORT_KEY_NODE_PATH;
  
  private Integer customerId;
  private String adFunctionType;
  private String entityState;
  private String nodePath;
  private Integer energyExchangeId;
  private Integer energyExchangeTypeId;
  private Integer adFunctionTemplateId;
  private Integer instanceId;
  
  public AdFunctionInstanceSearchCriteria() {
    super();
  }

  public AdFunctionInstanceSearchCriteria(
      Integer customerId,
      String adFunctionType,
      String entityState,
      String nodePath,
      Integer energyExchangeId,
      Integer energyExchangeTypeId,
      Integer adFunctionTemplateId,
      Integer instanceId,
      String sort,
      String sortDirection, 
      Integer offset, 
      Integer limit) {
    super(
        sort,
        sortDirection,
        offset,
        limit);
    this.customerId = customerId;
    this.adFunctionType = adFunctionType;
    this.entityState = entityState;
    this.energyExchangeId = energyExchangeId;
    this.instanceId = instanceId;
    this.nodePath = nodePath;
    this.energyExchangeTypeId = energyExchangeTypeId;
    this.adFunctionTemplateId = adFunctionTemplateId;
  }

  public Integer getCustomerId() {
    return customerId;
  }

  public String getAdFunctionType() {
    return adFunctionType;
  }
  
  public String getEntityState() {
    return entityState;
  }

  public String getNodePath() {
    return nodePath;
  }

  public Integer getEnergyExchangeId() {
    return energyExchangeId;
  }

  public Integer getEnergyExchangeTypeId() {
    return energyExchangeTypeId;
  }

  public Integer getAdFunctionTemplateId() {
    return adFunctionTemplateId;
  }

  public Integer getInstanceId() {
    return instanceId;
  }

  public void setCustomerId(Integer customerId) {
    this.customerId = customerId;
  }

  public void setAdFunctionType(String adFunctionType) {
    this.adFunctionType = adFunctionType;
  }

  public void setEntityState(String entityState) {
    this.entityState = entityState;
  }

  public void setNodePath(String nodePath) {
    this.nodePath = nodePath;
  }

  public void setEnergyExchangeId(Integer energyExchangeId) {
    this.energyExchangeId = energyExchangeId;
  }

  public void setEnergyExchangeTypeId(Integer energyExchangeTypeId) {
    this.energyExchangeTypeId = energyExchangeTypeId;
  }

  public void setAdFunctionTemplateId(Integer adFunctionTemplateId) {
    this.adFunctionTemplateId = adFunctionTemplateId;
  }

  public void setInstanceId(Integer instanceId) {
    this.instanceId = instanceId;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((adFunctionTemplateId == null) ? 0 : adFunctionTemplateId.hashCode());
    result = prime * result + ((adFunctionType == null) ? 0 : adFunctionType.hashCode());
    result = prime * result + ((customerId == null) ? 0 : customerId.hashCode());
    result = prime * result + ((energyExchangeId == null) ? 0 : energyExchangeId.hashCode());
    result = prime * result + ((energyExchangeTypeId == null) ? 0 : energyExchangeTypeId.hashCode());
    result = prime * result + ((entityState == null) ? 0 : entityState.hashCode());
    result = prime * result + ((instanceId == null) ? 0 : instanceId.hashCode());
    result = prime * result + ((nodePath == null) ? 0 : nodePath.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    AdFunctionInstanceSearchCriteria other = (AdFunctionInstanceSearchCriteria) obj;
    if (adFunctionTemplateId == null) {
      if (other.adFunctionTemplateId != null)
        return false;
    } else if (!adFunctionTemplateId.equals(other.adFunctionTemplateId))
      return false;
    if (adFunctionType == null) {
      if (other.adFunctionType != null)
        return false;
    } else if (!adFunctionType.equals(other.adFunctionType))
      return false;
    if (customerId == null) {
      if (other.customerId != null)
        return false;
    } else if (!customerId.equals(other.customerId))
      return false;
    if (energyExchangeId == null) {
      if (other.energyExchangeId != null)
        return false;
    } else if (!energyExchangeId.equals(other.energyExchangeId))
      return false;
    if (energyExchangeTypeId == null) {
      if (other.energyExchangeTypeId != null)
        return false;
    } else if (!energyExchangeTypeId.equals(other.energyExchangeTypeId))
      return false;
    if (entityState == null) {
      if (other.entityState != null)
        return false;
    } else if (!entityState.equals(other.entityState))
      return false;
    if (instanceId == null) {
      if (other.instanceId != null)
        return false;
    } else if (!instanceId.equals(other.instanceId))
      return false;
    if (nodePath == null) {
      if (other.nodePath != null)
        return false;
    } else if (!nodePath.equals(other.nodePath))
      return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("AdFunctionInstanceSearchCriteria [customerId=").append(customerId)
        .append(", adFunctionType=").append(adFunctionType).append(", entityState=")
        .append(entityState).append(", nodePath=").append(nodePath).append(", energyExchangeId=")
        .append(energyExchangeId).append(", energyExchangeTypeId=").append(energyExchangeTypeId)
        .append(", adFunctionTemplateId=").append(adFunctionTemplateId).append(", instanceId=")
        .append(instanceId).append("]");
    return builder.toString();
  }
}
//@formatter:on