//@formatter:off
package com.djt.hvac.domain.model.component;

import java.sql.Timestamp;

import com.djt.hvac.domain.model.component.enums.ComponentType;

public final class KmcCloudfillCustomerComponentEntity extends AbstractConfigurableCloudfillCustomerComponentEntity {

  private static final long serialVersionUID = 3867887026722373349L;
  
  public KmcCloudfillCustomerComponentEntity(
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
  
  public KmcCloudfillCustomerComponentEntity(
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
        deleted,
        configJson);
  }
}
//@formatter:on