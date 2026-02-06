//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.point.computed.async.custom.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.base.Enums;

public enum FillPolicy {
  LAST_KNOWN(1, "Last Known"), 
  ZERO(2, "Zero");
  //@formatter:on

  private final int id;
  private final String displayName;

  private FillPolicy(int id, String name) {
    this.id = id;
    this.displayName = name;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return displayName;
  }

  public static FillPolicy fromId(int id) {
    for (FillPolicy fillPolicy : values()) {
      if (fillPolicy.id == id)
        return fillPolicy;
    }

    return null;
  }

  @JsonCreator
  public static FillPolicy fromName(String name) {
    FillPolicy policy = Enums.getIfPresent(FillPolicy.class, name).orNull();

    if (policy != null) {
      return policy;
    }

    for (FillPolicy fillPolicy : values()) {
      if (fillPolicy.getName().equalsIgnoreCase(name))
        return fillPolicy;
    }

    return null;
  }
}
//@formatter:on