package com.djt.hvac.domain.model.nodehierarchy.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = TagInfo.Builder.class)
public class TagInfo {
  @SuppressWarnings("unused")
  private static Logger logger = LoggerFactory.getLogger(TagInfo.class);

  private final int id;
  private final String name;
  private final TagType type;
  private final int groupId;
  private final boolean isSystem;

  private TagInfo(Builder builder) {
    this.id = builder.id;
    this.name = builder.name;
    this.type = builder.type;
    this.groupId = builder.groupId;
    this.isSystem = builder.isSystem;
  }

  @JsonCreator
  public static Builder builder() {
    return new Builder();
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public TagType getType() {
    return type;
  }

  public int getGroupId() {
    return groupId;
  }
  
  public boolean getIsSystem() {
    return isSystem;
  }

  @Override
  public String toString() {
    return "TagInfo [id=" + id + ", name=" + name + ", tagType=" + type + ", tagGroupId="
        + groupId + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + id;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + groupId;
    result = prime * result + ((type == null) ? 0 : type.hashCode());
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
    TagInfo other = (TagInfo) obj;
    if (id != other.id)
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (groupId != other.groupId)
      return false;
    if (type != other.type)
      return false;
    return true;
  }

  @JsonPOJOBuilder
  public static class Builder {
    private int id;
    private String name;
    private TagType type;
    private int groupId;
    private boolean isSystem;

    public Builder withId(int id) {
      this.id = id;
      return this;
    }

    public Builder withName(String name) {
      this.name = name;
      return this;
    }

    public Builder withType(TagType tagType) {
      this.type = tagType;
      return this;
    }

    public Builder withGroupId(int tagGroupId) {
      this.groupId = tagGroupId;
      return this;
    }

    public Builder withIsSystem(boolean isSystem) {
      this.isSystem = isSystem;
      return this;
    }

    public TagInfo build() {
      return new TagInfo(this);
    }

  }
}
