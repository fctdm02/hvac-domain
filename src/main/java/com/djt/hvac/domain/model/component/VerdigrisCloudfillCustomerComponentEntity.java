//@formatter:off
package com.djt.hvac.domain.model.component;

import java.sql.Timestamp;

import com.djt.hvac.domain.model.component.enums.ComponentType;

public final class VerdigrisCloudfillCustomerComponentEntity extends AbstractCloudfillCustomerComponentEntity {

  private static final long serialVersionUID = 3867887026722373349L;
  
  public VerdigrisCloudfillCustomerComponentEntity(
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
  
  public VerdigrisCloudfillCustomerComponentEntity(
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
        updatedAt,
        active,
        deleted);
  }
}
//@formatter:on