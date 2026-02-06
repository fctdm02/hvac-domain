package com.djt.hvac.domain.model.nodehierarchy.dto;

import com.djt.hvac.domain.model.rawpoint.dto.RawPointDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

  /*
-- mappable point nodes
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
  p.raw_point_id AS point_raw_point_id,
  ppt.node_template_id AS point_point_template_id,
  p.is_cov, 
  cv.value AS point_value,
  cv.value_timestamp AS value_timestamp
FROM 
  nodes n
  JOIN mappable_points p ON n.id = p.id
  JOIN (select id, node_type_id from nodes) AS n2 on n.parent_id = n2.id 
  LEFT OUTER JOIN point_point_templates ppt ON ppt.node_id = p.id
  LEFT OUTER JOIN current_values cv ON cv.raw_point_id = p.raw_point_id
WHERE 
  n.customer_id = 4;
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "node_id",
    "node_name",
    "node_display_name",
    "node_parent_id",
    "node_parent_node_type_id",
    "node_created_at",
    "point_metric_id",
    "point_unit_type",
    "point_unit_id",
    "point_data_type_id",
    "point_range",
    "point_raw_point_id",
    "point_point_template_id",
    "is_cov",
    "point_value",
    "value_timestamp"
})
public class MappablePointNodeDto {

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
    @JsonProperty("point_metric_id")
    private String pointMetricId;
    @JsonProperty("point_unit_type")
    private String pointUnitType;
    @JsonProperty("point_unit_id")
    private Integer pointUnitId;
    @JsonProperty("point_data_type_id")
    private Integer pointDataTypeId;
    @JsonProperty("point_range")
    private String pointRange;
    @JsonProperty("point_raw_point_id")
    private Integer pointRawPointId;
    @JsonProperty("point_point_template_id")
    private Integer pointPointTemplateId;
    @JsonProperty("is_cov")
    private Boolean cov;
    @JsonProperty("point_value")
    private String value;
    @JsonProperty("value_timestamp")
    private Long valueTimestamp;
    @JsonProperty("rawPointDto")
    private RawPointDto rawPointDto;

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

    @JsonProperty("point_metric_id")
    public String getPointMetricId() {
        return pointMetricId;
    }

    @JsonProperty("point_metric_id")
    public void setPointMetricId(String pointMetricId) {
        this.pointMetricId = pointMetricId;
    }

    @JsonProperty("point_unit_type")
    public String getPointUnitType() {
        return pointUnitType;
    }

    @JsonProperty("point_unit_type")
    public void setPointUnitType(String pointUnitType) {
        this.pointUnitType = pointUnitType;
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

    @JsonProperty("point_raw_point_id")
    public Integer getPointRawPointId() {
        return pointRawPointId;
    }

    @JsonProperty("point_raw_point_id")
    public void setPointRawPointId(Integer pointRawPointId) {
        this.pointRawPointId = pointRawPointId;
    }

    @JsonProperty("point_point_template_id")
    public Integer getPointPointTemplateId() {
        return pointPointTemplateId;
    }

    @JsonProperty("point_point_template_id")
    public void setPointPointTemplateId(Integer pointPointTemplateId) {
        this.pointPointTemplateId = pointPointTemplateId;
    }

    @JsonProperty("cov")
    public Boolean getCov() {
        return cov;
    }

    @JsonProperty("cov")
    public void setCov(Boolean cov) {
        this.cov = cov;
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
    
    @JsonProperty("rawPointDto")
    public RawPointDto getRawPointDto() {
        return rawPointDto;
    }

    @JsonProperty("rawPointDto")
    public void setRawPointDto(RawPointDto rawPointDto) {
        this.rawPointDto = rawPointDto;
    }    
    
    public String toString() {
      
      return new StringBuilder()
          .append("MappablePointNodeDto [nodeId=")
          .append(nodeId)
          .append(", nodeName=")
          .append(nodeName)
          .append(", nodeDisplayName=")
          .append(nodeDisplayName)
          .append(", nodeParentId=")
          .append(nodeParentId)
          .append(", nodeParentNodeTypeId=")
          .append(nodeParentNodeTypeId)
          .append(", pointDataTypeId=")
          .append(pointDataTypeId)
          .append(", pointRange=")
          .append(pointRange)
          .append(", pointRawPointId=")
          .append(pointRawPointId)
          .append(", pointPointTemplateId=")
          .append(pointPointTemplateId)
          .append(", cov=")
          .append(cov)
          .append(", value=")
          .append(value)
          .append(", valueTimestamp=")
          .append(valueTimestamp)
          .append(", rawPointDto=")
          .append(rawPointDto)
          .append("]")
          .toString();
    }
}