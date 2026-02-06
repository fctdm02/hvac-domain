
package com.djt.hvac.domain.model.component.dto;

import java.util.Objects;

public class CustomerComponentDto {

  private Integer id;
  private Integer customer;
  private String componentType;
  private String uuid;
  private String name;
  private String ipAddress;
  private String createdAt;
  private String updatedAt;
  private Boolean active;
  private String cloudfillConnectorStatus;
  private Boolean deleted;
  private String configJson;
  
  public Integer getId() {
    return id;
  }
  public void setId(Integer id) {
    this.id = id;
  }
  public Integer getCustomer() {
    return customer;
  }
  public void setCustomer(Integer customer) {
    this.customer = customer;
  }
  public String getComponentType() {
    return componentType;
  }
  public void setComponentType(String componentType) {
    this.componentType = componentType;
  }
  public String getUuid() {
    return uuid;
  }
  public void setUuid(String uuid) {
    this.uuid = uuid;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getIpAddress() {
    return ipAddress;
  }
  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }
  public String getCreatedAt() {
    return createdAt;
  }
  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }
  public String getUpdatedAt() {
    return updatedAt;
  }
  public void setUpdatedAt(String updatedAt) {
    this.updatedAt = updatedAt;
  }
  public Boolean getActive() {
    return active;
  }
  public void setActive(Boolean active) {
    this.active = active;
  }
  public String getCloudfillConnectorStatus() {
    return cloudfillConnectorStatus;
  }
  public void setCloudfillConnectorStatus(String cloudfillConnectorStatus) {
    this.cloudfillConnectorStatus = cloudfillConnectorStatus;
  }
  public Boolean getDeleted() {
    return deleted;
  }
  public void setDeleted(Boolean deleted) {
    this.deleted = deleted;
  }
  public String getConfigJson() {
    return configJson;
  }
  public void setConfigJson(String configJson) {
    this.configJson = configJson;
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(active, cloudfillConnectorStatus, componentType, configJson, createdAt,
        customer, deleted, id, ipAddress, name, updatedAt, uuid);
  }
  
  @Override
  public String toString() {
    return "CustomerComponentDto [id=" + id + ", customer=" + customer + ", componentType="
        + componentType + ", uuid=" + uuid + ", name=" + name + ", ipAddress=" + ipAddress
        + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", active=" + active
        + ", cloudfillConnectorStatus=" + cloudfillConnectorStatus + ", deleted=" + deleted
        + ", configJson=" + configJson + "]";
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    CustomerComponentDto other = (CustomerComponentDto) obj;
    return Objects.equals(active, other.active)
        && Objects.equals(cloudfillConnectorStatus, other.cloudfillConnectorStatus)
        && Objects.equals(componentType, other.componentType)
        && Objects.equals(configJson, other.configJson)
        && Objects.equals(createdAt, other.createdAt) && Objects.equals(customer, other.customer)
        && Objects.equals(deleted, other.deleted) && Objects.equals(id, other.id)
        && Objects.equals(ipAddress, other.ipAddress) && Objects.equals(name, other.name)
        && Objects.equals(updatedAt, other.updatedAt) && Objects.equals(uuid, other.uuid);
  }

}
