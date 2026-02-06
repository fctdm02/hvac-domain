
package com.djt.hvac.domain.model.dictionary.dto.function;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 *
 <pre>
 -- AD Function Templates 
  SELECT 
  rt.id,
  json_build_object(
    'id', rt.id,
    'function_code_module_id', function_code_module_id, 
    'function_code_module_name', fcm.name, 
    'function_code_module_description', fcm.description,
    'function_type_id', fcm.function_type_id,
    'equipment_type_id', ARRAY_AGG(DISTINCT rtt.tag_id),
    'name', rt.name,
    'description', rt.description,
    'active', active,
    'display_name', display_name,
    'reference_number', reference_number,
    'node_filter_expression', node_filter_expression,
    'beta', beta,
    'tuple_constraint', tuple_constraint,
    'tuple_constraint_error_message', tuple_constraint_error_message,
    'input_points', array_agg(DISTINCT input_points.json),
    'input_consts', array_agg(DISTINCT input_consts.json),
    'output_points', array_agg(DISTINCT output_points.json)
  )::jsonb AS json
  FROM 
    ad_function_templates rt 
    JOIN ad_function_code_modules fcm ON rt.function_code_module_id = fcm.id
    JOIN ad_function_template_tags rtt ON rtt.ad_function_template_id = rt.id 
  JOIN (
    SELECT ad_function_template_id, json_build_object(
      'id', rti.id,
      'seq_no', seq_no,
      'name', name, 
      'description', description,
      'current_object_expression', current_object_expression,
      'required', is_required,
      'array', is_array,
      'tags', ARRAY_AGG(rtit.tag_name)
    )::jsonb AS json
    FROM 
      ad_function_template_input_points rti 
      JOIN ad_rule_template_input_point_tags rtit ON rti.id = rtit.ad_rule_template_input_point_id
    GROUP BY 
      ad_function_template_id,
      rti.id,
      rti.seq_no,
      rti.name, 
      rti.description,
      rti.current_object_expression,
      rti.is_required, 
      rti.is_array
  ) AS input_points ON rt.id = input_points.ad_function_template_id
  LEFT OUTER JOIN (
    SELECT ad_function_template_id, json_build_object(
      'id', id,
      'seq_no', seq_no,
      'name', name, 
      'description', description,
      'is_required', is_required,
      'data_type_id', data_type_id,
      'unit_id', unit_id,
      'default_value', default_value
    )::jsonb AS json
    FROM ad_function_template_input_consts
  ) AS input_consts ON rt.id = input_consts.ad_function_template_id
  JOIN (
    SELECT ad_function_template_id, json_build_object(
      'id', id,
      'seq_no', seq_no,
      'description', description, 
      'data_type_id', data_type_id,
      'unit_id', unit_id,
      'range', "range",
      'tags', ARRAY_AGG(rtot.tag_name)
    )::jsonb AS json
    FROM 
      ad_function_template_output_points rto
      LEFT OUTER JOIN ad_rule_template_output_point_tags rtot ON rto.id = rtot.ad_rule_template_output_point_id
    GROUP BY 
      rto.ad_function_template_id,
      rto.id,
      rto.seq_no,
      rto.description,
      rto.data_type_id,
      rto.unit_id,
      rto."range"
  ) AS output_points ON rt.id = output_points.ad_function_template_id
  GROUP BY 
  rt.id,
  rt.function_code_module_id,
  fcm.function_type_id,
  fcm.name,
  fcm.description,
  rt.name,
  rt.description,
  rt.active,
  rt.display_name,
  rt.reference_number,
  rt.node_filter_expression,
  rt.beta,
  rt.tuple_constraint,
  rt.tuple_constraint_error_message; 
  </pre>
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "function_code_module_id",
    "function_code_module_name",
    "function_code_module_description",
    "function_type_id",
    "name",
    "display_name",
    "description",
    "reference_number",
    "equipment_type_id",
    "node_filter_expression",
    "tuple_constraint",
    "tuple_constraint_error_message",
    "beta",
    "active",
    "input_consts",
    "input_points",
    "output_points",
    "version"
})
public class AdFunctionTemplateDto implements Comparable<AdFunctionTemplateDto> {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("function_code_module_id")
    private Integer functionCodeModuleId;
    @JsonProperty("function_code_module_name")
    private String functionCodeModuleName;
    @JsonProperty("function_code_module_description")
    private String functionCodeModuleDescription;
    @JsonProperty("function_type_id")
    private Integer functionTypeId;
    @JsonProperty("beta")
    private Boolean beta;
    @JsonProperty("name")
    private String name;
    @JsonProperty("active")
    private Boolean active;
    @JsonProperty("description")
    private String description;
    @JsonProperty("display_name")
    private String displayName;
    @JsonProperty("input_consts")
    private List<AdFunctionTemplateInputConstantDto> adFunctionTemplateInputConstantDtos = null;
    @JsonProperty("input_points")
    private List<AdFunctionTemplateInputPointDto> adFunctionTemplateInputPointDtos = null;
    @JsonProperty("output_points")
    private List<AdFunctionTemplateOutputPointDto> adFunctionTemplateOutputPointDtos = null;
    @JsonProperty("reference_number")
    private String referenceNumber;
    @JsonProperty("tuple_constraint")
    private String tupleConstraint;
    @JsonProperty("equipment_type_id")
    private List<Integer> equipmentTypeId = null;
    @JsonProperty("node_filter_expression")
    private String nodeFilterExpression;
    @JsonProperty("tuple_constraint_error_message")
    private String tupleConstraintErrorMessage;
    @JsonProperty("version")
    private Integer version = Integer.valueOf(1);

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }
    
    @JsonProperty("function_code_module_id")
    public Integer getFunctionCodeModuleId() {
        return functionCodeModuleId;
    }

    @JsonProperty("function_code_module_id")
    public void setFunctionCodeModuleId(Integer functionCodeModuleId) {
        this.functionCodeModuleId = functionCodeModuleId;
    }
    
    @JsonProperty("function_code_module_name")
    public String getFunctionCodeModuleName() {
        return functionCodeModuleName;
    }

    @JsonProperty("function_code_module_name")
    public void setFunctionCodeModuleName(String functionCodeModuleName) {
        this.functionCodeModuleName = functionCodeModuleName;
    }
    
    @JsonProperty("function_code_module_description")
    public String getFunctionCodeModulDescription() {
        return functionCodeModuleDescription;
    }

    @JsonProperty("function_code_module_description")
    public void setFunctionCodeModulDescription(String functionCodeModuleDescription) {
        this.functionCodeModuleDescription = functionCodeModuleDescription;
    }
    
    @JsonProperty("function_type_id")
    public Integer getFunctionTypeId() {
        return functionTypeId;
    }

    @JsonProperty("function_type_id")
    public void setFunctionTypeId(Integer functionTypeId) {
        this.functionTypeId = functionTypeId;
    }
    
    @JsonProperty("beta")
    public Boolean getBeta() {
        return beta;
    }

    @JsonProperty("beta")
    public void setBeta(Boolean beta) {
        this.beta = beta;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("active")
    public Boolean getActive() {
        return active;
    }

    @JsonProperty("active")
    public void setActive(Boolean active) {
        this.active = active;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("display_name")
    public String getDisplayName() {
        return displayName;
    }

    @JsonProperty("display_name")
    public void setDisplayName(String displayName) {
        this.displayName = displayName.replace(" ", " ");
    }

    @JsonProperty("input_consts")
    public List<AdFunctionTemplateInputConstantDto> getInputConsts() {
        return adFunctionTemplateInputConstantDtos;
    }

    @JsonProperty("input_consts")
    public void setInputConsts(List<AdFunctionTemplateInputConstantDto> adFunctionTemplateInputConstantDtos) {
        this.adFunctionTemplateInputConstantDtos = adFunctionTemplateInputConstantDtos;
    }

    @JsonProperty("input_points")
    public List<AdFunctionTemplateInputPointDto> getInputPoints() {
        return adFunctionTemplateInputPointDtos;
    }

    @JsonProperty("input_points")
    public void setInputPoints(List<AdFunctionTemplateInputPointDto> adFunctionTemplateInputPointDtos) {
        this.adFunctionTemplateInputPointDtos = adFunctionTemplateInputPointDtos;
    }

    @JsonProperty("output_points")
    public List<AdFunctionTemplateOutputPointDto> getOutputPoints() {
        return adFunctionTemplateOutputPointDtos;
    }

    @JsonProperty("output_points")
    public void setOutputPoints(List<AdFunctionTemplateOutputPointDto> adFunctionTemplateOutputPointDtos) {
        this.adFunctionTemplateOutputPointDtos = adFunctionTemplateOutputPointDtos;
    }

    @JsonProperty("reference_number")
    public String getReferenceNumber() {
        return referenceNumber;
    }

    @JsonProperty("reference_number")
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    @JsonProperty("tuple_constraint")
    public String getTupleConstraint() {
        return tupleConstraint;
    }

    @JsonProperty("tuple_constraint")
    public void setTupleConstraint(String tupleConstraint) {
        this.tupleConstraint = tupleConstraint;
    }

    @JsonProperty("equipment_type_id")
    public List<Integer> getEquipmentTypeId() {
        return equipmentTypeId;
    }

    @JsonProperty("equipment_type_id")
    public void setEquipmentTypeId(List<Integer> equipmentTypeId) {
        this.equipmentTypeId = equipmentTypeId;
    }

    @JsonProperty("node_filter_expression")
    public String getNodeFilterExpression() {
        return nodeFilterExpression;
    }

    @JsonProperty("node_filter_expression")
    public void setNodeFilterExpression(String nodeFilterExpression) {
        this.nodeFilterExpression = nodeFilterExpression;
    }

    @JsonProperty("tuple_constraint_error_message")
    public String getTupleConstraintErrorMessage() {
        return tupleConstraintErrorMessage;
    }

    @JsonProperty("tuple_constraint_error_message")
    public void setTupleConstraintErrorMessage(String tupleConstraintErrorMessage) {
        this.tupleConstraintErrorMessage = tupleConstraintErrorMessage;
    }
    
    @JsonProperty("version")
    public Integer getVersion() {
        return version;
    }

    @JsonProperty("version")
    public void setVersion(Integer version) {
        this.version = version;
    }    
    
    @Override
    public int compareTo(AdFunctionTemplateDto that) {

      // Given a fault number of A.B.C.D, then:
      // A is the ordinal of the rule system category
      // A is the ordinal of the rule equipment category
      // B is the subOrdinal of the rule equipment category
      // C is the ordinal of the rule template instance
      // D is the subOrdinal of the rule template instance
      String thatFaultNumber = that.referenceNumber.replaceAll("\\.", ":");
      String[] thatFaultNumberElements = thatFaultNumber.split(":");
      if (thatFaultNumberElements.length != 4) {
        return -1;
      }
      String thatOrdinalA = thatFaultNumberElements[2];
      String thatOrdinalB = thatFaultNumberElements[3];


      String thisFaultNumber = this.referenceNumber.replaceAll("\\.", ":");
      String[] thisFaultNumberElements = thisFaultNumber.split(":");
      if (thisFaultNumberElements.length != 4) {
        return -1;
      }
      String thisOrdinalA = thisFaultNumberElements[2];
      String thisOrdinalB = thisFaultNumberElements[3];

      int compareTo = Integer.valueOf(thisOrdinalA).compareTo(Integer.valueOf(thatOrdinalA));
      if (compareTo == 0) {
        compareTo = Integer.valueOf(thisOrdinalB).compareTo(Integer.valueOf(thatOrdinalB));
      }
      return compareTo;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((active == null) ? 0 : active.hashCode());
      result = prime * result + ((adFunctionTemplateInputConstantDtos == null) ? 0
          : adFunctionTemplateInputConstantDtos.hashCode());
      result = prime * result + ((adFunctionTemplateInputPointDtos == null) ? 0
          : adFunctionTemplateInputPointDtos.hashCode());
      result = prime * result + ((adFunctionTemplateOutputPointDtos == null) ? 0
          : adFunctionTemplateOutputPointDtos.hashCode());
      result = prime * result + ((beta == null) ? 0 : beta.hashCode());
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + ((displayName == null) ? 0 : displayName.hashCode());
      result = prime * result + ((equipmentTypeId == null) ? 0 : equipmentTypeId.hashCode());
      result = prime * result + ((functionCodeModuleDescription == null) ? 0
          : functionCodeModuleDescription.hashCode());
      result =
          prime * result + ((functionCodeModuleId == null) ? 0 : functionCodeModuleId.hashCode());
      result = prime * result
          + ((functionCodeModuleName == null) ? 0 : functionCodeModuleName.hashCode());
      result = prime * result + ((functionTypeId == null) ? 0 : functionTypeId.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result =
          prime * result + ((nodeFilterExpression == null) ? 0 : nodeFilterExpression.hashCode());
      result = prime * result + ((referenceNumber == null) ? 0 : referenceNumber.hashCode());
      result = prime * result + ((tupleConstraint == null) ? 0 : tupleConstraint.hashCode());
      result = prime * result
          + ((tupleConstraintErrorMessage == null) ? 0 : tupleConstraintErrorMessage.hashCode());
      result = prime * result + ((version == null) ? 0 : version.hashCode());
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
      AdFunctionTemplateDto other = (AdFunctionTemplateDto) obj;
      if (active == null) {
        if (other.active != null)
          return false;
      } else if (!active.equals(other.active))
        return false;
      if (adFunctionTemplateInputConstantDtos == null) {
        if (other.adFunctionTemplateInputConstantDtos != null)
          return false;
      } else if (!adFunctionTemplateInputConstantDtos
          .equals(other.adFunctionTemplateInputConstantDtos))
        return false;
      if (adFunctionTemplateInputPointDtos == null) {
        if (other.adFunctionTemplateInputPointDtos != null)
          return false;
      } else if (!adFunctionTemplateInputPointDtos.equals(other.adFunctionTemplateInputPointDtos))
        return false;
      if (adFunctionTemplateOutputPointDtos == null) {
        if (other.adFunctionTemplateOutputPointDtos != null)
          return false;
      } else if (!adFunctionTemplateOutputPointDtos.equals(other.adFunctionTemplateOutputPointDtos))
        return false;
      if (beta == null) {
        if (other.beta != null)
          return false;
      } else if (!beta.equals(other.beta))
        return false;
      if (description == null) {
        if (other.description != null)
          return false;
      } else if (!description.equals(other.description))
        return false;
      if (displayName == null) {
        if (other.displayName != null)
          return false;
      } else if (!displayName.equals(other.displayName))
        return false;
      if (equipmentTypeId == null) {
        if (other.equipmentTypeId != null)
          return false;
      } else if (!equipmentTypeId.equals(other.equipmentTypeId))
        return false;
      if (functionCodeModuleDescription == null) {
        if (other.functionCodeModuleDescription != null)
          return false;
      } else if (!functionCodeModuleDescription.equals(other.functionCodeModuleDescription))
        return false;
      if (functionCodeModuleId == null) {
        if (other.functionCodeModuleId != null)
          return false;
      } else if (!functionCodeModuleId.equals(other.functionCodeModuleId))
        return false;
      if (functionCodeModuleName == null) {
        if (other.functionCodeModuleName != null)
          return false;
      } else if (!functionCodeModuleName.equals(other.functionCodeModuleName))
        return false;
      if (functionTypeId == null) {
        if (other.functionTypeId != null)
          return false;
      } else if (!functionTypeId.equals(other.functionTypeId))
        return false;
      if (id == null) {
        if (other.id != null)
          return false;
      } else if (!id.equals(other.id))
        return false;
      if (name == null) {
        if (other.name != null)
          return false;
      } else if (!name.equals(other.name))
        return false;
      if (nodeFilterExpression == null) {
        if (other.nodeFilterExpression != null)
          return false;
      } else if (!nodeFilterExpression.equals(other.nodeFilterExpression))
        return false;
      if (referenceNumber == null) {
        if (other.referenceNumber != null)
          return false;
      } else if (!referenceNumber.equals(other.referenceNumber))
        return false;
      if (tupleConstraint == null) {
        if (other.tupleConstraint != null)
          return false;
      } else if (!tupleConstraint.equals(other.tupleConstraint))
        return false;
      if (tupleConstraintErrorMessage == null) {
        if (other.tupleConstraintErrorMessage != null)
          return false;
      } else if (!tupleConstraintErrorMessage.equals(other.tupleConstraintErrorMessage))
        return false;
      if (version == null) {
        if (other.version != null)
          return false;
      } else if (!version.equals(other.version))
        return false;
      return true;
    }

    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("AdFunctionTemplateDto [id=").append(id).append(", functionCodeModuleId=")
          .append(functionCodeModuleId).append(", functionCodeModuleName=")
          .append(functionCodeModuleName).append(", functionCodeModuleDescription=")
          .append(functionCodeModuleDescription).append(", functionTypeId=").append(functionTypeId)
          .append(", beta=").append(beta).append(", name=").append(name).append(", active=")
          .append(active).append(", description=").append(description).append(", displayName=")
          .append(displayName).append(", adFunctionTemplateInputConstantDtos=")
          .append(adFunctionTemplateInputConstantDtos).append(", adFunctionTemplateInputPointDtos=")
          .append(adFunctionTemplateInputPointDtos).append(", adFunctionTemplateOutputPointDtos=")
          .append(adFunctionTemplateOutputPointDtos).append(", referenceNumber=")
          .append(referenceNumber).append(", tupleConstraint=").append(tupleConstraint)
          .append(", equipmentTypeId=").append(equipmentTypeId).append(", nodeFilterExpression=")
          .append(nodeFilterExpression).append(", tupleConstraintErrorMessage=")
          .append(tupleConstraintErrorMessage).append(", version=").append(version).append("]");
      return builder.toString();
    }    
}
