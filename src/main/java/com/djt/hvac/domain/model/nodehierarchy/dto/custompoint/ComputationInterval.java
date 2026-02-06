package com.djt.hvac.domain.model.nodehierarchy.dto.custompoint;

public enum ComputationInterval {
  //@formatter:off
  Daily (1, "Daily", "1dc"),
  Monthly (2, "Monthly", "1nc"),
  QuarterHour(3, "QuarterHour", "15mc");
  //@formatter:on

  private final int id;
  private final String name;
  private final String aggregator;

  private ComputationInterval(int id, String name, String aggregator) {
    this.id = id;
    this.name = name;
    this.aggregator = aggregator;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getAggregator() {
    return aggregator;
  }

  public static ComputationInterval fromId(int id) {
    for (ComputationInterval computationInverval : values()) {
      if (computationInverval.id == id) {
        return computationInverval;
      }
    }

    return null;
  }

  public static ComputationInterval fromName(String name) {
    for (ComputationInterval computationInverval : values()) {
      if (computationInverval.name.equalsIgnoreCase(name)) {
        return computationInverval;
      }
    }

    return null;
  }

  public static ComputationInterval fromAggregator(String aggregator) {
    for (ComputationInterval computationInverval : values()) {
      if (computationInverval.aggregator.equalsIgnoreCase(aggregator)) {
        return computationInverval;
      }
    }

    return null;
  }
}
