
package com.djt.hvac.domain.model.dictionary.dto.function;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "name",
    "tags",
    "array",
    "seq_no",
    "required",
    "description",
    "current_object_expression"
})
public class AdFunctionTemplateInputPointDto {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("tags")
    private List<String> tags = null;
    @JsonProperty("array")
    private Boolean array;
    @JsonProperty("seq_no")
    private Integer seqNo;
    @JsonProperty("required")
    private Boolean required;
    @JsonProperty("description")
    private String description;
    @JsonProperty("current_object_expression")
    private String currentObjectExpression;

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

    @JsonProperty("tags")
    public List<String> getTags() {
        return tags;
    }

    @JsonProperty("tags")
    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @JsonProperty("array")
    public Boolean getArray() {
        return array;
    }

    @JsonProperty("array")
    public void setArray(Boolean array) {
        this.array = array;
    }

    @JsonProperty("seq_no")
    public Integer getSeqNo() {
        return seqNo;
    }

    @JsonProperty("seq_no")
    public void setSeqNo(Integer seqNo) {
        this.seqNo = seqNo;
    }

    @JsonProperty("required")
    public Boolean getRequired() {
        return required;
    }

    @JsonProperty("required")
    public void setRequired(Boolean required) {
        this.required = required;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("current_object_expression")
    public String getCurrentObjectExpression() {
        return currentObjectExpression;
    }

    @JsonProperty("current_object_expression")
    public void setCurrentObjectExpression(String currentObjectExpression) {
        this.currentObjectExpression = currentObjectExpression;
    }

}
