
package com.djt.hvac.domain.model.rawpoint.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/*
  -- raw points 
  select 
    id, 
    component_id, 
    metric_id, 
    point_type, 
    "range", 
    unit_type, 
    "ignore", 
    deleted, 
    created_at 
  from 
    raw_points 
  where 
    customer_id = 4 
    and id in (select raw_point_id from mappable_points);
    
    
    -- To load all raw points, remove the last clause.
  
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "customer_id",
    "component_id",
    "metric_id",
    "point_type",
    "range",
    "unit_type",
    "ignore",
    "deleted",
    "created_at"
})
public class RawPointDto {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("customer_id")
    private Integer customerId;
    @JsonProperty("component_id")
    private Integer componentId;
    @JsonProperty("metric_id")
    private String metricId;
    @JsonProperty("point_type")
    private String pointType;
    @JsonProperty("range")
    private String range;
    @JsonProperty("unit_type")
    private String unitType;
    @JsonProperty("ignore")
    private Boolean ignore;
    @JsonProperty("deleted")
    private Boolean deleted;
    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("customer_id")
    public Integer getCustomerId() {
        return customerId;
    }

    @JsonProperty("customer_id")
    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }
    
    @JsonProperty("component_id")
    public Integer getComponentId() {
        return componentId;
    }

    @JsonProperty("component_id")
    public void setComponentId(Integer componentId) {
        this.componentId = componentId;
    }

    @JsonProperty("metric_id")
    public String getMetricId() {
        return metricId;
    }

    @JsonProperty("metric_id")
    public void setMetricId(String metricId) {
        this.metricId = metricId;
    }

    @JsonProperty("point_type")
    public String getPointType() {
        return pointType;
    }

    @JsonProperty("point_type")
    public void setPointType(String pointType) {
        this.pointType = pointType;
    }

    @JsonProperty("range")
    public String getRange() {
        return range;
    }

    @JsonProperty("range")
    public void setRange(String range) {
        this.range = range;
    }

    @JsonProperty("unit_type")
    public String getUnitType() {
        return unitType;
    }

    @JsonProperty("unit_type")
    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    @JsonProperty("ignore")
    public Boolean getIgnore() {
        return ignore;
    }

    @JsonProperty("ignore")
    public void setIgnore(Boolean ignore) {
        this.ignore = ignore;
    }

    @JsonProperty("deleted")
    public Boolean getDeleted() {
        return deleted;
    }

    @JsonProperty("deleted")
    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    @JsonProperty("created_at")
    public String getCreatedAt() {
        return createdAt;
    }

    @JsonProperty("created_at")
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
      
      return new StringBuilder()
          .append("RawPointDto [id=")
          .append(id)
          .append(", componentId=")
          .append(componentId)
          .append(", metricId=")
          .append(metricId)
          .append(", pointType=")
          .append(pointType)
          .append(", range=")
          .append(range)
          .append(", unitType=")
          .append(unitType)
          .append(", ignore=")
          .append(ignore)
          .append(", deleted=")
          .append(deleted)
          .append(", createdAt=")
          .append(createdAt)
          .append("]").toString();
  }
}
