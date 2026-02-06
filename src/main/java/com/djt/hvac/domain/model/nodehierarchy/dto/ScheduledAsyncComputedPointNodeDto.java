package com.djt.hvac.domain.model.nodehierarchy.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/*
  -- scheduled async computed points
  SELECT  
    n.id AS node_id,
    n."name" AS node_name,
    n.display_name AS node_display_name,
    n.parent_id AS node_parent_id,
    n2.node_type_id AS node_parent_node_type_id, 
    n.created_at AS node_created_at,
    p.unit_id AS point_unit_id,
    p.data_type_id AS point_data_type_id,
    p."range" AS point_range,
    p."metric_id AS point_metric_id,
    p.configurable,
    p.timezone_based_rollups,
    p.global_computed_point_id,
    p.scheduled_event_type_id,
    ppt.node_template_id AS point_point_template_id,
    p.value AS point_value,
    p.value_timestamp AS value_timestamp
  FROM  
    nodes n
    JOIN scheduled_async_computed_points p ON n.id = p.id
    JOIN (SELECT id, node_type_id FROM nodes) AS n2 ON n.parent_id = n2.id
    LEFT OUTER JOIN point_point_templates ppt ON ppt.node_id = p.id     
  WHERE  
    n.customer_id = 4; 
    
    TODO: Remove configurable, timezone_based_rollups and global_computed_point_id
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "node_id",
    "node_name",
    "node_display_name",
    "node_parent_id",
    "node_parent_node_type_id",
    "node_created_at",
    "point_unit_id",
    "point_data_type_id",
    "point_range",
    "point_point_template_id",
    "point_metric_id",
    "point_value",
    "value_timestamp",
    "configurable",
    "timezone_based_rollups",
    "global_computed_point_id",
    "scheduled_event_type_id"
})
public class ScheduledAsyncComputedPointNodeDto {

    @JsonProperty("node_id")
    private Integer nodeId;
    @JsonProperty("node_name")
    private String nodeName;
    @JsonProperty("node_display_name")
    private String nodeDisplayName;
    @JsonProperty("node_parent_id")
    private Integer nodeParentId;
    @JsonProperty("node_parent_node_type_id")
    private Integer nodeParentNodeTypeId;
    @JsonProperty("node_created_at")
    private String nodeCreatedAt;
    @JsonProperty("point_unit_id")
    private Integer pointUnitId;
    @JsonProperty("point_data_type_id")
    private Integer pointDataTypeId;
    @JsonProperty("point_range")
    private String pointRange;
    @JsonProperty("point_point_template_id")
    private Integer pointPointTemplateId;
    @JsonProperty("point_metric_id")
    private String pointMetricId;
    @JsonProperty("point_value")
    private String value;
    @JsonProperty("value_timestamp")
    private Long valueTimestamp;
    @JsonProperty("configurable")
    private Boolean configurable;
    @JsonProperty("timezone_based_rollups")
    private Boolean timezoneBasedRollups;
    @JsonProperty("global_computed_point_id")
    private Integer globalComputedPointId;
    @JsonProperty("scheduled_event_type_id")
    private Integer scheduledEventTypeId;

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

    @JsonProperty("node_parent_node_type_id")
    public Integer getNodeParentNodeTypeId() {
        return nodeParentNodeTypeId;
    }

    @JsonProperty("node_parent_node_type_id")
    public void setNodeParentNodeTypeId(Integer nodeParentNodeTypeId) {
        this.nodeParentNodeTypeId = nodeParentNodeTypeId;
    }

    @JsonProperty("node_created_at")
    public String getNodeCreatedAt() {
        return nodeCreatedAt;
    }

    @JsonProperty("node_created_at")
    public void setNodeCreatedAt(String nodeCreatedAt) {
        this.nodeCreatedAt = nodeCreatedAt;
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

    @JsonProperty("point_point_template_id")
    public Integer getPointPointTemplateId() {
        return pointPointTemplateId;
    }

    @JsonProperty("point_point_template_id")
    public void setPointPointTemplateId(Integer pointPointTemplateId) {
        this.pointPointTemplateId = pointPointTemplateId;
    }
    
    @JsonProperty("point_metric_id")
    public String getPointMetricId() {
        return pointMetricId;
    }

    @JsonProperty("point_metric_id")
    public void setPointMetricId(String pointMetricId) {
        this.pointMetricId = pointMetricId;
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

    @JsonProperty("scheduled_event_type_id")
    public Integer getScheduledEventTypeId() {
        return scheduledEventTypeId;
    }

    @JsonProperty("scheduled_event_type_id")
    public void setScheduledEventTypeId(Integer scheduledEventTypeId) {
        this.scheduledEventTypeId = scheduledEventTypeId;
    }

    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("ScheduledAsyncComputedPointNodeDto [nodeId=").append(nodeId)
          .append(", nodeName=").append(nodeName).append(", nodeDisplayName=")
          .append(nodeDisplayName).append(", nodeParentId=").append(nodeParentId)
          .append(", nodeParentNodeTypeId=").append(nodeParentNodeTypeId).append(", nodeCreatedAt=")
          .append(nodeCreatedAt).append(", pointUnitId=").append(pointUnitId)
          .append(", pointDataTypeId=").append(pointDataTypeId).append(", pointRange=")
          .append(pointRange).append(", pointPointTemplateId=").append(pointPointTemplateId)
          .append(", pointMetricId=").append(pointMetricId).append(", value=")
          .append(value).append(", valueTimestamp=").append(valueTimestamp)
          .append(", configurable=").append(configurable).append(", timezoneBasedRollups=")
          .append(timezoneBasedRollups).append(", globalComputedPointId=")
          .append(globalComputedPointId).append(", scheduledEventTypeId=")
          .append(scheduledEventTypeId).append("]");
      return builder.toString();
    }
}
