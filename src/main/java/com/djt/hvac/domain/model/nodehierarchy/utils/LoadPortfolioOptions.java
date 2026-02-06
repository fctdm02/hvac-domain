package com.djt.hvac.domain.model.nodehierarchy.utils;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import com.djt.hvac.domain.model.dictionary.enums.NodeType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.ImmutableList;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = LoadPortfolioOptions.Builder.class)
public class LoadPortfolioOptions {
  
  public static final Integer DEFAULT_TIME_TO_LIVE_1_HOUR = 3600;
  
  private final Integer customerId;
  private final NodeType filterNodeType;
  private final List<Integer> filterNodePersistentIdentities;
  private final NodeType depthNodeType;
  private final Boolean loadAdFunctionInstances;
  private final Boolean loadReportInstances;
  private final Boolean loadPointLastValues;
  private final Boolean loadBuildingTemporalData;
  private final Boolean loadCustomPointTemporalData;
  private final Boolean loadUnmappedRawPointsOnly;
  private final Boolean loadIgnoredRawPoints;
  private final Boolean loadDeletedRawPoints; 
  private final Boolean loadDistributorPaymentMethods;
  private final Boolean loadDistributorUsers;
  private final Integer timeToLiveInSeconds; // If cached, then the TTL for the portfolio 

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (LoadPortfolioOptions loadPortfolioOptions) {
    return new Builder(loadPortfolioOptions);
  }

  private LoadPortfolioOptions (Builder builder) {
    this.customerId = builder.customerId;
    this.filterNodeType = builder.filterNodeType;
    this.filterNodePersistentIdentities = builder.filterNodePersistentIdentities;
    this.depthNodeType = builder.depthNodeType;
    this.loadAdFunctionInstances = builder.loadAdFunctionInstances;
    this.loadReportInstances = builder.loadReportInstances;
    this.loadPointLastValues = builder.loadPointLastValues;
    this.loadBuildingTemporalData = builder.loadBuildingTemporalData;
    this.loadCustomPointTemporalData = builder.loadCustomPointTemporalData;
    this.loadUnmappedRawPointsOnly = builder.loadUnmappedRawPointsOnly;
    this.loadIgnoredRawPoints = builder.loadIgnoredRawPoints;
    this.loadDeletedRawPoints = builder.loadDeletedRawPoints;
    this.loadDistributorPaymentMethods = builder.loadDistributorPaymentMethods;
    this.loadDistributorUsers = builder.loadDistributorUsers;
    this.timeToLiveInSeconds = builder.timeToLiveInSeconds;
  }

  public Integer getCustomerId() {
    return customerId;
  }

  public NodeType getFilterNodeType() {
    return filterNodeType;
  }

  public List<Integer> getFilterNodePersistentIdentities() {
    return filterNodePersistentIdentities;
  }

  public NodeType getDepthNodeType() {
    return depthNodeType;
  }

  public Boolean getLoadAdFunctionInstances() {
    return loadAdFunctionInstances;
  }

  public Boolean getLoadReportInstances() {
    return loadReportInstances;
  }

  public Boolean getLoadPointLastValues() {
    return loadPointLastValues;
  }

  public Boolean getLoadBuildingTemporalData() {
    return loadBuildingTemporalData;
  }
  
  public Boolean getLoadCustomPointTemporalData() {
    return loadCustomPointTemporalData;
  }

  public Boolean getLoadUnmappedRawPointsOnly() {
    return loadUnmappedRawPointsOnly;
  }

  public Boolean getLoadIgnoredRawPoints() {
    return loadIgnoredRawPoints;
  }

  public Boolean getLoadDeletedRawPoints() {
    return loadDeletedRawPoints;
  }
  
  public Boolean getLoadDistributorPaymentMethods() {
    return loadDistributorPaymentMethods;
  }

  public Boolean getLoadDistributorUsers() {
    return loadDistributorUsers;
  }
  
  public Integer getTimeToLiveInSeconds() {
    return timeToLiveInSeconds;
  }
  
  @Override
  public String toString() {
    return new StringBuilder()
        .append("Builder [customerId=")
        .append(customerId)
        .append(", filterNodeType=")
        .append(filterNodeType)
        .append(", filterNodePersistentIdentities=")
        .append(filterNodePersistentIdentities)
        .append(", depthNodeType=")
        .append(depthNodeType)
        .append(", loadAdFunctionInstances=")
        .append(loadAdFunctionInstances)
        .append(", loadReportInstances=")
        .append(loadReportInstances)
        .append(", loadPointLastValues=")
        .append(loadPointLastValues)
        .append(", loadBuildingTemporalData=")
        .append(loadBuildingTemporalData)
        .append(", loadCustomPointTemporalData=")
        .append(loadCustomPointTemporalData)
        .append(", loadUnmappedRawPointsOnly=")
        .append(loadUnmappedRawPointsOnly)
        .append(", loadIgnoredRawPoints=")
        .append(loadIgnoredRawPoints)
        .append(", loadDeletedRawPoints=")
        .append(loadDeletedRawPoints)
        .append(", loadDistributorPaymentMethods=")
        .append(loadDistributorPaymentMethods)
        .append(", loadDistributorUsers=")
        .append(loadDistributorUsers)
        .append(", timeToLiveInSeconds=")
        .append(timeToLiveInSeconds)
        .append("]")
        .toString();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((customerId == null) ? 0 : customerId.hashCode());
    result = prime * result + ((depthNodeType == null) ? 0 : depthNodeType.hashCode());
    result = prime * result + ((filterNodePersistentIdentities == null) ? 0 : filterNodePersistentIdentities.hashCode());
    result = prime * result + ((filterNodeType == null) ? 0 : filterNodeType.hashCode());
    result = prime * result + ((loadAdFunctionInstances == null) ? 0 : loadAdFunctionInstances.hashCode());
    result = prime * result + ((loadBuildingTemporalData == null) ? 0 : loadBuildingTemporalData.hashCode());
    result = prime * result + ((loadCustomPointTemporalData == null) ? 0 : loadCustomPointTemporalData.hashCode());
    result = prime * result + ((loadUnmappedRawPointsOnly == null) ? 0 : loadUnmappedRawPointsOnly.hashCode());
    result = prime * result + ((loadIgnoredRawPoints == null) ? 0 : loadIgnoredRawPoints.hashCode());
    result = prime * result + ((loadDeletedRawPoints == null) ? 0 : loadDeletedRawPoints.hashCode());
    result = prime * result + ((loadDistributorPaymentMethods == null) ? 0 : loadDistributorPaymentMethods.hashCode());
    result = prime * result + ((loadDistributorUsers == null) ? 0 : loadDistributorUsers.hashCode());
    result = prime * result + ((loadPointLastValues == null) ? 0 : loadPointLastValues.hashCode());
    result = prime * result + ((loadReportInstances == null) ? 0 : loadReportInstances.hashCode());
    result = prime * result + ((timeToLiveInSeconds == null) ? 0 : timeToLiveInSeconds.hashCode());
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
    LoadPortfolioOptions other = (LoadPortfolioOptions) obj;
    if (customerId == null) {
      if (other.customerId != null)
        return false;
    } else if (!customerId.equals(other.customerId))
      return false;
    if (depthNodeType != other.depthNodeType)
      return false;
    if (filterNodePersistentIdentities == null) {
      if (other.filterNodePersistentIdentities != null)
        return false;
    } else if (!filterNodePersistentIdentities.equals(other.filterNodePersistentIdentities))
      return false;
    if (filterNodeType != other.filterNodeType)
      return false;
    if (loadAdFunctionInstances == null) {
      if (other.loadAdFunctionInstances != null)
        return false;
    } else if (!loadAdFunctionInstances.equals(other.loadAdFunctionInstances))
      return false;
    if (loadBuildingTemporalData == null) {
      if (other.loadBuildingTemporalData != null)
        return false;
    } else if (!loadBuildingTemporalData.equals(other.loadBuildingTemporalData))
      return false;
    if (loadCustomPointTemporalData == null) {
      if (other.loadCustomPointTemporalData != null)
        return false;
    } else if (!loadCustomPointTemporalData.equals(other.loadCustomPointTemporalData))
      return false;
    if (loadUnmappedRawPointsOnly == null) {
      if (other.loadUnmappedRawPointsOnly != null)
        return false;
    } else if (!loadUnmappedRawPointsOnly.equals(other.loadUnmappedRawPointsOnly))
      return false;
    if (loadIgnoredRawPoints == null) {
      if (other.loadIgnoredRawPoints != null)
        return false;
    } else if (!loadIgnoredRawPoints.equals(other.loadIgnoredRawPoints))
      return false;
    if (loadDeletedRawPoints == null) {
      if (other.loadDeletedRawPoints != null)
        return false;
    } else if (!loadDeletedRawPoints.equals(other.loadDeletedRawPoints))
      return false;
    if (loadDistributorPaymentMethods == null) {
      if (other.loadDistributorPaymentMethods != null)
        return false;
    } else if (!loadDistributorPaymentMethods.equals(other.loadDistributorPaymentMethods))
      return false;
    if (loadDistributorUsers == null) {
      if (other.loadDistributorUsers != null)
        return false;
    } else if (!loadDistributorUsers.equals(other.loadDistributorUsers))
      return false;
    if (loadPointLastValues == null) {
      if (other.loadPointLastValues != null)
        return false;
    } else if (!loadPointLastValues.equals(other.loadPointLastValues))
      return false;
    if (loadReportInstances == null) {
      if (other.loadReportInstances != null)
        return false;
    } else if (!loadReportInstances.equals(other.loadReportInstances))
      return false;
    if (timeToLiveInSeconds == null) {
      if (other.timeToLiveInSeconds != null)
        return false;
    } else if (!timeToLiveInSeconds.equals(other.timeToLiveInSeconds))
      return false;
    
    return true;
  }
  
  @JsonPOJOBuilder
  public static class Builder {
    private Integer customerId;
    private NodeType filterNodeType;
    private List<Integer> filterNodePersistentIdentities;
    private NodeType depthNodeType;
    private Boolean loadAdFunctionInstances = Boolean.FALSE;
    private Boolean loadReportInstances = Boolean.FALSE;
    private Boolean loadPointLastValues = Boolean.FALSE;
    private Boolean loadBuildingTemporalData = Boolean.FALSE;
    private Boolean loadCustomPointTemporalData = Boolean.FALSE;
    private Boolean loadUnmappedRawPointsOnly = Boolean.FALSE;
    private Boolean loadIgnoredRawPoints = Boolean.FALSE;
    private Boolean loadDeletedRawPoints = Boolean.FALSE;
    private Boolean loadDistributorPaymentMethods = Boolean.FALSE;
    private Boolean loadDistributorUsers = Boolean.FALSE;
    private Integer timeToLiveInSeconds = DEFAULT_TIME_TO_LIVE_1_HOUR;

    private Builder() {}

    private Builder(LoadPortfolioOptions loadPortfolioOptions) {
      requireNonNull(loadPortfolioOptions, "loadPortfolioOptions cannot be null");
      this.customerId = loadPortfolioOptions.customerId;
      this.filterNodeType = loadPortfolioOptions.filterNodeType;
      this.filterNodePersistentIdentities = loadPortfolioOptions.filterNodePersistentIdentities;
      this.depthNodeType = loadPortfolioOptions.depthNodeType;
      this.loadAdFunctionInstances = loadPortfolioOptions.loadAdFunctionInstances;
      this.loadReportInstances = loadPortfolioOptions.loadReportInstances;
      this.loadPointLastValues = loadPortfolioOptions.loadPointLastValues;
      this.loadBuildingTemporalData = loadPortfolioOptions.loadBuildingTemporalData;
      this.loadCustomPointTemporalData = loadPortfolioOptions.loadCustomPointTemporalData;
      this.loadUnmappedRawPointsOnly = loadPortfolioOptions.loadUnmappedRawPointsOnly;
      this.loadIgnoredRawPoints = loadPortfolioOptions.loadIgnoredRawPoints;
      this.loadDeletedRawPoints = loadPortfolioOptions.loadDeletedRawPoints;
      this.loadDistributorPaymentMethods = loadPortfolioOptions.loadDistributorPaymentMethods;
      this.loadDistributorUsers = loadPortfolioOptions.loadDistributorUsers;
      this.timeToLiveInSeconds = loadPortfolioOptions.timeToLiveInSeconds;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withCustomerId(Integer customerId) {
      requireNonNull(customerId, "customerId cannot be null");
      this.customerId = customerId;
      return this;
    }

    public Builder withFilterNodeType(NodeType filterNodeType) {
      requireNonNull(filterNodeType, "filterNodeType cannot be null");
      this.filterNodeType = filterNodeType;
      return this;
    }

    public Builder withFilterNodePersistentIdentities(List<Integer> filterNodePersistentIdentities) {
      requireNonNull(filterNodePersistentIdentities, "filterNodePersistentIdentities cannot be null");
      this.filterNodePersistentIdentities = ImmutableList.copyOf(filterNodePersistentIdentities);
      return this;
    }

    public Builder withFilterNodePersistentIdentities(Collection<Integer> filterNodePersistentIdentities) {
      if (filterNodePersistentIdentities != null) {
        this.filterNodePersistentIdentities = new ArrayList<>();
        this.filterNodePersistentIdentities.addAll(filterNodePersistentIdentities);  
      }
      return this;
    }
    
    public Builder withFilterNodePersistentIdentity(Integer filterNodePersistentIdentity) {
      if (filterNodePersistentIdentity != null) {
        this.filterNodePersistentIdentities = Arrays.asList(filterNodePersistentIdentity);  
      }
      return this;
    }

    public Builder withDepthNodeType(NodeType depthNodeType) {
      requireNonNull(depthNodeType, "depthNodeType cannot be null");
      this.depthNodeType = depthNodeType;
      return this;
    }

    public Builder withLoadAdFunctionInstances(Boolean loadAdFunctionInstances) {
      requireNonNull(loadAdFunctionInstances, "loadAdFunctionInstances cannot be null");
      this.loadAdFunctionInstances = loadAdFunctionInstances;
      return this;
    }

    public Builder withLoadReportInstances(Boolean loadReportInstances) {
      requireNonNull(loadReportInstances, "loadReportInstances cannot be null");
      this.loadReportInstances = loadReportInstances;
      if (this.loadReportInstances) {
        this.loadAdFunctionInstances = true;  
      }
      return this;
    }

    public Builder withLoadPointLastValues(Boolean loadPointLastValues) {
      requireNonNull(loadPointLastValues, "loadPointLastValues cannot be null");
      this.loadPointLastValues = loadPointLastValues;
      return this;
    }

    public Builder withLoadDistributorPaymentMethods(Boolean loadDistributorPaymentMethods) {
      requireNonNull(loadDistributorPaymentMethods, "loadDistributorPaymentMethods cannot be null");
      this.loadDistributorPaymentMethods = loadDistributorPaymentMethods;
      return this;
    }

    public Builder withLoadBuildingTemporalData(Boolean loadBuildingTemporalData) {
      requireNonNull(loadBuildingTemporalData, "loadBuildingTemporalData cannot be null");
      this.loadBuildingTemporalData = loadBuildingTemporalData;
      return this;
    }
    
    public Builder withLoadCustomPointTemporalData(Boolean loadCustomPointTemporalData) {
      requireNonNull(loadCustomPointTemporalData, "loadCustomPointTemporalData cannot be null");
      this.loadCustomPointTemporalData = loadCustomPointTemporalData;
      return this;
    }
    
    public Builder withLoadUnmappedRawPointsOnly(Boolean loadUnmappedRawPointsOnly) {
      requireNonNull(loadUnmappedRawPointsOnly, "loadUnmappedRawPointsOnly cannot be null");
      this.loadUnmappedRawPointsOnly = loadUnmappedRawPointsOnly;
      return this;
    }

    public Builder withLoadIgnoredRawPoints(Boolean loadIgnoredRawPoints) {
      requireNonNull(loadIgnoredRawPoints, "loadIgnoredRawPoints cannot be null");
      this.loadIgnoredRawPoints = loadIgnoredRawPoints;
      return this;
    }

    public Builder withLoadDeletedRawPoints(Boolean loadDeletedRawPoints) {
      requireNonNull(loadDeletedRawPoints, "loadDeletedRawPoints cannot be null");
      this.loadDeletedRawPoints = loadDeletedRawPoints;
      return this;
    }

    public Builder withLoadDistributorUsers(Boolean loadDistributorUsers) {
      requireNonNull(loadDistributorUsers, "loadDistributorUsers cannot be null");
      this.loadDistributorUsers = loadDistributorUsers;
      return this;
    }

    public Builder withTimeToLiveInSeconds(Integer timeToLiveInSeconds) {
      requireNonNull(timeToLiveInSeconds, "timeToLiveInSeconds cannot be null");
      this.timeToLiveInSeconds = timeToLiveInSeconds;
      return this;
    }
        
    public LoadPortfolioOptions build() {
      requireNonNull(customerId, "customerId cannot be null");
      
      if (filterNodeType != null && filterNodePersistentIdentities == null) {
        throw new IllegalArgumentException("'filterNodePersistentIdentities' must be specified when 'filterNodeType' is specified: " + filterNodeType);
      } else if (filterNodePersistentIdentities != null && filterNodeType == null) {
        throw new IllegalArgumentException("'filterNodeType' must be specified when 'filterNodePersistentIdentities' is specified: " + filterNodeType);
      }

      if (filterNodePersistentIdentities != null && filterNodePersistentIdentities.isEmpty()) {
        filterNodeType = null;
        filterNodePersistentIdentities = null;
      }
      return new LoadPortfolioOptions(this);
    }
  }
}