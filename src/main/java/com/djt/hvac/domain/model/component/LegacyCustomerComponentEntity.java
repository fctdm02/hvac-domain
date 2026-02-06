//@formatter:off
package com.djt.hvac.domain.model.component;

import java.sql.Timestamp;

import com.djt.hvac.domain.model.component.enums.ComponentType;

public class LegacyCustomerComponentEntity extends AbstractCustomerComponentEntity {

  private static final long serialVersionUID = 3867887026722373349L;
  
  public LegacyCustomerComponentEntity(
      Integer customerId,
      ComponentType componentType,
      String uuid,
      String name,
      String ipAddress,
      Timestamp createdAt,
      Timestamp updatedAt) {
    super(
        null,
        customerId,
        componentType,
        uuid,
        name,
        ipAddress,
        createdAt,
        updatedAt);
  }
  
  public LegacyCustomerComponentEntity(
      Integer persistentIdentity,
      Integer customerId,
      ComponentType componentType,
      String uuid,
      String name,
      String ipAddress,
      Timestamp createdAt,
      Timestamp updatedAt) {
    super(
        null,
        customerId,
        componentType,
        uuid,
        name,
        ipAddress,
        createdAt,
        updatedAt);
  }  
}
//@formatter:on