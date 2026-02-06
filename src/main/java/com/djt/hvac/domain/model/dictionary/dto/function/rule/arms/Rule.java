
package com.djt.hvac.domain.model.dictionary.dto.function.rule.arms;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/*
<pre>
-- ARMS RULES
SELECT 
 rt.id,
 json_build_object(
   'id', rt.id,
   'number', rt."number", 
   'name', rt.name, 
   'summary', rt.summary,
   'expression', rt.expression,
   'tuple_constraint', rt.tuple_constraint,
   'node_filter_expression', rt.node_filter_expression,
   'notes', rt.notes,
   'aasm_state', rt.aasm_state,
   'jira_link', rt.jira_link,
   'equipment_type_id', ARRAY_AGG(DISTINCT rtt.equipment_type_tag_id),
   'input_points', array_agg(DISTINCT input_points.json),
   'input_consts', array_agg(DISTINCT input_consts.json)
 )::jsonb AS json
FROM 
 rules rt 
 JOIN equipment_type_tags rtt ON rtt.rule_id = rt.id 
 JOIN (
   SELECT rule_id, json_build_object(
       'id', rti.id,
       'point_template_id', point_template_id,
       'label', label, 
       'current_object_expression', current_object,
       'required', is_required,
       'array', is_array
     )::jsonb AS json
   FROM 
     inputs rti 
 ) AS input_points ON rt.id = input_points.rule_id
 LEFT OUTER JOIN (
   SELECT rule_id, json_build_object(
       'id', id,
       'label', "label", 
       'name', name,
       'default_value', default_value,
       'required', required      
     )::jsonb AS json
   FROM 
     constants
 ) AS input_consts ON rt.id = input_consts.rule_id
WHERE 
 rt.aasm_state = 'done'  
GROUP BY 
 rt.id,
 rt."number",
 rt.name,
 rt.summary,
 rt."expression",
 rt.tuple_constraint,
 rt.node_filter_expression,
 rt.root_cause, 
 rt.notes, 
 rt.aasm_state,
 rt.jira_link; 
</pre>
*/
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "name",
    "notes",
    "number",
    "summary",
    "jira_link",
    "aasm_state",
    "expression",
    "tuple_constraint",
    "node_filter_expression",
    "input_consts",
    "input_points",
    "equipment_type_id"
})
public class Rule implements Serializable {

  @JsonProperty("id")
  private Integer id;
  @JsonProperty("name")
  private String name;
  @JsonProperty("notes")
  private String notes;
  @JsonProperty("number")
  private String number;
  @JsonProperty("summary")
  private String summary;
  @JsonProperty("jira_link")
  private String jiraLink;
  @JsonProperty("aasm_state")
  private String aasmState;
  @JsonProperty("expression")
  private String expression;
  @JsonProperty("tuple_constraint")
  private String tupleConstraint;
  @JsonProperty("node_filter_expression")
  private String nodeFilterExpression;
  @JsonProperty("input_consts")
  private List<InputConst> inputConsts = null;
  @JsonProperty("input_points")
  private List<InputPoint> inputPoints = null;
  @JsonProperty("equipment_type_id")
  private List<Integer> equipmentTypeId = null;
  @JsonIgnore
  private Map<String, Object> additionalProperties = new HashMap<String, Object>();
  private final static long serialVersionUID = 527860275260083623L;

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
    this.name = name.trim();
  }

  @JsonProperty("notes")
  public String getNotes() {
    return notes;
  }

  @JsonProperty("notes")
  public void setNotes(String notes) {
    this.notes = notes;
  }

  @JsonProperty("number")
  public String getNumber() {
    return number;
  }

  @JsonProperty("number")
  public void setNumber(String number) {
    this.number = number.trim();
  }

  @JsonProperty("summary")
  public String getSummary() {
    return summary;
  }

  @JsonProperty("summary")
  public void setSummary(String summary) {
    this.summary = summary.trim();
  }

  @JsonProperty("jira_link")
  public String getJiraLink() {
    return jiraLink;
  }

  @JsonProperty("jira_link")
  public void setJiraLink(String jiraLink) {
    this.jiraLink = jiraLink;
  }

  @JsonProperty("aasm_state")
  public String getAasmState() {
    return aasmState;
  }

  @JsonProperty("aasm_state")
  public void setAasmState(String aasmState) {
    this.aasmState = aasmState;
  }

  @JsonProperty("expression")
  public String getExpression() {
    return expression;
  }

  @JsonProperty("expression")
  public void setExpression(String expression) {
    this.expression = expression;
  }

  @JsonProperty("tuple_constraint")
  public String getTupleConstraint() {
    return tupleConstraint;
  }

  @JsonProperty("tuple_constraint")
  public void setTupleConstraint(String tupleConstraint) {
    this.tupleConstraint = tupleConstraint;
  }
  
  @JsonProperty("node_filter_expression")
  public String getNodeFilterExpression() {
    return nodeFilterExpression;
  }

  @JsonProperty("node_filter_expression")
  public void setNodeFilterExpression(String nodeFilterExpression) {
    this.nodeFilterExpression = nodeFilterExpression;
  }
  
  @JsonProperty("input_consts")
  public List<InputConst> getInputConsts() {
    return inputConsts;
  }

  @JsonProperty("input_consts")
  public void setInputConsts(List<InputConst> inputConsts) {
    this.inputConsts = inputConsts;
  }

  @JsonProperty("input_points")
  public List<InputPoint> getInputPoints() {
    return inputPoints;
  }

  @JsonProperty("input_points")
  public void setInputPoints(List<InputPoint> inputPoints) {
    this.inputPoints = inputPoints;
  }

  @JsonProperty("equipment_type_id")
  public List<Integer> getEquipmentTypeId() {
    return equipmentTypeId;
  }

  @JsonProperty("equipment_type_id")
  public void setEquipmentTypeId(List<Integer> equipmentTypeId) {
    this.equipmentTypeId = equipmentTypeId;
  }

  @JsonAnyGetter
  public Map<String, Object> getAdditionalProperties() {
    return this.additionalProperties;
  }

  @JsonAnySetter
  public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }
}
