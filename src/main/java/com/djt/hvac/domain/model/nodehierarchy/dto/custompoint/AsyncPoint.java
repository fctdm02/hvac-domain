package com.djt.hvac.domain.model.nodehierarchy.dto.custompoint;

import java.util.Collections;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.Lists;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = AsyncPoint.Builder.class)
public class AsyncPoint {
  private final Integer id;
  private final String name;
  private final String displayName;
  private final String uuid;
  private final Integer customerId;
  private final Integer parentId;
  private final Integer dataTypeId;
  private final String metricId;
  private final String nodePath;
  private final String unitType;
  private final Integer unitId;
  private final String range;
  private final Boolean configurable;
  private final Boolean timezoneBasedRollups;
  private final ComputationInterval computationInterval;
  private final List<TemporalAsyncPointConfig> temporalAsyncPoints;

  private AsyncPoint(Builder builder) {
    this.id = builder.id;
    this.name = builder.name;
    this.displayName = builder.displayName;
    this.uuid = builder.uuid;
    this.customerId = builder.customerId;
    this.parentId = builder.parentId;
    this.dataTypeId = builder.dataTypeId;
    this.metricId = builder.metricId;
    this.nodePath = builder.nodePath;
    this.unitType = builder.unitType;
    this.unitId = builder.unitId;
    this.range = builder.range;
    this.configurable = builder.configurable;
    this.timezoneBasedRollups = builder.timezoneBasedRollups;
    this.computationInterval = builder.computationInterval;
    this.temporalAsyncPoints = builder.temporalAsyncPoints;
  }

  public Integer getId() {
    return id;
  }


  @NotNull(message = "{AsyncPoint.0}")
  public String getName() {
    return name;
  }

  @NotNull(message = "{AsyncPoint.1}")
  public String getDisplayName() {
    return displayName;
  }

  public String getUuid() {
    return uuid;
  }

  @NotNull(message = "{AsyncPoint.2}")
  public Integer getCustomerId() {
    return customerId;
  }

  @NotNull(message = "{AsyncPoint.3}")
  public Integer getParentId() {
    return parentId;
  }

  @NotNull(message = "{AsyncPoint.4}")
  public Integer getDataTypeId() {
    return dataTypeId;
  }

  @NotNull(message = "{AsyncPoint.5}")
  public String getMetricId() {
    return metricId;
  }

  public String getNodePath() {
    return nodePath;
  }

  public String getUnitType() {
    return unitType;
  }

  @NotNull(message = "{AsyncPoint.6}")
  public Integer getUnitId() {
    return unitId;
  }

  public String getRange() {
    return range;
  }

  public Boolean getConfigurable() {
    return configurable;
  }

  @NotNull(message = "{AsyncPoint.7}")
  public Boolean getTimezoneBasedRollups() {
    return timezoneBasedRollups;
  }

  @NotNull(message = "{AsyncPoint.8}")
  public ComputationInterval getComputationInterval() {
    return computationInterval;
  }

  @Valid
  @NotNull(message = "{AsyncPoint.9}")
  public List<TemporalAsyncPointConfig> getTemporalAsyncPoints() {
    return temporalAsyncPoints;
  }

  @Override
  public String toString() {
    return "AsyncPoint [id=" + id + ", name=" + name + ", displayName=" + displayName + ", uuid="
        + uuid + ", customerId=" + customerId + ", parentId=" + parentId + ", dataTypeId="
        + dataTypeId + ", metricId=" + metricId + ", nodePath=" + nodePath + ", unitType="
        + unitType + ", unitId=" + unitId + ", range=" + range + ", configurable=" + configurable
        + ", timezoneBasedRollups=" + timezoneBasedRollups + ", computationInterval="
        + computationInterval + ", temporalAsyncPoints=" + temporalAsyncPoints + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((computationInterval == null) ? 0 : computationInterval.hashCode());
    result = prime * result + ((configurable == null) ? 0 : configurable.hashCode());
    result = prime * result + ((customerId == null) ? 0 : customerId.hashCode());
    result = prime * result + ((dataTypeId == null) ? 0 : dataTypeId.hashCode());
    result = prime * result + ((displayName == null) ? 0 : displayName.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((metricId == null) ? 0 : metricId.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((nodePath == null) ? 0 : nodePath.hashCode());
    result = prime * result + ((parentId == null) ? 0 : parentId.hashCode());
    result = prime * result + ((range == null) ? 0 : range.hashCode());
    result = prime * result + ((temporalAsyncPoints == null) ? 0 : temporalAsyncPoints.hashCode());
    result =
        prime * result + ((timezoneBasedRollups == null) ? 0 : timezoneBasedRollups.hashCode());
    result = prime * result + ((unitId == null) ? 0 : unitId.hashCode());
    result = prime * result + ((unitType == null) ? 0 : unitType.hashCode());
    result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    AsyncPoint other = (AsyncPoint) obj;
    if (computationInterval != other.computationInterval)
      return false;
    if (configurable == null) {
      if (other.configurable != null)
        return false;
    } else if (!configurable.equals(other.configurable))
      return false;
    if (customerId == null) {
      if (other.customerId != null)
        return false;
    } else if (!customerId.equals(other.customerId))
      return false;
    if (dataTypeId == null) {
      if (other.dataTypeId != null)
        return false;
    } else if (!dataTypeId.equals(other.dataTypeId))
      return false;
    if (displayName == null) {
      if (other.displayName != null)
        return false;
    } else if (!displayName.equals(other.displayName))
      return false;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (metricId == null) {
      if (other.metricId != null)
        return false;
    } else if (!metricId.equals(other.metricId))
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (nodePath == null) {
      if (other.nodePath != null)
        return false;
    } else if (!nodePath.equals(other.nodePath))
      return false;
    if (parentId == null) {
      if (other.parentId != null)
        return false;
    } else if (!parentId.equals(other.parentId))
      return false;
    if (range == null) {
      if (other.range != null)
        return false;
    } else if (!range.equals(other.range))
      return false;
    if (temporalAsyncPoints == null) {
      if (other.temporalAsyncPoints != null)
        return false;
    } else if (!temporalAsyncPoints.equals(other.temporalAsyncPoints))
      return false;
    if (timezoneBasedRollups == null) {
      if (other.timezoneBasedRollups != null)
        return false;
    } else if (!timezoneBasedRollups.equals(other.timezoneBasedRollups))
      return false;
    if (unitId == null) {
      if (other.unitId != null)
        return false;
    } else if (!unitId.equals(other.unitId))
      return false;
    if (unitType == null) {
      if (other.unitType != null)
        return false;
    } else if (!unitType.equals(other.unitType))
      return false;
    if (uuid == null) {
      if (other.uuid != null)
        return false;
    } else if (!uuid.equals(other.uuid))
      return false;
    return true;
  }

  /**
   * Creates builder to build {@link AsyncPoint}.
   *
   * @return created builder
   */
  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(AsyncPoint other) {
    return new Builder(other);
  }

  /**
   * Builder to build {@link AsyncPoint}.
   */
  public static final class Builder {
    private Integer id;
    private String name;
    private String displayName;
    private String uuid;
    private Integer customerId;
    private Integer parentId;
    private Integer dataTypeId;
    private String metricId;
    private String nodePath;
    private String unitType;
    private Integer unitId;
    private String range;
    private Boolean configurable;
    private Boolean timezoneBasedRollups;
    private ComputationInterval computationInterval;
    private List<TemporalAsyncPointConfig> temporalAsyncPoints = Collections.emptyList();

    private Builder() {}

    private Builder(AsyncPoint other) {
      this.id = other.id;
      this.name = other.name;
      this.displayName = other.displayName;
      this.uuid = other.uuid;
      this.customerId = other.customerId;
      this.parentId = other.parentId;
      this.dataTypeId = other.dataTypeId;
      this.metricId = other.metricId;
      this.nodePath = other.nodePath;
      this.unitType = other.unitType;
      this.unitId = other.unitId;
      this.range = other.range;
      this.configurable = other.configurable;
      this.timezoneBasedRollups = other.timezoneBasedRollups;
      this.computationInterval = other.computationInterval;

      if (other.temporalAsyncPoints != null && other.temporalAsyncPoints.size() > 0) {
        this.temporalAsyncPoints = Lists.newCopyOnWriteArrayList(other.temporalAsyncPoints);
      }
    }

    public Builder withId(Integer id) {
      this.id = id;
      return this;
    }

    public Builder withName(String name) {
      this.name = name;
      return this;
    }

    public Builder withDisplayName(String displayName) {
      this.displayName = displayName;
      return this;
    }

    public Builder withUuid(String uuid) {
      this.uuid = uuid;
      return this;
    }

    public Builder withCustomerId(Integer customerId) {
      this.customerId = customerId;
      return this;
    }

    public Builder withParentId(Integer parentId) {
      this.parentId = parentId;
      return this;
    }

    public Builder withDataTypeId(Integer dataTypeId) {
      this.dataTypeId = dataTypeId;
      return this;
    }

    public Builder withMetricId(String metricId) {
      this.metricId = metricId;
      return this;
    }

    public Builder withNodePath(String nodePath) {
      this.nodePath = nodePath;
      return this;
    }

    public Builder withUnitType(String unitType) {
      this.unitType = unitType;
      return this;
    }

    public Builder withUnitId(Integer unitId) {
      this.unitId = unitId;
      return this;
    }

    public Builder withRange(String range) {
      this.range = range;
      return this;
    }

    public Builder withConfigurable(Boolean configurable) {
      this.configurable = configurable;
      return this;
    }

    public Builder withTimezoneBasedRollups(Boolean timezoneBasedRollups) {
      this.timezoneBasedRollups = timezoneBasedRollups;
      return this;
    }

    public Builder withComputationInterval(ComputationInterval computationInterval) {
      this.computationInterval = computationInterval;
      return this;
    }

    public Builder withTemporalAsyncPoints(List<TemporalAsyncPointConfig> temporalAsyncPoints) {
      this.temporalAsyncPoints = temporalAsyncPoints;
      return this;
    }

    public AsyncPoint build() {
      return new AsyncPoint(this);
    }
  }
}
