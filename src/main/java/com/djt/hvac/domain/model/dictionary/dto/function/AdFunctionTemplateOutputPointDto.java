
package com.djt.hvac.domain.model.dictionary.dto.function;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "range",
    "seq_no",
    "tags",
    "unit_id",
    "description",
    "data_type_id"
})
public class AdFunctionTemplateOutputPointDto {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("range")
    private String range;
    @JsonProperty("seq_no")
    private Integer seqNo;
    @JsonProperty("tags")
    private List<String> tags = null;
    @JsonProperty("unit_id")
    private Integer unitId;
    @JsonProperty("description")
    private String description;
    @JsonProperty("data_type_id")
    private Integer dataTypeId;

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }
    
    @JsonProperty("range")
    public String getRange() {
        return range;
    }

    @JsonProperty("range")
    public void setRange(String range) {
        this.range = range;
    }

    @JsonProperty("seq_no")
    public Integer getSeqNo() {
        return seqNo;
    }

    @JsonProperty("seq_no")
    public void setSeqNo(Integer seqNo) {
        this.seqNo = seqNo;
    }

    @JsonProperty("tags")
    public List<String> getTags() {
        return tags;
    }

    @JsonProperty("tags")
    public void setTags(List<String> tags) {
        this.tags = tags;
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

    @JsonProperty("data_type_id")
    public Integer getDataTypeId() {
        return dataTypeId;
    }

    @JsonProperty("data_type_id")
    public void setDataTypeId(Integer dataTypeId) {
        this.dataTypeId = dataTypeId;
    }

}
