
package com.djt.hvac.domain.model.nodehierarchy.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/*
-- Async Computed Points of Interest (Everything but "system" points)
SELECT 
  p.id AS node_id, 
  p."name" AS node_name, 
  p.display_name as node_display_name, 
  p.created_at AS created_at, 
  p.updated_at AS updated_at, 
  p.parent_id AS node_parent_id, 
  p.metric_id, 
  ppt.node_template_id AS point_point_template_id, 
  p.unit_id AS point_unit_id, 
  p.subtype AS subtype, 
  p.range AS point_range, 
  p.configurable AS configurable, 
  p.timezone_based_rollups AS timezone_based_rollups, 
  p.global_computed_point_id AS global_computed_point_id, 
  p.data_type_id AS point_data_type_id 
FROM 
  async_computed_points p 
  LEFT OUTER JOIN point_point_templates ppt ON ppt.node_id = p.id 
  left outer join ad_function_instance_output_points op on p.id = op.point_id 
  left outer join ad_function_instances fi on op.ad_function_instance_id = fi.id 
WHERE 
  p.customer_id = 10 
  and (fi.active is null or fi.active = true) 
  and p."name" not like '%Rule Aggregate%' 
  and p."name" not like '%Rollup%' 
  and p."name" not like '%ZoneTempMaxDev' 
  and p."name" not like '%ZoneTempAvg' 
  and p."name" not like '%ZoneTempMin' 
  and p."name" not like '%ZoneTempMax' 
  and p."name" not like '%Savings%';
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "node_id",
    "node_name",
    "node_display_name",
    "node_parent_id",
    "metric_id",
    "point_point_template_id",
    "point_value",
    "value_timestamp",
    "point_unit_id",
    "point_data_type_id",
    "point_range",
    "subtype",
    "configurable",
    "global_computed_point_id",
    "created_at",
    "updated_at"
})
public class AsyncComputedPointNodeDto {

  // Non-System
  public static final String CUSTOM = "CUSTOM";
  public static final String COMPUTED = "COMPUTED";
  public static final String RULE = "RULE";
  public static final String SCHEDULED = "SCHEDULED";
  public static final String WEATHER_STATION = "WEATHER_STATION";
  public static final String MANUAL = "MANUAL";
  
  // System
  public static final String SYSTEM = "SYSTEM";
  
    @JsonProperty("node_id")
    private Integer nodeId;
    @JsonProperty("node_name")
    private String nodeName;
    @JsonProperty("node_display_name")
    private String nodeDisplayName;
    @JsonProperty("node_parent_id")
    private Integer nodeParentId;
    @JsonProperty("metric_id")
    private String metricId;
    @JsonProperty("point_point_template_id")
    private Integer pointPointTemplateId;
    @JsonProperty("point_value")
    private String value;
    @JsonProperty("value_timestamp")
    private Long valueTimestamp;
    @JsonProperty("point_unit_id")
    private Integer pointUnitId;
    @JsonProperty("point_data_type_id")
    private Integer pointDataTypeId;
    @JsonProperty("point_range")
    private String pointRange;
    @JsonProperty("subtype")
    private String subtype;
    @JsonProperty("configurable")
    private Boolean configurable;
    @JsonProperty("timezone_based_rollups")
    private Boolean timezoneBasedRollups;
    @JsonProperty("global_computed_point_id")
    private Integer globalComputedPointId;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("updated_at")
    private String updatedAt;
    
    @JsonProperty("node_id")
    public Integer getNodeId() {
        return nodeId;
    }

    @JsonProperty("node_id")
    public void setNodeId(Integer nodeId) {
        this.nodeId = nodeId;
    }

    @JsonProperty("node_name")
    public String getNodeName() {
        return nodeName;
    }

    @JsonProperty("node_name")
    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    @JsonProperty("node_display_name")
    public String getNodeDisplayName() {
        return nodeDisplayName;
    }

    @JsonProperty("node_display_name")
    public void setNodeDisplayName(String nodeDisplayName) {
        this.nodeDisplayName = nodeDisplayName;
    }
    
    @JsonProperty("node_parent_id")
    public Integer getNodeParentId() {
        return nodeParentId;
    }

    @JsonProperty("node_parent_id")
    public void setNodeParentId(Integer nodeParentId) {
        this.nodeParentId = nodeParentId;
    }

    @JsonProperty("metric_id")
    public String getMetricId() {
        return metricId;
    }

    @JsonProperty("metric_id")
    public void setMetricId(String metricId) {
        this.metricId = metricId;
    }

    @JsonProperty("point_point_template_id")
    public Integer getPointPointTemplateId() {
        return pointPointTemplateId;
    }

    @JsonProperty("point_point_template_id")
    public void setPointPointTemplateId(Integer pointPointTemplateId) {
        this.pointPointTemplateId = pointPointTemplateId;
    }

    @JsonProperty("point_value")
    public String getValue() {
        return value;
    }

    @JsonProperty("point_value")
    public void setValue(String value) {
        this.value = value;
    }    
    
    @JsonProperty("value_timestamp")
    public Long getValueTimestamp() {
        return valueTimestamp;
    }

    @JsonProperty("value_timestamp")
    public void setValueTimestamp(Long valueTimestamp) {
        this.valueTimestamp = valueTimestamp;
    }
    
    @JsonProperty("point_unit_id")
    public Integer getPointUnitId() {
        return pointUnitId;
    }

    @JsonProperty("point_unit_id")
    public void setPointUnitId(Integer pointUnitId) {
        this.pointUnitId = pointUnitId;
    }

    @JsonProperty("point_data_type_id")
    public Integer getPointDataTypeId() {
        return pointDataTypeId;
    }

    @JsonProperty("point_data_type_id")
    public void setPointDataTypeId(Integer pointDataTypeId) {
        this.pointDataTypeId = pointDataTypeId;
    }

    @JsonProperty("point_range")
    public String getPointRange() {
        return pointRange;
    }

    @JsonProperty("point_range")
    public void setPointRange(String pointRange) {
        this.pointRange = pointRange;
    }
    
    @JsonProperty("subtype")
    public String getSubtype() {
        return subtype;
    }

    @JsonProperty("subtype")
    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }    
    
    @JsonProperty("configurable")
    public Boolean getConfigurable() {
        return configurable;
    }

    @JsonProperty("configurable")
    public void setConfigurable(Boolean configurable) {
        this.configurable = configurable;
    }

    @JsonProperty("timezone_based_rollups")
    public Boolean getTimezoneBasedRollups() {
        return timezoneBasedRollups;
    }

    @JsonProperty("timezone_based_rollups")
    public void setTimezoneBasedRollups(Boolean timezoneBasedRollups) {
        this.timezoneBasedRollups = timezoneBasedRollups;
    }
    
    @JsonProperty("global_computed_point_id")
    public Integer getGlobalComputedPointId() {
        return globalComputedPointId;
    }

    @JsonProperty("global_computed_point_id")
    public void setGlobalComputedPointId(Integer globalComputedPointId) {
        this.globalComputedPointId = globalComputedPointId;
    }      
    
    @JsonProperty("created_at")
    public String getCreatedAt() {
        return createdAt;
    }

    @JsonProperty("created_at")
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    @JsonProperty("updated_at")
    public String getUpdatedAt() {
        return updatedAt;
    }

    @JsonProperty("updated_at")
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
      
      return new StringBuilder()
          .append("AsyncComputedPointNodeDto [nodeId=")
          .append(nodeId)
          .append(", nodeName=")
          .append(nodeName)
          .append(", nodeParentId=")
          .append(nodeParentId)
          .append(", metricId=")
          .append(metricId)
          .append(", pointPointTemplateId=")
          .append(pointPointTemplateId)
          .append(", value=")
          .append(value)
          .append(", valueTimestamp=")
          .append(valueTimestamp)
          .append(", point_range=")
          .append(pointRange)
          .append(", subtype=")
          .append(subtype)
          .append(", configurable=")
          .append(configurable)
          .append(", timezoneBasedRollups=")
          .append(timezoneBasedRollups)
          .append(", globalComputedPointId=")
          .append(globalComputedPointId)
          .append(", createdAt=")
          .append(createdAt)
          .append(", updatedAt=")
          .append(updatedAt)
          .append("]")
          .toString();
    }
}
