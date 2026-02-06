package com.djt.hvac.domain.model.dictionary;

import static java.util.Objects.requireNonNull;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.djt.hvac.domain.model.common.AbstractPersistentEntity;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;

public class ScheduledEventTypeEntity extends AbstractPersistentEntity {
  private static final long serialVersionUID = 1L;
  
  private final String name;
  private final String metricId;
  private final String range;
  private final Set<String> haystackTags = new TreeSet<>();
  
  public ScheduledEventTypeEntity(
      Integer persistentIdentity,
      String name,
      String metricId,
      String range,
      Set<String> haystackTags) {
    super(persistentIdentity);
    requireNonNull(name, "name cannot be null");
    requireNonNull(metricId, "metricId cannot be null");
    requireNonNull(range, "range cannot be null");
    requireNonNull(haystackTags, "haystackTags cannot be null");
    this.name = name;
    this.metricId = metricId;
    this.range = range;
    this.haystackTags.addAll(haystackTags);
  }
  
  public String getName() {
    return name;
  }
  
  public String getMetricId() {
    return metricId;
  }
  
  public String getRange() {
    return range;
  }

  public Set<String> getHaystackTags() {
    return haystackTags;
  }
  
  public String getHaystackTagsAsString() {
    
    String tags = null;
    if (!getHaystackTags().isEmpty()) {
      tags = getHaystackTags()
          .toString()
          .replace(" ", "")
          .replace("[", "")
          .replace("\"", "")
          .replace("]", "");
    } else {
      tags = "";
    }
    return tags;
  }  
  
  public String getNaturalIdentity() {
    return name;
  }  
  
  @Override
  public void validate(
      Set<IssueType> issueTypes,
      List<ValidationMessage> validationMessages, 
      boolean remediate) {
  }
}
