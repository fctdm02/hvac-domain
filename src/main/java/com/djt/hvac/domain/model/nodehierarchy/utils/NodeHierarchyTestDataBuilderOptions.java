package com.djt.hvac.domain.model.nodehierarchy.utils;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.ImmutableSet;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = NodeHierarchyTestDataBuilderOptions.Builder.class)
public class NodeHierarchyTestDataBuilderOptions {
  private final Integer numBuildings;
  private final Integer numFloors;
  private final Integer numEquipmentPerEquipmentType;
  private final Integer numPointsPerEquipmentType;
  private final Set<String> equipmentTypeNames;
  private final Set<String> pointTemplateNames;
  private final Boolean performPointMapping;
  private final Boolean performEquipmentTagging;
  private final Boolean performPointTagging;
  private final Boolean createCustomPoints;
  private final Boolean createWeatherPoints;
  private final Boolean createScheduledPoints;
  private final Boolean createBuildingTemporalData;
  private final Boolean createAdFunctionInstanceCandidates;
  private final Boolean createAdFunctionInstances;
  private final Boolean evaluateReports;
  private final Boolean enableReports;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (NodeHierarchyTestDataBuilderOptions nodeHierarchyTestDataBuilderOptions) {
    return new Builder(nodeHierarchyTestDataBuilderOptions);
  }

  private NodeHierarchyTestDataBuilderOptions (Builder builder) {
    this.numBuildings = builder.numBuildings;
    this.numFloors = builder.numFloors;
    this.numEquipmentPerEquipmentType = builder.numEquipmentPerEquipmentType;
    this.numPointsPerEquipmentType = builder.numPointsPerEquipmentType;
    this.equipmentTypeNames = builder.equipmentTypeNames;
    this.pointTemplateNames = builder.pointTemplateNames;
    this.performPointMapping = builder.performPointMapping;
    this.performEquipmentTagging = builder.performEquipmentTagging;
    this.performPointTagging = builder.performPointTagging;
    this.createCustomPoints = builder.createCustomPoints;
    this.createWeatherPoints = builder.createWeatherPoints;
    this.createScheduledPoints = builder.createScheduledPoints;
    this.createBuildingTemporalData = builder.createBuildingTemporalData;
    this.createAdFunctionInstanceCandidates = builder.createAdFunctionInstanceCandidates;
    this.createAdFunctionInstances = builder.createAdFunctionInstances;
    this.evaluateReports = builder.evaluateReports;
    this.enableReports = builder.enableReports;
  }

  public Integer getNumBuildings() {
    return numBuildings;
  }

  public Integer getNumFloors() {
    return numFloors;
  }

  public Integer getNumEquipmentPerEquipmentType() {
    return numEquipmentPerEquipmentType;
  }

  public Integer getNumPointsPerEquipmentType() {
    return numPointsPerEquipmentType;
  }

  public Set<String> getEquipmentTypeNames() {
    return equipmentTypeNames;
  }

  public Set<String> getPointTemplateNames() {
    return pointTemplateNames;
  }

  public Boolean getPerformPointMapping() {
    return performPointMapping;
  }

  public Boolean getPerformEquipmentTagging() {
    return performEquipmentTagging;
  }

  public Boolean getPerformPointTagging() {
    return performPointTagging;
  }

  public Boolean getCreateCustomPoints() {
    return createCustomPoints;
  }

  public Boolean getCreateWeatherPoints() {
    return createWeatherPoints;
  }
  
  public Boolean getCreateScheduledPoints() {
    return createScheduledPoints;
  }

  public Boolean getCreateBuildingTemporalData() {
    return createBuildingTemporalData;
  }

  public Boolean getCreateAdFunctionInstanceCandidates() {
    return createAdFunctionInstanceCandidates;
  }

  public Boolean getCreateAdFunctionInstances() {
    return createAdFunctionInstances;
  }

  public Boolean getEvaluateReports() {
    return evaluateReports;
  }

  public Boolean getEnableReports() {
    return enableReports;
  }

  @Override
  public String toString() {
    return "NodeHierarchyTestDataBuilderOptions [numBuildings=" + numBuildings + ", numFloors="
        + numFloors + ", numEquipmentPerEquipmentType=" + numEquipmentPerEquipmentType
        + ", numPointsPerEquipmentType=" + numPointsPerEquipmentType + ", equipmentTypeNames="
        + equipmentTypeNames + ", pointTemplateNames=" + pointTemplateNames
        + ", performPointMapping=" + performPointMapping + ", performEquipmentTagging="
        + performEquipmentTagging + ", performPointTagging=" + performPointTagging
        + ", createCustomPoints=" + createCustomPoints + ", createWeatherPoints="
        + createWeatherPoints + ", createScheduledPoints=" + createScheduledPoints
        + ", createBuildingTemporalData=" + createBuildingTemporalData
        + ", createAdFunctionInstanceCandidates=" + createAdFunctionInstanceCandidates
        + ", createAdFunctionInstances=" + createAdFunctionInstances + ", evaluateReports="
        + evaluateReports + ", enableReports=" + enableReports + "]";
  }

  @JsonPOJOBuilder
  public static class Builder {
    private Integer numBuildings = Integer.valueOf(1);
    private Integer numFloors = Integer.valueOf(0);
    private Integer numEquipmentPerEquipmentType = Integer.valueOf(1);
    private Integer numPointsPerEquipmentType = Integer.valueOf(1);
    private Set<String> equipmentTypeNames = new HashSet<>(Arrays.asList("ahu"));
    private Set<String> pointTemplateNames = new HashSet<>(Arrays.asList("ClgCmd", "HtgCmd", "DaFanSts"));
    private Boolean performPointMapping = Boolean.TRUE;
    private Boolean performEquipmentTagging = Boolean.FALSE;
    private Boolean performPointTagging = Boolean.FALSE;
    private Boolean createCustomPoints = Boolean.FALSE;
    private Boolean createWeatherPoints = Boolean.FALSE;
    private Boolean createScheduledPoints = Boolean.FALSE;
    private Boolean createBuildingTemporalData = Boolean.FALSE;
    private Boolean createAdFunctionInstanceCandidates = Boolean.FALSE;
    private Boolean createAdFunctionInstances = Boolean.FALSE;
    private Boolean evaluateReports = Boolean.FALSE;
    private Boolean enableReports = Boolean.FALSE;

    private Builder() {}

    private Builder(NodeHierarchyTestDataBuilderOptions nodeHierarchyTestDataBuilderOptions) {
      requireNonNull(nodeHierarchyTestDataBuilderOptions, "nodeHierarchyTestDataBuilderOptions cannot be null");
      this.numBuildings = nodeHierarchyTestDataBuilderOptions.numBuildings;
      this.numFloors = nodeHierarchyTestDataBuilderOptions.numFloors;
      this.numEquipmentPerEquipmentType = nodeHierarchyTestDataBuilderOptions.numEquipmentPerEquipmentType;
      this.numPointsPerEquipmentType = nodeHierarchyTestDataBuilderOptions.numPointsPerEquipmentType;
      this.equipmentTypeNames = nodeHierarchyTestDataBuilderOptions.equipmentTypeNames;
      this.pointTemplateNames = nodeHierarchyTestDataBuilderOptions.pointTemplateNames;
      this.performPointMapping = nodeHierarchyTestDataBuilderOptions.performPointMapping;
      this.performEquipmentTagging = nodeHierarchyTestDataBuilderOptions.performEquipmentTagging;
      this.performPointTagging = nodeHierarchyTestDataBuilderOptions.performPointTagging;
      this.createCustomPoints = nodeHierarchyTestDataBuilderOptions.createCustomPoints;
      this.createWeatherPoints = nodeHierarchyTestDataBuilderOptions.createWeatherPoints;
      this.createScheduledPoints = nodeHierarchyTestDataBuilderOptions.createScheduledPoints;
      this.createBuildingTemporalData = nodeHierarchyTestDataBuilderOptions.createBuildingTemporalData;
      this.createAdFunctionInstanceCandidates = nodeHierarchyTestDataBuilderOptions.createAdFunctionInstanceCandidates;
      this.createAdFunctionInstances = nodeHierarchyTestDataBuilderOptions.createAdFunctionInstances;
      this.evaluateReports = nodeHierarchyTestDataBuilderOptions.evaluateReports;
      this.enableReports = nodeHierarchyTestDataBuilderOptions.enableReports;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withNumBuildings(int numBuildings) {
      this.numBuildings = numBuildings;
      return this;
    }

    public Builder withNumFloors(int numFloors) {
      this.numFloors = numFloors;
      return this;
    }

    public Builder withNumEquipmentPerEquipmentType(int numEquipmentPerEquipmentType) {
      this.numEquipmentPerEquipmentType = numEquipmentPerEquipmentType;
      return this;
    }

    public Builder withNumPointsPerEquipmentType(int numPointsPerEquipmentType) {
      this.numPointsPerEquipmentType = numPointsPerEquipmentType;
      return this;
    }

    public Builder withEquipmentTypeName(String equipmentTypeName) {
      requireNonNull(equipmentTypeName, "equipmentTypeName cannot be null");
      equipmentTypeNames = new HashSet<>();
      equipmentTypeNames.add(equipmentTypeName);
      return this;
    }

    public Builder withEquipmentTypeNames(Set<String> equipmentTypeNames) {
      if (equipmentTypeNames != null) {
        this.equipmentTypeNames = ImmutableSet.copyOf(equipmentTypeNames);  
      }
      return this;
    }

    public Builder withPointTemplateName(String pointTemplateName) {
      requireNonNull(pointTemplateName, "pointTemplateName cannot be null");
      pointTemplateNames = new HashSet<>();
      pointTemplateNames.add(pointTemplateName);
      return this;
    }
    
    public Builder withPointTemplateNames(Set<String> pointTemplateNames) {
      if (pointTemplateNames != null) {
        this.pointTemplateNames = ImmutableSet.copyOf(pointTemplateNames);
      }
      return this;
    }

    public Builder withPerformPointMapping(boolean performPointMapping) {
      this.performPointMapping = performPointMapping;
      return this;
    }

    public Builder withPerformEquipmentTagging(boolean performEquipmentTagging) {
      this.performEquipmentTagging = performEquipmentTagging;
      return this;
    }

    public Builder withPerformPointTagging(boolean performPointTagging) {
      this.performPointTagging = performPointTagging;
      return this;
    }

    public Builder withCreateCustomPoints(boolean createCustomPoints) {
      this.createCustomPoints = createCustomPoints;
      return this;
    }

    public Builder withCreateWeatherPoints(boolean createWeatherPoints) {
      this.createWeatherPoints = createWeatherPoints;
      return this;
    }

    public Builder withCreateScheduledPoints(boolean createScheduledPoints) {
      this.createScheduledPoints = createScheduledPoints;
      return this;
    }
    
    public Builder withCreateBuildingTemporalData(boolean createBuildingTemporalData) {
      this.createBuildingTemporalData = createBuildingTemporalData;
      return this;
    }

    public Builder withCreateAdFunctionInstanceCandidates(boolean createAdFunctionInstanceCandidates) {
      this.createAdFunctionInstanceCandidates = createAdFunctionInstanceCandidates;
      return this;
    }

    public Builder withCreateAdFunctionInstances(boolean createAdFunctionInstances) {
      this.createAdFunctionInstances = createAdFunctionInstances;
      return this;
    }

    public Builder withEvaluateReports(boolean evaluateReports) {
      this.evaluateReports = evaluateReports;
      return this;
    }

    public Builder withEnableReports(boolean enableReports) {
      this.enableReports = enableReports;
      return this;
    }

    public NodeHierarchyTestDataBuilderOptions build() {
      return new NodeHierarchyTestDataBuilderOptions(this);
    }
  }
}