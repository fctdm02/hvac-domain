package com.djt.hvac.domain.model.user.enums;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * <pre>
|id         |application_id|name                                                                                                |
|-----------|--------------|----------------------------------------------------------------------------------------------------|
|1          |1             |Super Admin                                                                                         |
|2          |1             |Portfolio Manager                                                                                   |
|3          |1             |Facility Manager                                                                                    |
|4          |1             |Limited User                                                                                        |
|5          |2             |Distributor Admin                                                                                   |
|6          |2             |Distributor User                                                                                    |
 * </pre>
 * 
 * @author tmyers
 *
 */
public enum UserRoleType {
  SUPER_ADMIN(1, ApplicationType.FUSION_3_0, " Super Admin"),
  PORTFOLIO_MANAGER(2, ApplicationType.FUSION_3_0, " Portfolio Manager"),
  FACILITY_MANAGER(3, ApplicationType.FUSION_3_0, "Facility Manager"),
  LIMITED_USER(4, ApplicationType.FUSION_3_0, "Limited User"),
  DISTRIBUTOR_ADMIN(5, ApplicationType.SYNERGY_3_0, "Distributor Admin"),
  DISTRIBUTOR_USER(6, ApplicationType.SYNERGY_3_0, "Distributor User");
  
  private static final Map<Integer, UserRoleType> VALUES;
  
  private final int id;
  private final ApplicationType applicationType;
  private final String name;
  
  static {
    Map<Integer, UserRoleType> types = Maps.newHashMap();
    for (UserRoleType type : UserRoleType.values()) {
      types.put(type.id, type);
    }
    VALUES = ImmutableMap.copyOf(types);
  }
  
  public static UserRoleType get(int id) {
    return VALUES.get(id);
  }
  
  private UserRoleType(int id, ApplicationType applicationType, String name) {
    this.id = id;
    this.applicationType = applicationType;
    this.name = name;
  }
  
  public int getId() {
    return id;
  }
  
  public ApplicationType getApplicationType() {
    return applicationType;
  }

  public String getName() {
    return name;
  }  
}
