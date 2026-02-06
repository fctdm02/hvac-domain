
package com.djt.hvac.domain.model.function.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * 
<pre>

-- AD Function Instance Candidates
SELECT 
  can.id AS id,
  can.equipment_id,
  can.ad_function_template_id AS template_id,
  can.candidate_json
FROM 
  ad_function_instance_candidates can
WHERE 
  can.customer_id = 4;


-- AD Function Instances
SELECT 
  fi.id AS id,
  fi.node_id AS equipment_id,
  fi.ad_function_template_id AS template_id,
  array_agg(fip.ad_function_template_input_point_id) AS template_input_point_id,
  array_agg(fip.point_id) AS input_point_id,
  array_agg(fip.subscript) AS input_point_subscript,
  fi.template_output_point_id,
  fi.output_point_id,
  fi2.template_input_const_id,
  fi2.input_const_value 
FROM  
  (
  SELECT 
    fi.id,
    fi.node_id,
    fi.ad_function_template_id,
    array_agg(fop.ad_function_template_output_point_id) AS template_output_point_id,
    array_agg(fop.point_id) AS output_point_id
  FROM   
    ad_function_instances fi 
    join ad_function_instance_output_points fop on fi.id = fop.ad_function_instance_id
  WHERE 
    fi.customer_id = 4 
    AND fi.active = true
  GROUP BY 
    fi.id,
    fi.node_id,
    fi.ad_function_template_id
  ) fi
LEFT OUTER JOIN 
  ad_function_instance_input_points fip on fi.id = fip.ad_function_instance_id
  JOIN 
  (
  SELECT 
    fi.id,
    fi.node_id,
    fi.ad_function_template_id,
    array_agg(fic.ad_function_template_input_const_id) AS template_input_const_id,
    array_agg(fic.value) AS input_const_value
  FROM   
    ad_function_instances fi 
    join ad_function_instance_input_consts fic on fi.id = fic.ad_function_instance_id
  WHERE 
    fi.customer_id = 4 
    AND fi.active = true
  GROUP BY 
    fi.id,
    fi.node_id,
    fi.ad_function_template_id
  ) fi2 on fi.id = fi2.id  
GROUP BY 
  fi.id,
  fi.node_id,
  fi.ad_function_template_id,
  fi.template_output_point_id,
  fi.output_point_id,
  fi2.template_input_const_id,
  fi2.input_const_value
ORDER BY  
  equipment_id,
  template_id;
  
 </pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "equipment_id",
    "template_id",
    "template_input_point_id",
    "input_point_id",
    "input_point_subscript",
    "template_output_point_id",
    "output_point_id",
    "template_input_const_id",
    "input_const_value",
    "candidate_json",
    "nodePath",
    "adFunctionTemplateDescription",
    "functionType",
    "ignored",
    "templateVersion",
    "instanceVersion"
})
public class AdFunctionInstanceDto {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("equipment_id")
    private Integer equipmentId;
    @JsonProperty("template_id")
    private Integer templateId;
    @JsonProperty("template_input_point_id")
    private String templateInputPointId;
    @JsonProperty("input_point_id")
    private String inputPointId;
    @JsonProperty("input_point_subscript")
    private String inputPointSubscript;
    @JsonProperty("template_output_point_id")
    private String templateOutputPointId;
    @JsonProperty("output_point_id")
    private String outputPointId;
    @JsonProperty("template_input_const_id")
    private String templateInputConstId;
    @JsonProperty("input_const_value")
    private String inputConstValue;
    @JsonProperty("candidate_json")
    private String candidateJson;
    @JsonProperty("nodePath")
    private String nodePath;
    @JsonProperty("adFunctionTemplateDescription")
    private String adFunctionTemplateDescription;
    @JsonProperty("adFunctionType")
    private String adFunctionType;
    @JsonProperty("ignored")
    private Boolean ignored;
    @JsonProperty("templateVersion")
    private Integer templateVersion = Integer.valueOf(1);
    @JsonProperty("instanceVersion")
    private Integer instanceVersion = Integer.valueOf(1);
    
    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("equipment_id")
    public Integer getEquipmentId() {
        return equipmentId;
    }

    @JsonProperty("equipment_id")
    public void setEquipmentId(Integer equipmentId) {
        this.equipmentId = equipmentId;
    }

    @JsonProperty("template_id")
    public Integer getTemplateId() {
        return templateId;
    }

    @JsonProperty("template_id")
    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }

    @JsonProperty("template_input_point_id")
    public String getTemplateInputPointId() {
        return templateInputPointId;
    }

    @JsonProperty("template_input_point_id")
    public void setTemplateInputPointId(String templateInputPointId) {
        this.templateInputPointId = templateInputPointId;
    }

    @JsonProperty("input_point_id")
    public String getInputPointId() {
        return inputPointId;
    }

    @JsonProperty("input_point_id")
    public void setInputPointId(String inputPointId) {
        this.inputPointId = inputPointId;
    }
    
    @JsonProperty("input_point_subscript")
    public String getInputPointSubscript() {
        return inputPointSubscript;
    }

    @JsonProperty("input_point_subscript")
    public void setInputPointSubscript(String inputPointSubscript) {
        this.inputPointSubscript = inputPointSubscript;
    }
    
    @JsonProperty("template_output_point_id")
    public String getTemplateOutputPointId() {
        return templateOutputPointId;
    }

    @JsonProperty("template_output_point_id")
    public void setTemplateOutputPointId(String templateOutputPointId) {
        this.templateOutputPointId = templateOutputPointId;
    }

    @JsonProperty("output_point_id")
    public String getOutputPointId() {
        return outputPointId;
    }

    @JsonProperty("output_point_id")
    public void setOutputPointId(String outputPointId) {
        this.outputPointId = outputPointId;
    }

    @JsonProperty("template_input_const_id")
    public String getTemplateInputConstId() {
        return templateInputConstId;
    }

    @JsonProperty("template_input_const_id")
    public void setTemplateInputConstId(String templateInputConstId) {
        this.templateInputConstId = templateInputConstId;
    }

    @JsonProperty("input_const_value")
    public String getInputConstValue() {
        return inputConstValue;
    }

    @JsonProperty("input_const_value")
    public void setInputConstValue(String inputConstValue) {
        this.inputConstValue = inputConstValue;
    }

    @JsonProperty("candidate_json")
    public String getCandidateJson() {
        return candidateJson;
    }

    @JsonProperty("candidate_json")
    public void setCandidateJson(String candidateJson) {
        this.candidateJson = candidateJson;
    }
    
    @JsonProperty("nodePath")
    public String getNodePath() {
        return nodePath;
    }

    @JsonProperty("nodePath")
    public void setNodePath(String nodePath) {
        this.nodePath = nodePath;
    }
    
    @JsonProperty("adFunctionTemplateDescription")
    public String getAdFunctionTemplateDescription() {
        return adFunctionTemplateDescription;
    }

    @JsonProperty("adFunctionTemplateDescription")
    public void setAdFunctionTemplateDescription(String adFunctionTemplateDescription) {
        this.adFunctionTemplateDescription = adFunctionTemplateDescription;
    }
    
    @JsonProperty("adFunctionType")
    public String getAdFunctionType() {
        return adFunctionType;
    }

    @JsonProperty("adFunctionType")
    public void setAdFunctionType(String adFunctionType) {
        this.adFunctionType = adFunctionType;
    }

    @JsonProperty("ignored")
    public Boolean getIgnored() {
        return ignored;
    }

    @JsonProperty("ignored")
    public void setIgnored(Boolean ignored) {
        this.ignored = ignored;
    }
    
    @JsonProperty("templateVersion")
    public Integer getTemplateVersion() {
        return templateVersion;
    }

    @JsonProperty("templateVersion")
    public void setTemplateVersion(Integer templateVersion) {
        if (templateVersion == null) {
          throw new IllegalArgumentException("templateVersion cannot be null");
        }
        this.templateVersion = templateVersion;
    }

    @JsonProperty("instanceVersion")
    public Integer getInstanceVersion() {
        return instanceVersion;
    }

    @JsonProperty("instanceVersion")
    public void setInstanceVersion(Integer instanceVersion) {
        if (instanceVersion == null) {
          throw new IllegalArgumentException("instanceVersion cannot be null");
        }
        this.instanceVersion = instanceVersion;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((adFunctionTemplateDescription == null) ? 0: adFunctionTemplateDescription.hashCode());
      result = prime * result + ((adFunctionType == null) ? 0 : adFunctionType.hashCode());
      result = prime * result + ((candidateJson == null) ? 0 : candidateJson.hashCode());
      result = prime * result + ((equipmentId == null) ? 0 : equipmentId.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((ignored == null) ? 0 : ignored.hashCode());
      result = prime * result + ((inputConstValue == null) ? 0 : inputConstValue.hashCode());
      result = prime * result + ((inputPointId == null) ? 0 : inputPointId.hashCode());
      result = prime * result + ((inputPointSubscript == null) ? 0 : inputPointSubscript.hashCode());
      result = prime * result + ((nodePath == null) ? 0 : nodePath.hashCode());
      result = prime * result + ((outputPointId == null) ? 0 : outputPointId.hashCode());
      result = prime * result + ((templateId == null) ? 0 : templateId.hashCode());
      result = prime * result + ((templateInputConstId == null) ? 0 : templateInputConstId.hashCode());
      result = prime * result + ((templateInputPointId == null) ? 0 : templateInputPointId.hashCode());
      result = prime * result + ((templateOutputPointId == null) ? 0 : templateOutputPointId.hashCode());
      result = prime * result + ((templateVersion == null) ? 0 : templateVersion.hashCode());
      result = prime * result + ((instanceVersion == null) ? 0 : instanceVersion.hashCode());
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
      AdFunctionInstanceDto other = (AdFunctionInstanceDto) obj;
      if (adFunctionTemplateDescription == null) {
        if (other.adFunctionTemplateDescription != null)
          return false;
      } else if (!adFunctionTemplateDescription.equals(other.adFunctionTemplateDescription))
        return false;
      if (adFunctionType == null) {
        if (other.adFunctionType != null)
          return false;
      } else if (!adFunctionType.equals(other.adFunctionType))
        return false;
      if (candidateJson == null) {
        if (other.candidateJson != null)
          return false;
      } else if (!candidateJson.equals(other.candidateJson))
        return false;
      if (equipmentId == null) {
        if (other.equipmentId != null)
          return false;
      } else if (!equipmentId.equals(other.equipmentId))
        return false;
      if (id == null) {
        if (other.id != null)
          return false;
      } else if (!id.equals(other.id))
        return false;
      if (ignored == null) {
        if (other.ignored != null)
          return false;
      } else if (!ignored.equals(other.ignored))
        return false;
      if (inputConstValue == null) {
        if (other.inputConstValue != null)
          return false;
      } else if (!inputConstValue.equals(other.inputConstValue))
        return false;
      if (inputPointId == null) {
        if (other.inputPointId != null)
          return false;
      } else if (!inputPointId.equals(other.inputPointId))
        return false;
      if (inputPointSubscript == null) {
        if (other.inputPointSubscript != null)
          return false;
      } else if (!inputPointSubscript.equals(other.inputPointSubscript))
        return false;
      if (nodePath == null) {
        if (other.nodePath != null)
          return false;
      } else if (!nodePath.equals(other.nodePath))
        return false;
      if (outputPointId == null) {
        if (other.outputPointId != null)
          return false;
      } else if (!outputPointId.equals(other.outputPointId))
        return false;
      if (templateId == null) {
        if (other.templateId != null)
          return false;
      } else if (!templateId.equals(other.templateId))
        return false;
      if (templateInputConstId == null) {
        if (other.templateInputConstId != null)
          return false;
      } else if (!templateInputConstId.equals(other.templateInputConstId))
        return false;
      if (templateInputPointId == null) {
        if (other.templateInputPointId != null)
          return false;
      } else if (!templateInputPointId.equals(other.templateInputPointId))
        return false;
      if (templateOutputPointId == null) {
        if (other.templateOutputPointId != null)
          return false;
      } else if (!templateOutputPointId.equals(other.templateOutputPointId))
        return false;
      if (templateVersion == null) {
        if (other.templateVersion != null)
          return false;
      } else if (!templateVersion.equals(other.templateVersion))
        return false;
      if (instanceVersion == null) {
        if (other.instanceVersion != null)
          return false;
      } else if (!instanceVersion.equals(other.instanceVersion))
        return false;
      return true;
    }

    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("AdFunctionInstanceDto [id=").append(id).append(", equipmentId=")
          .append(equipmentId).append(", templateId=").append(templateId)
          .append(", templateInputPointId=").append(templateInputPointId).append(", inputPointId=")
          .append(inputPointId).append(", inputPointSubscript=").append(inputPointSubscript)
          .append(", templateOutputPointId=").append(templateOutputPointId)
          .append(", outputPointId=").append(outputPointId).append(", templateInputConstId=")
          .append(templateInputConstId).append(", inputConstValue=").append(inputConstValue)
          .append(", candidateJson=").append(candidateJson).append(", nodePath=").append(nodePath)
          .append(", adFunctionTemplateDescription=").append(adFunctionTemplateDescription)
          .append(", adFunctionType=").append(adFunctionType).append(", ignored=").append(ignored)
          .append(", templateVersion=").append(templateVersion)
          .append(", instanceVersion=").append(instanceVersion).append("]");
      return builder.toString();
    }
}