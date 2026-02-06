
package com.djt.hvac.domain.model.referralagent.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/*
select 
  id,
  referral_agent_type_id,
  parent_id,
  "name",
  code
from 
  referral_agents 
order by 
  parent_id nulls first;
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "referral_agent_type_id",
    "parent_id",
    "name",
    "code"
})
public class ReferralAgentDto {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("referral_agent_type_id")
    private Integer referralAgentTypeId;
    @JsonProperty("parent_id")
    private Object parentId;
    @JsonProperty("name")
    private String name;
    @JsonProperty("code")
    private String code;

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("referral_agent_type_id")
    public Integer getReferralAgentTypeId() {
        return referralAgentTypeId;
    }

    @JsonProperty("referral_agent_type_id")
    public void setReferralAgentTypeId(Integer referralAgentTypeId) {
        this.referralAgentTypeId = referralAgentTypeId;
    }

    @JsonProperty("parent_id")
    public Object getParentId() {
        return parentId;
    }

    @JsonProperty("parent_id")
    public void setParentId(Object parentId) {
        this.parentId = parentId;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("code")
    public String getCode() {
        return code;
    }

    @JsonProperty("code")
    public void setCode(String code) {
        this.code = code;
    }

}
