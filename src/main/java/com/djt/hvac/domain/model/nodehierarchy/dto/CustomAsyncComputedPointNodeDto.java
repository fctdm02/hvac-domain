
package com.djt.hvac.domain.model.nodehierarchy.dto;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/*
 <pre>
-- CUSTOM ASYNC COMPUTED POINTS
SELECT
  acp.id,
  acp."name",
  acp.display_name,
  acp.parent_id,
  n2.node_type_id AS parent_node_type_id,
  acp.created_at,
  acp.updated_at,
  acp.unit_id,
  acp.value,
  acp.value_timestamp,
  acp.configurable,
  acp.timezone_based_rollups,
  acp.metric_id,
  ppt.node_template_id AS point_template_id,
  acpc.computation_interval,
  tacpc.id AS temporal_config_id,
  tacpc.effective_date,
  tacpc.formula,
  tacpc.description,
  ARRAY_AGG(tacpv.point_id) AS variable_point_id,
  ARRAY_AGG(tacpv.fill_policy_id) AS variable_fill_policy_id,
  ARRAY_AGG(tacpv.variable_name) AS variable_name
FROM 
  async_computed_points acp
  JOIN (SELECT id, node_type_id FROM nodes) AS n2 ON acp.parent_id = n2.id
  JOIN async_computed_point_configs acpc ON acp.id = acpc.id
  JOIN temporal_async_computed_point_configs tacpc ON acpc.id = tacpc.async_computed_point_config_id
  JOIN temporal_async_computed_point_vars tacpv ON tacpc.id = tacpv.temporal_async_computed_point_config_id
  LEFT OUTER JOIN point_point_templates ppt ON ppt.node_id = acp.id
WHERE
  acp.customer_id = 9
  AND acp.subtype = 'CUSTOM'
GROUP BY 
  acp.customer_id,
  acp.id,
  acp.name,
  acp.display_name,
  acp.parent_id,
  n2.node_type_id,
  acp.created_at,
  acp.updated_at,
  acp.unit_id,
  acp.value,
  acp.value_timestamp,
  acp.configurable,
  acp.timezone_based_rollups,
  acp.metric_id,
  ppt.node_template_id,
  acpc.computation_interval,
  tacpc.id,
  tacpc.effective_date,
  tacpc.formula,
  tacpc.description,
  acp.subtype;
  </pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "name",
    "display_name",
    "parent_id",
    "parent_node_type_id",
    "created_at",
    "updated_at",
    "unit_id",
    "value",
    "value_timestamp",
    "configurable",
    "timezone_based_rollups",
    "metric_id",
    "point_template_id",
    "computation_interval",
    "temporal_config_id",
    "effective_date",
    "formula",
    "description",
    "variable_point_id",
    "variable_fill_policy_id",
    "variable_name"
})
public class CustomAsyncComputedPointNodeDto implements Serializable
{

    @JsonProperty("id")
    private Integer id;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("display_name")
    private String displayName;
    
    @JsonProperty("parent_id")
    private Integer parentId;
    
    @JsonProperty("parent_node_type_id")
    private Integer parentNodeTypeId;
    
    @JsonProperty("created_at")
    private String createdAt;
    
    @JsonProperty("updated_at")
    private String updatedAt;
    
    @JsonProperty("unit_id")
    private Integer unitId;
    
    @JsonProperty("value")
    private String value;
    
    @JsonProperty("value_timestamp")
    private Long valueTimestamp;
    
    @JsonProperty("configurable")
    private Boolean configurable;
    
    @JsonProperty("timezone_based_rollups")
    private Boolean timezoneBasedRollups;
    
    @JsonProperty("metric_id")
    private String metricId;
    
    @JsonProperty("point_template_id")
    private Integer pointTemplateId;

    @JsonProperty("temporal_config_id")
    private Integer temporalConfigId;
    
    @JsonProperty("computation_interval")
    private String computationInterval;
    
    @JsonProperty("effective_date")
    private String effectiveDate;
    
    @JsonProperty("formula")
    private String formula;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("variable_point_id")
    private String variablePointId;
    
    @JsonProperty("variable_fill_policy_id")
    private String variableFillPolicyId;
    
    @JsonProperty("variable_name")
    private String variableName;
    
    private final static long serialVersionUID = 1587023438366182806L;

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("display_name")
    public String getDisplayName() {
        return displayName;
    }

    @JsonProperty("display_name")
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @JsonProperty("parent_id")
    public Integer getParentId() {
        return parentId;
    }

    @JsonProperty("parent_id")
    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    @JsonProperty("parent_node_type_id")
    public Integer getParentNodeTypeId() {
        return parentNodeTypeId;
    }

    @JsonProperty("parent_node_type_id")
    public void setParentNodeTypeId(Integer parentNodeTypeId) {
        this.parentNodeTypeId = parentNodeTypeId;
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

    @JsonProperty("unit_id")
    public Integer getUnitId() {
        return unitId;
    }

    @JsonProperty("unit_id")
    public void setUnitId(Integer unitId) {
        this.unitId = unitId;
    }

    @JsonProperty("value")
    public String getValue() {
        return value;
    }

    @JsonProperty("value")
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
    
    @JsonProperty("metric_id")
    public String getMetricId() {
        return metricId;
    }

    @JsonProperty("metric_id")
    public void setMetricId(String metricId) {
        this.metricId = metricId;
    }

    @JsonProperty("point_template_id")
    public Integer getPointTemplateId() {
        return pointTemplateId;
    }

    @JsonProperty("point_template_id")
    public void setPointTemplateId(Integer pointTemplateId) {
        this.pointTemplateId = pointTemplateId;
    }
    
    @JsonProperty("temporal_config_id")
    public Integer getTemporalConfigId() {
        return temporalConfigId;
    }

    @JsonProperty("temporal_config_id")
    public void setTemporalConfigId(Integer temporalConfigId) {
        this.temporalConfigId = temporalConfigId;
    }

    @JsonProperty("computation_interval")
    public String getComputationInterval() {
        return computationInterval;
    }

    @JsonProperty("computation_interval")
    public void setComputationInterval(String computationInterval) {
        this.computationInterval = computationInterval;
    }

    @JsonProperty("effective_date")
    public String getEffectiveDate() {
        return effectiveDate;
    }

    @JsonProperty("effective_date")
    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    @JsonProperty("formula")
    public String getFormula() {
        return formula;
    }

    @JsonProperty("formula")
    public void setFormula(String formula) {
        this.formula = formula;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("variable_point_id")
    public String getVariablePointId() {
        return variablePointId;
    }

    @JsonProperty("variable_point_id")
    public void setVariablePointId(String variablePointId) {
        this.variablePointId = variablePointId;
    }

    @JsonProperty("variable_fill_policy_id")
    public String getVariableFillPolicyId() {
        return variableFillPolicyId;
    }

    @JsonProperty("variable_fill_policy_id")
    public void setVariableFillPolicyId(String variableFillPolicyId) {
        this.variableFillPolicyId = variableFillPolicyId;
    }

    @JsonProperty("variable_name")
    public String getVariableName() {
        return variableName;
    }

    @JsonProperty("variable_name")
    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }
}