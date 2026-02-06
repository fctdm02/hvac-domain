//@formatter:off
package com.djt.hvac.domain.model.component;

import java.sql.Timestamp;

import com.djt.hvac.domain.model.component.enums.ComponentType;

public abstract class AbstractConfigurableCloudfillCustomerComponentEntity extends AbstractCloudfillCustomerComponentEntity {

  private static final long serialVersionUID = 3867887026722373349L;
  
  private String configJson;
  
  public AbstractConfigurableCloudfillCustomerComponentEntity(
      Integer customerId,
      ComponentType componentType,
      String uuid,
      String name,
      String ipAddress,
      Timestamp createdAt,
      Timestamp updatedAt,
      Boolean active,
      Boolean deleted,
      String configJson) {
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
        deleted,
        configJson);
  }
  
  public AbstractConfigurableCloudfillCustomerComponentEntity(
      Integer persistentIdentity,
      Integer customerId,
      ComponentType componentType,
      String uuid,
      String name,
      String ipAddress,
      Timestamp createdAt,
      Timestamp updatedAt,
      Boolean active,
      Boolean deleted,
      String configJson) {
    super(
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
    this.configJson = configJson;
  }

  public String getConfigJson() {
    return configJson;
  }

  public void setConfigJson(String configJson) {
    this.configJson = configJson;
  }  
}
//@formatter:on