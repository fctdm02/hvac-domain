package com.djt.hvac.domain.model.common.dto;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = EntityIndex.Builder.class)
@JsonPropertyOrder({
  "id",
  "name"  
})
public class EntityIndex {
  
  private final Integer id;
  private final String name;

  @JsonCreator
  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(EntityIndex entityIndex) {
    return new Builder(entityIndex);
  }

  private EntityIndex(Builder builder) {
    this.id = builder.id;
    this.name = builder.name;
  }

  public Integer getId() {
    return id;
  }

  public String getName() {
    return name;
  }
  
  @JsonPOJOBuilder
  public static class Builder {
    private Integer id;
    private String name;

    private Builder() {}

    private Builder(EntityIndex entityIndex) {
      requireNonNull(entityIndex, "entityIndex cannot be null");
      this.id = entityIndex.id;
      this.name = entityIndex.name;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withId(Integer id) {
      requireNonNull(id, "id cannot be null");
      this.id = id;
      return this;
    }

    public Builder withName(String name) {
      requireNonNull(name, "name cannot be null");
      this.name = name;
      return this;
    }

    public EntityIndex build() {
      requireNonNull(id, "id cannot be null");
      requireNonNull(name, "name cannot be null");
      return new EntityIndex(this);
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
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
    EntityIndex other = (EntityIndex) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder2 = new StringBuilder();
    builder2.append("EntityIndex [id=").append(id).append(", name=").append(name).append("]");
    return builder2.toString();
  }
}
