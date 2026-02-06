//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.service.command;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.function.Consumer;

import com.djt.hvac.domain.model.nodehierarchy.service.NodeHierarchyService;
import com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject.RawPointData;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = MapRawPointsRequest.Builder.class)
public class MapRawPointsRequest extends AbstractNodeHierarchyCommandRequest {
  
  private static final long serialVersionUID = 1L;
  
  private final List<RawPointData> rawPoints;
  private final String buildingName;
  private final List<String> subBuildingNames;
  private final List<String> plantNames;
  private final List<String> floorNames;
  private final List<String> equipmentNames;
  private final List<String> pointNames;
  private final Boolean performExclusionOnNames;
  private final String mappingExpression;
  private final String metricIdDelimiter;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (MapRawPointsRequest commandRequest) {
    return new Builder(commandRequest);
  }

  private MapRawPointsRequest(Builder builder) {
    super(builder);
    this.rawPoints = builder.rawPoints;
    this.buildingName = builder.buildingName;
    this.subBuildingNames = builder.subBuildingNames;
    this.plantNames = builder.plantNames;
    this.floorNames = builder.floorNames;
    this.equipmentNames = builder.equipmentNames;
    this.pointNames = builder.pointNames;
    this.performExclusionOnNames = builder.performExclusionOnNames;
    this.mappingExpression = builder.mappingExpression;
    this.metricIdDelimiter = builder.metricIdDelimiter;
  }

  public List<RawPointData> getRawPoints() {
    return rawPoints;
  }

  public String getBuildingName() {
    return buildingName;
  }
  
  public List<String> getSubBuildingNames() {
    return subBuildingNames;
  }

  public List<String> getPlantNames() {
    return plantNames;
  }

  public List<String> getFloorNames() {
    return floorNames;
  }

  public List<String> getEquipmentNames() {
    return equipmentNames;
  }

  public List<String> getPointNames() {
    return pointNames;
  }

  public Boolean getPerformExclusionOnNames() {
    return performExclusionOnNames;
  }

  public String getMappingExpression() {
    return mappingExpression;
  }

  public String getMetricIdDelimiter() {
    return metricIdDelimiter;
  }

  @Override
  public String toString() {
    
    return new StringBuilder()
        .append(super.toString())
        .append(", rawPoints=")
        .append(rawPoints)
        .append(", buildingName=")
        .append(buildingName)
        .append(", subBuildingNames=")
        .append(subBuildingNames)
        .append(", plantNames=")
        .append(plantNames)
        .append(", floorNames=")
        .append(floorNames)
        .append(", equipmentNames=")
        .append(equipmentNames)
        .append(", pointNames=")
        .append(pointNames)
        .append(", performExclusionOnNames=")
        .append(performExclusionOnNames)
        .append(", mappingExpression=")
        .append(mappingExpression)
        .append(", metricIdDelimiter=")
        .append(metricIdDelimiter)
        .append("]")
        .toString();
  }
  
  @Override
  public String getOperationCategory() {
    return NodeHierarchyCommandRequest.BULK_POINT_MAPPING_OPERATION_CATEGORY;
  }
  
  @Override
  public String getOperationType() {
    return NodeHierarchyCommandRequest.MAP_RAW_POINT_OPERATION_TYPE;
  }
  
  @JsonPOJOBuilder
  public static class Builder extends AbstractNodeHierarchyCommandRequest.Builder<MapRawPointsRequest, Builder> {
    
    private List<RawPointData> rawPoints;
    private String buildingName;
    private List<String> subBuildingNames;
    private List<String> plantNames;
    private List<String> floorNames;
    private List<String> equipmentNames;
    private List<String> pointNames;
    private Boolean performExclusionOnNames = Boolean.TRUE;    
    private String mappingExpression;
    private String metricIdDelimiter = NodeHierarchyService.DEFAULT_METRIC_ID_DELIMITER;

    private Builder() {}

    private Builder(MapRawPointsRequest request) {
      requireNonNull(request, "request cannot be null");
      this.rawPoints = request.rawPoints;
      this.buildingName = request.buildingName;
      this.subBuildingNames = request.subBuildingNames;
      this.plantNames = request.plantNames;
      this.floorNames = request.floorNames;
      this.equipmentNames = request.equipmentNames;
      this.pointNames = request.pointNames;
      this.performExclusionOnNames = request.performExclusionOnNames;
      this.mappingExpression = request.mappingExpression;
      this.metricIdDelimiter = request.metricIdDelimiter;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withRawPoints(List<RawPointData> rawPoints) {
      requireNonNull(rawPoints, "rawPoints cannot be null");
      this.rawPoints = ImmutableList.copyOf(rawPoints);
      return this;
    }

    public Builder withBuildingName(String buildingName) {
      this.buildingName = buildingName;
      return this;
    }

    public Builder withSubBuildingNames(List<String> subBuildingNames) {
      requireNonNull(subBuildingNames, "subBuildingNames cannot be null");
      this.subBuildingNames = ImmutableList.copyOf(subBuildingNames);
      return this;
    }

    public Builder withPlantNames(List<String> plantNames) {
      requireNonNull(plantNames, "plantNames cannot be null");
      this.plantNames = ImmutableList.copyOf(plantNames);
      return this;
    }

    public Builder withFloorNames(List<String> floorNames) {
      requireNonNull(floorNames, "floorNames cannot be null");
      this.floorNames = ImmutableList.copyOf(floorNames);
      return this;
    }

    public Builder withEquipmentNames(List<String> equipmentNames) {
      requireNonNull(equipmentNames, "equipmentNames cannot be null");
      this.equipmentNames = ImmutableList.copyOf(equipmentNames);
      return this;
    }

    public Builder withPointNames(List<String> pointNames) {
      requireNonNull(pointNames, "pointNames cannot be null");
      this.pointNames = ImmutableList.copyOf(pointNames);
      return this;
    }
    
    public Builder withPerformExclusionOnNames(Boolean performExclusionOnNames) {
      requireNonNull(performExclusionOnNames, "performExclusionOnNames cannot be null");
      this.performExclusionOnNames = performExclusionOnNames;
      return this;
    }
    
    public Builder withMappingExpression(String mappingExpression) {
      requireNonNull(mappingExpression, "mappingExpression cannot be null");
      this.mappingExpression = mappingExpression;
      return this;
    }

    public Builder withMetricIdDelimiter(String metricIdDelimiter) {
      requireNonNull(metricIdDelimiter, "metricIdDelimiter cannot be null");
      this.metricIdDelimiter = metricIdDelimiter;
      return this;
    }
    
    @Override
    protected Builder getThis() {
      return this;
    }

    @Override
    protected MapRawPointsRequest newInstance() {
      
      if (rawPoints == null) {
        throw new IllegalArgumentException("The 'rawPoints' parameter must be specified");
      }
      if (Strings.isNullOrEmpty(mappingExpression)) {
        throw new IllegalArgumentException("The 'mappingExpression' parameter must be specified");
      }
      if (Strings.isNullOrEmpty(metricIdDelimiter)) {
        throw new IllegalArgumentException("The 'metricIdDelimiter' parameter must be specified");
      }
      return new MapRawPointsRequest(this);
    }
  }
}
//@formatter:on