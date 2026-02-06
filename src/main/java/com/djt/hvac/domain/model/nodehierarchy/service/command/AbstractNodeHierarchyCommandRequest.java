//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.service.command;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class AbstractNodeHierarchyCommandRequest implements NodeHierarchyCommandRequest {
  
  private static final long serialVersionUID = 1L;
  
  private final Integer customerId;
  private final List<Integer> buildingIds;  
  private final String submittedBy;
  private final Boolean performAutomaticRemediation;
  private final Boolean performAutomaticEvaluateReports;
  private final Boolean performAutomaticConfiguration;
  
  protected <T extends AbstractNodeHierarchyCommandRequest, B extends Builder<T, B>> AbstractNodeHierarchyCommandRequest (B builder) {
    this.customerId = builder.customerId();
    this.buildingIds = builder.buildingIds();
    this.submittedBy = builder.submittedBy();
    this.performAutomaticRemediation = builder.performAutomaticRemediation();
    this.performAutomaticEvaluateReports = builder.performAutomaticEvaluateReports();
    this.performAutomaticConfiguration = builder.performAutomaticConfiguration();
  }
  
  @Override
  public Integer getCustomerId() {
    return customerId;
  }

  @JsonIgnore
  @Override
  public Integer getBuildingId() {
    if (this.buildingIds != null && buildingIds.size() == 1) {
      return this.buildingIds.get(0);
    }
    return null;
  }
  
  @Override
  public List<Integer> getBuildingIds() {
    return buildingIds;
  }
  
  @Override
  public String getSubmittedBy() {
    return submittedBy;
  }
  
  @Override
  public Boolean getPerformAutomaticRemediation() {
    return performAutomaticRemediation;
  }

  @Override
  public Boolean getPerformAutomaticEvaluateReports() {
    return performAutomaticEvaluateReports;
  }
  
  @Override
  public Boolean getPerformAutomaticConfiguration() {
    return performAutomaticConfiguration;
  }
  
  @Override
  public String toString() {
    
    return new StringBuilder()
        .append(getClass().getSimpleName())
        .append(" [customerId=")
        .append(customerId)
        .append(", operationCategory=")
        .append(getOperationCategory())
        .append(", operationType=")
        .append(getOperationType())
        .append(", submittedBy=")
        .append(submittedBy)
        .append(", buildingIds=")
        .append(buildingIds)
        .append(", performAutomaticRemediation=")
        .append(performAutomaticRemediation)
        .append(", performAutomaticEvaluateReports=")
        .append(performAutomaticEvaluateReports)
        .append(", performAutomaticConfiguration=")
        .append(performAutomaticConfiguration)
        .toString();
  }

  public abstract static class Builder <T extends AbstractNodeHierarchyCommandRequest, B extends Builder<T, B>> {
    
    private Integer customerId;
    private List<Integer> buildingIds;  
    private String submittedBy = SYSTEM;
    private Boolean performAutomaticRemediation = Boolean.FALSE;
    private Boolean performAutomaticEvaluateReports = Boolean.FALSE;
    private Boolean performAutomaticConfiguration = Boolean.FALSE;
    
    protected Builder () {}
    
    public B withCustomerId(Integer customerId) {
      this.customerId = requireNonNull(customerId, "customerId cannot be null");
      return getThis();
    }

    public B withBuildingId(Integer buildingId) {
      if (buildingId != null) {
        this.buildingIds = new ArrayList<>();
        this.buildingIds.add(buildingId);
      }
      return getThis();
    }

    public B withBuildingIds(Collection<Integer> buildingIds) {
      if (buildingIds != null && !buildingIds.isEmpty()) {
        this.buildingIds = new ArrayList<>();
        this.buildingIds.addAll(buildingIds);
      }
      return getThis();
    }
    
    public B withSubmittedBy(String submittedBy) {
      this.submittedBy = requireNonNull(submittedBy, "submittedBy cannot be null");
      return getThis();
    }

    public B withPerformAutomaticRemediation(Boolean performAutomaticRemediation) {
      this.performAutomaticRemediation = requireNonNull(performAutomaticRemediation, "performAutomaticRemediation cannot be null");
      return getThis();
    }

    public B withPerformAutomaticEvaluateReports(Boolean performAutomaticEvaluateReports) {
      this.performAutomaticEvaluateReports = requireNonNull(performAutomaticEvaluateReports, "performAutomaticEvaluateReports cannot be null");
      return getThis();
    }

    public B withPerformAutomaticConfiguration(Boolean performAutomaticConfiguration) {
      this.performAutomaticConfiguration = requireNonNull(performAutomaticConfiguration, "performAutomaticConfiguration cannot be null");
      return getThis();
    }
    
    protected Integer customerId() {
      return customerId;
    }

    protected List<Integer> buildingIds() {
      return buildingIds;
    }

    protected String submittedBy() {
      return submittedBy;
    }

    protected Boolean performAutomaticRemediation() {
      return performAutomaticRemediation;
    }

    protected Boolean performAutomaticEvaluateReports() {
      return performAutomaticEvaluateReports;
    }
    
    protected Boolean performAutomaticConfiguration() {
      return performAutomaticConfiguration;
    }

    public T build () {
      requireNonNull(customerId, "customerId cannot be null");
      requireNonNull(submittedBy, "submittedBy cannot be null");
      requireNonNull(performAutomaticRemediation, "performAutomaticRemediation cannot be null");
      requireNonNull(performAutomaticEvaluateReports, "performAutomaticEvaluateReports cannot be null");
      requireNonNull(performAutomaticConfiguration, "performAutomaticConfiguration cannot be null");
      return newInstance();
    }

    protected abstract B getThis ();
    
    protected abstract T newInstance ();
  }
}
//@formatter:on