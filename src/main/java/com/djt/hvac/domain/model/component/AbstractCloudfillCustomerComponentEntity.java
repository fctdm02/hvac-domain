//@formatter:off
package com.djt.hvac.domain.model.component;

import java.sql.Timestamp;

import com.djt.hvac.domain.model.component.enums.CloudfillConnectorStatus;
import com.djt.hvac.domain.model.component.enums.ComponentType;

public abstract class AbstractCloudfillCustomerComponentEntity extends AbstractCustomerComponentEntity {

  private static final long serialVersionUID = 3867887026722373349L;
  
  private Boolean active;
  private CloudfillConnectorStatus cloudfillConnectorStatus;
  private Boolean deleted;
  
  public AbstractCloudfillCustomerComponentEntity(
      Integer customerId,
      ComponentType componentType,
      String uuid,
      String name,
      String ipAddress,
      Timestamp createdAt,
      Timestamp updatedAt,
      Boolean active,
      Boolean deleted) {
    this(
        null,
        customerId,
        componentType,
        uuid,
        name,
        ipAddress,
        createdAt,
        updatedAt,
        active,
        deleted);
  }
  
  public AbstractCloudfillCustomerComponentEntity(
      Integer persistentIdentity,
      Integer customerId,
      ComponentType componentType,
      String uuid,
      String name,
      String ipAddress,
      Timestamp createdAt,
      Timestamp updatedAt,
      Boolean active,
      Boolean deleted) {
    super(
        null,
        customerId,
        componentType,
        uuid,
        name,
        ipAddress,
        createdAt,
        updatedAt);
    this.active = active;
    this.cloudfillConnectorStatus = CloudfillConnectorStatus.NOT_STARTED;
    this.deleted = deleted;
  }

  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

  public CloudfillConnectorStatus getCloudfillConnectorStatus() {
    return cloudfillConnectorStatus;
  }

  public void setCloudfillConnectorStatus(CloudfillConnectorStatus cloudfillConnectorStatus) {
    this.cloudfillConnectorStatus = cloudfillConnectorStatus;
  }

  public Boolean getDeleted() {
    return deleted;
  }

  public void setDeleted(Boolean deleted) {
    this.deleted = deleted;
  }  
}
//@formatter:on