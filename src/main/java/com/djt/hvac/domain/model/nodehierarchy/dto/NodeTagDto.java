package com.djt.hvac.domain.model.nodehierarchy.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/*
    -- node tags
    select 
      n.id,
      ARRAY_AGG(t.id) AS node_tags
    from 
      nodes n 
      join node_tags nt on n.id = nt.node_id 
      join tags t on nt.tag_id = t.id
    where    
      n.customer_id = 4   
    group by 
      n.id;  
*/
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "node_tags"
})
public class NodeTagDto {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("node_tags")
    private String nodeTags;
    @JsonProperty("tag_ids")
    private List<Integer> tagIds;

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("node_tags")
    public String getNodeTags() {
        return nodeTags;
    }

    @JsonProperty("node_tags")
    public void setNodeTags(String nodeTags) {
        this.nodeTags = nodeTags;
    }

    @JsonProperty("tag_ids")
    public List<Integer> getTagIds() {
        return tagIds;
    }

    @JsonProperty("tag_ids")
    public void setTagIds(List<Integer> tagIds) {
        this.tagIds = tagIds;
    }
    
    @Override
    public String toString() {
      return new StringBuilder()
          .append("NodeTagDto [id=")
          .append(id)
          .append(", nodeTags=")
          .append(nodeTags)
          .append(", tagIds=")
          .append(tagIds)
          .append("]")
          .toString();
    }

}
