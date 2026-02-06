
package com.djt.hvac.domain.model.dictionary.dto.function;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "name",
    "seq_no",
    "unit_id",
    "description",
    "is_required",
    "data_type_id",
    "default_value"
})
public class AdFunctionTemplateInputConstantDto {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("seq_no")
    private Integer seqNo;
    @JsonProperty("unit_id")
    private Integer unitId;
    @JsonProperty("description")
    private String description;
    @JsonProperty("is_required")
    private Boolean isRequired;
    @JsonProperty("data_type_id")
    private Integer dataTypeId;
    @JsonProperty("default_value")
    private String defaultValue;

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

    @JsonProperty("seq_no")
    public Integer getSeqNo() {
        return seqNo;
    }

    @JsonProperty("seq_no")
    public void setSeqNo(Integer seqNo) {
        this.seqNo = seqNo;
    }

    @JsonProperty("unit_id")
    public Integer getUnitId() {
        return unitId;
    }

    @JsonProperty("unit_id")
    public void setUnitId(Integer unitId) {
        this.unitId = unitId;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("is_required")
    public Boolean getIsRequired() {
        return isRequired;
    }

    @JsonProperty("is_required")
    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }

    @JsonProperty("data_type_id")
    public Integer getDataTypeId() {
        return dataTypeId;
    }

    @JsonProperty("data_type_id")
    public void setDataTypeId(Integer dataTypeId) {
        this.dataTypeId = dataTypeId;
    }

    @JsonProperty("default_value")
    public String getDefaultValue() {
        return defaultValue;
    }

    @JsonProperty("default_value")
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

}
