package com.djt.hvac.domain.model.dictionary.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/*
 *
 * select * from tags order by id;
 * 
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "tag_group_id",
    "tag_type_id",
    "name",
    "ui_inferred",
    "scoped_to_constraint",
    "tag_group",
    "tag_type"
})
public class TagDto {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("tag_group_id")
    private Integer tagGroupId;
    @JsonProperty("tag_type_id")
    private Integer tagTypeId;
    @JsonProperty("name")
    private String name;
    @JsonProperty("ui_inferred")
    private Boolean uiInferred;
    @JsonProperty("scoped_to_constraint")
    private Integer scopedToConstraint;
    @JsonProperty("tag_group")
    private String tagGroup;
    @JsonProperty("tag_type")
    private String tagType;

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("tag_group_id")
    public Integer getTagGroupId() {
        return tagGroupId;
    }

    @JsonProperty("tag_group_id")
    public void setTagGroupId(Integer tagGroupId) {
        this.tagGroupId = tagGroupId;
    }

    @JsonProperty("tag_type_id")
    public Integer getTagTypeId() {
        return tagTypeId;
    }

    @JsonProperty("tag_type_id")
    public void setTagTypeId(Integer tagTypeId) {
        this.tagTypeId = tagTypeId;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("ui_inferred")
    public Boolean getUiInferred() {
        return uiInferred;
    }

    @JsonProperty("ui_inferred")
    public void setUiInferred(Boolean uiInferred) {
        this.uiInferred = uiInferred;
    }

    @JsonProperty("scoped_to_constraint")
    public Integer getScopedToConstraint() {
        return scopedToConstraint;
    }

    @JsonProperty("scoped_to_constraint")
    public void setScopedToConstraint(Integer scopedToConstraint) {
        this.scopedToConstraint = scopedToConstraint;
    }

    @JsonProperty("tag_group")
    public String getTagGroup() {
        return tagGroup;
    }

    @JsonProperty("tag_group")
    public void setTagGroup(String tagGroup) {
        this.tagGroup = tagGroup;
    }

    @JsonProperty("tag_type")
    public String getTagType() {
        return tagType;
    }

    @JsonProperty("tag_type")
    public void setTagType(String tagType) {
        this.tagType = tagType;
    }

}
