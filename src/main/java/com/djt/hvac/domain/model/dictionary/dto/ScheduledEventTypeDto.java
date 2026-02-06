
package com.djt.hvac.domain.model.dictionary.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/*
  SELECT 
    st.id,
    st."name",
    st.metric_id,
    st."range",
    ARRAY_AGG(t.name) AS haystack_tags 
  FROM  
    scheduled_event_types st
    join point_template_tags ptt ON st.point_template_id = ptt.node_template_id 
    join tags t ON ptt.tag_id = t.id
  GROUP BY 
    st.id,
    st."name",
    st.metric_id,
    st."range"; 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "name",
    "metric_id",
    "range",
    "haystack_tags"
})
public class ScheduledEventTypeDto {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("metric_id")
    private String metricId;
    @JsonProperty("range")
    private String range;
    @JsonProperty("haystack_tags")
    private String haystackTags;

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

    @JsonProperty("metric_id")
    public String getMetricId() {
        return metricId;
    }

    @JsonProperty("metric_id")
    public void setMetricId(String metricId) {
        this.metricId = metricId;
    }

    @JsonProperty("range")
    public String getRange() {
        return range;
    }

    @JsonProperty("range")
    public void setRange(String range) {
        this.range = range;
    }

    @JsonProperty("haystack_tags")
    public String getHaystackTags() {
        return haystackTags;
    }

    @JsonProperty("haystack_tags")
    public void setHaystackTags(String haystackTags) {
        this.haystackTags = haystackTags;
    }

    @Override
    public String toString() {
      
      return new StringBuilder()
          .append("ScheduledEventTypeDto [id=")
          .append(id)
          .append(", name=")
          .append(name)
          .append(", metricId=")
          .append(metricId)
          .append(", range=")
          .append(range)
          .append(", haystackTags=")
          .append(haystackTags)
          .append("]")
          .toString();
    }
}
